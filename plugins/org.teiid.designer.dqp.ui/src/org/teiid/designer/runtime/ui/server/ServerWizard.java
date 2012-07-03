/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * The <code>ServerWizard</code> is the wizard used to create and edit servers.
 */
public final class ServerWizard extends Wizard {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * Non-<code>null</code> if the wizard is editing an existing server.
     */
    private Server existingServer;

    /**
     * The wizard page containing all the controls that allow editing of server properties.
     */
    private final ServerPage page;

    /**
     * The manager in charge of the server registry.
     */
    private final ServerManager serverManager;

    private Server resultServer;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs a wizard that creates a new server.
     * 
     * @param serverManager the server manager in charge of the server registry (never <code>null</code>)
     */
    public ServerWizard( ServerManager serverManager ) {
        this.page = new ServerPage();
        this.serverManager = serverManager;

        setDefaultPageImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SERVER_WIZBAN));
        setWindowTitle(UTIL.getString("serverWizardNewServerTitle")); //$NON-NLS-1$
    }

    /**
     * Constructs a wizard that edits an existing server.
     * 
     * @param serverManager the server manager in charge of the server registry (never <code>null</code>)
     * @param server the server whose properties are being edited (never <code>null</code>)
     */
    public ServerWizard( ServerManager serverManager,
                         Server server ) {
        this.page = new ServerPage(server);
        this.serverManager = serverManager;
        this.existingServer = server;
        setWindowTitle(UTIL.getString("serverWizardEditServerTitle")); //$NON-NLS-1$
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(this.page);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#getDialogSettings()
     */
    @Override
    public IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();

        if (settings == null) {
            IDialogSettings temp = DqpUiPlugin.getDefault().getDialogSettings();
            settings = temp.getSection(getClass().getSimpleName());

            if (settings == null) {
                settings = temp.addNewSection(getClass().getSimpleName());
            }

            setDialogSettings(settings);
        }

        return super.getDialogSettings();
    }

    /**
     * @return the server manager (never <code>null</code>)
     */
    protected ServerManager getServerManager() {
        return this.serverManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        if (! this.page.isPageComplete()) {
            return false;
        }
        
        // first let page know that wizard finished and was not canceled
        this.page.performFinish();

        IStatus status = Status.OK_STATUS;
        resultServer = this.page.getServer();

        if (this.existingServer == null) {
            status = this.serverManager.addServer(resultServer);

            if (status.getSeverity() == IStatus.ERROR) {
                MessageDialog.openError(getShell(), UTIL.getString("errorDialogTitle"), //$NON-NLS-1$
                                        UTIL.getString("serverWizardEditServerErrorMsg")); //$NON-NLS-1$
            }
        } else if (!this.existingServer.equals(resultServer)) {
            status = this.serverManager.updateServer(this.existingServer, resultServer);

            if (status.getSeverity() == IStatus.ERROR) {
                MessageDialog.openError(getShell(), UTIL.getString("errorDialogTitle"), //$NON-NLS-1$
                                        UTIL.getString("serverWizardNewServerErrorMsg")); //$NON-NLS-1$
            }
        }

        // log if necessary
        if (!status.isOK()) {
            UTIL.log(status);
        }

        return (status.getSeverity() != IStatus.ERROR);
    }

    public boolean shouldAutoConnect() {
        return this.page.shouldAutoConnect();
    }

    public Server getServer() {
        return this.resultServer;
    }
}
