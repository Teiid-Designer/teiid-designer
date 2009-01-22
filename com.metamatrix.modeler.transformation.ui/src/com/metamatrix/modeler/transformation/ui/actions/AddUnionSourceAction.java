/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.diagram.ui.editor.DiagramToolBarManager;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.query.sql.lang.SetQuery;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * AddUnionSourceAction
 * This class required special wiring to the DiagramToolBarManager to access whether or not an ToolItem
 * was in focus (and pressed) to initiate this action.
 * 
 * The problem arised when the button was pressed when the diagram was not in focus.  This caused two
 * selection events (null, null) and a re-selection of the last selected item in the diagram.
 * These selection events were handled before the action's doRun() was being called, so the doRun() would
 * always act on the wrong item, or not at all (action disabled).
 * By asking the toolBarManager for a focusedToolItem() during the selectionChanged() method, we could enable 
 * the action based on the "old selection" from the previous part (i.e. the tree, or another diagram).
 */
public class AddUnionSourceAction extends TransformationAction {
    private static final String ACTION_DESCRIPTION_1 = "Add Union Transformation Source"; //$NON-NLS-1$
    private static final String ACTION_DESCRIPTION_MANY = "Add Union Transformation Sources"; //$NON-NLS-1$

    private static final String SQL_NOT_UPDATEABLE_TITLE = UiConstants.Util.getString("TransformationUpdateError.sqlNotResolvableDialog.title"); //$NON-NLS-1$
    private static final String SQL_NOT_UPDATEABLE_TEXT = UiConstants.Util.getString("TransformationUpdateError.sqlNotResolvableDialog.text"); //$NON-NLS-1$
    
    private static final String CANNOT_ADD_PROC_WITH_PARM_TITLE 
        = UiConstants.Util.getString("com.metamatrix.modeler.transformation.ui.actions.AddUnionSourceAction.cannotAddProcedureWithParm.title"); //$NON-NLS-1$
    private static final String CANNOT_ADD_PROC_WITH_PARM_TEXT 
        = UiConstants.Util.getString("com.metamatrix.modeler.transformation.ui.actions.AddUnionSourceAction.cannotAddProcedureWithParm.text"); //$NON-NLS-1$    

    private ActionContributionItem thisToolItem;
    
    private DiagramToolBarManager toolBarManager;

    private ISelection oldSelection;
    
