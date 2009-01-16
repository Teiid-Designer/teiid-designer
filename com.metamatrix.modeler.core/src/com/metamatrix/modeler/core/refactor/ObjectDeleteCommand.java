/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.core.refactor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * ObjectDeleteCommand
 */
public class ObjectDeleteCommand extends ResourceRefactorCommand {

    public static final int ERROR_MISSING_OBJECT = 3001;
    public static final int ERROR_MULTIPLE_FILES = 3002;
    public static final int ERROR_DELETE_NULL = 3003;
    public static final int EXCEPTION_CALCULATING_DEPENDENCIES = 3004;
    public static final int ERROR_READONLY_RESOURCES = 3006;
    public static final int EXCEPTION_DURING_DELETE = 3007;
    public static final int ERROR_SIBLING_NAME = 3008;
    public static final int ERROR_SAME_NAME = 3008;

    private IStatus currentStatus;
    private EObject[] objectsToDelete;
    private String objectName;
    private boolean moreThanOneResource = false;
    private boolean deleteArrayContainsNull = false;

    /**
     * Construct an instance of ResourceRenameCommand.
     */
    public ObjectDeleteCommand() {
        super(ModelerCore.Util.getString("ObjectDeleteCommand.label")); //$NON-NLS-1$
    }

    /**
     * Set the new name for this resource, without the extension. This command will re-apply the resource extension (if one
     * exists).
     * 
     * @param name
     */
    public void setObjectsToDelete( EObject[] objects ) {
        this.objectsToDelete = objects;
        this.deleteArrayContainsNull = false;
        this.moreThanOneResource = false;

        if (objects != null && objects.length > 0 && objects[0] != null) {
            // get the IResource from the first object and set it on the base class
            Resource firstResource = objects[0].eResource();
            ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(firstResource);
            super.setResource(modelResource.getResource());

            // all objects to delete must be in the same resource
            if (objects.length > 1) {
                for (int i = 1; i < objects.length; ++i) {
                    if (objects[i] == null) {
                        deleteArrayContainsNull = true;
                        break;
                    }
                    if (!objects[i].eResource().equals(firstResource)) {
                        moreThanOneResource = true;
                        break;
                    }
                }
            }
        }

        // set the object name for labels & messages
        if (objects.length == 1) {
            this.objectName = ModelerCore.getModelEditor().getName(objects[0]);
        } else {
            String objectCount = new Integer(objects.length).toString();
            this.objectName = ModelerCore.Util.getString("ObjectDeleteCommand.Number_of_objects", objectCount); //$NON-NLS-1$
        }
    }

    private void checkStatus() {
        if (this.objectsToDelete == null || this.objectsToDelete.length == 0) {
            final String msg = ModelerCore.Util.getString("ObjectDeleteCommand.No_delete_target_selected"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_MISSING_OBJECT, msg, null);
            return;
        }

        if (this.moreThanOneResource) {
            final String msg = ModelerCore.Util.getString("ObjectDeleteCommand.Delete_from_more_than_one_file"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_MULTIPLE_FILES, msg, null);
            return;
        }

        if (this.deleteArrayContainsNull) {
            final String msg = ModelerCore.Util.getString("ObjectDeleteCommand.Delete_array_contains_null"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_DELETE_NULL, msg, null);
            return;
        }

        final Object[] params = new Object[] {objectName};
        final String msg = ModelerCore.Util.getString("ObjectDeleteCommand.Ready_to_delete", params); //$NON-NLS-1$
        currentStatus = new Status(IStatus.OK, PID, CAN_EXECUTE, msg, null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#getCanExecuteStatus()
     */
    @Override
    protected IStatus getCanExecuteStatus() {
        checkStatus();
        return this.currentStatus;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#modifyResource(org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus modifyResource( IResource resource,
                                      IProgressMonitor monitor ) {
        try {
            // Added to fix null pointer issue, defect #16050
            super.setModifiedResource(resource);
            monitor.worked(5);
            ModelerCore.getModelEditor().delete(Arrays.asList(objectsToDelete), monitor);
        } catch (ModelerCoreException e) {
            final Object[] params = new Object[] {objectName};
            final String msg = ModelerCore.Util.getString("ObjectDeleteCommand.Error_attempting_to_delete", params); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, EXCEPTION_DURING_DELETE, msg, e);
        }

        return null;
    }

    /* (non-Javadoc)
     * Overridden to collect up only the models that actually reference the object to be deleted.
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getDependentResources()
     */
    @Override
    public Collection getDependentResources() {
        Collection result = new HashSet();
        Collection emfResourceList = new HashSet();

        for (int i = 0; i < objectsToDelete.length; ++i) {
            try {
                Collection list = ModelerCore.getModelEditor().findOtherObjectsToBeDeleted(objectsToDelete[i]);
                list = ModelerCore.getModelEditor().findExternalReferencesToObjectsBeingDeleted(objectsToDelete[i], list);
                for (Iterator iter = list.iterator(); iter.hasNext();) {
                    Resource resource = ((EObject)iter.next()).eResource();
                    if (!emfResourceList.contains(resource)) {
                        emfResourceList.add(resource);
                        result.add(ModelerCore.getModelEditor().findModelResource(resource));
                    }
                }
            } catch (ModelerCoreException e) {
                final String msg = ModelerCore.Util.getString("ObjectDeleteCommand.Error_attempting_calculate_dependencies", objectsToDelete[i]); //$NON-NLS-1$
                super.addProblem(new Status(IStatus.ERROR, PID, EXCEPTION_CALCULATING_DEPENDENCIES, msg, e));
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#undo()
     */
    @Override
    public IStatus undoResourceModification( IProgressMonitor monitor ) {
        // swjTODO: undo the delete
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#redo()
     */
    @Override
    public IStatus redoResourceModification( IProgressMonitor monitor ) {
        // swjTODO: redo the delete
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#getLabel()
     */
    @Override
    public String getLabel() {
        return ModelerCore.Util.getString("ObjectDeleteCommand.delete_label", objectName); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#getDescription()
     */
    @Override
    public String getDescription() {
        final Object[] params = new Object[] {objectName};
        return ModelerCore.Util.getString("ObjectDeleteCommand.delete_description", params); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#canRedo()
     */
    @Override
    public boolean canRedo() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#canUndo()
     */
    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    protected Map getMovedResourcePathMap( boolean isUndo ) {
        return new HashMap();
    }

    /* (non-Javadoc) We do not want to rebuild imports
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#shouldRebuildImports()
     */
    @Override
    protected boolean shouldRebuildImports() {
        return false;
    }
}
