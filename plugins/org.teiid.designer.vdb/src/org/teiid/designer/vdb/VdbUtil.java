/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.ChecksumUtil;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.OperationUtil.Unreliable;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ResourceFinder;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.vdb.Vdb.Xml;
import org.teiid.designer.vdb.file.ValidationVersionCallback;
import org.teiid.designer.vdb.file.VdbFileProcessor;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.MetadataElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.ProblemElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.Severity;
import org.teiid.designer.vdb.manifest.SourceElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.xml.sax.SAXException;

/**
 * Utility methods used to query VDB manifest and VDB's
 *
 * @since 8.0
 */
public class VdbUtil implements VdbConstants {

    @SuppressWarnings( "javadoc" )
    public static final String PHYSICAL = "PHYSICAL"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String VIRTUAL = "VIRTUAL"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String FUNCTION = "FUNCTION"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String OTHER = "OTHER"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String DEPRECATED_TYPE = "TYPE"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String XML_EXTENSION = "XML"; //$NON-NLS-1$
    

    /**
     * @param theVdb
     * @return list of vdb model files
     */
    public static Collection<IFile> getVdbModels( Vdb theVdb ) {
        Collection<IFile> iFiles = new ArrayList<IFile>();

        for (VdbEntry modelEntry : theVdb.getModelEntries()) {
            IResource resource = ModelerCore.getWorkspace().getRoot().findMember(modelEntry.getPath());

            // if resource has been moved in the workspace since being added to the VDB then it will not be found
            if ((resource != null) && resource.exists()) {
                iFiles.add((IFile)resource);
            }
        }

        return iFiles;
    }

    /**
     * @param file
     * @return preview attribute value for VDB. true or false
     * @throws Exception 
     */
    public static boolean isPreviewVdb( final IFile file ) throws Exception {
        CoreArgCheck.isNotNull(file, "file is null"); //$NON-NLS-1$

        if (file.exists()) {
            // if VDB file is empty just check file name
            if (file.getLocation().toFile().length() == 0L) {
                // make sure file prefix and extension is right
                if (!ITeiidVdb.VDB_EXTENSION.equals(file.getFileExtension())) {
                    return false;
                }

                return file.getName().startsWith(VdbConstants.PREVIEW_PREFIX);
            }

            VdbElement manifest = VdbUtil.getVdbManifest(file);
            if (manifest != null) {
                // VDB properties
                for (final PropertyElement property : manifest.getProperties()) {
                    final String name = property.getName();
                    if (Xml.PREVIEW.equals(name)) {
                        return Boolean.parseBoolean(property.getValue());
                    }
                }
            }
        }

        return false;
    }
    
    /**
     * @param file
     * @return preview attribute value for VDB. true or false
     */
    public static boolean isDynamicVdb( final IFile file ) {
        CoreArgCheck.isNotNull(file, "file is null"); //$NON-NLS-1$
        
        boolean result = false;
        
        if (file.exists()) {
            // if VDB file is empty just check file name
            if (file.getLocation().toFile().length() > 0) {
                // make sure file extension is right
                if (! XML_EXTENSION.equalsIgnoreCase(file.getFileExtension())) {
                    return false;
                }
                
	            VdbElement manifest = null;
	            
	            try {
					manifest = VdbUtil.getVdbManifest(file.getLocation().toFile());
				} catch (Exception ex) {
					result = false; //VdbPlugin.UTIL.log(IStatus.ERROR, ex, "Problem loading VDB manifest for VDB = " + file.getName());
				}
	            
	            if (manifest != null) {
	            	result = true;
	            }
            }
        }
        
        return result;
    }
    
    
    /**
     * @param file
     * @return preview attribute value for VDB. true or false
     */
    public static boolean isDdlVdb( final IFile file ) {
        CoreArgCheck.isNotNull(file, "file is null"); //$NON-NLS-1$
        
        boolean result = false;
        
        if (file.exists()) {
            // if VDB file is empty just check file name
            if (file.getLocation().toFile().length() > 0) {
                // make sure file prefix and extension is right
                if ( ! ITeiidVdb.VDB_EXTENSION.equalsIgnoreCase(file.getFileExtension())) {
                    return false;
                }
                
	            VdbElement manifest = null;
	            
	            try {
					manifest = VdbUtil.getVdbManifest(file);
				} catch (Exception ex) {
					VdbPlugin.UTIL.log(ex);
				}
	            
	            if (manifest != null) {
	                for( ModelElement model : manifest.getModels() ) {
	                    List<MetadataElement> allMetadata =  model.getMetadata();
	                    if( allMetadata != null) {
	                        for( MetadataElement metadata :allMetadata ) {
	                            if( metadata.getType().equalsIgnoreCase("DDL-FILE") ) {
	                                result = true;
	                                break;
	                            }
	                        }
	                    }
	                    if( result ) break;
	                }
	            }
            }
        }
        
        return result;
    }

