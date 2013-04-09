package org.teiid.designer.ui.refactor.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;

/**
 * Descriptor for storing the resource move change
 */
public class MoveResourcesDescriptor extends RefactoringDescriptor {

    static final String RESOURCE_PATH_KEY = "ResourcePathKey"; //$NON-NLS-1$
    
    static final String DESTINATION_PATH_KEY = "DestinationPathKey"; //$NON-NLS-1$

	static final String REFACTORING_ID = MoveResourcesDescriptor.class.getPackage().getName();

	private static final String UNAMED_DESCRIPTOR = "Unamed_Descriptor"; //$NON-NLS-1$
   
	/** The destination */
    private IPath destinationPath;

    /** The resources to move */
    private Collection<IPath> resourcePaths;

	/**
	 * Create new default instance
	 */
	public MoveResourcesDescriptor() {
	    this(null, UNAMED_DESCRIPTOR, null);
    }
	
	/**
	 * Create new instance
	 * 
	 * @param project
	 * @param description
	 * @param comment
	 */
	public MoveResourcesDescriptor(String project, String description, String comment) {
		super(REFACTORING_ID, project, description, comment, RefactoringDescriptor.STRUCTURAL_CHANGE | RefactoringDescriptor.MULTI_CHANGE);
		destinationPath = null;
        resourcePaths = Collections.emptyList();
	}

	/**
     * @return the destinationPath
     */
    public IPath getDestinationPath() {
        return this.destinationPath;
    }
    
	/**
	 * @param destination
	 */
	public void setDestinationPath(IPath destination) {
        CoreArgCheck.isNotNull(destination);
        destinationPath = destination;
    }
	
	/**
     * @return the resourcePaths
     */
    public Collection<IPath> getResourcePaths() {
        return this.resourcePaths;
    }
	
	/**
	 * @param resourcePaths
	 */
	public void setResourcePathsToMove(Collection<IPath> resourcePaths) {
	    this.resourcePaths = resourcePaths;
	}

    /**
     * @param resources
     */
    public void setResourcesToMove(Collection<IResource> resources) {
        CoreArgCheck.isNotNull(resources);
        
        resourcePaths = new ArrayList<IPath>();
        for (IResource resource : resources) {
            resourcePaths.add(resource.getFullPath());
        }
    }

	@Override
	public Refactoring createRefactoring(RefactoringStatus status) throws CoreException {
	    try {
	        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

	        IResource destination = root.findMember(destinationPath);
	        if (!(destination instanceof IFolder || destination instanceof IProject) || !destination.exists()) {
	            status.addFatalError(RefactorResourcesUtils.getString("MoveResourceDescriptor.destinationNotExistError", destinationPath)); //$NON-NLS-1$
	            return null;
	        }

	        List<IResource> resources = new ArrayList<IResource>();
	        
	        for (IPath resourcePath : resourcePaths) {
	            IResource resource = root.findMember(resourcePath);
	            if (resource == null || !resource.exists()) {
	                status.addFatalError(RefactorResourcesUtils.getString("MoveResourceDescriptor.resourceNoExistError", resourcePath)); //$NON-NLS-1$
	                return null;
	            }
	            
	            if (!(resource instanceof IFile || resource instanceof IFolder)) {
	                status.addFatalError(RefactorResourcesUtils.getString("MoveResourceDescriptor.resourceNotFileOrFolder", resourcePath)); //$NON-NLS-1$
	                return null;
	            }
	            
	            resources.add(resource);
	        }
	        
	        MoveResourcesRefactoring refactoring = new MoveResourcesRefactoring(resources);
	        refactoring.setDestination((IContainer) destination);
	        
	        return refactoring;
	        
	    } catch (Exception ex) {
	        status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
	        throw new CoreException(new Status(IStatus.ERROR, REFACTORING_ID, ex.getMessage()));
	    }
	}
}