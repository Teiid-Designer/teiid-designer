/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import org.teiid.designer.runtime.ServerUtils;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * The <code>ServerPage</code> is used to create or modify a server.
 */
public final class ServerPage extends WizardPage {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The button used to test the connection to the server. Should only be enabled when server properties are valid.
     */
    private Button btnTestConnection;

    /**
     * The user password needed to login to the server.
     */
    private String password;

    /**
     * Indicates if the password should be persisted.
     */
    private boolean savePassword;

    /**
     * The server being editor or <code>null</code> if creating a new server.
     */
    private Server server;

    /**
     * The current validation status.
     */
    private IStatus status;

    /**
     * The server URL.
     */
    private String url;

    /**
     * The user needed to login to the server.
     */
    private String user;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs a wizard page that will create a new server.
     */
    public ServerPage() {
        super(ServerPage.class.getSimpleName());
        setTitle(UTIL.getString("serverPageTitle")); //$NON-NLS-1$
        setPageComplete(false);
    }

    /**
     * Constructs a wizard page that edits the specified server's properties.
     * 
     * @param server the server being edited
     */
    public ServerPage( Server server ) {
        super(ServerPage.class.getSimpleName());
        setTitle(UTIL.getString("serverPageTitle")); //$NON-NLS-1$

        this.server = server;
        this.url = server.getUrl();
        this.user = server.getUser();
        this.password = server.getPassword();
        this.savePassword = server.isPasswordBeingPersisted();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    private void constructAuthenticationPanel( Composite parent ) {
        Group pnl = new Group(parent, SWT.NONE);
        pnl.setText(UTIL.getString("serverPageAuthenticationGroupTitle")); //$NON-NLS-1$
        pnl.setLayout(new GridLayout(2, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        { // user row
            Label lblUser = new Label(pnl, SWT.LEFT);
            lblUser.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblUser.setText(UTIL.getString("serverPageUserLabel")); //$NON-NLS-1$

            Text txtUser = new Text(pnl, SWT.BORDER);
            txtUser.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            txtUser.setToolTipText(UTIL.getString("serverPageUserToolTip")); //$NON-NLS-1$

            // set initial value
            if (this.user != null) {
                txtUser.setText(this.user);
            }

            txtUser.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleUserModified(((Text)e.widget).getText());
                }
            });
        }

        { // password row
            Label lblPassword = new Label(pnl, SWT.LEFT);
            lblPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblPassword.setText(UTIL.getString("serverPagePasswordLabel")); //$NON-NLS-1$

            Text txtPassword = new Text(pnl, SWT.BORDER);
            txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtPassword.setToolTipText(UTIL.getString("serverPagePasswordToolTip")); //$NON-NLS-1$
            txtPassword.setEchoChar('*');

            // set initial value before hooking up listener
            if (this.password != null) {
                txtPassword.setText(this.password);
            }

            // listener for when value changes
            txtPassword.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handlePasswordModified(((Text)e.widget).getText());
                }
            });
        }

        { // save button row
            final Button btn = new Button(pnl, SWT.CHECK | SWT.LEFT);
            btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            ((GridData)btn.getLayoutData()).horizontalSpan = 2;
            btn.setText(UTIL.getString("serverPageSavePasswordButton")); //$NON-NLS-1$
            btn.setToolTipText(UTIL.getString("serverPageSavePasswordToolTip")); //$NON-NLS-1$

            // set initial value before hooking up listeners
            if (this.savePassword) {
                btn.setSelection(true);
            }

            // listener for when value changes
            btn.addSelectionListener(new SelectionAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleSavePasswordChanged(((Button)e.widget).getSelection());
                }
            });

            // update page message first time selected to get rid of initial message by forcing validation
            btn.addSelectionListener(new SelectionAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    updateInitialMessage();
                    btn.removeSelectionListener(this);
                }
            });
        }

        { // save password message row
            Label lblImage = new Label(pnl, SWT.NONE);
            lblImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
            lblImage.setImage(Display.getDefault().getSystemImage(SWT.ICON_INFORMATION));

            StyledText st = new StyledText(pnl, SWT.READ_ONLY | SWT.MULTI | SWT.NO_FOCUS | SWT.WRAP);
            st.setText(UTIL.getString("serverPageSavePasswordLabel")); //$NON-NLS-1$
            st.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            st.setCaret(null);
            GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
            gd.grabExcessVerticalSpace = false;
            gd.horizontalIndent = 4;
            gd.verticalIndent = 8;
            gd.widthHint = 100;
            st.setLayoutData(gd);
        }
    }

    private void constructTestConnectionPanel( Composite parent ) {
        Composite pnl = new Composite(parent, SWT.NONE);
        pnl.setLayout(new GridLayout(2, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lbl = new Label(pnl, SWT.LEFT);
        lbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lbl.setText(UTIL.getString("serverPageTestConnectionLabel")); //$NON-NLS-1$

        this.btnTestConnection = new Button(pnl, SWT.PUSH);
        this.btnTestConnection.setText(UTIL.getString("serverPageTestConnectionButton")); //$NON-NLS-1$
        this.btnTestConnection.setToolTipText(UTIL.getString("serverPageTestConnectionButtonToolTip")); //$NON-NLS-1$

        // add margins to the side of the text
        GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        Point minSize = this.btnTestConnection.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        gd.widthHint = Math.max(widthHint, minSize.x + 10);
        this.btnTestConnection.setLayoutData(gd);

        this.btnTestConnection.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleTestConnection();
            }
        });
    }

    private void constructUrlPanel( Composite parent ) {
        Composite pnl = new Composite(parent, SWT.NONE);
        pnl.setLayout(new GridLayout(2, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblUrl = new Label(pnl, SWT.LEFT);
        lblUrl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblUrl.setText(UTIL.getString("serverPageUrlLabel")); //$NON-NLS-1$

        Text txtUrl = new Text(pnl, SWT.BORDER);
        txtUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtUrl.setToolTipText(UTIL.getString("serverPageUrlToolTip")); //$NON-NLS-1$

        // set initial value
        if (this.url == null) {
            this.url = ServerUtils.DEFAULT_SERVER;
        }

        txtUrl.setText(this.url);

        txtUrl.addModifyListener(new ModifyListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( ModifyEvent e ) {
                handleUrlModified(((Text)e.widget).getText());
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( Composite parent ) {
        Composite pnlMain = new Composite(parent, SWT.NONE);
        pnlMain.setLayout(new GridLayout());
        pnlMain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        constructUrlPanel(pnlMain);
        constructAuthenticationPanel(pnlMain);
        constructTestConnectionPanel(pnlMain);
        setControl(pnlMain);

        // register with the help system
        IWorkbenchHelpSystem helpSystem = DqpUiPlugin.getDefault().getWorkbench().getHelpSystem();
        // TODO bring over Help contexts from Publishing Plugin
        // helpSystem.setHelp(pnlMain, SERVER_DIALOG_HELP_CONTEXT);
        helpSystem.setHelp(pnlMain, "SERVER_DIALOG_HELP_CONTEXT"); //$NON-NLS-1$
    }

    /**
     * @return the server represented by the dialog inputs
     * @throws RuntimeException if called when all inputs are not valid
     * @see #isPageComplete()
     */
    public Server getServer() {
        if (this.status.getSeverity() != IStatus.ERROR) {
            return new Server(this.url, this.user, this.password, this.savePassword, getServerManager());
        }

        // should never be called if error status
        throw new RuntimeException(UTIL.getString("serverPageInvalidServerProperties")); //$NON-NLS-1$
    }

    /**
     * @return the server manager obtained from the wizard
     */
    ServerManager getServerManager() {
        return ((ServerWizard)getWizard()).getServerManager();
    }

    /**
     * Handler for when the password control value is modified
     * 
     * @param newPassword the new password value
     */
    void handlePasswordModified( String newPassword ) {
        this.password = newPassword;
        updateState();
    }

    /**
     * @param savePassword <code>true</code> if the password should be persisted on the local file system
     */
    void handleSavePasswordChanged( boolean savePassword ) {
        this.savePassword = savePassword;
    }

    /**
     * Tests the connection of the server specified by the properties entered on this page. Precondition is that server properties
     * are valid.
     */
    void handleTestConnection() {
        final Server server = getServer();
        final boolean[] success = new boolean[1];

        BusyIndicator.showWhile(null, new Runnable() {
            /**
             * {@inheritDoc}
             * 
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                success[0] = server.isConnected();
            }
        });

        if (success[0]) {
            MessageDialog.openInformation(getShell(), UTIL.getString("serverPageTestConnectionDialogTitle"), //$NON-NLS-1$
                                          UTIL.getString("serverPageTestConnectionDialogSuccessMsg")); //$NON-NLS-1$
        } else {
            MessageDialog.openError(getShell(), UTIL.getString("serverPageTestConnectionDialogTitle"), //$NON-NLS-1$
                                    UTIL.getString("serverPageTestConnectionDialogFailureMsg")); //$NON-NLS-1$
        }
    }

    /**
     * Handler for when the URL control value is modified
     * 
     * @param newUrl the new URL value
     */
    void handleUrlModified( String newUrl ) {
        this.url = newUrl;
        updateState();
    }

    /**
     * Handler for when the user control value is modified
     * 
     * @param newUser the new user value
     */
    void handleUserModified( String newUser ) {
        this.user = newUser;
        updateState();
    }

    private IStatus isServerValid( String url,
                                   String username,
                                   String password ) {
        try {
            ServerUtils.validateServerUrl(url);
        } catch (IllegalArgumentException e) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, UTIL.getString("serverPageInvalidServerUrl"), e); //$NON-NLS-1$
        }

        if (username == null) {
            return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, UTIL.getString("serverPageUsernameCannotBeNull")); //$NON-NLS-1$
        }

        // TODO actually check server validity
        // Utils.isServerValid(this.url, this.user, this.password);
        return Status.OK_STATUS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);

        if (visible) {
            // set initial state
            validate();

            // set initial message
            setMessage(UTIL.getString("serverPageOkStatusMsg")); //$NON-NLS-1$
        }
    }

    /**
     * If the initial message is being displayed do a validation.
     */
    void updateInitialMessage() {
        if (UTIL.getString("serverPageOkStatusMsg").equals(getMessage())) { //$NON-NLS-1$
            updateState();
        }
    }

    /**
     * Updates message, message icon, and OK button enablement based on validation results
     */
    private void updateState() {
        // get the current status
        validate();

        // update OK/Finish button and test button enablement
        setPageComplete(this.status.getSeverity() != IStatus.ERROR);
        this.btnTestConnection.setEnabled(isPageComplete());

        // update message
        if (this.status.getSeverity() == IStatus.ERROR) {
            setMessage(this.status.getMessage(), IMessageProvider.ERROR);
        } else {
            if (this.status.getSeverity() == IStatus.WARNING) {
                setMessage(this.status.getMessage(), IMessageProvider.WARNING);
            } else if (this.status.getSeverity() == IStatus.INFO) {
                setMessage(this.status.getMessage(), IMessageProvider.INFORMATION);
            } else {
                setMessage(UTIL.getString("serverPageOkStatusMsg")); //$NON-NLS-1$
            }
        }
    }

    /**
     * Validates all inputs and sets the validation status.
     */
    private void validate() {
        this.status = isServerValid(this.url, this.user, this.password);

        // now check to see if a server is already registered
        if (this.status.isOK()) {
            Server changedServer = getServer();

            // don't check if modifying existing server and identifying properties have not changed
            if (((this.server == null) || !this.server.hasSameKey(changedServer))
                && getServerManager().isRegistered(changedServer)) {
                this.status = new Status(IStatus.ERROR, PLUGIN_ID, UTIL.getString("serverExistsMsg", changedServer.getUrl())); //$NON-NLS-1$
            }
        }
    }

}
