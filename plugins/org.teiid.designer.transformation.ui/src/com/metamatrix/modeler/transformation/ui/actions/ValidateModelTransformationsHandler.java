package com.metamatrix.modeler.transformation.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.handlers.HandlerUtil;

import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

public final class ValidateModelTransformationsHandler extends AbstractHandler {

    private void clearUUIDSqlString( SqlTransformationMappingRoot mappingRoot ) {
        if (mappingRoot != null) {
            MappingHelper helper = mappingRoot.getHelper();

            if ((helper != null) && (helper instanceof SqlTransformation)) {
                SqlTransformation sqlHelper = (SqlTransformation)helper;

                if (sqlHelper.isInsertAllowed() && !sqlHelper.isInsertSqlDefault()) {
                    sqlHelper.setInsertSql(null);
                }

                if (sqlHelper.isUpdateAllowed() && !sqlHelper.isUpdateSqlDefault()) {
                    sqlHelper.setUpdateSql(null);
                }

                if (sqlHelper.isDeleteAllowed() && !sqlHelper.isDeleteSqlDefault()) {
                    sqlHelper.setDeleteSql(null);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        IEditorPart editor = HandlerUtil.getActiveEditor(event);

        // get the currently active ModelObjectEditorPage, ensure its TransformationObjEditorPage
        if (editor instanceof MultiPageModelEditor) {
            ModelObjectEditorPage editorPage = ((MultiPageModelEditor)editor).getActiveObjectEditor();

            if ((editorPage != null) && (editorPage instanceof TransformationObjectEditorPage)) {
                ((MultiPageModelEditor)editor).closeObjectEditor();
            }
        }

        // the one selected object will be an IFile of a virtual model because of how Command extension is implemented 
        IStructuredSelection selection = (IStructuredSelection)HandlerUtil.getCurrentSelection(event);
        IFile virtualModelFile = (IFile)selection.getFirstElement();
        ModelResource virtualModel = null;

        try {
            ModelResource modelResource = ModelUtil.getModelResource(virtualModelFile, false);

            if (ModelUtilities.isVirtual(modelResource)) {
                virtualModel = modelResource;
            }
        } catch (ModelWorkspaceException e) {
            UiConstants.Util.log(e);
        }

        if (virtualModel != null) {
            // force the modelEditor open so that changes mark the resource dirty
            IResource resource = virtualModel.getResource();
            ModelEditorManager.getModelEditorForFile((IFile)resource, true);

            final ModelResource viewModel = virtualModel;
            final List[] brokenTables = new ArrayList[1];

            final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
                @Override
                public void execute( IProgressMonitor theMonitor ) {
                    brokenTables[0] = revalidate(viewModel);
                    theMonitor.done();
                }
            };

            // run operation
            Shell shell = HandlerUtil.getActiveShell(event);

            try {
                new ProgressMonitorDialog(shell).run(true, true, op);
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                UiConstants.Util.log(e.getTargetException());
            }

            warnUserAboutInvalidTransformations(shell, brokenTables[0]);
        }

        return null; // per javadoc
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

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    private boolean resetTransformation( SqlTransformationMappingRoot mappingRoot,
                                         int cmdType,
                                         QueryValidator qv ) {
        String userSqlString = TransformationHelper.getSqlString(mappingRoot, cmdType);
        setUserString(mappingRoot, userSqlString, cmdType);
        TransformationMappingHelper.reconcileMappingsOnSqlChange(mappingRoot, null);
        QueryValidationResult result = qv.validateSql(userSqlString, cmdType, false);

        return result.isParsable() && result.isResolvable() && result.isValidatable();
    }

    List revalidate( ModelResource virtualModel ) {
        // Keep list of problems
        List brokenSqlTables = new ArrayList();

        if (virtualModel != null) {
            // shut off transformation notifications
            UiPlugin.getDefault().setIgnoreTransformationNotifications(true);

            // start txn
            boolean requiredStart = ModelerCore.startTxn(true, false, "Revalidate Transformations", this); //$NON-NLS-1$
            boolean succeeded = false;

            try {
                Object nextObj = null;
                List transformations = virtualModel.getModelTransformations().getTransformations();

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
                            clearUUIDSqlString(mappingRoot);
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

    private void setUserString( SqlTransformationMappingRoot mappingRoot,
                                String userSqlStr,
                                int cmdType ) {
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

    private void warnUserAboutInvalidTransformations( Shell shell,
                                                      List invalidSourceList ) {
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

            ListMessageDialog.openWarning(shell, title, null, msg, copyOfList, null);
        }
    }

}
