/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import java.util.EventObject;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.core.designer.event.EventObjectListener;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.query.QueryValidationResult;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.QueryEditorStatusEvent;
import org.teiid.designer.transformation.ui.editors.TransformationObjectEditorPage;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlEditorPanel;
import org.teiid.designer.transformation.ui.reconciler.ReconcilerDialog;
import org.teiid.designer.transformation.util.SqlMappingRootCache;
import org.teiid.designer.transformation.util.SqlTransformationStatusChangeEvent;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.TransformationMappingHelper;
import org.teiid.designer.transformation.validation.SqlTransformationResult;
import org.teiid.designer.transformation.validation.TransformationValidator;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.undo.ModelerUndoManager;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.ui.sqleditor.component.QueryDisplayNode;


/**
 * The <code>ReconcileSql</code> class is the action that handles the global copy.
 * 
 * @since 8.0
 */
public class ReconcileTransformationAction extends TransformationAction implements EventObjectListener {

    private static final String DIALOG_TITLE_TEXT = UiConstants.Util.getString("ReconcilerDialog.title.text"); //$NON-NLS-1$
    private static final String SUBQUERY_RECONCILE_TITLE_TEXT = UiConstants.Util.getString("ReconcilerDialog.subQueryReconcile.title.text"); //$NON-NLS-1$
    private static final String SUBQUERY_RECONCILE_MESSAGE_TEXT = UiConstants.Util.getString("ReconcilerDialog.subQueryMessage.text"); //$NON-NLS-1$

    TransformationObjectEditorPage tObjEditorPage = null;
    boolean txnOK = true;

