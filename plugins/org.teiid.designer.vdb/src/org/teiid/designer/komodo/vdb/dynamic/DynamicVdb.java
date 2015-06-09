/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.core.resources.IFile;
import org.teiid.designer.komodo.vdb.BasicVdb;
import org.teiid.designer.komodo.vdb.Condition;
import org.teiid.designer.komodo.vdb.DataRole;
import org.teiid.designer.komodo.vdb.Mask;
import org.teiid.designer.komodo.vdb.Metadata;
import org.teiid.designer.komodo.vdb.Model;
import org.teiid.designer.komodo.vdb.ModelSource;
import org.teiid.designer.komodo.vdb.Permission;
import org.teiid.designer.komodo.vdb.Translator;
import org.teiid.designer.komodo.vdb.VdbImport;
import org.teiid.designer.komodo.vdb.VdbManagementException;
import org.teiid.designer.komodo.vdb.manifest.ConditionElement;
import org.teiid.designer.komodo.vdb.manifest.DataRoleElement;
import org.teiid.designer.komodo.vdb.manifest.ImportVdbElement;
import org.teiid.designer.komodo.vdb.manifest.MaskElement;
import org.teiid.designer.komodo.vdb.manifest.ModelElement;
import org.teiid.designer.komodo.vdb.manifest.PermissionElement;
import org.teiid.designer.komodo.vdb.manifest.PropertyElement;
import org.teiid.designer.komodo.vdb.manifest.SourceElement;
import org.teiid.designer.komodo.vdb.manifest.TranslatorElement;
import org.teiid.designer.komodo.vdb.manifest.VdbElement;
import org.xml.sax.SAXException;

/**
 * @author blafond
 *
 */
public class DynamicVdb extends BasicVdb {

	File dynamicVdbFile;
	
	// Dynamic VDB needs to manage a *-vdb.xml file
	// This represents the manifest with embedded model <metadata/> element

	/**
	 * 
	 */
	public DynamicVdb() {
		super();
	}
	
	/**
	 * @param name 
	 * 
	 */
	public DynamicVdb(String name) {
		super();
		setName(name);
	}
	
	/**
	 * Constructor for Eclipse-based use-cases where an IResource/IFile is available
	 * @param file
	 */
	public DynamicVdb(IFile file) {
		this(file.getLocation().toFile());
	}
	
