/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidParentServerListener;
import org.teiid.designer.runtime.TeiidServerFactory.ServerOptions;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterFactory;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * The <code>ServerPage</code> is used to create or modify a server.
 *
 * @since 8.0
 */
public final class ServerPage extends WizardPage {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * parent display of this control
     */
    private Display display;
    
    /**
     * The server being editor or <code>null</code> if creating a new server.
     */
    private ITeiidServer teiidServer;

    /**
     * The current validation status.
     */
    private IStatus status;

    private Text displayNameText;
    
    private Text jbossServerNameText;
    
    private Text jdbcUsernameText;

    private Text jdbcPasswordText;

    private Text jdbcURLText;
    
    private IServerLifecycleListener serverLifecycleListener = new IServerLifecycleListener() {
        
        @Override
        public void serverRemoved(IServer server) {
        }
        
        @Override
        public void serverChanged(IServer server) {
        }
        
        @Override
        public void serverAdded(final IServer server) {
            // This is performed on its own thread
            createTeiidServer(server);
        }
    };
    
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
        
        DqpPlugin.getInstance().getServersProvider().addServerLifecycleListener(serverLifecycleListener);
        status = Status.CANCEL_STATUS;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================
    
    private void constructDisplayNamePanel(Composite pnlMain) {
        Composite displayNameComposite = new Composite(pnlMain, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(displayNameComposite);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(displayNameComposite);
        
        Label displayNameLabel = new Label(displayNameComposite, SWT.NONE);
        displayNameLabel.setText(UTIL.getString("serverPageDisplayNameLabel")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(displayNameLabel);
        
        displayNameText = new Text(displayNameComposite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(displayNameText);
        displayNameText.addFocusListener(new FocusListener() {
            
            @Override
            public void focusLost(FocusEvent e) {
                handlePropertiesModified();
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                // do nothing
            }
        });
    }
    
    private void constructIServerCreationPanel( Composite parent ) {
        Group serverGroup = WidgetFactory.createGroup(parent, UTIL.getString("serverPageIServerLabel")); //$NON-NLS-1$);
        GridDataFactory.fillDefaults().applyTo(serverGroup);
        GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 5).applyTo(serverGroup);
        
        Label serverLabel = new Label(serverGroup, SWT.NONE);
        serverLabel.setText(UTIL.getString("serverPageIServerLabel")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(serverLabel);
        
        this.jbossServerNameText = new Text(serverGroup, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jbossServerNameText);
        this.jbossServerNameText.setEditable(false);
		this.jbossServerNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        final Hyperlink newServerHyperlink = new Hyperlink(serverGroup, SWT.NONE);
        newServerHyperlink.setText(UTIL.getString("serverPageIServerNewButton")); //$NON-NLS-1$
        newServerHyperlink.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        newServerHyperlink.setUnderlined(true);
        GridDataFactory.swtDefaults().applyTo(newServerHyperlink);
        newServerHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                // Need to stop the server manager from adding a teiid server
                // since this page is in charge of that in this case.
                
                TeiidParentServerListener.getInstance().sleep();
                
                ServerUIUtil.showNewServerWizard(ServerPage.this.getShell(), null, null, null);
                
                TeiidParentServerListener.getInstance().wake();
            }
        });
    }
    
    private void constructTeiidJdbcConnectionPanel( Composite parent ) {
        Group teiidJdbcGroup = WidgetFactory.createGroup(parent, UTIL.getString("serverPageJDBCConnectionInfoLabel")); //$NON-NLS-1$);
        GridDataFactory.fillDefaults().applyTo(teiidJdbcGroup);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 5).applyTo(teiidJdbcGroup);
        
        {
        	Text helpText = new Text(teiidJdbcGroup, SWT.WRAP | SWT.READ_ONLY); 
            GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
            gd.heightHint = 40;
            gd.widthHint =  240;
            gd.horizontalSpan = 2;
            helpText.setLayoutData(gd);
        	
        	helpText.setText(UTIL.getString("serverPageJDBCConnectionInfoHelp.txt"));  //$NON-NLS-1$
        	helpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        	helpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	//GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(helpText);
        }

