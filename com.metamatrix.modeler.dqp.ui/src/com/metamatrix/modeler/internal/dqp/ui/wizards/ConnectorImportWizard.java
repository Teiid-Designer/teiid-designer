/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

public final class ConnectorImportWizard extends AbstractWizard implements DqpUiConstants {

    private final ConnectorImportHelper helper;

    private final IWizardPage connectorTypePage;

    private final IWizardPage connectorPage;

    private final IWizardPage jarPage;

    /**
     * @param plugin
     * @param title
     * @param image
     */
    public ConnectorImportWizard() {
        super(DqpUiPlugin.getDefault(), I18n.WizardTitle,
              DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.IMPORT_CONNECTORS_WIZBAN));
        this.helper = new ConnectorImportHelper();
        this.connectorTypePage = new ConnectorTypeSelectionPage(this.helper);
        this.connectorPage = new ConnectorSelectionPage(this.helper);
        this.jarPage = new JarSelectionPage(this.helper);
    }

    /**
     * A way for inner classes to get to the business object.
     * 
     * @since 5.5.3
     */
    ConnectorImportHelper accessHelper() {
        return this.helper;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        super.addPages();
        addPage(this.connectorTypePage);
        addPage(this.connectorPage);
        addPage(this.jarPage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#canFinish()
     */
    @Override
    public boolean canFinish() {
        return (this.helper.getImportStatus().getSeverity() != IStatus.ERROR);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#finish()
     */
    @Override
    public boolean finish() {
        // make sure it is OK to go ahead with import
        assert (this.helper.getImportStatus().getSeverity() != IStatus.ERROR);

        // status of import
        final IStatus[] status = new Status[1];

        // import operation gets done here
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run( IProgressMonitor monitor ) {
                status[0] = accessHelper().performImport(monitor);
            }
        };

        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());

        try {
            // show dialog and run operation until finished or canceled
            dialog.run(true, true, op);
        } catch (Throwable e) {
            if (e instanceof InterruptedException) {
                // dialog was canceled but the (incomplete) status should've been filled in by the business object but check
                if (status[0] == null) {
                    status[0] = new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, IStatus.OK, I18n.ImportCanceled, null);
                }
            } else {
                if (e instanceof InvocationTargetException) {
                    e = ((InvocationTargetException)e).getTargetException();
                }

                status[0] = new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, I18n.ImportException, e);
            }

            // show status of import in dialog
            WidgetUtil.show(status[0]);
        } finally {
            dialog.getProgressMonitor().done();

            // show dialog if not OK status
            if (!status[0].isOK()) {
                UTIL.log(status[0]);
                WidgetUtil.show(status[0]);
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        if (page == this.connectorTypePage) {
            if (this.helper.getAllImportFileConnectors().isEmpty()) {
                return this.jarPage;
            }
        }

        return super.getNextPage(page);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage( IWizardPage page ) {
        if (page == this.jarPage) {
            if (this.helper.getAllImportFileConnectors().isEmpty()) {
                return this.connectorTypePage;
            }
        }

        return super.getPreviousPage(page);
    }
}
