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
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.refactor.RefactorModelExtensionManager;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.common.util.KeyInValueHashMap;
import org.teiid.designer.ui.common.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.vdb.refactor.VdbResourceChange;

/**
 *
 */
public abstract class AbstractResourcesRefactoring extends Refactoring {

    private final List<IResource> resources;

    private IWorkbenchWindow workbenchWindow;

    private Map<IResource, Collection<Change>> changes = new LinkedHashMap<IResource, Collection<Change>>();

    private final KeyInValueHashMap<IFile, VdbResourceChange> vdbChanges;
    
    private String name;

    private class VdbResourceChangeAdapter implements KeyFromValueAdapter<IFile, VdbResourceChange> {

        @Override
        public IFile getKey(VdbResourceChange value) {
            return value.getVdb();
        }
    }
    
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
        this.vdbChanges = new KeyInValueHashMap<IFile, VdbResourceChange>(new VdbResourceChangeAdapter());
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
    
    protected void addVdbChange(VdbResourceChange change) {
        VdbResourceChange mappedChange = vdbChanges.get(change.getVdb());
        if (mappedChange == null) {
            vdbChanges.add(change);
            return;
        }

        mappedChange.addReplacements(change.getReplacedResources());
    }
    
    protected void addVdbChange(IFile vdbFile, IPath invalidResourcePath, IPath newResourcePath) {
        VdbResourceChange change = vdbChanges.get(vdbFile);
        if (change == null) {
            change = new VdbResourceChange(vdbFile);
        }
        
        change.addReplacement(invalidResourcePath.toOSString(), newResourcePath.toOSString());
        vdbChanges.add(change);
    }

    protected void addChange(IResource resource, Change change) {
        if (change instanceof VdbResourceChange) {
            addVdbChange((VdbResourceChange) change);
            return;
        }
        
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
        
        // Add vdb changes last since synchronisation should be done
        // after all other changes
        changeCollection.addAll(vdbChanges.values());
        
        return changeCollection;
    }

    protected void clearChanges() {
        changes.clear();
        vdbChanges.clear();
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

    private void checkDirtyFile(RefactoringStatus result, IFile file) {
        if (!file.exists())
            return;
        ITextFileBuffer buffer= FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
        if (buffer != null && buffer.isDirty()) {
            if (buffer.isStateValidated() && buffer.isSynchronized()) {
                result.addWarning(RefactorResourcesUtils.getString("ResourcesRefactoring.unsavedFile", file.getFullPath())); //$NON-NLS-1$
            } else {
                result.addFatalError(RefactorResourcesUtils.getString("ResourcesRefactoring.unsavedFile", file.getFullPath())); //$NON-NLS-1$
            }
        }
    }

    protected void checkDirtyResources(final RefactoringStatus status) {
        for (IResource resource : getResources()) {
            if (resource instanceof IProject && !((IProject) resource).isOpen())
                continue;

            try {
                resource.accept(new IResourceVisitor() {
                    @Override
                    public boolean visit(IResource visitedResource) {
                        if (visitedResource instanceof IFile) {
                            checkDirtyFile(status, (IFile)visitedResource);
                        }
                        return true;
                    }
                }, IResource.DEPTH_INFINITE, false);
            } catch (CoreException ex) {
                status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
            }
        }
    }
}