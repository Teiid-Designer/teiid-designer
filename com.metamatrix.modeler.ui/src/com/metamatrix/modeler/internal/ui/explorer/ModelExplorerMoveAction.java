/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.explorer;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.views.navigator.ResourceNavigatorMoveAction;

import com.metamatrix.modeler.internal.ui.refactor.actions.MoveRefactorAction;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.DelegatableAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ModelExplorerRenameAction is a specialization of ResourceNavigatorRenameAction that also
 * handles in-line renaming of EObjects.  In addition, it prevents renaming of ModelResources
 * that are open in a ModelEditor.
 */
public class ModelExplorerMoveAction extends ResourceNavigatorMoveAction implements UiConstants {
                                                                                     
    //============================================================================================================================
    // Constants
        
//    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelExplorerRenameAction.class);
        
    private String sMoveLabel 
        = UiConstants.Util.getString( "com.metamatrix.modeler.internal.ui.refactor.actions.MoveRefactorAction.text" ); //$NON-NLS-1$
    private String sMoveTooltip 
        = UiConstants.Util.getString( "com.metamatrix.modeler.internal.ui.refactor.actions.MoveRefactorAction.toolTip" ); //$NON-NLS-1$
 
    //============================================================================================================================
    // Static Methods
        
    /**<p>
     * </p>
     * @since 4.0
     */
        
    //============================================================================================================================
    // Variables
//    private MoveRefactorAction actMove;    
    private IActionDelegate delMove;
    private IWorkbenchWindow window;
    private DelegatableAction actMove;    
    
    /**
     * Construct an instance of ModelExplorerRenameAction.
     * @param shell
     * @param treeViewer
     */
    public ModelExplorerMoveAction(Shell shell, TreeViewer treeViewer) {
        super(shell, treeViewer);
        window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

    }
    
    /* Overridden to handle EObjects and model files.
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        Object selection = getStructuredSelection().getFirstElement();
        if ( selection instanceof IResource ) {
            // set the selection on the TreeViewerRenameAction and run it
         
            getMoveActionDelegate().selectionChanged( actMove, getStructuredSelection() );
            
            getMoveActionDelegate().run( actMove );                
        } 
    }
    
    private IActionDelegate getMoveActionDelegate() {
       // move
        if ( actMove == null ) {
            delMove = new MoveRefactorAction();       
            
            actMove = new DelegatableAction( delMove, window );
            
            actMove.setText( sMoveLabel );
            actMove.setToolTipText( sMoveTooltip );
        }
        return delMove;      
    }
    
    
    /* (non-Javadoc)
     * jhTODO: reimplement this method so that it delegates to the refactor rename action also
     * @see org.eclipse.ui.actions.SelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection(IStructuredSelection selection) {
        boolean bResult = false;

        if ( selection.size() > 1 )
            return false;
    
        List lstResourceObjects = SelectionUtilities.getSelectedIResourceObjects( selection );
        if ( lstResourceObjects.size() > 0 ) {
//            IResource res = (IResource)lstResourceObjects.get( 0 );
                                
            getMoveActionDelegate().selectionChanged( actMove, selection );
            bResult = actMove.isEnabled();
           
        }
        return bResult;
   }
    
    // ====================================================
    // ISelectionProvider methods
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return getStructuredSelection();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        throw new RuntimeException("ModelExplorerRenameAction.addSelectionChangedListener is not supported");   //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) { 
        throw new RuntimeException("ModelExplorerRenameAction.removeSelectionChangedListener is not supported");   //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        throw new RuntimeException("ModelExplorerRenameAction.setSelection is not supported");   //$NON-NLS-1$
    }

}
