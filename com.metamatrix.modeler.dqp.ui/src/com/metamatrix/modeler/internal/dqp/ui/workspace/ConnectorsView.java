/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.model.BasicConnectorBinding;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.workspace.SourceModelInfo;
import com.metamatrix.modeler.dqp.internal.workspace.WorkspaceConfigurationManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.wizards.ConnectorImportWizard;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.CloneConnectorBindingAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.DeleteConnectorBindingAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.DeleteConnectorTypeAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.DeleteSourceBindingAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.EditConnectorBindingAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ExportConnectorBindingsAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.NewConnectorBindingAction;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.viewsupport.StatusBarUpdater;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.widget.Label;

/**
 * The ConnectorsView provides a tree view of workpace connector bindings which are stored in a configuration.xml file and
 * corresponding model-to-connector mappings in a WorkspaceBindings.def file.
 */
public class ConnectorsView extends ViewPart implements ISelectionListener {

    static final String PREFIX = I18nUtil.getPropertyPrefix(ConnectorsView.class);
    private static final String OPEN_ACTION_LABEL = getString("openAction.text"); //$NON-NLS-1$
    private static final String IMPORT_CONNECTORS_ACTION_LABEL = getString("importConnectorsAction.text"); //$NON-NLS-1$
    private static final String IMPORT_CONNECTORS_ACTION_TOOLTIP = getString("importConnectorsAction.tooltip"); //$NON-NLS-1$
    private static final String SOURCE_BINDING_STATUS_OK = "statusBarUpdater.statusLabel"; //$NON-NLS-1$
    private static final String SOURCE_BINDING_STATUS_MULTIPLE = "statusBarUpdater.statusLabelMultipleConnectors"; //$NON-NLS-1$
    private static final String SOURCE_BINDING_STATUS_NONE = "statusBarUpdater.statusLabelNotBound"; //$NON-NLS-1$
    private static final String CONNECTOR_BINDING_STATUS_LABEL = "statusBarUpdater.connectorBindingStatusLabel"; //$NON-NLS-1$
    private static final String CONNECTOR_TYPE_STATUS_LABEL = "statusBarUpdater.connectorTypeStatusLabel"; //$NON-NLS-1$

    static final String SHOW_CONNECTORS_LABEL = getString("showConnectors.tooltip"); //$NON-NLS-1$
    static final String HIDE_CONNECTORS_LABEL = getString("hideConnectors.tooltip"); //$NON-NLS-1$

    static String getString( final String stringId ) {
        return DqpUiConstants.UTIL.getString(PREFIX + stringId);
    }

    TreeViewer viewer;
    ConnectorsViewTreeProvider treeProvider;
    private Action importConnectorsAction;
    private ExportConnectorBindingsAction exportConnectorsAction;

    Action showConnectorTypesToggleAction;
    private EditConnectorBindingAction editConnectorBindingAction;
    private NewConnectorBindingAction newConnectorBindingAction;
    private DeleteConnectorBindingAction deleteConnectorBindingAction;
    private CloneConnectorBindingAction cloneConnectorBindingAction;
    private DeleteSourceBindingAction deleteSourceBindingAction;
    private DeleteConnectorTypeAction deleteConnectorsAction;
    private Action openModelAction;
    private IAction collapseAllAction;

    /** needed for key listening */
    private KeyAdapter kaKeyAdapter;

    private IChangeListener configListener;

    private StatusBarUpdater statusBarListener;

    private IPropertySourceProvider propertySourceProvider;

