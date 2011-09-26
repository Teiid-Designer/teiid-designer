/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.wizards;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.core.extension.ModelExtensionUtils;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

public class ManageModelExtensionDefnsWizard extends AbstractWizard {

    // ============================================================================================================================

    // The page for driving the user options.
    protected CurrentModelExtensionDefnsPage currentModelExtensionDefnsPage;

    private MultiStatus status;

    private IWizardPage[] wizardPageArray;
    public ModelResource selectedModelResource;

    /**
     * Constructor for NewModelWizard.
     */
    public ManageModelExtensionDefnsWizard() {
        super(UiPlugin.getDefault(), Messages.manageMedsWizardTitle, null);
        setNeedsProgressMonitor(false);
    }

    // ************************** Wizard Methods **************************

    /**
     * Adding the page to the wizard.
     */
    @Override
    public void addPages() {
        currentModelExtensionDefnsPage = new CurrentModelExtensionDefnsPage(this.selectedModelResource);
        addPage(currentModelExtensionDefnsPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as
     * execution context.
     */
    @Override
    public boolean finish() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) {
                // Get the options and execute the build.
                doFinish(monitor);
            }
        };

        boolean success = false;
        // Detmine TXN status and start one if required.
        // This operation is not undoable OR significant.
        final boolean startedTxn = ModelerCore.startTxn(false,
                                                        false,
                                                        ManageModelExtensionDefnsWizard.this.getWindowTitle(),
                                                        ManageModelExtensionDefnsWizard.this);
        try {
            new ProgressMonitorDialog(getShell()).run(false, false, op);
            success = true;
        } catch (Throwable err) {
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
        } finally {
            // This operation is NOT undoable or significant... ALWAYS comit to ensure
            // Nothing is left hanging.
            if (startedTxn) {
                ModelerCore.commitTxn();
            }
        }
        MultiStatus status = getStatus();
        if (!success || !status.isOK()) {
            WidgetUtil.showError(Messages.manageMedsWizardErrorMsg);
            return false;
        }

        return true;
    }

    /**
     * The worker method. It will find the container, create the file(s) - Made this method public to allow for headless testing.
     * 
     * @param IPRogressMonitor - The progress monitor for this operation.
     */

    public void doFinish( final IProgressMonitor monitor ) {
        boolean hasErrors = false;
        if (currentModelExtensionDefnsPage == null) {
            addStatus(IStatus.ERROR, Messages.manageMedsWizardInitError, null);
            return;
        }

        // Get the currently selected ModelResource.
        ModelResource modelResource = currentModelExtensionDefnsPage.getSelectedModelResource();
        if (modelResource == null) {
            addStatus(IStatus.ERROR, Messages.manageMedsWizardInitError, null);
            return;
        }

        // Starting the update process
        monitor.beginTask(Messages.manageMedsWizardUpdateMedsMsg, 300);

        // Remove Selected Namespaces
        List<String> namespacesToRemove = currentModelExtensionDefnsPage.getNamespacesToRemove();
        if (namespacesToRemove.size() > 0) {
            for (String namespacePrefix : namespacesToRemove) {
                try {
                    monitor.subTask(Messages.manageMedsWizardRemoveMedsMsg);
                    ModelExtensionUtils.removeModelExtensionDefinition(modelResource, namespacePrefix);
                } catch (Exception e) {
                    hasErrors = true;
                    addStatus(IStatus.ERROR, Messages.manageMedsWizardRemoveMedsError, e);
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }
        }

        monitor.worked(100);

        // Add Selected ModelExtensionDefinitions
        List<ModelExtensionDefinition> medsToAdd = currentModelExtensionDefnsPage.getModelExtensionDefnsToAdd();
        if (medsToAdd.size() > 0) {
            // Add the Meds to the model
            for (ModelExtensionDefinition med : medsToAdd) {
                try {
                    monitor.subTask(Messages.manageMedsWizardAddMedsMsg);
                    ModelExtensionUtils.updateModelExtensionDefinition(modelResource, med);
                } catch (Exception e) {
                    hasErrors = true;
                    addStatus(IStatus.ERROR, Messages.manageMedsWizardAddMedsError, e);
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }
        }
        monitor.worked(100);

        // Save the ModelResource
        try {
            monitor.subTask(Messages.manageMedsWizardSaveModelMsg);
            modelResource.save(new NullProgressMonitor(), true);
        } catch (ModelWorkspaceException e) {
            hasErrors = true;
            addStatus(IStatus.ERROR, Messages.manageMedsWizardSaveModelError, e);
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }
        monitor.worked(100);
        monitor.done();

        // If the add, remove, save process succeeded - set status=OK
        if (!hasErrors) {
            addStatus(IStatus.OK, Messages.manageMedsWizardSuccessMsg, null);
        }
    }

    public MultiStatus getStatus() {
        if (status == null) {
            status = new MultiStatus(UiConstants.PLUGIN_ID, 0, Messages.manageMedsWizardResult, null);
        }

        return status;
    }

    private void addStatus( final int severity,
                            final String message,
                            final Throwable ex ) {
        final Status sts = new Status(severity, UiConstants.PLUGIN_ID, 0, message, ex);
        getStatus().add(sts);
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        if (selection != null && !selection.isEmpty()) {
            selectedModelResource = (ModelResource)SelectionUtilities.getSelectedObjects(selection).get(0);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        if (page == currentModelExtensionDefnsPage) {
            return null;
        }

        for (int i = 0; i < wizardPageArray.length; ++i) {
            if (wizardPageArray[i] == page) {
                if (i + 1 < wizardPageArray.length) {
                    return wizardPageArray[i + 1];
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * This Wizard can finish if the Options page is complete.
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if (currentPage == this.currentModelExtensionDefnsPage) {
            result = currentPage.isPageComplete();
        } else {
            boolean lastPage = (currentPage == wizardPageArray[wizardPageArray.length - 1]);
            result = lastPage && currentPage.isPageComplete();
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getPageCount()
     */
    @Override
    public int getPageCount() {
        if (wizardPageArray != null) {
            return wizardPageArray.length + 1;
        }
        return 1;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage( IWizardPage page ) {

        if (wizardPageArray == null || page == this.currentModelExtensionDefnsPage) {
            return null;
        }
        if (page == wizardPageArray[0]) {
            return this.currentModelExtensionDefnsPage;
        }
        for (int i = 1; i < wizardPageArray.length; ++i) {
            if (page == wizardPageArray[i]) {
                return wizardPageArray[i - 1];
            }
        }
        return null;
    }

}
