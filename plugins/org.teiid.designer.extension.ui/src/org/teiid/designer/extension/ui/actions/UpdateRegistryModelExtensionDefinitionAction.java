/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.actions;

import static org.teiid.designer.extension.ExtensionConstants.MED_EXTENSION;
import static org.teiid.designer.extension.ui.Messages.updateMedInRegistryActionText;
import static org.teiid.designer.extension.ui.Messages.updateMedInRegistryActionToolTip;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.REGISTERY_MED_UPDATE_ACTION;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.teiid.designer.extension.ui.Activator;

import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * 
 */
public final class UpdateRegistryModelExtensionDefinitionAction extends SortableSelectionAction {

    public UpdateRegistryModelExtensionDefinitionAction() {
        super(updateMedInRegistryActionText, SWT.FLAT);
        setImageDescriptor(Activator.getDefault().getImageDescriptor(REGISTERY_MED_UPDATE_ACTION));
        setToolTipText(updateMedInRegistryActionToolTip);
    }

    /**
     * Allow single selection of mxd file
     * 
     * @param selection
     * @return
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        Object obj = SelectionUtilities.getSelectedObject(selection);

        if ((obj != null) && (obj instanceof IFile)) {
            String extension = ((IFile)obj).getFileExtension();
            return ((extension != null) && extension.equals(MED_EXTENSION));
        }

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    protected boolean isValidSelection( ISelection selection ) {
        return isApplicable(selection);
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {
        IStructuredSelection medFileSelection = (IStructuredSelection)getSelection();
        RegistryDeploymentValidator.deploy((IFile)medFileSelection.getFirstElement());
    }

}
