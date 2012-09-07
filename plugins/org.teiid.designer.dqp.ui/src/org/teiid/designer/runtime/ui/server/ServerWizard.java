/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;

/**
 * The <code>ServerWizard</code> is the wizard used to create and edit servers.
 *
 * @since 8.0
 */
public final class ServerWizard extends Wizard {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The wizard page containing all the controls that allow editing of server properties.
     */
    private final ServerPage page;

    /**
     * The manager in charge of the server registry.
     */
    private final TeiidServerManager teiidServerManager;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs a wizard that creates a new server.
     * 
     * @param teiidServerManager the server manager in charge of the server registry (never <code>null</code>)
     */
    public ServerWizard( TeiidServerManager teiidServerManager ) {
        this.page = new ServerPage();
        this.teiidServerManager = teiidServerManager;

        setDefaultPageImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SERVER_WIZBAN));
        setWindowTitle(UTIL.getString("serverWizardNewServerTitle")); //$NON-NLS-1$
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
    protected TeiidServerManager getServerManager() {
        return this.teiidServerManager;
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
        IStatus status = this.page.performFinish();

        // log if necessary
        if (!status.isOK()) {
            UTIL.log(status);
        }

        return (status.getSeverity() != IStatus.ERROR);
    }
}
