/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.internal.jdbc.ui.ModelerJdbcUiPlugin;
import com.metamatrix.modeler.internal.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.data.Request;
import com.metamatrix.modeler.jdbc.data.Results;
import com.metamatrix.modeler.jdbc.data.ResultsMetadata.ColumnMetadata;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.ui.ModelerJdbcUiConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.widget.AbstractTableLabelProvider;

/**
 * @since 4.0
 */
public class JdbcImportObjectsPage extends WizardPage
    implements InternalUiConstants.Widgets, ModelerJdbcUiConstants.Images, PluginConstants.Images, StringUtil.Constants,
    UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcImportObjectsPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 1;

    private static final String INITIAL_MESSAGE_ID = "initialMessage"; //$NON-NLS-1$

    private static final String STATUS_LABEL_ID = "statusLabel"; //$NON-NLS-1$

    private static final String SHOW_SELECTED_SCHEMAS_ID = "showSelectedSchemasValue"; //$NON-NLS-1$

    private static final String INVALID_PAGE_MESSAGE = getString("invalidPageMessage"); //$NON-NLS-1$

    private static final String NO_OBJECTS = getString("noObjects"); //$NON-NLS-1$

    private static final String AND = "and"; //$NON-NLS-1$

    private static final String REFRESH_TOOLTIP = getString("refreshTooltip"); //$NON-NLS-1$

    private static final int[] SPLITTER_WEIGHTS = new int[] {30, 70};

    private static final String NA = getString("na"); //$NON-NLS-1$

    private static final String REQUEST_ERROR_MESSAGE_ID = "requestErrorMessage"; //$NON-NLS-1$

    private static final String UNKNOWN_COLUMN_LABEL = "Col_"; //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.0
     */
    private static String getString( final String id,
                                     final String parameter ) {
        return Util.getString(I18N_PREFIX + id, parameter);
    }

    JdbcDatabase db;
    private SashForm splitter;
    private ViewForm objsView;
    TreeViewer treeViewer;
    private CTabFolder tabFolder;
    private SelectionAdapter tabListener;
    private CLabel dbLabel, statusLabel;
    Button showSelectedSchemasButton;
    private JdbcNode selectedNode;
    private Map counts;

    /**
     * @param pageName
     * @since 4.0
     */
    protected JdbcImportObjectsPage() {
        super(JdbcImportObjectsPage.class.getSimpleName(), TITLE, null);
        this.counts = new HashMap(0);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    public void createControl( final Composite parent ) {
        // Create page
        final Composite pg = new Composite(parent, SWT.NONE) {

            @Override
            public Point computeSize( final int widthHint,
                                      final int heightHint,
                                      final boolean changed ) {
                final Point size = super.computeSize(widthHint, heightHint, changed);
                size.x = 800;
                return size;
            }
        };
        pg.setLayout(new GridLayout(COLUMN_COUNT, false));
        setControl(pg);
        // Add widgets to page
        this.splitter = WidgetFactory.createSplitter(pg);
        {
            this.objsView = new ViewForm(this.splitter, SWT.BORDER);
            {
                // Add title label to view form's title bar
                this.dbLabel = WidgetFactory.createLabel(this.objsView, ModelerJdbcUiPlugin.getDefault().getImage(DATABASE));
                this.objsView.setTopLeft(this.dbLabel);
                // Add refresh button to view form's title bar
                final ToolBar bar = new ToolBar(this.objsView, SWT.FLAT);
                final ToolBarManager mgr = new ToolBarManager(bar);
                final Action action = new Action() {

                    @Override
                    public void run() {
                        refresh();
                    }
                };
                // defect 18678 -- now uses modeler.ui.UiPlugin instead of metamatrix.ui.UiPlugin to use an image that exists.
                action.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.REFRESH_ICON));
                action.setToolTipText(REFRESH_TOOLTIP);
                mgr.add(action);
                mgr.update(true);
                this.objsView.setTopRight(bar);
                // Add contents to view form
                this.treeViewer = new TreeViewer(this.objsView, SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
                final Tree tree = this.treeViewer.getTree();
                this.objsView.setContent(tree);
                this.treeViewer.setContentProvider(createTreeContentProvider());

                // Check events can occur separate from selection events.
                // In this case move the selected node.
                // Also trigger selection of node in model.
                this.treeViewer.getTree().addSelectionListener(new SelectionListener() {

                    public void widgetSelected( SelectionEvent e ) {
                        if (e.detail == SWT.CHECK) {
                            TreeItem treeItem = (TreeItem)e.item;
                            JdbcNode jdbcNode = (JdbcNode)treeItem.getData();
                            JdbcImportObjectsPage.this.treeViewer.getTree().setSelection(new TreeItem[] {treeItem});
                            nodeSelected(jdbcNode);
                            setNodeSelected(jdbcNode, treeItem, jdbcNode.getSelectionMode() != JdbcNode.SELECTED);
                        }
                    }

                    public void widgetDefaultSelected( SelectionEvent e ) {
                    }
                });

                this.treeViewer.setLabelProvider(new LabelProvider() {

                    @Override
                    public String getText( final Object node ) {
                        return ((JdbcNode)node).getName();
                    }
                });
                // Add listener to display details when a node is selected.
                this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                    public void selectionChanged( final SelectionChangedEvent event ) {
                        JdbcNode node = (JdbcNode)((IStructuredSelection)event.getSelection()).getFirstElement();
                        nodeSelected(node);
                    }
                });
                // Add listener to expand/collapse node when double-clicked
                this.treeViewer.addDoubleClickListener(new IDoubleClickListener() {

                    public void doubleClick( final DoubleClickEvent event ) {
                        nodeDoubleClicked(event);
                    }
                });
                // Add listener to select node when expanded/collapsed
                this.treeViewer.addTreeListener(new ITreeViewerListener() {

                    public void treeCollapsed( final TreeExpansionEvent event ) {
                        // nodeExpandedOrCollapsed(event);
                    }

                    public void treeExpanded( final TreeExpansionEvent event ) {
                        nodeExpanded(event);
                    }
                });
            }
            final ViewForm tabView = new ViewForm(this.splitter, SWT.BORDER);
            this.tabFolder = WidgetFactory.createTabFolder(tabView);
            tabView.setContent(this.tabFolder);
            // Create listener to populate tabs upon selection. Added to tab folder only after tab selected.
            this.tabListener = new SelectionAdapter() {

                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    tabSelected();
                }
            };
        }
        this.splitter.setWeights(SPLITTER_WEIGHTS);
        this.statusLabel = WidgetFactory.createLabel(pg, GridData.HORIZONTAL_ALIGN_FILL);
        final Button deselectAllButton = WidgetFactory.createButton(pg, DESELECT_ALL_BUTTON);
        deselectAllButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                deselectAllButtonSelected();
            }
        });

        Label spacer = new Label(pg, SWT.NULL);
        GridData spGridData = new GridData();
        spGridData.horizontalSpan = 2;
        spacer.setLayoutData(spGridData);

        // Control to prompt for generation of XML Documents
        showSelectedSchemasButton = new Button(pg, SWT.CHECK);
        GridData buttonGridData = new GridData();
        buttonGridData.horizontalSpan = 1;
        showSelectedSchemasButton.setLayoutData(buttonGridData);
        showSelectedSchemasButton.setText("Show Only Selected Schemas"); //Util.getString("GenerateXsdWizard.genXml")); //$NON-NLS-1$
        showSelectedSchemasButton.setSelection(false);

        restoreWidgetValues();

        showSelectedSchemasButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleShowSelectedSchemasButtonPressed();
                saveWidgetValues();
            }
        });
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     * @since 4.0
     */
    @Override
    public void setVisible( final boolean visible ) {
        if (visible) {
            this.db = ((JdbcImportWizard)getWizard()).getDatabase();
            refresh();
        }
        super.setVisible(visible);
    }

    /**
     * @since 4.0
     */
    protected void deselectAllButtonSelected() {
        final TreeItem[] items = this.treeViewer.getTree().getItems();
        for (int ndx = items.length; --ndx >= 0;) {
            final TreeItem item = items[ndx];
            final JdbcNode node = (JdbcNode)item.getData();
            setNodeSelected(node, item, false);
        }
    }

    /**
     * @since 4.0
     */
    void nodeDoubleClicked( final DoubleClickEvent event ) {
        final Object node = ((IStructuredSelection)event.getSelection()).getFirstElement();
        this.treeViewer.setExpandedState(node, !this.treeViewer.getExpandedState(node));
    }

    /**
     * @since 4.0
     */
    void nodeExpanded( final TreeExpansionEvent event ) {
        // nodeExpandedOrCollapsed(event);
        updateCheckBoxes(WidgetUtil.findTreeItem(event.getElement(), this.treeViewer).getItems());
    }

    void handleShowSelectedSchemasButtonPressed() {
        refresh();
    }

    /**
     * @since 4.0
     */
    void nodeSelected( final JdbcNode node ) {
        // Return if node already selected
        if (node == null || node == this.selectedNode) {
            return;
        }
        // Remember selected node
        this.selectedNode = node;
        // Remove tab listener
        this.tabFolder.removeSelectionListener(this.tabListener);
        // Remove old tabs
        final CTabItem[] items = this.tabFolder.getItems();
        for (int ndx = items.length; --ndx >= 0;) {
            items[ndx].dispose();
        }
        try {
            final String[] names = node.getNamesOfResults();
            // Hide description/value areas if no details available
            if (names.length == 0) {
                WidgetFactory.createTab(this.tabFolder, NA);
                return;
            }
            // Create tabs
            for (int ndx = 0; ndx < names.length; ++ndx) {
                WidgetFactory.createTab(this.tabFolder, names[ndx]);
            }
            // Add listener to populate tabs upon selection
            this.tabFolder.addSelectionListener(this.tabListener);
            // Select first tab
            if (names.length > 0) {
                // Select first tab
                this.tabFolder.setSelection(this.tabFolder.getItem(0));
                this.tabFolder.notifyListeners(SWT.Selection, new Event());
            }
        } catch (final JdbcException err) {
            JdbcUiUtil.showAccessError(err);
        }
    }

    /**
     * @since 4.0
     */
    protected void refresh() {
        this.db.refresh();
        JdbcNode[] selectedNodes = getSelectedChildren();
        if (selectedNodes == null || selectedNodes.length == 0) {

            if (showSelectedSchemasButton.getSelection()) {
                // We need to show ALL schemas because user has to select at least one
                showSelectedSchemasButton.setSelection(false);
                handleShowSelectedSchemasButtonPressed();
                showSelectedSchemasButton.setEnabled(false);
            }
        } else {
            showSelectedSchemasButton.setEnabled(true);
        }

        final String name = this.db.getName();
        this.dbLabel.setText(name);
        this.treeViewer.setInput(this.db);
        validatePage(this.treeViewer.getTree().getItems());
        // Set initial message
        if (isPageComplete()) {
            setMessage(getString(INITIAL_MESSAGE_ID, name));
        }
        // Scroll to first selected node
        try {
            final JdbcNode[] nodes = this.db.getChildren();
            for (int ndx = 0; ndx < nodes.length; ++ndx) {
                final JdbcNode node = nodes[ndx];
                if (node.getSelectionMode() != JdbcNode.UNSELECTED) {
                    this.treeViewer.setSelection(new StructuredSelection(node), true);
                    break;
                }
            }
        } catch (final JdbcException err) {
            JdbcUiUtil.showAccessError(err);
        }
    }

    /**
     * @since 4.0
     */
    void tabSelected() {
        // Populate tab
        final CTabFolder tabFolder = this.tabFolder;
        final CTabItem tab = tabFolder.getSelection();
        // Return if table already created
        if (tab.getControl() != null) {
            return;
        }
        try {
            final Request rqst = this.selectedNode.getRequest(this.selectedNode.getNamesOfResults()[tabFolder.getSelectionIndex()]);
            if (!rqst.invoke()) {
                final IStatus status = rqst.getProblems();
                Util.log(status);
                Image img;
                if (status.getSeverity() == IStatus.ERROR) {
                    img = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
                } else {
                    img = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
                }
                final String msg = WidgetUtil.getCauseMessage(getString(REQUEST_ERROR_MESSAGE_ID, tab.getText()), status);
                final Composite panel = WidgetFactory.createPanel(this.tabFolder, SWT.NO_TRIM, GridData.FILL_BOTH, 1, 2);
                WidgetFactory.createLabel(panel, GridData.VERTICAL_ALIGN_BEGINNING, img, SWT.TOP);
                WidgetFactory.createWrappingLabel(panel, GridData.FILL_BOTH, msg);
                tab.setControl(panel);
                return;
            }
            final Results results = rqst.getResults();
            final List metadatas = results.getMetadata().getColumnMetadata();
            if (metadatas.isEmpty()) {
                final Object[] row = results.getRows();
                if (row.length > 0) {
                    final Object obj = row[0];
                    if (obj != null) {
                        WidgetFactory.createLabel(this.tabFolder, obj.toString(), SWT.WRAP);
                    }
                }
            } else {
                // Create table
                final TableViewer viewer = new TableViewer(this.tabFolder, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
                tab.setControl(viewer.getTable());
                // Create label provider
                viewer.setLabelProvider(new AbstractTableLabelProvider() {

                    public String getColumnText( final Object row,
                                                 final int column ) {
                        final Object obj = ((List)row).get(column);
                        return (obj == null ? EMPTY_STRING : obj.toString());
                    }
                });
                // Create content provider
                viewer.setContentProvider(new IStructuredContentProvider() {

                    public Object[] getElements( final Object inputElement ) {
                        return results.getRows();
                    }

                    public void dispose() {
                    }

                    public void inputChanged( final Viewer viewer,
                                              final Object oldInput,
                                              final Object newInput ) {
                    }
                });
                // Add columns to table
                final Table table = viewer.getTable();
                table.setHeaderVisible(true);
                // Defect in case there is no metadata label, we'll provide one (EXCEL import case)
                // Defect 22706
                int nUnknownColumns = 0;
                for (final Iterator iter = metadatas.iterator(); iter.hasNext();) {
                    final TableColumn col = new TableColumn(table, SWT.NONE);
                    final ColumnMetadata metadata = (ColumnMetadata)iter.next();
                    if (metadata.getLabel() != null) {
                        col.setText(metadata.getLabel());
                    } else {
                        col.setText(UNKNOWN_COLUMN_LABEL + nUnknownColumns);
                        nUnknownColumns++;
                    }
                    switch (metadata.getType()) {
                        case Types.BIGINT:
                        case Types.BIT:
                        case Types.DECIMAL:
                        case Types.DOUBLE:
                        case Types.FLOAT:
                        case Types.INTEGER:
                        case Types.NUMERIC:
                        case Types.REAL:
                        case Types.SMALLINT:
                        case Types.TINYINT: {
                            col.setAlignment(SWT.RIGHT);
                            break;
                        }
                    }
                }
                // Initialize table
                viewer.setInput(this);
                final TableColumn[] cols = table.getColumns();
                for (int ndx = 0; ndx < cols.length; ++ndx) {
                    cols[ndx].pack();
                }
            }
        } catch (final JdbcException err) {
            JdbcUiUtil.showAccessError(err);
        }
    }

    /**
     * @since 4.0
     */
    protected void setNodeSelected( final JdbcNode node,
                                    final TreeItem item,
                                    final boolean selected ) {
        // Select node in model
        // this.treeViewer.setSelection(new StructuredSelection(node));
        node.setSelected(selected);
        // Update check boxes of item, ancestors, and expanded (now or previously) children
        updateCheckBox(item);
        for (TreeItem parent = item.getParentItem(); parent != null; parent = parent.getParentItem()) {
            updateCheckBox(parent);
        }
        validatePage(item.getItems());
    }

    protected JdbcNode[] getSelectedChildren() {
        try {
            return getDatabase().getSelectedChildren();
        } catch (final JdbcException err) {
            JdbcUiUtil.showAccessError(err);
            return null;
        }
    }

    /**
     * @since 4.0
     */
    protected void updateCheckBox( final TreeItem item ) {
        final JdbcNode node = (JdbcNode)item.getData();
        if (node == null) {
            return;
        }
        final int selection = node.getSelectionMode();
        item.setChecked(selection != JdbcNode.UNSELECTED);
        item.setGrayed(selection == JdbcNode.PARTIALLY_SELECTED);
    }

    /**
     * @since 4.0
     */
    private void updateCheckBoxes( final TreeItem[] items ) {
        for (int ndx = items.length; --ndx >= 0;) {
            final TreeItem item = items[ndx];
            updateCheckBoxes(item.getItems());
            updateCheckBox(item);
        }
    }

    /**
     * @since 4.0
     */
    private void updateCounts( final JdbcNode[] nodes ) throws JdbcException {
        for (int ndx = nodes.length; --ndx >= 0;) {
            final JdbcNode node = nodes[ndx];
            final int selection = node.getSelectionMode();
            if (selection != JdbcNode.UNSELECTED) {
                if (node.allowsChildren()) {
                    updateCounts(node.getChildren());
                } else if (selection == JdbcNode.SELECTED) {
                    final String name = node.getTypeName();
                    final Integer count = (Integer)this.counts.get(name);
                    if (count == null) {
                        this.counts.put(name, new Integer(1));
                    } else {
                        this.counts.put(name, new Integer(count.intValue() + 1));
                    }
                }
            }
        }
    }

    /**
     * @since 4.0
     */
    protected void validatePage( final TreeItem[] items ) {
        try {
            // Recalculate node type selection counts
            this.counts.clear();
            updateCounts(this.db.getChildren());
            // Update check boxes
            updateCheckBoxes(items);
            // Update status label, page message, and page completeness
            final boolean valid = (this.counts.size() > 0);
            if (valid) {
                final StringBuffer msg = new StringBuffer();
                final Set set = this.counts.entrySet();
                for (final Iterator iter = set.iterator(); iter.hasNext();) {
                    final Entry entry = (Entry)iter.next();
                    if (msg.length() > 0) {
                        if (set.size() > 2) {
                            msg.append(',');
                        }
                        msg.append(' ');
                        if (!iter.hasNext()) {
                            msg.append(Util.keyExists(AND) ? Util.getString(AND) : AND);
                            msg.append(' ');
                        }
                    }
                    msg.append(entry.getValue());
                    msg.append(' ');
                    msg.append(entry.getKey().toString().toLowerCase());
                    this.statusLabel.setText(getString(STATUS_LABEL_ID, msg.toString()));
                    WizardUtil.setPageComplete(this);
                }
            } else {
                this.statusLabel.setText(getString(STATUS_LABEL_ID, NO_OBJECTS));
                WizardUtil.setPageComplete(this, INVALID_PAGE_MESSAGE, ERROR);
            }
        } catch (final JdbcException err) {
            JdbcUiUtil.showAccessError(err);
        }

        if (getSelectedChildren().length == 0) showSelectedSchemasButton.setEnabled(false);
        else showSelectedSchemasButton.setEnabled(true);
    }

    /**
     * @return Returns the database.
     * @since 4.3
     */
    public final JdbcDatabase getDatabase() {
        return db;
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they will persist into the next invocation of
     * this wizard page
     */
    protected void saveWidgetValues() {
        final IDialogSettings dlgSettings = getDialogSettings();
        dlgSettings.put(SHOW_SELECTED_SCHEMAS_ID, showSelectedSchemasButton.getSelection());
    }

    /**
     * Use the dialog store to restore widget values to the values that they held last time this wizard was used to completion
     */
    protected void restoreWidgetValues() {
        final IDialogSettings dlgSettings = getDialogSettings();
        boolean showSelectedSchemas = dlgSettings.getBoolean(SHOW_SELECTED_SCHEMAS_ID);
        showSelectedSchemasButton.setSelection(showSelectedSchemas);
    }

    /**
     * @return The TreeContentProvider used by this page.
     * @since 4.3
     */
    protected ITreeContentProvider createTreeContentProvider() {
        return new TreeContentProvider();
    }

    /**
     * @since 4.0
     */
    class TreeContentProvider implements ITreeContentProvider {

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 4.0
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         * @since 4.0
         */
        public Object[] getChildren( final Object node ) {
            try {
                if (showSelectedSchemasButton.getSelection()) {
                    if (node instanceof JdbcDatabase) return getSelectedChildren();
                }
                return ((JdbcNode)node).getChildren();
            } catch (final JdbcException err) {
                JdbcUiUtil.showAccessError(err);
                return null;
            }
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.0
         */
        public Object[] getElements( final Object inputElement ) {

            try {
                if (showSelectedSchemasButton.getSelection()) {
                    return getSelectedChildren();
                }

                return JdbcImportObjectsPage.this.db.getChildren();
            } catch (final JdbcException err) {
                JdbcUiUtil.showAccessError(err);
                return null;
            }
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         * @since 4.0
         */
        public Object getParent( final Object node ) {
            return ((JdbcNode)node).getParent();
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         * @since 4.0
         */
        public boolean hasChildren( final Object node ) {
            try {
                return (((JdbcNode)node).getChildren().length > 0);
            } catch (final JdbcException err) {
                JdbcUiUtil.showAccessError(err);
                return false;
            }
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.0
         */
        public void inputChanged( final Viewer viewer,
                                  final Object oldInput,
                                  final Object newInput ) {
        }
    }
}
