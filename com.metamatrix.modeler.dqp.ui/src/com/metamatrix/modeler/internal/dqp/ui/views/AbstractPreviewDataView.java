/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.views;

import net.sourceforge.sqlexplorer.SqlexplorerImages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.widget.LabelContributionItem;

/**
 * @since 4.3
 */
public abstract class AbstractPreviewDataView extends ViewPart implements DqpUiConstants {

    static final String PREFIX = I18nUtil.getPropertyPrefix(AbstractResultsView.class);

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

    private LabelContributionItem lblRowCount;

    protected Composite resultsParent;

    /**
     * The action that copies results or the query plan to the clipboard.
     * 
     * @see 4.3
     */
    private IAction saveToFileAction;

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
                handleCloseResults();
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
     * Obtains the action responsible for copying the results to the clipboard.
     * 
     * @return the action or <code>null</code> if this view doesn't support copying results
     * @since 4.3
     */
    protected abstract IAction createCopyResultsAction();

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    public void createPartControl( Composite theParent ) {
        this.resultsParent = createResultsParent(theParent);

        // construct actions to be installed in toolbar
        IActionBars actionBars = getViewSite().getActionBars();
        fillActionBars(actionBars);

        // update to get actions to show
        actionBars.updateActionBars();
    }

    /**
     * @param parent the parent to use when creating the return value
     * @return the parent to all the results panels
     * @since 5.5.3
     */
    protected abstract Composite createResultsParent( Composite parent );

    /**
     * Obtains the action responsible for saving the results to a file.
     * 
     * @since 4.3
     */
    protected abstract IAction createSaveToFileAction();

    /**
     * Populates the <code>IActionBars</code> with contributions.
     * 
     * @param actionBars the action bars being contributed to
     * @since 5.5.3
     */
    protected void fillActionBars( IActionBars actionBars ) {
        IToolBarManager toolBarMgr = actionBars.getToolBarManager();

        // row count label
        this.lblRowCount = new LabelContributionItem();
        toolBarMgr.add(this.lblRowCount);

        // add show query plan action
        contributeShowResultActions(actionBars);

        // add close action
        contributeCloseAction(actionBars);

        // add copy action
        this.copyAction = new ResultsCopyAction(createCopyResultsAction());
        toolBarMgr.add(this.copyAction);

        // add save to file action
        this.saveToFileAction = createSaveToFileAction();
        toolBarMgr.add(this.saveToFileAction);

        toolBarMgr.update(true);
    }

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

        if (!isResultsParentControlDisposed()) {
            ResultsPanel pnl = getSelectedResultsPanel();

            if (pnl != null) {
                result = pnl.getResultsDetailControl();
            }
        }

        return result;
    }

    /**
     * Obtains the selected <code>ResultsPanel</code>.
     * 
     * @return the ResultsPanel or <code>null</code> if none selected
     * @since 5.5.3
     */
    protected abstract ResultsPanel getSelectedResultsPanel();

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
     * Closes the currently selected results panel.
     * 
     * @since 5.5.3
     */
    protected abstract void handleCloseResults();

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

    protected boolean isResultsParentControlDisposed() {
        return ((this.resultsParent == null) || this.resultsParent.isDisposed());
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     * @since 4.3
     */
    @Override
    public void setFocus() {
        if (!isResultsParentControlDisposed()) {
            this.resultsParent.setFocus();
        }
    }

    protected void setRowCountText( String text ) {
        this.lblRowCount.update(text);
    }

    /**
     * Updates the enabled state of all controls contained in this view.
     * 
     * @since 4.3
     */
    protected void updateState() {
        if (!isResultsParentControlDisposed()) {
            ResultsPanel pnlResults = getSelectedResultsPanel();
            boolean hasResults = (pnlResults != null);

            // check to make sure actions exists as subclass may have gotten rid of it
            if (this.closeAction != null) {
                this.closeAction.setEnabled(hasResults);
            }

            // disable actions if no results displayed
            this.copyAction.setEnabled(hasResults);
            this.saveToFileAction.setEnabled(hasResults);

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

        public ResultsCopyAction( IAction theResultsAction ) {
            super("", IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
            this.delegateAction = theResultsAction;
            setImageDescriptor(this.delegateAction.getImageDescriptor());
            setDisabledImageDescriptor(this.delegateAction.getDisabledImageDescriptor());
            setHoverImageDescriptor(this.delegateAction.getHoverImageDescriptor());
            setToolTipText(this.delegateAction.getToolTipText());
            setEnabled(false);
        }

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