    private ISelection focusedSelection;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public AddUnionSourceAction(EObject transformationEObject, Diagram diagram) {
        super(transformationEObject, diagram);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.ADD_UNION_SOURCES));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        // Save off current selection to old if part is ModelEditor and selection == null
        // This indicates a focus change and part activation.
        if (thePart != null && thePart instanceof ModelEditor) {
            oldSelection = getSelection();
        }
        
        // initialize abstract base class info
        super.selectionChanged(thePart, theSelection);
        
        setFocusedSelection();
        
        setEnabled(shouldEnable());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        
        // final check (jh defect 19361)
        if ( !canProceed() ) {
            // show msg dialog
            MessageDialog.openWarning( null, CANNOT_ADD_PROC_WITH_PARM_TITLE, CANNOT_ADD_PROC_WITH_PARM_TEXT );
            
            // bail out
            return;
        }        
        
        boolean isSingle = SelectionUtilities.isSingleSelection(focusedSelection);
        boolean requiredStart = false;
        boolean canUndo = ITransformationDiagramActionConstants.DiagramActions.UNDO_ADD_TRANSFORMATION_SOURCE;
        boolean succeeded = false;
        try {
            //start txn
            if( isSingle )
                requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION_1, this);
            else
                requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION_MANY, this);            

            // If the SQL is not modifiable, notify the user
            boolean dialogAns = true;
            if(!TransformationSqlHelper.canAddGroupToSelectSql(getTransformation())) {
                dialogAns = MessageDialog.openConfirm(null, SQL_NOT_UPDATEABLE_TITLE,SQL_NOT_UPDATEABLE_TEXT);
            }

            // If dialog OK'd, proceed with addition
            if(dialogAns) {
                // call addSource method
                if( SelectionUtilities.isSingleSelection(focusedSelection) ) {
                    TransformationSourceManager.addUnionSource(getTransformation(), 
                                                               SelectionUtilities.getSelectedEObject(focusedSelection), useUnionAll());
                } else {
                    TransformationSourceManager.addUnionSources(getTransformation(), 
                                                      SelectionUtilities.getSelectedEObjects(focusedSelection), useUnionAll());
                }
            }
            succeeded = true;            
        } finally {
            if (requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                    if( !canUndo)
                        ModelerUndoManager.getInstance().clearAllEdits();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
            
        // Reset toolBarManager, so noone else will find the focused Item.
        // This should be called because a focused item should be in the toolbar any time you 
        // select a toolbar button.
        if(toolBarManager!=null) {
            toolBarManager.resetFocusedToolItem();
        }
        
        // Need to set the focused selection back to the current selection.
        // This will happen because the toolBarManager's focused item is now null (previous call above).
        setFocusedSelection();
    }

    private boolean useUnionAll( ) {
        // Default is to UNION ALL
        boolean unionAll = true;
        EObject transMappingRoot = getTransformation();
        if(TransformationHelper.isParsableSetQuery(transMappingRoot)) {
            SetQuery setQuery = (SetQuery)TransformationHelper.getCommand(transMappingRoot, QueryValidator.SELECT_TRNS);
            return setQuery.isAll();
        }
        return unionAll;
    }
    
    /**
     * (jh defect 19361)
     * Method to determine if any conditions exist that should prevent this action.
     * @return 'true' if it is ok to execute the action.
     */
        
    private boolean canProceed() {
        boolean bResult = true;       
        
        //Procedures with and without parameters are valid sources for transformations
        
//        TransformationMappingRoot tmrRoot = null;
//        if ( getTransformation() instanceof TransformationMappingRoot ) {
//            tmrRoot = (TransformationMappingRoot)getTransformation();
//        }
//        
//        if ( tmrRoot != null && TransformationHelper.isMappingClass( tmrRoot.getTarget() ) ) {
//            
//            if ( SelectionUtilities.isSingleSelection( focusedSelection ) ) {
//                EObject selectedEO = SelectionUtilities.getSelectedEObject( focusedSelection );
//                if ( TransformationSourceManager.isProcedureWithParm( selectedEO ) ) {
//                    bResult = false;
//                }
//            } else {
//                List eObjectList = SelectionUtilities.getSelectedEObjects(focusedSelection);
//                if( !eObjectList.isEmpty() ) {
//                    Iterator it = eObjectList.iterator();
//                    
//                    while( it.hasNext() ) {
//                        EObject eoTemp = (EObject)it.next();
//                        if ( TransformationSourceManager.isProcedureWithParm( eoTemp ) ) {
//                            bResult = false;
//                            break;
//                        }                    
//                    }
//                }
//            }
//        }
        
        return bResult;
    }
    
    
    /**
     * Determine if this action should be enabled 
     * @return 'true' to enable, 'false' to disable
     */
    private boolean shouldEnable() {
        boolean enable = false;
        
        if( !isDependencyDiagram() && 
            focusedSelection != null &&
            !focusedSelection.isEmpty() &&
            getTransformation() != null ) {
            if(!editorOpenWithPendingChanges()) {
                if (SelectionUtilities.isSingleSelection(focusedSelection)) {
                    EObject singleEObj = SelectionUtilities.getSelectedEObject(focusedSelection);
                    if( singleEObj != null)
                        enable =
                            TransformationSourceManager.canAddUnionSource(getTransformation(),singleEObj,this.getPart());
                } else if (SelectionUtilities.isMultiSelection(focusedSelection) && 
                           SelectionUtilities.isAllEObjects(focusedSelection)) {
                    List eObjList = SelectionUtilities.getSelectedEObjects(focusedSelection);
                    if( !eObjList.isEmpty() )
                        enable =
                            TransformationSourceManager.canAddUnionSource(getTransformation(), eObjList,this.getPart());
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
    
    public boolean wasToolBarItemSelected() {
        if( toolBarManager != null ) {
            if( toolBarManager.getFocusedToolItem() != null && thisToolItem != null ) {
                if( thisToolItem.equals(toolBarManager.getFocusedToolItem()))
                    return true;
            }
        }
        
        return false;
    }
    
    public void setItem(ActionContributionItem aci) {
        if( toolBarManager != null ) {
            thisToolItem = aci;
        }
    }
    
    private void setFocusedSelection() {
        if( wasToolBarItemSelected() )
            focusedSelection = oldSelection;
        else
            focusedSelection = getSelection();
    }
    
    public void setToolBarManager(DiagramToolBarManager tbManager) {
        toolBarManager = tbManager;
    }
    
    /**
     * Gets a string representation of the properties of the given <code>Notification</code>.
     * @param theNotification the notification being processed
     * @return the string representation
     */
    public String getActionString() {

        return new StringBuffer()
        .append("\n       Action State ---------------------------------------------- = ") //$NON-NLS-1$
        .append("\n       oldSelection           = ").append(oldSelection) //$NON-NLS-1$
        .append("\n       focusedSelection       = ").append(focusedSelection) //$NON-NLS-1$
        .append("\n       currentSelection       = ").append(getSelection()) //$NON-NLS-1$
        .append("\n       enabled                = ").append(this.isEnabled()) //$NON-NLS-1$
        .append("\n       -----------------------------------------------------------") //$NON-NLS-1$
        .toString();
    }

}
