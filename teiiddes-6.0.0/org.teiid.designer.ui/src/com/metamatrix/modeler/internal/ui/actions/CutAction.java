/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;



import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;

import com.metamatrix.modeler.internal.ui.actions.workers.CutWorker;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.TransactionSettings;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;


/**
 * The <code>CutAction</code> class is the action that handles the global cut.
 * @since 4.0
 */
public class CutAction extends ModelObjectAction implements UiConstants {
    //============================================================================================================================
    // Fields
    

    private CutWorker worker;
    private static final String CANNOT_UNDO_TITLE = "CutAction.cannotUndoTitle"; //$NON-NLS-1$
    private static final String CANNOT_UNDO_MSG = "CutAction.cannotUndoMsg"; //$NON-NLS-1$

    
    //============================================================================================================================
    // Constructors

    /**
     * @since 4.0
     */    
    public CutAction() {
        super(UiPlugin.getDefault());
        final ISharedImages imgs = getPlugin().getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
        
        worker = new CutWorker(true);
        setActionWorker(worker);
    }
        
    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     * This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        /* 
         *      1. create the transactionsettings object
         *      2. call canCut and canUndo cut methods in the helper
         *          -- put result of these calls in the TranSettings object
         *      3. if canUndo == false,
         *          present warning dialog
         *          if user wants to continue,
         *              make sure we use the value of TxnSettings.canUndo in the
         *              canUndo arg of the startTxn method
         *      4.  if canUndo was true, return true
         *          if canUndo was false, return true if user wishes to continue
         *                                return false if user does NOT wish to continue 
         */
        TransactionSettings ts = getTransactionSettings();
        worker.setTransactionSettings( ts );
        worker.initTransactionSettings();
                
        if ( !ts.isUndoable() ) {
            String sTitle = UiConstants.Util.getString( CANNOT_UNDO_TITLE );
            String sMsg = UiConstants.Util.getString( CANNOT_UNDO_MSG );
            
            
            boolean bDoAnyway 
                = MessageDialog.openQuestion( getShell(), sTitle, sMsg );
            
            if ( !bDoAnyway ) {
                return false;
            }
        }
                
        
        if( requiresEditorForRun() ) {
            Object cachedSelection = worker.getSelection();
            if( worker.getFocusedObject() != null ) { 

                if( !ModelEditorManager.isOpen(worker.getFocusedObject()) )
                    ModelEditorManager.open(worker.getFocusedObject(), true);
            } else if( worker.getModelResource() != null ) {

                ModelEditorManager.activate(worker.getModelResource(), true);
            }
            // Reset the selection on the worker
            worker.selectionChanged(cachedSelection);
        }
        
        return true;
    }
    @Override
    protected void postRun() {

        worker.selectionChanged(getSelection());
        worker.setTransactionSettings( null );
        super.postRun();
    }
    
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
    
    
    /**<p>
     * </p>
     * @see com.metamatrix.ui.actions.AbstractAction#getSelection()
     * @since 4.0
     */
    @Override
    public ISelection getSelection() {
        if( worker.getEditorIsOpening() && worker.getTempSelection() != null )
            return worker.getTempSelection();
        
        return super.getSelection();
    }
    
    protected Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

}
