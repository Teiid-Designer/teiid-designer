/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.refactor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.TransactionRunnable;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.ImportsAspect;
import org.teiid.designer.core.transaction.UnitOfWork;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelResourceFilter;
import org.teiid.designer.core.workspace.ModelResourceImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;

/**
 * ResourceRefactorCommand is an abstract base class for RefactorCommands that need to operate on <code>IResource</code> objects.
 * It accomplishes many tasks common to most RefactorCommand implementations, such as finding dependent models, checking for
 * read-only resources that must be modified, and rebuilding the list of model imports.
 *
 * @since 8.0
 */
public abstract class ResourceRefactorCommand implements RefactorCommand {

    private static final ModelResourceFilter RESOURCE_FILTER = new ModelResourceFilter();

    /** IStatus code indicating that no target Resource has been set for this command before calling canExecute */
    public static final int ERROR_MISSING_RESOURCE = 1100;

    /** IStatus code indicating a model resource was not found */
    public static final int WARNING_REBUILD_IMPORTS = 1101;

    /** IStatus code indicating an exception occurred obtaining the model resource */
    public static final int ERROR_REBUILD_IMPORTS = 1102;

    /** IStatus code indicating that no target Resource has been set for this command before calling canExecute */
    public static final int REBUILD_IMPORTS_COMPLETE = 1103;

    /** IStatus code indicating that the required indexes could not build to search for the depenedent resources */
    public static final int ERROR_BUILD_INDEXES = 1104;

    /** IStatus code indicating that the required indexes to search for the depenedent resources succeeded */
    public static final int BUILD_INDEXES_COMPLETE = 1105;

    /** IStatus code indicating an exception occurred obtaining the model resource */
    public static final int ERROR_RENAMING_VDB = 1106;
    
    /** IStatus code indicating that the call to refactor modified resource in case an internal property contained 
     * the string name or path of the model being modified
     */
    public static final int REFACTOR_MODIFIED_RESOURCE_COMPLETE = 1107;
    
    public static final int REFACTOR_MODIFIED_RESOURCE_ERROR = 1108;

    private static final boolean UNDO_REQUEST = true;

    private static final boolean NOT_UNDO_REQUEST = false;

    public static final String PID = ModelerCore.PLUGIN_ID;
    protected static final IStatus[] EMPTY_ISTATUS = new IStatus[0];

    // MyDefect : Added
    private String nameAfterRename;
    private String label;
    private IResource resource;
    private IResource modifiedResource;
    Collection dependentResources;
    private Collection dependencies;
    private List problems = new ArrayList();
    private OrganizeImportHandler handler;

    /**
     * Construct an instance of ResourceRefactorCommand.
     */
    public ResourceRefactorCommand( final String label ) {
        this.label = label;
        dependencies = Collections.EMPTY_LIST;
        dependentResources = Collections.EMPTY_LIST;
    }

    public void setResource( final IResource resource ) {
        this.resource = resource;
    }

    protected void setModifiedResource( final IResource resource ) {
        this.modifiedResource = resource;
    }

    protected IResource getResource() {
        return this.resource;
    }

    protected IResource getModifiedResource() {
        return this.modifiedResource;
    }

    public void setImportHandler( OrganizeImportHandler handler ) {
        this.handler = handler;
    }

    /**
     * Set the label of this command. Useful when the label contains the name of the resource that has been modified, which may
     * not be known at construction time.
     * 
     * @param label the label of this command, which may be pre-pended with "Undo " or "Redo " by the UI.
     */
    public void setLabel( final String label ) {
        this.label = label;
    }

    // MyDefect : 17255 Refactored,
    /**
     * This method moved from ResourceRenameCommand to base class so all the extending classes have access to it.
     * 
     * @since 4.2
     */
    public void setNewName( String name ) {
        this.nameAfterRename = name;
    }

    public String getNewName() {
        return this.nameAfterRename;
    }

