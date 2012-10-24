/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.views;

import java.util.List;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;
import org.teiid.adminapi.Model;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterUtil;
import org.teiid.designer.runtime.connection.SourceConnectionBinding;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.server.NewServerAction;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.runtime.ui.views.content.AbstractTeiidFolder;
import org.teiid.designer.runtime.ui.views.content.TeiidEmptyNode;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.KeyInValueHashMap;
import org.teiid.designer.ui.common.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Label;


/**
 * The ConnectorsView provides a tree view of workspace connector bindings which are stored in a configuration.xml file and
 * corresponding model-to-connector mappings in a WorkspaceBindings.def file.
 *
 * @since 8.0
 */
public class TeiidView extends CommonNavigator implements DqpUiConstants {

    /**
     * Text constant for the server hyperlink label when there are servers available
     */
    private static final String NEW_SERVER_LABEL = UTIL.getString("TeiidServerOverviewSection.newHyperlinkLabel"); //$NON-NLS-1$

    /**
     * Text constant for the server hyperlink label when there are servers available
     */
    private static final String EDIT_SERVER_LABEL = UTIL.getString("TeiidServerOverviewSection.editHyperlinkLabel"); //$NON-NLS-1$

    /**
     * A <code>ViewerFilter</code> that hides the translators.
     */
    static final ViewerFilter TRANSLATORS_FILTER = new ViewerFilter() {
        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean select( Viewer viewer, Object parentElement, Object element ) {
            TeiidTranslator teiidTranslator = RuntimeAssistant.adapt(element, TeiidTranslator.class);
            if (teiidTranslator != null)
                return false;

            return true;
        }
    };
    
    /**
     * A <code>ViewerFilter</code> that hides Preview Data Sources.
     */
    static final ViewerFilter PREVIEW_DATA_SOURCE_FILTER = new ViewerFilter() {
        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean select( Viewer viewer, Object parentElement, Object element ) {
            TeiidDataSource dataSource = RuntimeAssistant.adapt(element, TeiidDataSource.class);
            if (dataSource != null && dataSource.isPreview())
                return false;

            return true;
        }
    };
    
    /**
     * A <code>ViewerFilter</code> that hides Preview VDBs.
     */
    static final ViewerFilter PREVIEW_VDB_FILTER = new ViewerFilter() {
        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean select( Viewer viewer, Object parentElement, Object element ) {
            TeiidVdb vdb = RuntimeAssistant.adapt(element, TeiidVdb.class);
            if (vdb != null && vdb.isPreviewVdb())
                return false;

            return true;
        }
    };

    /**
     * Prefix for language NLS properties
     */
    static final String PREFIX = I18nUtil.getPropertyPrefix(TeiidView.class);

    static final String ACTIVE_VDB = getString("activeVdb"); //$NON-NLS-1$
    static final String INACTIVE_VDB = getString("inactiveVdb"); //$NON-NLS-1$
    
    /**
     * Used for restoring view state
     */
    private static IMemento viewMemento;

    static String getString( final String stringId ) {
        return UTIL.getString(PREFIX + stringId);
    }

    static String getString( final String stringId,
                             final Object param ) {
        return UTIL.getString(PREFIX + stringId, param);
    }

    private Combo jbossServerCombo;

    private CommonViewer viewer;

    /** needed for key listening */
    private KeyAdapter kaKeyAdapter;

    private IPropertySourceProvider propertySourceProvider;

    private KeyFromValueAdapter adapter = new KeyFromValueAdapter<String, IServer>() {

        @Override
        public String getKey(IServer value) {
            return value.getName();
        }
    };
    
    private KeyInValueHashMap<String, IServer> serverMap = new KeyInValueHashMap<String, IServer>(adapter);

