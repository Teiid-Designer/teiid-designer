/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.server.editor;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.editor.ServerResourceCommandManager;
import org.teiid.designer.core.loading.ComponentLoadingManager;
import org.teiid.designer.core.loading.IManagedLoading;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.IServersProvider;
import org.teiid.designer.runtime.TeiidServerFactory;
import org.teiid.designer.runtime.TeiidServerFactory.ServerOptions;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterFactory;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.util.ErrorHandler;

/**
 * @since 8.0
 */
public class TeiidServerEditor extends EditorPart implements IManagedLoading {

    /**
     * Identifier of this editor
     */
    public static final String EDITOR_ID = TeiidServerEditor.class.getCanonicalName();

    /**
     * Flag indicating editor's dirty status
     */
    private boolean dirty = false;

    private IServerWorkingCopy parentServerWorkingCopy;

    private IServer parentServer;

    private ITeiidServer teiidServer;

    private ScrolledForm form;
    private FormToolkit toolkit;

    private Composite contentsPanel;

    private ProgressBar progressBar;

    private Text customNameText;

    private Label jbServerNameLabel;

    private Label hostNameLabel;
    
    private Combo versionValueCombo;

    private Label adminDescriptionLabel;
    
    private Text adminUserNameText;

    private Text adminPasswdText;
    
    private Control adminPort;
    
    private Button adminSSLCheckbox;

    private Hyperlink adminPingHyperlink;

    private Label adminPingResultLabel;

    private Text jdbcUserNameText;

    private Text jdbcPasswdText;

    private Control jdbcPort;
    
    private Button jdbcSSLCheckbox;

    private Hyperlink jdbcPingHyperlink;

    private Label jdbcPingResultLabel;

    private ServerResourceCommandManager commandManager;

