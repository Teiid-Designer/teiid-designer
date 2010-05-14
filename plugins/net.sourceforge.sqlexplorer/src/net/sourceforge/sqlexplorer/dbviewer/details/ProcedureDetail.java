/*
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
package net.sourceforge.sqlexplorer.dbviewer.details;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.actions.CopyTableAction;
import net.sourceforge.sqlexplorer.dbviewer.model.ProcedureNode;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @since 4.3
 */
public class ProcedureDetail {

    public static final int NAME_COLUMN = 0;
    public static final int TYPE_COLUMN = 1;
    public static final int DATATYPE_COLUMN = 2;
    public static final int NULLABLE_COLUMN = 3;
    public static final int LENGTH_COLUMN = 4;
    public static final int PRECISION_COLUMN = 5;
    public static final int SCALE_COLUMN = 6;
    public static final int COMMENTS_COLUMN = 7;

    private static final String NAME_COLUMN_TXT = Messages.getString("ProcedureDetail.nameColumn"); //$NON-NLS-1$
    private static final String TYPE_COLUMN_TXT = Messages.getString("ProcedureDetail.typeColumn"); //$NON-NLS-1$
    private static final String DATA_TYPE_COLUMN_TXT = Messages.getString("ProcedureDetail.dataTypeColumn"); //$NON-NLS-1$
    private static final String NULLABLE_COLUMN_TXT = Messages.getString("ProcedureDetail.nullableColumn"); //$NON-NLS-1$
    private static final String LENGTH_COLUMN_TXT = Messages.getString("ProcedureDetail.lengthColumn"); //$NON-NLS-1$
    private static final String SCALE_COLUMN_TXT = Messages.getString("ProcedureDetail.scaleColumn"); //$NON-NLS-1$
    private static final String PRECISION_COLUMN_TXT = Messages.getString("ProcedureDetail.precisionColumn"); //$NON-NLS-1$
    private static final String COMMENTS_COLUMN_TXT = Messages.getString("ProcedureDetail.commentsColumn"); //$NON-NLS-1$

    TableViewer viewer;
    TableNode node;

    // MySQLTableSorter sorter;

    /**
     * @since 4.3
     */
    public ProcedureDetail( final Composite parent,
                            final ResultSetReader rsReader,
                            final ProcedureNode node ) {
        viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
        final Table table = viewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        MenuManager menuMgr = new MenuManager("#ProcedureMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(false);
        menuMgr.add(new CopyTableAction(table));
        Menu fDbContextMenu = menuMgr.createContextMenu(table);
        table.setMenu(fDbContextMenu);

        TableColumn tc = new TableColumn(table, SWT.NULL);
        tc.setText(NAME_COLUMN_TXT);

        tc = new TableColumn(table, SWT.NULL);
        tc.setText(TYPE_COLUMN_TXT);

        tc = new TableColumn(table, SWT.NULL);
        tc.setText(DATA_TYPE_COLUMN_TXT);

        tc = new TableColumn(table, SWT.NULL);
        tc.setText(NULLABLE_COLUMN_TXT);

        tc = new TableColumn(table, SWT.NULL);
        tc.setText(LENGTH_COLUMN_TXT);

        tc = new TableColumn(table, SWT.NULL);
        tc.setText(SCALE_COLUMN_TXT);

        tc = new TableColumn(table, SWT.NULL);
        tc.setText(PRECISION_COLUMN_TXT);

        tc = new TableColumn(table, SWT.NULL);
        tc.setText(COMMENTS_COLUMN_TXT);

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1, 100, true));
        for (int i = 1; i < 8; i++)
            tableLayout.addColumnData(new ColumnWeightData(1, 70, true));
        table.setLayout(tableLayout);

        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        viewer.setContentProvider(new ProcedureDetailContentProvider());

        ProcedureDetailTableModel model = new ProcedureDetailTableModel(rsReader);
        ProcedureDetailLabelProvider provider = new ProcedureDetailLabelProvider(model);
        viewer.setLabelProvider(provider);
        viewer.setInput(model);

        node.setArgumentNameList(model.getArgumentNameList());
    }

}
