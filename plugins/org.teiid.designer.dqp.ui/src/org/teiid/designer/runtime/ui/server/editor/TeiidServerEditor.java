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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.editor.ServerResourceCommandManager;
import org.jboss.ide.eclipse.as.core.server.internal.JBossServer;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.loading.ComponentLoadingManager;
import org.teiid.designer.core.loading.IManagedLoading;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.IServersProvider;
import org.teiid.designer.runtime.TeiidParentServerListener;
import org.teiid.designer.runtime.TeiidServerFactory;
import org.teiid.designer.runtime.TeiidServerFactory.ServerOptions;
import org.teiid.designer.runtime.adapter.JBoss7ServerUtil;
import org.teiid.designer.runtime.adapter.JBossServerUtil;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterFactory;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion.VersionID;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.util.ErrorHandler;

/**
 * @since 8.0
 */
public class TeiidServerEditor extends EditorPart implements IManagedLoading, IServerListener {

    /**
     * Identifier of this editor
     */
    public static final String EDITOR_ID = TeiidServerEditor.class.getCanonicalName();
    
    private static final String EMPTY_STRING = CoreStringUtil.Constants.EMPTY_STRING;
    
    private static final String NOT_CONNECTED = UTIL.getString("TeiidServerJDBCSection.notConnectedLabel"); //$NON-NLS-1$
    
    private static final int PORT_MIN = 1;
    private static final int PORT_MAX = 65535;

    /**
     * Flag indicating editor's dirty status
     */
    private boolean dirty = false;
    
    private boolean active = false;

    private IServerWorkingCopy parentServerWorkingCopy;

    private IServer parentServer;

    private ITeiidServer teiidServer;

    private ScrolledForm form;
    private FormToolkit toolkit;

    private Composite contentsPanel;

    private ProgressBar progressBar;

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
    
    private Text jdbcPortOverride;
    
    private Button jdbcSSLCheckbox;

    private Hyperlink jdbcPingHyperlink;

    private Label jdbcPingResultLabel;
    
    private Section overviewSection;
    
    private Section adminSection;
    
    private Section jdbcSection;
    
