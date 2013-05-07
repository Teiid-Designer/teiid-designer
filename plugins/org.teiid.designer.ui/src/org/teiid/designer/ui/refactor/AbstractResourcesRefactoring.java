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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.teiid.designer.ui.common.util.KeyInValueHashMap;
import org.teiid.designer.ui.common.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.teiid.designer.vdb.refactor.VdbResourceChange;

/**
 *
 */
public abstract class AbstractResourcesRefactoring extends Refactoring {

    private final List<IResource> resources;

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
     * Provides a specific set of tests to test the given resource to ensure
     * that the operation can continue. This can then be used in both the
     * implementation of createInitialConditions and when testing dependencies.
     *
     * @param resource
     * @param status
     */
    protected abstract void checkResource(IResource resource, IProgressMonitor progressMonitor, RefactoringStatus status);
    
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