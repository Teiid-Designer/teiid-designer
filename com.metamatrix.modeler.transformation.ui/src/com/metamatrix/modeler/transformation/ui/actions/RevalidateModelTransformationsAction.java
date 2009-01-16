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
package com.metamatrix.modeler.transformation.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.transformation.util.SqlConverter;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

public class RevalidateModelTransformationsAction extends Action implements ISelectionListener, Comparable, ISelectionAction {

    private ModelResource modelResource;
    List brokenTables = Collections.EMPTY_LIST;
    private static final String REVALIDATE_TRANS = "Revalidate Transformations"; //$NON-NLS-1$

    public RevalidateModelTransformationsAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.REVALIDATE_TRANSFORMATION_ICON));
    }

    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        Object selectedObject = SelectionUtilities.getSelectedObject(selection);
        boolean enable = false;
        if (selectedObject instanceof IResource && ModelUtilities.isModelFile((IResource)selectedObject)) {
            try {
                this.modelResource = ModelUtilities.getModelResource(((IFile)selectedObject), false);
                if (ModelUtilities.isVirtual(this.modelResource)) {
                    enable = true;
                }
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(e);
            }
        }
        setEnabled(enable);
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {
        //
        // Get the currently active ModelObjectEditorPage, ensure its TransformationObjEditorPage
        //
        IEditorPart editor = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().getActiveEditor();
        if (editor != null && editor instanceof MultiPageModelEditor) {
            ModelObjectEditorPage moep = ((MultiPageModelEditor)editor).getActiveObjectEditor();
            if (moep != null && moep instanceof TransformationObjectEditorPage) {
                ((MultiPageModelEditor)editor).closeObjectEditor();
            }
        }
        // Force the modelEditor open, so that changes mark the resource dirty
        IResource resource = modelResource.getResource();
        ModelEditorManager.getModelEditorForFile((IFile)resource, true);

        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( IProgressMonitor theMonitor ) {
                brokenTables = revalidate();
                theMonitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            UiConstants.Util.log(e.getTargetException());
        }
        warnUserAboutInvalidTransformations(brokenTables);
        brokenTables = Collections.EMPTY_LIST;
    }

    List revalidate() {
        // Keep list of problems
        List brokenSqlTables = new ArrayList();

        if (modelResource != null) {
            // shut off transformation notifications
            UiPlugin.getDefault().setIgnoreTransformationNotifications(true);

            // start txn
            boolean requiredStart = ModelerCore.startTxn(true, false, REVALIDATE_TRANS, this);
            boolean succeeded = false;
            try {

                Object nextObj = null;
                List transformations = modelResource.getModelTransformations().getTransformations();

                // Clean out the SqlMappingRootCache for these transformations
                invalidateSqlRootCache(transformations);

                // Iterate all of the transformation mapping roots
                for (Iterator iter = transformations.iterator(); iter.hasNext();) {
                    nextObj = iter.next();
                    if (TransformationHelper.isSqlTransformationMappingRoot(nextObj)) {
                        // Get current mapping root and query validator
                        SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)nextObj;
                        QueryValidator qv = new TransformationValidator(mappingRoot, false);

                        // Check if INSERT/UPDATE/DELETE should be validated
                        boolean supportsUpdates = false;
                        EObject mRootTarget = mappingRoot.getTarget();
                        SqlAspect sqlAspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(mRootTarget);
                        if (sqlAspect != null && (sqlAspect instanceof SqlTableAspect)) {
                            supportsUpdates = ((SqlTableAspect)sqlAspect).supportsUpdate(mRootTarget);
                        }

                        // Init query valid flags - will be set to false if problems.
                        boolean selectValid = true;
                        boolean insertValid = true;
                        boolean updateValid = true;
                        boolean deleteValid = true;

                        // Reset Select
                        selectValid = resetTransformation(mappingRoot, QueryValidator.SELECT_TRNS, qv);

                        // Reset Insert, Update, Delete
                        if (supportsUpdates) {
                            // RESET INSERT
                            insertValid = resetTransformation(mappingRoot, QueryValidator.INSERT_TRNS, qv);
                            // RESET UPDATE
                            updateValid = resetTransformation(mappingRoot, QueryValidator.UPDATE_TRNS, qv);
                            // RESET DELETE
                            deleteValid = resetTransformation(mappingRoot, QueryValidator.DELETE_TRNS, qv);
                        } else {
                            clearUUIDSqlString(mappingRoot, QueryValidator.INSERT_TRNS);
                            clearUUIDSqlString(mappingRoot, QueryValidator.UPDATE_TRNS);
                            clearUUIDSqlString(mappingRoot, QueryValidator.DELETE_TRNS);
                        }

                        // If any of the components have a problem, add to broken table list.
                        if (!selectValid || !insertValid || !updateValid || !deleteValid) {
                            brokenSqlTables.add(mRootTarget);
                        }
                    }
                }
                succeeded = true;
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(e);
            } finally {
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
                // re-enable transformation notifications
                UiPlugin.getDefault().setIgnoreTransformationNotifications(false);
            }
        }

        return brokenSqlTables;
    }

    private boolean resetTransformation( final SqlTransformationMappingRoot mappingRoot,
                                         final int cmdType,
                                         final QueryValidator qv ) {
        boolean isValid = true;

        // Validate the uuid string - if valid, delete the user string
        String uuidString = TransformationHelper.getUUIDSqlString(mappingRoot, cmdType);
        String userSqlString = TransformationHelper.getSqlString(mappingRoot, cmdType);
        QueryValidationResult qvr = qv.validateSql(uuidString, cmdType, true, false);
        // if UUID form is correct, use it to reset the User form
        if (qvr.isValidatable()) {
            String userString = SqlConverter.convertToString(uuidString, mappingRoot, cmdType);
            setUserString(mappingRoot, userString, cmdType);
        } else {
            // validate the user form
            qvr = qv.validateSql(userSqlString, cmdType, false, false);
            if (qvr.isValidatable()) {
                // This resets both the user and uuid forms on the mappingRoot
                TransformationHelper.setSqlString(mappingRoot, userSqlString, cmdType, false, null);
            } else {
                isValid = false;
            }
        }
        // If valid, revalidate mappings
        if (isValid) {
            // Reconcile the mapping root Inputs / Attributes / etc to conform to the SQL
            // (TransformationNotificationListener ignores sql Change generated by this panel)
            TransformationMappingHelper.reconcileMappingsOnSqlChange(mappingRoot, null);
        }

        return isValid;
    }

    private void clearUUIDSqlString( final SqlTransformationMappingRoot mappingRoot,
                                     final int cmdType ) {
        if (mappingRoot != null) {
            MappingHelper helper = mappingRoot.getHelper();
            if (helper != null && helper instanceof SqlTransformation) {
                SqlTransformation sqlHelper = (SqlTransformation)helper;
                switch (cmdType) {
                    case QueryValidator.SELECT_TRNS:
                        sqlHelper.setSelectSql(null);
                        break;
                    case QueryValidator.INSERT_TRNS:
                        if (sqlHelper.isInsertAllowed() && !sqlHelper.isInsertSqlDefault()) {
                            sqlHelper.setInsertSql(null);
                        }
                        break;
                    case QueryValidator.UPDATE_TRNS:
                        if (sqlHelper.isUpdateAllowed() && !sqlHelper.isUpdateSqlDefault()) {
                            sqlHelper.setUpdateSql(null);
                        }
                        break;
                    case QueryValidator.DELETE_TRNS:
                        if (sqlHelper.isDeleteAllowed() && !sqlHelper.isDeleteSqlDefault()) {
                            sqlHelper.setDeleteSql(null);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setUserString( final SqlTransformationMappingRoot mappingRoot,
                                final String userSqlStr,
                                final int cmdType ) {
        if (cmdType == QueryValidator.SELECT_TRNS) {
            TransformationHelper.setSelectSqlUserString(mappingRoot, userSqlStr, false, false, this);
        } else if (cmdType == QueryValidator.INSERT_TRNS) {
            TransformationHelper.setInsertSqlUserString(mappingRoot, userSqlStr, false, false, this);
        } else if (cmdType == QueryValidator.UPDATE_TRNS) {
            TransformationHelper.setUpdateSqlUserString(mappingRoot, userSqlStr, false, false, this);
        } else if (cmdType == QueryValidator.DELETE_TRNS) {
            TransformationHelper.setDeleteSqlUserString(mappingRoot, userSqlStr, false, false, this);
        }
    }

    public void warnUserAboutInvalidTransformations( List invalidSourceList ) {
        if (!invalidSourceList.isEmpty()) {
            String title = UiConstants.Util.getString("RevalidateModelTransformationsAction.invalidTransformationsTitle"); //$NON-NLS-1$
            String msg = UiConstants.Util.getString("RevalidateModelTransformationsAction.invalidTransformationsMessage"); //$NON-NLS-1$
            List copyOfList = new ArrayList(invalidSourceList.size());
            for (Iterator iter = invalidSourceList.iterator(); iter.hasNext();) {
                Object nextObj = iter.next();
                String path = ModelObjectUtilities.getTrimmedFullPath((EObject)nextObj);
                String name = ModelerCore.getModelEditor().getName((EObject)nextObj);
                String row = path + "/" + name; //$NON-NLS-1$
                copyOfList.add(row);
            }

            ListMessageDialog.openWarning(Display.getCurrent().getActiveShell(), title, null, msg, copyOfList, null);
        }
    }

    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        Object selectedObject = SelectionUtilities.getSelectedObject(selection);
        if (selectedObject instanceof IResource && ModelUtilities.isModelFile((IResource)selectedObject)) {
            try {
                this.modelResource = ModelUtilities.getModelResource(((IFile)selectedObject), false);
                if (ModelUtilities.isVirtual(this.modelResource)) {
                    result = true;
                }
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(e);
            }
        }
        return result;
    }

    private void invalidateSqlRootCache( List mappingRoots ) {
        Object nextObj = null;
        for (Iterator iter = mappingRoots.iterator(); iter.hasNext();) {
            nextObj = iter.next();
            if (nextObj instanceof SqlTransformationMappingRoot) {
                SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)nextObj;
                SqlMappingRootCache.invalidateSelectStatus(mappingRoot, false, this);
            }
        }
    }
}