    private WorkspaceConfigurationManager workspaceConfig = DqpPlugin.getWorkspaceConfig();

    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public ConnectorsView() {
        this.setPartName(getString("title.text")); //$NON-NLS-1$
        this.setTitleImage(DqpUiPlugin.getDefault().getImage(DqpUiConstants.Images.SOURCE_BINDING_ICON));
        this.setTitleToolTip(getString("title.tooltip")); //$NON-NLS-1$
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl( Composite parent ) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

        viewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                if (element instanceof SourceModelInfo) {
                    SourceModelInfo smi = (SourceModelInfo)element;

                    // Check to see if model in closed project or not?
                    String modelName = smi.getName();
                    IResource openModel = ModelUtilities.findModelByName(modelName);
                    if (openModel != null) {
                        return true;
                    }
                } else {
                    return true;
                }

                return false;
            }
        });

        initDragAndDrop();

        treeProvider = new ConnectorsViewTreeProvider();
        treeProvider.setShowTypes(true);
        viewer.setContentProvider(treeProvider);
        viewer.setLabelProvider(treeProvider);

        hookToolTips();

        viewer.setSorter(new NameSorter());
        viewer.setInput(workspaceConfig);
        viewer.expandToLevel(2);

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                handleSelectionChanged(event);
            }
        });

        initActions();

        hookContextMenu();

        contributeToActionBars();

        initKeyListener();

        // Wire as listener to configuration manager
        // register to receive configuration changes
        this.configListener = new IChangeListener() {
            public void stateChanged( IChangeNotifier theSource ) {
                handleConfigurationChanged();
            }
        };
        DqpPlugin.getInstance().getConfigurationManager().addChangeListener(this.configListener);
        workspaceConfig.addChangeListener(this.configListener);

        // hook up our status bar manager for EObjects
        IStatusLineManager slManager = getViewSite().getActionBars().getStatusLineManager();
        statusBarListener = new MyStatusBarUpdater(slManager);
        viewer.addSelectionChangedListener(statusBarListener);

        getViewSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);

        // hook up this view's selection provider to this site
        getViewSite().setSelectionProvider(viewer);
    }

    void handleSelectionChanged( SelectionChangedEvent event ) {

    }

    void handleConfigurationChanged() {
        if (viewer.getTree().isDisposed()) {
            return;
        }

        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                if (!viewer.getTree().isDisposed()) {
                    viewer.refresh();
                    viewer.expandToLevel(2);
                    // Get Selected Index
                    if (viewer.getTree().getSelectionCount() == 1) {
                        ISelection currentSelection = viewer.getSelection();
                        viewer.setSelection(new StructuredSelection());
                        viewer.setSelection(currentSelection);
                    }
                }
            }
        }, false);

        // Refresh the Model Explorer too
        ModelerUiViewUtils.refreshModelExplorerResourceNavigatorTree();
    }

    /**
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#initDragAndDrop()
     */
    private void initDragAndDrop() {
        // code copied from superclass. only change is to the drag adapter
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] {ResourceTransfer.getInstance()};

        // drop support
        ConnectorsViewDropAdapter adapter = new ConnectorsViewDropAdapter(this.viewer);
        adapter.setFeedbackEnabled(false);

        viewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers, adapter);
    }

    /**
     * Tooltips over connectors and types requires a mouse label listener
     */
    private void hookToolTips() {
        final Listener labelListener = new Listener() {
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
                                String tooltip = StringUtil.Constants.EMPTY_STRING;
                                if (data instanceof BasicConnectorBinding) {
                                    tooltip = getConnectorBindingToolTip((BasicConnectorBinding)data);
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

    String getConnectorBindingToolTip( BasicConnectorBinding binding ) {
        Object params = new Object[] {binding.getName(), binding.getDeployedName(), binding.getConnectorClass(),
            binding.getComponentTypeID(), binding.getConfigurationID(), binding.getID(), binding.isEssential()};
        return DqpUiConstants.UTIL.getString(PREFIX + "bindingToolTip", params); //$NON-NLS-1$
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow( IMenuManager manager ) {
                ConnectorsView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        bars.getMenuManager().setVisible(false);
        // fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private Object getSelectedObject() {
        StructuredSelection selection = (StructuredSelection)viewer.getSelection();
        if (!selection.isEmpty()) {
            return selection.getFirstElement();
        }

        return null;
    }

    SourceModelInfo getSelectedModel() {
        StructuredSelection selection = (StructuredSelection)viewer.getSelection();
        if (!selection.isEmpty() && selection.getFirstElement() instanceof SourceModelInfo) {
            return (SourceModelInfo)selection.getFirstElement();
        }

        return null;
    }

    void fillContextMenu( IMenuManager manager ) {
        Object selection = getSelectedObject();
        if (selection != null) {
            if (selection instanceof ConnectorBinding) {
                manager.add(newConnectorBindingAction);
                manager.add(new Separator());
                manager.add(editConnectorBindingAction);
                manager.add(cloneConnectorBindingAction);
                manager.add(new Separator());
                manager.add(deleteConnectorBindingAction);
                manager.add(new Separator());
                manager.add(importConnectorsAction);
                manager.add(exportConnectorsAction);
            } else if (selection instanceof ComponentTypeID || selection instanceof ComponentType) {
                manager.add(newConnectorBindingAction);
                manager.add(new Separator());
                manager.add(deleteConnectorsAction);
                manager.add(new Separator());
                manager.add(importConnectorsAction);
                manager.add(exportConnectorsAction);
            } else {
                manager.add(deleteSourceBindingAction);
                manager.add(new Separator());
                manager.add(openModelAction);
            }
        } else {
            newConnectorBindingAction.checkEnablement();
            manager.add(newConnectorBindingAction);
            manager.add(new Separator());
            manager.add(importConnectorsAction);
            manager.add(exportConnectorsAction);
        }

        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar( IToolBarManager manager ) {
        manager.add(newConnectorBindingAction);
        manager.add(new Separator());
        manager.add(importConnectorsAction);
        manager.add(exportConnectorsAction);
        manager.add(new Separator());
        manager.add(showConnectorTypesToggleAction);
        manager.add(new Separator());
        manager.add(collapseAllAction);
    }

    /*
     *  Initialize view actions, set icons and action text.
     */
    private void initActions() {
        editConnectorBindingAction = new EditConnectorBindingAction();
        viewer.addSelectionChangedListener(editConnectorBindingAction);

        newConnectorBindingAction = new NewConnectorBindingAction();
        viewer.addSelectionChangedListener(newConnectorBindingAction);

        deleteConnectorBindingAction = new DeleteConnectorBindingAction();
        viewer.addSelectionChangedListener(deleteConnectorBindingAction);

        cloneConnectorBindingAction = new CloneConnectorBindingAction();
        viewer.addSelectionChangedListener(cloneConnectorBindingAction);

        deleteSourceBindingAction = new DeleteSourceBindingAction();
        viewer.addSelectionChangedListener(deleteSourceBindingAction);

        deleteConnectorsAction = new DeleteConnectorTypeAction();
        viewer.addSelectionChangedListener(deleteConnectorsAction);

        exportConnectorsAction = new ExportConnectorBindingsAction();
        viewer.addSelectionChangedListener(exportConnectorsAction);

        openModelAction = new Action(OPEN_ACTION_LABEL) {
            @Override
            public void run() {
                SourceModelInfo modelInfo = getSelectedModel();
                if (modelInfo != null) {
                    String modelName = modelInfo.getName();

                    IResource theModel = ModelUtilities.findModelByName(modelName);
                    if (theModel != null) {
                        ModelEditor mEditor = ModelEditorManager.getModelEditorForFile((IFile)theModel, false);
                        if (mEditor != null) {
                            // Editor already open, just activite to bring to top.
                            ModelEditorManager.activate(mEditor);
                        } else {
                            // Editor not open, activate and force to open
                            ModelEditorManager.activate((IFile)theModel, true);
                        }
                    } else {
                        final String title = getString("openModelAction.title"); //$NON-NLS-1$
                        final String message = DqpUiConstants.UTIL.getString(PREFIX + "openModelAction.noModelFoundMessage", modelName, modelInfo.getContainerPath()); //$NON-NLS-1$
                        MessageDialog.openInformation(UiUtil.getWorkbenchShellOnlyIfUiThread(), title, message);
                    }
                }
            }
        };
        openModelAction.setEnabled(true);

        showConnectorTypesToggleAction = new Action(" ", SWT.TOGGLE) { //$NON-NLS-1$
            @Override
            public void run() {
                treeProvider.setShowTypes(showConnectorTypesToggleAction.isChecked());
                // Set Tooltip based on toggle state
                if (showConnectorTypesToggleAction.isChecked()) {
                    showConnectorTypesToggleAction.setToolTipText(HIDE_CONNECTORS_LABEL);
                    viewer.refresh();
                    viewer.expandAll();
                } else {
                    showConnectorTypesToggleAction.setToolTipText(SHOW_CONNECTORS_LABEL);
                    viewer.refresh();
                    viewer.expandAll();
                }

            }
        };
        showConnectorTypesToggleAction.setEnabled(true);
        showConnectorTypesToggleAction.setChecked(true);
        showConnectorTypesToggleAction.setToolTipText(HIDE_CONNECTORS_LABEL);
        showConnectorTypesToggleAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SHOW_HIDE_CONNECTORS_ICON));

        importConnectorsAction = new Action() {
            @Override
            public void run() {
                ConnectorImportWizard wizard = new ConnectorImportWizard();
                WizardDialog dialog = new WizardDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), wizard);
                dialog.open();
            }
        };
        importConnectorsAction.setText(IMPORT_CONNECTORS_ACTION_LABEL);
        importConnectorsAction.setToolTipText(IMPORT_CONNECTORS_ACTION_TOOLTIP);
        importConnectorsAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.IMPORT_CONNECTORS_ICON));

        collapseAllAction = new Action() {
            @Override
            public void run() {
                viewer.collapseAll();
            }
        };

        collapseAllAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.COLLAPSE_ALL_ICON));
        collapseAllAction.setToolTipText(getString("collapseAllAction.tooltip")); //$NON-NLS-1$
        collapseAllAction.setEnabled(true);

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
     * On certain keys execute certain actions
     */
    void handleKeyEvent( KeyEvent event ) {
        if (event.stateMask != 0) return;

        if (event.character == SWT.DEL) {
            if (deleteConnectorBindingAction != null && deleteConnectorBindingAction.isEnabled()) {
                deleteConnectorBindingAction.run();
            }
        }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void dispose() {
        if (this.configListener != null) {
            DqpPlugin.getInstance().getConfigurationManager().removeChangeListener(this.configListener);
            this.workspaceConfig.removeChangeListener(this.configListener);
        }

        super.dispose();
    }

    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IPropertySheetPage.class)) {
            propertySourceProvider = new ConnectorBindingPropertySourceProvider();
            ((ConnectorBindingPropertySourceProvider)propertySourceProvider).setEditable(true, false);
            PropertySheetPage page = new PropertySheetPage();
            page.setPropertySourceProvider(propertySourceProvider);
            return page;
        }
        return super.getAdapter(adapter);
    }

    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        // If Selection is a SINGLE MODEL FILE, then we can find the source and select it??

        if (theSelection instanceof StructuredSelection) {
            StructuredSelection sel = (StructuredSelection)theSelection;
            if (sel.size() == 1) {
                Object selObj = sel.getFirstElement();
                if (selObj instanceof IResource && ModelUtilities.isModelFile((IResource)selObj)) {
                    SourceModelInfo modelInfo = workspaceConfig.getSourceModelInfo(((IResource)selObj).getName());
                    if (modelInfo != null) {
                        viewer.setSelection(new StructuredSelection(modelInfo), true);
                    }
                }
            }
        }
    }

    /**
     * Inner class required to provide status label for selected ConnectorBinding and ModelInfo objects in ConnectorsView
     * 
     * @since 5.0
     */
    class MyStatusBarUpdater extends StatusBarUpdater {

        public MyStatusBarUpdater( IStatusLineManager statusLineManager ) {
            super(statusLineManager);
        }

        @Override
        protected String formatMessage( ISelection theSel ) {
            if (theSel instanceof IStructuredSelection && !theSel.isEmpty()) {
                IStructuredSelection selection = (IStructuredSelection)theSel;

                int nElements = selection.size();
                if (nElements == 1) {
                    Object elem = selection.getFirstElement();
                    if (elem instanceof ConnectorBinding) {
                        return DqpUiConstants.UTIL.getString(PREFIX + CONNECTOR_BINDING_STATUS_LABEL,
                                                             ((ConnectorBinding)elem).getName());
                    } else if (elem instanceof SourceModelInfo) {
                        // Check for Connector Bindings
                        SourceModelInfo smi = (SourceModelInfo)elem;
                        List cbNames = smi.getConnectorBindingNames();
                        if (cbNames.isEmpty()) {
                            return DqpUiConstants.UTIL.getString(PREFIX + SOURCE_BINDING_STATUS_NONE, smi.getName());
                        } else if (cbNames.size() == 1) {
                            String firstConnectorName = (String)cbNames.get(0);
                            return DqpUiConstants.UTIL.getString(PREFIX + SOURCE_BINDING_STATUS_OK,
                                                                 smi.getName(),
                                                                 firstConnectorName);
                        } else {
                            return DqpUiConstants.UTIL.getString(PREFIX + SOURCE_BINDING_STATUS_MULTIPLE, smi.getName());
                        }
                    } else if (elem instanceof ComponentTypeID) {
                        return DqpUiConstants.UTIL.getString(PREFIX + CONNECTOR_TYPE_STATUS_LABEL,
                                                             ((ComponentTypeID)elem).getName());
                    }
                }
            }

            return super.formatMessage(theSel);
        }

    }
}
