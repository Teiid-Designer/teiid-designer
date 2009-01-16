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

package com.metamatrix.modeler.internal.ui.actions;



import com.metamatrix.modeler.internal.ui.actions.workers.CopyNameWorker;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * The <code>CopyNameAction</code> class is the action copies the short name to the clipboard.
 * @since 4.0
 */
public class CopyNameAction extends ModelObjectAction {

    //============================================================================================================================
    // Constants

    //============================================================================================================================
    // Fields
    private CopyNameWorker worker;

    
    //============================================================================================================================
    // Constructors
    
    public CopyNameAction() {
        super( UiPlugin.getDefault() );
        
        // This action will copy just the name to the clipboard.  Init worker to do that. 
        worker = new CopyNameWorker( true, CopyNameWorker.SHOW_JUST_NAME );
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
