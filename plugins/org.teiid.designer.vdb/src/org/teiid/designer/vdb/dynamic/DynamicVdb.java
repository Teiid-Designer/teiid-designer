/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.core.designer.util.OperationUtil.Unreliable;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.datatools.profiles.jbossds.IJBossDsProfileConstants;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.vdb.BasicVdb;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbFileEntry;
import org.teiid.designer.vdb.VdbImportVdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbPlugin;
import org.teiid.designer.vdb.VdbSchemaEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbSourceInfo;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicModel.Type;
import org.teiid.designer.vdb.manifest.ConditionElement;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.ImportVdbElement;
import org.teiid.designer.vdb.manifest.MaskElement;
import org.teiid.designer.vdb.manifest.MetadataElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PermissionElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.SourceElement;
import org.teiid.designer.vdb.manifest.TranslatorElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.w3c.dom.Document;

/**
 * Dynamic VDB needs to manage a *-vdb.xml file
 * This represents the manifest with embedded model <metadata/> element
 *
 * @author blafond
 *
 */
public class DynamicVdb extends BasicVdb {

    private Map<String, DynamicModel> models;

    /**
     * Default constructor
     */
    public DynamicVdb() {
        super();
    }

	/**
	 * Constructor for Eclipse-based use-cases where an IResource/IFile is available
	 *
	 * @param file
	 * @throws Exception
	 */
	public DynamicVdb(IFile file) throws Exception {
	    super(file);
	}

	private Map<String, DynamicModel> models() {
	    if (models == null)
	        models = new HashMap<String, DynamicModel>();

	    return models;
	}

