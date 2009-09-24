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
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramBehavior;


/**
 * ToggleSyncTreeAndDiagramExpandsAction
 */
public class ToggleSyncTreeAndDiagramExpandsAction extends MappingAction {

    private static String SYNC_TREE_AND_DIAGRAM_TOOLTIP 
        = UiConstants.Util.getString( "ToggleSyncTreeAndDiagramExpandsAction.sync.tooltip" );  //$NON-NLS-1$
    private static String SYNC_TREE_AND_DIAGRAM_TEXT 
        = UiConstants.Util.getString( "ToggleSyncTreeAndDiagramExpandsAction.sync.text" );  //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ToggleSyncTreeAndDiagramExpandsAction() {
        super( UiPlugin.getDefault(), SWT.TOGGLE );

        setImageDescriptor( UiPlugin.getDefault().getImageDescriptor( UiConstants.Images.SYNC_TREE_AND_DIAGRAM_WHEN_EXPANDING ) );                        
        setToolTipText( SYNC_TREE_AND_DIAGRAM_TOOLTIP );
        setText( SYNC_TREE_AND_DIAGRAM_TEXT );
    }
    

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void setDiagramEditor( DiagramEditor editor ) {
        super.setDiagramEditor( editor );
                
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
        boolean bSyncTreeAndDiagramState = getBehavior().getSyncTreeAndDiagramState();
               
        // reverse it
        bSyncTreeAndDiagramState = !bSyncTreeAndDiagramState;
        
        getBehavior().setSyncTreeAndDiagramState( bSyncTreeAndDiagramState );

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

        setChecked( getBehavior().getSyncTreeAndDiagramState() );
    }
    
}
