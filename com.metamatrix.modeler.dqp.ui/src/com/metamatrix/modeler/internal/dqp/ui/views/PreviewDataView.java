/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.views;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import com.metamatrix.core.util.HashCodeUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.dqp.ui.actions.CopySqlResultsToClipboardAction;
import com.metamatrix.modeler.internal.dqp.ui.actions.CopyXmlResultsToClipboardAction;
import com.metamatrix.modeler.internal.dqp.ui.actions.SaveSqlResultsToFileAction;
import com.metamatrix.modeler.internal.dqp.ui.actions.SaveXmlResultsToFileAction;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.SqlResultsModel;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.XmlDocumentResultsModel;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Label;

/**
 * @since 5.5.3
 */
public class PreviewDataView extends AbstractPreviewDataView implements IResultsProvider, ISelectionChangedListener {

    private ILabelProvider labelProvider;

    private ILabelProvider labelProviderHelper = new ModelExplorerLabelProvider();

    private Map<PreviewResultsModel, ResultsPanel> idResultsMap = new HashMap<PreviewResultsModel, ResultsPanel>();

    private List<PreviewResultsModel> previewResults = new ArrayList<PreviewResultsModel>();

    private StackLayout stackLayout;

    private TableViewer viewer;

    private IAction previewCopyAction;

    private PreviewSaveAction previewSaveAction;

    private PreviewSorter sorter;

    private long maxXmlDocLength = 10000;

    TableViewer accessListViewer() {
        return this.viewer;
    }

    /**
     * @return the currently displayed result models
     * @since 5.5.3
     */
    PreviewResultsModel[] accessModelObjects() {
        return this.previewResults.toArray(new PreviewResultsModel[this.previewResults.size()]);
    }

    /**
     * Adds results to the existing results being displayed.
     * 
     * @param results the results being added
     * @param eObject the object that is being previewed
     * @since 5.5.3
     */
    public void addResults( final IResults results,
                            final EObject eObject ) {
        Image image = this.labelProviderHelper.getImage(eObject);
        String name = getName(eObject);
        String path = getPath(eObject);
        final PreviewResultsModel model = new PreviewResultsModel(results, name, path, image);

        final Composite parentControl = this.resultsParent;
        final Map<PreviewResultsModel, ResultsPanel> map = this.idResultsMap;
        final List<PreviewResultsModel> list = this.previewResults;

        if (!isResultsParentControlDisposed()) {
            getSite().getShell().getDisplay().asyncExec(new Runnable() {

                public void run() {
                    if (getSite().getShell().getDisplay().isDisposed()) {
                        return;
                    }

                    // create and setup results
                    final ResultsPanel pnl = new ResultsPanel(PreviewDataView.this, parentControl);

                    // add to collections
                    if (!list.contains(model)) {
                        list.add(model);
                    }
                    map.put(model, pnl);

                    Control c = createResultsControl(pnl.getResultsDetailParent(), model);
                    pnl.setResultsDetailControl(c);
                    setTopControl(pnl);

                    // set the query plan if one exists
                    Statement statement = results.getStatement();

                    if ((statement != null) && (statement instanceof com.metamatrix.jdbc.api.Statement)) {
                        pnl.setQueryPlan(((com.metamatrix.jdbc.api.Statement)statement).getPlanDescription());
                        pnl.setDebugLog(((com.metamatrix.jdbc.api.Statement)statement).getDebugLog());
                        handleShowResults();
                    }

                    // select results
                    accessListViewer().refresh();
                    packHistoryList();
                    accessListViewer().setSelection(new StructuredSelection(model));
                }
            });
        }
    }

    /**
     * Closes all {@link PreviewResultsModel result models} displayed in this view
     * 
     * @since 5.5.3
     */
    void closeAllResults() {
        closeResults(accessModelObjects());
    }

    /**
     * @param results the {@link PreviewResultsModel result models} that should be closed
     * @since 5.5.3
     */
    void closeResults( Object[] results ) {
        for (Object result : results) {
            this.viewer.setSelection(new StructuredSelection(result));
            handleCloseResults();
        }
    }