    /**
     * This version of canExecute checks that the argument of <code>setResource</code> has been set and is not read-only. Either
     * of these conditions will generate an <code>IStatus</code> of severity <code>IStatus.ERROR</code>. If these conditions pass,
     * the return value will have severity of <code>IStatus.OK</code>
     * 
     * @see org.teiid.designer.core.refactor.RefactorCommand#canExecute()
     * @return an IStatus
     */
    @Override
	final public IStatus canExecute() {
        if (this.resource == null) {
            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.No_resource_has_been_selected"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_MISSING_RESOURCE, msg, null);
        } else if (ModelUtil.isIResourceReadOnly(resource)) {
            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Selection_is_read_only"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_READONLY_RESOURCE, msg, null);
        }
        IStatus result = getCanExecuteStatus();
        if (result != null) {
            return result;
        }
        final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Ready_to_execute"); //$NON-NLS-1$
        return new Status(IStatus.OK, PID, CAN_EXECUTE, msg, null);
    }

    /**
     * Determine if all conditions necessary for this command to execute have been satisfied.
     * 
     * @return an IStatus with the appropriate severity and message for the pre-execution state of this command. The
     *         implementation may return null if it is okay to execute.
     */
    abstract protected IStatus getCanExecuteStatus();

    /* (non-Javadoc)
     * Subclasses must not implement behavior.
     * @See org.teiid.designer.core.refactor.RefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	final public IStatus execute( IProgressMonitor monitor ) {

        problems.clear();

        try {
            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Execution_complete"); //$NON-NLS-1$
            IStatus result = new Status(IStatus.OK, PID, EXECUTE_SUCCEEDED, msg, null);
            final IStatus okayStatus = result;

            // To check the dependent resources, the index files of all the model resources
            // in the workspace are to be searched. So, generate the index files.
            // result = buildIndexes(monitor);

            if (result.getSeverity() == IStatus.ERROR) {
                return result;
            }

            // check the dependent resources
            int severity = checkDependentResources(NOT_UNDO_REQUEST);

            // see if we should modify the resource
            if (severity < IStatus.ERROR) {

                // If dependent resources are loaded and the effected model resource
                // is unloaded this can cause proxy references to be broken. This can
                // be avoided by unloading the dependent resources first.
                unloadDependentResources();

                // tell the subclass to modify the resource
                result = modifyResource(this.getResource(), monitor);

                if (result == null || result.getSeverity() < IStatus.ERROR) {
                	final Collection<PathPair> refactoredPaths = getMovedResourcePathCollection(false);
                	result = refactorModelContents(monitor, refactoredPaths);
                }
                
                // if modification succeeded, refactor the dependent files
                if (result == null || result.getSeverity() < IStatus.ERROR) {
                    // Get the map of the refactored paths, that is old path mapped to the new refactored path
                    final Collection<PathPair> refactoredPaths = getMovedResourcePathCollection(false);
                    result = refactorDependentResources(monitor, refactoredPaths);
                }

            } else {
                final String msg2 = ModelerCore.Util.getString("ResourceRefactorCommand.Dependent_resource_error"); //$NON-NLS-1$
                return new Status(severity, PID, ERROR_READONLY_RESOURCE, msg2, null);
            }

            if (result.isOK()) {
                return okayStatus;
            }
            return result;
        } catch (Exception ex) {
            return new Status(IStatus.ERROR, PID, ex.getMessage(), ex);
        } finally {
            if (monitor != null) monitor.done();
        }

    }

    /**
     * Perform the necessary modifications to the selected resource
     * 
     * @return an IStatus with the appropriate severity and message for the pre-execution state of this command.
     */
    abstract protected IStatus modifyResource( IResource resource,
                                               IProgressMonitor monitor );

