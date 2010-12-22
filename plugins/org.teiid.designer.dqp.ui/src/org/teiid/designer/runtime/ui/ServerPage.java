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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import org.teiid.designer.runtime.ServerUtils;
import org.teiid.designer.runtime.TeiidAdminInfo;
import org.teiid.designer.runtime.TeiidJdbcInfo;

import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * The <code>ServerPage</code> is used to create or modify a server.
 */
public final class ServerPage extends WizardPage {

    /**
     * The key in the wizard <code>IDialogSettings</code> for the auto-connect flag.
     */
    private static final String AUTO_CONNECT_KEY = "autoConnect"; //$NON-NLS-1$
    
    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

	private boolean isEdit = false;
	
	private boolean autoConnect = true;

	/**
     * The button used to test the connection to the server. Should only be enabled when server properties are valid.
     */
    private Button btnTestConnection;
    
    /**
     * The Check-box to allow auto-connect on Finish
     */
    private Button btnAutoConnectOnFinish;

    /**
     * The server being editor or <code>null</code> if creating a new server.
     */
    private Server server;

    /**
     * The current validation status.
     */
    private IStatus status;

    /**
     * The user needed to login to the teiid server via admin
     */
    private String adminUsername;
    
    /**
     * The password needed to login to the teiid server via admin
     */
    private String adminPassword;
    
    /**
     * Indicates if the admin password should be persisted.
     */
    private boolean saveAdminPassword;
    
    /**
     * The host name needed to login to the teiid server via admin
     */
    private String adminHost;
    
    /**
     * The port needed to login to the teiid server via admin
     */
    private String adminPort;
    
    /**
     * The SSL true/false protocol for admin URL
     */
    private boolean adminURLIsSecure;
    
    
    private Text adminURLText;
    
    /**
     * The user needed to login to the teiid server via jdbc
     */
    private String jdbcUsername;
    
    /**
     * The password needed to login to the teiid server via jdbc
     */
    private String jdbcPassword;
    
    /**
     * Indicates if the admin password should be persisted.
     */
    private boolean saveJdbcPassword;
    
    /**
     * The port needed to login to the teiid server via jdbc
     */
    private String jdbcPort;
    
    /**
     * The SSL true/false protocol for jdbc URL
     */
    private boolean jdbcURLIsSecure;
    
    
    private Text jdbcURLText;

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
        
    	this.server = new Server( new TeiidAdminInfo(), new TeiidJdbcInfo(), null);
        
        this.adminHost = server.getTeiidAdminInfo().getHost();
        this.adminPort = server.getTeiidAdminInfo().getPort();
        this.adminUsername = server.getTeiidAdminInfo().getUsername();
        this.adminPassword = server.getTeiidAdminInfo().getPassword();
        this.adminURLIsSecure = server.getTeiidAdminInfo().isSecure();
        this.saveAdminPassword = server.getTeiidAdminInfo().isPasswordBeingPersisted();
        
        this.jdbcPort = server.getTeiidJdbcInfo().getPort();
        this.jdbcUsername = server.getTeiidJdbcInfo().getUsername();
        this.jdbcPassword = server.getTeiidJdbcInfo().getPassword();
        this.jdbcURLIsSecure = server.getTeiidJdbcInfo().isSecure();
        this.saveJdbcPassword = server.getTeiidJdbcInfo().isPasswordBeingPersisted();

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
        
        this.adminHost = server.getTeiidAdminInfo().getHost();
        this.adminPort = server.getTeiidAdminInfo().getPort();
        this.adminUsername = server.getTeiidAdminInfo().getUsername();
        this.adminPassword = server.getTeiidAdminInfo().getPassword();
        this.adminURLIsSecure = server.getTeiidAdminInfo().isSecure();
        this.saveAdminPassword = server.getTeiidAdminInfo().isPasswordBeingPersisted();
        
        this.jdbcPort = server.getTeiidJdbcInfo().getPort();
        this.jdbcUsername = server.getTeiidJdbcInfo().getUsername();
        this.jdbcPassword = server.getTeiidJdbcInfo().getPassword();
        this.jdbcURLIsSecure = server.getTeiidJdbcInfo().isSecure();
        this.saveJdbcPassword = server.getTeiidJdbcInfo().isPasswordBeingPersisted();
        
