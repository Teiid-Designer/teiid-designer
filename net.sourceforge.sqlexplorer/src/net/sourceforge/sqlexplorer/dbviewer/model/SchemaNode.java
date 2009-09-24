package net.sourceforge.sqlexplorer.dbviewer.model;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.sqlexplorer.ext.PluginManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import org.eclipse.swt.widgets.Composite;

public class SchemaNode implements IDbModel {
    public Composite getComposite( DetailManager detailManager ) {
        return null;
    }

    private IDbModel parent;
    private String txt;
    private ArrayList list = new ArrayList(10);
    private boolean loaded = false;
    private SessionTreeNode sessionNode;
    private PluginManager pm;
    String[] tbTypes = null;

    public SchemaNode( IDbModel s,
                       String name,
                       SessionTreeNode sessionNode,
                       PluginManager pm ) {
        this.sessionNode = sessionNode;
        this.pm = pm;
        parent = s;
        txt = name;

    }

    public Object getParent() {
        return parent;
    }

    public void refresh() {
        list.clear();
        load();
    }

    private void load() {
        try {
            tbTypes = sessionNode.getConnection().getSQLMetaData().getTableTypes();
            for (int i = 0; i < tbTypes.length; ++i) {
                String tableType = tbTypes[i];
                add(new TableObjectTypeNode(this, tableType, sessionNode.getConnection()));
            }
        } catch (Throwable e) {
            SQLExplorerPlugin.error("Error Retrieving schema children ", e); //$NON-NLS-1$

        }
        try {
            IDbModel[] added = pm.getSchemaAddedTypes(this, sessionNode);
            if (added != null) for (int i = 0; i < added.length; ++i) {
                IDbModel added_ = added[i];
                add(added_);// ,this,sessionNode.getConnection(),detailManager);
            }
        } catch (Throwable e) {
            SQLExplorerPlugin.error("Error Retrieving schema children in plugin ", e); //$NON-NLS-1$
        }
    }

    public Object[] getChildren() {
        if (!loaded) {
            load();
            loaded = true;
        }
        return list.toArray();
    }

    private void add( IDbModel m ) {
        // don't add any types that have no children
        if (m.getChildren().length > 0) {
            list.add(m);
        }
    }

    @Override
    public String toString() {
        return txt;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj == this) return true;
        if (!(obj instanceof SchemaNode)) return false;
        SchemaNode sc = (SchemaNode)obj;
        return sc.txt == this.txt;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hash == 0) {
            int result = 17;
            if (txt != null) result = 37 * result + txt.hashCode();
            hash = result;
        }
        return hash;
    }

    private volatile int hash = 0;

    // This method load all the children
    // in a single query for all table types.
    // It's quite faster than having a separate query for each table type.
    // In Oracle there are 3 table types, TABLE, VIEW and SYNONYM. So we run a query instead of three.
    public void fastLoadSchema() {
        if (!loaded) {
            load();
            loaded = true;
        }
        try {
            if (tbTypes == null) tbTypes = sessionNode.getConnection().getSQLMetaData().getTableTypes();
            // They are ordered by TABLE_TYPE, TABLE_SCHEM and TABLE_NAME
            // long start=System.currentTimeMillis();
            ITableInfo[] tables = sessionNode.getConnection().getSQLMetaData().getTables(null, txt, "%", tbTypes);//$NON-NLS-1$
            SystemContainer systemContainer = new SystemContainer(this, txt, sessionNode.getConnection());

            // long end=System.currentTimeMillis();
            HashMap mp = new HashMap();
            if (tables == null) return;
            for (int i = 0; i < tables.length; i++) {
                String type = tables[i].getType();
                ArrayList listTables = (ArrayList)mp.get(type);
                if (listTables == null) {
                    listTables = new ArrayList(100);
                    mp.put(type, listTables);
                }
                // It's wrong to set a null parent; this is fixed inside setTables method. It's not a good solution.
                TableNode node = new TableNode(null, tables[i].getSimpleName(), sessionNode.getConnection(), tables[i]);
                if (!systemContainer.isSystemObject(node)) {
                    listTables.add(node);
                }
            }
            for (int i = 0, sz = list.size(); i < sz; i++) {
                IDbModel dbModel = (IDbModel)list.get(i);
                if (dbModel instanceof TableObjectTypeNode) {
                    TableObjectTypeNode tp = (TableObjectTypeNode)dbModel;
                    ArrayList ls = (ArrayList)mp.get(tp.toString());
                    if (ls != null) {
                        tp.setTables(ls);
                    } else tp.setTables(new ArrayList(0));

                }
                // else
                // System.out.println(">>"+ list.get(i).getClass());
            }

            ProcedureContainer pc = new ProcedureContainer(this, txt, sessionNode.getConnection(), systemContainer);
            WebServiceContainer wc = new WebServiceContainer(this, txt, sessionNode.getConnection());
            pc.setWebServiceContainer(wc);
            add(pc);
            add(wc);
            add(systemContainer);

        } catch (SQLException e) {
            SQLExplorerPlugin.error("Error Retrieving schema children in plugin ", e); //$NON-NLS-1$
        }

    }

    /**
     * @return
     */
    public SessionTreeNode getSessionNode() {
        return sessionNode;
    }

}
