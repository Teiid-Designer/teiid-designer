/*
 * Copyright � 2006 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.dbviewer.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.sqlexplorer.dbviewer.details.ProcedureDetailRow;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.swt.widgets.Composite;

/**
 * @since 5.0.1
 */
public class WebServiceContainer implements IDbModel {

    static boolean isWebServiceProcedure( SQLConnection conn,
                                          IProcedureInfo iProcInfo ) {
        ResultSetReader reader = null;
        try {
            ResultSet rs = conn.getSQLMetaData().getProcedureColumns(iProcInfo);
            reader = new ResultSetReader(rs);
            Object[] row1 = reader.readRow();
            Object[] row2 = reader.readRow();
            Object[] row3 = reader.readRow();

            if (row1 != null && row2 != null && row3 == null) {
                ProcedureDetailRow pdr1 = new ProcedureDetailRow(row1);
                ProcedureDetailRow pdr2 = new ProcedureDetailRow(row2);
                if (pdr1.getDataType().equals("xml") && pdr2.getDataType().equals("xml")) {
                    if (pdr1.isInType() && pdr2.isResultType()) {
                        return true;
                    } else if (pdr2.isInType() && pdr1.isResultType()) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            SQLExplorerPlugin.error("Error Retrieving schema children in plugin ", e); //$NON-NLS-1$
        }

        return false;
    }

    private ArrayList children = new ArrayList();
    private IDbModel parent;

    /**
     * @since 5.0.1
     */
    public WebServiceContainer( IDbModel s,
                                String schemaPattern,
                                SQLConnection conn ) {
        parent = s;
    }

    /**
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getChildren()
     * @since 5.0.1
     */
    public Object[] getChildren() {
        return children.toArray();
    }

    /**
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getComposite(net.sourceforge.sqlexplorer.dbviewer.DetailManager)
     * @since 5.0.1
     */
    public Composite getComposite( DetailManager dm ) {
        return null;
    }

    /**
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getParent()
     * @since 5.0.1
     */
    public Object getParent() {
        return parent;
    }

    /**
     * @param obj
     * @since 5.0.1
     */
    void addChild( Object obj ) {
        this.children.add(obj);
    }

    /**
     * @see java.lang.Object#toString()
     * @since 5.0.1
     */
    @Override
    public String toString() {
        return "Web Service Operation";
    }

}
