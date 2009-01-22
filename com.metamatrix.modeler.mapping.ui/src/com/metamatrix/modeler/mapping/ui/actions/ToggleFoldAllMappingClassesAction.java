/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramBehavior;


/**
 * ToggleFoldAllMappingClassesAction
 */
public class ToggleFoldAllMappingClassesAction extends MappingAction {

    private static String TOGGLE_UNFOLD_ALL_TOOLTIP 
        = UiConstants.Util.getString( "ToggleFoldAllMappingClassesAction.unfoldAll.tooltip" );  //$NON-NLS-1$
    private static String TOGGLE_UNFOLD_ALL_TEXT 
        = UiConstants.Util.getString( "ToggleFoldAllMappingClassesAction.unfoldAll.text" );  //$NON-NLS-1$
    private static String TOGGLE_FOLD_ALL_TEXT 
        = UiConstants.Util.getString( "ToggleFoldAllMappingClassesAction.foldAll.text" );  //$NON-NLS-1$
    private boolean logicalModel = false;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ToggleFoldAllMappingClassesAction() {
        super( UiPlugin.getDefault(), SWT.TOGGLE );

        setImageDescriptor( UiPlugin.getDefault().getImageDescriptor( UiConstants.Images.EXPAND_MAPPING_CLASS_COLUMNS ) );                        
        setToolTipText( TOGGLE_UNFOLD_ALL_TOOLTIP );
        setText( TOGGLE_UNFOLD_ALL_TEXT );
    }
    

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void setDiagramEditor( DiagramEditor editor ) {
        super.setDiagramEditor( editor );
        
        // update tooltip if logical model type
        boolean logical = ModelIdentifier.isLogicalModel(editor.getCurrentModelResource());

        if (this.logicalModel != logical) {
            this.logicalModel = logical;
            String key = (logical ? "ToggleFoldAllMappingClassesAction.logicalModel.unfoldAll.tooltip" //$NON-NLS-1$
                                  : TOGGLE_UNFOLD_ALL_TOOLTIP);
            setToolTipText(UiConstants.Util.getString(key));
            key = (logical ? "ToggleFoldAllMappingClassesAction.logicalModel.unfoldAll.text" //$NON-NLS-1$
                            : TOGGLE_UNFOLD_ALL_TEXT);
            setText(UiConstants.Util.getString(key));
        }

        // update the button state
        updateButtonState();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {

        super.selectionChanged(thePart, theSelection);
        determineEnablement();
    }
    
    
    private MappingDiagramBehavior getBehavior() {
        return MappingDiagramUtil.getCurrentMappingDiagramBehavior();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        
        // get current state
        boolean bMappingClassFoldedState = getBehavior().getDefaultMappingClassFoldedState();
               
        // reverse it
        bMappingClassFoldedState = !bMappingClassFoldedState;
//        System.out.println("{ToggleFoldAllMappingClassesAction.doRun] About to setDefaultMappingClassFoldedState to: " + bMappingClassFoldedState );
        
        getBehavior().setDefaultMappingClassFoldedState( bMappingClassFoldedState );

        // update the button
        updateButtonState();
        
        // refresh the diagram...
        editor.doRefreshDiagram();        
    }

    private void determineEnablement() {

        /*
         * jhTODO A refinement would be to disable this action when the tree is fully expanded
         */
        // always enable
        boolean enable = true;

        setEnabled(enable);
    }
    

    protected void updateButtonState() {
        boolean bMappingClassFoldedState 
            = getBehavior().getDefaultMappingClassFoldedState();
//        System.out.println("{ToggleFoldAllMappingClassesAction.updateButtonState] About to setDefaultMappingClassFoldedState to: " + bMappingClassFoldedState );
        setChecked( bMappingClassFoldedState );
        if(bMappingClassFoldedState) {
            setText( TOGGLE_FOLD_ALL_TEXT );
        } else {
            setText( TOGGLE_UNFOLD_ALL_TEXT );
        }
    }
    
}