    private int confirmDisplayAllXmlResults( XmlDocumentResultsModel xmlModel ) {
        int okToDisplay = Window.CANCEL;
        String[] xmlDocs = xmlModel.getResults();
        long totalStringLength = 0;

        // find total length of all documents
        for (int i = 0; i < xmlDocs.length; ++i) {
            totalStringLength += xmlDocs[i].length();
        }

        if (totalStringLength > maxXmlDocLength) {
            okToDisplay = Window.OK;

            // reuse existing messages
            String prefix = I18nUtil.getPropertyPrefix(XmlDocumentSqlResultsView.class);
            String title = UTIL.getString(prefix + "confirmDisplayResults.title"); //$NON-NLS-1$
            String line_1 = UTIL.getString(prefix + "confirmDisplayResults.line_1", Long.toString(maxXmlDocLength)); //$NON-NLS-1$
            String line_2 = UTIL.getString(prefix + "confirmDisplayResults.line_2"); //$NON-NLS-1$
            String line_3 = UTIL.getString(prefix + "confirmDisplayResults.line_3"); //$NON-NLS-1$
            String line_4 = UTIL.getString(prefix + "confirmDisplayResults.line_4"); //$NON-NLS-1$

            String message = line_1 + line_2 + line_3 + line_4;
            okToDisplay = openCancelableQuestion(getViewSite().getShell(), title, message);
        }

        return okToDisplay;
    }

