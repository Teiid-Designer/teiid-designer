/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.AbstractAction;
import org.teiid.designer.ui.editors.ModelEditor;


/**
 * AbstractAction
 *
 * @since 8.0
 */
public abstract class AbstractModelerAction extends AbstractAction {
    
    protected TransactionSettings txnSettings;

    public AbstractModelerAction(AbstractUiPlugin thePlugin, int theStyle) {
        super(thePlugin, theStyle);
    }

    public AbstractModelerAction(AbstractUiPlugin thePlugin) {
        super(thePlugin);
    }

    protected ModelEditor getActiveEditor() {
        IWorkbenchPage page = getPlugin().getCurrentWorkbenchWindow().getActivePage();
        // see if active page is available:
        if (page == null) {
            // not available, see if we have any reference to a page:
            page = AbstractUiPlugin.getLastValidPage();
            
            if (page == null) {
                // still no page; exit:
                return null;
            } // endif
        } // endif
    
        IEditorPart editor = page.getActiveEditor();
    
        if (editor instanceof ModelEditor) {
            return (ModelEditor) editor;                   
        }
        return null;
    }
    
    protected TransactionSettings getTransactionSettings() {
        if ( txnSettings == null ) {
            txnSettings = new TransactionSettings();    
        }
        return txnSettings;
    }
    
    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     */
    @Override
    protected boolean preRun() {
        /*
         * overriding preRun here in AbstractModelerAction to make it return a
         * TransactionSettings object.  But do we wish to change the interface,
         * Or just create a second preRun to which we will delegate?
         */
        
        
        return getTransactionSettings().doTransaction();
    }
    

    @Override
    protected void postRun() {
        txnSettings = null;    
    }
}
