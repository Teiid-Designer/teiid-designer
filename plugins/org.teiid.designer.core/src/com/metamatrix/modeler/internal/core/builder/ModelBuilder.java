/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.notification.util.IgnorableNotificationSource;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.SearchIndexResourceVisitor;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * ModelIndexBuilder
 */
public class ModelBuilder extends IncrementalProjectBuilder implements IgnorableNotificationSource {

    /**
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    @Override
    protected void clean( final IProgressMonitor monitor ) throws CoreException {
        super.clean(monitor);
        final IProject proj = getProject();

        // construct visitor to be used
        SearchIndexResourceVisitor visitor = new SearchIndexResourceVisitor();

        // Notify listeners of clean
        ModelWorkspaceManager.getModelWorkspaceManager().notifyClean(proj);

        // Clean all indexes for project
        ModelWorkspaceManager.getModelWorkspaceManager().deleteIndexes(proj, visitor);

        // Set build state to not indexed on all resources within project
        for (final Iterator iter = visitor.getResources().iterator(); iter.hasNext();) {
            IResource model = (IResource)iter.next();
            final ModelResource resrc = ModelerCore.getModelWorkspace().findModelResource(model);
            if (resrc != null) {
                model.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE); // clear current markers
                resrc.setIndexType(ModelResource.NOT_INDEXED);
            }
        }
    }

    /**
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IProject[] build( final int kind,
                                final Map args,
                                final IProgressMonitor monitor ) throws CoreException {

        final IProject project = getProject();
        if (project == null || !project.isAccessible()) {
            return null;
        }

        switch (kind) {
            case FULL_BUILD: {
                performFullBuild(monitor);
                break;
            }
            case AUTO_BUILD:
            case INCREMENTAL_BUILD: {
                // Build with a delta (Auto/Incremental)
                final IResourceDelta delta = getDelta(project);
                if (delta == null) {
                    performFullBuild(monitor);
                } else {
                    performIncrementaBuild(monitor, delta);
                }
                break;
            }
        }

        return null;
    }

    /**
     * Get all IResources in the given project and index them
     * 
     * @throws CoreException
     */
    private void performFullBuild( final IProgressMonitor monitor ) throws CoreException {
        // System.out.println(" ModelBuilder.performFullBuild(): ******** START ******** ");
        final SearchIndexResourceVisitor visitor = new SearchIndexResourceVisitor();
        // collect all IResources for model files
        getProject().accept(visitor);

        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                // build the resources (index and validate)
                final Container container = doGetContainer();
                List resources = visitor.getResources();

                // clear all markers on these resources as we are going to create fresh markers
                for (Object resource : resources) {
                    try {
                        ((IResource)resource).deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
                    } catch (CoreException e) {
                        ModelerCore.Util.log(e);
                    }
                }

                ModelBuildUtil.buildResources(monitor, visitor.getResources(), container, false);

                return null;
            }
        };
        // Get the list of "modified" EMF resources within the model container
        // prior to performing the build. Since indexing and validation are
        // considered read-only operations we must make sure that any resources
        // that are loaded as a result of these operations have a status of
        // not modified
        final List modifiedResources = ModelBuildUtil.getModifiedResources();

        // Execute the indexing and validation within a transaction
        ModelerCore.getModelEditor().executeAsTransaction(runnable, "Full Build", false, false, this); //$NON-NLS-1$

        // Reset the modified state of all EMF resources to what it was prior to
        // indexing and validating. Simply commiting the transaction can mark a
        // resource as modified so we must undo it.
        ModelBuildUtil.setModifiedResources(modifiedResources);

        // after every full build remove any index files that have
        // no coressponding resources in the workspace
        cleanOrphanedIndexes();
        // System.out.println(" ModelBuilder.performFullBuild(): ********  END  ******** \n");
    }

    /**
     * Get all changed IResources in the given IResourceDelta and index them
     * 
     * @throws CoreException
     */
    private void performIncrementaBuild( final IProgressMonitor monitor,
                                         final IResourceDelta delta ) throws CoreException {
        // System.out.println(" ModelBuilder.performIncrementaBuild(): ******** START ********");
        class ResourceDeltaVisitor implements IResourceDeltaVisitor {
            List resources = new ArrayList();

            public boolean visit( final IResourceDelta delta ) throws CoreException {
                IResource resource = delta.getResource();
                if (isIncludedResource(resource)) {
                    resources.add(resource);
                    // clear all markers on this resource, as we are going to create fresh markets
                    resource.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
                }
                return true;
            }

            public List getResources() {
                return resources;
            }

            private boolean isIncludedResource( final IResource resource ) {
                if (resource == null || !resource.exists()) {
                    return false;
                }

                if (ModelUtil.isModelFile(resource) || ModelUtil.isXsdFile(resource) || ModelUtil.isVdbArchiveFile(resource)) {
                    return true;
                }
                return false;
            }
        }

        final ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
        // collect all IResources for model files
        delta.accept(visitor);

        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                // Collection of IResources to validate/index
                final Collection iResources = visitor.getResources();

                // Add to the iResources collection any VDB IResource that references one of
                // the entries in iResourcs. We want to revalidate VDBs that reference modified
                // resources in the workspace (defect 15779)
                IResource[] vdbResources = WorkspaceResourceFinderUtil.getVdbResourcesThatContain(iResources);
                for (int i = 0; i != vdbResources.length; ++i) {
                    IResource vdbResource = vdbResources[i];
                    if (!iResources.contains(vdbResource)) {
                        iResources.add(vdbResource);
                    }
                }
                // build the resources (index and validate)
                final Container container = doGetContainer();

                ModelBuildUtil.buildResources(monitor, iResources, container, false);

                return null;
            }
        };
        // Get the list of "modified" EMF resources within the model container
        // prior to performing the build. Since indexing and validation are
        // considered read-only operations we must make sure that any resources
        // that are loaded as a result of these operations have a status of
        // not modified
        final List modifiedResources = ModelBuildUtil.getModifiedResources();

        // Execute the indexing and validation within a transaction
        ModelerCore.getModelEditor().executeAsTransaction(runnable, "Incremental Build", false, false, this); //$NON-NLS-1$

        // Reset the modified state of all EMF resources to what it was prior to
        // indexing and validating. Simply commiting the transaction can mark a
        // resource as modified so we must undo it.
        ModelBuildUtil.setModifiedResources(modifiedResources);
        // System.out.println(" ModelBuilder.performIncrementaBuild(): ********  END  ******** \n");
    }

    protected Container doGetContainer() throws ModelerCoreException {
        try {
            return ModelerCore.getModelContainer();
        } catch (CoreException err) {
            throw new ModelerCoreException(err);
        }
    }

    /**
     * Deletes index files that are not associated with any resource in any of the open projects in the workspace.
     * 
     * @throws CoreException
     * @since 4.2
     */
    protected void cleanOrphanedIndexes() throws CoreException {
        // collect all the projects that have been built
        // in the workspace
        IProject currentProj = getProject();
        Collection builtProjects = new LinkedList();
        // collect all projects in the workspace, if any of them have not been built
        // this method aborts
        if (currentProj != null) {
            IWorkspace workspace = currentProj.getWorkspace();
            IWorkspaceRoot root = workspace.getRoot();
            if (root != null) {
                IProject[] projects = root.getProjects();
                for (int i = 0; i < projects.length; i++) {
                    IProject project = projects[i];
                    if (project != null && project.isAccessible()) {
                        // if this is not the current project being built and
                        // the project has never been built...abort...we may be\
                        // in the middle of a full build in the workspace
                        if (!super.hasBeenBuilt(project) && !currentProj.equals(project)) {
                            // abort
                            return;
                        }
                        builtProjects.add(project);
                    }
                }
            }
        }

        if (!builtProjects.isEmpty()) {
            // collect all the file resources that have indexes in
            // all the projects
            final SearchIndexResourceVisitor visitor = new SearchIndexResourceVisitor();
            for (final Iterator iter = builtProjects.iterator(); iter.hasNext();) {
                IProject proj = (IProject)iter.next();
                proj.accept(visitor);
            }
            // get all the files from the location containing indexes
            File[] indexFiles = new File(IndexUtil.INDEX_PATH).listFiles();
            for (int i = 0; i < indexFiles.length; i++) {
                File indexFile = indexFiles[i];
                if (IndexUtil.isIndexFile(indexFile)) {
                    String fileName = indexFile.getName();
                    // check the name of the file againt the name of indexes for a file resource
                    if (!visitor.getIndexNames().contains(fileName)) {
                        // if no resource found for the index file....get rid off it
                        indexFile.delete();
                    }
                }
            }
        }
    }

}
