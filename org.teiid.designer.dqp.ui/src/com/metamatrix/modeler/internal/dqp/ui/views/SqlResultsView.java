/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.views;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.dqp.ui.actions.CopySqlResultsToClipboardAction;
import com.metamatrix.modeler.internal.dqp.ui.actions.SaveSqlResultsToFileAction;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.SqlResultsModel;
import com.metamatrix.ui.internal.widget.LabelContributionItem;

/**
 * @since 4.3
 */
public class SqlResultsView extends AbstractResultsView implements IResultsProvider {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(SqlResultsView.class);

    private IAction copyAction;

    private IAction saveToFileAction;

    private LabelContributionItem lblRowCount;

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#createResultsControl(org.eclipse.swt.widgets.Control,
     *      com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults)
     * @since 4.3
     */
    @Override
    protected Control createResultsControl( Composite theParent,
                                            IResults theResults ) {
        assert (theResults instanceof SqlResultsModel);

        SqlResultsViewer viewer = new SqlResultsViewer(theParent, (SqlResultsModel)theResults);
        viewer.setInput(theResults);

        // save results object to be used later
        Table table = viewer.getTable();
        table.setData(theResults);
        table.setMenu(createMenu(table));

        // pack table columns
        for (int size = table.getColumnCount(), i = 0; i < size; ++i) {
            table.getColumn(i).pack();
        }

        return table;
    }

    /**
     * Creates a context menu with the copy action.
     * 
     * @param theControl the control whose context menu is being created
     * @return the menu
     * @since 5.0
     */
    private Menu createMenu( Control theControl ) {
        MenuManager mgr = new MenuManager();
        mgr.add(this.copyAction);
        mgr.add(this.saveToFileAction);
        Menu menu = mgr.createContextMenu(theControl);

        return menu;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#fillActionBars(org.eclipse.ui.IActionBars)
     * @since 4.3
     */
    @Override
    protected void fillActionBars( IActionBars theActionBars ) {
        IToolBarManager toolBarMgr = theActionBars.getToolBarManager();

        //
        // row count label
        //

        this.lblRowCount = new LabelContributionItem();
        toolBarMgr.add(this.lblRowCount);
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#getCopyResultsAction()
     * @since 4.3
     */
    @Override
    protected IAction getCopyResultsAction() {
        if (this.copyAction == null) {
            this.copyAction = new CopySqlResultsToClipboardAction(this, this);
        }

        return this.copyAction;
    }

    @Override
    protected IAction getSaveToFileAction() {
        if (this.saveToFileAction == null) {
            this.saveToFileAction = new SaveSqlResultsToFileAction(this);
        }

        return saveToFileAction;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#getLocalizationKeyPrefix()
     * @since 4.3
     */
    @Override
    protected String getLocalizationKeyPrefix() {
        return PREFIX;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider#getResults()
     * @since 5.5
     */
    public IResults getResults() {
        Control c = getSelectedResultsControl();

        if (c != null) {
            Table table = (Table)c;
            return (IResults)table.getData();
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#handleGetResultsViewPartId()
     * @since 4.3
     */
    @Override
    protected String handleGetResultsViewPartId() {
        return Extensions.SQL_RESULTS_VIEW;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#handleResultSelected(org.eclipse.swt.widgets.Control)
     * @since 4.3
     */
    @Override
    protected void handleResultSelected( Control theControl ) {
        updateState();
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#updateState()
     * @since 4.3
     */
    @Override
    protected void updateState() {
        super.updateState();

        boolean enable = false;
        Control c = getSelectedResultsControl();

        if ((c != null) && (c.getData() != null) && (c.getData() instanceof SqlResultsModel)) {
            enable = true;
        }

        // update row count label
        String msg = ""; //$NON-NLS-1$

        if (enable) {
            SqlResultsModel model = (SqlResultsModel)c.getData();

            if (model.isUpdateModel()) {
                if (model.getTotalRowCount() == 1) {
                    msg = UTIL.getStringOrKey(PREFIX + "oneRowUpdated"); //$NON-NLS-1$
                } else {
                    msg = UTIL.getString(PREFIX + "updateCount", Integer.toString(model.getTotalRowCount())); //$NON-NLS-1$
                }
            } else {
                Object[] params = new Object[] {Integer.toString(((Table)c).getItemCount()),
                    Integer.toString(model.getTotalRowCount())};
                msg = UTIL.getString(PREFIX + "recordCount", params); //$NON-NLS-1$
            }
            if (model.getStatus().getSeverity() == IStatus.ERROR) {
                msg = UTIL.getString(PREFIX + "resultsError") + " - " + msg; //$NON-NLS-1$  //$NON-NLS-2$
            }

            // don't enable action if nothing to copy
            if (model.getTotalRowCount() == 0) {
                enable = false;
            }
        }

        this.saveToFileAction.setEnabled(true);

        // update copy action state
        this.copyAction.setEnabled(enable);

        // update row count message
        this.lblRowCount.update(msg);
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

    /** The label provider for the table. */
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
}
