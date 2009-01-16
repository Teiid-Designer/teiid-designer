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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * PrintAction
 */
public class PrintAction extends AbstractAction {

    /*
     * This is the Modeler's default print action.  Real implementations 
     * must be contributed so as to replace this one.  That is why this
     * one defaults to disabled.
     */

    // ====================================================================
    // CONSTRUCTORS
    // ====================================================================
    
    public PrintAction() {
        super(UiPlugin.getDefault());
 
    }
    
    // ====================================================================
    // METHODS
    // ====================================================================
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
                                     
        super.selectionChanged(thePart, theSelection);
        setEnabled( false );
    }

	@Override
    protected void doRun() {
//        System.out.println("[PrintAction.doRun] TOP"); //$NON-NLS-1$
	}
}
