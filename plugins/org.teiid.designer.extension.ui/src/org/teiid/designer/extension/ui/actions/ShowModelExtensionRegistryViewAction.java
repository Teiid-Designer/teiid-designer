/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.actions;

import static org.teiid.designer.extension.ui.Messages.errorDialogTitle;
import static org.teiid.designer.extension.ui.Messages.errorOpeningRegistryViewMsg;
import static org.teiid.designer.extension.ui.Messages.showRegistryViewActionText;
import static org.teiid.designer.extension.ui.Messages.showRegistryViewActionToolTip;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.SHOW_REGISTRY_VIEW_ACTION;
import static org.teiid.designer.extension.ui.UiConstants.ViewIds.REGISTRY_VIEW;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.extension.ui.Activator;

/**
 * 
 */
public final class ShowModelExtensionRegistryViewAction extends Action {

    public ShowModelExtensionRegistryViewAction() {
        super(showRegistryViewActionText, SWT.FLAT);
        setImageDescriptor(Activator.getDefault().getImageDescriptor(SHOW_REGISTRY_VIEW_ACTION));
        setToolTipText(showRegistryViewActionToolTip);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(REGISTRY_VIEW);
        } catch (PartInitException e) {
            MessageDialog.openError(null, errorDialogTitle, errorOpeningRegistryViewMsg);
        }
    }

}