	@Override
    public void read(final IFile file) throws Exception {
	    CoreArgCheck.isNotNull(file);

	    setSourceFile(file);

	    if(! file.exists() ) {
            return;
        }

	    final File dynVdbFile = file.getLocation().toFile();
	    if (dynVdbFile.length() == 0)
	        return; // file is empty so don't bother reading

        InputStream xml = null;
        try {
            xml = new FileInputStream(dynVdbFile);
            validate(xml);
        } finally {
            if (xml != null)
                xml.close();
        }

        OperationUtil.perform(new Unreliable() {

            InputStream fileStream = null;

            @Override
            public void doIfFails() {
                // Nothing to do
            }

            @Override
            public void finallyDo() throws Exception {
                if (fileStream != null)
                    fileStream.close();
            }

            @Override
            public void tryToDo() throws Exception {
                try {
                    fileStream = new FileInputStream(dynVdbFile);
                    DynamicVdb vdb = DynamicVdb.this;

                    // Initialize using manifest
                    final Unmarshaller unmarshaller = vdb.getJaxbContext().createUnmarshaller();
                    unmarshaller.setSchema(vdb.getManifestSchema());
                    final VdbElement manifest = (VdbElement)unmarshaller.unmarshal(fileStream);

                    CommentReader reader = new CommentReader(manifest);
                    reader.read(dynVdbFile);

                    vdb.setDescription(manifest.getDescription());
                    vdb.setVersion(manifest.getVersion());
                    vdb.setName(manifest.getName());

                    vdb.addComments(manifest.getComments());

                    // VDB properties
                    for (final PropertyElement property : manifest.getProperties()) {
                        final String name = property.getName();
                        final String value = property.getValue();

                        vdb.addPropertyComments(name, property.getComments());

                        if (Xml.PREVIEW.equals(name)) {
                            vdb.setPreview(Boolean.parseBoolean(value));
                            // The stored timeout is in milliseconds. We are converting to seconds for display in Designer
                        } else if (Xml.QUERY_TIMEOUT.equals(name)) {
                            int timeoutMillis = Integer.parseInt(value);
                            if (timeoutMillis > 0) {
                                vdb.setQueryTimeout(timeoutMillis / 1000);
                            }
                        } else if (Xml.ALLOWED_LANGUAGES.equals(name)) {
                            /*
                             *  EXAMPLE XML FRAGMENT
                             *  multiple properties allowed with SAME KEY different values
                             *  Need to discover and treat these differently
                                <property name="allowed-languages" value="javascript, perl, php"/>
                             */
                            vdb.getAllowedLanguages().addAllowedLanguage(value);
                        } else if (Xml.SECURITY_DOMAIN.equals(name)) {
                            vdb.setSecurityDomain(value);
                        } else if (Xml.GSS_PATTERN.equals(name)) {
                            vdb.setGssPattern(value);
                        } else if (Xml.PASSWORD_PATTERN.equals(name)) {
                            vdb.setPasswordPattern(value);
                        } else if (Xml.AUTHENTICATION_TYPE.equals(name)) {
                            vdb.setAuthenticationType(value);
                        } else if (Xml.AUTO_GENERATE_REST_WAR.equals(name)) {
                            vdb.setAutoGenerateRESTWar(Boolean.parseBoolean(value));
                        }  else {
                            vdb.setProperty(name, value);
                        }
                    }

                    for (final ModelElement element : manifest.getModels()) {
                        DynamicModel model = new DynamicModel();
                        model.setName(element.getName());
                        model.setVisible(element.isVisible());
                        model.setDescription(element.getDescription());

                        model.addComments(element.getComments());

                        for (final PropertyElement property : element.getProperties()) {
                            final String name = property.getName();
                            final String value = property.getValue();

                            model.setProperty(name, value);
                            model.addPropertyComments(name, property.getComments());
                        }

                        if (element.getMetadata() != null && element.getMetadata().size() > 0) {
                            MetadataElement metadataElement = element.getMetadata().get(0);
                            String schemaText = metadataElement.getSchemaText();
                            String metadataType = element.getMetadata().get(0).getType();
                            Metadata metadata = new Metadata();
                            metadata.setSchemaText(schemaText);
                            metadata.setType(metadataType);
                            metadata.addComments(metadataElement.getComments());
                            model.setMetadata(metadata);
                        }
                        model.setModelType(element.getType());
                        if (element.getSources() != null && !element.getSources().isEmpty()) {
                            for (final SourceElement sourceElement : element.getSources()) {
                                VdbSource modelSource = new VdbSource(
                                                                      vdb,
                                                                      sourceElement.getName(),
                                                                      sourceElement.getJndiName() == null ? EMPTY_STRING : sourceElement.getJndiName(),
                                                                      sourceElement.getTranslatorName() == null ? EMPTY_STRING : sourceElement.getTranslatorName());
                                modelSource.addComments(sourceElement.getComments());
                                model.addSource(modelSource);
                            }
                        }
                        vdb.addDynamicModel(model);
                    }

                    // Vdb Import entries
                    for (final ImportVdbElement element : manifest.getImportVdbEntries()) {
                        VdbImportVdbEntry vdbImport = new VdbImportVdbEntry(vdb, element.getName());
                        vdbImport.setImportDataPolicies(false);
                        vdbImport.setVersion(vdb.getVersion());
                        vdbImport.addComments(element.getComments());
                        vdb.addImport(vdbImport);
                    }

                    // load translator overrides
                    for (final TranslatorElement translatorElement : manifest.getTranslators()) {
                        TranslatorOverride translator = new TranslatorOverride(vdb, translatorElement);
                        translator.addComments(translatorElement.getComments());

                        for (final PropertyElement property : translatorElement.getProperties()) {
                            final String name = property.getName();
                            final String value = property.getValue();

                            translator.setProperty(name, value);
                            translator.addPropertyComments(name, property.getComments());
                        }

                        vdb.addTranslator(translator);
                    }

                    for (final DataRoleElement element : manifest.getDataPolicies()) {
                        DataRole role = new DataRole(element.getName());
                        role.setVdb(vdb);
                        role.setAllowCreateTempTables(element.allowCreateTempTables());
                        role.setAnyAuthenticated(element.isAnyAuthenticated());
                        role.setGrantAll(element.doGrantAll());
                        role.setDescription(element.getDescription());
                        role.addComments(element.getComments());

                        { // Handle Permissions
                            for (PermissionElement pe : element.getPermissions()) {
                                boolean allow = false;

                                if (pe != null) {
                                    allow = pe.isAllowLanguage();
                                }

                                Permission permission = new Permission(pe.getResourceName(), pe.isCreate(), pe.isRead(),
                                                                       pe.isUpdate(), pe.isDelete(), pe.isExecute(), pe.isAlter());
                                permission.addComments(pe.getComments());

                                ConditionElement condition = pe.getCondition();
                                if (condition != null) {
                                    permission.setCondition(condition.getSql());
                                    Boolean constraint = condition.getConstraint();
                                    permission.setConstraint(constraint == null ? false : constraint);
                                    permission.setConditionComments(condition.getComments());
                                }

                                MaskElement mask = pe.getMask();
                                if (mask != null) {
                                    if (mask.getOrder() != null) {
                                        permission.setOrder(Integer.valueOf(mask.getOrder()));
                                    }
                                    permission.setMask(mask.getSql());
                                    permission.setMaskComments(mask.getComments());
                                }

                                if (allow) {
                                    permission.setAllowLanguage(true);
                                }

                                role.addPermission(permission);
                            }
                        }

                        vdb.addDataRole(role);

                        for (String mappedRoleName : element.getMappedRoleNames()) {
                            role.addRoleName(mappedRoleName);
                        }
                    }

                } finally {
                    if (fileStream != null)
                        fileStream.close();
                }
            }
        });
	}

