/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.refactor.RefactorModelExtensionManager;
import org.teiid.designer.core.refactor.RelatedResourceFinder;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.common.util.UiUtil;

/**
 *
 */
public abstract class AbstractResourcesRefactoring extends Refactoring {

    private final List<IResource> resources;

    private IWorkbenchWindow workbenchWindow;

    private Map<IResource, Collection<Change>> changes = new LinkedHashMap<IResource, Collection<Change>>();

    private String name;

    /**
     * Create new instance
     *
     * @param name
     * @param resources
     */
    public AbstractResourcesRefactoring(String name, List<IResource> resources) {
        super();
        this.name = name;
        this.resources = resources;
    }

    /**
     * @param window
     */
    public void setWorkbenchWindow(IWorkbenchWindow window) {
        this.workbenchWindow = window;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the resources
     *
     * @return the resources
     */
    public List<IResource> getResources() {
        return resources;
    }

    /**
     * Get the workbench window
     *
     * @return window
     */
    public IWorkbenchWindow getWorkbenchWindow() {
        return this.workbenchWindow;
    }

    protected void addChange(IResource resource, Change change) {
        Collection<Change> collection = changes.get(resource);
        if (collection == null) {
            collection = new ArrayList<Change>();
            changes.put(resource, collection);
        }

        collection.add(change);
    }

    protected Collection<Change> getChanges() {
        List<Change> changeCollection = new ArrayList<Change>();
        for (Map.Entry<IResource, Collection<Change>> entry : changes.entrySet()) {
            changeCollection.addAll(entry.getValue());
        }
        return changeCollection;
    }

    protected void clearChanges() {
        changes.clear();
        vdbChanges.clear();
    }

    protected void checkRelatedResources(RefactoringStatus status) {
        for (IResource resource : resources) {
            try {
                ModelResource modelResource = ModelUtil.getModel(resource);

                if (modelResource != null && modelResource.isReadOnly()) {
                    status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyResourceError", resource.getName()))); //$NON-NLS-1$
                    return;
                }

                RelatedResourceFinder finder = new RelatedResourceFinder(resource);
                Collection<IFile> relatedFiles = finder.findRelatedResources(Relationship.ALL);

                for (IFile relatedFile : relatedFiles) {
                    try {
                        modelResource = ModelUtil.getModel(relatedFile);
                        if (modelResource != null && modelResource.isReadOnly()) {
                            status.merge(RefactoringStatus.createWarningStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyRelatedResourceError", modelResource.getItemName()))); //$NON-NLS-1$
                        }
                    } catch (ModelWorkspaceException err) {
                        ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                    }
                }
            } catch (Exception err) {
                ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                status.merge(RefactoringStatus.createErrorStatus(err.getMessage()));
                return;
            }
        }
    }

    protected void checkResourcesNotEmpty(RefactoringStatus status) {
        if (getResources() == null || getResources().isEmpty()) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.noResourceError"))); //$NON-NLS-1$
        }
    }

    protected void closeDirtyEditors(RefactoringStatus status) {
        if (UiUtil.saveDirtyEditors(getWorkbenchWindow(), null, true)) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.saveEditorsError"))); //$NON-NLS-1$
        }
    }

    protected boolean checkResourceExists(IResource resource, RefactoringStatus status) {
        if (!resource.exists()) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.resourceNoExistError", resource.getName()))); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    protected boolean checkResourceReadOnly(IResource resource, RefactoringStatus status) {
        if (ModelUtil.isIResourceReadOnly(resource)) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyResourceError", resource.getName()))); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    protected boolean checkForProject(IResource resource, RefactoringStatus status) {
        if (resource instanceof IProject) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.refactorProjectError", resource.getName()))); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    protected boolean checkExtensionManager(IResource resource, RefactorType refactorType, IProgressMonitor progressMonitor, RefactoringStatus status) {
        if (! RefactorModelExtensionManager.preProcess(refactorType, resource, progressMonitor)) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.extensionManagerError"))); //$NON-NLS-1$
            return false;
        }

        return true;
    }
}