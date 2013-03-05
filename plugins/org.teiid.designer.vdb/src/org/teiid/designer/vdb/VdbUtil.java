/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
import org.teiid.core.designer.util.ChecksumUtil;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.OperationUtil.Unreliable;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.vdb.Vdb.Xml;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.xml.sax.SAXException;

/**
 * Utility methods used to query VDB manifest and VDB's
 *
 * @since 8.0
 */
public class VdbUtil {

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

    private static final String MANIFEST = "META-INF/vdb.xml"; //$NON-NLS-1$

    /**
     * @param theVdb
     * @return list of vdb model files
     */
    public static Collection<IFile> getVdbModels( Vdb theVdb ) {
        Collection<IFile> iFiles = new ArrayList<IFile>();

        for (VdbModelEntry modelEntry : theVdb.getModelEntries()) {
            // IPath modelPath = modelEntry.getName();
            IResource resource = ModelerCore.getWorkspace().getRoot().findMember(modelEntry.getName());

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
     */
    public static boolean isPreviewVdb( final IFile file ) {
        CoreArgCheck.isNotNull(file, "file is null"); //$NON-NLS-1$

        if (file.exists()) {
            // if VDB file is empty just check file name
            if (file.getLocation().toFile().length() == 0L) {
                // make sure file prefix and extension is right
                if (!Vdb.FILE_EXTENSION_NO_DOT.equals(file.getFileExtension())) {
                    return false;
                }

                return file.getName().startsWith(Vdb.PREVIEW_PREFIX);
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
     * Utility method to determine if a vdb contains models of a certain "class"
	 * @param file
	 * * @param modelClass
     * @param type
     * @return preview attribute value for VDB. true or false
     */
	public static boolean hasModelClass(final IFile file, final String modelClass, final String type) {
        if (file.exists() && Vdb.FILE_EXTENSION_NO_DOT.equals(file.getFileExtension())) {
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
     */
    public static VdbElement getVdbManifest( final IFile file ) {
        final VdbElement[] manifest = new VdbElement[1];

        if (!file.exists()) {
            return null;
        }

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

        return manifest[0];
    }

    /**
     * @param file
     * @return version the vdb version number
     */
    public static int getVdbVersion( final IFile file ) {

        if (file.exists()) {
            VdbElement manifest = VdbUtil.getVdbManifest(file);
            if (manifest != null) {
                return manifest.getVersion();
            }
        }

        return 0;
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

    static JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(new Class<?>[] {VdbElement.class});
    }

    static Schema getManifestSchema() throws SAXException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(VdbElement.class.getResource("/vdb-deployer.xsd")); //$NON-NLS-1$
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
	 */
	public static boolean modelInVdb(final IFile theVdb, final IFile theModelFile) {
		if (theVdb.exists()) {
			VdbElement manifest = VdbUtil.getVdbManifest(theVdb);
			if (manifest != null) {
				for (ModelElement model : manifest.getModels()) {
					String modelName = model.getName()+ ModelUtil.DOT_EXTENSION_XMI;
					if (modelName.equalsIgnoreCase(theModelFile.getName())) {
						// We found the model, now replace the path
						return true;
					}
				}
			}
		}
		return false;
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
			VdbElement manifest = VdbUtil.getVdbManifest(theVdbFile);
			Vdb theVdb = new Vdb(theVdbFile, new NullProgressMonitor());
			if (manifest != null) {
				for (ModelElement model : manifest.getModels()) {
					String modelName = model.getName();
					String modelUuid = getUuid(model);
					
					IResource resource = null;
					
					// Check if model with that name exists in project
					// first check if uuid == null
					if( modelUuid == null ) {
						
						IResource[] resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(modelName, theProject);
						if( resources.length == 1 ) {
							resource = resources[0];
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
							IResource[] resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(modelName, theProject);
							if( resources.length == 1 ) {
								resource = resources[0];
							}
						}
					}
					
					boolean nameChanged = false;
					
					if( resource == null ) {
						statuses.add(  new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID, 
								VdbPlugin.UTIL.getString("vdbValidationWarning_noModelInWorkspace", modelName)) ); //$NON-NLS-1$
					} else {
						// check same name
						String resourceName = resource.getFullPath().removeFileExtension().lastSegment();
						if( ! modelName.equals(resourceName) ) {
							nameChanged = true;
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
	 		for( VdbModelEntry modelEntry : theVdb.getModelEntries()) {
				if( modelEntry.getName().lastSegment().equalsIgnoreCase(theModelFile.getName()) ) {
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
	 */
	public static Collection<IFile> getModelsWithWrongPaths(final IFile theVdb) {
		Collection<IFile> misMatchedResources = new ArrayList<IFile>();
		
		if (theVdb.exists()) {
			IProject theProject = theVdb.getProject();

			VdbElement manifest = VdbUtil.getVdbManifest(theVdb);
			if (manifest != null) {
				for (ModelElement model : manifest.getModels()) {
					String modelName = model.getName()+ ModelUtil.DOT_EXTENSION_XMI;
					IResource[] resources = WorkspaceResourceFinderUtil.findIResourceInProjectByName(modelName, theProject);
					if( resources.length == 1 ) {
						String path = model.getPath();
						IResource matchingResource = resources[0];
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

}
