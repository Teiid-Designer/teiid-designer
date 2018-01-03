/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import java.util.Properties;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.Server;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.loading.ComponentLoadingManager;
import org.teiid.designer.core.loading.IManagedLoading;
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.UiPlugin;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.spi.ITeiidServerVersionListener;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.common.graphics.GlobalUiFontManager;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.forms.FormUtil;

@SuppressWarnings("restriction")
public class DefaultServerSection implements IManagedLoading {
    private FormToolkit toolkit;
    
    private static Image changeServerImage = org.teiid.designer.ui.UiPlugin.getDefault().getImage(PluginConstants.Images.CONFIGURE_ICON);
    private static Image startServerImage = UiPlugin.getDefault().getImage(UiConstants.IMAGES.LAUNCH_RUN);
    private static Image stopServerImage = UiPlugin.getDefault().getImage(UiConstants.IMAGES.LAUNCH_STOP);
    private static Image startServerImageDisabled = UiPlugin.getDefault().getImage(UiConstants.IMAGES.LAUNCH_RUN_DISABLED);
    private static Image stopServerImageDisabled = UiPlugin.getDefault().getImage(UiConstants.IMAGES.LAUNCH_STOP_DISABLED);
    
    private Section defaultServerSection;
    private Composite defaultServerSectionBody;
    private Hyperlink defaultServerLink;
    private ImageHyperlink startServerLink;
    private ImageHyperlink stopServerLink;
    private Label defaultTeiidVersionLabel;
    private Label defaultServerStatusLabel;
    private Label defaultTeiidStatusLabel;
    
    private DataSourcesSection sourcesSection;
    
    private Composite toolbar;
    
    /* Listen for change in default teiid instance */
    private ITeiidServerVersionListener teiidServerVersionListener = new ITeiidServerVersionListener() {

        @Override
        public void serverChanged(ITeiidServer server) {
            if (defaultServerLink == null || defaultServerStatusLabel==null || defaultTeiidVersionLabel==null)
                return;

            UiUtil.runInSwtThread(new Runnable() {
                @Override
                public void run() {
                    setDefaultServerText(ModelerCore.getDefaultServerName());
                    setDefaultServerStatusIcon(ModelerCore.isDefaultParentConnected());
                    boolean connected = ModelerCore.isDefaultTeiidConnected();
                    setDefaultTeiidInstanceStatusIcon(connected);
                    setDefaultTeiidVersionText(ModelerCore.getTeiidServerVersion());
                    updateServerActionBar(connected);
                }
            }, true);

            if (server != null)
                addExecutionConfigurationListener(server.getEventManager());
        }

        @Override
        public void versionChanged(ITeiidServerVersion version) {
            if (defaultTeiidVersionLabel == null)
                return;

            setDefaultTeiidVersionText(version);
        }
    };

    /* Listen for configuration changes to existing default teiid instance */
    private IExecutionConfigurationListener execConfigurationListener = new IExecutionConfigurationListener() {

        @Override
        public void configurationChanged(ExecutionConfigurationEvent event) {
            UiUtil.runInSwtThread(new Runnable() {
                @Override
                public void run() {
                    setDefaultServerText(ModelerCore.getDefaultServerName());
                    setDefaultServerStatusIcon(ModelerCore.isDefaultParentConnected());
                    boolean connected = ModelerCore.isDefaultTeiidConnected();
                    setDefaultTeiidInstanceStatusIcon(connected);
                    setDefaultTeiidVersionText(ModelerCore.getTeiidServerVersion());
                    updateServerActionBar(connected);
                    if( connected ) {
                    	sourcesSection.refresh();
                    }
                }
            }, true);
        }
    };
    
	public DefaultServerSection( FormToolkit toolkit,
            Composite parent,
            DataSourcesSection sourcesSection) {
		super();
		this.toolkit = toolkit;
		
		this.sourcesSection = sourcesSection;

		createSection(parent);
	}

