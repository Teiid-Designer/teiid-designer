/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.refactor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResourceFilter;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;

/**
 * Find resources related to the given resource 
 */
public class RelatedResourceFinder {
    
    private static final ModelResourceFilter RESOURCE_FILTER = new ModelResourceFilter();
    
    public enum Relationship {
        /**
         * The relationship where resource A is imported by resource B
         * hence B is dependent on A
         */
        DEPENDENT,

        /**
         * The relationship where resource A imports resource B
         * hence A is dependent on B
         */
        DEPENDENCY,

        /**
         * Encompasses all types of relationship
         */
        ALL;
    }

    private final IResource sourceResource;
    
    public RelatedResourceFinder(IResource sourceResource) {
        this.sourceResource = sourceResource;
    }

    /**
     * Recursive dependent find method on the given resource
     * 
     * @param resource
     * @return Collection<IFile>
     */
    private Collection<IFile> findDependentResources(IResource resource) {

        Collection<IFile> dependentResources = Collections.emptyList();

        try {
            if (resource instanceof IContainer) {
                // sometimes this is getting called with a nonexistent resource...
                // see defect 18558 for more details
                if (resource.exists()) {
                    IContainer folder = (IContainer) resource;
                    IResource[] resources = folder.members();
                    dependentResources = new HashSet<IFile>();

                    for (int idx = 0; idx < resources.length; idx++) {
                        dependentResources.addAll(findDependentResources(resources[idx]));
                    }
                } // endif
            } else {
                dependentResources = WorkspaceResourceFinderUtil.getResourcesThatUse(resource, RESOURCE_FILTER, IResource.DEPTH_INFINITE);
            }
        } catch (CoreException ce) {
            ModelerCore.Util.log(ce);
        }

        return dependentResources;
    }
    
    /**
     * find the dependent resources of the source resource
     * 
     * @return Collection<IFile>
     */
    private Collection<IFile> findDependentResources() {
        return findDependentResources(sourceResource);
    }

    /**
     * Recursive dependency find method on the given resource
     * 
     * @param resource
     * @return Collection<IFile>
     */
    private Collection<IFile> findDependencyResources(IResource resource) {

        Collection<IFile> dependencyResources = new HashSet<IFile>();

        try {
            if (resource instanceof IContainer) {
                IContainer folder = (IContainer)resource;
                IResource[] resources = folder.members();

                for (int idx = 0; idx < resources.length; idx++) {
                    dependencyResources.addAll(findDependencyResources(resources[idx]));
                }

            } else {
                dependencyResources.addAll(WorkspaceResourceFinderUtil.getDependentResources(resource));
            }
        } catch (CoreException ce) {
            ModelerCore.Util.log(ce);
        }

        return dependencyResources;
    }

    /**
     * calculateDependencyResources
     * 
     * @return Collection
     * @since 4.3
     */
    private Collection<IFile> findDependencyResources() {
        return findDependencyResources(sourceResource);
    }
    
    public Collection<IFile> findRelatedResources(Relationship typeOfRelationship) {
        switch (typeOfRelationship) {
            case DEPENDENT:
                return findDependentResources();
            case DEPENDENCY:
                return findDependencyResources();
            case ALL:
                Set<IFile> files = new HashSet<IFile>();
                files.addAll(findDependentResources());
                files.addAll(findDependentResources());
                return files;
        }
        
        throw new IllegalStateException();
    }
   
}