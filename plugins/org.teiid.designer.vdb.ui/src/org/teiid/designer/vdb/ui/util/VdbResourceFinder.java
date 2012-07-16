/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.TeiidException;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.common.xsd.XsdHeader;
import org.teiid.designer.common.xsd.XsdHeaderReader;
import org.teiid.designer.core.ModelEditorImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ContainerImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.core.xmi.ModelImportInfo;
import org.teiid.designer.core.xmi.XMIHeader;
import org.teiid.designer.core.xmi.XMIHeaderReader;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.vdb.Vdb;

/**
 * This class provides utilities to find resources given in input VDB file
 */
public class VdbResourceFinder {
	@SuppressWarnings("javadoc")
	protected static final Resource[] EMPTY_RESOURCE_ARRAY = new Resource[0];
	
    private ContainerImpl container;
	private Vdb vdb;
    private IFile vdbFile;
    
    private Resource[] vdbModelFiles;

	/**
	 * @param vdbFile the vdbFile
	 * @throws CoreException 
	 */
	public VdbResourceFinder(IFile vdbFile) throws CoreException {
		this.vdbFile = vdbFile;
		
		initialize();
	}
	
	private void initialize() throws CoreException {
        this.container = (ContainerImpl)ModelerCore.createContainer("tempVdbModelContainer"); //$NON-NLS-1$
        ModelEditorImpl.setContainer(this.container);
        
        this.vdb = new Vdb(vdbFile, false, new NullProgressMonitor());
        
    	Collection<Resource> theFiles = new ArrayList<Resource>();
    	
        Collection<File> modelFiles = getVdb().getModelFiles();

        for (File modelFile : modelFiles) {
            Resource res = this.container.getResource(URI.createFileURI(modelFile.getPath()), true);
            theFiles.add(res);
        }
        
        this.vdbModelFiles = theFiles.toArray(new Resource[0]);
	}
	
	/**
	 * This method needs to be called in order to clean up/restore the ModelEditor's container
	 */
	public void dispose() {
		this.vdb = null;
		this.container = null;
		ModelEditorImpl.setContainer(null);
	}
	
    /**
     * @return the temporary Vdb Model container
     */
    public ContainerImpl getContainer() {
		return this.container;
	}

	/**
	 * @return the Vdb
	 */
	public Vdb getVdb() {
		return this.vdb;
	}

	/**
	 * @return the vdbFile
	 */
	public IFile getVdbFile() {
		return this.vdbFile;
	}
	