    /**
     * Unload all the dependent resources
     *
     * @throws Exception
     */
    private void unloadDependentResources() throws Exception {
        if (dependentResources == null)
            throw new Exception("Programming error: dependent resources should be calculated before trying to unload them"); //$NON-NLS-1$

        for (Object resource : dependentResources) {
            IFile file  = (IFile) resource;
            unloadModelResources(file);
        }
    }

    /**
     * checkDependentResources
     * 
     * @param isUndo
     * @return int
     * @since 4.3
     */
    protected int checkDependentResources( final boolean isUndo ) {

        // Determine dependent resource
        Collection searchResults = dependentResources(isUndo);
        ResourceStatusList statusList = new ResourceStatusList(searchResults);
        this.dependentResources = statusList.getResourceList();

        // Determine dependencies
        searchResults = dependencyResources(isUndo);
        statusList = new ResourceStatusList(searchResults, IStatus.OK);
        this.dependencies = statusList.getResourceList();

        this.problems.addAll(statusList.getProblems());
        return statusList.getHighestSeverity();
    }

    /**
     * calculateDependentResources
     * 
     * @param isUndo
     * @return Collection
     * @since 4.3
     */
    private Collection dependentResources( final boolean isUndo ) {

        // search the workspace for any models that import anything beneath the path that is moving
        IResource res = resource;
        if (isUndo) {
            res = modifiedResource;
        }

        Collection dependentResource = dependentResources(res);

        // MyDefect : 17647 added to make sure no duplicate dependent resources.
        return checkForDuplicate(dependentResource);
    }

    /**
     * calculateDependentResources
     * 
     * @param res
     * @return Collection
     * @since 4.3
     */
    private static Collection dependentResources( IResource res ) {

        Collection dependentResources = new ArrayList();

        try {
            if (res instanceof IContainer) {
                // sometimes this is getting called with a nonexistent resourse...
                // see defect 18558 for more details
                if (res.exists()) {
                    IContainer folder = (IContainer)res;
                    IResource[] resources = folder.members();
                    dependentResources = new LinkedList();

                    for (int idx = 0; idx < resources.length; idx++) {
                        dependentResources.addAll(dependentResources(resources[idx]));
                    }
                } // endif
            } else {
                WorkspaceResourceFinderUtil.getResourcesThatUseRecursive(res, RESOURCE_FILTER, dependentResources);
            }
        } catch (CoreException ce) {
            ModelerCore.Util.log(ce);
            dependentResources = Collections.EMPTY_LIST;
        }

        return dependentResources;
    }

    /**
     * calculateDependencyResources
     * 
     * @param isUndo
     * @return Collection
     * @since 4.3
     */
    private Collection dependencyResources( final boolean isUndo ) {

        IResource res = resource;
        if (isUndo) {
            res = modifiedResource;
        }

        return dependencyResources(res);
    }

    /**
     * calculateDependencyResources
     * 
     * @param res
     * @return Collection
     * @since 4.3
     */
    private static Collection dependencyResources( IResource res ) {

        Collection dependencyResource = Collections.EMPTY_LIST;

        try {
            if (res instanceof IContainer) {
                IContainer folder = (IContainer)res;
                IResource[] resources = folder.members();
                dependencyResource = new LinkedList();

                for (int idx = 0; idx < resources.length; idx++) {
                    dependencyResource.addAll(dependencyResources(resources[idx]));
                }

            } else {
                dependencyResource = Arrays.asList(WorkspaceResourceFinderUtil.getDependentResources(res));
                // ModelWorkspaceSearch search = new ModelWorkspaceSearch();
                // String path = res.getFullPath().toString();
                // dependencyResource = search.getResourcesImportedByModel(path);

            }
        } catch (CoreException ce) {
            ModelerCore.Util.log(ce);
            dependencyResource = Collections.EMPTY_LIST;
        }

        return dependencyResource;
    }