    private Label noTeiidLabel;

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
                        	// Put the refesh on Swt thread
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {		
                                	refreshDisplayValues(teiidServer.getTeiidAdminInfo().getPassword(), 
                                			teiidServer.getTeiidJdbcInfo().getPassword());
                                }
                            };
                            UiUtil.runInSwtThread(runnable, true);
                            break;
                        case REMOVE:
                            disposeContents();
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
    private ModifyListener dirtyModifyListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent arg0) {
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

            if (form.getBody().isDisposed())  return;
            
//            try {
//				TeiidServerAdapterFactory adapterFactory = new TeiidServerAdapterFactory();
//				if (parentServer.getServerState() == IServer.STATE_STARTED) {
//					ITeiidServer teiidServer = adapterFactory.adaptServer(parentServer);
//					if( teiidServer == null ) {
//						disposeContents();
//						
//		                buildNoTeiidLabel(true);
//		                
//						return;
//					}
//				} else {
//					
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
            
            resetServerEditorPanel();
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
            TeiidParentServerListener.getInstance().addRegisteredParentListener(this);
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

                if (form.getBody().isDisposed())
                    return;

                IServersProvider serversProvider = DqpPlugin.getInstance().getServersProvider();
                serversProvider.addServerLifecycleListener(serverLifecycleListener);

                getServerManager().addListener(excutionConfigListener);

                String adminPWD = null;
                String jdbcPWD = null;
                
                try {
                    TeiidServerAdapterFactory adapterFactory = new TeiidServerAdapterFactory();
                    if (parentServer.getServerState() == IServer.STATE_STARTED) {
                        // If server is started we can be more adventurous in what to display since we can ask
                        // the server whether teiid has been installed.
                        teiidServer = adapterFactory.adaptServer(parentServer, ServerOptions.ADD_TO_REGISTRY);
                        if( teiidServer != null ) {
                            if( !JBossServerUtil.isTeiidServer(parentServer, null) ) {
                            	getServerManager().removeServer(teiidServer);
                            	teiidServer = null;
                        	} else {
		                    	adminPWD = teiidServer.getTeiidAdminInfo().getPassword();
		                    	jdbcPWD = teiidServer.getTeiidJdbcInfo().getPassword();
                        	}
                        }
                    } else {
                        // Cannot ask a lot except whether the server is a JBoss Server
                        teiidServer = adapterFactory.adaptServer(parentServer,
                                                             ServerOptions.NO_CHECK_CONNECTION,
                                                             ServerOptions.ADD_TO_REGISTRY);
                        // password may still be a passToken only
                        if( teiidServer != null ) {
	                        adminPWD = teiidServer.getTeiidAdminInfo().getPassword();
	                        jdbcPWD = teiidServer.getTeiidJdbcInfo().getPassword();
                        }
                    }
                } catch (Exception ex) {
                	if(! ex.getMessage().contains(TeiidParentServerListener.JBAS013493_CODE)) {
                		ErrorHandler.toExceptionDialog(ex);
                	}
                }

                if (teiidServer != null) {
                    // insert sections
                    buildTeiidServerPanel(false);
                } else {
                    buildNoTeiidLabel(false);
                }

                form.reflow(true);
                
                active = true;
                
                if( teiidServer != null ) {
                	final String aPWD = adminPWD;
                	final String jPWD = jdbcPWD;
                	// Put the refesh on Swt thread
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                        	refreshDisplayValues(aPWD, jPWD);
                        }
                    };
                    UiUtil.runInSwtThread(runnable, true);
                }
            }
        };

        UiUtil.runInSwtThread(runnable, true);
    }
    
    private void buildTeiidServerPanel(boolean doLayout) {
        // insert sections
        contentsPanel = toolkit.createComposite(form.getBody());
        GridLayoutFactory.fillDefaults().numColumns(1).spacing(10, 0).applyTo(contentsPanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(contentsPanel);
        
        createOverviewSection(contentsPanel);
        createAdminSection(contentsPanel);
        createJDBCSection(contentsPanel);
        
    	form.setText(UTIL.getString("TeiidServerEditor.title"));//$NON-NLS-1$
    	form.setImage(null);
    	
        if( doLayout ) {
        	contentsPanel.layout(true);
        }
    }

    private void buildNoTeiidLabel(boolean doLayout)  {
        contentsPanel = toolkit.createComposite(form.getBody());
        GridLayoutFactory.fillDefaults().numColumns(1).spacing(10, 0).applyTo(contentsPanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(contentsPanel);
        
    	noTeiidLabel = toolkit.createLabel(contentsPanel, UTIL.getString("TeiidServerEditor.noTeiidServer")); //$NON-NLS-1$
        blueForeground(noTeiidLabel);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true,  true).applyTo(noTeiidLabel);
        
    	form.setText(UTIL.getString("TeiidServerEditor.noTeiidTitle")); //$NON-NLS-1$
    	form.setImage(DqpUiPlugin.getDefault().getImage(DqpUiConstants.Images.WARNING_ICON));
    	
        if( doLayout ) {
        	contentsPanel.layout(true);
        }
    }

    private void disposeContents() {
        if (contentsPanel != null) {
            contentsPanel.dispose();
            jbServerNameLabel = null;
            hostNameLabel = null;
            versionValueCombo = null;
            adminDescriptionLabel = null;
            adminUserNameText = null;
            adminPasswdText = null;
            adminPort = null;            
            adminSSLCheckbox = null;
            adminPingHyperlink = null;
            adminPingResultLabel = null;
            jdbcUserNameText = null;
            jdbcPasswdText = null;
            jdbcPort = null;
            jdbcPortOverride = null;
            jdbcSSLCheckbox = null;
            jdbcPingHyperlink = null;
            jdbcPingResultLabel = null;
            overviewSection = null;
            adminSection = null;            
            jdbcSection = null;
        }
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
        overviewSection = section;
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

        Label jbLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.jbLabel")); //$NON-NLS-1$
        blueForeground(jbLabel);
        
        String jbServerName = parentServer != null ? parentServer.getName() : ""; //$NON-NLS-1$
        jbServerNameLabel = toolkit.createLabel(composite, jbServerName);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jbServerNameLabel);
        blueForeground(jbServerNameLabel);

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
            Collection<ITeiidServerVersion> registeredServerVersions = TeiidRuntimeRegistry.getInstance().getSupportedVersions();
            serverVersions = TeiidServerVersion.orderVersions(registeredServerVersions, true);
        } catch (Exception ex) {
            for (VersionID versionId : VersionID.values()) {
                serverVersions.add(versionId.toString());
            }
        }

        versionValueCombo.setItems(serverVersions.toArray(new String[0]));
        versionValueCombo.setText(teiidServer.getServerVersion().toString());
        versionValueCombo.addModifyListener(dirtyModifyListener);

        // Can only edit if teiid server has been stopped
        versionValueCombo.setEnabled(! teiidServer.isConnected());

        toolkit.paintBordersFor(composite);
        section.setClient(composite);
    }
    
    private void createAdminSection(Composite parent) {
        Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED|ExpandableComposite.TITLE_BAR);
        section.setText(UTIL.getString("TeiidServerAdminSection.title")); //$NON-NLS-1$ 
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        adminSection = section;
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
       
        if (isSevenServer()) {
            
            Label userNameLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerAdminSection.userNameLabel")); //$NON-NLS-1$
            blueForeground(userNameLabel);
            
            adminUserNameText = toolkit.createText(composite, teiidServer.getTeiidAdminInfo().getUsername());
            GridDataFactory.fillDefaults().grab(true, false).applyTo(adminUserNameText);
            adminUserNameText.addModifyListener(dirtyModifyListener);
            
            Label passwdLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerAdminSection.passwordLabel")); //$NON-NLS-1$
            blueForeground(passwdLabel);
            
            adminPasswdText = toolkit.createText(composite, teiidServer.getTeiidAdminInfo().getPassword(), SWT.PASSWORD);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(adminPasswdText);
            adminPasswdText.addModifyListener(dirtyModifyListener);
            
            Label portLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerAdminSection.portLabel")); //$NON-NLS-1$
            blueForeground(portLabel);
            
            Text adminPortText = toolkit.createText(composite, teiidServer.getTeiidAdminInfo().getPort());
            GridDataFactory.fillDefaults().grab(true, false).applyTo(adminPortText);
            adminPortText.addModifyListener(dirtyModifyListener);
            adminPort = adminPortText;
            
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
        jdbcSection = section;
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
        
        Label userNameLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.userNameLabel")); //$NON-NLS-1$
        blueForeground(userNameLabel);
        
        jdbcUserNameText = toolkit.createText(composite, teiidServer.getTeiidJdbcInfo().getUsername());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcUserNameText);
        jdbcUserNameText.addModifyListener(dirtyModifyListener);

        Label passwdLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.passwordLabel")); //$NON-NLS-1$
        blueForeground(passwdLabel);
        
        jdbcPasswdText = toolkit.createText(composite, teiidServer.getTeiidJdbcInfo().getPassword(), SWT.PASSWORD);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPasswdText);
        jdbcPasswdText.addModifyListener(dirtyModifyListener);
        
        Label portLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.portLabel")); //$NON-NLS-1$
        portLabel.setToolTipText(UTIL.getString("TeiidServerJDBCSection.portToolTip"));  //$NON-NLS-1$
        blueForeground(portLabel);
        
        if (isSevenServer() ) {
            // Only if Teiid Instance is version 7 will the port be editable since subsequent 
            // versions the port can be gathered from the server
            Text jdbcPortText = toolkit.createText(composite, teiidServer.getTeiidJdbcInfo().getPort());
            GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPortText);
            jdbcPortText.addModifyListener(dirtyModifyListener);
            jdbcPort = jdbcPortText;
        } else {
        	String portValue = NOT_CONNECTED;
	        if( teiidServer.isConnected() ) {
	        	portValue = getServerManager().getJdbcPort(teiidServer, false);
	        	if( portValue == null ) {
	        		portValue = NOT_CONNECTED;
	        	}
	        }
            jdbcPort = toolkit.createLabel(composite, portValue);
            jdbcPort.setToolTipText(UTIL.getString("TeiidServerJDBCSection.portToolTip"));  //$NON-NLS-1$

        }
        blueForeground(jdbcPort);
        
        if ( !isSevenServer() ) {
	        Label portOverrideLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.portOverrideLabel")); //$NON-NLS-1$
	        blueForeground(portOverrideLabel);
	        portOverrideLabel.setToolTipText(UTIL.getString("TeiidServerJDBCSection.portOverrideToolTip"));  //$NON-NLS-1$
	        String portOverride = getServerManager().getJdbcPort(teiidServer, true);
	        if( portOverride == null ) {
	        	portOverride = EMPTY_STRING;
	        }
	        
	    	jdbcPortOverride = toolkit.createText(composite, portOverride);
	        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPortOverride);
	        jdbcPortOverride.addModifyListener(dirtyModifyListener);
	        jdbcPortOverride.setToolTipText(UTIL.getString("TeiidServerJDBCSection.portOverrideToolTip"));  //$NON-NLS-1$
        }
        
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

    private void refreshDisplayValues(final String adminPWD, final String jdbcPWD) {
        if (!active || teiidServer == null || form.isDisposed())
            return;

        if( hostNameLabel.isDisposed() ) return;
        
        hostNameLabel.setText(teiidServer.getHost());
        String versionValue = teiidServer.getServerVersion().toString();
        if(! versionValueCombo.getText().equals(versionValue)){
        	versionValueCombo.setText(versionValue);
        }
        // Can only edit if teiid server has been stopped
        versionValueCombo.setEnabled(! teiidServer.isConnected());
        jbServerNameLabel.setText(parentServer != null ? parentServer.getName() : ""); //$NON-NLS-1$

        ITeiidAdminInfo teiidAdminInfo = teiidServer.getTeiidAdminInfo();
        ITeiidJdbcInfo teiidJdbcInfo = teiidServer.getTeiidJdbcInfo();
        
        if( isSevenServer() ) {
	        if (adminUserNameText != null) {
	            setIfDifferent(adminUserNameText, teiidAdminInfo.getUsername() != null ? teiidAdminInfo.getUsername() : EMPTY_STRING);
	        }
	
	        if (adminPasswdText != null) {
	            setIfDifferent(adminPasswdText, adminPWD != null ? adminPWD : EMPTY_STRING);
	        }
	        
	        String portValue = teiidAdminInfo.getPort() != null ? teiidAdminInfo.getPort() : EMPTY_STRING;
	        if (adminPort instanceof Text) {
	            setIfDifferent((Text) adminPort, portValue);
	        } else if (adminPort instanceof Label) {
	            ((Label) adminPort).setText(portValue);
	        }
	
	        if (adminSSLCheckbox != null) {
	            adminSSLCheckbox.setSelection(teiidAdminInfo.isSecure());
	        }
        } else {
	        String portValue = teiidAdminInfo.getPort() != null ? teiidAdminInfo.getPort() : EMPTY_STRING;
	        if (adminPort instanceof Text) {
	            setIfDifferent((Text) adminPort, portValue);
	        } else if (adminPort instanceof Label) {
	            ((Label) adminPort).setText(portValue);
	        }
        }

        setIfDifferent(jdbcUserNameText, teiidJdbcInfo.getUsername() != null ? teiidJdbcInfo.getUsername() : EMPTY_STRING);
        setIfDifferent(jdbcPasswdText, jdbcPWD != null ? jdbcPWD : EMPTY_STRING);
        
        if( isSevenServer() ) {
        	String portValue = teiidJdbcInfo.getPort() != null ? teiidJdbcInfo.getPort() : EMPTY_STRING;
        	if (jdbcPort instanceof Text) {
                setIfDifferent((Text) jdbcPort, portValue);
            } 
        } else {
	        String portValue = NOT_CONNECTED;
	        if( teiidServer.isConnected() ) {
	        	portValue = getServerManager().getJdbcPort(teiidServer, false);
	        	if( portValue == null ) {
	        		portValue = NOT_CONNECTED;
	        	}
	        	((Label) jdbcPort).setText(portValue);
	        }
	        portValue = getServerManager().getJdbcPort(teiidServer, true);
	        if( portValue != null ) {
	        	setIfDifferent(jdbcPortOverride, portValue);
	        }
        }

        jdbcSSLCheckbox.setSelection(teiidJdbcInfo.isSecure());
    }
    
    private void setIfDifferent(Text text, String value){
    	if((value != null) && (! value.equals(text.getText()))){
    		text.setText(value);
    	}
    }

    private ITeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }

    @Override
    public void dispose() {
        getServerManager().removeListener(excutionConfigListener);
        TeiidParentServerListener.getInstance().removeRegisteredParentListener(this);

        IServersProvider serversProvider = DqpPlugin.getInstance().getServersProvider();
        serversProvider.removeServerLifecycleListener(serverLifecycleListener);
        
        super.dispose();
    }
    
    @Override
    public void setFocus() {
        if(versionValueCombo != null && ! versionValueCombo.isDisposed()) {
        	versionValueCombo.setFocus();
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        if (teiidServer == null)
            return;
        
        // VALIDATE ANY UI CONTENT
        String invalidMessage = validate();
        
        if( invalidMessage != null ) {
        	MessageDialog.openError(getSite().getShell(), "Teiid Server Configuration Errors", invalidMessage);
        	return;
        }

        ITeiidServerVersion newTeiidServerVersion = teiidServer.getServerVersion();

        // =========================================================================
        // Make sure that the latest override value is saved to the server manager
        //
        
        updatePortOverride();
        
        // =========================================================================
        
        if (versionValueCombo.getText() != null)
            newTeiidServerVersion = new TeiidServerVersion(versionValueCombo.getText());

        // Overwrite the properties of the Teiid Instance

        TeiidServerFactory teiidServerFactory = new TeiidServerFactory();

        List<ServerOptions> serverOptions = new ArrayList<ServerOptions>();
        if (adminSSLCheckbox != null && adminSSLCheckbox.getSelection())
            serverOptions.add(ServerOptions.ADMIN_SECURE_CONNECTION);
        if (jdbcSSLCheckbox.getSelection())
            serverOptions.add(ServerOptions.JDBC_SECURE_CONNECTION);
        
        // =========================================================================
        // Determine if an override exists and use it instead the discovered PORT
        String finalJdbcPort = EMPTY_STRING;
        if( isSevenServer() ) {
        	finalJdbcPort = jdbcPort instanceof Text ? ((Text) jdbcPort).getText() : teiidServer.getTeiidJdbcInfo().getPort();
        } else {
        	finalJdbcPort = getServerManager().getJdbcPort(teiidServer, true);
	        if( StringUtilities.isEmpty(finalJdbcPort) ) {
	        	finalJdbcPort = jdbcPort instanceof Text ? ((Text) jdbcPort).getText() : teiidServer.getTeiidJdbcInfo().getPort();
	        }
        }
        // =========================================================================
        
        String adminUname = null;
        String adminPwd = null;
        if( isSevenServer() )  {
        	adminUname = adminUserNameText != null ? adminUserNameText.getText() : teiidServer.getTeiidAdminInfo().getUsername();
        	adminPwd = adminPasswdText != null ? adminPasswdText.getText() : teiidServer.getTeiidAdminInfo().getPassword();
        } else {
        	JBossServer jb = (JBossServer) teiidServer.getParent().loadAdapter(JBossServer.class, null);
        	adminUname = jb.getUsername();
        	adminPwd = jb.getPassword();
        }
        
        teiidServer.getTeiidAdminInfo().setAll(
        		teiidServer.getTeiidAdminInfo().getHost(), 
        		teiidServer.getTeiidAdminInfo().getPort(), 
        		adminUname, 
        		adminPwd, 
        		teiidServer.getTeiidAdminInfo().isSecure());
        
        teiidServer.getTeiidJdbcInfo().setAll(
        		teiidServer.getTeiidAdminInfo().getHost(), 
        		finalJdbcPort, 
        		jdbcUserNameText.getText(), 
        		jdbcPasswdText.getText(), 
        		teiidServer.getTeiidAdminInfo().isSecure());

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
    
    /*
     * Update the port override value in the server manager
     */
    private void updatePortOverride() {
    	if ( isSevenServer() ) return;
    	
    	String value = jdbcPortOverride.getText();
    	int port = 0;
    	if( !value.isEmpty() ) {
    		port = Integer.parseInt(value);
    	}
    	
    	getServerManager().setJdbcPort(teiidServer, port, true);
    }
    
    /*
     * Validate any UI content
     */
    private String validate() {
    	if ( isSevenServer() ) return null;
    	String msg = null;
    	
    	// Check jdbc port override value
    	
    	String value = jdbcPortOverride.getText();
    	
    	if( value == null ) {
    		msg = "PORT override undefined";
    	} else if( !value.isEmpty() ) {
    		try {
				int port = Integer.parseInt(value);
				if( port < PORT_MIN || port > PORT_MAX ) {
					msg = UTIL.getString("TeiidServerJDBCSection.invalidPortNumberMessage", PORT_MIN, PORT_MAX);
				}
			} catch (NumberFormatException e) {
				msg = UTIL.getString("TeiidServerJDBCSection.invalidPortNumberMessage", PORT_MIN, PORT_MAX);
			}
    	}
    	
    	return msg;
    }
    
    public void serverChanged(ServerEvent event) {
        int state = event.getState();
        IServer server = event.getServer();

        if (state == IServer.STATE_STOPPING || state == IServer.STATE_STOPPED) {
	        if (! parentServer.equals(server))
	            return;
        }

        resetServerEditorPanel();
    }
    
    private void resetServerEditorPanel() {
    	// Put the refesh on Swt thread
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
        			TeiidServerAdapterFactory adapterFactory = new TeiidServerAdapterFactory();
        			if (parentServer.getServerState() == IServer.STATE_STARTED) {
        				ITeiidServer teiidServer = adapterFactory.adaptServer(parentServer);
        				
        				if( teiidServer == null ) {
        					disposeContents();
        					
        					buildNoTeiidLabel(true);
        	                
        					return;
        				} else if( isTeiidServerInstalled(parentServer, teiidServer)){
                        	if( contentsPanel == null ) {
                        		buildTeiidServerPanel(true);
                        	}
                        	refreshDisplayValues(teiidServer.getTeiidAdminInfo().getPassword(), teiidServer.getTeiidJdbcInfo().getPassword());
        				 } else {
                            getServerManager().removeServer(teiidServer);
                            disposeContents();
                            
                            teiidServer = null;
                            
                            buildNoTeiidLabel(true);
        				}
        			} else {
        				ITeiidServer teiidServer = adapterFactory.adaptServer(parentServer);
        				if( teiidServer != null ) {
	                    	if( contentsPanel == null ) {
	                    		buildTeiidServerPanel(true);
	                    	}
	                    	refreshDisplayValues(teiidServer.getTeiidAdminInfo().getPassword(), teiidServer.getTeiidJdbcInfo().getPassword());
        				} else  {
                            disposeContents();
                            buildNoTeiidLabel(true);
        				}

        			}
        		} catch (Exception e) {
        			if(! e.getMessage().contains(TeiidParentServerListener.JBAS013493_CODE)) {
        				e.printStackTrace();
        			}
        		}
            }
        };
        UiUtil.runInSwtThread(runnable, true);

    }
    
    private boolean isTeiidServerInstalled(IServer parentServer, ITeiidServer teiidServer) throws Exception {
    	JBoss7Server jb7s = (JBoss7Server) parentServer.loadAdapter(JBoss7Server.class, null);
    	if( jb7s != null ) {
    		return JBoss7ServerUtil.isTeiidServer(parentServer, jb7s);
    	} else {
    		JBossServer jbs = (JBossServer) teiidServer.getParent().loadAdapter(JBossServer.class, null);
    		if( jbs != null ) {
    			return JBossServerUtil.isTeiidServer(parentServer, jbs);
    		}
    	}
    	return false;
    }

}
