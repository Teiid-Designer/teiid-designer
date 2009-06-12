/*
 * Copyright � 2006 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.dbviewer.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

/**
 * @since 5.0.1
 */
public class WebServiceNode extends ProcedureNode {


    private static final String UUID_SQL = "select UID, ModelUID from System.Procedures where FullName = '"; //$NON-NLS-1$
    
    private String objectID;
    private String modelID;
    
    /**
     * @since 5.0.1
     */
    public WebServiceNode(IDbModel s,
                          String name,
                          SQLConnection conn,
                          IProcedureInfo iProcInfo) {
        super(s, name, conn, iProcInfo);
    }
    
    /**
     * @see java.lang.Object#toString()
     * @since 5.0.1
     */
    @Override
    public String toString() {
        String fullname = super.toString();
        return fullname.substring(fullname.lastIndexOf('.') + 1);
    }

    /**
     * Execute a call against the VDB to retrieve it's WSDL. 
     */
    public String getUUID() throws SQLException {
        if ( objectID == null ) {
            setIdValues();
        }
        return objectID;
    }
    
    /**
     * Execute a call against the VDB to retrieve it's WSDL. 
     */
    public String getModelUUID() throws SQLException {
        if ( modelID == null ) {
            setIdValues();
        }
        return modelID;
    }

    public String getFullName() {
        return super.toString();
    }

    private void setIdValues() throws SQLException {

        ResultSet rs = null;
        try {
            if (conn == null) {
                System.out.println("Connection is NULL"); //$NON-NLS-1$
            }
            
            Statement statement = conn.getConnection().createStatement();
            
            boolean rtn = statement.execute(UUID_SQL + super.toString() + '\'');
            
            if (rtn==true) {
                rs = statement.getResultSet();
            } else {
                System.out.println("statement.execute returned false"); //$NON-NLS-1$
            }
            
            if (rs.next()) {
                objectID = rs.getString(1);
                modelID = rs.getString(2);
            } else {
                System.out.println("result set was empty"); //$NON-NLS-1$
            }
                        
        } finally {
            if ( rs != null ) {
                rs.close();
            }
        }
        
    }
    
}
