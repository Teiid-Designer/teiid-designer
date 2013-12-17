/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.TransactionRunnable;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.core.transaction.UnitOfWork;
import org.teiid.designer.core.workspace.ModelResource;


/**
 * ObjectDeleteCommand
 *
 * @since 8.0
 */
public class ObjectDeleteCommand implements RefactorCommand {

    private static final String PID = ModelerCore.PLUGIN_ID;

    /** IStatus code indicating that no target Resource has been set for this command before calling canExecute */
    private static final int ERROR_MISSING_OBJECT = 3001;
    private static final int ERROR_MULTIPLE_FILES = 3002;
    private static final int ERROR_DELETE_NULL = 3003;
    private static final int EXCEPTION_CALCULATING_DEPENDENCIES = 3004;
    private static final int EXCEPTION_DURING_DELETE = 3007;

    private List<EObject> objectsToDelete;
    private String objectName;
    private boolean moreThanOneResource = false;
    private boolean deleteArrayContainsNull = false;

    private List<IStatus> problems = new ArrayList<IStatus>();
    private Collection<IFile> relatedResources;

    /**
     * Construct an instance of ResourceRenameCommand.
     */
    public ObjectDeleteCommand() {
        relatedResources = Collections.emptyList();
    }

    /**
     * Add a problem to the problems list
     * 
     * @param problem
     */
    private void addProblem( IStatus problem ) {
        this.problems.add(problem);
    }

    /**
     * Set the objects to be deleted by this command
     *
     * @param objects
     *
     */
    public void setObjectsToDelete( List<EObject> objects ) {
        this.objectsToDelete = objects;
        this.deleteArrayContainsNull = false;
        this.moreThanOneResource = false;

        if (objects != null && objects.size() > 0 && objects.get(0) != null) {
            // get the IResource from the first object and set it on the base class
            Resource firstResource = objects.get(0).eResource();

            // all objects to delete must be in the same resource
            if (objects.size() > 1) {
                for (EObject eObject : objects) {
                    if (eObject == null) {
                        deleteArrayContainsNull = true;
                        break;
                    }
                    if (!eObject.eResource().equals(firstResource)) {
                        moreThanOneResource = true;
                        break;
                    }
                }
            }
        }

        // set the object name for labels & messages
        if (objects.size() == 1) {
            this.objectName = ModelerCore.getModelEditor().getName(objects.get(0));
        } else {
            this.objectName = ModelerCore.Util.getString("ObjectDeleteCommand.Number_of_objects", objects.size()); //$NON-NLS-1$
        }
    }

    @Override
    public String getLabel() {
        return ModelerCore.Util.getString("ObjectDeleteCommand.delete_label", objectName); //$NON-NLS-1$
    }

    @Override
    public String getDescription() {
        Object[] params = new Object[] {objectName};
        return ModelerCore.Util.getString("ObjectDeleteCommand.delete_description", params); //$NON-NLS-1$
    }

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public IStatus canExecute() {
        IStatus status;
        String msg;

        if (this.objectsToDelete == null || this.objectsToDelete.size() == 0) {
            msg = ModelerCore.Util.getString("ObjectDeleteCommand.No_delete_target_selected"); //$NON-NLS-1$
            status = new Status(IStatus.ERROR, PID, ERROR_MISSING_OBJECT, msg, null);
            return status;
        }

        if (this.moreThanOneResource) {
            msg = ModelerCore.Util.getString("ObjectDeleteCommand.Delete_from_more_than_one_file"); //$NON-NLS-1$
            status = new Status(IStatus.ERROR, PID, ERROR_MULTIPLE_FILES, msg, null);
            return status;
        }

        if (this.deleteArrayContainsNull) {
            msg = ModelerCore.Util.getString("ObjectDeleteCommand.Delete_array_contains_null"); //$NON-NLS-1$
            status = new Status(IStatus.ERROR, PID, ERROR_DELETE_NULL, msg, null);
            return status;
        }

        ModelResource modelResource = getModelResource();
        if (modelResource == null || modelResource.isReadOnly()) {
            msg = ModelerCore.Util.getString("ObjectDeleteCommand.Selection_is_read_only"); //$NON-NLS-1$
            status = new Status(IStatus.ERROR, PID, ERROR_READONLY_RESOURCE, msg, null);
            return status;
        }

        Object[] params = new Object[] {objectName};
        msg = ModelerCore.Util.getString("ObjectDeleteCommand.Ready_to_delete", params); //$NON-NLS-1$
        status = new Status(IStatus.OK, PID, CAN_EXECUTE, msg, null);
        return status;
    }