	/**
	 * @return collection of Resource's
	 */
    public Collection<Resource> getWebServiceResources() {
    	
    	Collection<Resource> webServiceModels = new ArrayList<Resource>();
        try {
            Collection<File> modelFiles = getVdb().getModelFiles();

            for (File modelFile : modelFiles) {
                boolean isVisible = true;

                Resource res = this.container.getResource(URI.createFileURI(modelFile.getPath()), true);
                if (isVisible && ModelUtil.isModelFile(res) && !ModelUtil.isXsdFile(res)) {
                    EObject firstEObj = res.getContents().get(0);
                    ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                    String mmURI = ma.getPrimaryMetamodelUri();
                    if (ModelUtil.URI_WEB_SERVICES_VIEW_MODEL.equalsIgnoreCase(mmURI)) {
                    	webServiceModels.add(res);
                    }
                }
            }
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return webServiceModels;
    }
    
    
    /**
     * @param resource
     * @return Resource array
     */
    public Resource[] getDependentResources( final Resource resource ) {
        if (resource == null || getWorkspace() == null) return EMPTY_RESOURCE_ARRAY;
        
        File theFile = new File(resource.getURI().toFileString());
        
        final Collection<Resource> result = new ArrayList<Resource>();
        try {

            // Get the header information from the XSD file
            if (ModelUtil.isXsdFile(resource)) {
                final XsdHeader header = XsdHeaderReader.readHeader(theFile);
                if (header != null) {

                    // Add all the imported schema locations
                    String[] locations = header.getImportSchemaLocations();
                    for (int i = 0; i != locations.length; ++i) {
                        final String location = locations[i];

                        // TODO: Need to convert the following call to work with "Resource" instead of "IResource" 
                        Resource dependentResource = findResource(location);
                        if (dependentResource == null) {
                            final String absolutePath = WorkspaceResourceFinderUtil.getAbsoluteLocation(theFile, location);
                            dependentResource = findResource(absolutePath);
                        }
                        if (dependentResource != null && !result.contains(dependentResource)) result.add(dependentResource);
                    }

                    // Add all the included schema locations
                    locations = header.getIncludeSchemaLocations();
                    for (int i = 0; i != locations.length; ++i) {
                        final String location = locations[i];
                     // TODO: Need to convert the following call to work with "Resource" instead of "IResource" 
                        Resource dependentResource = findResource(location);
                        if (dependentResource == null) {
                            final String absolutePath = WorkspaceResourceFinderUtil.getAbsoluteLocation(theFile, location);
                            dependentResource = findResource(absolutePath);
                        }
                        if (dependentResource != null && !result.contains(dependentResource)) result.add(dependentResource);
                    }
                }

                // Get the header information from the XMI file
            } else if (ModelUtil.isModelFile(resource)) {
                final XMIHeader header = XMIHeaderReader.readHeader(theFile);
                if (header != null) {

                	String fullFilePath = resource.getURI().path();
                	IPath fileLocationPath = new Path(fullFilePath).removeLastSegments(1);
                	String fileLocation = fileLocationPath.toOSString();
                	
                    final ModelImportInfo[] infos = header.getModelImportInfos();
                    for (final ModelImportInfo info : infos) {
                        Resource dependentResource = null;

                        final String location = info.getLocation();
                        if( !location.startsWith("http:") ) { //$NON-NLS-1$
	                        final String path = new Path(fileLocation).append(location).toOSString() ;
	                     // TODO: Need to convert the following call to work with "Resource" instead of "IResource" 
	                        if (!CoreStringUtil.isEmpty(path)) {
	                        	dependentResource = findResource(path);
	                        } else if (!CoreStringUtil.isEmpty(location)) {
	                            final String depPath = fileLocation;
	                            if (!WorkspaceResourceFinderUtil.isGlobalResource(depPath)) {
	                                dependentResource = findResource(depPath);
	                                if (dependentResource == null) {
	                                    final String absolutePath = WorkspaceResourceFinderUtil.getAbsoluteLocation(theFile, location);
	                                    dependentResource = findResource(absolutePath);
	                                }
	                            }
	                        }
	                        if (dependentResource != null && !result.contains(dependentResource)) {
	                        	result.add(dependentResource);
	                        }
                        }
                    }
                }

            }

        } catch (final Exception err) {
            final Object[] params = new Object[] {resource.getURI()};
            final String msg = ModelerCore.Util.getString("WorkspaceResourceFinderUtil.Error_getting_model_imports_from_resource", params); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR, err, msg);
        }

        return result.toArray(new Resource[result.size()]);
    }
    
    /**
     * Returns the workbench associated with this object.
     * @return the workspace
     */
    private IWorkspace getWorkspace() {
        return ModelerCore.getWorkspace();
    }
    
    
    /**
     * @param resource
     * @return the resource
     */
    public Resource findResource( final Resource resource ) {

        try {
            if (resource != null && resource.getURI() != null && getWorkspace() != null) return findResource(resource.getURI().toString());
        } catch (final IllegalStateException ise) {
            // do nothing
        }

        return null;
    }
    
    
    /**
     * Return the Resource instance corresponding to the specified URI string. The URI represents a relative path within the
     * workspace to particular file resource. If the URI is one of the well-known Teiid Designer/EMF identifiers to a global
     * resource such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * then null is returned since there is no Resource in the workspace that represents any one of those models.
     * 
     * @param workspaceUri the URI string
     * @return the IResource identified by the URI if it exists; may return null
     */
    public Resource findResource( final String workspaceUri ) {

        if (!WorkspaceResourceFinderUtil.isValidWorkspaceUri(workspaceUri)) return null;
        final String normalizedUriString = WorkspaceResourceFinderUtil.normalizeUriString(workspaceUri);
        
        // Check existing vdb models
        File modelFile = new File(normalizedUriString);
        if( modelFile.exists() ) {
        	return getExistingVdbResource(modelFile);
        }

        // MyDefect : 16368 Refactored methods.

        Resource fileResource = null;
        final Resource[] fileResources = this.vdbModelFiles;

        // If the workspace URI starts with "http" then check it against the target
        // namespaces of any XML schema in the workspace ...
        fileResource = getResourceStartsWithHttp(fileResources, normalizedUriString);
        if (fileResource != null) return fileResource;

        // Try to convert the workspace URI to a relative path and then match
        // this path in workspace to one of the IResource paths instances
        fileResource = getResourceStartsWithPathSeparator(fileResources, normalizedUriString);
        if (fileResource != null) return fileResource;

        // Try to match the workspace URI to a IResource location ...
        fileResource = getResourceByLocation(fileResources, normalizedUriString);
        if (fileResource != null) return fileResource;

        return null;
    }
    
