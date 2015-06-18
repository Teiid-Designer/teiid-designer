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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.OperationUtil.Unreliable;
import org.teiid.designer.komodo.vdb.DynamicModel;
import org.teiid.designer.komodo.vdb.Metadata;
import org.teiid.designer.komodo.vdb.VdbManagementException;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.vdb.BasicVdb;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbFileEntry;
import org.teiid.designer.vdb.VdbImportVdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbSchemaEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.manifest.ConditionElement;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.ImportVdbElement;
import org.teiid.designer.vdb.manifest.MaskElement;
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
	    if( file == null || ! file.exists() ) {
            throw new VdbManagementException("File " + file.getFullPath() + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
        }

	    setFile(file);

	    final File dynVdbFile = file.getLocation().toFile();
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
                    vdb.setDescription(manifest.getDescription());
                    vdb.setVersion(manifest.getVersion());
                    // VDB properties
                    for (final PropertyElement property : manifest.getProperties()) {
                        final String name = property.getName();
                        final String value = property.getValue();

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
                        } else {
                            vdb.setProperty(name, value);
                        }
                    }

                    for (final ModelElement element : manifest.getModels()) {
                        DynamicModel model = new DynamicModel();
                        model.setName(element.getName());
                        model.setVisible(element.isVisible());
                        if (element.getMetadata() != null && element.getMetadata().size() > 0) {
                            String schemaText = element.getMetadata().get(0).getSchemaText();
                            String metadataType = element.getMetadata().get(0).getType();
                            Metadata metadata = new Metadata();
                            metadata.setSchemaText(schemaText);
                            metadata.setType(metadataType);
                            model.setMetadata(metadata);
                        }
                        model.setModelType(element.getType());
                        if (element.getSources() != null && !element.getSources().isEmpty()) {
                            for (final SourceElement source : element.getSources()) {
                                VdbSource modelSource = new VdbSource(
                                                                      vdb,
                                                                      source.getName(),
                                                                      source.getJndiName() == null ? EMPTY_STRING : source.getJndiName(),
                                                                      source.getTranslatorName() == null ? EMPTY_STRING : source.getTranslatorName());
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
                        vdb.addImport(vdbImport);
                    }

                    // load translator overrides
                    for (final TranslatorElement translatorElement : manifest.getTranslators()) {
                        TranslatorOverride translator = new TranslatorOverride(vdb, translatorElement);
                        vdb.addTranslator(translator);
                    }

                    for (final DataRoleElement element : manifest.getDataPolicies()) {
                        DataRole role = new DataRole(element.getName());
                        role.setVdb(vdb);
                        role.setAllowCreateTempTables(element.allowCreateTempTables());
                        role.setAnyAuthenticated(element.isAnyAuthenticated());
                        role.setGrantAll(element.doGrantAll());

                        { // Handle Permissions
                            for (PermissionElement pe : element.getPermissions()) {
                                boolean allow = false;

                                if (pe != null) {
                                    allow = pe.isAllowLanguage();
                                }

                                Permission permission = new Permission(pe.getResourceName(), pe.isCreate(), pe.isRead(),
                                                                       pe.isUpdate(), pe.isDelete(), pe.isExecute(), pe.isAlter());

                                ConditionElement condition = pe.getCondition();
                                if (condition != null) {
                                    permission.setCondition(condition.getSql());
                                    permission.setConstraint(condition.getConstraint());
                                }

                                MaskElement mask = pe.getMask();
                                if (mask != null) {
                                    if (mask.getOrder() != null) {
                                        permission.setOrder(Integer.valueOf(mask.getOrder()));
                                    }
                                    permission.setMask(mask.getSql());
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
     * @see org.teiid.designer.vdb.Vdb#addDynamicModel(org.teiid.designer.komodo.vdb.DynamicModel)
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
    public DynamicVdb dynVdbConvert() {
        return this;
    }

    @Override
    public XmiVdb xmiVdbConvert() {
        throw new UnsupportedOperationException();
    }
}