    private IServerLifecycleListener serversListener = new ServerLifecycleAdapter() {
        
        private void refresh() {
            jbossServerCombo.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    populateJBossServerCombo();
                }
            });
        }
        
        @Override
        public void serverAdded(IServer server) {
            refresh();
        }
        
        @Override
        public void serverRemoved(IServer server) {
            refresh();
        }
    };
    


    private Hyperlink newServerOrOpenServerViewHyperlink;    /**
     * The constructor.
     */
    public TeiidView() {
        this.setPartName(getString("title.text")); //$NON-NLS-1$
        this.setTitleImage(DqpUiPlugin.getDefault().getImage(Images.SOURCE_BINDING_ICON));
        this.setTitleToolTip(getString("title.tooltip")); //$NON-NLS-1$
    }
    
    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl( Composite parent ) {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        
        Composite frame = toolkit.createComposite(parent, SWT.BORDER);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(frame);
        
        Composite comboDescFrame = toolkit.createComposite(frame, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(comboDescFrame);
        GridLayoutFactory.fillDefaults().margins(5, 20).spacing(SWT.DEFAULT, 25).applyTo(comboDescFrame);
        
        Composite comboFrame = toolkit.createComposite(comboDescFrame, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(comboFrame);
        GridLayoutFactory.fillDefaults().applyTo(comboFrame);
        
        Composite labelFrame = toolkit.createComposite(comboFrame, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(labelFrame);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(labelFrame);
        
        Label jbLabel = WidgetFactory.createLabel(labelFrame, UTIL.getString("TeiidServerOverviewSection.jbLabel")); //$NON-NLS-1$
        jbLabel.setForeground(labelFrame.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.swtDefaults().grab(false, false).applyTo(jbLabel);
        
        newServerOrOpenServerViewHyperlink = toolkit.createHyperlink(labelFrame, EDIT_SERVER_LABEL, SWT.NONE);
        GridDataFactory.swtDefaults().applyTo(newServerOrOpenServerViewHyperlink);
        newServerOrOpenServerViewHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {
                if (serverMap.isEmpty()) {
                    // There are no servers so open the server wizard
                    NewServerAction action = new NewServerAction(getViewSite().getShell(), getServerManager());
                    action.run();
                } else {
                    //open the servers view
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    try {
                        window.getActivePage().showView("org.eclipse.wst.server.ui.ServersView"); //$NON-NLS-1$
                    } catch (PartInitException ex) {
                        UTIL.log(ex);
                    }
                }
            }
        });
        
        jbossServerCombo = new Combo(comboFrame, SWT.READ_ONLY | SWT.DROP_DOWN);
        toolkit.adapt(jbossServerCombo);
        GridDataFactory.swtDefaults().minSize(250, 30).grab(true, false).applyTo(jbossServerCombo);
        jbossServerCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleServerComboSelection();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                handleServerComboSelection();
            }
        });
        
        Text descriptionText = toolkit.createText(comboDescFrame,
                                                  UTIL.getString("TeiidServerOverviewSection.description"), //$NON-NLS-1$
                                                  SWT.MULTI | SWT.WRAP);
        descriptionText.setForeground(comboDescFrame.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(false, true).hint(100, SWT.DEFAULT).applyTo(descriptionText);
        
        super.createPartControl(frame);
        
        viewer = getCommonViewer();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());

        hookToolTips();

        viewer.setSorter(new NameSorter());

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleSelectionChanged(event);
            }
        });

        initKeyListener();
        
        DqpPlugin.getInstance().getServersProvider().addServerLifecycleListener(serversListener);
        
        // Populate the jboss server combo box which
        // should also populate the viewer as well
        populateJBossServerCombo();
        
        viewer.expandToLevel(3);
    }

    private void populateJBossServerCombo() {
        serverMap.clear();

        IServer[] servers = DqpPlugin.getInstance().getServersProvider().getServers();
        for (IServer server : servers) {
            if (TeiidServerAdapterUtil.isJBossServer(server)) {
                serverMap.add(server);
            }
        }
        
        String[] items = serverMap.keySet().toArray(new String[0]);
        jbossServerCombo.setItems(items);
        
        if (items.length == 0) {
            newServerOrOpenServerViewHyperlink.setText(NEW_SERVER_LABEL);
        } else {
            newServerOrOpenServerViewHyperlink.setText(EDIT_SERVER_LABEL);
            jbossServerCombo.setText(items[0]);
        }      
        
        // even if nothing in combo, still want the viewer to refresh
        handleServerComboSelection();
    }

    /**
     * Take the server combo's selection and apply it
     * to the viewer
     */
    private void handleServerComboSelection() {
        TeiidEmptyNode emptyNode = new TeiidEmptyNode();
        // populate viewer
        String serverName = jbossServerCombo.getText();
        IServer server = serverMap.get(serverName);
        if (server == null) {
            viewer.setInput(emptyNode);
        } else {
            viewer.setInput(server);
        }
        
        // Ensures that the action provider is properly initialised in this view
        IStructuredSelection selection = new StructuredSelection(emptyNode);
        getNavigatorActionService().setContext(new ActionContext(selection));
        getNavigatorActionService().fillActionBars(getViewSite().getActionBars());
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IPropertySheetPage.class)) {
            propertySourceProvider = new RuntimePropertySourceProvider();
            ((RuntimePropertySourceProvider)propertySourceProvider).setEditable(true, false);
            PropertySheetPage page = new PropertySheetPage();
            page.setPropertySourceProvider(propertySourceProvider);
            return page;
        }
        return super.getAdapter(adapter);
    }

    String getConnectorToolTip( TeiidTranslator connector ) {
        return connector.getName();
    }

    SourceConnectionBinding getSelectedBinding() {
        StructuredSelection selection = (StructuredSelection)viewer.getSelection();
        if (!selection.isEmpty() && selection.getFirstElement() instanceof SourceConnectionBinding) {
            return (SourceConnectionBinding)selection.getFirstElement();
        }

        return null;
    }

    List<Object> getSelectedObjects() {
        StructuredSelection selection = (StructuredSelection)viewer.getSelection();
        if (!selection.isEmpty()) {
            return SelectionUtilities.getSelectedObjects(selection);
        }

        return null;
    }

    /**
     * @return the server manager being used by this view
     */
    TeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }

    String getVDBToolTip( TeiidVdb vdb ) {
        StringBuilder builder = new StringBuilder();
        builder.append("VDB:\t\t").append(vdb.getName()).append("\nState:\t"); //$NON-NLS-1$ //$NON-NLS-2$
        if (vdb.isActive()) {
            builder.append(ACTIVE_VDB);
        } else {
            builder.append(INACTIVE_VDB);
            for (String error : vdb.getVdb().getValidityErrors()) {
                builder.append("\nERROR:\t").append(error); //$NON-NLS-1$
            }
        }

        builder.append("\nModels:"); //$NON-NLS-1$
        for (Model model : vdb.getVdb().getModels()) {
            builder.append("\n\t   ").append(model.getName()); //$NON-NLS-1$
        }
        return builder.toString();
    }

    /**
     * On certain keys execute certain actions
     */
    void handleKeyEvent( KeyEvent event ) {
        if (event.stateMask != 0) return;

        if (event.character == SWT.DEL) {
            // if (deleteConnectorBindingAction != null && deleteConnectorBindingAction.isEnabled()) {
            // deleteConnectorBindingAction.run();
            // }
        }
    }

    void handleSelectionChanged( SelectionChangedEvent event ) {
        updateStatusLine((IStructuredSelection)event.getSelection());
    }

    /**
     * Tooltips over connectors and types requires a mouse label listener
     */
    private void hookToolTips() {
        final Listener labelListener = new Listener() {
            @Override
            public void handleEvent( Event event ) {
                Label label = (Label)event.widget;
                Shell shell = label.getShell();
                switch (event.type) {
                    case SWT.MouseDown:
                        viewer.setSelection(new StructuredSelection(label.getData("_TOOLTIP"))); //$NON-NLS-1$
                        shell.dispose();
                        break;
                    case SWT.MouseExit:
                        shell.dispose();
                        break;
                }
            }
        };

        Listener treeListener = new Listener() {

            Shell tip = null;
            Label label = null;

            private void disposeTip() {
                if (tip != null) {
                    tip.dispose();
                    tip = null;
                    label = null;
                }
            }

            @Override
            public void handleEvent( Event event ) {
                switch (event.type) {
                    case SWT.MouseMove: {
                        if (tip == null) {
                            break;
                        }
                        TreeItem item = viewer.getTree().getItem(new Point(event.x, event.y));
                        if (item != null && !item.isDisposed()) {
                            Object data = item.getData();
                            if (!label.isDisposed() && data == label.getData("_TOOLTIP")) { //$NON-NLS-1$
                                break;
                            }
                        }
                        disposeTip();
                        break;
                    }
                    case SWT.FocusOut:
                    case SWT.Dispose:
                    case SWT.KeyDown: {
                        disposeTip();
                        break;
                    }
                    case SWT.MouseHover: {
                        TreeItem item = viewer.getTree().getItem(new Point(event.x, event.y));
                        if (item != null) {
                            if (tip != null && !tip.isDisposed()) {
                                tip.dispose();
                            }
                            Object data = item.getData();
                            if (data != null) {
                                String tooltip = CoreStringUtil.Constants.EMPTY_STRING;
                                if (data instanceof TeiidTranslator) {
                                    tooltip = getConnectorToolTip((TeiidTranslator)data);
                                } else if (data instanceof TeiidVdb) {
                                    tooltip = getVDBToolTip((TeiidVdb)data);
                                } else if (data instanceof AbstractTeiidFolder) {
                                    tooltip = ((AbstractTeiidFolder)data).getName();
                                } else if( data instanceof TeiidServer ) {
                                	TeiidServer teiidServer = (TeiidServer)data;
                                	String ttip = teiidServer.toString();
                                	if( teiidServer.getConnectionError() != null ) {
                                		ttip = ttip + "\n\n" + teiidServer.getConnectionError(); //$NON-NLS-1$
                                	}
                                	tooltip = ttip;
                                } else {
                                    tooltip = data.toString();
                                }
                                if (tooltip != null) {
                                    tip = new Shell(viewer.getTree().getShell(), SWT.ON_TOP | SWT.TOOL);
                                    FillLayout fillLayout = new FillLayout();
                                    fillLayout.marginHeight = 1;
                                    fillLayout.marginWidth = 1;
                                    tip.setLayout(fillLayout);
                                    label = new Label(tip, SWT.NONE);
                                    label.setForeground(tip.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                                    label.setBackground(tip.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                                    label.setData("_TOOLTIP", data); //$NON-NLS-1$
                                    label.setText(tooltip);
                                    label.addListener(SWT.MouseExit, labelListener);
                                    label.addListener(SWT.MouseDown, labelListener);
                                    Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                                    Point pt = viewer.getTree().toDisplay(event.x, event.y);
                                    tip.setBounds(pt.x, pt.y + 26, size.x, size.y);
                                    tip.setVisible(true);
                                }
                            }
                        }
                    }
                }
            }
        };
        viewer.getTree().setToolTipText(""); //$NON-NLS-1$
        viewer.getTree().addListener(SWT.FocusOut, treeListener);
        viewer.getTree().addListener(SWT.Dispose, treeListener);
        viewer.getTree().addListener(SWT.KeyDown, treeListener);
        viewer.getTree().addListener(SWT.MouseMove, treeListener);
        viewer.getTree().addListener(SWT.MouseHover, treeListener);
    }

    private void initKeyListener() {

        // create the adapter
        if (kaKeyAdapter == null) {

            kaKeyAdapter = new KeyAdapter() {

                @Override
                public void keyReleased( KeyEvent event ) {
                    handleKeyEvent(event);
                }
            };
        }

        // add the adapter as a listener
        if (viewer != null) {
            viewer.getControl().removeKeyListener(kaKeyAdapter);
            viewer.getControl().addKeyListener(kaKeyAdapter);

        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
     */
    @Override
    public void init( IViewSite site,
                      IMemento memento ) throws PartInitException {
        // first time a view is opened in a session a memento is passed in. if view is closed and reopened the memento passed in
        // is null. so will save initial non-null memento to use when a view is reopened in same session. however, it will start
        // with the same settings that the session started with.
        if ((viewMemento == null) && (memento != null)) {
            viewMemento = memento;
        }

        super.init(site, memento);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    /**
     * @param object the object needing to be updated in the viewer
     */
    public void updateLabel(Object object) {
        this.viewer.update(object, null);
    }

    /**
     * Updates Eclipse's Status line based on current selection in Teiid View
     * 
     * @param selection the current viewer selection (never <code>null</code>)
     */
    private void updateStatusLine( IStructuredSelection selection ) {
        // If no selection or mutli-selection
        String msg = StringUtilities.EMPTY_STRING;

        if (selection.size() == 1) {
            Object selectedObject = selection.getFirstElement();

            if (selectedObject instanceof TeiidServer) {
                msg = getString("statusBar.server.label", ((TeiidServer)selectedObject).toString()); //$NON-NLS-1$
            } else if (selectedObject instanceof TeiidTranslator) {
                msg = getString("statusBar.translator.label", ((TeiidTranslator)selectedObject).getName()); //$NON-NLS-1$
            } else if (selectedObject instanceof TeiidVdb) {
                msg = getString("statusBar.vdb.label", ((TeiidVdb)selectedObject).getName()); //$NON-NLS-1$
            } else if (selectedObject instanceof TeiidDataSource) {
                msg = getString("statusBar.datasource.label", ((TeiidDataSource)selectedObject).getDisplayName()); //$NON-NLS-1$
            }
        }
        getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
    }

    class NameSorter extends ViewerSorter {
    }

}
