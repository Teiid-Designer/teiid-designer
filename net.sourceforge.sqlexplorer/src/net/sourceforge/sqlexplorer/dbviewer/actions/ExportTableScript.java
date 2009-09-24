/*
 * Copyright (C) 2003 Andrea Mazzolini
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
package net.sourceforge.sqlexplorer.dbviewer.actions;

import java.sql.ResultSet;
import java.util.ArrayList;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

public class ExportTableScript extends Action {
    /**
     * @param node
     */
    SessionTreeNode node;
    TableNode tableNode;

    public ExportTableScript( SessionTreeNode node,
                              TableNode tableNode ) {

        this.node = node;
        this.tableNode = tableNode;
    }

    private ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getTableIcon());

    @Override
    public String getText() {
        return Messages.getString("Create_Table_Script_1"); //$NON-NLS-1$
    }

    @Override
    public void run() {
        ResultSet rs;
        StringBuffer buf = new StringBuffer(4 * 1024);
        String sep = System.getProperty("line.separator"); //$NON-NLS-1$
        try {
            ArrayList pks = new ArrayList();
            ResultSet rsPks = tableNode.getPK();
            // String sPkName = "";
            while (rsPks.next()) {
                // sPkName = rsPks.getString(6);
                String name = rsPks.getString(4);
                // int pos=rsPks.getInt(5) - 1; //sequence number within primary key
                pks.add(name);
            }
            rsPks.close();

            rs = tableNode.getColumns();
            String tableName = tableNode.getTableInfo().getSimpleName();
            buf.append("create table "); //$NON-NLS-1$
            buf.append(tableName);
            buf.append("("); //$NON-NLS-1$

            while (rs.next()) {
                String columnName = rs.getString(4);
                String typeName = rs.getString(6);
                String columnSize = rs.getString(7);
                String decimalDigits = rs.getString(9);
                String defaultValue = rs.getString(13);
                boolean notNull = "NO".equalsIgnoreCase(rs.getString(18)); //$NON-NLS-1$
                String sLower = typeName.toLowerCase();
                buf.append(sep);
                buf.append(columnName + " "); //$NON-NLS-1$

                buf.append(typeName);

                boolean bNumeric = false;
                if (sLower.equals("numeric") || sLower.equals("number") || sLower.equals("decimal")) //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
                bNumeric = true;

                if (sLower.indexOf("char") != -1 || sLower.indexOf("int") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
                    buf.append("("); //$NON-NLS-1$
                    buf.append(columnSize);
                    buf.append(")"); //$NON-NLS-1$
                } else if (bNumeric) {
                    buf.append("("); //$NON-NLS-1$
                    buf.append(columnSize);
                    String tmp = decimalDigits;
                    if (tmp != null && !"".equals(tmp) && !"0".equals(tmp)) //$NON-NLS-1$ //$NON-NLS-2$
                    {
                        buf.append(","); //$NON-NLS-1$
                        buf.append(tmp);
                    }
                    buf.append(")"); //$NON-NLS-1$
                }
                if (pks.size() == 1 && pks.get(0).equals(columnName)) {
                    buf.append(" PRIMARY KEY"); //$NON-NLS-1$
                }

                if (defaultValue != null && !defaultValue.equals("")) { //$NON-NLS-1$
                    buf.append(" default "); //$NON-NLS-1$
                    if (!bNumeric) buf.append("'"); //$NON-NLS-1$
                    buf.append(defaultValue);
                    if (!bNumeric) buf.append("'"); //$NON-NLS-1$

                }

                if (notNull) {
                    buf.append(" not null"); //$NON-NLS-1$
                }
                buf.append(","); //$NON-NLS-1$
            }
            buf.deleteCharAt(buf.length() - 1);
            rs.close();
            buf.append(")" + sep); //$NON-NLS-1$

            SQLEditorInput input = new SQLEditorInput("SQL Editor (" + SQLExplorerPlugin.getDefault().getNextElement() + ").sql");//$NON-NLS-1$ //$NON-NLS-2$
            input.setSessionNode(node);
            IWorkbenchPage page = SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

            SQLEditor editorPart = (SQLEditor)page.openEditor(input, "net.sourceforge.sqlexplorer.plugin.editors.SQLEditor"); //$NON-NLS-1$
            editorPart.setText(buf.toString());
        } catch (Throwable e) {
            SQLExplorerPlugin.error("Error creating export script", e); //$NON-NLS-1$
        }
    }

    @Override
    public ImageDescriptor getHoverImageDescriptor() {
        return img;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return img;
    }
}
