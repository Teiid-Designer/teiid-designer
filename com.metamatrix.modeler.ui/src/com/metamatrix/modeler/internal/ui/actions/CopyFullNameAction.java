/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;



import com.metamatrix.modeler.internal.ui.actions.workers.CopyNameWorker;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * The <code>CopyFullNameAction</code> class is the action copies the full name to the clipboard.
 * @since 4.0
 */
public class CopyFullNameAction extends ModelObjectAction {

    //============================================================================================================================
    // Constants

    //============================================================================================================================
    // Fields
    private CopyNameWorker worker;
    
    //============================================================================================================================
    // Constructors
    
    public CopyFullNameAction() {
        super( UiPlugin.getDefault() );
        
        // This action will copy the fully qualified name to the clipboard. 
        worker = new CopyNameWorker( true, CopyNameWorker.SHOW_FULLY_QUALIFIED_NAME );
        setActionWorker( worker );
    }
    
    //============================================================================================================================
    // Methods
        
    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     * This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return false;
    }
}