    public ReconcileTransformationAction( EObject transformationEObject,
                                          Diagram diagram ) {
        super(transformationEObject, diagram);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.RECONCILER_ICON));
        SqlMappingRootCache.addEventListener(this);
    }

    public void setTransObjectEditorPage( TransformationObjectEditorPage toePage ) {
        if (toePage != null) {
            this.tObjEditorPage = toePage;
            this.tObjEditorPage.addEventListener(this);
            setEnabled(shouldEnable(toePage.getCurrentMappingRoot()));
        }
    }

    /**
     * Override default method - enable based on the state of the SQL
     * 
     * @param transformationEObject the transformation object
     */
    @Override
    public void setTransformation( EObject transformationEObject ) {
        super.setTransformation(transformationEObject);
        boolean enable = false;

        if (!isDependencyDiagram()) {
            // Check the current transformation
            EObject transMappingRoot = getTransformation();
            if (transMappingRoot != null && TransformationHelper.isTransformationMappingRoot(transMappingRoot)) {
                enable = shouldEnable(transMappingRoot);
            }
        }

        setEnabled(enable);
    }
    
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        if( this.tObjEditorPage != null ) {
        	setEnabled(shouldEnable(this.tObjEditorPage.getCurrentMappingRoot()));
        }
    }

    /**
     * handler for SqlTransformationStatusChangeEvents
     * 
     * @param e the eventObject
     */
    @Override
	public void processEvent( EventObject e ) {
        // ----------------------------------------------------------------------
        // respond to SqlTransformationStatusChangeEvent for the current
        // Transformation. This event is fired whenever the SQL for a
        // Transformation changes.
        // ----------------------------------------------------------------------
        if (e instanceof SqlTransformationStatusChangeEvent) {

            boolean enable = false;
            // MappingRoot on which the sql changed
            Object eventMappingRoot = ((SqlTransformationStatusChangeEvent)e).getMappingRoot();
            // If sql has changed on actions transformation, see if we should enable
            if (eventMappingRoot != null && eventMappingRoot.equals(getTransformation())) {
                enable = shouldEnable(getTransformation());
                setEnabled(enable);
            }
        } else if (e instanceof QueryEditorStatusEvent) {
            setEnabled(shouldEnable(getTransformation()));
        }
    }

    /* 
     * determine whether the ReconcileTransformationAction should be enabled
     * @param transMappingRoot the transformation mappingRoot
     * @return 'true' if the action should be enabled, 'false' if not.
     */
    private boolean shouldEnable( EObject transMappingRoot ) {
        boolean shouldEnable = false;

        // Determine based on the current transformation if we can enable
        if (rootIsValid(transMappingRoot)) {
            boolean canUseReconciler = true; //TransformationHelper.canUseReconciler(transMappingRoot);
            boolean isReadOnly = ModelObjectUtilities.isReadOnly(transMappingRoot);
            if (canUseReconciler && !isReadOnly) {
                shouldEnable = true;
            }
        }
        // If transformation can be reconciled, check Editor - is there any other reason we should disable
        if (shouldEnable) {
            // Assume current tab is select, check editor if set
            boolean currentTabIsSelect = true;
            boolean hasPendingChanges = false;
            if (this.tObjEditorPage != null) {
                if (!tObjEditorPage.isCurrentTabSelect()) {
                    currentTabIsSelect = false;
                } else {
                    hasPendingChanges = tObjEditorPage.hasPendingChanges();
                }
            }
            // Disable if different tab is selected
            if (!currentTabIsSelect || hasPendingChanges) {
                shouldEnable = false;
                // do further checking if this is a union query
            } else {
                if (this.tObjEditorPage != null) {
                    SqlEditorPanel sePanel = tObjEditorPage.getCurrentSqlEditor();
                    boolean isUnion = sePanel.isCommandUnion();
                    if (isUnion) {
                        Command command = sePanel.getCommand();
                        int uIndex = sePanel.getCurrentUnionCommandSegmentIndex();
                        // index -1 : consider the entire command, make sure its resolvable
                        if (uIndex == -1) {
                            if (!command.isResolved()) {
                                shouldEnable = false;
                            }
                        } else {
                            QueryDisplayNode qdn = (QueryDisplayNode)sePanel.getCurrentCommandDisplayNode();
                            command = (Query)qdn.getLanguageObject();
                            // If command is not resolved, attempt to resolve it.
                            if (!command.isResolved()) {
                                QueryValidator validator = new TransformationValidator(
                                                                                       (SqlTransformationMappingRoot)transMappingRoot);
                                QueryValidationResult result = validator.validateSql(command.toString(),
                                                                                     QueryValidator.SELECT_TRNS,
                                                                                     false);
                                if (!result.isResolvable()) {
                                    shouldEnable = false;
                                }
                            }
                        }
                    } else {
                        Command command = sePanel.getCommand();
                        if (command != null && !command.isResolved()) {
                            shouldEnable = false;
                        }
                    }
                }
            }
        }

        return shouldEnable;
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    protected void doRun() {
        Shell shell = super.getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell();
        
        // Check for Dirty Model and if can use reconciler
        if( this.tObjEditorPage == null ) return;
        
        if( this.tObjEditorPage.getParentModelEditor().isResourceDirty() ) {
        	MessageDialog.openWarning(shell, 
        			UiConstants.Util.getString("ReconcileTransformationAction.pendingChangesTitle"),  //$NON-NLS-1$
        			UiConstants.Util.getString("ReconcileTransformationAction.pendingChangesMessage")); //$NON-NLS-1$
        	return;
        }
        
        int subIndex = -1;
        List builderGroups = null;
        boolean isUnion = false;
        SqlEditorPanel sePanel = null;

        // If T-editor is closed, this action may still be on the diagram toolbar
        // So we need to check if the mapping root was set to NULL on deactivate()
        // as well as if the page has not been created yet.
        if (tObjEditorPage == null || tObjEditorPage.getCurrentMappingRoot() == null) {
            // Need to open editor page so we can get the builder groups from the basic query
            ModelEditorManager.edit(getTransformation());
        }

        if (tObjEditorPage != null) {
        	// Check for null or EMPTY SQL
        	
            if( this.tObjEditorPage.getCurrentSqlEditor().getText() == null || this.tObjEditorPage.getCurrentSqlEditor().getText().trim().length() == 0 ) {
            	MessageDialog.openWarning(shell, 
            			UiConstants.Util.getString("ReconcileTransformationAction.emptySQLTitle"),  //$NON-NLS-1$
            			UiConstants.Util.getString("ReconcileTransformationAction.emptySQLMessage")); //$NON-NLS-1$
            	return;
            }
        	
        	// Check Parsable Status
            SqlTransformationResult existingStatus = SqlMappingRootCache.getSqlTransformationStatus(this.tObjEditorPage.getCurrentMappingRoot(), QueryValidator.SELECT_TRNS, true, null);
            if( existingStatus != null ) {
            	if( !existingStatus.isParsable()) {
            		MessageDialog.openWarning(shell, 
                			UiConstants.Util.getString("ReconcileTransformationAction.sqlNotParsableTitle"),  //$NON-NLS-1$
                			UiConstants.Util.getString("ReconcileTransformationAction.sqlNotParsableMessage")); //$NON-NLS-1$
                	return;
            	}
            }
        	
            sePanel = tObjEditorPage.getCurrentSqlEditor();
            isUnion = sePanel.isCommandUnion();

            boolean isSubQuerySelected = sePanel.isSubQuerySelected();
            if (isSubQuerySelected) {
                // User is trying to reconcile a subquery and we don't want to go there... so pop up a Warning dialog
                // to tell the user to put cursor in primary query and reconcile.
                MessageDialog.openWarning(shell, SUBQUERY_RECONCILE_TITLE_TEXT, SUBQUERY_RECONCILE_MESSAGE_TEXT);
                return;
            }

            if (isUnion) {
                subIndex = sePanel.getCurrentUnionCommandSegmentIndex();
            }

            builderGroups = sePanel.getGroupsForBuilderTree(true);
        }
        // --------------------------------------------------
        // Create the Reconciler Dialog and Open it
        // --------------------------------------------------
        ReconcilerDialog reconcilerDialog = new ReconcilerDialog(shell, getTransformation(), DIALOG_TITLE_TEXT, isUnion,
                                                                 subIndex, builderGroups);
        int returnStatus = reconcilerDialog.open();

        // -------------------------------------------------------------------------
        // If the Dialog is OK'd and it has Modifications, do the updates
        // -------------------------------------------------------------------------
        if ((returnStatus == Window.OK) && reconcilerDialog.hasValidModifications()) {
            final boolean canUndo = ITransformationDiagramActionConstants.DiagramActions.UNDO_RECONCILE_TRANSFORMATION;

            final int index = subIndex;
            final ReconcilerDialog dialog = reconcilerDialog;
            txnOK = true;

            if (reconcilerDialog.hasPreModifications()) {
                final boolean requiredStartPre = ModelerCore.startTxn(true, canUndo, "Reconcile Transformation", this); //$NON-NLS-1$

                UiBusyIndicator.showWhile(UiUtil.getWorkbenchShellOnlyIfUiThread().getDisplay(), new Runnable() {
                    @Override
					public void run() {
                        boolean succeeded = false;

                        try {
                            // Apply all of the Modifications
                            dialog.applyPreModifications(ModelerCore.getCurrentUoW().getSource());
                            succeeded = true;
                        } finally {
                            // If we start txn, commit it
                            if (requiredStartPre) {
                                if (succeeded) {
                                    ModelerCore.commitTxn();
                                    tObjEditorPage.refreshEditorContent();
                                    if (!canUndo) {
                                        ModelerUndoManager.getInstance().clearAllEdits();
                                    }
                                } else {
                                    ModelerCore.rollbackTxn();
                                    txnOK = false;
                                }
                            }
                            TransformationMappingHelper.setCreateTargetAttributes(true);
                        }
                    }
                });

            }

            if (txnOK) {
                final boolean requiredStart = ModelerCore.startTxn(true, canUndo, "Reconcile Transformation", this); //$NON-NLS-1$
                // show busy cursor while applying modifications and committing transaction
                UiBusyIndicator.showWhile(UiUtil.getWorkbenchShellOnlyIfUiThread().getDisplay(), new Runnable() {
                    @Override
					public void run() {
                        boolean succeeded = false;

                        try {
                            // Apply all of the Modifications
                            dialog.applyAllModifications(index, ModelerCore.getCurrentUoW().getSource());
                            succeeded = true;
                        } finally {
                            // If we start txn, commit it
                            if (requiredStart) {
                                if (succeeded) {
                                    ModelerCore.commitTxn();
                                    tObjEditorPage.refreshEditorContent();
                                    if (!canUndo) {
                                        ModelerUndoManager.getInstance().clearAllEdits();
                                    }
                                } else {
                                    ModelerCore.rollbackTxn();
                                }
                            }
                            TransformationMappingHelper.setCreateTargetAttributes(true);
                        }
                    }
                });
            }
        }
    }

    private boolean rootIsValid( EObject transMappingRoot ) {
        if (transMappingRoot != null && transMappingRoot.eResource() != null) return true;

        return false;
    }

    @Override
    public void dispose() {
        SqlMappingRootCache.removeEventListener(this);
        super.dispose();
    }
}
