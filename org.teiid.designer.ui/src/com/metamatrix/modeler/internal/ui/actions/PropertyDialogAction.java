/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
