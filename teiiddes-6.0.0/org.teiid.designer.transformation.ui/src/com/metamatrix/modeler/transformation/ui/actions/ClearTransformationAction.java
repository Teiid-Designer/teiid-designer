/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.EventObject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityManager;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.SqlTransformationStatusChangeEvent;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;

/**
 * ClearTransformation
 */
public class ClearTransformationAction extends TransformationAction implements EventObjectListener,UiConstants {
    private static final String ACTION_DESCRIPTION = "Clear Transformation"; //$NON-NLS-1$
    private static final String REMOVE_ATTRIBUTES_TITLE = UiConstants.Util.getString("ClearTransformationAction.deleteAttributesTitle"); //$NON-NLS-1$
    private static final String CONFIRM_TITLE = UiConstants.Util.getString("ClearTransformationAction.confirmTitle"); //$NON-NLS-1$
    private static final String CONFIRM_MESSAGE = UiConstants.Util.getString("ClearTransformationAction.confirmMessage"); //$NON-NLS-1$


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ClearTransformationAction(EObject transformationEObject, Diagram diagram) {
        super(transformationEObject, diagram);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.CLEAR_TRANSFORMATION));
        SqlMappingRootCache.addEventListener( this );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Override default method - enable based on the state of the SQL
     * @param transformationEObject the transformation object
     */
    @Override
    public void setTransformation(EObject transformationEObject) {
        super.setTransformation(transformationEObject);
        boolean enable = false;
        
        if( !isDependencyDiagram()) {
            // Check the current transformation
            EObject transMappingRoot = getTransformation();
            if(transMappingRoot!=null && TransformationHelper.isTransformationMappingRoot(transMappingRoot)) {
                enable = shouldEnable(transMappingRoot);
            }
        }
        
        setEnabled(enable);
    }

    /* 
     * determine whether the ReconcileTransformationAction should be enabled
     * @param transMappingRoot the transformation mappingRoot
     * @return 'true' if the action should be enabled, 'false' if not.
     */
    private boolean shouldEnable(EObject transMappingRoot) {
        return (rootIsValid(transMappingRoot) && TransformationSourceManager.canClear(transMappingRoot));
    }

    /**
     * handler for SqlTransformationStatusChangeEvents
     * @param e the eventObject
     */
    public void processEvent(EventObject e) {
        //----------------------------------------------------------------------
        // respond to SqlTransformationStatusChangeEvent for the current 
        // Transformation.  This event is fired whenever the SQL for a 
        // Transformation changes.
        //----------------------------------------------------------------------
        if (e instanceof SqlTransformationStatusChangeEvent) {
            boolean enable=false;
            // MappingRoot on which the sql changed
            Object eventMappingRoot = ((SqlTransformationStatusChangeEvent)e).getMappingRoot();
            // If sql has changed on actions transformation, see if we should enable
            if(eventMappingRoot!=null && eventMappingRoot.equals(getTransformation())) {
                enable = shouldEnable(getTransformation());
                setEnabled(enable);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        boolean userContinue = MessageDialog.openConfirm(shell, CONFIRM_TITLE, CONFIRM_MESSAGE);
        if ( userContinue ) {
            // Defect 21479 Fix
            boolean objectEditorOpen = false;
            ModelEditor activeEditor = getActiveEditor();
            if( activeEditor != null && activeEditor.getActiveObjectEditor() != null ) {
                objectEditorOpen = true;
            }
            
            
            boolean canUndo = ITransformationDiagramActionConstants.DiagramActions.UNDO_CLEAR_TRANSFORMATION;
            //start txn
            boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
            boolean succeeded = false;
            try {
 
                EObject targetEObject = ((TransformationMappingRoot)getTransformation()).getTarget();
                boolean removeAttributes = false;
                if ( !TransformationHelper.isMappingClass(targetEObject) || TransformationHelper.isStagingTable(targetEObject) ) {
                    // Check to see if there ARE attributes
                    if( targetEObject.eContents().size() > 0) {
                        // Ask the user to remove all attributes in virtual table.
                        String message = UiConstants.Util.getString(
                                                "ClearTransformationAction.deleteAttributesMessage", //$NON-NLS-1$
                                                ModelerCore.getModelEditor().getName(targetEObject));  
                        // Prompt whether to remove the Group Elements from the query
                        removeAttributes = MessageDialog.openQuestion(shell, REMOVE_ATTRIBUTES_TITLE, message);
                    }
                }
                // Defect 21479 Fix
                // Let's close the Editor first so it doesn't react to the clearing and do anymore transaction work.
                if( objectEditorOpen ) {
                    activeEditor.closeObjectEditor();
                }
                
                TransformationSourceManager.clear(getTransformation(), removeAttributes);
                DiagramEntityManager.cleanUpDiagram(getCurrentDiagram());

                succeeded = true;
            } finally {
                if (requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                        if( !canUndo) {
                            ModelerUndoManager.getInstance().clearAllEdits();
                        }
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
                setEnabled(shouldEnable(getTransformation()));
            }
        }
            
    }
    
    private boolean rootIsValid(EObject transMappingRoot) {
        if( transMappingRoot != null && transMappingRoot.eResource() != null )
            return true;
        
        return false;
    }

    @Override
    public void dispose() {
        SqlMappingRootCache.removeEventListener( this );
        super.dispose();
    }
    
}
