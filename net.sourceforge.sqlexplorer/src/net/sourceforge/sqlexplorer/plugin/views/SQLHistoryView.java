/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.sqlexplorer.plugin.views;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.SqlHistoryChangedListener;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Andrea Mazzolini To change the template for this generated type comment go to Window>Preferences>Java>Code
 *         Generation>Code and Comments
 */
public class SQLHistoryView extends ViewPart implements SqlHistoryChangedListener {
    private static final int ID_COL_INDEX = 0;
    private static final int SQL_COL_INDEX = 1;

    TableViewer tableViewer;

    private ListenerList selectionListeners = new ListenerList(ListenerList.IDENTITY);
    private IAction linkAction; // synchs history selection
    private boolean linkedToHistory = true;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( final Composite parent ) {
        SQLExplorerPlugin.getDefault().addListener(this);
        tableViewer = new TableViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        tableViewer.setLabelProvider(new TableLabelProvider());
        tableViewer.setContentProvider(new IStructuredContentProvider() {

            public Object[] getElements( Object inputElement ) {
                return SQLExplorerPlugin.getDefault().getSQLHistory().toArray();
            }

            public void dispose() {

            }

            public void inputChanged( Viewer viewer,
                                      Object oldInput,
                                      Object newInput ) {

            }
        });
        TableColumn tc = new TableColumn(table, SWT.NULL);
        tc.setText(Messages.getString("SQLHistoryView.idColumnName")); //$NON-NLS-1$

        tc = new TableColumn(table, SWT.NULL);
        tc.setText(Messages.getString("SQLHistoryView.sqlColumnName")); //$NON-NLS-1$

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(3, 10, true));
        tableLayout.addColumnData(new ColumnWeightData(7, 100, true));
        table.setLayout(tableLayout);
        table.layout();
        final MenuManager menuMgr = new MenuManager("#HistoryPopupMenu"); //$NON-NLS-1$
        Menu historyContextMenu = menuMgr.createContextMenu(table);
        menuMgr.add(new Action() {
            @Override
            public String getText() {
                return "Open in SQL Editor";
            }

            @Override
            public void run() {
                try {
                    IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();

                    if (!selection.isEmpty()) {
                        IWorkbenchPage page = SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        if (page == null) return;

                        SqlHistoryRecord record = (SqlHistoryRecord)selection.getFirstElement();
                        SQLEditorInput input = new SQLEditorInput("SQL Editor ("
                                                                  + SQLExplorerPlugin.getDefault().getNextElement() + ").sql");
                        input.setSessionNode(record.getSession());
                        SQLEditor editorPart = (SQLEditor)page.openEditor(input,
                                                                          "net.sourceforge.sqlexplorer.plugin.editors.SQLEditor");
                        editorPart.setText(record.getSql());
                    }
                } catch (Throwable e) {
                    SQLExplorerPlugin.error("Error creating sql editor", e);
                }
            }
        });
        menuMgr.add(new Action() {
            @Override
            public String getText() {
                return "Remove from history";
            }

            @Override
            public void run() {
                try {
                    IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();

                    if (!selection.isEmpty()) {
                        Object[] records = selection.toArray();

                        for (int i = 0; i < records.length; ++i) {
                            SqlHistoryRecord record = (SqlHistoryRecord)records[i];
                            SQLExplorerPlugin.getDefault().removeSQLHistory(record.getSql());
                        }
                    }
                } catch (Throwable e) {
                    SQLExplorerPlugin.error("Error removing item from clipboard", e);
                }
            }
        });
        menuMgr.add(new Action() {
            @Override
            public String getText() {
                return "Clear history records";
            }

            @Override
            public void run() {
                try {
                    List historyRecords = new ArrayList(SQLExplorerPlugin.getDefault().getSQLHistory());

                    for (int size = historyRecords.size(), i = 0; i < size; ++i) {
                        SqlHistoryRecord record = (SqlHistoryRecord)historyRecords.get(i);
                        SQLExplorerPlugin.getDefault().removeSQLHistory(record.getSql());
                    }
                } catch (Throwable theException) {
                    SQLExplorerPlugin.error("Error removing all history records", theException);
                }
            }
        });
        menuMgr.add(new Action() {
            @Override
            public String getText() {
                return "Copy to Clipboard";
            }

            @Override
            public void run() {
                try {
                    IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();

                    if (!selection.isEmpty()) {
                        StringBuffer buf = new StringBuffer();
                        Clipboard cb = new Clipboard(Display.getCurrent());
                        Object[] records = selection.toArray();

                        for (int i = 0; i < records.length; ++i) {
                            if (i != 0) {
                                buf.append('\n');
                            }

                            SqlHistoryRecord record = (SqlHistoryRecord)records[i];
                            buf.append(record.getSql());
                        }

                        cb.setContents(new Object[] {buf.toString()}, new Transfer[] {TextTransfer.getInstance()});
                    }
                } catch (Throwable e) {
                    SQLExplorerPlugin.error("Error copying to clipboard", e);
                }
            }
        });
        table.setMenu(historyContextMenu);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow( IMenuManager manager ) {
                TableItem[] ti = tableViewer.getTable().getSelection();
                MenuItem[] items = menuMgr.getMenu().getItems();
                if (ti == null || ti.length < 1) {
                    for (int i = 0; i < items.length; i++) {
                        items[i].setEnabled(false);
                    }
                } else {
                    for (int i = 0; i < items.length; i++) {
                        items[i].setEnabled(true);
                    }
                }

            }
        });

        // add doubleclick listener that will set focus to the results view showing the selected history record
        this.tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent theEvent ) {
                handleHistoryRecordDoubleClicked((IStructuredSelection)theEvent.getSelection());
            }
        });

        // add selection to be used for linking
        this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleTableRowSelected();
            }
        });

        // create link to history action and add to View Site menu
        this.linkAction = new Action(Messages.getString("SqlHistoryView.linkAction"), IAction.AS_CHECK_BOX) { //$NON-NLS-1$
            @Override
            public void run() {
                handleLinkToResultViews();
            }
        };

        this.linkAction.setImageDescriptor(ImageDescriptor.createFromURL(SqlexplorerImages.getLinkToResultsIcon()));
        this.linkAction.setToolTipText(Messages.getString("SqlHistoryView.linkAction.tip")); //$NON-NLS-1$
        this.linkAction.setChecked(this.linkedToHistory);

        getViewSite().getActionBars().getToolBarManager().add(this.linkAction);

        tableViewer.setInput(this);

        tableViewer.getTable().getDisplay().asyncExec(new Runnable() {
            public void run() {
                packTableColumns();
            }
        });
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        tableViewer.getTable().setFocus();

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        SQLExplorerPlugin.getDefault().removeListener(this);
        super.dispose();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.plugin.SqlHistoryChangedListener#changed()
     */
    public void changed() {
        if (!this.tableViewer.getTable().isDisposed()) {
            final Table table = this.tableViewer.getTable();

            table.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (!table.isDisposed()) {
                        tableViewer.refresh();
                        packTableColumns();
                    }
                }
            });
        }
    }

    /**
     * @see net.sourceforge.sqlexplorer.plugin.SqlHistoryChangedListener#added(java.lang.Object)
     * @since 5.0
     */
    public void added( Object theId ) {
        if (!this.tableViewer.getTable().isDisposed()) {
            final TableViewer viewer = this.tableViewer;

            // find new record
            List historyRecords = SQLExplorerPlugin.getDefault().getSQLHistory();
            Object temp = null;

            for (int size = historyRecords.size(), i = 0; i < size; ++i) {
                SqlHistoryRecord record = (SqlHistoryRecord)historyRecords.get(i);

                if (record.getId().equals(theId)) {
                    temp = record;
                    break;
                }
            }

            final Object newRecord = temp;

            viewer.getControl().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (!viewer.getControl().isDisposed()) {
                        tableViewer.refresh();
                        packTableColumns();

                        if (newRecord != null) {
                            viewer.setSelection(new StructuredSelection(newRecord), true);
                        }
                    }
                }
            });
        }
    }

    /**
     * @see net.sourceforge.sqlexplorer.plugin.SqlHistoryChangedListener#removed(java.lang.Object)
     * @since 5.0
     */
    public void removed( Object theId ) {
        changed();
    }

    void packTableColumns() {
        TableColumn[] columns = tableViewer.getTable().getColumns();

        for (int i = 0; i < columns.length; ++i) {
            columns[i].pack();
        }
    }

    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText( Object theElement,
                                     int theIndex ) {
            String result = null;

            if (theElement instanceof SqlHistoryRecord) {
                switch (theIndex) {
                    case ID_COL_INDEX: {
                        result = ((SqlHistoryRecord)theElement).getId().toString();
                        break;
                    }
                    case SQL_COL_INDEX: {
                        result = ((SqlHistoryRecord)theElement).getSql();
                        break;
                    }
                    default: {
                        result = super.getText(theElement);
                        break;
                    }
                }
            } else {
                result = super.getText(theElement);
            }

            return result;
        }

        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            return null;
        }
    }

    /**
     * Registers the specified listener to receive selection events from the history table. After registration, the new listener
     * will be notified of the current history record selection and double-click.
     * 
     * @param theListener the listener being registered
     */
    public void addSelectionListener( ISqlHistoryViewSelectionListener theListener ) {
        this.selectionListeners.add(theListener);

        if (this.linkedToHistory) {
            theListener.selectionChanged(new SelectionChangedEvent(this.tableViewer, this.tableViewer.getSelection()));
        }
    }

    /**
     * Unregisters the specified listener from receiving selection events from the history table.
     * 
     * @param theListener the listener being unregistered
     */
    public void removeSelectionListener( ISqlHistoryViewSelectionListener theListener ) {
        this.selectionListeners.remove(theListener);
    }

    /**
     * Sets focus to the {@link ISqlHistoryViewSelectionListener}'s {@link IWorkbenchPart} if one exists.
     */
    private boolean activateResultsPart( IStructuredSelection theSelection ) {
        boolean result = false;

        if (!theSelection.isEmpty()) {
            Object[] listeners = this.selectionListeners.getListeners();

            if (listeners.length != 0) {
                Object id = ((SqlHistoryRecord)theSelection.getFirstElement()).getId();

                for (int i = 0; i < listeners.length; ++i) {
                    ISqlHistoryViewSelectionListener l = (ISqlHistoryViewSelectionListener)listeners[i];

                    if (l.isShowingSqlHistoryRecord(id)) {
                        String viewId = l.getWorkbenchPartId();

                        if (viewId != null) {
                            try {
                                getSite().getPage().showView(viewId);
                                result = true;
                            } catch (PartInitException theException) {
                                SQLExplorerPlugin.error(theException.getLocalizedMessage(), theException);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    void handleLinkToResultViews() {
        this.linkedToHistory = this.linkAction.isChecked();

        if (this.linkedToHistory) {
            // pass current selection to all listeners if linking is turned on
            handleTableRowSelected();
        }
    }

    void handleHistoryRecordDoubleClicked( IStructuredSelection theSelection ) {
        if (!theSelection.isEmpty() && !activateResultsPart(theSelection)) {
            // if no results being displayed for this history record tell user
            String msg = MessageFormat.format(Messages.getString("SqlHistoryView.noResultsDialog.detailedMsg"), //$NON-NLS-1$
                                              new Object[] {((SqlHistoryRecord)theSelection.getFirstElement()).getSql()});
            IStatus status = new Status(IStatus.INFO, SQLExplorerPlugin.PLUGIN_ID, IStatus.OK, msg, null);
            ErrorDialog.openError(getSite().getShell(), Messages.getString("SqlHistoryView.noResultsDialog.title"), //$NON-NLS-1$
                                  Messages.getString("SqlHistoryView.noResultsDialog.msg"), //$NON-NLS-1$
                                  status);
        }
    }

    void handleTableRowSelected() {
        if (this.linkedToHistory) {
            IStructuredSelection selection = (IStructuredSelection)this.tableViewer.getSelection();

            // make sure there is a results part showing this result and activate it
            if (activateResultsPart(selection)) {
                // let listeners know the selection in the history table has changed
                Object[] listeners = this.selectionListeners.getListeners();

                if (listeners.length != 0) {
                    SelectionChangedEvent event = new SelectionChangedEvent(this.tableViewer, selection);

                    for (int i = 0; i < listeners.length; ++i) {
                        ((ISelectionChangedListener)listeners[i]).selectionChanged(event);
                    }
                }
            }
        }
    }
}