        this.isEdit = true;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================
    
    
    private void constructTeiidAdminConnectionPanel( Composite parent ) {
    	Group pnl = WidgetFactory.createGroup(parent, UTIL.getString("serverPageAdminConnectionInfoLabel")); //$NON-NLS-1$);
        pnl.setLayout(new GridLayout(3, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        


        { // Host row
            Label label = new Label(pnl, SWT.LEFT);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            label.setText(UTIL.getString("serverPageHostNameLabel")); //$NON-NLS-1$
            
	        Text text = new Text(pnl, SWT.BORDER);
	        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	        text.setToolTipText(UTIL.getString("serverPageHostNameTooltip")); //$NON-NLS-1$
	
	        // set initial value
	        if (this.adminHost != null) {
	        	text.setText(this.adminHost);
	        }
	
	        text.addModifyListener(new ModifyListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	             */
	            @Override
	            public void modifyText( ModifyEvent e ) {
	                handleAdminHostModified(((Text)e.widget).getText());
	            }
	        });
	        
	        
	        {
	        	Label emptyLabel = new Label(pnl, SWT.LEFT);
	        	emptyLabel.setText(" "); //$NON-NLS-1$
	        	
	        }
        }
        
        { // PORT ROW
            Label label = new Label(pnl, SWT.LEFT);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            label.setText(UTIL.getString("serverPagePortNumberLabel")); //$NON-NLS-1$
            
	        Text text = new Text(pnl, SWT.BORDER);
	        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	        text.setToolTipText(UTIL.getString("serverPagePortNumberTooltip")); //$NON-NLS-1$
	
	        // set initial value
	        if (this.adminPort != null) {
	        	text.setText(this.adminPort);
	        }
	
	        text.addModifyListener(new ModifyListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	             */
	            @Override
	            public void modifyText( ModifyEvent e ) {
	                handleAdminPortModified(((Text)e.widget).getText());
	            }
	        });
	        
	        {
	        	Label emptyLabel = new Label(pnl, SWT.LEFT);
	        	emptyLabel.setText(" "); //$NON-NLS-1$
	        	
	        }
        }
        
        { // user row
            Label label = new Label(pnl, SWT.LEFT);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            label.setText(UTIL.getString("serverPageUserLabel")); //$NON-NLS-1$
            
	        Text text = new Text(pnl, SWT.BORDER);
	        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	        text.setToolTipText(UTIL.getString("serverPageUserToolTip")); //$NON-NLS-1$
	
	        // set initial value
	        if (this.adminUsername != null) {
	        	text.setText(this.adminUsername);
	        }
	
	        text.addModifyListener(new ModifyListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	             */
	            @Override
	            public void modifyText( ModifyEvent e ) {
	                handleAdminUserModified(((Text)e.widget).getText());
	            }
	        });
	        
	        {
	        	Label emptyLabel = new Label(pnl, SWT.LEFT);
	        	emptyLabel.setText(" "); //$NON-NLS-1$
	        	
	        }
        }
        