    /**
     * Get the model resource from the objects being
     * deleted. Since all objects are in the same resource,
     * returning the first object's resource is sufficient.
     */
    private ModelResource getModelResource() {
        ModelEditor editor = ModelerCore.getModelEditor();
        ModelResource modelResource = editor.findModelResource(objectsToDelete.get(0));
        return modelResource;
    }

    /**
     * Get the resources dependent upon the objects being deleted
     *
     * @return collection
     */
    public Set<ModelResource> getDependentResources() {
        Set<ModelResource> result = new HashSet<ModelResource>();
        Collection<Resource> emfResourceList = new HashSet<Resource>();

        for (EObject object : objectsToDelete) {
            try {
                Collection<EObject> relatedList = ModelerCore.getModelEditor().findOtherObjectsToBeDeleted(object);
                relatedList = ModelerCore.getModelEditor().findExternalReferencesToObjectsBeingDeleted(object, relatedList);
                for (EObject dependent : relatedList) {
                    Resource resource = dependent.eResource();
                    if (!emfResourceList.contains(resource)) {
                        emfResourceList.add(resource);
                        result.add(ModelerCore.getModelEditor().findModelResource(resource));
                    }
                }
            } catch (ModelerCoreException e) {
                String msg = ModelerCore.Util.getString("ObjectDeleteCommand.Error_attempting_calculate_dependencies", object); //$NON-NLS-1$
                addProblem(new Status(IStatus.ERROR, PID, EXCEPTION_CALCULATING_DEPENDENCIES, msg, e));
            }
        }

        return result;
    }

    /**
     * Check the dependent resources to determine if it is OK to delete the objects.
     *
     * TODO
     * Consider whether it is better to use eObjects to search as in {@link #getDependentResources()}
     * rather than this implementation which uses the first eObject's resource.
     *
     * @return value of severity of problems encountered.
     */
    private int checkDependentResources() {

        RelatedResourceFinder finder = new RelatedResourceFinder(getModelResource().getResource());

        // Determine dependent resource
        Collection<IFile> searchResults = finder.findRelatedResources(Relationship.ALL);
        ResourceStatusList statusList = new ResourceStatusList(searchResults);
        this.relatedResources = statusList.getResourceList();
        this.problems.addAll(statusList.getProblems());

        return statusList.getHighestSeverity();
    }


    /**
     * Calls the appropriate validate method to re-index the related resources.
     */
    private void validateDependentResources() {
        if (relatedResources.isEmpty())
            return;

        TransactionRunnable runnable = new TransactionRunnable() {
            @Override
            public Object run( final UnitOfWork uow ) {
                Container cont = null;
                try {
                    cont = ModelerCore.getModelContainer();
                } catch (CoreException err) {
                    String msg = ModelerCore.Util.getString("ObjectDeleteCommand.doGetContainerProblemMessage"); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, err, msg);
                }
                ModelBuildUtil.validateResources(null, relatedResources, cont, false);
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

    @Override
    public IStatus execute( IProgressMonitor monitor ) {
        problems.clear();

        try {
            String msg = ModelerCore.Util.getString("ObjectDeleteCommand.Execution_complete"); //$NON-NLS-1$
            IStatus result = new Status(IStatus.OK, PID, EXECUTE_SUCCEEDED, msg, null);

            // To check the dependent resources, the index files of all the model resources
            // in the workspace are to be searched. So, generate the index files.
            // result = buildIndexes(monitor);

            if (result.getSeverity() == IStatus.ERROR) {
                return result;
            }

            // check the dependent resources
            int severity = checkDependentResources();

            // see if we should modify the resource
            if (severity >= IStatus.ERROR) {
                msg = ModelerCore.Util.getString("ObjectDeleteCommand.Dependent_resource_error"); //$NON-NLS-1$
                return new Status(severity, PID, ERROR_READONLY_RESOURCE, msg, null);
            }

            // Delete the objects
            try {
                ModelerCore.getModelEditor().delete(objectsToDelete, monitor);
            } catch (ModelerCoreException e) {
                final Object[] params = new Object[] {objectName};
                msg = ModelerCore.Util.getString("ObjectDeleteCommand.Error_attempting_to_delete", params); //$NON-NLS-1$
                return new Status(IStatus.ERROR, PID, EXCEPTION_DURING_DELETE, msg, e);
            }

            validateDependentResources();
            return result;
        } catch (Exception ex) {
            return new Status(IStatus.ERROR, PID, ex.getMessage(), ex);
        } finally {
            if (monitor != null) monitor.done();
        }

    }

    @Override
    public IStatus undo( IProgressMonitor monitor ) {
        // do nothing - command is not undoable
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo( IProgressMonitor monitor ) {
        // do nothing - command is not redoable
        return Status.OK_STATUS;
    }

    @Override
    public Collection<Object> getResult() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<Object> getAffectedObjects() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<IStatus> getPostExecuteMessages() {
        return this.problems;
    }
}
