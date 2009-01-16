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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.actions.ActionFactory;

import com.metamatrix.modeler.ui.UiConstants;

/**
 * PropertyDialogAction
 */
public class PropertyDialogAction extends org.eclipse.ui.dialogs.PropertyDialogAction
                                  implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public PropertyDialogAction(Control theControl,
                                ISelectionProvider theSelectionProvider) {
        super(new SameShellProvider(theControl), theSelectionProvider);
        setId(ActionFactory.PROPERTIES.getId());
        setText(Util.getString("PropertyDialogAction.text")); //$NON-NLS-1$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.PropertyDialogAction#selectionChanged(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void selectionChanged(IStructuredSelection theSelection) {
        IStructuredSelection selection = null;
        
        if (theSelection.isEmpty() ||
            (!theSelection.isEmpty() && (theSelection.getFirstElement() instanceof IResource))) {
            selection = theSelection;
        } else {
            selection = StructuredSelection.EMPTY;
        }

        super.selectionChanged(selection);
    }
    
}
