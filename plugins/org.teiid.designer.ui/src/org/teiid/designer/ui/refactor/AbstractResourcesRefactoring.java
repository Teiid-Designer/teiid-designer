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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.KeyInValueHashMap;
import org.teiid.designer.core.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils.AbstractResourceCallback;
import org.teiid.designer.vdb.refactor.VdbResourceChange;

/**
 *
 */
public abstract class AbstractResourcesRefactoring extends Refactoring {

    private final List<IResource> resources;

    private Set<IResource> resourcesAndChildren;

    private Map<IResource, Collection<Change>> changes = new LinkedHashMap<IResource, Collection<Change>>();

    private final KeyInValueHashMap<String, VdbResourceChange> vdbChanges;
    
    private String name;

    private class VdbResourceChangeAdapter implements KeyFromValueAdapter<String, VdbResourceChange> {

        @Override
        public String getKey(VdbResourceChange value) {
            return value.getParentFolder() + value.getVdbName();
        }
    }

    /**
     * Callback implementation for adding vdb resource changes
     */
    public class VdbResourceCallback extends AbstractResourceCallback {
        @Override
        public void indexVdb(IResource resource, IFile vdbFile, RefactoringStatus status) {
            addChange(vdbFile, new VdbResourceChange(vdbFile));
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
        this.vdbChanges = new KeyInValueHashMap<String, VdbResourceChange>(new VdbResourceChangeAdapter());
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
     * Returns all the resources as well as their children
     *
     * @param status to be populate if there are any errors with collecting the resources
     *
     * @return the resourcesAndChildren
     */
    public Set<IResource> getResourcesAndChildren(RefactoringStatus status) {
        if (this.resourcesAndChildren == null) {
            this.resourcesAndChildren = new HashSet<IResource>();

            for (IResource resource : resources) {
                try {
                    resource.accept(new IResourceVisitor() {
                        @Override
                        public boolean visit(IResource visitedResource) {
                            resourcesAndChildren.add(visitedResource);
                            return true;
                        }
                    }, IResource.DEPTH_INFINITE, false);
                } catch (Exception err) {
                    ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                    status.merge(RefactoringStatus.createFatalErrorStatus(err.getMessage()));
                }
            }
        }

        return this.resourcesAndChildren;
    }

    /**
     * Provides a specific set of tests to test the given resource to ensure
     * that the operation can continue. This can then be used in both the
     * implementation of createInitialConditions and when testing dependencies.
     *
     * @param resource
     * @param status
     */
    protected abstract void checkResource(IResource resource, IProgressMonitor progressMonitor, RefactoringStatus status);
    
    protected void addVdbChange(VdbResourceChange change) {
        VdbResourceChange mappedChange = vdbChanges.get(change.getParentFolder() + change.getVdbName());
        if (mappedChange == null) {
            vdbChanges.add(change);
            return;
        }

        mappedChange.addReplacements(change.getReplacedResources());
    }
    
    protected void addVdbChange(IFile vdbFile, IPath invalidResourcePath, IPath newResourcePath) {
        VdbResourceChange change = vdbChanges.get(vdbFile.getParent().getName() + vdbFile.getName());
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

    /**
     * Add a text change to the refactoring for the given file.
     *
     * @param pathpairs the pairs of paths being modified as a result of the move
     * @param file resource being analysed
     *
     * @return true if the text change was added, false otherwise.
     *
     * @throws Exception
     */
    protected boolean addTextChange(IFile file, TextFileChange textFileChange) {
        if (textFileChange == null)
            return false;

        if (textFileChange.getEdit() == null || !textFileChange.getEdit().hasChildren())
            return false;

        // Only if the file is actually being changed do we add the text change
        addChange(file, textFileChange);
        return true;
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

    /**
     * Check wth the resources is populated at all.
     *
     * Populate the given status accordingly.
     *
     * @param status
     * @return true if the resources collection is populated, otherwise false.
     */
    protected boolean checkResourcesNotEmpty(RefactoringStatus status) {
        if (getResources() == null || getResources().isEmpty()) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.noResourceError"))); //$NON-NLS-1$
            return false;
        }

        return true;
    }
}