package org.teiid.designer.ui.refactor.rename;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.internal.core.refactoring.BasicElementLabels;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;

/**
 * Descriptor for storing the resource move change
 */
public class RenameResourceDescriptor extends RefactoringDescriptor {

    static final String RESOURCE_PATH_KEY = "ResourcePathKey"; //$NON-NLS-1$
    
    static final String RENAME_PATH_KEY = "RenameKey"; //$NON-NLS-1$

	static final String REFACTORING_ID = RenameResourceDescriptor.class.getPackage().getName();

	private static final String UNAMED_DESCRIPTOR = "Unamed_Descriptor"; //$NON-NLS-1$
	
	/** The new name for the resource */
    private String newName;

    /** The resource to rename */
    private IPath resourcePath;

	/**
	 * Create new default instance
	 */
	public RenameResourceDescriptor() {
	    this(null, UNAMED_DESCRIPTOR, null);
    }
	
	/**
	 * Create new instance
	 * 
	 * @param project
	 * @param description
	 * @param comment
	 */
	public RenameResourceDescriptor(String project, String description, String comment) {
		super(REFACTORING_ID, project, description, comment, RefactoringDescriptor.STRUCTURAL_CHANGE | RefactoringDescriptor.MULTI_CHANGE);
	}

	/**
     * @return the new name
     */
    public String getNewName() {
        return newName;
    }
    
    /**
     * @param newName
     */
	public void setNewName(String newName) {
        CoreArgCheck.isNotNull(newName);
        this.newName = newName;
    }
	
	/**
     * @return the resourcePath
     */
    public IPath getResourcePath() {
        return this.resourcePath;
    }

    /**
     * @param resourcePath
     */
	public void setResourcePathToRename(IPath resourcePath) {
	    this.resourcePath = resourcePath;
	}

	/**
	 * @param resource
	 */
    public void setResourceToRename(IResource resource) {
        CoreArgCheck.isNotNull(resource);
        this.resourcePath = resource.getFullPath();
    }

	@Override
	public Refactoring createRefactoring(RefactoringStatus status) throws CoreException {
	    try {
            IPath resourcePath = getResourcePath();
            if (resourcePath == null) {
                status.addFatalError(RefactorResourcesUtils.getString("RenameRefactoring.errorPathNotSet")); //$NON-NLS-1$
                return null;
            }

            IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(resourcePath);
            if (resource == null || !resource.exists()) {
                status.addFatalError(RefactorResourcesUtils.getString("RenameRefactoring.errorResourceNotExisting", BasicElementLabels.getPathLabel(resourcePath, false))); //$NON-NLS-1$
                return null;
            }

            String newName = getNewName();
            if (newName == null || newName.length() == 0) {
                status.addFatalError(RefactorResourcesUtils.getString("RenameRefactoring.errorNameNotDefined")); //$NON-NLS-1$
                return null;
            }

            RenameResourceRefactoring refactoring = new RenameResourceRefactoring(resource);
            refactoring.setNewResourceName(newName);

            return refactoring;

        } catch (Exception ex) {
            status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
            throw new CoreException(new Status(IStatus.ERROR, REFACTORING_ID, ex.getMessage()));
        }
	}
}