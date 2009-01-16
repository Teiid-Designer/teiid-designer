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

package com.metamatrix.query.ui.actions;

//import org.eclipse.jface.action.IAction;
//import org.eclipse.ui.IFileEditorInput;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;

import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.actions.AbstractActionService;

/**
 * The <code>QueryActionService</code> class is the Modeler Plugin's action service. It is responsible for
 * managing all actions for this plugin.
 */
public final class QueryActionService extends AbstractActionService 
                                      implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>QueryActionService</code> associated with the given <code>IWorkbenchWindow</code>.
     * @param theWindow the associated workbench window
     */
    public QueryActionService(IWorkbenchPage page) {
        super(UiPlugin.getDefault(), page);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractActionService#getDefaultAction(java.lang.String)
     */
    public IAction getDefaultAction(String theActionId) {
        return null;
    }

    
}
