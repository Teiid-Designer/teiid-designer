/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.ui.actions;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * AbstractAction
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
