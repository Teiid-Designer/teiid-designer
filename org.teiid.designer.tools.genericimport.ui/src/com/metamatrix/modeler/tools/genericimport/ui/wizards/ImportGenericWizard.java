/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.genericimport.ui.wizards;

import java.sql.Connection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.builder.processor.Processor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.tools.genericimport.ui.GenericImportUiPlugin;
import com.metamatrix.modeler.tools.genericimport.ui.PluginConstants;
import com.metamatrix.modeler.tools.genericimport.ui.UiConstants;
import com.metamatrix.modeler.tools.genericimport.ui.util.GenericImportUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * ImportGenericWizard - this is the wizard for Generic VDB import.
 */
public class ImportGenericWizard extends AbstractWizard
    implements PluginConstants.Images, IImportWizard, StringUtil.Constants, UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ImportGenericWizard.class);
    private static final String WIDTH = "width"; //$NON-NLS-1$
    private static final String HEIGHT = "height"; //$NON-NLS-1$

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = GenericImportUiPlugin.getDefault().getImageDescriptor(IMPORT_PROJECT_ICON);
    private static final String NOT_LICENSED_MSG = getString("notLicensedMessage"); //$NON-NLS-1$

    // Set Licensed to true. Leave licencing code in, just in case we decide
    // to license in the future...
    private static boolean importLicensed = true;

    private GenericImportManager importManager;
    private MultiStatus importStatus;

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private VDBDefinitionPage vdbDefinitionPage;

    /**
     * @since 4.0
     */
    public ImportGenericWizard() {
        super(GenericImportUiPlugin.getDefault(), TITLE, IMAGE);
    }

    Composite createEmptyPageControl( final Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        if (importLicensed) {
            // If no dialog size settings, then use default of 500X500
            IDialogSettings settings = getDialogSettings();
            // Try to get height and width settings
            try {
                settings.getInt(WIDTH);
                settings.getInt(HEIGHT);
                // If height or width not found, set 500x500 default
            } catch (NumberFormatException e) {
                settings.put(WIDTH, 500);
                settings.put(HEIGHT, 500);
            }
            super.createPageControls(pageContainer);
        }
    }

    /**
     * The Finish button was pressed. Try to do the required work now and answer a boolean indicating success. If false is
     * returned then the wizard will not close.
     * 
     * @return boolean
     */
    @Override
    public boolean finish() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) {
                generateModels(monitor);
            }
        };

        try {
            final ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            dlg.run(false, false, op);
        } catch (final InterruptedException ignored) {
            GenericImportUtil.addStatus(importStatus, IStatus.CANCEL, getString("errorDialog.importInterrupted.message")); //$NON-NLS-1$
        } catch (final Exception err) {
            GenericImportUtil.addStatus(importStatus, IStatus.ERROR, getString("errorDialog.importError.message"), err); //$NON-NLS-1$
        }

        String dialogTitle;
        String dialogMessage;
        int statusMask;
        if (importStatus.getSeverity() == IStatus.ERROR) {
            dialogTitle = getString("errorDialog.hasErrors.title"); //$NON-NLS-1$
            dialogMessage = getString("errorDialog.hasErrors.message"); //$NON-NLS-1$
            statusMask = IStatus.ERROR;
        } else if (importStatus.getSeverity() == IStatus.WARNING) {
            dialogTitle = getString("errorDialog.completed.title"); //$NON-NLS-1$
            dialogMessage = getString("errorDialog.hasWarnings.message"); //$NON-NLS-1$
            statusMask = IStatus.WARNING;
        } else {
            dialogTitle = getString("errorDialog.completed.title"); //$NON-NLS-1$
            dialogMessage = getString("errorDialog.completedOK.Message"); //$NON-NLS-1$
            statusMask = IStatus.INFO | IStatus.OK;
        }

        // IStatus status = filterStatus(this.importStatus,filteredStatus);
        ErrorDialog.openError(getShell(), dialogTitle, dialogMessage, importStatus, statusMask);
        return true;
    }

    /**
     * Generate models and entities using the GenericImportProcessor.
     * 
     * @param monitor the progressMonitor
     */
    void generateModels( IProgressMonitor monitor ) {
        // Get the ImportManager settings - pass to processor
        Connection sqlConnection = this.importManager.getSQLConnection();
        String selectedVDBModel = this.importManager.getSelectedVDBModel();
        IContainer targetLocation = this.importManager.getTargetLocation();
        this.importStatus = new MultiStatus(UiConstants.PLUGIN_ID, 0, getString("generateModels.statusTitle"), null); //$NON-NLS-1$

        Processor importProcessor = new GenericImportProcessor(sqlConnection, selectedVDBModel, targetLocation, importStatus);

        boolean requiredStart = false;
        boolean succeeded = false;
        try {
            // -------------------------------------------------
            // Wrap the processing in a single transaction
            // -------------------------------------------------

            requiredStart = ModelerCore.startTxn(false, false, "Generic Import Processing", this); //$NON-NLS-1$$

            importProcessor.process(monitor);

            succeeded = true;
        } catch (Exception ex) {
            String message = "Error processing the import"; //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, ex, message);
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        if (importLicensed) {
            importManager = new GenericImportManager();
            vdbDefinitionPage = new VDBDefinitionPage(importManager);
            addPage(vdbDefinitionPage);
        } else {
            // Create empty page
            WizardPage page = new WizardPage(ImportGenericWizard.class.getSimpleName(), TITLE, null) {

                public void createControl( final Composite parent ) {
                    setControl(createEmptyPageControl(parent));
                }
            };
            page.setMessage(NOT_LICENSED_MSG, IMessageProvider.ERROR);
            page.setPageComplete(false);
            addPage(page);
        }
    }
}