    /**
     * @param dependentResource
     * @return Collection
     * @since 4.3
     */
    private Collection checkForDuplicate( Collection dependentResource ) {

        if (dependentResource.size() <= 1) return dependentResource;

        Set uniqueSet = new HashSet();

        for (Iterator iter = dependentResource.iterator(); iter.hasNext();) {
            IResource nextResource = (IResource)iter.next();
            uniqueSet.add(nextResource);
        }

        return new ArrayList(uniqueSet);
    }

    /**
     * Obtain a list of resources that are dependent upon the target resource for this object.
     * 
     * @return
     */
    protected Collection getModifiedDependentResources() {
        return dependentResources(getModifiedResource());
    }

    /**
     * Obtain a list of resources that are dependent upon the target resource for this object.
     * 
     * @return
     */
    // protected static Collection getDependentResources(IResource res) {
    // Collection rv;
    //
    // if (res instanceof IContainer) {
    // // defect 12382 - if a folder type, we need to scan all resources underneath:
    // IContainer cont = (IContainer) res;
    // IResource[] mems = EMPTY_IRESOURCE_ARRAY;
    // try {
    // mems = cont.members();
    // } catch (CoreException ex) {
    //                String message = ModelerCore.Util.getString("ResourceRefactorCommand.getDependentResourcesProblemMessage", res.getFullPath());  //$NON-NLS-1$
    // ModelerCore.Util.log(IStatus.ERROR, ex, message);
    // } // endtry
    //            
    // if (mems.length > 0) {
    // rv = new HashSet();
    // for (int i = 0; i < mems.length; i++) {
    // IResource subres = mems[i];
    // rv.addAll(getDependentResources(subres));
    // } // endfor
    // } else {
    // // no children, or exception:
    // rv = Collections.EMPTY_SET;
    // } // endif
    // } else {
    // // search the workspace for any models that import anything beneath the path that is moving
    // Collection colDependentResources = WorkspaceResourceFinderUtil.getResourcesThatUse(res, RESOURCE_FILTER);
    //            
    // // build a ResourceStatusList from the search results
    // ResourceStatusList statusList = new ResourceStatusList(colDependentResources);
    // rv = statusList.getResourceList();
    // } // endif
    //        
    // return rv;
    // }
    
