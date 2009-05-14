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

package net.sourceforge.sqlexplorer.dbviewer.model;

import java.util.ArrayList;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.utility.Dictionary;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.swt.widgets.Composite;

public class TableObjectTypeNode implements IDbModel {

    public Composite getComposite( DetailManager detailManager ) {
        return null;
    }

    private IDbModel parent;
    private String txt;
    private ArrayList list = new ArrayList(10);
    private SQLConnection conn;
    private boolean loaded = false;

    public TableObjectTypeNode( IDbModel s,
                                String name,
                                SQLConnection conn ) {
        parent = s;
        txt = name;
        this.conn = conn;
    }

    public void refresh() {
        list.clear();
        load();
    }

    private void load() {
        try {
            String catName = null, schemaName = null;
            Dictionary dict = null;
            if (parent instanceof CatalogNode) {
                dict = ((CatalogNode)parent).getSessionNode().getDictionary();
                catName = parent.toString();
                if (catName.equals(Messages.getString("NoCatalog_2"))) { //$NON-NLS-1$
                    catName = null;
                }
            } else if (parent instanceof SchemaNode) {
                dict = ((SchemaNode)parent).getSessionNode().getDictionary();
                schemaName = parent.toString();
            }

            ITableInfo[] tables = conn.getSQLMetaData().getTables(catName, schemaName, "%", new String[] {txt}); //$NON-NLS-1$

            for (int i = 0; i < tables.length; i++) {
                list.add(new TableNode(this, tables[i].getSimpleName(), conn, tables[i]));
            }
            if (dict != null) {
                loadDict(dict);
            }
        } catch (Throwable e) {
            SQLExplorerPlugin.error("Error getting table information ", e); //$NON-NLS-1$
            list.clear();
        }
    }

    private void loadDict( Dictionary dict ) {
        for (int i = 0; i < list.size(); i++) {
            IDbModel nd = (IDbModel)list.get(i);
            if (!(nd instanceof TableNode)) continue;
            String name = nd.toString();
            ArrayList listDict = null;
            listDict = (ArrayList)dict.getByTableName(name);
            if (listDict == null) {
                listDict = new ArrayList(2);
                dict.putTableName(name, listDict);
            }
            listDict.add(nd);
        }
    }

    public Object[] getChildren() {
        if (!loaded) {
            load();
            loaded = true;
        }
        return list.toArray();

    }

    public Object getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return txt;
    }

    /**
     * Set the list of table nodes
     */
    public void setTables( ArrayList tables ) {
        this.list = tables;
        loaded = true;
        for (int i = 0; i < list.size(); i++) {
            TableNode tn = (TableNode)list.get(i);
            tn.setParent(this);
        }
        Dictionary dict = null;
        if (parent instanceof CatalogNode) {
            dict = ((CatalogNode)parent).getSessionNode().getDictionary();
        } else if (parent instanceof SchemaNode) {
            dict = ((SchemaNode)parent).getSessionNode().getDictionary();
        }
        if (dict != null) {
            loadDict(dict);
        }
    }
}