	@SuppressWarnings("unused")
	private void createSection(Composite parent) {

        SECTION : {
            defaultServerSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT | ExpandableComposite.TWISTIE );
            Color bkgdColor = toolkit.getColors().getBackground();
            defaultServerSection.setText(Messages.DefaultServer); //$NON-NLS-1$
            defaultServerSection.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            GridDataFactory.fillDefaults().grab(true, false).applyTo(defaultServerSection);

            defaultServerSectionBody = toolkit.createComposite(defaultServerSection);
	        GridLayout layout = new GridLayout(2, false);
	        layout.numColumns = 3;
	        layout.verticalSpacing = 3;
	        layout.horizontalSpacing = 3;
	        defaultServerSectionBody.setLayout(layout);

	        GridData bodyGD = new GridData(GridData.FILL_BOTH);
	        bodyGD.verticalAlignment = GridData.CENTER;
	        defaultServerSectionBody.setLayoutData(bodyGD);
	        
//            GridDataFactory.fillDefaults().grab(true, true).applyTo(defaultServerSectionBody);
//            GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(defaultServerSectionBody);
//            defaultServerSectionBody.setBackground(bkgdColor);

            /*
             * Parent panel for the server instance status icon and display name
             */
            Composite serverDetailsPanel = toolkit.createComposite(defaultServerSectionBody);
            GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(serverDetailsPanel);
            GridLayoutFactory.fillDefaults().numColumns(3).applyTo(serverDetailsPanel);

            /*
             * Default Instance Name
             */
            Label serverPrefixLabel = toolkit.createLabel(serverDetailsPanel, Messages.Name);
            serverPrefixLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            GridDataFactory.fillDefaults().grab(false, false).applyTo(serverPrefixLabel);

            /*
             * Default Instance status icon (initially blank)
             */
            defaultServerStatusLabel = toolkit.createLabel(serverDetailsPanel, StringConstants.EMPTY_STRING, SWT.NONE);
            GridDataFactory.fillDefaults().grab(false, false).applyTo(defaultServerStatusLabel);

            /*
             * Default Instance display name (initially blank)
             */
            defaultServerLink = toolkit.createHyperlink(serverDetailsPanel, StringConstants.EMPTY_STRING, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(defaultServerLink);
            
            createSeparator(serverDetailsPanel, 3,1);
            
            defaultServerLink.addHyperlinkListener(new HyperlinkAdapter() {
                @Override
                public void linkActivated(HyperlinkEvent e) {
                    //open the servers view
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    try {
                        window.getActivePage().showView("org.eclipse.wst.server.ui.ServersView"); //$NON-NLS-1$

                        // Need to fetch the default teiid instance again since the parameter in the
                        // parent method does not remain assigned becoming null
                        if (! ModelerCore.hasDefaultTeiidServer()) {
                            // No default teiid instance so most likely no servers at all so open the new server wizard
                            IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
                            handlerService.executeCommand("org.teiid.designer.dqp.ui.newServerAction", null); //$NON-NLS-1$
                        } else {
                            //  defaultServer is a valid server so open the editServer editor
                            IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

                            // Load the default teiid instance into an event's data in order to
                            // make it available to the command's handler.
                            Event event = ModelerCore.createDefaultTeiidServerEvent();
                            handlerService.executeCommand("org.teiid.designer.dqp.ui.editServerAction", event); //$NON-NLS-1$
                        }

                    } catch (Exception ex) {
                        UiConstants.UTIL.log(ex);
                    }
                }
            });

            /*
             * Teiid instance prefix
             */
            Label versionPrefixLabel = toolkit.createLabel(defaultServerSectionBody, Messages.TeiidVersion);
            versionPrefixLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            GridDataFactory.fillDefaults().grab(false, false).applyTo(versionPrefixLabel);
            
            /*
             * Default Instance status icon (initially blank)
             */
            defaultTeiidStatusLabel = toolkit.createLabel(defaultServerSectionBody, StringConstants.EMPTY_STRING, SWT.NONE);
            GridDataFactory.fillDefaults().minSize(30,  100).grab(false, false).applyTo(defaultTeiidStatusLabel);

            /*
             * Teiid Instance version
             */
            defaultTeiidVersionLabel = toolkit.createLabel(defaultServerSectionBody, StringConstants.EMPTY_STRING);
            Font standardFont = defaultTeiidVersionLabel.getFont();
            FontData boldData = standardFont.getFontData()[0];
            boldData.setStyle(SWT.BOLD);
            defaultTeiidVersionLabel.setFont(GlobalUiFontManager.getFont(boldData));
            
            GridDataFactory.fillDefaults().minSize(30,  100).grab(true, false).applyTo(defaultTeiidVersionLabel);

            /*
             * Create the toolbar - contains button to change the default instance.
             */
            createDefaultServerSectionToolbar();

            defaultServerSection.setClient(defaultServerSectionBody);

            /*
             * Delay the loading of the default instance details until the server manager has been
             * properly restored and has the STARTED status.
             */
            ServerVersionLoadingThread thread = new ServerVersionLoadingThread();
            thread.start();
            
            ComponentLoadingManager manager = ComponentLoadingManager.getInstance();
            manager.manageLoading(this);
    	}
	}
	