	/**
	 * Actual File on file system use-case
	 * @param file
	 */
	public DynamicVdb(File file) {
		super();
		this.dynamicVdbFile = file;
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#load()
	 */
	@Override
	public void load() throws Exception {
		if( dynamicVdbFile == null || !dynamicVdbFile.exists() ) {
			throw new VdbManagementException("File " + dynamicVdbFile.getAbsolutePath() + " does not exist");
		}
		
//		// Assume this is an XML file
//        OperationUtil.perform(new Unreliable() {

            InputStream fileStream = null;

//            @Override
//            public void doIfFails() {
//            }
//
//            @Override
//            public void finallyDo() throws Exception {
//                if (fileStream != null) fileStream.close();
//            }

//            @Override
//            public void tryToDo() throws Exception {
            try {
            	fileStream = new FileInputStream(dynamicVdbFile);

                // Initialize using manifest
                final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                unmarshaller.setSchema(getManifestSchema());
                final VdbElement manifest = (VdbElement)unmarshaller.unmarshal(fileStream);
                setDescription(manifest.getDescription());
                setVersion(manifest.getVersion());
                setName(manifest.getName());
                // VDB properties
                for (final PropertyElement property : manifest.getProperties()) {
                    final String name = property.getName();
                    final String value = property.getValue();
                    
                    if (Xml.PREVIEW.equals(name)) {
                    	setPreview(Boolean.parseBoolean(value));
                        // The stored timeout is in milliseconds. We are converting to seconds for display in Designer
                    } else if (Xml.QUERY_TIMEOUT.equals(name)) { 
                        int timeoutMillis = Integer.parseInt(value);
                        if (timeoutMillis > 0) {
                            setQueryTimeout(timeoutMillis / 1000);
                        }
                    } else if(Xml.ALLOWED_LANGUAGES.equals(name) ) {
                    	/*
                    	 *  EXAMPLE XML FRAGMENT
                    	 *  multiple properties allowed with SAME KEY different values
                    	 *  Need to discover and treat these differently
						    <property name="allowed-languages" value="javascript, perl, php"/>
                    	 */
                    	getAllowedLanguages().addAllowedLanguage(value);
                    } else if (Xml.SECURITY_DOMAIN.equals(name)) { 
                        setSecurityDomain(value);
                    } else if (Xml.GSS_PATTERN.equals(name)) { 
                        setGssPattern(value);
                    } else if (Xml.PASSWORD_PATTERN.equals(name)) { 
                        setPasswordPattern(value);
                    } else if (Xml.AUTHENTICATION_TYPE.equals(name)) { 
                        setAuthenticationType(value);
                    } else {
                    	setProperty(name, value);
                    }
                }
                
                for (final ModelElement element : manifest.getModels()) {
                	Model model = new Model();
                	model.setName(element.getName());
                	model.setVisible(element.isVisible());
                	if( element.getMetadata() != null && element.getMetadata().size() > 0) {
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
                        	ModelSource modelSource = new ModelSource();
                        	modelSource.setName(source.getName());
                        	modelSource.setJndiName(source.getJndiName() == null ? EMPTY_STRING : source.getJndiName());
                        	modelSource.setTranslatorName(source.getTranslatorName() == null ? EMPTY_STRING : source.getTranslatorName());
                        	model.addSource(modelSource);
                        }
                	}
                	addModel(model);
                }                        
                
                // Vdb Import entries
                for (final ImportVdbElement element : manifest.getImportVdbEntries()) {
                	VdbImport vdbImport = new VdbImport(element.getName(), false, getVersion());
                	addImport(vdbImport);
                }
                
                // load translator overrides
                for (final TranslatorElement translatorElement : manifest.getTranslators()) {
                	Translator translator = new Translator();
                	translator.setName(translatorElement.getName());
                	translator.setDescription(translatorElement.getDescription());
                	translator.setType(translatorElement.getType());
                	
                	for( PropertyElement prop : translatorElement.getProperties() ) {
                		translator.setProperty(prop.getName(), prop.getValue());
                	}
                	addTranslator(translator);
                }

                for (final DataRoleElement element : manifest.getDataPolicies()) {
                	DataRole role = new DataRole();
                	role.setName(element.getName());
                	role.setAllowCreateTempTables(element.allowCreateTempTables());
                	role.setAnyAuthenticated(element.isAnyAuthenticated());
                	role.setGrantAll(element.doGrantAll());
                	
                	{ // Handle Permissions
                        for( PermissionElement pe : element.getPermissions()) {
	                       	 boolean allow = false;
	                       	 
	                       	 if( pe != null ) {
	                       		 allow = pe.isAllowLanguage();
	                       	 }
	                       	 
	                       	 Permission permission = new Permission(pe.getResourceName(), 
	                       			 pe.isCreate(), pe.isRead(), pe.isUpdate(), pe.isDelete(), pe.isExecute(), pe.isAlter());
	                				 
	                       	 ConditionElement condition = pe.getCondition();
	                       	 if( condition != null )  {
	                       		 permission.setCondition(new Condition(condition.getSql(), condition.getConstraint()));
	                       	 }
	                       	 
	                       	 MaskElement mask = pe.getMask();
	                       	 if( mask != null )  {
	                       		 Mask theMask = new Mask();
	                       		 theMask.setName(mask.getSql());
	                     		 if( mask.getOrder() != null) {
	                     			theMask.setOrder(Integer.valueOf(mask.getOrder()));
	                       		 }
	                       		 permission.setMask(theMask);
	                       	 }
	                       	 
	                       	 if( allow ) {
	                       		 permission.setAllowLanguage(true);
	                       	 }
	                       	 
	                   		 role.addPermission(permission);
	                	}
                	}
                	
                	addDataRole(role);
                	
                	for( String mappedRoleName : element.getMappedRoleNames() ) {
                		role.addMappedRole(mappedRoleName);
                	}
                }
            } finally {
            	if (fileStream != null) fileStream.close();
            }
//        });
	}

	/** (non-Javadoc)
	 * @see org.teiid.designer.komodo.vdb.Vdb#export()
	 */
	@Override
	public void export() {
        FileOutputStream out = null;
        if( dynamicVdbFile == null ) {
        	dynamicVdbFile = new File(getOriginalFilePath());
        }

        try {
			out = new FileOutputStream(dynamicVdbFile);

			VdbElement vdbElement = new VdbElement(this);
			
			try {
			    final Marshaller marshaller = getJaxbContext().createMarshaller();
			    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			    marshaller.setSchema(getManifestSchema());
			    marshaller.marshal(vdbElement, dynamicVdbFile);
			} finally {
			    out.close();
			}

			out = null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
    /** (non-Javadoc)
     * @return context
     * @throws JAXBException 
     * @see org.teiid.designer.vdb.Vdb#getJaxbContext()
     */
	public JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(new Class<?>[] { VdbElement.class });
    }

    /** (non-Javadoc)
     * @return schema
     * @throws SAXException 
     * @see org.teiid.designer.vdb.Vdb#getManifestSchema()
     */
	public Schema getManifestSchema() throws SAXException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(VdbElement.class.getResource("/vdb-deployer.xsd")); //$NON-NLS-1$
    }


}
