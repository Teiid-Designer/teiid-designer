/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.views;

import java.sql.Statement;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.SqlHistoryChangedListener;
import net.sourceforge.sqlexplorer.plugin.views.ISqlHistoryViewSelectionListener;
import net.sourceforge.sqlexplorer.plugin.views.SQLHistoryView;
import net.sourceforge.sqlexplorer.plugin.views.SqlHistoryRecord;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.teiid.jdbc.TeiidStatement;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * @since 4.3
 */
public abstract class AbstractResultsView extends ViewPart implements DqpUiConstants {

    static final String PREFIX = I18nUtil.getPropertyPrefix(AbstractResultsView.class);

    protected TabFolder tabFolder;

    /**
     * The action that closes a result.
     * 
     * @see 4.3
     */
    private IAction closeAction;

    /**
     * The action that copies results or the query plan to the clipboard.
     * 
     * @see 4.3
     */
    private ResultsCopyAction copyAction;

    /**
     * The action that copies results or the query plan to the clipboard.
     * 
     * @see 4.3
     */
    private IAction saveToFileAction;

    /**
     * Listener to changes of SQL History records.
     */
    private SqlHistoryChangedListener historyListener;

    /**
     * Listener for selection events in the history view.
     * 
     * @see 4.3
     */
    private ISqlHistoryViewSelectionListener historyViewListener;

    /**
     * Listener for part events pertaining to the history view.
     * 
     * @see 4.3
     */
    private IPartListener partListener;

    /**
     * The action that shows the debuglog.
     * 
     * @since 5.0.2
     */
    private IAction showDebugLogAction;

    /**
     * The action that shows the query plan document.
     * 
     * @see 4.3
     */
    private IAction showQueryPlanDocumentAction;

    /**
     * The action that shows the query plan tree.
     * 
     * @see 4.3
     */
    private IAction showQueryPlanTreeAction;

    /**
     * The action that shows the query results.
     * 
     * @see 4.3
     */
    private IAction showResultsAction;

