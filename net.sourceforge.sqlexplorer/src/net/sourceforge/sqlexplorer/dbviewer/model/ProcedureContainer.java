/*
 * Copyright � 2000-2005 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.dbviewer.model;

import java.sql.SQLException;
import java.util.ArrayList;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import org.eclipse.swt.widgets.Composite;


/** 
 * @since 4.3
 */
public class ProcedureContainer implements IDbModel {

    private static final String TEXT = Messages.getString("ProcedureContainer.label"); //$NON-NLS-1$
    
    private IDbModel parent;
    private ArrayList children;
    private String schemaPattern;
    private SQLConnection conn;
    private SystemContainer systemContainer;
    
    /** 
     * @since 4.3
     */
    public ProcedureContainer(IDbModel s,String schemaPattern, SQLConnection conn, SystemContainer systemContainer) {
        this.parent = s;
        this.schemaPattern = schemaPattern;
        this.conn = conn;
        this.systemContainer = systemContainer;
    }
    
    public void setWebServiceContainer(WebServiceContainer wsc) {
        try {
            IProcedureInfo[] procArray = conn.getSQLMetaData().getProcedures(null, schemaPattern, "%"); //$NON-NLS-1$
            if ( procArray != null ) {
                children = new ArrayList(procArray.length);
                for ( int i=0 ; i<procArray.length ; ++i ) {
                    if ( WebServiceContainer.isWebServiceProcedure(conn, procArray[i]) ) {
                        wsc.addChild(new WebServiceNode(parent, procArray[i].getSimpleName(), conn, procArray[i]));
                    } else {
                        ProcedureNode node = new ProcedureNode(parent, procArray[i].getSimpleName(), conn, procArray[i]);
                        if ( ! systemContainer.isSystemObject(node) ) {
                            children.add(node);
                        }
                    }
                }
            } else {
                children = new ArrayList(0);
            }
        } catch (SQLException e) {
            SQLExplorerPlugin.error("Error Retrieving schema children in plugin ",e); //$NON-NLS-1$
            children = new ArrayList(0);
        } 
    }

    /** 
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getChildren()
     * @since 4.3
     */
    public Object[] getChildren() {
        return children.toArray();
    }

    /** 
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getParent()
     * @since 4.3
     */
    public Object getParent() {
        return parent;
    }

    /** 
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getComposite(net.sourceforge.sqlexplorer.dbviewer.DetailManager)
     * @since 4.3
     */
    public Composite getComposite(DetailManager dm) {
        return null;
    }
    
    @Override
    public String toString() {
        return TEXT;
    }

}
