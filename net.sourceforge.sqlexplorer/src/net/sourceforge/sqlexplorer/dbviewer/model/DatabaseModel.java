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

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.sqlexplorer.ext.PluginManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import org.eclipse.swt.widgets.Composite;

public class DatabaseModel implements IDbModel {
    // private SQLConnection conn;

    public Composite getComposite( DetailManager detailManager ) {
        return null;
    }

    private DatabaseNode root;
    private List auxiliaryRoot = new ArrayList(5);
    boolean supportsCatalogs = false;
    boolean supportsSchemas = false;

    public boolean supportsCatalogs() {
        return supportsCatalogs;
    }

    public boolean supportsSchemas() {
        return supportsSchemas;
    }

    public String[] getCatalogNames() {
        ArrayList ls = new ArrayList();
        for (int i = 0; i < root.children.size(); i++) {
            if (root.children.get(i) instanceof CatalogNode) ls.add(((CatalogNode)root.children.get(i)).toString());
        }
        return (String[])ls.toArray(new String[] {});
    }

    public List getCatalogs() {
        ArrayList ls = new ArrayList();
        for (int i = 0; i < root.children.size(); i++) {
            if (root.children.get(i) instanceof CatalogNode) ls.add(root.children.get(i));
        }
        return ls;
    }

    public List getSchemas() {
        ArrayList ls = new ArrayList();
        for (int i = 0; i < root.children.size(); i++) {
            if (root.children.get(i) instanceof SchemaNode) ls.add(root.children.get(i));
        }
        return ls;
    }

    public DatabaseModel( SessionTreeNode sessionNode,
                          PluginManager pm ) {
        root = new DatabaseNode(Messages.getString("Database_1")); //$NON-NLS-1$

        if (sessionNode == null) return;
        SQLDatabaseMetaData md = sessionNode.getConnection().getSQLMetaData();
        try {
            // supportsCatalogs = md.supportsCatalogsInTableDefinitions();
            supportsCatalogs = md.supportsCatalogs();
        } catch (Exception ex) {
        }

        supportsSchemas = false;
        try {
            // supportsSchemas = md.supportsSchemasInTableDefinitions();
            supportsSchemas = md.supportsSchemas();
            if (supportsCatalogs) {
                final String[] catalogs = md.getCatalogs();
                for (int i = 0; i < catalogs.length; ++i) {
                    final String catalogName = catalogs[i];
                    root.add(new CatalogNode(root, catalogName, sessionNode, pm));
                }
            } else if (supportsSchemas) {
                final String[] schemas = md.getSchemas();
                for (int i = 0; i < schemas.length; ++i) {
                    final String schemaName = schemas[i];
                    root.add(new SchemaNode(root, schemaName, sessionNode, pm));
                }
            } else {
                root.add(new CatalogNode(root, Messages.getString("NoCatalog_2"), sessionNode, pm)); //$NON-NLS-1$
            }
            try {
                IDbModel[] added = sessionNode.getPluginManager().getDbRootAddedTypes(root, sessionNode);

                if (added != null) for (int i = 0; i < added.length; ++i) {
                    IDbModel added_ = added[i];
                    auxiliaryRoot.add(added_);
                }
            } catch (Throwable e) {
                SQLExplorerPlugin.error("Error Retrieving added root nodes in plugin ", e); //$NON-NLS-1$
            }
        } catch (Throwable ex) {
            SQLExplorerPlugin.error("error creating database model ", ex); //$NON-NLS-1$
        }

    }

    public Object[] getChildren() {
        if (auxiliaryRoot.size() == 0) return new Object[] {root};
        List ls = new ArrayList();
        ls.addAll(auxiliaryRoot);
        ls.add(0, root);
        return ls.toArray();
    }

    public Object getParent() {
        return null;
    }

}