    /**
     * Listener that updates the editor if the server is changed externally to the editor
     */
    private IExecutionConfigurationListener excutionConfigListener = new IExecutionConfigurationListener() {
        
        @Override
        public void configurationChanged(final ExecutionConfigurationEvent event) {

            TeiidServerEditor.this.getSite().getShell().getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    switch (event.getEventType()) {
                        case REFRESH:
                        case UPDATE:
                            refreshDisplayValues();
                            break;
                        case REMOVE:
                            disposeContents();
                            manageLoad(new Properties());
                            break;
                        case DEFAULT:
                        case ADD:
                        default:
                            // do nothing
                    }
                }
            });
        }
    };

    private class TeiidServerCommand extends ServerCommand {

        /**
         * @param server
         */
        public TeiidServerCommand(IServerWorkingCopy server) {
            super(server, "TeiidServerCommand"); //$NON-NLS-1$
        }

        @Override
        public void execute() {
            // Nothing to execute.
            // Merely a dummy command for signalling the dirty state
        }

        @Override
        public void undo() {
            // Not undoable
        }
        
    }

    /**
     * Listener that sets the editor dirty on typing
     */
    private KeyAdapter dirtyKeyListener = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 's')) {
                // avoid setting dirty when user used keys to save
                return;
            }
            
            if (e.keyCode == SWT.CTRL) {
                // if ctrl-s was pressed then ctrl is released last
                return;
            }

            TeiidServerEditor.this.setDirty();
            execute(new TeiidServerCommand(parentServerWorkingCopy));
        }
    };
    
    /**
     * Listener that sets the editor dirty on checking of one of the checkboxes
     */
    private SelectionListener dirtySelectionListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            TeiidServerEditor.this.setDirty();
            execute(new TeiidServerCommand(parentServerWorkingCopy));
        }
    };

    /**
     * Listener that closes the editor if the server is deleted
     */
    private IServerLifecycleListener serverLifecycleListener = new ServerLifecycleAdapter() {
        @Override
        public void serverChanged(IServer server) {
            if (! parentServer.equals(server))
                return;

            refreshDisplayValues();
        }
    };

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        setSite(site);
        setInput(input);

        /*
         * Needs to be delayed since the editor could be open upon startup
         * and the server manager has not yet restored its state
         */
        if (input instanceof IServerEditorPartInput) {
            IServerEditorPartInput serverInput = (IServerEditorPartInput) input;
            commandManager = ((ServerEditorPartInput) input).getServerCommandManager();
            parentServerWorkingCopy = serverInput.getServer();
            parentServer = parentServerWorkingCopy.getOriginal();
        }    
    }
    
    @Override
    public void createPartControl(Composite parent) {
        String title = UTIL.getString("TeiidServerEditor.title"); //$NON-NLS-1$
        this.setPartName(title);

        toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        toolkit.decorateFormHeading(form.getForm());        
        form.setText(title);
        GridLayoutFactory.fillDefaults().applyTo(form.getBody());

        progressBar = new ProgressBar(form.getBody(), SWT.SMOOTH | SWT.INDETERMINATE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true,  true).applyTo(progressBar);

        ComponentLoadingManager manager = ComponentLoadingManager.getInstance();
        manager.manageLoading(this);
    }

    /**
     * Populates the editor with the Teiid Instance's properties
     */
    @Override
    public void manageLoad(Properties args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressBar.dispose();

                contentsPanel = toolkit.createComposite(form.getBody());
                GridLayoutFactory.fillDefaults().numColumns(1).spacing(10, 0).applyTo(contentsPanel);
                GridDataFactory.fillDefaults().grab(true, true).applyTo(contentsPanel);

                IServersProvider serversProvider = DqpPlugin.getInstance().getServersProvider();
                serversProvider.addServerLifecycleListener(serverLifecycleListener);

                getServerManager().addListener(excutionConfigListener);

                try {
                    TeiidServerAdapterFactory adapterFactory = new TeiidServerAdapterFactory();
                    if (parentServer.getServerState() == IServer.STATE_STARTED)
                        // If server is started we can be more adventurous in what to display since we can ask
                        // the server whether teiid has been installed.
                        teiidServer = adapterFactory.adaptServer(parentServer, ServerOptions.ADD_TO_REGISTRY);
                    else {
                        // Cannot ask a lot except whether the server is a JBoss Server
                        teiidServer = adapterFactory.adaptServer(parentServer,
                                                             ServerOptions.NO_CHECK_CONNECTION,
                                                             ServerOptions.ADD_TO_REGISTRY);
                    }
                } catch (Exception ex) {
                    ErrorHandler.toExceptionDialog(ex);
                }

                if (teiidServer != null) {
                    // insert sections
                    createOverviewSection(contentsPanel);
                    createAdminSection(contentsPanel);
                    createJDBCSection(contentsPanel);
                } else {
                    Label noTeiidLabel = toolkit.createLabel(contentsPanel, UTIL.getString("TeiidServerEditor.noTeiidServer")); //$NON-NLS-1$
                    blueForeground(noTeiidLabel);
                    GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true,  true).applyTo(noTeiidLabel);
                }

                form.reflow(true);
            }
        };

        UiUtil.runInSwtThread(runnable, true);
    }

    private void disposeContents() {
        if (contentsPanel != null)
            contentsPanel.dispose();
    }

    private void blueForeground(Control control) {
        control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
    }

    private boolean isSevenServer() {
        if (teiidServer == null)
            return false;
        
        return teiidServer.getServerVersion().isSevenServer();
    }

    /**
     * @param parent
     * @param toolkit
     */
    private void createOverviewSection(Composite parent) {
        Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED|ExpandableComposite.TITLE_BAR);
        section.setText(UTIL.getString("TeiidServerOverviewSection.title")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
        
        Label customNameLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.customLabel")); //$NON-NLS-1$
        blueForeground(customNameLabel);
        
        customNameText = toolkit.createText(composite, teiidServer.getCustomLabel() != null ? teiidServer.getCustomLabel() : ""); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(customNameText);
        customNameText.addKeyListener(dirtyKeyListener);
        
        Label hostLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.hostLabel")); //$NON-NLS-1$
        blueForeground(hostLabel);
        
        hostNameLabel = toolkit.createLabel(composite, teiidServer.getHost());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(hostNameLabel);
        blueForeground(hostNameLabel);
        
        Label versionLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.versionLabel")); //$NON-NLS-1$
        blueForeground(versionLabel);

        versionValueCombo = new Combo(composite, SWT.DROP_DOWN);
        versionValueCombo.setToolTipText(UTIL.getString("TeiidServerOverviewSection.versionValueTooltip")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(false, false).applyTo(versionValueCombo);
        versionValueCombo.addKeyListener(dirtyKeyListener);
        versionValueCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TeiidServerEditor.this.setDirty();
                execute(new TeiidServerCommand(parentServerWorkingCopy));
            }
        });

        // Populate the version value combo with existing server versions
        // and the current teiid server version
        List<String> serverVersions = new ArrayList<String>();
        serverVersions.add(teiidServer.getServerVersion().toString());
        try {
            Collection<ITeiidServerVersion> registeredServerVersions = TeiidRuntimeRegistry.getInstance().getRegisteredServerVersions();
            for (ITeiidServerVersion version : registeredServerVersions) {
                serverVersions.add(version.toString());
            }
        } catch (Exception ex) {
            serverVersions.addAll(TeiidServerVersion.DEFAULT_TEIID_SERVER_IDS);
        }

        Collections.sort(serverVersions, Collections.reverseOrder());
        versionValueCombo.setItems(serverVersions.toArray(new String[0]));
        versionValueCombo.setText(teiidServer.getServerVersion().toString());

        // Can only edit if teiid server has been stopped
        versionValueCombo.setEnabled(! teiidServer.isConnected());

        Label jbLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.jbLabel")); //$NON-NLS-1$
        blueForeground(jbLabel);
        
        String jbServerName = parentServer != null ? parentServer.getName() : ""; //$NON-NLS-1$
        jbServerNameLabel = toolkit.createLabel(composite, jbServerName);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jbServerNameLabel);
        blueForeground(jbServerNameLabel);

        toolkit.paintBordersFor(composite);
        section.setClient(composite);
    }
    
    private void createAdminSection(Composite parent) {
        Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED|ExpandableComposite.TITLE_BAR);
        section.setText(UTIL.getString("TeiidServerAdminSection.title")); //$NON-NLS-1$ 
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
       
        if (isSevenServer()) {
            
            Label userNameLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerAdminSection.userNameLabel")); //$NON-NLS-1$
            blueForeground(userNameLabel);
            
            adminUserNameText = toolkit.createText(composite, teiidServer.getTeiidAdminInfo().getUsername());
            GridDataFactory.fillDefaults().grab(true, false).applyTo(adminUserNameText);
            adminUserNameText.addKeyListener(dirtyKeyListener);
            
            Label passwdLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerAdminSection.passwordLabel")); //$NON-NLS-1$
            blueForeground(passwdLabel);
            
            adminPasswdText = toolkit.createText(composite, teiidServer.getTeiidAdminInfo().getPassword(), SWT.PASSWORD);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(adminPasswdText);
            adminPasswdText.addKeyListener(dirtyKeyListener);
            
            Label portLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerAdminSection.portLabel")); //$NON-NLS-1$
            blueForeground(portLabel);
            
            adminPort = toolkit.createText(composite, teiidServer.getTeiidAdminInfo().getPort());
            GridDataFactory.fillDefaults().grab(true, false).applyTo(adminPort);
            adminPort.addKeyListener(dirtyKeyListener);
            
            Label checkboxLabel = toolkit.createLabel(composite, UTIL.getString("serverPageSecureConnAdminLabel")); //$NON-NLS-1$
            GridDataFactory.fillDefaults().grab(true, false).applyTo(checkboxLabel);
            blueForeground(checkboxLabel);
            
            adminSSLCheckbox = toolkit.createButton(composite, "", SWT.CHECK); //$NON-NLS-1$
            adminSSLCheckbox.setSelection(teiidServer.getTeiidAdminInfo().isSecure());
            adminSSLCheckbox.addSelectionListener(dirtySelectionListener);
            GridDataFactory.fillDefaults().grab(false, false).applyTo(adminSSLCheckbox);
        } else {
            adminDescriptionLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerAdminSection.description")); //$NON-NLS-1$
            blueForeground(adminDescriptionLabel);
            GridDataFactory.fillDefaults().grab(false, false).span(2, 1).applyTo(adminDescriptionLabel);

            Label portLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerAdminSection.portLabel")); //$NON-NLS-1$
            blueForeground(portLabel);
            
            adminPort = toolkit.createLabel(composite, teiidServer.getTeiidAdminInfo().getPort());
        }
        
        blueForeground(adminPort);
        
        adminPingHyperlink = toolkit.createHyperlink(composite, UTIL.getString("TeiidServerAdminSection.testPingButtonLabel"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(adminPingHyperlink);
        adminPingHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
            
            @Override
            public void linkActivated(HyperlinkEvent e) {
                doSave(null);

                IStatus status = teiidServer.testPing();
                adminPingResultLabel.setText(status.getMessage());
                form.layout(true, true);
            }
        });
        
        adminPingResultLabel = toolkit.createLabel(composite, "", SWT.WRAP); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).grab(true, true).hint(325, SWT.DEFAULT).applyTo(adminPingResultLabel);
        
        toolkit.paintBordersFor(composite);
        section.setClient(composite);
    }

    /**
     * @param parent
     */
    private void createJDBCSection(Composite parent) {
        Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED|ExpandableComposite.TITLE_BAR);
        section.setText(UTIL.getString("TeiidServerJDBCSection.title")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
        
        Label userNameLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.userNameLabel")); //$NON-NLS-1$
        blueForeground(userNameLabel);
        
        jdbcUserNameText = toolkit.createText(composite, teiidServer.getTeiidJdbcInfo().getUsername());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcUserNameText);
        jdbcUserNameText.addKeyListener(dirtyKeyListener);
        
        Label passwdLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.passwordLabel")); //$NON-NLS-1$
        blueForeground(passwdLabel);
        
        jdbcPasswdText = toolkit.createText(composite, teiidServer.getTeiidJdbcInfo().getPassword(), SWT.PASSWORD);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPasswdText);
        jdbcPasswdText.addKeyListener(dirtyKeyListener);
        
        Label portLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.portLabel")); //$NON-NLS-1$
        blueForeground(portLabel);
        
        if (isSevenServer()) {
            // Only if Teiid Instance is version 7 will the port be editable since subsequent 
            // versions the port can be gathered from the server
            jdbcPort = toolkit.createText(composite, teiidServer.getTeiidJdbcInfo().getPort());
            GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPort);
            jdbcPort.addKeyListener(dirtyKeyListener);
        } else {
            jdbcPort = toolkit.createLabel(composite, teiidServer.getTeiidJdbcInfo().getPort());
        }
        blueForeground(jdbcPort);
        
        Label checkboxLabel = toolkit.createLabel(composite, UTIL.getString("serverPageSecureConnJDBCLabel")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(checkboxLabel);
        blueForeground(checkboxLabel);
        
        jdbcSSLCheckbox = toolkit.createButton(composite, "", SWT.CHECK); //$NON-NLS-1$
        jdbcSSLCheckbox.setSelection(teiidServer.getTeiidJdbcInfo().isSecure());
        blueForeground(jdbcSSLCheckbox);
        jdbcSSLCheckbox.addSelectionListener(dirtySelectionListener);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(jdbcSSLCheckbox);
        
        jdbcPingHyperlink = toolkit.createHyperlink(composite, UTIL.getString("TeiidServerJDBCSection.testPingButtonLabel"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPingHyperlink);
        jdbcPingHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {                
                doSave(null);

                IStatus status = teiidServer.testJDBCPing(teiidServer.getHost(), 
                                                                                 teiidServer.getTeiidJdbcInfo().getPort(), 
                                                                                 teiidServer.getTeiidJdbcInfo().getUsername(), 
                                                                                 teiidServer.getTeiidJdbcInfo().getPassword());
                jdbcPingResultLabel.setText(status.getMessage());
                form.layout(true, true);
            }
        });
        
        jdbcPingResultLabel = toolkit.createLabel(composite, "", SWT.WRAP); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).grab(true, true).hint(325, SWT.DEFAULT).applyTo(jdbcPingResultLabel);
        
        toolkit.paintBordersFor(composite);
        section.setClient(composite);
    }

    private void execute(ServerCommand command) {
        commandManager.execute(command);
    }

    private void refreshDisplayValues() {
        if (teiidServer == null || form.isDisposed())
            return;

        customNameText.setText(teiidServer.getCustomLabel() != null ? teiidServer.getCustomLabel() : ""); //$NON-NLS-1$
        hostNameLabel.setText(teiidServer.getHost());
        versionValueCombo.setText(teiidServer.getServerVersion().toString());
        // Can only edit if teiid server has been stopped
        versionValueCombo.setEnabled(! teiidServer.isConnected());
        jbServerNameLabel.setText(parentServer != null ? parentServer.getName() : ""); //$NON-NLS-1$

        ITeiidAdminInfo teiidAdminInfo = teiidServer.getTeiidAdminInfo();
        ITeiidJdbcInfo teiidJdbcInfo = teiidServer.getTeiidJdbcInfo();

        if (adminUserNameText != null) {
            adminUserNameText.setText(teiidAdminInfo.getUsername());
        }

        if (adminPasswdText != null) {
            adminPasswdText.setText(teiidAdminInfo.getPassword());
        }

        if (adminPort instanceof Text) {
            ((Text) adminPort).setText(teiidAdminInfo.getPort());
        } else if (adminPort instanceof Label) {
            ((Label) adminPort).setText(teiidAdminInfo.getPort());
        }

        if (adminSSLCheckbox != null) {
            adminSSLCheckbox.setSelection(teiidAdminInfo.isSecure());
        }

        jdbcUserNameText.setText(teiidJdbcInfo.getUsername());
        jdbcPasswdText.setText(teiidJdbcInfo.getPassword());
                
        if (jdbcPort instanceof Text) {
            ((Text) jdbcPort).setText(teiidJdbcInfo.getPort());
        } else if (jdbcPort instanceof Label) {
            ((Label) jdbcPort).setText(teiidJdbcInfo.getPort());
        }

        jdbcSSLCheckbox.setSelection(teiidJdbcInfo.isSecure());
    }

    private ITeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }

    @Override
    public void dispose() {
        getServerManager().removeListener(excutionConfigListener);

        IServersProvider serversProvider = DqpPlugin.getInstance().getServersProvider();
        serversProvider.removeServerLifecycleListener(serverLifecycleListener);
        
        super.dispose();
    }
    
    @Override
    public void setFocus() {
        if(customNameText != null && ! customNameText.isDisposed()) {
            customNameText.setFocus();
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        if (teiidServer == null)
            return;

        ITeiidServerVersion newTeiidServerVersion = teiidServer.getServerVersion();
        if (versionValueCombo.getText() != null)
            newTeiidServerVersion = new TeiidServerVersion(versionValueCombo.getText());

        // Overwrite the properties of the Teiid Instance
        teiidServer.setCustomLabel(customNameText.getText());

        TeiidServerFactory teiidServerFactory = new TeiidServerFactory();

        List<ServerOptions> serverOptions = new ArrayList<ServerOptions>();
        if (adminSSLCheckbox != null && adminSSLCheckbox.getSelection())
            serverOptions.add(ServerOptions.ADMIN_SECURE_CONNECTION);
        if (jdbcSSLCheckbox.getSelection())
            serverOptions.add(ServerOptions.JDBC_SECURE_CONNECTION);

        ITeiidServer newTeiidServer = teiidServerFactory.createTeiidServer(
                                             newTeiidServerVersion,
                                             getServerManager(),
                                             teiidServer.getParent(),
                                             teiidServer.getTeiidAdminInfo().getPort(),
                                             adminUserNameText != null ? adminUserNameText.getText() : teiidServer.getTeiidAdminInfo().getUsername(),
                                             adminPasswdText != null ? adminPasswdText.getText() : teiidServer.getTeiidAdminInfo().getPassword(),
                                             jdbcPort instanceof Text ? ((Text) jdbcPort).getText() : teiidServer.getTeiidJdbcInfo().getPort(),
                                             jdbcUserNameText.getText(),
                                             jdbcPasswdText.getText(),
                                             serverOptions.toArray(new ServerOptions[0]));

        teiidServer.update(newTeiidServer);

        dirty = false;
        firePropertyChange(IEditorPart.PROP_DIRTY);

        getServerManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(teiidServer));
    }

    @Override
    public void doSaveAs() {
        // do nothing
    }
    
    private void setDirty() {
        dirty = true;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

}
