/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.TransformationObjectEditorPage;
import org.teiid.designer.transformation.util.TransformationSqlHelper;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelObjectEditorPage;
import org.teiid.designer.ui.editors.MultiPageModelEditor;

/**
 * RemoveTransformationSource
 *
 * @since 8.0
 */
public class RemoveTransformationSourceAction extends TransformationAction {
    private static final String ACTION_DESCRIPTION_1 = UiConstants.Util.getString("RemoveTransformationSourceAction.actionTitleSingle"); //$NON-NLS-1$
    private static final String ACTION_DESCRIPTION_MANY = "Remove Transformation Sources"; //$NON-NLS-1$
    
    private static final String CONFIRM_TITLE = UiConstants.Util.getString("RemoveTransformationSourceAction.confirmTitle"); //$NON-NLS-1$    
    private static final String CONFIRM_MULTIPLE = UiConstants.Util.getString("RemoveTransformationSourceAction.confirmMultiple"); //$NON-NLS-1$
    
    private static final String SQL_NOT_UPDATEABLE_TITLE = UiConstants.Util.getString("TransformationUpdateError.sqlNotResolvableDialog.title"); //$NON-NLS-1$
    private static final String SQL_NOT_UPDATEABLE_TEXT = UiConstants.Util.getString("TransformationUpdateError.sqlNotResolvableDialog.text"); //$NON-NLS-1$

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public RemoveTransformationSourceAction(EObject transformationEObject, Diagram diagram) {
        super(transformationEObject, diagram);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.REMOVE_SOURCES));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        setEnabled(shouldEnable());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        boolean isSingle = SelectionUtilities.isSingleSelection(getSelection());
        
        boolean userContinue = false;
        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        if ( isSingle ) {
            EObject obj = SelectionUtilities.getSelectedEObject(getSelection());
            String name = ModelerCore.getModelEditor().getName(obj); 
            String message = UiConstants.Util.getString("RemoveTransformationSourceAction.confirmSingle", name); //$NON-NLS-1$ 
            userContinue = MessageDialog.openConfirm(shell, CONFIRM_TITLE, message);
        } else {
            userContinue = MessageDialog.openConfirm(shell, CONFIRM_TITLE, CONFIRM_MULTIPLE);
        }
        
        if ( userContinue ) {

            //start txn
            boolean requiredStart = false;
            boolean canUndo = ITransformationDiagramActionConstants.DiagramActions.UNDO_REMOVE_TRANSFORMATION_SOURCE;
            boolean succeeded = false;
            try {
                if( isSingle )
                    requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION_1, this);
                else
                    requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION_MANY, this);

                // If the SQL is not modifiable, notify the user
                boolean dialogAns = true;
                if(!TransformationSqlHelper.canRemoveGroupFromSelectSql(getTransformation())) {
                    dialogAns = MessageDialog.openConfirm(null, SQL_NOT_UPDATEABLE_TITLE,SQL_NOT_UPDATEABLE_TEXT);
                }
                
                // If dialog OK'd, proceed with source removal
                if(dialogAns) {
                    // call removeSource method
                    if( SelectionUtilities.isSingleSelection(getSelection()) )
                        TransformationSourceManager.removeSource(getTransformation(), SelectionUtilities.getSelectedEObject(getSelection()));
                    else
                        TransformationSourceManager.removeSources(getTransformation(), SelectionUtilities.getSelectedEObjects(getSelection()));
                }
                
                succeeded = true;           
            } finally {
                if (requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
        
               setEnabled(false);
            }

        }
        
    }
    
    /**
     * Determine if this action should be enabled 
     * @param theSelection the current selection
     * @return 'true' to enable, 'false' to disable
     */
    private boolean shouldEnable() {
        boolean enable = false;
        
        if( !isDependencyDiagram() && areEObjectsSelected() && !editorOpenWithPendingChanges() && getTransformation() != null ) {
            ISelection sel = getSelection();
            if( sel != null && getTransformation() != null && ! sel.isEmpty() ) {
                if (isEObjectSelected()) {
                    enable = TransformationSourceManager.canRemove(getTransformation(), SelectionUtilities.getSelectedEObject(sel));
                } else if (areEObjectsSelected()) {
                    enable = TransformationSourceManager.canRemove(getTransformation(), SelectionUtilities.getSelectedEObjects(sel));
                }
            }
        }
        
        return enable;
    }
    
    /**
     * Determine if there is a transformation editor open, and if it has pending changes. 
     * @return 'true' if transformation editor is open and it has pending changes.
     */
    private boolean editorOpenWithPendingChanges() {
        boolean openWithPending = false;
        TransformationObjectEditorPage toep = getTransObjectEditorPage();
        if(toep!=null && toep.hasPendingChanges()) {
            openWithPending = true;
        }
        return openWithPending;
    }

    /**
     * Get the currently active TransformationObjectEditorPage, null if not open. 
     * @return the active transformation editor, null if not active
     */
    private TransformationObjectEditorPage getTransObjectEditorPage() {
        TransformationObjectEditorPage transOEP = null;
        //
        // Get the currently active ModelObjectEditorPage, ensure its TransformationObjEditorPage
        //
        IEditorPart editor = 
            UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().getActiveEditor();
        if(editor!=null && editor instanceof MultiPageModelEditor) {
            ModelObjectEditorPage moep = ((MultiPageModelEditor)editor).getActiveObjectEditor();
            if(moep!=null && moep instanceof TransformationObjectEditorPage) {
                transOEP = (TransformationObjectEditorPage)moep;
            }
        }
        return transOEP;
    }
}