    /**
     * Utility method to determine if a vdb contains models of a certain "class"
	 * @param file
	 * * @param modelClass
     * @param type
     * @return preview attribute value for VDB. true or false
     * @throws Exception 
     */
	public static boolean hasModelClass(final IFile file, final String modelClass, final String type) throws Exception {
        if (file.exists() && ITeiidVdb.VDB_EXTENSION.equals(file.getFileExtension())) {
            VdbElement manifest = VdbUtil.getVdbManifest(file);
            if (manifest != null) {
                for (ModelElement model : manifest.getModels()) {
                    String typeValue = model.getType();
                    if (type.equalsIgnoreCase(typeValue)) {
                        for (final PropertyElement property : model.getProperties()) {
                            final String name = property.getName();
                            if (ModelElement.MODEL_CLASS.equals(name)) {
                                String modelClassValue = property.getValue();
                                if (modelClass.equalsIgnoreCase(modelClassValue)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Utility method to extract a copy of a VDB zip file's vdb.xml in VDB element xml structure
     * @param file
     * @return the root VdbElement
     * @throws Exception 
     */
    public static VdbElement getVdbManifest( final IFile file ) throws Exception {
        final VdbElement[] manifest = new VdbElement[1];

        if (!file.exists()) {
            return null;
        }

        if( ModelUtil.isVdbArchiveFile(file) ) {
	        try {
	            OperationUtil.perform(new Unreliable() {
	
	                ZipFile archive = null;
	                InputStream entryStream = null;
	
	                @Override
	                public void doIfFails() {
	                }
	
	                @Override
	                public void finallyDo() throws Exception {
	                    if (entryStream != null) entryStream.close();
	                    if (archive != null) archive.close();
	                }
	
	                @Override
	                public void tryToDo() throws Exception {
	                    archive = new ZipFile(file.getLocation().toString());
	                    boolean foundManifest = false;
	                    for (final Enumeration<? extends ZipEntry> iter = archive.entries(); iter.hasMoreElements();) {
	                        final ZipEntry zipEntry = iter.nextElement();
	                        entryStream = archive.getInputStream(zipEntry);
	                        if (zipEntry.getName().equals(MANIFEST)) {
	                            // Initialize using manifest
	                            foundManifest = true;
	                            final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
	                            unmarshaller.setSchema(getManifestSchema());
	                            manifest[0] = (VdbElement)unmarshaller.unmarshal(entryStream);
	
	                        }
	                        // Don't process any more than we need to.
	                        if (foundManifest) {
	                            break;
	                        }
	                    }
	                }
	            });
	        } catch (Exception ex) {
	            VdbPlugin.UTIL.log(ex);
	            return null;
	        }
	        return manifest[0];
        }
        
        if( file.getFileExtension() != null && file.getFileExtension().equalsIgnoreCase("XML") ) {
	        try {
	            OperationUtil.perform(new Unreliable() {

	                InputStream fileStream = null;
	
	                @Override
	                public void doIfFails() {
	                }
	
	                @Override
	                public void finallyDo() throws Exception {
	                    if (fileStream != null) fileStream.close();
	                }
	
	                @Override
	                public void tryToDo() throws Exception {
                        fileStream = new FileInputStream(file.getLocation().toFile()); 
                        final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                        unmarshaller.setSchema(getManifestSchema());
                        manifest[0] = (VdbElement)unmarshaller.unmarshal(fileStream);
	                }
	            });
	        } catch (Exception ex) {
	            VdbPlugin.UTIL.log(ex);
	            return null;
	        }
	        return manifest[0];
        }

        return null;

    }
    
    /**
     * @param dynamicVdbFile
     * @return the VdbElement
     * @throws Exception
     */
    public static VdbElement getVdbManifest( final File dynamicVdbFile ) throws Exception {
    	final VdbElement[] manifest = new VdbElement[1];

        try {
            OperationUtil.perform(new Unreliable() {

                InputStream fileStream = null;

                @Override
                public void doIfFails() {
                }

                @Override
                public void finallyDo() throws Exception {
                    if (fileStream != null) fileStream.close();
                }

                @Override
                public void tryToDo() throws Exception {
                    final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                    unmarshaller.setSchema(getManifestSchema());
                    InputStream fileStream = new FileInputStream(dynamicVdbFile);
                    
                    manifest[0] = (VdbElement)unmarshaller.unmarshal(fileStream);
                }
            });
        } catch (Exception ex) {
        	if( ! (ex.getCause() instanceof UnmarshalException) ) {
        		VdbPlugin.UTIL.log(IStatus.ERROR, ex, "Error finding VDB manifest for file: " + dynamicVdbFile.getName());
        	}
            return null;
        }
        
    	return manifest[0];
    }
    
     /**
	 * @param xmlString
	 * @return the VdbElement
	 * @throws Exception
	 */
	public static VdbElement getVdbManifest( final String xmlString ) throws Exception {
    	final VdbElement[] manifest = new VdbElement[1];

        try {
            OperationUtil.perform(new Unreliable() {

                InputStream fileStream = null;

                @Override
                public void doIfFails() {
                }

                @Override
                public void finallyDo() throws Exception {
                    if (fileStream != null) fileStream.close();
                }

                @Override
                public void tryToDo() throws Exception {
                    final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                    unmarshaller.setSchema(getManifestSchema());
                    InputStream fileStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8")); //$NON-NLS-1$
                    
                    manifest[0] = (VdbElement)unmarshaller.unmarshal(fileStream);
                }
            });
        } catch (Exception ex) {
            VdbPlugin.UTIL.log(ex);
            return null;
        }
        
    	return manifest[0];
    }

    /**
     * @param file
     * @return version the vdb version number
     * @throws Exception 
     */
    public static int getVdbVersion( final IFile file ) throws Exception {

        if (file.exists()) {
            VdbElement manifest = VdbUtil.getVdbManifest(file);
            if (manifest != null) {
                return manifest.getVersion();
            }
        }

        return 0;
    }

    /**
     * Simple method that peeks inside a VDB manifest to check if the VDB was built with Teiid 8.0 or greater
     * The vdb version property value was added in Teiid Designer 8.2. So it's relatively safe to do this check.
     * 
     * @param file
     * @return vdb is based on Teiid 7
     */
    public static boolean isVdbTeiidVersion8orGreater( final IFile file) {
        if (file == null || ! file.exists())
            return false;

        ValidationVersionCallback callback = new ValidationVersionCallback(file);
        VdbFileProcessor processor = new VdbFileProcessor(callback);
        processor.process();

        ITeiidServerVersion validationVersion = callback.getValidationVersion();
        if (callback.hasException() || validationVersion == null)
            return false;

        return validationVersion.isGreaterThanOrEqualTo(Version.TEIID_8_0);
    }
    
    /**
     * @param modelElement the vdb model element
     * @return the uuid string. may be null
     */
    public static String getUuid(final ModelElement modelElement) {
	    for (final PropertyElement property : modelElement.getProperties()) {
	        final String name = property.getName();
	        if (ModelElement.MODEL_UUID.equals(name)) {
	            return property.getValue();
	        }
	    }
	    
	    return null;
    }
    
    /**
     * Builds a comma-separated string from an array of strings
	 * @param values an array of strings
     * @return string of comma separated values
     *
     */
    public static String buildCommaDelimitedString(AllowedLanguages values) {
        StringBuilder sb = new StringBuilder();
        int i=0;
        int numVal = values.size();
        for (String val : values) {
        	i++;
        	sb.append(val);
        	if( i< numVal ) {
        		sb.append(StringConstants.COMMA).append(StringConstants.SPACE);
        	}
        }
        
        return sb.toString();
    }
    static JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(new Class<?>[] {VdbElement.class});
    }

    /**
     * @return the vdb xsd as a resource
     */
    public static URL getVdbXsd() {
        return VdbElement.class.getResource(FORWARD_SLASH + VDB_DEPLOYER_XSD);
    }

    /**
     * @return the vdb xsd as a resource stream
     */
    public static InputStream getVdbXsdStream() {
        return VdbElement.class.getResourceAsStream(FORWARD_SLASH + VDB_DEPLOYER_XSD);
    }

    /**
     * @return the schema for the vdb manifest
     *
     * @throws SAXException
     */
    public static Schema getManifestSchema() throws SAXException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(getVdbXsd());
    }

    /**
     * This method converts a vdb manifest model type and model path to a Designer ModelType object
     * Reason being that an XML Schema (TYPE) model is defined in the vdb manifest as "OTHER"
     * @param vdbModelType
     * @param modelPath
     * @return ModelType
     */
    public static ModelType getModelType(String vdbModelType, String modelPath) {
        if (vdbModelType == OTHER && modelPath.toUpperCase().endsWith(".XSD")) { //$NON-NLS-1$
            return ModelType.TYPE_LITERAL;
        }

        return ModelType.get(vdbModelType);
    }

	/**
	 * Simple check to see if the model file is in the vdb
	 * 
	 * @param theVdb
	 * @param theModelFile
	 * @return true if model exists by name in vdb
	 * @throws Exception 
	 */
	public static boolean modelInVdb(final IFile theVdb, final IFile theModelFile) throws Exception {
		if (theVdb.exists()) {
			VdbElement manifest = VdbUtil.getVdbManifest(theVdb);
			if (manifest != null) {
				for (ModelElement model : manifest.getModels()) {
					String modelName = model.getName()+ StringConstants.DOT_XMI;
					if (modelName.equalsIgnoreCase(theModelFile.getName())) {
						// We found the model, now replace the path
						return true;
					}
				}
			}
		}
		return false;
	}

    private static void validateVdbVersion(final IFile theVdbFile, Collection<IStatus> statuses) {
        ITeiidServerVersion defaultTeiidVersion = ModelerCore.getTeiidServerVersion();
        ITeiidServerVersion maxDesignerVersion = Version.TEIID_DEFAULT.get();

        ValidationVersionCallback callback = new ValidationVersionCallback(theVdbFile);
        VdbFileProcessor processor = new VdbFileProcessor(callback);
        processor.process();

        ITeiidServerVersion validationVersion = callback.getValidationVersion();
        if (validationVersion == null) {
            /* No Validation version so probably pre-8.2 */
            statuses.add( new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
                                     VdbPlugin.UTIL.getString("vdbValidationWarning_noValidationVersionInVdb")) ); //$NON-NLS-1$
            return;
        }

        if (! validationVersion.compareTo(defaultTeiidVersion) && (validationVersion.isGreaterThan(defaultTeiidVersion) || validationVersion.isLessThan(defaultTeiidVersion)))
            /* Vdb version does not match teiid server version selected so may not deploy */
            statuses.add( new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
                                     VdbPlugin.UTIL.getString("vdbValidationWarning_differentValidationVersions", validationVersion, defaultTeiidVersion)) ); //$NON-NLS-1$    

        ITeiidServerVersion maxMmVersion = new TeiidServerVersion(maxDesignerVersion.getMajor(), maxDesignerVersion.getMinor(), ITeiidServerVersion.WILDCARD);
        if (validationVersion.isGreaterThan(maxMmVersion))
            /* Vdb version is greater than the tested Designer Teiid Version which means all bets are off! */
            statuses.add( new Status(IStatus.ERROR, VdbConstants.PLUGIN_ID,
                                     VdbPlugin.UTIL.getString("vdbValidationError_validationVersionUnsupported")) ); //$NON-NLS-1$
    }
	
	/**
	 * Simple check to see if the model file is in the vdb
	 * 
	 * @param theVdbFile
	 @return true if model exists by name in vdb
	 */
	public static MultiStatus validateVdbModelsInWorkspace(final IFile theVdbFile) {
		Collection<IStatus> statuses = new ArrayList<IStatus>();
		IProject theProject = theVdbFile.getProject();
		MultiStatus finalStatus = new MultiStatus(VdbConstants.PLUGIN_ID, 0, VdbPlugin.UTIL.getString("vdbValidationOK"), null); //$NON-NLS-1$
		
		if (theVdbFile.exists()) {

		    validateVdbVersion(theVdbFile, statuses);

            Vdb theVdb = null;
            VdbElement manifest = null;
            try {
                theVdb = new XmiVdb(theVdbFile);
                manifest = VdbUtil.getVdbManifest(theVdbFile);
            } catch (Exception ex) {
                statuses.add(new Status(IStatus.ERROR, VdbConstants.PLUGIN_ID, ex.getLocalizedMessage(), ex));
            }

			if (theVdb != null && manifest != null) {
				// Check Security settings
				String securityDomain = theVdb.getSecurityDomain();
				if( securityDomain != null ) {
					String gssPattern = theVdb.getGssPattern();
					String passwordPattern = theVdb.getPasswordPattern();
					String authenticationType = theVdb.getAuthenticationType();
					if( authenticationType != null ) {
						if( gssPattern != null || passwordPattern != null ) {
							statuses.add( new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID, 
									VdbPlugin.UTIL.getString("vdbValidationWarning_singleAuthenticationType_0_willBeIgnored", authenticationType)) ); //$NON-NLS-1$
						}
					}
				}
				
				for (ModelElement model : manifest.getModels()) {
					String modelName = model.getName();
					
					// Check for models with ERRORS
					for( ProblemElement problem : model.getProblems() ) {
						if( problem.getSeverity() == Severity.ERROR ) {
							statuses.add( new Status(IStatus.ERROR, VdbConstants.PLUGIN_ID, 
									VdbPlugin.UTIL.getString("vdbValidationError_modelContainsErrors", modelName)) ); //$NON-NLS-1$
							break;
						}
					}

					IResource resource = null;
					
					// Check if model with that name exists in project
					// first check if uuid == null

					String modelUuid = getUuid(model);
					
					if( modelUuid == null ) {
						
						Collection<IFile> resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(modelName, theProject);
						if( resources.size() == 1 ) {
							IFile someResource = resources.iterator().next();
							if( ! ModelUtil.isVdbArchiveFile(someResource)) {
								resource = someResource;
							}
						}
						
						if( resource != null ) {
							statuses.add( new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID, 
									VdbPlugin.UTIL.getString("vdbValidationWarning_modelUuidMissing", modelName)) ); //$NON-NLS-1$
						}
					} else {
						// Check if uuid exists in workspace or not
						resource = WorkspaceResourceFinderUtil.findIResourceByUUID(modelUuid);
						if( resource == null ) {
							// Find by name
							Collection<IFile> resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(modelName, theProject);
							IFile someResource = resources.iterator().next();
							if( ! ModelUtil.isVdbArchiveFile(someResource)) {
								resource = someResource;
							}
						}
					}
					
					boolean nameChanged = false;
					
					if( resource == null ) {
						statuses.add(  new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID, 
								VdbPlugin.UTIL.getString("vdbValidationWarning_noModelInWorkspace", modelName)) ); //$NON-NLS-1$
					} else {
						// check same name
						// Check that resource is not a VDB 
						String resourceName = FileUtils.getNameWithoutExtension(resource);
						if( ! modelName.equals(resourceName) ) {
							nameChanged = true;
						} else {
							if( ModelUtil.isVdbArchiveFile(resource) ) {
								continue;
							}
						}

						String path = model.getPath();
						
						// Check IPath
						IPath iPath = new Path(path);
						IResource expectedResourceAtPath = ModelerCore.getWorkspace().getRoot().findMember(iPath);
						
						if( expectedResourceAtPath == null || nameChanged ) {
							statuses.add(new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
									VdbPlugin.UTIL.getString("vdbValidationWarning_modelExistsWithDifferentLocationOrName", //$NON-NLS-1$
									modelName, resource.getFullPath()))); 
						} else {
							// Is it in sync
							if( ! isSynchronized(theVdb, (IFile)expectedResourceAtPath)) {
								statuses.add(new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
										VdbPlugin.UTIL.getString("vdbValidationWarning_modelNotSynchronized", //$NON-NLS-1$
										modelName))); 
							}
						}
					}
					
					// Check for single source binding but mutliple sources
					
					if( model.getSources() != null && model.getSources().size() > 1 ) {
						boolean multiSourceIsFalse = true;
						for( PropertyElement prop : model.getProperties() ) {
							if( prop.getName().equals(ModelElement.SUPPORTS_MULTI_SOURCE) ) {
								// Check boolean property
								if( Boolean.parseBoolean(prop.getValue()) ) {
									multiSourceIsFalse = false;
									break;
								}
							}
						}
						
						if( multiSourceIsFalse ) {
							statuses.add(new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
									VdbPlugin.UTIL.getString("vdbValidationWarning_singleSourceModelHasMultipleSources", //$NON-NLS-1$
									modelName))); 
						}
					}
					
					// Check for Missing Translator type and JNDI name and add WARNINGs
					
					if( model.getSources() != null ) {
						for( SourceElement elem : model.getSources()) {
							if( StringUtilities.isEmpty(elem.getTranslatorName()) ) {
								statuses.add(new Status(IStatus.ERROR, VdbConstants.PLUGIN_ID,
										VdbPlugin.UTIL.getString("vdbValidationWarning_sourceMissingTranslatorType", //$NON-NLS-1$
										modelName, theVdb.getName())));
								break;
							}
						}
						for( SourceElement elem : model.getSources()) {
							if( StringUtilities.isEmpty(elem.getJndiName()) ) {
								statuses.add(new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
										VdbPlugin.UTIL.getString("vdbValidationWarning_sourceMissingJndiName", //$NON-NLS-1$
										modelName, theVdb.getName())));
								break;
							}
						}
					}
					
				}
				