    /*
     * Create the Default Server Section toolbar
     */
	private void createDefaultServerSectionToolbar() {
        // configure section toolbar
        toolbar = FormUtil.createSectionToolBar(this.defaultServerSection, toolkit);
        toolbar.setBackground(this.defaultServerSection.getTitleBarBackground());

        ImageHyperlink changeDefaultServerLink = toolkit.createImageHyperlink(toolbar, SWT.NONE);
        changeDefaultServerLink.setImage(changeServerImage);
        changeDefaultServerLink.setToolTipText(Messages.ChangeDefaultServer);
        changeDefaultServerLink.addHyperlinkListener(new HyperlinkAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void linkActivated(HyperlinkEvent e) {
                try {
                    // Set the default server
                    IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
                    handlerService.executeCommand("org.teiid.designer.dqp.ui.setDefaultServerAction", null); //$NON-NLS-1$
                } catch (Exception ex) {
                    UiConstants.UTIL.log(ex);
                }
            }
        });

        startServerLink = toolkit.createImageHyperlink(toolbar, SWT.NONE);
        startServerLink.setImage(startServerImage);
        startServerLink.setToolTipText(Messages.Start);
        startServerLink.addHyperlinkListener(new HyperlinkAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void linkActivated(HyperlinkEvent e) {
                try {
                	((IServer)ModelerCore.getTeiidServerManager().getDefaultServer().getParent()).start("run", (IOperationListener)null);
                } catch (Exception ex) {
                    UiConstants.UTIL.log(ex);
                }
            }
        });
        
        stopServerLink = toolkit.createImageHyperlink(toolbar, SWT.NONE);
        stopServerLink.setImage(stopServerImageDisabled);
        stopServerLink.setToolTipText(Messages.Stop);
        stopServerLink.addHyperlinkListener(new HyperlinkAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void linkActivated(HyperlinkEvent e) {
                try {
                	IServer server = ((IServer)ModelerCore.getTeiidServerManager().getDefaultServer().getParent());
                	
                	if( ! ModelerCore.isDefaultTeiidConnected() ) return;
                	
                	if( server.getServerType() != null && server.canStop().isOK() ) {
                		stop(server, getSite().getShell());
                	} else {
                		MessageDialog.openInformation(getSite().getShell(), "Stop Server Not Available", "some message.............");
                	}
                } catch (Exception ex) {
                    UiConstants.UTIL.log(ex);
                }
            }
        });
	}
	
	public static void stop(IServer server, Shell shell) {
		
		IJobManager jobManager = Job.getJobManager();
		Job[] jobs = jobManager.find(ServerUtil.SERVER_JOB_FAMILY);
		for (Job j: jobs) {
			if (j instanceof Server.StartJob) {
				Server.StartJob startJob = (Server.StartJob) j;
				if (startJob.getServer().equals(server)) {
					startJob.cancel();
					return;
				}
			}
		}
		
		server.stop(false);
	}
	
    /**
     * Required since the server version cannot be loaded on startup due to
     * the TeiidServerManager needing to wait for the ServerCore to initialise
     * first.
     */
    private class ServerVersionLoadingThread extends Thread {

        public ServerVersionLoadingThread() {
            super(DefaultServerSection.this + "." + ServerVersionLoadingThread.class.getSimpleName()); //$NON-NLS-1$
            setDaemon(true);
        }

        @Override
        public void run() {
            ITeiidServerManager serverManager = ModelerCore.getTeiidServerManager();
            while(! serverManager.isStarted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    UiConstants.UTIL.log(ex);
                }
            }

            UiUtil.runInSwtThread(new Runnable() {
                @Override
                public void run() {
                    setDefaultServerText(ModelerCore.getDefaultServerName());
                    setDefaultServerStatusIcon(ModelerCore.isDefaultParentConnected());
                    setDefaultTeiidInstanceStatusIcon(ModelerCore.isDefaultTeiidConnected());
                    setDefaultTeiidVersionText(ModelerCore.getTeiidServerVersion());

                    /* Listen for changes to the default teiid instance */
                    ModelerCore.addTeiidServerVersionListener(teiidServerVersionListener);
                    addExecutionConfigurationListener(ModelerCore.getDefaultServerEventManager());
                }
            }, true);

        }
    }
    
    /**
     * Add the configuration listener to the given {@link ITeiidServer}'s event manager,
     * which is normally the TeiidServerManager
     *
     * @param teiidServer
     */
    private void addExecutionConfigurationListener(EventManager eventManager) {
        if (eventManager == null)
            return;

        eventManager.addListener(execConfigurationListener);
    }
    
    /**
     * Called by a number of different methods to update the default teiid instance name text field.
     *
     * @param serverName name of the server or a noDefaultServer message
     */
    private void setDefaultServerText(String serverName) {
        if(defaultServerSectionBody.isDisposed() || defaultServerLink.isDisposed())
            return;

        Display display = defaultServerSectionBody.getDisplay();
        final String hyperlinkText = serverName;
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                defaultServerLink.setText(hyperlinkText);
            }
        });
    }

    /**
     * Updates the default server's status icon.
     *
     * @param serverConnected
     */
    private void setDefaultServerStatusIcon(boolean serverConnected) {
        if(defaultServerSectionBody.isDisposed() || defaultServerStatusLabel.isDisposed())
            return;
        
        Image newImage = null;
        String tooltip = null;
        if (serverConnected) {
            newImage = org.teiid.designer.ui.UiPlugin.getDefault().getImage(PluginConstants.Images.JBOSS_SERVER_STARTED_ICON);
            tooltip = Messages.DefaultServerConnected;
        } else {
            newImage = org.teiid.designer.ui.UiPlugin.getDefault().getImage(PluginConstants.Images.JBOSS_SERVER_STOPPED_ICON);
            tooltip = Messages.DefaultServerNotConnected;
        }

        defaultServerStatusLabel.setImage(newImage);
        ((GridData)defaultTeiidStatusLabel.getLayoutData()).minimumHeight = 20;
        defaultServerStatusLabel.setToolTipText(tooltip);
        defaultServerStatusLabel.getParent().layout(true);
        defaultServerSectionBody.layout(true);
    }
    
    private void updateServerActionBar(boolean serverConnected) {
    	if( startServerLink.isDisposed()) return;
    	
        stopServerLink.setRedraw(false);
        startServerLink.setRedraw(false);
        if (serverConnected) {
            startServerLink.setImage(startServerImageDisabled);
            startServerLink.setToolTipText(Messages.ServerConnected);
            stopServerLink.setImage(stopServerImage);
            stopServerLink.setToolTipText(Messages.StopServer);
        } else {
            startServerLink.setImage(startServerImage);
            startServerLink.setToolTipText(Messages.StartServer);
            stopServerLink.setImage(stopServerImageDisabled);
            stopServerLink.setToolTipText(Messages.ServerNotStarted);
        }
        stopServerLink.setRedraw(true);
        startServerLink.setRedraw(true);
        startServerLink.layout(true);
        stopServerLink.layout(true);
    }

    private void setDefaultTeiidVersionText(final ITeiidServerVersion teiidServerVersion) {
        if(defaultServerSectionBody==null || defaultServerSectionBody.isDisposed() || defaultTeiidVersionLabel.isDisposed())
            return;
        
        Display display = defaultTeiidVersionLabel.getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
            	defaultTeiidVersionLabel.setText(teiidServerVersion.toString());
            }
        });
    }
    
    /**
     * Updates the default server's status icon.
     *
     * @param serverConnected
     */
    private void setDefaultTeiidInstanceStatusIcon(boolean teiidConnected) {
        if(defaultServerSectionBody.isDisposed() || defaultTeiidStatusLabel.isDisposed())
            return;
        
        Image newImage = null;
        String tooltip = null;
        if (teiidConnected) {
            newImage = org.teiid.designer.ui.UiPlugin.getDefault().getImage(PluginConstants.Images.TEIID_SERVER_DEFAULT_ICON);
            tooltip = Messages.DefaultServerConnected;
        } else {
            newImage = org.teiid.designer.ui.UiPlugin.getDefault().getImage(PluginConstants.Images.TEIID_SERVER_DISCONNECTED_ICON);
            tooltip = Messages.DefaultServerNotConnected;
        }

        defaultTeiidStatusLabel.setImage(newImage);
        ((GridData)defaultTeiidStatusLabel.getLayoutData()).minimumHeight = 20;
        defaultTeiidStatusLabel.setToolTipText(tooltip);
        defaultTeiidStatusLabel.getParent().layout(true);
        defaultServerSectionBody.layout(true);
    }
    
    private IWorkbenchPartSite getSite() {
    	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    	return window.getActivePage().getActivePart().getSite();
    }

    
	private void createSeparator(Composite parent, int nColumns, int height) {
		Composite bottomSep = toolkit.createCompositeSeparator(parent);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = nColumns;
		layoutData.heightHint = height;
		bottomSep.setLayoutData(layoutData);
	}

	@Override
	public void manageLoad(Properties args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateServerActionBar(ModelerCore.isDefaultTeiidConnected());
            }
        };

        UiUtil.runInSwtThread(runnable, false);
	}

}
