package net.sourceforge.sqlexplorer.dbviewer.details;

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

import java.sql.ResultSet;
import java.sql.Statement;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.actions.CopyTreeAction;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class IndexDetail implements IDetailLogDisplay {

    TreeViewer viewer;
    TableNode node;
    boolean activated = false;
    IDetailLogDisplay detailLog;
    String errorMessage = ""; //$NON-NLS-1$

    public void setMessage( String s ) {
        detailLog.setMessage(s);
        errorMessage = s;
    }

    public IndexDetail( TabItem itemTab,
                        Composite parent,
                        IDetailLogDisplay detailLog ) {
        this.detailLog = detailLog;
        viewer = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
        itemTab.setControl(viewer.getControl());
        Tree tree = viewer.getTree();
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        MenuManager menuMgr = new MenuManager("#TableMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(false);
        menuMgr.add(new CopyTreeAction(tree));
        Menu fDbContextMenu = menuMgr.createContextMenu(tree);
        tree.setMenu(fDbContextMenu);
        TreeColumn tc = new TreeColumn(tree, SWT.NULL);
        tc.setText(Messages.getString("Index_Name_2")); //$NON-NLS-1$
        tc = new TreeColumn(tree, SWT.NULL);
        tc.setText(Messages.getString("Unique_3")); //$NON-NLS-1$
        tc = new TreeColumn(tree, SWT.NULL);
        tc.setText(Messages.getString("Type_4")); //$NON-NLS-1$
        tc = new TreeColumn(tree, SWT.NULL);
        tc.setText(Messages.getString("Order_5")); //$NON-NLS-1$
        tc = new TreeColumn(tree, SWT.NULL);
        tc.setText(Messages.getString("Asc_or_Desc_6")); //$NON-NLS-1$

        TableLayout tableLayout = new TableLayout();
        for (int i = 0; i < 5; i++)
            tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
        tree.setLayout(tableLayout);
        viewer.setContentProvider(new IndexDetailContentProvider());
    }

    public void setNode( TableNode node ) {
        this.node = node;
        activated = false;
    }

    public void activate() {
        if (!activated) {
            Display display = viewer.getControl().getDisplay();
            BusyIndicator.showWhile(display, new Runnable() {
                public void run() {
                    ResultSet rs = null;
                    try {
                        rs = node.getIndexes();
                    } catch (java.lang.Exception e) {
                        SQLExplorerPlugin.error("Error activating index detail view ", e); //$NON-NLS-1$
                        errorMessage = e.getMessage();
                        detailLog.setMessage(errorMessage);
                    }

                    IndexDetailTableModel idtm = new IndexDetailTableModel(rs, IndexDetail.this);
                    IndexDetailLabelProvider idlp = new IndexDetailLabelProvider(idtm);
                    viewer.setLabelProvider(idlp);
                    viewer.setInput(idtm);
                    viewer.refresh();
                    activated = true;
                    try {
                        Statement st = rs.getStatement();
                        if (st != null) st.close();
                        rs.close();
                    } catch (Throwable e) {
                    }
                }
            });
        } else detailLog.setMessage(errorMessage);
    }
}