    /**
     * Adds results to the existing results being displayed.
     * 
     * @param theResults the results being added
     * @since 4.3
     */
    public void addResults( final IResults theResults ) {
        if (!isTabFolderDisposed()) {
            final TabFolder tabFolder = this.tabFolder;
            final IAction action = this.showResultsAction;

            getSite().getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (getSite().getShell().getDisplay().isDisposed()) {
                        return;
                    }

                    // create and setup results
                    final ResultsPanel pnl = new ResultsPanel(AbstractResultsView.this, tabFolder);
                    Control c = createResultsControl(pnl.getResultsDetailParent(), theResults);
                    pnl.setResultsDetailControl(c);

                    // attach results to tab folder
                    TabItem tabItem = getTabItem(theResults);
                    tabItem.setToolTipText(theResults.getSql());
                    tabItem.setControl(pnl);

                    // set the query plan if one exists
                    Statement statement = theResults.getStatement();

                    if (statement instanceof TeiidStatement) {
                        pnl.setQueryPlan(((TeiidStatement)statement).getPlanDescription());
                        pnl.setDebugLog(((TeiidStatement)statement).getDebugLog());
                        action.run();
                    }

                    // select this tab & notify listeners
                    tabFolder.setSelection(tabFolder.indexOf(tabItem));
                    Event e = new Event();
                    e.widget = tabItem;
                    e.type = SWT.Selection;
                    tabFolder.notifyListeners(SWT.Selection, e);
                }
            });
        }
    }

    /**
     * Adds the close action to the toolbar. Subclasses can override if this toolbar button is not needed.
     * 
     * @param theActionBars the <codeIActionBars</code> to add the action
     * @since 4.3
     */
    protected void contributeCloseAction( IActionBars theActionBars ) {
        IToolBarManager toolBarMgr = theActionBars.getToolBarManager();

        this.closeAction = new Action(UTIL.getStringOrKey(PREFIX + "closeAction"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleClose();
            }
        };

        this.closeAction.setImageDescriptor(ImageDescriptor.createFromURL(SqlexplorerImages.getCloseIcon()));
        this.closeAction.setToolTipText(UTIL.getStringOrKey(PREFIX + "closeAction.tip")); //$NON-NLS-1$
        this.closeAction.setEnabled(false);

        toolBarMgr.add(this.closeAction);

        // update the toolbar to get actions to show
        toolBarMgr.update(true);
    }

    /**
     * Add the copy action to the toolbar and get the subclasses action used to copy results.
     * 
     * @param theActionBars the <codeIActionBars</code> to add the action
     * @since 4.3
     */
    private void contributeCopyAction( IActionBars theActionBars ) {
        IToolBarManager toolBarMgr = theActionBars.getToolBarManager();
        IAction action = getCopyResultsAction();

        if (action == null) {
            this.copyAction = new ResultsCopyAction();
        } else {
            this.copyAction = new ResultsCopyAction(action);
        }

        toolBarMgr.add(this.copyAction);

        // update the toolbar to get actions to show
        toolBarMgr.update(true);
    }

    /**
     * Add the save-to-file action to the toolbar.
     * 
     * @param theActionBars the <codeIActionBars</code> to add the action
     * @since 4.3
     */
    private void contributeSaveToFileAction( IActionBars theActionBars ) {
        IToolBarManager toolBarMgr = theActionBars.getToolBarManager();
        IAction action = getSaveToFileAction();

        if (action != null) {
            this.saveToFileAction = action;

            toolBarMgr.add(this.saveToFileAction);

            // update the toolbar to get actions to show
            toolBarMgr.update(true);
        }

    }

    /**
     * Adds the show query plan action to the toolbar. Subclasses can override if this toolbar button is not needed.
     * 
     * @param theActionBars the <codeIActionBars</code> to add the action
     * @since 4.3
     */
    protected void contributeShowResultActions( IActionBars theActionBars ) {
        IToolBarManager mgr = theActionBars.getToolBarManager();

        //
        // add show results action
        //

        this.showResultsAction = new Action(UTIL.getStringOrKey(PREFIX + "showQueryResultsAction"), IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleShowResults();
            }
        };
        this.showResultsAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SHOW_SQL_RESULTS_ICON));
        this.showResultsAction.setDisabledImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SHOW_SQL_RESULTS_DISABLED_ICON));
        this.showResultsAction.setToolTipText(UTIL.getStringOrKey(PREFIX + "showQueryResultsAction.tip")); //$NON-NLS-1$
        this.showResultsAction.setEnabled(false);
        mgr.add(this.showResultsAction);

        //
        // add show query plan tree action
        //

        this.showQueryPlanTreeAction = new Action(
                                                  UTIL.getStringOrKey(PREFIX + "showQueryPlanTreeAction"), IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleShowQueryPlanTree();
            }
        };
        this.showQueryPlanTreeAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SHOW_PLAN_TREE_ICON));
        this.showQueryPlanTreeAction.setDisabledImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SHOW_PLAN_TREE_DISABLED_ICON));
        this.showQueryPlanTreeAction.setToolTipText(UTIL.getStringOrKey(PREFIX + "showQueryPlanTreeAction.tip")); //$NON-NLS-1$
        this.showQueryPlanTreeAction.setEnabled(false);
        mgr.add(this.showQueryPlanTreeAction);

        //
        // add show query plan document action
        //

        this.showQueryPlanDocumentAction = new Action(
                                                      UTIL.getStringOrKey(PREFIX + "showQueryPlanDocumentAction"), IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleShowQueryPlanDocument();
            }
        };
        this.showQueryPlanDocumentAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SHOW_PLAN_DOCUMENT_ICON));
        this.showQueryPlanDocumentAction.setDisabledImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SHOW_PLAN_DOCUMENT_DISABLED_ICON));
        this.showQueryPlanDocumentAction.setToolTipText(UTIL.getStringOrKey(PREFIX + "showQueryPlanDocumentAction.tip")); //$NON-NLS-1$
        this.showQueryPlanDocumentAction.setEnabled(false);
        mgr.add(this.showQueryPlanDocumentAction);

        //
        // add show debug log action
        //

        this.showDebugLogAction = new Action(UTIL.getStringOrKey(PREFIX + "showDebugLogAction"), IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleShowDebugLog();
            }
        };
        this.showDebugLogAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SHOW_DEBUG_LOG_ICON));
        this.showDebugLogAction.setDisabledImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SHOW_DEBUG_LOG_DISABLED_ICON));
        this.showDebugLogAction.setToolTipText(UTIL.getStringOrKey(PREFIX + "showDebugLogAction.tip")); //$NON-NLS-1$
        this.showDebugLogAction.setEnabled(false);
        mgr.add(this.showDebugLogAction);

        // separator
        mgr.add(new Separator());

        mgr.update(true);
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    public void createPartControl( Composite theParent ) {
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);

        this.tabFolder = new TabFolder(pnl, SWT.NULL);
        this.tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleTabSelected();
            }
        });

        // construct actions to be installed in toolbar
        IActionBars actionBars = getViewSite().getActionBars();
        fillActionBars(actionBars);

        // add show query plan action
        contributeShowResultActions(actionBars);

        // add close tab action
        contributeCloseAction(actionBars);

        contributeCopyAction(actionBars);

        contributeSaveToFileAction(actionBars);

        // update to get actions to show
        actionBars.updateActionBars();

        // setup listeners
        this.historyViewListener = createSqlHistoryViewSelectionListener();
        this.partListener = createPartListener();
        this.historyListener = createSqlHistoryChangedListener();

        // if history view is open register to receive selection events
        SQLHistoryView historyView = getSqlHistoryView();

        if (historyView != null) {
            handlePartOpened(historyView);
        }
    }

    /**
     * Creates the <code>IPartListener</code> which listens for events concerning the {@link SQLHistoryView}.
     * 
     * @return the listener
     * @since 4.3
     */
    protected IPartListener createPartListener() {
        IPartListener listener = new IPartListener() {
            public void partActivated( IWorkbenchPart thePart ) {
            }

            public void partBroughtToTop( IWorkbenchPart thePart ) {
            }

            public void partClosed( IWorkbenchPart thePart ) {
                handlePartClosed(thePart);
            }

            public void partDeactivated( IWorkbenchPart thePart ) {
            }

            public void partOpened( IWorkbenchPart thePart ) {
                handlePartOpened(thePart);
            }
        };

        // register part listener now that we have a view site
        getSite().getPage().addPartListener(listener);

        return listener;
    }

    protected SqlHistoryChangedListener createSqlHistoryChangedListener() {
        SqlHistoryChangedListener listener = new SqlHistoryChangedListener() {

            public void changed() {
            }

            public void removed( Object theId ) {
                handleHistoryRecordRemoved(theId);
            }

            public void added( Object theId ) {
            }
        };

        // register to receive history record changes
        SQLExplorerPlugin.getDefault().addListener(listener);

        return listener;
    }

    /**
     * Creates the <code>Control</code> where the results are displayed.
     * 
     * @param theParent the parent control
     * @param theResults the results being added
     * @return the control displaying the results
     * @since 4.3
     */
    protected abstract Control createResultsControl( Composite theParent,
                                                     IResults theResults );

    /**
     * Creates the <code>ISqlHistoryViewSelectionListener</code> which listens for selection events from the
     * {@link SQLHistoryView}.
     * 
     * @return the listener
     * @since 4.3
     */
    protected ISqlHistoryViewSelectionListener createSqlHistoryViewSelectionListener() {
        // setup for listening to the history view selections
        ISqlHistoryViewSelectionListener listener = new ISqlHistoryViewSelectionListener() {
            public String getWorkbenchPartId() {
                return handleGetResultsViewPartId();
            }

            public boolean isShowingSqlHistoryRecord( Object theId ) {
                return handleIsShowingHistoryRecord(theId);
            }

            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleHistoryRecordSelected(theEvent);
            }
        };

        return listener;
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     * @since 4.3
     */
    @Override
    public void dispose() {
        if (this.historyListener != null) {
            SQLExplorerPlugin.getDefault().removeListener(this.historyListener);
        }

        // if history view is open unregister to receive selection events
        SQLHistoryView historyView = getSqlHistoryView();

        if ((historyView != null) && (this.historyViewListener != null)) {
            historyView.removeSelectionListener(this.historyViewListener);
        }

        if (this.partListener != null) {
            getSite().getPage().removePartListener(this.partListener);
        }

        super.dispose();
    }

    /**
     * Allows subclasses to populate the <code>IActionBars</code> with their contributions.
     * 
     * @param theActionBars the action bars being contributed to
     * @since 4.3
     */
    protected abstract void fillActionBars( IActionBars theActionBars );

    /**
     * Obtains the selected tab.
     * 
     * @param theId the id of the tab being searched for
     * @return the selected tab or <code>null</code> if not found
     * @since 4.3
     */
    protected TabItem findTab( String theId ) {
        TabItem result = null;

        if (!isTabFolderDisposed()) {
            for (int size = this.tabFolder.getItemCount(), i = 0; i < size; ++i) {
                TabItem temp = this.tabFolder.getItem(i);

                if (temp.getText().equals(theId)) {
                    result = temp;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Obtains the action responsible for copying the results to the clipboard.
     * 
     * @return the action or <code>null</code> if this view doesn't support copying results
     * @since 4.3
     */
    protected abstract IAction getCopyResultsAction();

    /**
     * Obtains the action responsible for saving the results to a file.
     * 
     * @since 4.3
     */
    protected abstract IAction getSaveToFileAction();

    /**
     * Obtains the properties file key prefix used for obtaining localized text.
     * 
     * @return the prefix (must not be <code>null</code>)
     * @since 4.3
     */
    protected abstract String getLocalizationKeyPrefix();

    /**
     * Obtains the <code>Control</code> containing the results currently being displayed.
     * 
     * @return the control or <code>null</code> if no results are selected
     * @since 4.3
     */
    protected Control getSelectedResultsControl() {
        Control result = null;

        if (!isTabFolderDisposed()) {
            ResultsPanel pnl = getSelectedResultsPanel();

            if (pnl != null) {
                result = pnl.getResultsDetailControl();
            }
        }

        return result;
    }

    /**
     * Obtains the <code>ResultsPanel</code> of the selected tab.
     * 
     * @return the ResultsPanel or <code>null</code> if no tab is selected
     * @since 4.3
     */
    ResultsPanel getSelectedResultsPanel() {
        ResultsPanel result = null;

        if (!isTabFolderDisposed()) {
            TabItem item = getSelectedTabItem();

            if (item != null) {
                result = (ResultsPanel)item.getControl();
            }
        }

        return result;
    }

    /**
     * Obtains the currently selected <code>TabItem</code>.
     * 
     * @return the tab or <code>null</code> if none selected
     * @since 4.3
     */
    private TabItem getSelectedTabItem() {
        TabItem result = null;

        if (!isTabFolderDisposed()) {
            TabItem[] items = this.tabFolder.getSelection();

            if (items.length != 0) {
                result = items[0];
            }
        }

        return result;
    }

    /**
     * Obtains the instance of the {@link SQLHistoryView}.
     * 
     * @return the history view or <code>null</code> if not found
     * @since 4.3
     */
    protected SQLHistoryView getSqlHistoryView() {
        SQLHistoryView result = null;
        IViewPart view = UiUtil.getViewPart(IConstants.Extensions.Views.SQL_HISTORY_VIEW);

        if (view != null) {
            result = (SQLHistoryView)view;
        }

        return result;
    }

    /**
     * Helper method to obtain localized text from the plugin properties file. Uses the subclass key prefix.
     * 
     * @param theKey the properties file key that will be prefixed with the subclasses key prefix
     * @return the localized text
     * @since 4.3
     * @see #getLocalizationKeyPrefix()
     */
    protected String getString( String theKey ) {
        return UTIL.getStringOrKey(getLocalizationKeyPrefix() + theKey);
    }

    /**
     * Obtains and existing or creates a new tab for the specified SQL.
     * 
     * @param results the results whose tab is being requested
     * @return the requested tab
     * @since 4.3
     */
    protected TabItem getTabItem( IResults results ) {
        TabItem result = null;
        String sql = results.getSql();
        Object temp = SQLExplorerPlugin.getDefault().getSqlHistoryRecordId(sql);

        if (temp != null) {
            String id = temp.toString();
            result = findTab(id);

            // create new TabItem if one wasn't found
            if (result == null) {
                result = new TabItem(this.tabFolder, SWT.NULL);
                result.setText(id);
            }
        }

        return result;
    }

    /**
     * Handler for when a result display is closed.
     * 
     * @since 4.3
     */
    protected void handleClose() {
        if (!isTabFolderDisposed()) {
            TabItem[] items = this.tabFolder.getSelection();

            for (int i = 0; i < items.length; ++i) {
                Control c = items[i].getControl();
                items[i].setControl(null);
                items[i].dispose();
                c.dispose();
            }

            updateState();
        }
    }

    /**
     * Closes the results for the history record with the specified ID.
     * 
     * @param theId the history record ID whose results are being closed
     * @since 5.0
     */
    protected void handleHistoryRecordRemoved( Object theId ) {
        if (!isTabFolderDisposed()) {
            TabItem item = findTab(theId.toString());

            if ((item != null) && !item.isDisposed()) {
                Control c = item.getControl();
                item.setControl(null);
                item.dispose();
                c.dispose();
            }

            updateState();
        }
    }

    /**
     * Handler for copying debug log to clipboard.
     * 
     * @since 5.0.2
     */
    void handleCopyDebugLog() {
        ResultsPanel pnl = getSelectedResultsPanel();

        if (pnl != null) {
            pnl.copyDebugLogToClipboard();
        }
    }

    /**
     * Handler for copying query plan document to clipboard.
     * 
     * @since 4.3
     */
    void handleCopyQueryPlanDocument() {
        ResultsPanel pnl = getSelectedResultsPanel();

        if (pnl != null) {
            pnl.copyQueryPlanDocumentToClipboard();
        }
    }

    /**
     * Obtain the part identifier for the results view.
     * 
     * @return the ID
     * @since 4.3
     */
    protected abstract String handleGetResultsViewPartId();

    /**
     * Handler for a selecion in the {@link SQLHistoryView}.
     * 
     * @param theEvent the selection event being processed
     * @since 4.3
     */
    void handleHistoryRecordSelected( SelectionChangedEvent theEvent ) {
        if (this.tabFolder.getSelection().length != 0) {
            ISelection selection = theEvent.getSelection();

            if (!selection.isEmpty()) {
                // know its a structured selection since selection is from table viewer
                Object temp = ((IStructuredSelection)selection).getFirstElement();

                if (temp instanceof SqlHistoryRecord) {
                    String id = ((SqlHistoryRecord)temp).getId().toString();

                    // see if we have a tab open with that id
                    TabItem tab = findTab(id);

                    if (tab != null) {
                        // select that tab
                        this.tabFolder.setSelection(this.tabFolder.indexOf(tab));
                    }
                }
            }
        }
    }

    /**
     * Indicates if a result for the specified ID is currently being shown.
     * 
     * @param theId the ID being checked
     * @return <code>true</code> if result is currently being shown; <code>false</code>.
     * @since 4.3
     */
    boolean handleIsShowingHistoryRecord( Object theId ) {
        boolean result = false;

        if (!isTabFolderDisposed()) {
            result = (findTab(theId.toString()) != null);
        }

        return result;
    }

    /**
     * Handler for when the {@link SQLHistoryView} is closed. Need to remove selection listener.
     * 
     * @param thePart the part being processed
     * @since 4.3
     */
    void handlePartClosed( IWorkbenchPart thePart ) {
        if (!isTabFolderDisposed() && (this.historyViewListener != null) && (thePart instanceof SQLHistoryView)) {
            ((SQLHistoryView)thePart).removeSelectionListener(this.historyViewListener);
        }
    }

    /**
     * Handler for when the {@link SQLHistoryView} is opened. Need to add selection listener.
     * 
     * @param thePart the part being processed
     * @since 4.3
     */
    void handlePartOpened( IWorkbenchPart thePart ) {
        if (!isTabFolderDisposed() && (this.historyViewListener != null) && (thePart instanceof SQLHistoryView)) {
            ((SQLHistoryView)thePart).addSelectionListener(this.historyViewListener);
        }
    }

    /**
     * Handler for when a results is selected by the user.
     * 
     * @param theControl the control displaying the result that was selected
     * @since 4.3
     */
    protected abstract void handleResultSelected( Control theControl );

    /**
     * Handler for when the show debug log action is selected.
     * 
     * @since 5.0.2
     */
    protected void handleShowDebugLog() {
        if (this.showDebugLogAction != null) {
            ResultsPanel pnl = getSelectedResultsPanel();

            if (pnl != null) {
                this.showQueryPlanDocumentAction.setChecked(false);
                this.showQueryPlanTreeAction.setChecked(false);
                this.showResultsAction.setChecked(false);
                pnl.show(ResultsPanel.ID_DEBUG_LOG);

                this.copyAction.setCopyView(ResultsPanel.ID_DEBUG_LOG);
            }
        }
    }

    /**
     * Handler for when the show query plan document action is selected.
     * 
     * @since 4.3
     */
    protected void handleShowQueryPlanDocument() {
        if (this.showQueryPlanDocumentAction != null) {
            ResultsPanel pnl = getSelectedResultsPanel();

            if (pnl != null) {
                this.showQueryPlanTreeAction.setChecked(false);
                this.showResultsAction.setChecked(false);
                this.showDebugLogAction.setChecked(false);
                pnl.show(ResultsPanel.ID_QUERY_PLAN_DOCUMENT_VIEW);

                this.copyAction.setCopyView(ResultsPanel.ID_QUERY_PLAN_DOCUMENT_VIEW);
            }
        }
    }

    /**
     * Handler for when the show query plan tree action is selected.
     * 
     * @since 4.3
     */
    protected void handleShowQueryPlanTree() {
        if (this.showQueryPlanTreeAction != null) {
            ResultsPanel pnl = getSelectedResultsPanel();

            if (pnl != null) {
                this.showQueryPlanDocumentAction.setChecked(false);
                this.showResultsAction.setChecked(false);
                this.showDebugLogAction.setChecked(false);
                pnl.show(ResultsPanel.ID_QUERY_PLAN_TREE_VIEW);

                this.copyAction.setCopyView(ResultsPanel.ID_QUERY_PLAN_TREE_VIEW);
            }
        }
    }

    /**
     * Handler for when the show query plan tree action is selected.
     * 
     * @since 4.3
     */
    protected void handleShowResults() {
        if (this.showResultsAction != null) {
            ResultsPanel pnl = getSelectedResultsPanel();

            if (pnl != null) {
                this.showQueryPlanTreeAction.setChecked(false);
                this.showQueryPlanDocumentAction.setChecked(false);
                this.showDebugLogAction.setChecked(false);
                pnl.show(ResultsPanel.ID_RESULTS_VIEW);

                this.copyAction.setCopyView(ResultsPanel.ID_RESULTS_VIEW);
            }
        }
    }

    /**
     * Handler for when a tab is selected.
     * 
     * @since 4.3
     */
    void handleTabSelected() {
        updateState();
        handleResultSelected(getSelectedResultsControl());
    }

    protected boolean isTabFolderDisposed() {
        return ((this.tabFolder == null) || this.tabFolder.isDisposed());
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     * @since 4.3
     */
    @Override
    public void setFocus() {
        if (!isTabFolderDisposed()) {
            this.tabFolder.setFocus();
        }
    }

    /**
     * Updates the enabled state of all controls contained in this view.
     * 
     * @since 4.3
     */
    protected void updateState() {
        if (!isTabFolderDisposed()) {
            // check to make sure actions exists as subclass may have gotten rid of it
            if (this.closeAction != null) {
                this.closeAction.setEnabled(this.tabFolder.getSelectionIndex() != -1);
            }

            // disable copy if no tabs
            this.copyAction.setEnabled(this.tabFolder.getSelectionIndex() != -1);

            ResultsPanel pnlResults = getSelectedResultsPanel();
            boolean hasResults = (pnlResults != null);
            boolean enable = hasResults;

            if (enable) {
                this.copyAction.setCopyView(pnlResults.getDetailsViewlId());
            }

            if (this.showResultsAction != null) {
                this.showResultsAction.setEnabled(enable);
                this.showResultsAction.setChecked(enable && (pnlResults.getDetailsViewlId() == ResultsPanel.ID_RESULTS_VIEW));
            }

            enable = hasResults && pnlResults.hasQueryPlan();

            if (this.showQueryPlanDocumentAction != null) {
                this.showQueryPlanDocumentAction.setEnabled(enable);
                this.showQueryPlanDocumentAction.setChecked(enable
                                                            && (pnlResults.getDetailsViewlId() == ResultsPanel.ID_QUERY_PLAN_DOCUMENT_VIEW));
            }

            if (this.showQueryPlanTreeAction != null) {
                this.showQueryPlanTreeAction.setEnabled(enable);
                this.showQueryPlanTreeAction.setChecked(enable
                                                        && (pnlResults.getDetailsViewlId() == ResultsPanel.ID_QUERY_PLAN_TREE_VIEW));
            }

            enable = hasResults && pnlResults.hasDebugLog();

            if (this.showDebugLogAction != null) {
                this.showDebugLogAction.setEnabled(enable);
                this.showDebugLogAction.setChecked(enable && (pnlResults.getDetailsViewlId() == ResultsPanel.ID_DEBUG_LOG));
            }
        }
    }

    private class ResultsCopyAction extends Action {

        private IAction delegateAction;

        private int viewId = ResultsPanel.ID_RESULTS_VIEW;

        public ResultsCopyAction() {
            super("", IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
            ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
            setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
            setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
            setEnabled(false);
        }

        public ResultsCopyAction( IAction theResultsAction ) {
            this();
            this.delegateAction = theResultsAction;
            setEnabled(this.delegateAction.isEnabled());
        }

        //
        // METHODS
        //

        @Override
        public void run() {
            if ((this.viewId == ResultsPanel.ID_RESULTS_VIEW) && (this.delegateAction != null)) {
                this.delegateAction.run();
                setEnabled(this.delegateAction.isEnabled());
            } else if (this.viewId == ResultsPanel.ID_QUERY_PLAN_DOCUMENT_VIEW) {
                handleCopyQueryPlanDocument();
            } else if (this.viewId == ResultsPanel.ID_DEBUG_LOG) {
                handleCopyDebugLog();
            }
        }

        /**
         * Set the type of copy to perform.
         * 
         * @param theViewId the ID of the view to be copied
         * @since 4.3
         * @see ResultsPanel#ID_RESULTS_VIEW
         * @see ResultsPanel#ID_QUERY_PLAN_TREE_VIEW
         * @see ResultsPanel#ID_QUERY_PLAN_DOCUMENT_VIEW
         */
        void setCopyView( int theViewId ) {
            this.viewId = theViewId;

            // variables for action text and tooltip
            String txt = ""; //$NON-NLS-1$
            String tip = ""; //$NON-NLS-1$

            // update enabled state of action
            boolean enable = false;

            if (this.viewId == ResultsPanel.ID_RESULTS_VIEW) {
                enable = ((this.delegateAction != null) && this.delegateAction.isEnabled());

                if (this.delegateAction == null) {
                    txt = UTIL.getStringOrKey(PREFIX + "ResultsCopyAction.noDelegateCopyAction"); //$NON-NLS-1$
                    tip = UTIL.getStringOrKey(PREFIX + "ResultsCopyAction.noDelegateCopyAction.tip"); //$NON-NLS-1$
                } else {
                    txt = this.delegateAction.getText();
                    tip = this.delegateAction.getToolTipText();
                }
            } else if (this.viewId == ResultsPanel.ID_QUERY_PLAN_DOCUMENT_VIEW) {
                ResultsPanel pnl = getSelectedResultsPanel();

                if ((pnl != null) && pnl.hasQueryPlan()) {
                    enable = true;
                }

                txt = UTIL.getStringOrKey(PREFIX + "ResultsCopyAction.queryPlanDocumentCopy"); //$NON-NLS-1$
                tip = UTIL.getStringOrKey(PREFIX + "ResultsCopyAction.queryPlanDocumentCopy.tip"); //$NON-NLS-1$
            } else if (this.viewId == ResultsPanel.ID_QUERY_PLAN_TREE_VIEW) {
                txt = UTIL.getStringOrKey(PREFIX + "ResultsCopyAction.queryPlanTreeCopy"); //$NON-NLS-1$
                tip = UTIL.getStringOrKey(PREFIX + "ResultsCopyAction.queryPlanTreeCopy.tip"); //$NON-NLS-1$
            } else if (this.viewId == ResultsPanel.ID_DEBUG_LOG) {
                txt = UTIL.getStringOrKey(PREFIX + "ResultsCopyAction.debugLogCopy"); //$NON-NLS-1$
                tip = UTIL.getStringOrKey(PREFIX + "ResultsCopyAction.debugLogCopy.tip"); //$NON-NLS-1$
                enable = true;
            }

            // set enabled state
            setEnabled(enable);

            // set text and tooltip
            setText(txt);
            setToolTipText(tip);
        }
    }
}
