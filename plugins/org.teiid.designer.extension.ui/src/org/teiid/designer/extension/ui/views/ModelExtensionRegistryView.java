/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.views;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * 
 */
public class ModelExtensionRegistryView extends ViewPart {

    private IAction cloneMedAction;

    private IAction findMedReferencesAction;

    private IAction openMedEditorAction;

    private IAction registerMedAction;

    private final ModelExtensionRegistry registry;

    private IAction unregisterMedAction;

    private TableViewer viewer;

    public ModelExtensionRegistryView() {
        this.registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);
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
    }

    private void createColumns( final Table table ) {
        // must create in the order in ColumnIndexes
        // TODO add builtin column
        TableViewerColumn column = new TableViewerColumn(this.viewer, SWT.LEFT);
        column.getColumn().setText(ColumnHeaders.NAMESPACE_PREFIX);
        column.setLabelProvider(new MedLabelProvider(ColumnIndexes.NAMESPACE_PREFIX));

        column = new TableViewerColumn(this.viewer, SWT.LEFT);
        column.getColumn().setText(ColumnHeaders.NAMESPACE_URI);
        column.setLabelProvider(new MedLabelProvider(ColumnIndexes.NAMESPACE_URI));

        column = new TableViewerColumn(this.viewer, SWT.LEFT);
        column.getColumn().setText(ColumnHeaders.METAMODEL_URI);
        column.setLabelProvider(new MedLabelProvider(ColumnIndexes.METAMODEL_URI));

        column = new TableViewerColumn(this.viewer, SWT.RIGHT);
        column.getColumn().setText(ColumnHeaders.VERSION);
        column.setLabelProvider(new MedLabelProvider(ColumnIndexes.VERSION));

        final TableViewerColumn lastColumn = new TableViewerColumn(this.viewer, SWT.LEFT);
        lastColumn.getColumn().setText(ColumnHeaders.DESCRIPTION);
        lastColumn.setLabelProvider(new MedLabelProvider(ColumnIndexes.DESCRIPTION));

        table.addControlListener(new ControlAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
             */
            @Override
            public void controlResized( ControlEvent e ) {
                lastColumn.getColumn().setWidth(table.getSize().x);
            }
        });
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

        createActions();

        // populate the view
        this.viewer.setInput(this);
        WidgetUtil.pack(this.viewer);

        MenuManager mgr = initContextMenu();
        Menu menu = mgr.createContextMenu(this.viewer.getControl());
        this.viewer.getControl().setMenu(menu);
        //
        // getSite().registerContextMenu(mgr, this.selectionProvider);
        // getSite().setSelectionProvider(this.selectionProvider);

        IActionBars actionBars = getViewSite().getActionBars();
        initMenu(actionBars.getMenuManager());
        initToolBar(actionBars.getToolBarManager());

        registerGlobalActions(getViewSite().getActionBars());

        this.viewer.addOpenListener(new IOpenListener() {
            @Override
            public void open( OpenEvent event ) {
                handleOpenMed();
            }
        });
    }

    Collection<ModelExtensionDefinition> getModelExtensionDefinitions() {
        if (this.registry != null) {
            return this.registry.getAllDefinitions();
        }

        return Collections.emptyList();
    }

    TableViewer getViewer() {
        return this.viewer;
    }

    void handleCloneMed() {
        // TODO implement handleSaveAsMed
        MessageDialog.openInformation(null, null, "Clone MED not implemented");
    }

    void handleFindMedReferences() {
        // TODO implement handleFindMedReferences
        MessageDialog.openInformation(null, null, "Find MED references not implemented");
    }

    void handleMedSelected() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        boolean enable = (selection.size() == 1);

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
            this.unregisterMedAction.setEnabled(enable);
        }
    }

    void handleOpenMed() {
        // TODO implement handleOpenMed
        MessageDialog.openInformation(null, null, "Open MED not implemented");
    }

    void handleRegisterMed() {
        // TODO implement handleCreateMed
        MessageDialog.openInformation(null, null, "Register MED not implemented");
    }

    void handleUnregisterMed() {
        // TODO implement handleDeleteMed
        MessageDialog.openInformation(null, null, "Unregister MED not implemented");
    }

    private MenuManager initContextMenu() {
        MenuManager mgr = new MenuManager();
        mgr.add(this.registerMedAction);
        mgr.add(this.unregisterMedAction);
        mgr.add(this.openMedEditorAction);
        mgr.add(this.cloneMedAction);

        return mgr;
    }

    private void initMenu( IMenuManager menuMgr ) {
        menuMgr.add(this.findMedReferencesAction);
    }

    private void initToolBar( IToolBarManager toolBarMgr ) {
        toolBarMgr.add(this.registerMedAction);
        toolBarMgr.add(this.unregisterMedAction);
        toolBarMgr.add(this.openMedEditorAction);
        toolBarMgr.add(this.cloneMedAction);
        toolBarMgr.update(true);
    }

    private void registerGlobalActions( IActionBars actionBars ) {
        // TODO implement registerGlobalActions
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
        String DESCRIPTION = Messages.descriptionColumnText;
        String METAMODEL_URI = Messages.extendedMetamodelUriColumnText;
        String NAMESPACE_PREFIX = Messages.namespacePrefixColumnText;
        String NAMESPACE_URI = Messages.namespaceUriColumnText;
        String VERSION = Messages.versionColumnText;
    }

    interface ColumnIndexes {
        int DESCRIPTION = 4;
        int METAMODEL_URI = 2;
        int NAMESPACE_PREFIX = 0;
        int NAMESPACE_URI = 1;
        int VERSION = 3;
    }

    class MedLabelProvider extends ColumnLabelProvider {

        private final int columnIndex;

        public MedLabelProvider( int columnIndex ) {
            this.columnIndex = columnIndex;
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