    /**
     * @param object the next object to be previewed
     * @return a content provider for the current result model objects
     * @since 5.5.3
     */
    public IPreviewDataContentProvider createContentProvider( EObject object ) {
        return new PreviewContentProvider(object);
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#getCopyResultsAction()
     * @since 5.5.3
     */
    @Override
    protected IAction createCopyResultsAction() {
        if (this.previewCopyAction == null) {
            this.previewCopyAction = new PreviewCopyAction(this, this);
        }

        return this.previewCopyAction;
    }

    private void createList( Composite parent ) {
        Composite listPanel = WidgetFactory.createGroup(parent, GridData.FILL_BOTH);

        this.viewer = new TableViewer(listPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        this.viewer.addSelectionChangedListener(this);
        this.labelProvider = new PreviewLabelProvider();
        this.viewer.setLabelProvider(this.labelProvider);
        this.viewer.setContentProvider(new PreviewContentProvider(null));
        this.viewer.setInput(this.previewResults);

        // configure table
        Table table = this.viewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        TableColumn col = new TableColumn(table, SWT.LEFT);
        col.setText(getString("historyColumnHeading")); //$NON-NLS-1$
        col.addSelectionListener(new SelectionAdapter() {

            /**
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             * @since 5.5.3
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                sortHistoryList();
            }
        });

        // initially set the column width to the width of the table then stop listening for control events since the column
        // will be resized each time new results are added
        final TableColumn tableColumn = col;
        table.addControlListener(new ControlAdapter() {

            /**
             * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
             * @since 5.5.3
             */
            @Override
            public void controlResized( ControlEvent e ) {
                tableColumn.setWidth(accessListViewer().getTable().getClientArea().width);
                tableColumn.getParent().removeControlListener(this);
            }
        });

        hookToolTips();
    }

    /**
     * @param control the control whose context menu is being created
     * @return the context menu
     * @since 5.5.3
     */
    private Menu createMenu( Control control ) {
        MenuManager mgr = new MenuManager();
        mgr.add(this.previewCopyAction);
        mgr.add(this.previewSaveAction);
        Menu menu = mgr.createContextMenu(control);

        return menu;
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    public void createPartControl( Composite parent ) {
        // parent is passed in by Eclipse and has a fill layout.
        Layout layout = parent.getLayout();

        if (layout instanceof FillLayout) {
            ((FillLayout)layout).marginWidth = 4;
        }

        SashForm splitter = WidgetFactory.createSplitter(parent, SWT.HORIZONTAL);

        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
        splitter.setLayoutData(gid);

        createList(splitter);

        Composite pnl = WidgetFactory.createGroup(splitter, GridData.FILL_BOTH);
        ((GridLayout)pnl.getLayout()).marginHeight = 2;
        super.createPartControl(pnl);

        splitter.setWeights(new int[] {2, 8});
        splitter.layout();
    }

    /**
     * Creates the <code>Control</code> where the results are displayed.
     * 
     * @param theParent the parent control
     * @param theResults the results being added
     * @return the control displaying the results
     * @since 5.5.3
     */
    Control createResultsControl( Composite parent,
                                  IResults results ) {
        assert (results instanceof PreviewResultsModel);
        PreviewResultsModel previewResults = (PreviewResultsModel)results;

        if (previewResults.getResults() instanceof SqlResultsModel) {
            return createSqlResultsControl(parent, results);
        }

        return createXmlResultsControl(parent, results);
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractPreviewDataView#createResultsParent(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected Composite createResultsParent( Composite parent ) {
        Composite pnlCards = new Composite(parent, SWT.NONE);
        pnlCards.setLayout(this.stackLayout = new StackLayout());
        pnlCards.setLayoutData(new GridData(GridData.FILL_BOTH));

        return pnlCards;
    }

    private Control createSqlResultsControl( Composite parent,
                                             IResults results ) {
        PreviewResultsModel previewResults = (PreviewResultsModel)results;

        SqlResultsViewer viewer = new SqlResultsViewer(parent, (SqlResultsModel)previewResults.getResults());
        viewer.setInput(previewResults.getResults());

        // save results object to be used later
        Table table = viewer.getTable();
        table.setData(previewResults);
        table.setMenu(createMenu(table));

        // pack table columns
        for (int size = table.getColumnCount(), i = 0; i < size; ++i) {
            table.getColumn(i).pack();
        }

        return table;
    }

    private Control createXmlResultsControl( Composite parent,
                                             IResults results ) {
        PreviewResultsModel previewResults = (PreviewResultsModel)results;
        XmlDocumentResultsModel resultsModel = (XmlDocumentResultsModel)previewResults.getResults();

        StyledText styledText = new StyledText(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        styledText.setLayoutData(new GridData(GridData.FILL_BOTH));

        // construct the data
        String text = results.getStatus().getMessage();
        IPreferenceStore store = SQLExplorerPlugin.getDefault().getPreferenceStore();

        // check preference
        if (store.getLong(IConstants.XML_CHAR_LIMIT) != 0L) {
            this.maxXmlDocLength = store.getLong(IConstants.XML_CHAR_LIMIT);
        }

        if (results.getStatus().getSeverity() != IStatus.ERROR) {
            String prefix = I18nUtil.getPropertyPrefix(XmlDocumentSqlResultsView.class); // reuse existing messages
            int confirmation = confirmDisplayAllXmlResults(resultsModel);

            switch (confirmation) {
                case 0: { // RESULTS ARE TOO LARGE AND USER WANTS TO SAVE TO FILE
                    // perform the save
                    this.previewSaveAction.run();

                    if (this.previewSaveAction.wasSaveSuccessful()) {
                        String key = UTIL.getString(prefix + "tooLargeDocumentSaved"); //$NON-NLS-1$
                        text = key + StringUtil.Constants.DBL_SPACE + this.previewSaveAction.getFileName();
                    } else {
                        text = UTIL.getString(prefix + "tooLargeDocumentSaveCancelled"); //$NON-NLS-1$
                    }

                    break;
                }

                case 2: {
                    text = UTIL.getString(prefix + "tooLargeDisplayCancelled"); //$NON-NLS-1$
                    break;
                }

                default: {
                    text = resultsModel.getResultsAsText();
                }
            }

            // store results model
            styledText.setData(previewResults);
        }

        styledText.setText(text.toString());
        styledText.setMenu(createMenu(styledText));

        return styledText;
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     * @since 5.5.3
     */
    @Override
    public void dispose() {
        if (this.labelProvider != null) {
            this.labelProvider.dispose();
        }

        if (this.labelProviderHelper != null) {
            this.labelProviderHelper.dispose();
        }

        super.dispose();
    }

    /**
     * @return the label provider used to display result model objects
     * @since 5.5.3
     */
    public ILabelProvider getLabelProvider() {
        return this.labelProvider;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#getLocalizationKeyPrefix()
     * @since 5.5.3
     */
    @Override
    protected String getLocalizationKeyPrefix() {
        return I18nUtil.getPropertyPrefix(PreviewDataView.class);
    }

    String getName( EObject eObject ) {
        return this.labelProviderHelper.getText(eObject);
    }

    String getPath( EObject eObject ) {
        return ModelerCore.getModelEditor().getFullPathToParent(eObject).toString();
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider#getResults()
     * @since 5.5.3
     */
    public IResults getResults() {
        Control c = getSelectedResultsControl();

        if ((c != null) && (c.getData() != null)) {
            PreviewResultsModel model = (PreviewResultsModel)c.getData();
            return model.getResults();
        }

        return null;
    }

    /**
     * @return the count of preview results currently showing in this view
     */
    public int getResultCount() {
        return this.previewResults.size();
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#getSaveToFileAction()
     * @since 5.5.3
     */
    @Override
    protected IAction createSaveToFileAction() {
        if (this.previewSaveAction == null) {
            this.previewSaveAction = new PreviewSaveAction(this);
        }

        return this.previewSaveAction;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractPreviewDataView#getSelectedResultsPanel()
     * @since 5.5.3
     */
    @Override
    protected ResultsPanel getSelectedResultsPanel() {
        if (!this.viewer.getSelection().isEmpty()) {
            return this.idResultsMap.get(((StructuredSelection)this.viewer.getSelection()).getFirstElement());
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractPreviewDataView#handleCloseResults()
     * @since 5.5.3
     */
    @Override
    protected void handleCloseResults() {
        Object model = getSelectedResultsControl().getData();

        // find the results to display next
        Object results = null;
        int index = this.previewResults.indexOf(model);

        if (index == 0) {
            if (this.previewResults.size() > 1) {
                results = this.previewResults.get(1);
            }
        } else {
            results = this.previewResults.get(index - 1);
        }

        this.previewResults.remove(index);
        this.idResultsMap.remove(model).dispose();
        this.viewer.refresh();

        if (results != null) {
            this.viewer.setSelection(new StructuredSelection(results), true);
        }

        updateState();
    }

    private void hookToolTips() {
        final Table table = this.viewer.getTable();
        final Listener labelListener = new Listener() {

            public void handleEvent( Event event ) {
                Label label = (Label)event.widget;
                Shell shell = label.getShell();

                switch (event.type) {
                    case SWT.MouseDown:
                        accessListViewer().setSelection(new StructuredSelection(label.getData("_TOOLTIP"))); //$NON-NLS-1$
                        shell.dispose();
                        break;
                    case SWT.MouseExit:
                        shell.dispose();
                        break;
                }
            }
        };

        Listener tableListener = new Listener() {

            Shell tip = null;
            Label label = null;

            public void handleEvent( Event event ) {
                switch (event.type) {
                    case SWT.Dispose:
                    case SWT.KeyDown:
                    case SWT.MouseMove: {
                        if (tip == null) {
                            break;
                        }

                        tip.dispose();
                        tip = null;
                        label = null;
                        break;
                    }
                    case SWT.MouseHover: {
                        TableItem item = table.getItem(new Point(event.x, event.y));

                        if (item != null) {
                            if (tip != null && !tip.isDisposed()) {
                                tip.dispose();
                            }

                            tip = new Shell(table.getShell(), SWT.ON_TOP | SWT.TOOL);
                            tip.setLayout(new FillLayout());

                            label = new Label(tip, SWT.NONE);
                            label.setForeground(tip.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                            label.setBackground(tip.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

                            label.setData("_TOOLTIP", item); //$NON-NLS-1$
                            label.setText(((PreviewResultsModel)item.getData()).getToolTip());
                            label.addListener(SWT.MouseExit, labelListener);
                            label.addListener(SWT.MouseDown, labelListener);

                            Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                            Rectangle rect = item.getBounds(0);
                            Point pt = table.toDisplay(rect.x, rect.y);
                            tip.setBounds(pt.x + 16, pt.y, size.x, size.y);
                            tip.setVisible(true);
                        }
                    }
                }
            }
        };

        table.setToolTipText(""); //$NON-NLS-1$
        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseHover, tableListener);
    }

    /**
     * @param eObject the object whose preview results are being checked
     * @return <code>true</code> if preview results are currently being displayed for the specified object
     */
    public boolean isShowingResult( EObject eObject ) {
        for (PreviewResultsModel model : this.previewResults) {
            if (model.getName().equals(getName(eObject)) && model.getPath().equals(getPath(eObject))) {
                return true;
            }
        }

        return false;
    }

    private int openCancelableQuestion( Shell parent,
                                        String title,
                                        String message ) {
        MessageDialog dialog = new MessageDialog(parent, title, null, // accept the default window icon
                                                 message, MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL,
                                                     IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL}, 0); // yes is the
        // default
        return dialog.open();
    }

    void packHistoryList() {
        this.viewer.getTable().getColumn(0).pack();
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @since 5.5.3
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        StructuredSelection selection = (StructuredSelection)this.viewer.getSelection();

        if (!selection.isEmpty()) {
            setTopControl(this.idResultsMap.get(selection.getFirstElement()));
            updateState();
        }
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     * @since 5.5.3
     */
    @Override
    public void setFocus() {
        this.viewer.getControl().setFocus();
    }

    void setTopControl( Control newTopControl ) {
        this.stackLayout.topControl = newTopControl;
        this.resultsParent.layout();
    }

    void sortHistoryList() {
        if (this.sorter == null) {
            this.sorter = new PreviewSorter();
            this.viewer.setSorter(this.sorter);
        } else {
            this.sorter.reverseDirection();
            this.viewer.refresh();
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#updateState()
     * @since 5.5
     */
    @Override
    protected void updateState() {
        super.updateState();

        Control c = getSelectedResultsControl();
        String msg = ""; //$NON-NLS-1$

        if ((c != null) && (c.getData() != null)) {
            PreviewResultsModel previewModel = (PreviewResultsModel)c.getData();

            if (previewModel.getResults() instanceof SqlResultsModel) {
                String prefix = I18nUtil.getPropertyPrefix(SqlResultsView.class);
                SqlResultsModel model = (SqlResultsModel)previewModel.getResults();
                Object params = new Object[] {Integer.toString(((Table)c).getItemCount()),
                    Integer.toString(model.getTotalRowCount())};

                msg = UTIL.getString(prefix + "recordCount", params); //$NON-NLS-1$

                if (model.getStatus().getSeverity() == IStatus.ERROR) {
                    msg = UTIL.getString(prefix + "resultsError") + " - " + msg; //$NON-NLS-1$  //$NON-NLS-2$
                }
            } else {
                String prefix = I18nUtil.getPropertyPrefix(XmlDocumentSqlResultsView.class);
                XmlDocumentResultsModel model = (XmlDocumentResultsModel)previewModel.getResults();

                if (model.getStatus().getSeverity() != IStatus.ERROR) {
                    msg = UTIL.getString(prefix + "recordCount", new Object[] {Integer.toString(model.getResults().length), //$NON-NLS-1$
                        Integer.toString(model.getTotalDocumentCount())});
                }
            }
        }

        // update row count message
        setRowCountText(msg);
    }

    class PreviewLabelProvider extends LabelProvider {

        /**
         * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
         * @since 5.5.3
         */
        @Override
        public Image getImage( Object element ) {
            if (element instanceof PreviewResultsModel) {
                return ((PreviewResultsModel)element).getImage();
            }

            if (element instanceof EObject) {
                return getImage(element);
            }

            return super.getImage(element);
        }

        /**
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         * @since 5.5.3
         */
        @Override
        public String getText( Object element ) {
            if (element instanceof PreviewResultsModel) {
                PreviewResultsModel resultObj = (PreviewResultsModel)element;
                String key = I18nUtil.getPropertyPrefix(PreviewDataView.class) + "objectDisplayName"; //$NON-NLS-1$
                return UTIL.getString(key, resultObj.getName(), resultObj.getPath());
            }

            if (element instanceof EObject) {
                return getName((EObject)element);
            }

            return super.getText(element);
        }
    }

    protected class PreviewResultsModel implements IResults {

        private final IResults results;

        final String name;

        final String path;

        private final Image image;

        public PreviewResultsModel( IResults results,
                                    String name,
                                    String path,
                                    Image image ) {
            this.results = results;
            this.name = name;
            this.path = path;
            this.image = image;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         * @since 5.5.3
         */
        @Override
        public boolean equals( Object obj ) {
            if (obj instanceof PreviewResultsModel) {
                PreviewResultsModel other = (PreviewResultsModel)obj;

                if (this.name.equals(other.name) && this.path.equals(other.path)) {
                    return true;
                }
            }

            return false;
        }

        Image getImage() {
            return this.image;
        }

        String getName() {
            return this.name;
        }

        String getPath() {
            return this.path;
        }

        public IResults getResults() {
            return this.results;
        }

        /**
         * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getSql()
         * @since 5.5.3
         */
        public String getSql() {
            return this.results.getSql();
        }

        /**
         * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getStatement()
         * @since 5.5.3
         */
        public Statement getStatement() {
            return this.results.getStatement();
        }

        /**
         * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getStatus()
         * @since 5.5.3
         */
        public IStatus getStatus() {
            return this.results.getStatus();
        }

        public String getToolTip() {
            String key = I18nUtil.getPropertyPrefix(PreviewDataView.class) + "objectToolTip"; //$NON-NLS-1$
            return UTIL.getString(key, this.name, this.path, getSql());
        }

        /**
         * @see java.lang.Object#hashCode()
         * @since 5.5.3
         */
        @Override
        public int hashCode() {
            int result = 0;
            result = HashCodeUtil.hashCode(result, this.name);
            result = HashCodeUtil.hashCode(result, this.path);

            return result;
        }
    }

    private class PreviewSaveAction extends Action {

        final SaveSqlResultsToFileAction sqlSave;
        final SaveXmlResultsToFileAction xmlSave;
        final IResultsProvider resultsProvider;

        public PreviewSaveAction( IResultsProvider resultsProvider ) {
            super(
                  UTIL.getString(I18nUtil.getPropertyPrefix(SaveSqlResultsToFileAction.class) + "saveToFileAction"), IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
            this.sqlSave = new SaveSqlResultsToFileAction(resultsProvider);
            this.xmlSave = new SaveXmlResultsToFileAction(resultsProvider);
            this.resultsProvider = resultsProvider;

            setImageDescriptor(this.sqlSave.getImageDescriptor());
            setToolTipText(this.sqlSave.getToolTipText());
            setEnabled(false);
        }

        /**
         * @return the name of the file where the last successful save occurred or <code>null</code>
         * @since 5.5.3
         */
        public String getFileName() {
            IResults results = this.resultsProvider.getResults();

            if (results instanceof XmlDocumentResultsModel) {
                return this.xmlSave.getFileName();
            }

            return this.sqlSave.getFileName();
        }

        /**
         * @see org.eclipse.jface.action.Action#run()
         * @since 5.5
         */
        @Override
        public void run() {
            IResults results = this.resultsProvider.getResults();

            if (results instanceof SqlResultsModel) {
                this.sqlSave.run();
            } else {
                this.xmlSave.run();
            }
        }

        /**
         * @return <code>true</code> if save was successful
         * @since 5.5.3
         */
        public boolean wasSaveSuccessful() {
            IResults results = this.resultsProvider.getResults();

            if (results instanceof SqlResultsModel) {
                return this.sqlSave.wasSaveSuccessful();
            }

            return this.xmlSave.wasSaveSuccessful();
        }
    }

    private class PreviewCopyAction extends Action {

        final IAction sqlCopy;
        final IAction xmlCopy;
        final IResultsProvider resultsProvider;

        public PreviewCopyAction( IResultsProvider resultsProvider,
                                  ViewPart view ) {
            super(
                  UTIL.getString(I18nUtil.getPropertyPrefix(CopySqlResultsToClipboardAction.class) + "copyAction"), IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
            this.sqlCopy = new CopySqlResultsToClipboardAction(resultsProvider, view);
            this.xmlCopy = new CopyXmlResultsToClipboardAction(resultsProvider, view);
            this.resultsProvider = resultsProvider;

            setImageDescriptor(this.sqlCopy.getImageDescriptor());
            setDisabledImageDescriptor(this.sqlCopy.getImageDescriptor());
            setHoverImageDescriptor(this.sqlCopy.getImageDescriptor());
            setToolTipText(this.sqlCopy.getToolTipText());
            setEnabled(true);
        }

        /**
         * @see org.eclipse.jface.action.Action#run()
         * @since 5.5
         */
        @Override
        public void run() {
            IResults results = this.resultsProvider.getResults();

            if (results instanceof SqlResultsModel) {
                this.sqlCopy.run();
            } else {
                this.xmlCopy.run();
            }
        }
    }

    class PreviewSorter extends ViewerSorter {

        private boolean ascending = true;

        /**
         * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 5.5.3
         */
        @Override
        public int compare( Viewer viewer,
                            Object e1,
                            Object e2 ) {
            int result = ((PreviewResultsModel)e1).getName().compareToIgnoreCase(((PreviewResultsModel)e2).getName());

            if (!this.ascending) {
                result *= -1;
            }

            return result;
        }

        public boolean isAscending() {
            return this.ascending;
        }

        public void reverseDirection() {
            this.ascending = !this.ascending;
        }
    }

    private class SqlResultsViewer extends TableViewer {

        SqlResultsModel model;

        public SqlResultsViewer( Composite theParent,
                                 SqlResultsModel theModel ) {
            super(theParent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

            this.model = theModel;

            setContentProvider(new IStructuredContentProvider() {

                public void dispose() {
                }

                public Object[] getElements( Object theInputElement ) {
                    return SqlResultsViewer.this.model.getRows();
                }

                public void inputChanged( Viewer theViewer,
                                          Object theOldInput,
                                          Object theNewInput ) {
                }
            });
            setLabelProvider(new TableLabelProvider());

            Table table = getTable();
            table.setHeaderVisible(true);
            table.setLinesVisible(true);

            constructTableColumns(model.getColumnNames());
        }

        private void constructTableColumns( String[] theColumnNames ) {
            Table table = this.getTable();

            for (int i = 0; i < theColumnNames.length; ++i) {
                TableColumn col = new TableColumn(table, SWT.LEFT);
                col.setText(theColumnNames[i]);
                col.pack();
            }
        }
    }

    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            return null;
        }

        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            // should always have a column index but check just in case
            // see defect 23459 which was caused by a problem with our driver. once the driver problem is fixed
            // in theory wouldn't have to check to make sure columns existed
            if ((theElement instanceof Object[]) && (((Object[])theElement).length != 0)) {
                return getText(((Object[])theElement)[theColumnIndex]);
            }

            return super.getText(theElement);
        }
    }

    private class PreviewContentProvider implements IPreviewDataContentProvider {

        private EObject filteredObject;

        /**
         * @param nextPreviewedObject the object being previewed next
         */
        PreviewContentProvider( EObject filteredObject ) {
            this.filteredObject = filteredObject;
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.modeler.internal.dqp.ui.views.IPreviewDataContentProvider#clearAllResults()
         */
        public void clearAllResults() {
            if (this.filteredObject == null) {
                closeAllResults();
            } else {
                closeResults(getElements(null));
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            PreviewResultsModel[] elements = accessModelObjects();

            if (this.filteredObject != null) {
                List<PreviewResultsModel> temp = new ArrayList<PreviewResultsModel>(Arrays.asList(elements));

                for (PreviewResultsModel model : temp) {
                    if (getName(this.filteredObject).equals(model.name) && getPath(this.filteredObject).equals(model.path)) {
                        temp.remove(model);
                        break;
                    }
                }

                elements = temp.toArray(new PreviewResultsModel[temp.size()]);
            }

            return elements;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.modeler.internal.dqp.ui.views.IPreviewDataContentProvider#removeResults(java.lang.Object[])
         */
        public void removeResults( Object[] results ) {
            closeResults(results);
        }
    }
}