				// Check for duplicate model and/or user file names
				Map<String, String> fileFileNames = new HashMap<String, String>();
				Set<String> modelNamesWithMultiple = new HashSet<String>();
				
				for (ModelElement model : manifest.getModels()) {
					String modelName = model.getName();
					if( fileFileNames.get(modelName.toUpperCase()) != null ) {
						modelNamesWithMultiple.add(modelName);
					} else {
						fileFileNames.put(modelName.toUpperCase(), modelName);
					}
				}
				
				// Add a problem for duplicate model names
				for( String modelName : modelNamesWithMultiple ) {
					statuses.add(new Status(IStatus.ERROR, VdbConstants.PLUGIN_ID,
							VdbPlugin.UTIL.getString("vdbValidationError_duplicateModelNames", //$NON-NLS-1$
							modelName, theVdb.getName())));
					break;
				}

			}
		} else {
			statuses.add(new Status(IStatus.ERROR, VdbConstants.PLUGIN_ID, "ERROR : VDB " + theVdbFile.getName() + " does not exist")); //$NON-NLS-1$  //$NON-NLS-2$
		}
		
		if( ! statuses.isEmpty() ) {
	        final IStatus[] result = new IStatus[statuses.size()];
	        statuses.toArray(result);
			finalStatus = new MultiStatus(VdbConstants.PLUGIN_ID, 0, result, "ERROR : VDB " + theVdbFile.getName() + " has problems", null); //$NON-NLS-1$  //$NON-NLS-2$
		}
		
		return finalStatus;
	}
	
	/**
	 * @param modelName
	 * @param pathIncludingModel 
	 * @param vdb
	 * @return if model already exists in VDB or not
	 */
	public static boolean modelAlreadyExistsInVdb(String modelName, IPath pathIncludingModel, Vdb vdb) {
		// Check for duplicate model and/or user file names
		Map<String, String> existingNames = new HashMap<String, String>();
		Set<String> existingPaths = new HashSet<String>();
		
		for (VdbEntry model : vdb.getModelEntries()) {
			String existingName = model.getPath().removeFileExtension().lastSegment();
			IPath path = model.getPath();
			existingNames.put(existingName.toUpperCase(), existingName);
			existingPaths.add(path.toString().toUpperCase());
		}
		
		if(existingNames.get(modelName.toUpperCase()) != null && !existingPaths.contains(pathIncludingModel.toString().toUpperCase())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method to determine whether or not a model file (xmi) can be added to a VDB.
	 * The basic check is to look at the existing model names (without extension) and the target file to be added
	 * and include any dependent models that would be added. ALL of these names (without extension) must be unique
	 * and ignore case
	 * 
	 * @param model file either .xmi
	 * @param theVdb can be null
	 * @return true if model can be added to an existing VDB
	 * @throws Exception
	 */
	public static boolean canAddModelToVdb(final IFile model, final Vdb theVdb) throws Exception {
		ModelResource mr = ModelUtil.getModel(model);
		
		// Assume existing names will not include duplicates, but the MAP will insure they are unique
		Map<String, String> existingNames = new HashMap<String, String>();
		Set<String> existingPaths = new HashSet<String>();
		
		if( theVdb != null ) {
			for (VdbEntry modelEntry : theVdb.getModelEntries()) {
				String existingName = modelEntry.getPath().removeFileExtension().lastSegment();
				IPath path = modelEntry.getPath();
				existingNames.put(existingName.toUpperCase(), existingName);
				existingPaths.add(path.toString().toUpperCase());
			}
		}
		
		// Check target model first
		String targetModelName = FileUtils.getNameWithoutExtension(model);
		if( existingNames.get(targetModelName.toUpperCase()) != null 
				&& !existingPaths.contains(model.getFullPath().toString().toUpperCase())) {
			return false;
		}
		
		existingNames.put(targetModelName.toUpperCase(), targetModelName);
		
		// Now get dependent models and check those
		ResourceFinder finder = null;
		finder = ModelerCore.getModelContainer().getResourceFinder();
		Resource[] refs = finder.findReferencesFrom(mr.getEmfResource(), true, false);
		
		for( Resource res : refs ) {
		    if (ModelUtil.isXsdFile(res)) {
		        // See TEIIDDES-2120
		        // Xsd files are now added to 'Other Files' so are
		        // not applicable to this test
		        continue;
		    }

			ModelResource modelRes = ModelUtil.getModel(res);

			String refModelName = modelRes.getCorrespondingResource().getFullPath().removeFileExtension().lastSegment();
			IPath refModelPath = modelRes.getCorrespondingResource().getFullPath();
			if( existingNames.get(refModelName.toUpperCase()) != null 
					&& !existingPaths.contains(refModelPath.toString().toUpperCase())) {
				return false;
			}
			
			existingNames.put(refModelName.toUpperCase(), refModelName);
		}

		return true;
	}
	
	/**
	 * @param theVdb the vdb
	 * @param theModelFile the model file in the workspace that is also in the VDB
	 * @return if model file in vdb is synchronized
	 */
	public static boolean isSynchronized(final Vdb theVdb, final IFile theModelFile) {
        long fileCheckSum = 0L;
        boolean foundCheckSum = false;
        
        try {
			fileCheckSum = getCheckSum(theModelFile);
			foundCheckSum = true;
		} catch (Exception ex) {
			foundCheckSum = false;
		}

        if( foundCheckSum ) {
	 		for( VdbEntry modelEntry : theVdb.getModelEntries()) {
				if( modelEntry.getPath().lastSegment().equalsIgnoreCase(theModelFile.getName()) ) {
	 				return modelEntry.getChecksum() == fileCheckSum;
	 			}
	 		}
        }
        
        return false;
	}
	
    /**
     * Compute checksum for the given file.
     * 
     * @param f The file for which checksum needs to be computed
     * @return The checksum
     * @throws Exception
     * @since 4.3
     */
    public static long getCheckSum(final IFile f) throws Exception {
        CoreArgCheck.isNotNull(f);
        InputStream is = null;
        try {
            is = f.getContents();
            return ChecksumUtil.computeChecksum(is).getValue();
        } finally {
            if (is != null) try {
                is.close();
            } catch (final IOException err1) {
            }
        }
    }
	
	/**
	 * Method which returns a list of models in your workspace that have the wrong path defined in the specified VDB
	 * 
	 * @param theVdb the vdb
	 * @return the list of models with wrong paths in VDB
	 * @throws Exception 
	 */
	public static Collection<IFile> getModelsWithWrongPaths(final IFile theVdb) throws Exception {
		Collection<IFile> misMatchedResources = new ArrayList<IFile>();
		
		if (theVdb.exists()) {
			IProject theProject = theVdb.getProject();

			VdbElement manifest = VdbUtil.getVdbManifest(theVdb);
			if (manifest != null) {
				for (ModelElement model : manifest.getModels()) {
					String modelName = model.getName()+ StringConstants.DOT_XMI;
					Collection<IFile> resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(modelName, theProject);
					if( resources.size() == 1 ) {
						String path = model.getPath();
						IResource matchingResource = resources.iterator().next();
						// Check IPath
						IPath iPath = new Path(path);
						IResource resource = ModelerCore.getWorkspace().getRoot().findMember(iPath);
						
						if( resource == null ) {
							misMatchedResources.add((IFile)matchingResource);
						}
					}
				}
			}
		}
		
		return misMatchedResources;
	}
	
	/**
	 * @param modelElement
	 * @return the collection of model import strings
	 */
	public static Collection<String> getModelImports(ModelElement modelElement) {
		Collection<String> imports = new ArrayList<String>();
		
		for( PropertyElement element : modelElement.getProperties() ) {
			if( element.getName().equalsIgnoreCase(ModelElement.IMPORTS) ) {
				imports.add(element.getValue());
			}
		}
		
		return imports;
	}

	private static VdbModelEntry getVdbModelEntry(ModelElement element, Vdb actualVDB) {
        for( VdbEntry modelEntry : actualVDB.getModelEntries()) {
            if( modelEntry.getPath().removeFileExtension().lastSegment().equalsIgnoreCase(element.getName()) ) {
                return (VdbModelEntry)modelEntry;
            }
        }

        return null;
    }

	/*
     * Simple method to return a project relative path to a project folder or sub-folder based on the model path
     * defined in another project (or same project)
     *
     * Method removes the "project" segment from the input path and appends the rest to the target project
     */
    private static IPath getProjectRelativeModelPath(final String modelPathInVdb, final IProject targetProject) {
        IPath vdbModelPath = new Path(modelPathInVdb);

        IPath targetPath = new Path(StringConstants.EMPTY_STRING);
        int iSegs = vdbModelPath.segmentCount();
        if( iSegs > 1 ) {
            for( int i=1; i<iSegs; i++ ) {
                targetPath = targetPath.append(vdbModelPath.segment(i));
            }
        }

        return targetPath;
    }

	/**
     *
     * @param zipFile
     * @param zipEntryFullPath
     * @param projectRelativeTargetFolder
     * @return true if successful, false if not
	 * @throws Exception
     */
    private static boolean extractFileFromVdbToSameProject(final IFile zipFile, final String zipEntryFullPath, IPath projectRelativeTargetFolder)
                                                                                                                throws Exception {
        ZipInputStream zin = null;
        boolean result = false;

        try {
            String zipFilePath = ModelUtil.getLocation(zipFile).toOSString();
            String projectFilePath = ModelUtil.getLocation(ModelerCore.getWorkspace().getRoot()) + zipFile.getProject().getFullPath().toOSString();

            FileInputStream fin = new FileInputStream(zipFilePath);
            BufferedInputStream bin = new BufferedInputStream(fin);
            zin = new ZipInputStream(bin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                String entryName = '/' + ze.getName();
                if (entryName.equals(zipEntryFullPath)) {
                    String finalModelPath = projectFilePath + '/' + projectRelativeTargetFolder;
                    File entryFile = new File(finalModelPath);
                    if( !entryFile.getParentFile().exists() ) {
                        entryFile.getParentFile().mkdir();
                    }

                    FileOutputStream outstream = new FileOutputStream(finalModelPath);
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zin.read(buffer)) != -1) {
                        outstream.write(buffer, 0, len);
                    }

                    outstream.close();
                    zin.closeEntry();
                    result = true;
                    break;
                }
            }
        } finally {
            if( zin != null ) {
                try {
                    zin.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        zipFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        return result;
    }

	/**
     * @param theVdb the VDB
     *
	 * @return status of synchronize
	 * @throws Exception
     */
    public static boolean synchronizeWorkspace(final IFile theVdb) throws Exception {
        boolean result = false;
        if (! theVdb.exists())
            return result;

        IProject theProject = theVdb.getProject();
        VdbElement manifest = VdbUtil.getVdbManifest(theVdb);
        if (manifest == null)
            return result;

        for (ModelElement model : manifest.getModels()) {
            String modelUuid = VdbUtil.getUuid(model);
            IResource resource = null;

            if (modelUuid != null) {
                resource = WorkspaceResourceFinderUtil.findIResourceByUUID(modelUuid);
            } else {
                // Find my model name
                Collection<IFile> resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(model.getName()
                                                                                                 + StringConstants.DOT_XMI,
                                                                                                 theProject);
                if (resources.size() == 1) {
                    resource = resources.iterator().next();
                }
            }

            // Check if resource is found or not.
            if (resource == null) {
                // Note that we have a View model that is in a VDB and it's source is removed (VDB.removeEntry())
                // then that action will remove the View Model as well.
                // We need to prevent this OR need to tell the user we can't do anything about this and maybe
                // bail from this method.

                // Construct model path
                IPath targetPath = getProjectRelativeModelPath(model.getPath(), theVdb.getProject());
                //extractModelFromVdb(theVdb, model, targetPath);
                extractFileFromVdbToSameProject(theVdb, model.getPath(), targetPath);

                result = true;
            }
        }
        
        for (EntryElement nextRes : manifest.getEntries()) {

            // Find my resource (schema? jar?)
        	IPath path = new Path(nextRes.getPath());
            IFile resource = WorkspaceResourceFinderUtil.findIResourceByPath(path);


            // Check if resource is found or not.
            if (resource == null) {
                // Note that we have a View model that is in a VDB and it's source is removed (VDB.removeEntry())
                // then that action will remove the View Model as well.
                // We need to prevent this OR need to tell the user we can't do anything about this and maybe
                // bail from this method.

                // Construct model path
                IPath targetPath = getProjectRelativeModelPath(nextRes.getPath(), theVdb.getProject());
                //extractModelFromVdb(theVdb, model, targetPath);
                extractFileFromVdbToSameProject(theVdb, nextRes.getPath(), targetPath);

                result = true;
            }
        }

        return result;
    }

	/**
     * @param theVdb the VDB
     * @param extractMissingModels
	 * @param updateValidationVersion
	 *
	 * @throws Exception
     */
    public static void synchronizeVdb(final IFile theVdb, boolean extractMissingModels, boolean updateValidationVersion) 
                                                                                                                                                            throws Exception {
        if (! theVdb.exists())
            return;

        IProject theProject = theVdb.getProject();
        VdbElement manifest = VdbUtil.getVdbManifest(theVdb);
        if (manifest == null)
            return;

        if (extractMissingModels) {
            synchronizeWorkspace(theVdb);
        }

        Vdb actualVDB = new XmiVdb(theVdb, true);

        Set<ModelElement> modelsToReplace = new HashSet<ModelElement>();
        Collection<IResource> matchingResources = new ArrayList<IResource>();
        Set<ModelElement> modelsNotInWorkspace = new HashSet<ModelElement>();
        Map<String, IResource> oldModelPathToResourceMap = new HashMap<String, IResource>();

        Set<String> dependentViewModelPaths = new HashSet<String>();

        for (ModelElement model : manifest.getModels()) {
            Collection<String> modelImports = VdbUtil.getModelImports(model);
            for (String importedModelPath : modelImports) {
                for (ModelElement model_2 : manifest.getModels()) {
                    if (model_2.getPath().equals(importedModelPath)) {
                        dependentViewModelPaths.add(model.getPath());
                        break;
                    }
                }
            }
        }

        for (ModelElement model : manifest.getModels()) {
            String modelName = model.getName();
            String modelUuid = VdbUtil.getUuid(model);

            IResource resource = null;

            boolean addTheUuid = (modelUuid == null);
            boolean nameWasChanged = false;

            if (!addTheUuid) {
                resource = WorkspaceResourceFinderUtil.findIResourceByUUID(modelUuid);
                if (resource != null) {
                    nameWasChanged = !resource.getFullPath().removeFileExtension().lastSegment().equalsIgnoreCase(modelName);
                }
            } else {
                // Find my model name
                IPath modelPath = new Path(model.getPath());
                Collection<IFile> resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(model.getName()
                                                                                                 + CoreStringUtil.Constants.DOT + modelPath.getFileExtension(),
                                                                                                 theProject);
                if (resources.size() == 1) {
                    resource = resources.iterator().next();
                }
            }

            // Check if resource is found or not.
            if (resource != null) {
                String path = model.getPath();

                // Check IPath
                IPath iPath = new Path(path);
                IResource expectedResourceAtPath = ModelerCore.getWorkspace().getRoot().findMember(iPath);

                boolean fixThePath = (expectedResourceAtPath == null);

                oldModelPathToResourceMap.put(model.getPath(), resource);

                if (fixThePath || nameWasChanged || addTheUuid) {
                    for (VdbEntry modelEntry : actualVDB.getModelEntries()) {
                        if (modelEntry.getPath().removeFileExtension().lastSegment().equalsIgnoreCase(modelName)) {
                            modelsToReplace.add(model);
                            matchingResources.add(resource);
                            break;
                        }
                    }

                }
            } else {
                // Note that we have a View model that is in a VDB and it's source is removed (VDB.removeEntry())
                // then that action will remove the View Model as well.
                // We need to prevent this OR need to tell the user we can't do anything about this and maybe
                // bail from this method.
                modelsNotInWorkspace.add(model);
                // Construct model path
                //                      IPath targetPath = getProjectRelativeModelPath(model.getPath(), theVdb.getProject());
                //                      //extractModelFromVdb(theVdb, model, targetPath);
                //                      VdbUiUtil.extractFileFromVdbToSameProject(theVdb, model.getPath(), targetPath);
            }
        }

        // Check to see that any dependent View models are in the modelsNotInWorkspace list
        if (!modelsNotInWorkspace.isEmpty()) {

            // check if any are "dependent" (i.e. view models)
            for (ModelElement missingModel : modelsNotInWorkspace) {
                if (dependentViewModelPaths.contains(missingModel.getPath())) {
                    // TODO: Throw up dialog saying can't synchronize because view model does not exist in workspace
                    return;
                }
            }
        }

        // Loop through the changed models and remove/add them back as model entries
        Collection<String> modelPathsToReplace = new ArrayList<String>();

        for (ModelElement element : modelsToReplace) {
            VdbModelEntry modelEntry = getVdbModelEntry(element, actualVDB);
            if (modelEntry != null) {
                modelPathsToReplace.add(element.getPath());
                actualVDB.removeEntry(modelEntry);
            }
        }

        // Then loop through dependentViewModels list and add back any that are NOT in the VDB yet. These will be the
        // View model VdbModelEntry() instances that are auto-removed when "source" models are removed by VdbModelEntry.dispose() method.
        for (String thePath : modelPathsToReplace) {
            IResource matchingResource = oldModelPathToResourceMap.get(thePath);
            if (matchingResource != null) {
                actualVDB.addEntry(matchingResource.getFullPath());
            }
        }

        for (String thePath : dependentViewModelPaths) {
            IResource matchingResource = oldModelPathToResourceMap.get(thePath);
            if (matchingResource != null) {
                actualVDB.addEntry(matchingResource.getFullPath());
            }
        }

        actualVDB.synchronize();
        
        if( updateValidationVersion ) {
	        actualVDB.setValidationDateTime(new Date());
	        actualVDB.setValidationVersion(ModelerCore.getTeiidServerVersion().toString());
        }

        actualVDB.save();
        theVdb.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
    }
    
    /**
     * @param theVdb
     * @param targetName
     * @return the set of data role names for a VDB
     */
    public static Set<String> getDataRoleNames(Vdb theVdb, String targetName) {
    	Set<String> names = new HashSet<String>();
    	Collection<DataRole> entries = theVdb.getDataRoles();
    	for( DataRole role : entries) {
    		names.add(role.getName());
    	}
    	
    	if( targetName != null && !targetName.isEmpty() ) {
    		names.remove(targetName);
    	}
    	
    	return names;
    }
}