    /**
     * @param xml
	 * @return true if vdb file is valid
	 * @throws Exception 
     */
    private boolean validate(InputStream xml) throws Exception {
        if (xml == null)
            return false;

        Schema schema = VdbUtil.getManifestSchema();
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));

        return true;
    }

    private void marshallComments(VdbElement vdbElement, Document document) {
        CommentWriter writer = new CommentWriter(document);
        writer.visit(vdbElement);
    }

    /**
     * Export vdb to destination writer. If null then use source file}
     * @param destination
     * @throws Exception 
     */
	public void write(Writer destination) throws Exception {
	    if( destination == null ) {
	        File destFile = getSourceFile().getFullPath().toFile();
	        destination = new FileWriter(destFile);
	    }

	    VdbElement vdbElement = new VdbElement(this);

	    try {
	        final Marshaller marshaller = getJaxbContext().createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	        marshaller.setSchema(getManifestSchema());

	        //
            // To get around the lack of CDATA support in jaxb, first marshall
            // to a DOM then use an XSL transformer to include the CDATA pragmas
            //
	        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        Document document = docBuilderFactory.newDocumentBuilder().newDocument();

	        // Marshall the feed object into the empty document.
	        marshaller.marshal(vdbElement, document);
	        marshallComments(vdbElement, document);

	        // Transform the DOM to the output stream
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
	        transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "metadata"); //$NON-NLS-1$
	        transformer.transform(new DOMSource(document), new StreamResult(destination));

	    } finally {
	        destination.close();
	    }
	}

	/** (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#getDynamicModels()
     */
    @Override
    public Collection<DynamicModel> getDynamicModels() {
        return Collections.unmodifiableCollection(models().values());
    }

	/** (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#addDynamicModel(org.teiid.designer.vdb.dynamic.DynamicModel)
     */
    @Override
    public void addDynamicModel(DynamicModel model) {
        DynamicModel existing = models().put(model.getName(), model);
        model.setVdb(this);
        setChanged(existing != null);
    }

    @Override
    public boolean isSynchronized() {
        return isChanged();
    }

    @Override
    public void synchronize() throws Exception {
        // TODO
        // Not sure how to synchronize atm
    }

    @Override
    public void save() throws Exception {
        write(null);
    }

    /** (non-Javadoc)
     * @see org.teiid.designer.vdb.Vdb#removeDynamicModel(java.lang.String)
     */
    @Override
    public void removeDynamicModel(String modelToRemove) {
        DynamicModel removed = models().remove(modelToRemove);
        setChanged(removed != null);
    }

    @Override
    public <T extends VdbEntry> T addEntry(IPath name) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void synchronizeUdfJars(Set<VdbFileEntry> newJarEntries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<VdbSchemaEntry> getSchemaEntries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<VdbFileEntry> getUdfJarEntries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getUdfJarNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<VdbFileEntry> getUserFileEntries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<VdbEntry> getEntries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<VdbModelEntry> getModelEntries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<File> getModelFiles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<File> getSchemaFiles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEntry(VdbEntry entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DynamicVdb clone() {
        DynamicVdb clone = new DynamicVdb();
        populateVdb(clone);

        for (DynamicModel dynModel : getDynamicModels()) {
            DynamicModel cloneModel = dynModel.clone();
            clone.addDynamicModel(cloneModel);
        }

        return clone;
    }

    @Override
    public DynamicVdb dynVdbConvert(IFile destination, Properties properties) throws Exception {
        CoreArgCheck.isNotNull(destination);

        File newVdbFile = destination.getLocation().toFile();
        FileWriter writer = null;
        try {
             writer = new FileWriter(newVdbFile);
            this.write(writer);
            DynamicVdb vdb = new DynamicVdb(destination);
            return vdb;
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    @Override
    public XmiVdb xmiVdbConvert(IFile destination, Properties options) throws Exception {
        NullProgressMonitor monitor = new NullProgressMonitor();

        try {
            //
            // Broadcast that a conversion is underway
            //
            VdbPlugin.singleton().setConversionInProgress(true);

            XmiVdb xmiVdb = new XmiVdb(destination);

            //
            // Populate the new vdb with the basic specification
            //
            populateVdb(xmiVdb);

            //
            // No external files coming from dynamic vdb so
            // no file entries to populate
            //

            List<DynamicModel> dynamicModels = new ArrayList<DynamicModel>(getDynamicModels());
            if (! dynamicModels.isEmpty())
                Collections.sort(dynamicModels, new DynamicModelComparator());

            // NOTE that objects in one model may have references to objects in another model
            // Example is a Materialized Table Reference
            // So we'll need to:
            //  - capture these references (simple model name and object name?) in each DdlImporter
            //  - keep a map of all DdlImporters and their resulting ModelResource
            //  - AFTER all models are created and saved
            //  - Get the deferred reference objects for each importer
            //     - If they exist, then run a utility to find the reference object and the target View and set the EMF reference
            
            Map<ModelResource, DdlImporter> importerModelMap = new HashMap<ModelResource, DdlImporter>();
            
            
            for (DynamicModel dynModel : dynamicModels) {
                IFile sourceFile = this.getSourceFile();
                IContainer parent = sourceFile.getParent();

                String fileName = dynModel.getName() + DOT_XMI;
                ModelResource modelResource = null;

                //
                // Create the empty model
                //
                IFile modelFile = parent.getFile(new Path(fileName));
                if (modelFile.exists())
                    modelFile.delete(true, monitor);

                modelResource = ModelerCore.create(modelFile);
                if (modelResource == null)
                    throw new Exception("Failed to create model resource"); //$NON-NLS-1$

                //
                // Apply the model annotation
                //
                ModelAnnotation annotation = modelResource.getModelAnnotation();
                annotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
                annotation.setModelType(ModelType.get(dynModel.getModelType().getType()));

                //
                // Inject the source properties into the model
                //
                VdbSource[] sources = dynModel.getSources();
                if (sources != null) {
                    for (VdbSource source : sources) {
                        String translatorProperty = IConnectionInfoHelper.TRANSLATOR_NAMESPACE
                                                    + IConnectionInfoHelper.TRANSLATOR_NAME_KEY;
                        ModelUtil.setModelAnnotationPropertyValue(modelResource, translatorProperty, source.getTranslatorName());

                        String jndiProperty = IConnectionInfoHelper.CONNECTION_NAMESPACE + IJBossDsProfileConstants.JNDI_PROP_ID;
                        ModelUtil.setModelAnnotationPropertyValue(modelResource, jndiProperty, source.getJndiName());
                    }
                }

                //
                // Save the resource
                //
                modelResource.save(monitor, false);

                //
                // Index the resource
                //
                ModelBuildUtil.indexResources(monitor, Collections.singleton(modelResource.getCorrespondingResource()));

                //
                // If we have a some DDL then importer it into the model resource
                //
                Metadata metadata = dynModel.getMetadata();
                if (metadata != null) {
                    IProject project = parent.getProject();

                    DdlImporter importer = new DdlImporter(new IProject[] {project});

                    // Set the destination the model file
                    importer.setModelFolder(parent);
                    importer.setModelName(fileName);

                    // Set some options
                    importer.setOptToCreateModelEntitiesForUnsupportedDdl(false);

                    String ddlAsDescriptionOption = options.getProperty(Vdb.SET_DDL_AS_DESCRIPTION, Boolean.FALSE.toString());
                    importer.setOptToSetModelEntityDescription(Boolean.parseBoolean(ddlAsDescriptionOption));

                    // Set the model type
                    Type dynModelType = dynModel.getModelType();
                    String modelType = dynModelType.toString();
                    importer.setModelType(ModelType.get(modelType));
                    importer.setGenerateDefaultSQL(Type.VIRTUAL.equals(dynModelType));

                    // Not actually used by the importer but better to
                    // just populate it in case used in the future.
                    importer.setDdlFileName(getSourceFile().getLocation().toOSString());

                    // Limit the importer to Teiid-only syntax
                    importer.setSpecifiedParser("TEIID"); //$NON-NLS-1$

                    //
                    // Import the ddl
                    //
                    importer.importDdl(metadata.getSchemaText(), monitor, 1, new Properties());

                    if (importer.hasParseError()) {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("Error Message:").append(TAB); //$NON-NLS-1$
                        buffer.append(importer.getParseErrorMessage().trim()).append(NEW_LINE);

                        buffer.append(TAB).append(TAB).append(SPACE).append(SPACE).append("Error Line Number:").append(TAB); //$NON-NLS-1$
                        buffer.append(importer.getParseErrorLineNumber()).append(NEW_LINE);

                        buffer.append(TAB).append(TAB).append(SPACE).append(SPACE).append("Error Column Number:").append(TAB); //$NON-NLS-1$
                        buffer.append(importer.getParseErrorColNumber()).append(NEW_LINE);

                        buffer.append(TAB).append(TAB).append(SPACE).append(SPACE).append("Error Index:").append(TAB); //$NON-NLS-1$
                        buffer.append(importer.getParseErrorIndex()).append(NEW_LINE);

                        throw new Exception(buffer.toString());
                    }

                    if (!importer.noDdlImported()) {
                        importer.save(monitor, 1);
                    }

                    modelResource = importer.model();
                    // Add model description
                    String desc = dynModel.getDescription();
                    if( !StringUtilities.isEmpty(desc) ) {
                    	modelResource.getModelAnnotation().setDescription(desc);
                    	modelResource.save(monitor, false);
                    }
                    
                    importerModelMap.put(modelResource, importer);
                }

                VdbModelEntry modelEntry = xmiVdb.addEntry(modelFile.getFullPath());

                //
                // Set any model properties
                //
                for (Map.Entry<Object, Object> prop : dynModel.getProperties().entrySet()) {
                    modelEntry.setProperty(prop.getKey().toString(), prop.getValue().toString());
                }
                
                String desc = dynModel.getDescription();
                if( !StringUtilities.isEmpty(desc) ) {
                	modelEntry.setDescription(desc);
                }

                VdbSourceInfo sourceInfo = modelEntry.getSourceInfo();
                sourceInfo.setIsMultiSource(dynModel.isMultiSource());
                sourceInfo.setAddColumn(dynModel.doAddColumn());
                sourceInfo.setColumnAlias(dynModel.getColumnAlias());

                //
                // Check to ensure the sources are added to the VdbModelEntry
                //
                for (VdbSource source : dynModel.getSources()) {
                    sourceInfo.add(source.getName(), source.getJndiName(), source.getTranslatorName());
                }
                
                // copy any model properties into archive VDB
                for (Map.Entry<Object, Object> entry : dynModel.getProperties().entrySet()) {
                	modelEntry.setProperty(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            
            // Now process any materialized table references
            for( DdlImporter importer : importerModelMap.values() ) {
            	importer.setMaterializedTableReferences(importerModelMap.keySet());
            }
            

            return xmiVdb;
        } finally {
            VdbPlugin.singleton().setConversionInProgress(false);
        }
    }
}