    /**
     * Rebuilds the model import list and fixes broken references for all model files that use this command's resource.
     * 
     * @param monitor
     * @return
     */
    protected IStatus refactorDependentResources( IProgressMonitor monitor,
                                                  final Collection<PathPair> refactoredPathPairs ) {
        Collection errorList = new ArrayList();
        int severity = IStatus.OK;

        final ModelEditor editor = ModelerCore.getModelEditor();
        // defect 16804 - make sure we need to rebuild imports:
        if (shouldRebuildImports()) {
            for (final Iterator iter = this.dependentResources.iterator(); iter.hasNext();) {
                IFile modelFile = (IFile)iter.next();
                try {
                    ModelResource modelResource = editor.findModelResource(modelFile);
                    if (modelResource != null) {
                        if (modelResource.getResource() == null || !modelResource.getResource().exists()) {
                            continue;
                        }
                        // defect 16804 - should not try to refactor read-only dependent resources:
                        if (!modelResource.isReadOnly()) {
                            rebuildImports(modelResource, monitor, refactoredPathPairs);

                            modelResource.save(null, false);
                            
//                            RefactorModelExtensionManager.helpUpdateDependentModelContents(IRefactorModelHandler.RENAME, modelResource, refactoredPathPairs, monitor);
//                            
//                            modelResource.save(null, false);
                        } // endif -- readonly
                    } else {
                        if (severity < IStatus.WARNING) {
                            severity = IStatus.WARNING;
                        }
                        final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Model_resource_not_in_ws", modelFile.getName()); //$NON-NLS-1$
                        errorList.add(new Status(IStatus.WARNING, PID, WARNING_REBUILD_IMPORTS, msg, null));
                    }

                } catch (ModelWorkspaceException e) {
                    severity = IStatus.ERROR;
                    final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Exception_finding_model_resource", modelFile.getName()); //$NON-NLS-1$
                    errorList.add(new Status(IStatus.ERROR, PID, ERROR_REBUILD_IMPORTS, msg, e));
                }
            }
        } // endif

        // Added as partial fix to Defect 15916
        validateDependentResources();

        // Update imports within model being refactored.
        final Map map = new HashMap();
        for (final Iterator iter = this.dependencies.iterator(); iter.hasNext();) {
            final IFile file = (IFile)iter.next();
            final String path = file.getFullPath().toString();
            map.put(path, path);
        }
        if (!map.isEmpty()) {
            try {
                ModelResource model;

                // defect 18462 - sometimes this resource is a folder, not a file.
                if (resource instanceof IFile) {
                    model = editor.findModelResource((IFile)this.resource);
                } else {
                    // we were not a file, so no modelRes exists:
                    model = null;
                } // endif

                if (model != null && model.isXsd()) {
                    final Resource oldEmfResrc = model.getEmfResource();
                    // MyDefect : 16368
                    final XSDSchema xsdSchema = ((XSDResourceImpl)oldEmfResrc).getSchema();
                    if (xsdSchema != null) {
                        final Iterator oldIter = xsdSchema.getContents().iterator();
                        model = editor.findModelResource((IFile)this.modifiedResource);
                        final Resource emfResrc = model.getEmfResource();
                        final Iterator iter = ((XSDResourceImpl)emfResrc).getSchema().getContents().iterator();
                        while (oldIter.hasNext()) {
                            // For each of the EObjects, get the import Aspect
                            final EObject oldEObj = (EObject)oldIter.next();
                            final EObject eObj = (EObject)iter.next();
                            // Skip annotations
                            if (oldEObj instanceof XSDAnnotation) {
                                continue;
                            }
                            // Break if named component found since all imports, includes, and redefines must appear before these
                            if (oldEObj instanceof XSDNamedComponent) {
                                break;
                            }
                            final ImportsAspect importsAspect = AspectManager.getModelImportsAspect(oldEObj);
                            if (importsAspect != null) {

                                if (oldEObj instanceof XSDSchemaDirective) {
                                    Container cntr = ModelerCore.getContainer(emfResrc);
                                    Resource refResource = cntr.getResourceFinder().findByImport((XSDSchemaDirective)oldEObj,
                                                                                                 true);
                                    if (refResource != null) {
                                        importsAspect.setModelLocation(eObj, refResource.getURI());
                                        emfResrc.setModified(true);
                                    }

                                }

                                // // If imports Aspect is not null, get the import path
                                // final IPath importPath = importsAspect.getModelPath(oldEObj);
                                // if (importPath != null) {
                                // final String refactoredPath = (String)map.get(importPath.makeAbsolute().toString());
                                // // If the import path has been changed
                                // if (refactoredPath != null) {
                                // // unload the old refactored model resource (since its not unloaed during refactoring of the
                                // physical file)
                                // importsAspect.setModelPath(eObj, new Path(refactoredPath));
                                // emfResrc.setModified(true);
                                // }
                                // }
                            }
                        }
                    }
                }
            } catch (final ModelWorkspaceException err) {
                severity = IStatus.ERROR;
                final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Exception_finding_model_resource", //$NON-NLS-1$
                                                              this.resource.getName());
                errorList.add(new Status(IStatus.ERROR, PID, ERROR_REBUILD_IMPORTS, msg, err));
            }
        }

