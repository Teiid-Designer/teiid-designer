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

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sqlpanel.SQLTableSorter;
import net.sourceforge.sqlexplorer.sqlpanel.SqlTableLabelProvider;
import net.sourceforge.sqlexplorer.sqlpanel.SqlTableModel;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mazzolini To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and
 *         Comments
 */
public class SqlResultsView extends ViewPart implements IConstants.Extensions.Views {
    private String[][] ss;
    private int[] colCount;

    CompositeSQLResultsViewer cmp[];
    SqlTableModel mo[];

    SQLTableSorter sorter[];
    Composite parent;
    TabFolder tabFolder;

    private ISqlHistoryViewSelectionListener historyViewListener;
    private IPartListener partListener;

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        this.parent = new Composite(parent, SWT.NONE);
        this.parent.setLayout(new GridLayout());

        // setup for listening to the history view selections
        this.historyViewListener = new ISqlHistoryViewSelectionListener() {
            public String getWorkbenchPartId() {
                return handleGetWorkbenchPartId();
            }

            public boolean isShowingSqlHistoryRecord( Object theId ) {
                return handleIsShowingHistoryRecord(theId);
            }

            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleHistoryRecordSelected(theEvent);
            }
        };

        // setup listening for when the SqlHistoryView is openend/closed in order to add/remove the selection listener
        this.partListener = new IPartListener() {
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

        getSite().getPage().addPartListener(this.partListener);

        // if history view is open register to receive selection events
        SQLHistoryView historyView = getSqlHistoryView();

        if (historyView != null) {
            historyView.addSelectionListener(this.historyViewListener);
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        if ((this.tabFolder != null) && !this.tabFolder.isDisposed()) {
            this.tabFolder.setFocus();
        }
    }

    /**
     * @param reader
     * @param mo
     * @param sorter
     */
    public void setData( SqlTableModel[] new_mo ) throws Exception {
        if (mo != null) {
            for (int i = 0; i < mo.length; i++) {
                mo[i].closeResultSet();
            }

        }
        if (tabFolder == null) {
            tabFolder = new TabFolder(parent, SWT.NULL);
            this.tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        this.mo = new_mo;
        sorter = new SQLTableSorter[mo.length];
        colCount = new int[mo.length];
        ss = new String[mo.length][];
        cmp = new CompositeSQLResultsViewer[mo.length];
        TabItem tabToSelect = null;

        for (int i = 0; i < new_mo.length; i++) {
            sorter[i] = mo[i].sorter;
            colCount[i] = mo[i].ss.length;
            ss[i] = mo[i].ss;
            TabItem ti = null;
            String id = getResultsId(new_mo[i]);

            // see if results for this id are already showing
            for (int size = this.tabFolder.getItemCount(), j = 0; j < size; ++j) {
                TabItem temp = this.tabFolder.getItem(j);

                if (temp.getText().equals(id)) {
                    ti = temp;
                    break;
                }
            }

            // create new TabItem if one wasn't found
            if (ti == null) {
                ti = new TabItem(this.tabFolder, SWT.NULL);
                ti.setText(id);
            }

            // set the data
            cmp[i] = new CompositeSQLResultsViewer(this, tabFolder, SWT.NULL, i, ti);
            ti.setControl(cmp[i]);

            if (tabToSelect == null) {
                tabToSelect = ti;
            }
        }

        // select tab to give it focus
        if (tabToSelect != null) {
            this.tabFolder.setSelection(this.tabFolder.indexOf(tabToSelect));
        }

        refresh();

    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     * @since 4.3
     */
    @Override
    public void dispose() {
        // if history view is open unregister to receive selection events
        SQLHistoryView historyView = getSqlHistoryView();

        if (historyView != null) {
            historyView.removeSelectionListener(this.historyViewListener);
        }

        getSite().getPage().removePartListener(this.partListener);
        super.dispose();
    }

    private TabItem findTab( String theId ) {
        TabItem result = null;

        for (int size = this.tabFolder.getItemCount(), i = 0; i < size; ++i) {
            TabItem temp = this.tabFolder.getItem(i);

            if (temp.getText().equals(theId)) {
                result = temp;
                break;
            }
        }

        return result;
    }

    private SQLHistoryView getSqlHistoryView() {
        SQLHistoryView result = null;
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        if (page != null) {
            result = (SQLHistoryView)page.findView(IConstants.Extensions.Views.SQL_HISTORY_VIEW);
        }

        return result;
    }

    private String getResultsId( SqlTableModel theModel ) {
        return SQLExplorerPlugin.getDefault().getSqlHistoryRecordId(theModel.getSql()).toString();
    }

    String handleGetWorkbenchPartId() {
        return SQL_RESULTS_VIEW;
    }

    void handleHistoryRecordSelected( SelectionChangedEvent theEvent ) {
        if ((this.tabFolder != null) && (this.tabFolder.getSelection().length != 0)) {
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

    boolean handleIsShowingHistoryRecord( Object theId ) {
        boolean result = false;

        if ((this.tabFolder != null) && !this.tabFolder.isDisposed()) {
            result = (findTab(theId.toString()) != null);
        }

        return result;
    }

    void handlePartClosed( IWorkbenchPart thePart ) {
        if (thePart instanceof SQLHistoryView) {
            ((SQLHistoryView)thePart).removeSelectionListener(this.historyViewListener);
        }
    }

    void handlePartOpened( IWorkbenchPart thePart ) {
        if (thePart instanceof SQLHistoryView) {
            ((SQLHistoryView)thePart).addSelectionListener(this.historyViewListener);
        }
    }

    private void refresh() throws Exception {
        for (int jj = 0; jj < mo.length; jj++) {
            final int ii = jj;
            int count = colCount[ii];

            final Image imgAsc = ImageDescriptor.createFromURL(SqlexplorerImages.getAscOrderIcon()).createImage();
            final Image imgDesc = ImageDescriptor.createFromURL(SqlexplorerImages.getDescOrderIcon()).createImage();
            final TableViewer tableViewer = cmp[ii].getTableViewer();
            tableViewer.getControl().addDisposeListener(new DisposeListener() {
                public void widgetDisposed( DisposeEvent e ) {
                    imgAsc.dispose();
                    imgDesc.dispose();
                }
            });

            final Table table = tableViewer.getTable();
            table.removeAll();

            if (mo == null) return;

            SelectionListener headerListener = new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    if (tableViewer.getSorter() == null) tableViewer.setSorter(sorter[ii]);
                    int column = table.indexOf((TableColumn)e.widget);
                    if (column == sorter[ii].getTopPriority()) {
                        int k = sorter[ii].reverseTopPriority();
                        if (k == SQLTableSorter.ASCENDING) ((TableColumn)e.widget).setImage(imgAsc);
                        else ((TableColumn)e.widget).setImage(imgDesc);
                    } else {
                        sorter[ii].setTopPriority(column);
                        ((TableColumn)e.widget).setImage(imgAsc);
                    }
                    TableColumn[] tcArr = table.getColumns();
                    for (int i = 0; i < tcArr.length; i++) {
                        if (i != column) {
                            tcArr[i].setImage(null);
                        }
                    }
                    tableViewer.refresh();
                    cmp[ii].setMessagePanel1("");
                }
            };
            for (int i = 0; i < count; i++) {
                TableColumn tc = new TableColumn(table, SWT.NULL);
                tc.setText(ss[ii][i]);
                tc.addSelectionListener(headerListener);
            }
            tableViewer.setColumnProperties(ss[ii]);
            CellEditor[] cellEditors = new CellEditor[count];
            for (int i = 0; i < cellEditors.length; i++) {
                final int colIndex = i;
                cellEditors[i] = new TextCellEditor(table) {
                    @Override
                    protected void keyReleaseOccured( KeyEvent keyEvent ) {
                        super.keyReleaseOccured(keyEvent);
                        int index = table.getSelectionIndex();
                        int newCol = colIndex;

                        TableItem[] selection = table.getSelection();
                        fireApplyEditorValue();
                        if (selection != null) {
                            table.setSelection(selection);
                        }

                        Object element = tableViewer.getElementAt(index);
                        tableViewer.reveal(element);
                        tableViewer.editElement(element, newCol);
                    }
                };

            }
            tableViewer.setCellEditors(cellEditors);
            tableViewer.setLabelProvider(new SqlTableLabelProvider(mo[ii]));

            tableViewer.setInput(mo[ii]);
            tableViewer.refresh();
            tableViewer.getControl().addDisposeListener(new DisposeListener() {
                public void widgetDisposed( DisposeEvent e ) {
                    mo[ii].closeResultSet();
                }
            });
            cmp[ii].setMessagePanel2(mo[ii].getPartial());
            tableViewer.getTable().addSelectionListener(new SelectionListener() {
                public void widgetDefaultSelected( SelectionEvent e ) {
                }

                public void widgetSelected( SelectionEvent e ) {
                    cmp[ii].setMessagePanel1(Messages.getString("Selected_Row__1") + (tableViewer.getTable().getSelectionIndex() + 1)); //$NON-NLS-1$
                }
            });

            cmp[ii].enableMoreRows(!mo[ii].isFinished());
            for (int i = 0; i < count; i++) {
                table.getColumn(i).pack();
            }
            table.layout();
            parent.layout();
            parent.redraw();
        }
    }

    public SqlTableModel[] getModel() {
        return mo;

    }

    public TableViewer getTableViewer( int ii ) {
        if (cmp == null) return null;
        return cmp[ii].getTableViewer();

    }

    public void enableMoreRows( int ii,
                                boolean b ) {
        cmp[ii].enableMoreRows(b);
    }

    public void setMessagePanel2( int ii,
                                  String string ) {
        if (cmp != null) cmp[ii].setMessagePanel2(string);

    }
}
