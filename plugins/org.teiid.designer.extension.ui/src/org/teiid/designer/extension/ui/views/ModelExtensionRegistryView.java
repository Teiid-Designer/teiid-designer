/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.views;

import static org.teiid.designer.extension.ui.UiConstants.UTIL;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.CHECK_MARK;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.registry.RegistryEvent;
import org.teiid.designer.extension.registry.RegistryListener;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * 
 */
public final class ModelExtensionRegistryView extends ViewPart {

    private IAction cloneMedAction;

    private IAction findMedReferencesAction;

    private IAction openMedEditorAction;

    private IAction registerMedAction;

    private final ModelExtensionRegistry registry;

    private IAction unregisterMedAction;

    private TableViewer viewer;

    public ModelExtensionRegistryView() {
        this.registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);
        this.registry.addListener(new RegistryListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.teiid.designer.extension.registry.RegistryListener#process(org.teiid.designer.extension.registry.RegistryEvent)
             */
            @Override
            public void process( RegistryEvent event ) {
                handleRegistryChanged(event);
            }
        });
    }

    private void configureColumn( TableViewerColumn viewerColumn,
                                  int columnIndex,
                                  String headerText,
                                  boolean resizable ) {
        viewerColumn.setLabelProvider(new MedLabelProvider(columnIndex));

        TableColumn column = viewerColumn.getColumn();
        column.setText(headerText);
        column.setMoveable(false);
        column.setResizable(resizable);
    }

    private void configureMenu( IMenuManager menuMgr ) {
        menuMgr.add(this.findMedReferencesAction);
    }

    private void configureToolBar( IToolBarManager toolBarMgr ) {
        toolBarMgr.add(this.registerMedAction);
        toolBarMgr.add(this.unregisterMedAction);
        toolBarMgr.add(this.openMedEditorAction);
        toolBarMgr.add(this.cloneMedAction);
        toolBarMgr.update(true);
    }

    private void createActions() {
        this.cloneMedAction = new Action(Messages.cloneMedActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleCloneMed();
            }
        };
        this.cloneMedAction.setToolTipText(Messages.cloneMedActionToolTip);
        this.cloneMedAction.setEnabled(false);
        this.cloneMedAction.setImageDescriptor(PlatformUI.getWorkbench()
                                                         .getSharedImages()
                                                         .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

        this.findMedReferencesAction = new Action(Messages.findMedReferencesActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleFindMedReferences();
            }
        };
        this.findMedReferencesAction.setToolTipText(Messages.findMedReferencesActionToolTip);
        this.findMedReferencesAction.setEnabled(false);

        try {
            URL imageUrl = new URL("platform:/plugin/org.eclipse.search/icons/full/etool16/search.gif"); //$NON-NLS-1$
            this.findMedReferencesAction.setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
        } catch (Exception e) {
            UTIL.log(e);
        }

        this.openMedEditorAction = new Action(Messages.openMedActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleOpenMed();
            }
        };
        this.openMedEditorAction.setToolTipText(Messages.openMedActionToolTip);
        this.openMedEditorAction.setEnabled(false);
        this.openMedEditorAction.setImageDescriptor(PlatformUI.getWorkbench()
                                                              .getSharedImages()
                                                              .getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

        this.registerMedAction = new Action(Messages.registerMedActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleRegisterMed();
            }
        };
        this.registerMedAction.setToolTipText(Messages.registerMedActionToolTip);
        this.registerMedAction.setImageDescriptor(PlatformUI.getWorkbench()
                                                            .getSharedImages()
                                                            .getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

        this.unregisterMedAction = new Action(Messages.unregisterMedActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleUnregisterMed();
            }
        };
        this.unregisterMedAction.setToolTipText(Messages.unregisterMedActionToolTip);
        this.unregisterMedAction.setEnabled(false);
        this.unregisterMedAction.setImageDescriptor(PlatformUI.getWorkbench()
                                                              .getSharedImages()
                                                              .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
    }

    private void createColumns( final Table table ) {
        // NOTE: create in the order in ColumnIndexes
        TableViewerColumn column = new TableViewerColumn(this.viewer, SWT.CENTER);
        configureColumn(column, ColumnIndexes.BUILT_IN, ColumnHeaders.BUILT_IN, false);

        column = new TableViewerColumn(this.viewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.NAMESPACE_PREFIX, ColumnHeaders.NAMESPACE_PREFIX, true);

        column = new TableViewerColumn(this.viewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.NAMESPACE_URI, ColumnHeaders.NAMESPACE_URI, true);

        column = new TableViewerColumn(this.viewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.METAMODEL_URI, ColumnHeaders.METAMODEL_URI, true);

        column = new TableViewerColumn(this.viewer, SWT.RIGHT);
        configureColumn(column, ColumnIndexes.VERSION, ColumnHeaders.VERSION, true);

        final TableViewerColumn lastColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
        configureColumn(lastColumn, ColumnIndexes.DESCRIPTION, ColumnHeaders.DESCRIPTION, true);
    }

    private MenuManager createContextMenu() {
        MenuManager mgr = new MenuManager();
        mgr.add(this.registerMedAction);
        mgr.add(this.unregisterMedAction);
        mgr.add(this.openMedEditorAction);
        mgr.add(this.cloneMedAction);

        return mgr;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        Composite pnlMain = new Composite(parent, SWT.BORDER);
        pnlMain.setLayout(new GridLayout());
        pnlMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.viewer = new TableViewer(pnlMain, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
        ColumnViewerToolTipSupport.enableFor(this.viewer);

        // configure table
        Table table = this.viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create columns
        createColumns(table);

        this.viewer.setComparator(new ViewerComparator() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public int compare( Viewer viewer,
                                Object med1,
                                Object med2 ) {
                assert med1 instanceof ModelExtensionDefinition;
                assert med1 instanceof ModelExtensionDefinition;

                return super.compare(viewer, ((ModelExtensionDefinition)med1).getNamespacePrefix(),
                                     ((ModelExtensionDefinition)med2).getNamespacePrefix());
            }
        });

        this.viewer.setContentProvider(new IStructuredContentProvider() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( Object inputElement ) {
                Collection<ModelExtensionDefinition> definitions = getModelExtensionDefinitions();
                return definitions.toArray(new ModelExtensionDefinition[definitions.size()]);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public void inputChanged( Viewer viewer,
                                      Object oldInput,
                                      Object newInput ) {
                // nothing to do
            }
        });

        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleMedSelected();
            }
        });

        // double-click will open an editor
        this.viewer.addOpenListener(new IOpenListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IOpenListener#open(org.eclipse.jface.viewers.OpenEvent)
             */
            @Override
            public void open( OpenEvent event ) {
                handleOpenMed();
            }
        });

        // populate the view
        this.viewer.setInput(this);
        WidgetUtil.pack(this.viewer);

        createActions();

        MenuManager mgr = createContextMenu();
        Menu menu = mgr.createContextMenu(this.viewer.getControl());
        this.viewer.getControl().setMenu(menu);
        // TODO need MXD path stored somewhere so selection can find in explorer tree
        // getSite().registerContextMenu(mgr, this.selectionProvider);
        // getSite().setSelectionProvider(this.selectionProvider);

        IActionBars actionBars = getViewSite().getActionBars();
        configureMenu(actionBars.getMenuManager());
        configureToolBar(actionBars.getToolBarManager());

        registerGlobalActions(getViewSite().getActionBars());
    }

    Collection<ModelExtensionDefinition> getModelExtensionDefinitions() {
        if (this.registry != null) {
            return this.registry.getAllDefinitions();
        }

        return Collections.emptyList();
    }

    private ModelExtensionDefinition getSelectedMed() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        // selection must be one MED
        assert (selection.size() == 1) : "selection size is not zero or one"; //$NON-NLS-1$
        assert (selection.getFirstElement() instanceof ModelExtensionDefinition) : "selected object is not a MED"; //$NON-NLS-1$

        return (ModelExtensionDefinition)selection.getFirstElement();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return NLS.bind(Messages.registryViewToolTip, this.registry.getAllDefinitions().size());
    }

    void handleCloneMed() {
        // TODO implement handleCloneMed
        ModelExtensionDefinition selectedMed = getSelectedMed();
        assert (selectedMed != null) : "Clone MED action should not be enabled if there is no selection"; //$NON-NLS-1$

        MessageDialog.openInformation(null, null, "Clone MED not implemented");
    }

    void handleFindMedReferences() {
        // TODO implement handleFindMedReferences
        ModelExtensionDefinition selectedMed = getSelectedMed();
        assert (selectedMed != null) : "Find MED references action should not be enabled if there is no selection"; //$NON-NLS-1$

        MessageDialog.openInformation(null, null, "Find MED references not implemented");
    }

    void handleMedSelected() {
        ModelExtensionDefinition selectedMed = getSelectedMed();
        boolean enable = (selectedMed != null);

        if (this.cloneMedAction.isEnabled() != enable) {
            this.cloneMedAction.setEnabled(enable);
        }

        if (this.findMedReferencesAction.isEnabled() != enable) {
            this.findMedReferencesAction.setEnabled(enable);
        }

        if (this.openMedEditorAction.isEnabled() != enable) {
            this.openMedEditorAction.setEnabled(enable);
        }

        if (this.unregisterMedAction.isEnabled() != enable) {
            if (enable && !selectedMed.isBuiltIn()) {
                this.unregisterMedAction.setEnabled(true);
            } else {
                this.unregisterMedAction.setEnabled(false);
            }
        }
    }

    void handleOpenMed() {
        ModelExtensionDefinition selectedMed = getSelectedMed();
        assert (selectedMed != null) : "Open MED editor action should not be enabled if there is no selection"; //$NON-NLS-1$
        // TODO implement handleOpenMed
//
//        String path = selectedMed.getResourcePath();
//
//        if (CoreStringUtil.isEmpty(path)) {
//            MessageDialog.openInformation(null, null, "The selected Model Extension Definition's resource (*.mxd) cannot be found.");
//        } else {
//            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
//
//            if (file != null) {
//                try {
//                    IWorkbenchWindow window = Activator.getDefault().getCurrentWorkbenchWindow();
//                    IWorkbenchPage page = window.getActivePage();
//                    IDE.openEditor(page, new FileEditorInput(file), MED_EDITOR);
//                } catch (Exception e) {
//                    UTIL.log(e);
//                }
//            }
//        }
    }

    void handleRegisterMed() {
        // TODO implement handleRegisterMed
        MessageDialog.openInformation(null, null, "Register MED not implemented");
    }

    void handleRegistryChanged( RegistryEvent event ) {
        ModelExtensionDefinition med = event.getDefinition();

        if (event.isAdd()) {
            this.viewer.add(med);
        } else if (event.isChange()) {
            this.viewer.refresh(med);
        } else if (event.isRemove()) {
            this.viewer.remove(med);
        }
    }

    void handleUnregisterMed() {
        // TODO implement handleDeleteMed
        ModelExtensionDefinition selectedMed = getSelectedMed();
        assert (selectedMed != null) : "Unregister MED action should not be enabled if there is no selection"; //$NON-NLS-1$

        MessageDialog.openInformation(null, null, "Unregister MED not implemented");
    }

    private void registerGlobalActions( IActionBars actionBars ) {
        actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), this.cloneMedAction);
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), this.unregisterMedAction);

        // properties and select all should always be disabled
        IAction unsupportedAction = new Action() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.action.Action#setEnabled(boolean)
             */
            @Override
            public void setEnabled( boolean theEnabled ) {
                super.setEnabled(false);
            }
        };
        unsupportedAction.setEnabled(false);

        actionBars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), unsupportedAction);
        actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), unsupportedAction);

        // save these changes
        actionBars.updateActionBars();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        if ((this.viewer != null) && !this.viewer.getControl().isDisposed()) {
            this.viewer.getControl().setFocus();
        }
    }

    interface ColumnHeaders {
        String BUILT_IN = Messages.builtInColumnText;
        String DESCRIPTION = Messages.descriptionColumnText;
        String METAMODEL_URI = Messages.extendedMetamodelUriColumnText;
        String NAMESPACE_PREFIX = Messages.namespacePrefixColumnText;
        String NAMESPACE_URI = Messages.namespaceUriColumnText;
        String VERSION = Messages.versionColumnText;
    }

    interface ColumnIndexes {
        int BUILT_IN = 0;
        int DESCRIPTION = 5;
        int METAMODEL_URI = 3;
        int NAMESPACE_PREFIX = 1;
        int NAMESPACE_URI = 2;
        int VERSION = 4;
    }

    class MedLabelProvider extends ColumnLabelProvider {

        private final int columnIndex;

        public MedLabelProvider( int columnIndex ) {
            this.columnIndex = columnIndex;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            if (this.columnIndex == ColumnIndexes.BUILT_IN) {
                assert element instanceof ModelExtensionDefinition;
                ModelExtensionDefinition med = (ModelExtensionDefinition)element;

                if (med.isBuiltIn()) {
                    return Activator.getDefault().getImage(CHECK_MARK);
                }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            assert element instanceof ModelExtensionDefinition;
            ModelExtensionDefinition med = (ModelExtensionDefinition)element;

            if (this.columnIndex == ColumnIndexes.BUILT_IN) {
                return CoreStringUtil.Constants.EMPTY_STRING;
            }

            if (this.columnIndex == ColumnIndexes.NAMESPACE_PREFIX) {
                return med.getNamespacePrefix();
            }

            if (this.columnIndex == ColumnIndexes.NAMESPACE_URI) {
                return med.getNamespaceUri();
            }

            if (this.columnIndex == ColumnIndexes.METAMODEL_URI) {
                return med.getMetamodelUri();
            }

            if (this.columnIndex == ColumnIndexes.VERSION) {
                return Integer.toString(med.getVersion());
            }

            if (this.columnIndex == ColumnIndexes.DESCRIPTION) {
                return med.getDescription();
            }

            // shouldn't happen
            assert false : "Unknown column index of " + this.columnIndex; //$NON-NLS-1$
            return null;
        }

    }

}