        { // user row
            Label label = new Label(teiidJdbcGroup, SWT.LEFT); 
            label.setText(UTIL.getString("serverPageUserLabel")); //$NON-NLS-1$
            GridDataFactory.swtDefaults().applyTo(label);

            jdbcUsernameText = new Text(teiidJdbcGroup, SWT.BORDER);
            jdbcUsernameText.setText(ITeiidJdbcInfo.DEFAULT_JDBC_USERNAME);
            GridDataFactory.fillDefaults().applyTo(jdbcUsernameText);
            jdbcUsernameText.setToolTipText(UTIL.getString("serverPageUserToolTip")); //$NON-NLS-1$
            jdbcUsernameText.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    handlePropertiesModified();
                }
            });
        }

        { // password row
            Label label = new Label(teiidJdbcGroup, SWT.LEFT); 
            label.setText(UTIL.getString("serverPagePasswordLabel")); //$NON-NLS-1$
            GridDataFactory.swtDefaults().applyTo(label);

            jdbcPasswordText = new Text(teiidJdbcGroup, SWT.BORDER);
            jdbcPasswordText.setText(ITeiidJdbcInfo.DEFAULT_JDBC_PASSWORD);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPasswordText);
            jdbcPasswordText.setToolTipText(UTIL.getString("serverPagePasswordToolTip")); //$NON-NLS-1$
            jdbcPasswordText.setEchoChar('*');

            // listener for when value changes
            jdbcPasswordText.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    handlePropertiesModified();
                }
            });
        }
        
        {
            Label label = new Label(teiidJdbcGroup, SWT.LEFT);
            label.setText(UTIL.getString("serverPageJDBCUrlLabel")); //$NON-NLS-1$
            GridDataFactory.swtDefaults().applyTo(label);
            
            this.jdbcURLText = new Text(teiidJdbcGroup, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcURLText);
            this.jdbcURLText.setEditable(false);
            this.jdbcURLText.setToolTipText(UTIL.getString("serverPageJDBCUrlToolTip")); //$NON-NLS-1$
            this.jdbcURLText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
            this.jdbcURLText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( Composite parent ) {
        display = parent.getDisplay();
        
        Composite pnlMain = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(pnlMain);
        GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(pnlMain);

        // Add display name component
        constructDisplayNamePanel(pnlMain);
        constructIServerCreationPanel(pnlMain);
        constructTeiidJdbcConnectionPanel(pnlMain);

        setControl(pnlMain);

        // register with the help system
        IWorkbenchHelpSystem helpSystem = DqpUiPlugin.getDefault().getWorkbench().getHelpSystem();
        // TODO bring over Help contexts from Publishing Plugin
        // helpSystem.setHelp(pnlMain, SERVER_DIALOG_HELP_CONTEXT);
        helpSystem.setHelp(pnlMain, "SERVER_DIALOG_HELP_CONTEXT"); //$NON-NLS-1$
    }

    /**
     * @return the server manager obtained from the wizard
     */
    private ITeiidServerManager getServerManager() {
        return ((ServerWizard) getWizard()).getServerManager();
    }
    
    /**
     * This in internal and should only be called by threads other 
     * than the UI thread.
     * 
     * @param server
     */
    private void createTeiidServer(final IServer server) {
        if (Thread.currentThread() == display.getThread())
            throw new RuntimeException("Cannot create the teiid server on the UI thread"); //$NON-NLS-1$
        
        TeiidServerAdapterFactory factory = new TeiidServerAdapterFactory();
        /*
         * Adapt the IServer to a teiid server but have to consider that the former has 
         * probably not been started yet so a 'default server' will be returned. Only when
         * the server is started will the settings be updated correctly.
         */
        teiidServer = factory.adaptServer(server, ServerOptions.NO_CHECK_CONNECTION);
        if (teiidServer != null) {
            teiidServer.getParent().addServerListener(TeiidParentServerListener.getInstance());
        }

        display.asyncExec(new Runnable() {
            
                @Override
                public void run() {
        
                    setErrorMessage(null);
        
                    if (teiidServer == null) {
                        String msg = UTIL.getString("serverPageNewServerNotCompatibleWithTeiid"); //$NON-NLS-1$
                        setErrorMessage(msg);
                        return;
                    }
                    
                    jbossServerNameText.setText(server.getName());

                    ITeiidJdbcInfo teiidJdbcInfo = teiidServer.getTeiidJdbcInfo();
                    
                    if (ITeiidJdbcInfo.DEFAULT_JDBC_USERNAME.equals(jdbcUsernameText.getText()))
                        jdbcUsernameText.setText(teiidJdbcInfo.getUsername());
                    
                    if (ITeiidJdbcInfo.DEFAULT_JDBC_PASSWORD.equals(jdbcPasswordText.getText()))
                        jdbcPasswordText.setText(teiidJdbcInfo.getPassword());
                   
                    jdbcURLText.setText(teiidJdbcInfo.getUrl());
                    
                    String displayName = displayNameText.getText();
                    if (displayName != null && displayName.length() > 0)
                        teiidServer.setCustomLabel(displayName);
                    
                    updateState();
                }
        });
    }

    /**
     * Handler for when the user control value is modified
     * 
     * @param newUser the new user value
     */
    private void handlePropertiesModified() {
        if (teiidServer != null) {
            teiidServer.setCustomLabel(displayNameText.getText());
            
            ITeiidJdbcInfo teiidJdbcInfo = teiidServer.getTeiidJdbcInfo();        
            teiidJdbcInfo.setUsername(jdbcUsernameText.getText());
            teiidJdbcInfo.setPassword(jdbcPasswordText.getText());
            
            jdbcURLText.setText(teiidJdbcInfo.getUrl());
        }
        
        updateState();
    }

    /**
     * Updates message, message icon, and OK button enablement based on validation results
     */
    private void updateState() {

        // get the current status
        validate();

        // update OK/Finish button and test button enablement
        setPageComplete(this.status.getSeverity() != IStatus.ERROR);

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
        
        if (teiidServer == null)
            return;
        
        // validate JDBC connection info
        this.status = teiidServer.getTeiidJdbcInfo().validate();

        if (!this.status.isOK()) {
            return;
        }

        // now check to see if a server is already registered
        if (getServerManager().isRegistered(teiidServer)) {
            status = new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, 
                                UTIL.getString("serverExistsMsg", teiidServer)); //$NON-NLS-1$
            return;
        }

        // if necessary, check custom label
        if (StringUtilities.isEmpty(teiidServer.getCustomLabel())) {
            this.status = new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, 
                                     UTIL.getString("serverPageEmptyCustomLabelMsg")); //$NON-NLS-1$
        }
    }
    
    @Override
    public boolean isPageComplete() {
        return teiidServer != null && IStatus.OK == this.status.getSeverity();
    }

    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);

        if (visible) {
            setDescription(UTIL.getString("serverPageDescription")); //$NON-NLS-1$
            
            // set initial state
            validate();
        }
    }
    
    @Override
    public void dispose() {
        DqpPlugin.getInstance().getServersProvider().removeServerLifecycleListener(serverLifecycleListener);
    }
    
    boolean shouldAutoConnect() {
        // TODO fix
        return true;
    }

    /**
     * @return the server represented by the dialog inputs
     */
    ITeiidServer getServer() {
        return teiidServer;
    }
    
    /**
     * Processing done after wizard 'Finish' button is clicked. Wizard was not cancelled.
     */
    IStatus performFinish() {
        // Check we have aquired the latest properties 
        // in case the focus listener caused some anomoly
        handlePropertiesModified();
        
        if (! isPageComplete()) {
            return status;
        }
        
        // Add server to the registry
        status = getServerManager().addServer(teiidServer);
        
        // Server has not been started so open the editors for the user to review
        ServerUIPlugin.editServer(teiidServer.getParent());
        DqpUiPlugin.editTeiidServer(teiidServer);
        
        return status;
    }

    /**
     * Processing done after wizard 'Cancel' button is clicked. Wizard was cancelled
     */
    public void performCancel() {
        if (teiidServer == null)
            return;
        
        try {
            IServer server = teiidServer.getParent();
            if (server != null)
                server.delete();
            
        } catch (CoreException ex) {
            UTIL.log(ex);
        }
    }
}