        { // password row
            Label label = new Label(pnl, SWT.LEFT);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            label.setText(UTIL.getString("serverPagePasswordLabel")); //$NON-NLS-1$

            Text text = new Text(pnl, SWT.BORDER);
            text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            text.setToolTipText(UTIL.getString("serverPagePasswordToolTip")); //$NON-NLS-1$
            text.setEchoChar('*');

            // set initial value before hooking up listener
            if (this.adminPassword != null) {
            	text.setText(this.adminPassword);
            }

            // listener for when value changes
            text.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleAdminPasswordModified(((Text)e.widget).getText());
                }
            });
            { // save button row
                final Button btn = new Button(pnl, SWT.CHECK | SWT.LEFT);
                btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                ((GridData)btn.getLayoutData()).horizontalSpan = 1;
                btn.setText("Save"); //UTIL.getString("serverPageSavePasswordButton")); //$NON-NLS-1$
                String tooltip = UTIL.getString("serverPageSavePasswordToolTip") + '\n' + '\n' + UTIL.getString("serverPageSavePasswordLabel"); //$NON-NLS-1$ //$NON-NLS-2$
                btn.setToolTipText(tooltip);

                // set initial value before hooking up listeners
                if (this.saveAdminPassword) {
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
                        handleAdminSavePasswordChanged(((Button)e.widget).getSelection());
                    }
                });
            }
        }
        
        {
	        Label label = new Label(pnl, SWT.LEFT);
	        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	        label.setText(UTIL.getString("serverPageUrlLabel")); //$NON-NLS-1$
	
	        adminURLText = new Text(pnl, SWT.BORDER | SWT.READ_ONLY );// SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
	        adminURLText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	        adminURLText.setToolTipText(UTIL.getString("serverPageUrlToolTip")); //$NON-NLS-1$
	        adminURLText.setText(this.server.getTeiidAdminInfo().getURL());
	        
	        { // Secure SSL row
	            final Button btn = new Button(pnl, SWT.CHECK | SWT.LEFT);
	            btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	            ((GridData)btn.getLayoutData()).horizontalSpan = 1;
	            btn.setText(UTIL.getString("serverPageSSLConnectionLabel")); //$NON-NLS-1$
	            btn.setToolTipText(UTIL.getString("serverPageSSLConnectionTooltip")); //$NON-NLS-1$

	            // set initial value before hooking up listeners
	            btn.setSelection(this.adminURLIsSecure);

	            // listener for when value changes
	            btn.addSelectionListener(new SelectionAdapter() {
	                /**
	                 * {@inheritDoc}
	                 * 
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                    handleAdminSSLChanged(((Button)e.widget).getSelection());
	                }
	            });
	        }

        }
        
        { // AUTO CONNECT ROW
            this.btnAutoConnectOnFinish = new Button(pnl, SWT.CHECK);
            this.btnAutoConnectOnFinish.setText(UTIL.getString("serverPageAutoConnectLabel")); //$NON-NLS-1$
            this.btnAutoConnectOnFinish.setToolTipText(UTIL.getString("serverPageAutoConnectToolTip")); //$NON-NLS-1$
            this.btnAutoConnectOnFinish.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
            this.btnAutoConnectOnFinish.setSelection(true);
            
            // set the auto connect flag based on dialog settings
            if (getDialogSettings().get(AUTO_CONNECT_KEY) != null) {
                this.autoConnect = getDialogSettings().getBoolean(AUTO_CONNECT_KEY);
            }

            this.btnAutoConnectOnFinish.setSelection(this.autoConnect);
            
            this.btnAutoConnectOnFinish.addSelectionListener(new SelectionAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleAutoConnect();
                }
            });
            
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
    }
    
    private void constructTeiidJdbcConnectionPanel( Composite parent ) {
    	Group pnl = WidgetFactory.createGroup(parent, UTIL.getString("serverPageJDBCConnectionInfoLabel")); //$NON-NLS-1$);
        pnl.setLayout(new GridLayout(3, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                
        { // PORT ROW
            Label label = new Label(pnl, SWT.LEFT);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            label.setText(UTIL.getString("serverPagePortNumberLabel")); //$NON-NLS-1$);
            
	        Text text = new Text(pnl, SWT.BORDER);
	        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	        text.setToolTipText(UTIL.getString("serverPagePortNumberTooltip")); //$NON-NLS-1$);
	
	        // set initial value
	        if (this.jdbcPort != null) {
	        	text.setText(this.jdbcPort);
	        }
	
	        text.addModifyListener(new ModifyListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	             */
	            @Override
	            public void modifyText( ModifyEvent e ) {
	                handleJdbcPortModified(((Text)e.widget).getText());
	            }
	        });
	        {
	        	Label emptyLabel = new Label(pnl, SWT.LEFT);
	        	emptyLabel.setText(" "); //$NON-NLS-1$
	        	
	        }
        }
        
        { // user row
            Label label = new Label(pnl, SWT.LEFT);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            label.setText(UTIL.getString("serverPageUserLabel")); //$NON-NLS-1$
            
	        Text text = new Text(pnl, SWT.BORDER);
	        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	        text.setToolTipText(UTIL.getString("serverPageUserToolTip")); //$NON-NLS-1$
	
	        // set initial value
	        if (this.jdbcUsername != null) {
	        	text.setText(this.jdbcUsername);
	        }
	
	        text.addModifyListener(new ModifyListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	             */
	            @Override
	            public void modifyText( ModifyEvent e ) {
	                handleJdbcUserModified(((Text)e.widget).getText());
	            }
	        });
	        
	        {
	        	Label emptyLabel = new Label(pnl, SWT.LEFT);
	        	emptyLabel.setText(" "); //$NON-NLS-1$
	        	
	        }
        }
        
        { // password row
            Label label = new Label(pnl, SWT.LEFT);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            label.setText(UTIL.getString("serverPagePasswordLabel")); //$NON-NLS-1$

            Text text = new Text(pnl, SWT.BORDER);
            text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            text.setToolTipText(UTIL.getString("serverPagePasswordToolTip")); //$NON-NLS-1$
            text.setEchoChar('*');

            // set initial value before hooking up listener
            if (this.jdbcPassword != null) {
            	text.setText(this.jdbcPassword);
            }

            // listener for when value changes
            text.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleJdbcPasswordModified(((Text)e.widget).getText());
                }
            });
            { // save button row
                final Button btn = new Button(pnl, SWT.CHECK | SWT.LEFT);
                btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
                ((GridData)btn.getLayoutData()).horizontalSpan = 1;
                btn.setText("Save"); //UTIL.getString("serverPageSavePasswordButton")); //$NON-NLS-1$
                String tooltip = UTIL.getString("serverPageSavePasswordToolTip") + '\n' + '\n' + UTIL.getString("serverPageSavePasswordLabel"); //$NON-NLS-1$ //$NON-NLS-2$
                btn.setToolTipText(tooltip);

                // set initial value before hooking up listeners
                if (this.saveJdbcPassword) {
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
                        handleJdbcSavePasswordChanged(((Button)e.widget).getSelection());
                    }
                });
            }
        }
        
        {
	        Label label = new Label(pnl, SWT.LEFT);
	        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	        label.setText(UTIL.getString("serverPageUrlLabel")); //$NON-NLS-1$
	
	        jdbcURLText = new Text(pnl, SWT.BORDER | SWT.READ_ONLY );// SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
	        jdbcURLText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	        jdbcURLText.setToolTipText(UTIL.getString("serverPageUrlToolTip")); //$NON-NLS-1$
	        jdbcURLText.setText(this.server.getTeiidJdbcInfo().getURL());
	        
	        { // Secure SSL row
	            final Button btn = new Button(pnl, SWT.CHECK | SWT.LEFT);
	            btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	            ((GridData)btn.getLayoutData()).horizontalSpan = 1;
	            btn.setText(UTIL.getString("serverPageSSLConnectionLabel")); //$NON-NLS-1$
	            btn.setToolTipText(UTIL.getString("serverPageSSLConnectionTooltip")); //$NON-NLS-1$

	            // set initial value before hooking up listeners
	            btn.setSelection(this.jdbcURLIsSecure);

	            // listener for when value changes
	            btn.addSelectionListener(new SelectionAdapter() {
	                /**
	                 * {@inheritDoc}
	                 * 
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                    handleJdbcSSLChanged(((Button)e.widget).getSelection());
	                }
	            });
	        }

        }
        
        
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
        
        constructTeiidAdminConnectionPanel(pnlMain);
        
        constructTeiidJdbcConnectionPanel(pnlMain);
        
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
        	return new Server(this.server.getTeiidAdminInfo(), this.server.getTeiidJdbcInfo(), getServerManager());
        }

        // should never be called if error status
        MessageDialog.openError(getShell(), UTIL.getString("serverPageTestConnectionDialogTitle"), //$NON-NLS-1$
                UTIL.getString("serverPageInvalidServerProperties")); //$NON-NLS-1$
        return null;
    }

    /**
     * @return the server manager obtained from the wizard
     */
    ServerManager getServerManager() {
        return ((ServerWizard)getWizard()).getServerManager();
    }
    
    void handleAutoConnect() {
    	this.autoConnect = this.btnAutoConnectOnFinish.getSelection();
    }

    /**
     * Handler for when the password control value is modified
     * 
     * @param newPassword the new password value
     */
    void handleAdminPasswordModified( String newPassword ) {
        this.adminPassword = newPassword;
        this.server.getTeiidAdminInfo().setPassword(newPassword);
        updateState();
    }

    /**
     * @param savePassword <code>true</code> if the password should be persisted on the local file system
     */
    void handleAdminSavePasswordChanged( boolean savePassword ) {
        this.saveAdminPassword = savePassword;
        this.server.getTeiidAdminInfo().setPersistPassword(savePassword);
        updateState();
    }
    
    /**
     * @param newHost the new host value
     */
    void handleAdminHostModified( String newHost ) {
        this.adminHost = newHost;
        this.server.getTeiidAdminInfo().setHost(newHost);
        // Need to update BOTH server info objects with same Host Name
        this.server.getTeiidJdbcInfo().setHost(newHost);
        updateState();
    }
    
    /**
     * @param newPort the new host value
     */
    void handleAdminPortModified( String newPort ) {
        this.adminPort = newPort;
        this.server.getTeiidAdminInfo().setPort(newPort);
        updateState();
    }
    
    /**
     * Handler for when the password control value is modified
     * 
     * @param newPassword the new password value
     */
    void handleJdbcPasswordModified( String newPassword ) {
        this.jdbcPassword = newPassword;
        this.server.getTeiidJdbcInfo().setPassword(newPassword);
        updateState();
    }

    /**
     * @param savePassword <code>true</code> if the password should be persisted on the local file system
     */
    void handleJdbcSavePasswordChanged( boolean savePassword ) {
        this.saveJdbcPassword = savePassword;
        this.server.getTeiidJdbcInfo().setPersistPassword(savePassword);
        updateState();
    }
    
    /**
     * @param newPort the new host value
     */
    void handleJdbcPortModified( String newPort ) {
        this.jdbcPort = newPort;
        this.server.getTeiidJdbcInfo().setPort(newPort);
        updateState();
    }

    /**
     * Tests the connection of the server specified by the properties entered on this page. Precondition is that server properties
     * are valid.
     */
    void handleTestConnection() {
        final Server server = getServer();
        
        if( server != null ) {
	        final boolean[] success = new boolean[1];
	
	        BusyIndicator.showWhile(null, new Runnable() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see java.lang.Runnable#run()
	             */
	            @Override
	            public void run() {
	                success[0] = server.testPing().isOK();
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
    }
    
    /**
     * Handler for when the user control value is modified
     * 
     * @param newUser the new user value
     */
    void handleAdminUserModified( String newUser ) {
        this.adminUsername = newUser;
        this.server.getTeiidAdminInfo().setUsername(newUser);
        updateState();
    }
    /**
     * Handler for when the user control value is modified
     * 
     * @param newUser the new user value
     */
    void handleAdminSSLChanged( boolean isSecure ) {
        this.adminURLIsSecure = isSecure;
        this.server.getTeiidAdminInfo().setSecure(isSecure);
        updateState();
    } 
    
    /**
     * Handler for when the user control value is modified
     * 
     * @param newUser the new user value
     */
    void handleJdbcUserModified( String newUser ) {
        this.jdbcUsername = newUser;
        this.server.getTeiidJdbcInfo().setUsername(newUser);
        updateState();
    }
    /**
     * Handler for when the user control value is modified
     * 
     * @param newUser the new user value
     */
    void handleJdbcSSLChanged( boolean isSecure ) {
        this.jdbcURLIsSecure = isSecure;
        this.server.getTeiidJdbcInfo().setSecure(isSecure);
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
        
        if( !this.isEdit && getServerManager().isRegisteredUrl(url) ) {
        	return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, UTIL.getString("serverPageExistingServerUrl", url)); //$NON-NLS-1$
        }

        if (username == null || username.length() == 0) {
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
     * 
     * @return true if autoconnect is checked
     */
    public boolean shouldAutoConnect() {
		return autoConnect;
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
    	this.adminURLText.setText(this.server.getTeiidAdminInfo().getURL());
    	this.jdbcURLText.setText(this.server.getTeiidJdbcInfo().getURL());
    	
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
        this.status = isServerValid(this.server.getTeiidAdminInfo().getURL(), this.adminUsername, this.adminPassword);

        // now check to see if a server is already registered
        if (this.status.isOK()) {
            Server changedServer = getServer();

            // don't check if modifying existing server and identifying properties have not changed
            if (((this.server == null) || !this.server.hasSameKey(changedServer))
                && getServerManager().isRegistered(changedServer)) {
                this.status = new Status(IStatus.ERROR, PLUGIN_ID, UTIL.getString("serverExistsMsg", changedServer.getTeiidAdminInfo().getURL())); //$NON-NLS-1$
            }
        }
    }

    /**
     * Processing done after wizard 'Finish' button is clicked. Wizard was not canceled.
     */
    void performFinish() {
        // update dialog settings
        getDialogSettings().put(AUTO_CONNECT_KEY, this.autoConnect);
    }
}