        // defect 16076 - display the correct text on completion, and display all errors
        String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Execution_complete"); //$NON-NLS-1$
        MultiStatus multiStatus = new MultiStatus(PID, REBUILD_IMPORTS_COMPLETE, (IStatus[])errorList.toArray(EMPTY_ISTATUS),
                                                  msg, null);
        if (!multiStatus.isOK()) {
            msg = ModelerCore.Util.getString("ResourceRefactorCommand.Dependent_resource_error"); //$NON-NLS-1$
            multiStatus = new MultiStatus(PID, ERROR_REBUILD_IMPORTS, (IStatus[])errorList.toArray(EMPTY_ISTATUS), msg, null);
        }
        return multiStatus;
    }

    /*
     * This method calls the appropriate validate method to re-index the dependent resources.
     * Though the resources are saved, some modeler components will think it's Dirty, for instance. DEFECT 15916 partial fix.
     */
    protected void validateDependentResources() {
        if (!dependentResources.isEmpty()) {
            final TransactionRunnable runnable = new TransactionRunnable() {
                @Override
				public Object run( final UnitOfWork uow ) {
                    Container cont = null;
                    try {
                        cont = ModelerCore.getModelContainer();
                    } catch (CoreException err) {
                        String message = ModelerCore.Util.getString("ResourceRefactorCommand.doGetContainerProblemMessage"); //$NON-NLS-1$
                        ModelerCore.Util.log(IStatus.ERROR, err, message);
                    }
                    ModelBuildUtil.validateResources(null, dependentResources, cont, false);
                    return null;
                }
            };
            // Execute the validation within a transaction as this operation may open resources
            // and create new EObjects
            try {
                ModelerCore.getModelEditor().executeAsTransaction(runnable, "Updating ModelIndexes", false, false, this); //$NON-NLS-1$
            } catch (CoreException err) {
                ModelerCore.Util.log(err);
            }
        }
    }

    protected IStatus rebuildImports( ModelResource modelResource,
                                      IProgressMonitor monitor,
                                      Collection<PathPair> refactoredPathPairs ) throws ModelWorkspaceException {
        final OrganizeImportCommand importCommand = new OrganizeImportCommand();
        importCommand.setResource(modelResource.getEmfResource());
        importCommand.setIncludeDiagramReferences(true);

        // The OrganizeImportCommand has to be provided with a Map of old and new paths
        // of resources that are affected by refactoring.
        importCommand.setRefactoredPaths(refactoredPathPairs);
        importCommand.setHandler(this.handler);
        IStatus status = importCommand.canExecute();
        if (status.isOK()) {
            status = importCommand.execute(monitor);
        }
        return status;
    }

    /* (non-Javadoc)
     * Default implementation returns true.  Override to return false.
     * @See org.teiid.designer.core.refactor.RefactorCommand#canUndo()
     */
    @Override
	public boolean canUndo() {
        if (anyResourceReadOnly()) return false;

        return true;
    }

    /* (non-Javadoc)
     * Default implementation returns true.  Override to return false.
     * @See org.teiid.designer.core.refactor.RefactorCommand#canRedo()
     */
    @Override
	public boolean canRedo() {
        if (anyResourceReadOnly()) return false;

        return true;
    }

    /* (non-Javadoc)
     * Returns an empty list.  Subclasses may override to implement
     * @See org.teiid.designer.core.refactor.RefactorCommand#getResult()
     */
    @Override
	public Collection getResult() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * Returns an empty list.  Subclasses may override to implement
     * @See org.teiid.designer.core.refactor.RefactorCommand#getAffectedObjects()
     */
    @Override
	public Collection getAffectedObjects() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.RefactorCommand#getLabel()
     */
    @Override
	public String getLabel() {
        return label;
    }

    /* (non-Javadoc)
     * Returns the label property.  Subclasses may override to implement.
     * @See org.teiid.designer.core.refactor.RefactorCommand#getDescription()
     */
    @Override
	public String getDescription() {
        return label;
    }

    /* (non-Javadoc)
     * Returns an empty list.  Subclasses may override to implement
     * @See org.teiid.designer.core.refactor.RefactorCommand#getPostExecuteMessages()
     */
    @Override
	public Collection getPostExecuteMessages() {
        return this.problems;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.RefactorCommand#redo(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	final public IStatus redo( IProgressMonitor monitor ) {
        problems.clear();

        int severity = checkDependentResources(NOT_UNDO_REQUEST);
        if (severity >= IStatus.ERROR) {
            final String msg2 = ModelerCore.Util.getString("ResourceRefactorCommand.Dependent_resource_error"); //$NON-NLS-1$
            return new Status(severity, PID, ERROR_READONLY_RESOURCE, msg2, null);
        }

        redoResourceModification(monitor);
        final Collection<PathPair> refactoredPaths = getMovedResourcePathCollection(false);
        
    	IStatus result = refactorModelContents(monitor, refactoredPaths);
    
	    // if modification succeeded, refactor the dependent files
	    if (result == null || result.getSeverity() < IStatus.ERROR) {
	        result = refactorDependentResources(monitor, refactoredPaths);
	    }
        
        return result;
    }

    /**
     * Re-apply the resource modification. Typically called after undo. Subclasses that do not support redo may no-op.
     * 
     * @param monitor
     */
    abstract protected IStatus redoResourceModification( IProgressMonitor monitor );
    
    abstract protected IStatus refactorModelContents( IProgressMonitor monitor, Collection<PathPair> refactoredPathPairs);

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.RefactorCommand#undo(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	final public IStatus undo( IProgressMonitor monitor ) {
        problems.clear();

        int severity = checkDependentResources(UNDO_REQUEST);
        if (severity >= IStatus.ERROR) {
            final String msg2 = ModelerCore.Util.getString("ResourceRefactorCommand.Dependent_resource_error"); //$NON-NLS-1$
            return new Status(severity, PID, ERROR_READONLY_RESOURCE, msg2, null);
        }

        undoResourceModification(monitor);
        final Collection<PathPair> refactoredPaths = getMovedResourcePathCollection(true);
    	IStatus result = refactorModelContents(monitor, refactoredPaths);
        
	    // if modification succeeded, refactor the dependent files
	    if (result == null || result.getSeverity() < IStatus.ERROR) {
	        result = refactorDependentResources(monitor, refactoredPaths);
	    }
        
        return result;
    }

    /**
     * Undo the resource modification. Can be called either after execute or redo. Subclasses that do not support undo may no-op.
     * 
     * @param monitor
     */
    abstract protected IStatus undoResourceModification( IProgressMonitor monitor );

    /**
     * Allow subclasses to add problems to the list
     * 
     * @param problem
     */
    protected void addProblem( IStatus problem ) {
        this.problems.add(problem);
        // swjTODO: figure out what the severity is and, if ERROR, return ERROR from execute's IStatus.
    }

    protected void unloadModelResources( IResource resource ) throws CoreException {
        // Collect all IResources within all IProjects
        ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();
        resource.accept(visitor);
        for (Iterator iter = visitor.getModelResources().iterator(); iter.hasNext();) {
            ModelResource mResource = (ModelResource)iter.next();

            // MyDefect : 16368
            // if (!mResource.isXsd()) {
            // Do not unload xsd model resources, as they are used for getting the imports aspect
            mResource.unload();
            mResource.close();
            if (mResource instanceof ModelResourceImpl) {
                ((ModelResourceImpl)mResource).removeEmfResource();
            }
            // }
        }

        // The resources move/rename will trigger the event that will actually remove and create
        // the corresponding resources, since these too are workspace management events
        // they are processed after the refactoring is done. But since we need the index files at
        // the old path to be deleted and the index files at the new path to be created,
        // we do it explicitly.

        // Delete the index files corresponding to the model resource at the old path
        for (Iterator iter = visitor.getResources().iterator(); iter.hasNext();) {
            IResource tmpResource = (IResource)iter.next();

            if (ModelUtil.isModelFile(tmpResource) && tmpResource.getLocation() != null) {
                // Remove the runtime index file associated with the resource being removed
                String runtimeIndexFileName = IndexUtil.getRuntimeIndexFileName(tmpResource);
                File runtimeIndexFile = new File(IndexUtil.INDEX_PATH, runtimeIndexFileName);
                if (!runtimeIndexFile.delete()) {
                    runtimeIndexFile.deleteOnExit();
                }

                // Remove the search index file associated with the resource being removed
                String searchIndexFileName = IndexUtil.getSearchIndexFileName(tmpResource);
                File searchIndexFile = new File(IndexUtil.INDEX_PATH, searchIndexFileName);
                if (!searchIndexFile.delete()) {
                    searchIndexFile.deleteOnExit();
                }
            }
        }
    }

    protected IStatus buildIndexes( final IProgressMonitor monitor,
                                    final Collection iResources ) {
        try {
            // If there are models with unsaved changes create temporary index files
            // for use in query validation and resolution
            final TransactionRunnable runnable = new TransactionRunnable() {
                @Override
				public Object run( final UnitOfWork uow ) {
                    ModelBuildUtil.indexResources(null, iResources);
                    return null;
                }
            };
            // Execute the indexing within a transaction as this operation may open resources
            // and create new EObjects
            ModelerCore.getModelEditor().executeAsTransaction(runnable, "Updating ModelIndexes", false, false, this); //$NON-NLS-1$            
        } catch (Exception e) {
            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Exception_building_indexes"); //$NON-NLS-1$            
            return new Status(IStatus.ERROR, PID, ERROR_REBUILD_IMPORTS, msg, e);
        }
        final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Building_indexes_complete"); //$NON-NLS-1$
        return new Status(IStatus.OK, PID, BUILD_INDEXES_COMPLETE, msg, null);
    }

    protected IStatus buildIndexes( final IProgressMonitor monitor,
                                    IResource iResource ) {
        Collection iResources = new ArrayList(1);
        iResources.add(iResource);
        return buildIndexes(monitor, iResources);
    }

    protected boolean anyResourceReadOnly() {
        return !getReadOnlyDependentResources().isEmpty();
    }

    public List getReadOnlyDependentResources() {
        IResource res = getModifiedResource();
        if (res == null) {
            res = getResource();
        } // endif
        return getReadOnlyDependentResources(res);
    }

    protected static List getReadOnlyDependentResources( IResource res ) {
        // We need to check all resources to see if any have been changed to read-only
        List rv = new ArrayList();
        ModelResource mr = null;

        try {
            mr = ModelUtil.getModel(res);
        } catch (Exception err) {
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
        }

        if (mr != null && mr.isReadOnly()) {
            rv.add(mr);
        }

        // Iterator iter = getDependentResources(res).iterator();
        Iterator iter = dependentResources(res).iterator();
        while (iter.hasNext()) {
            IResource dres = (IResource)iter.next();
            try {
                mr = ModelUtil.getModel(dres);
                if (mr != null && mr.isReadOnly()) {
                    rv.add(dres);
                }
            } catch (ModelWorkspaceException err) {
                ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
            }
        }

        return rv;
    }

    /**
     * Obtain a map containing the path of any ModelResource that is being changed by this operation. The key values shall be the
     * String path of the ModelResource before the move, thus enabling lookup for Model Imports that need to change. This method
     * will be called following <code>modifyResource</code>, and <code>redoResourceModification</code>. The method will also be
     * called following <code>undoResourceModification</code>, in which case the key/value entries in the map must be the reverse
     * of the map returned following <code>modifyResource</code>.
     * 
     * @param isUndo true if this operation is being undone.
     * @return
     */
    protected abstract Collection<PathPair> getMovedResourcePathCollection( boolean isUndo );

    /** Indicate whether we should attempt to rebuild imports */
    protected boolean shouldRebuildImports() {
        return true;
    }

    /**
     * @since 4.2
     */
    public Collection getDependentResources() {
        return this.dependentResources;
    }
}
