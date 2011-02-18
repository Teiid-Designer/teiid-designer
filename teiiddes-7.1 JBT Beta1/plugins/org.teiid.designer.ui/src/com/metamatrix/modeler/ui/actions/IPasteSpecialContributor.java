/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * IPasteSpecialContributor
 */
public interface IPasteSpecialContributor extends IWorkbenchWindowActionDelegate, ISelectionListener {

    /**
     * Determine if this contribution can paste the current clipboard contents into
     * the current workbench selection.  This method is used only to determine enablement
     * of the PasteSpecialAction in the workbench.  The actual paste should be implemented in 
     * the run method.
     * @return true if the contribution can paste.
     */
    boolean canPaste();

}
