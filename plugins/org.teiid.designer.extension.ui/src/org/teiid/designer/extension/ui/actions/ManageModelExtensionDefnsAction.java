/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.actions;

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.wizards.ManageModelExtensionDefnsWizard;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class ManageModelExtensionDefnsAction extends SortableSelectionAction {
    private final ModelExtensionRegistry registry;

    /**
     * @since 7.6
     */
    public ManageModelExtensionDefnsAction() {
        super(Messages.manageModelExtensionDefnsActionTitle, SWT.DEFAULT);
        // setImageDescriptor(DatatoolsUiPlugin.getDefault().getImageDescriptor(DatatoolsUiConstants.Images.VIEW_CONNECTION_ICON));
        // providerFactory = new ConnectionInfoProviderFactory();
        this.registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 7.6
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return isExtendableModelSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 7.6
     */
    @Override
    public void run() {
        ModelResource modelResource = null;
        if (!getSelection().isEmpty()) {
            IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
            modelResource = ModelUtilities.getModelResource(modelFile);
        }

        if (modelResource != null) {
            IStructuredSelection structuredSelection = new StructuredSelection(modelResource);
            final ManageModelExtensionDefnsWizard wizard = new ManageModelExtensionDefnsWizard();
            wizard.init(UiPlugin.getDefault().getCurrentWorkbenchWindow().getWorkbench(), structuredSelection);
            final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
            int rc = dialog.open();

// if (rc != Window.CANCEL) {
            // final MultiStatus result = wizard.getResult();
            // final int severity = result.getSeverity();
            // if (severity == IStatus.ERROR) {
            //                    final String errTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.errTitle"); //$NON-NLS-1$
            //                    final String err = UiConstants.Util.getString("GenerateXsdSchemaAction.errFinish"); //$NON-NLS-1$
            // ErrorDialog.openError(wizard.getShell(), errTitle, err, result);
            // } else if (severity == IStatus.WARNING) {
            //                    final String warnTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.warnTitle"); //$NON-NLS-1$
            //                    final String warn = UiConstants.Util.getString("GenerateXsdSchemaAction.warnFinish"); //$NON-NLS-1$
            // ErrorDialog.openError(wizard.getShell(), warnTitle, warn, result);
            // } else {
            //                    final String okTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.successTitle"); //$NON-NLS-1$
            //                    final String ok = UiConstants.Util.getString("GenerateXsdSchemaAction.successFinish"); //$NON-NLS-1$
            //
            // List msgs = new ArrayList(result.getChildren().length);
            // for (int i = 0; i < result.getChildren().length; i++) {
            // msgs.add(result.getChildren()[i].getMessage());
            // }
            // // Defect 20589 - Thread off this dialog, so it shows up AFTER auto-build and other jobs which are
            // // causing more Progress monitors to appear (lots of flashing).
            // final List messgs = msgs;
            // Display.getCurrent().asyncExec(new Runnable() {
            // public void run() {
            // ListMessageDialog.openInformation(wizard.getShell(), okTitle, null, ok, messgs, null);
            // }
            // });
            // }
            // }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 7.6
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return isExtendableModelSelected(selection);
    }

    /**
     * Determine if the selection is allowed to have a Model Extension Definition
     * 
     * @param theSelection the selected object
     * @return 'true' if the selection is an extendable model
     * @since 7.6
     */
    private boolean isExtendableModelSelected( ISelection theSelection ) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        // Must be a single selection
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Object selectedObj = allObjs.get(0);
            // the selected model must (1) have a metamodelURI that can be extended and (2) be a source model
            if (selectedObj instanceof IFile) {
                result = false;
                try {
                    ModelResource modelResource = ModelUtil.getModelResource((IFile)selectedObj, false);
                    if ((modelResource != null) && isMetamodelExtendable(modelResource) && ModelUtilities.isPhysical(modelResource)) {
                        result = true;
                    }
                } catch (final ModelWorkspaceException theException) {
                    ModelerCore.Util.log(IStatus.ERROR, theException, theException.getMessage());
                }
            } else {
                result = false;
            }
        }

        return result;
    }

    /**
     * Determine if the selection is allowed to have a Model Extension Definition. Asks the registry if the metamodel URI is a
     * valid extendable URI.
     * @param modelResource the model resource
     * @return 'true' if the models metamodel URI is extendable.
     * @since 7.6
     */
    private boolean isMetamodelExtendable( ModelResource modelResource ) {
        if (this.registry != null) {
            String selectedModelURI = ModelIdentifier.getPrimaryMetamodelURI(modelResource);
            return registry.isExtendable(selectedModelURI);
        }
        return false;
    }

}