    private Resource getExistingVdbResource(File file) {
    	for( Resource modelFile : this.vdbModelFiles) {
    	
    		String vdbFileStr = WorkspaceResourceFinderUtil.normalizeUriString(modelFile.getURI().toFileString());
    		
    		//This will remove the drive from the file string on Windows
    		String prefix = vdbFileStr.substring(0, vdbFileStr.indexOf("\\")>-1?vdbFileStr.indexOf("\\"):0);  //$NON-NLS-1$ //$NON-NLS-2$
    		if (prefix.length() > 0 && prefix.contains(":")){ //$NON-NLS-1$
    			vdbFileStr = vdbFileStr.replace(prefix, ""); //$NON-NLS-1$
    		}
    		
    		String fileStr = file.getPath();
    		if( vdbFileStr.equals(fileStr) ) {
    			return modelFile;
    		}
    	}
    	
    	return null;
    }
    
	/**
	 * @param resources
	 * @param dependentSchemas
	 */
	public void getAllDependentSchemas( Resource[] resources, ArrayList<Resource> dependentSchemas) {

		// Add discovered dependent schemas
		for (Resource resource : resources) {
			if (ModelUtil.isXsdFile(resource) && shouldAddSchema(resource, dependentSchemas)) {
				dependentSchemas.add(resource);
			}
		}

		// Now iterate through the dependent schemas and find their dependent
		// resources, if any
		for (Resource resource : resources) {
			Resource[] moreResources = getDependentResources(resource);
			if (moreResources.length > 0) {
				getAllDependentSchemas(moreResources, dependentSchemas);
			}
		}

	}
	
	private boolean shouldAddSchema(Resource resource, ArrayList<Resource> dependentSchemas) {
		for( Resource schemaResource : dependentSchemas ) {
			if( resource.getURI().toFileString().equalsIgnoreCase(schemaResource.getURI().toFileString()) ) {
				return false;
			}
		}
		
		return true;
	}
	
	private Resource getResourceStartsWithHttp(final Resource[] fileResources,
			final String workspaceUri) {

		File file = null;
		String targetNamespace;

		if (workspaceUri.startsWith("http")) { //$NON-NLS-1$
			for (final Resource fileResource2 : fileResources) { 
				File modelFile = new File(fileResource2.getURI().toFileString());
				file = modelFile;
				targetNamespace = getXsdTargetNamespace(file);
				if (workspaceUri.equals(targetNamespace))
					return fileResource2;
			}
		}

		return null;
	}
    
    private String getXsdTargetNamespace( final File file ) {
        if (ModelUtil.isXsdFile(file)) {
            if (file.exists()) try {
                final XsdHeader header = XsdHeaderReader.readHeader(file);
                if (header != null) return header.getTargetNamespaceURI();
            } catch (final TeiidException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        return null;
    }
    
	private static Resource getResourceStartsWithPathSeparator( final Resource[] fileResources, final String workspaceUri) {

		Resource fileResource = null;
		IPath pathInWorkspace;

		if (workspaceUri.charAt(0) == IPath.SEPARATOR) {
			pathInWorkspace = new Path(workspaceUri);
			
			URI pathInWorkspaceURI = URI.createURI(pathInWorkspace.toString());
			for (final Resource fileResource2 : fileResources) {
				fileResource = fileResource2;

				if (fileResource != null && fileResource.getURI().equals(pathInWorkspaceURI))
					return fileResource;
			}
		}

		return null;
	}
	
	private static Resource getResourceByLocation(final Resource[] fileResources,
			final String workspaceUri) {

		Resource fileResource = null;
		String resourceLocation;

		// Try to match the workspace URI to a Resource URI ...
		for (final Resource fileResource2 : fileResources) {
			fileResource = fileResource2;
			resourceLocation = fileResource.getURI().toString();
			if (workspaceUri.endsWith(resourceLocation))
				return fileResource;
		}

		// Case 5683 - look for a match of the supplied workspaceUri (usually
		// file.ext) to the
		// last segment of the fileResource path.
		for (final Resource fileResource2 : fileResources) {
			fileResource = fileResource2;
			final String fileNameSegment = fileResource.getURI().lastSegment();
			if (fileNameSegment != null && fileNameSegment.equalsIgnoreCase(workspaceUri))
				return fileResource;
		}

		// MyDefect : 16368 Added to fix the defect
		for (final Resource fileResource2 : fileResources) {
			fileResource = fileResource2;
			final IPath resrcLocation = new Path(fileResource.getURI().toFileString());
			resourceLocation = resrcLocation.toOSString();
			if (resourceLocation.endsWith(workspaceUri))
				return fileResource;
			resourceLocation = resrcLocation.toString();
			if (resourceLocation.endsWith(workspaceUri))
				return fileResource;
		}

		return null;
	}
}
