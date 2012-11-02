/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
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
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.ITeiidVdb;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterUtil;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.server.NewServerAction;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.runtime.ui.views.content.TeiidEmptyNode;
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
            ITeiidVdb vdb = RuntimeAssistant.adapt(element, ITeiidVdb.class);
            if (vdb != null && vdb.isPreviewVdb())
                return false;

            return true;
        }
    };

    /**
     * Prefix for language NLS properties
     */
    static final String PREFIX = I18nUtil.getPropertyPrefix(TeiidView.class);

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

        viewer.setSorter(new NameSorter());

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

    /**
     * @return the server manager being used by this view
     */
    TeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
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

    class NameSorter extends ViewerSorter {
    }

}
