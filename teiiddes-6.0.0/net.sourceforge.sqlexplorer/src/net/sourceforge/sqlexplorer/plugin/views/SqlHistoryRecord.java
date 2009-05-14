/*
 * Copyright � 2000-2005 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.plugin.views;

import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;


/** 
 * @since 4.3
 */
public final class SqlHistoryRecord {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Object id;
    
    private SessionTreeNode session;
    
    private String sql;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public SqlHistoryRecord(String theSql,
                            Object theId,
                            SessionTreeNode theSession) throws IllegalArgumentException {
        validateSql(theSql);
        validateId(theId);
        
        this.sql = theSql;
        this.id = theId;
        this.session = theSession;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public String getSql() {
        return this.sql;
    }
    
    public Object getId() {
        return this.id;
    }
    
    /**
     * Indicates if another <code>SqlHistoryRecord</code> has the same SQL and same session information. 
     * @see java.lang.Object#equals(java.lang.Object)
     * @since 4.3
     */
    @Override
    public boolean equals(Object theObject) {
        boolean result = false;
        
        if (theObject != null) {
            if (theObject instanceof SqlHistoryRecord) {
                SqlHistoryRecord other = (SqlHistoryRecord)theObject;
                result = this.sql.equals(other.getSql());
                
                if (result) {
                    SessionTreeNode otherSession = other.getSession();
                    
                    if (this.session == null) {
                        result = (otherSession == null);
                    } else {
                        result = this.session.getAlias().equals(otherSession.getAlias());
                    }
                }
            }
        }

        return result;
    }
    
    /** 
     * @see java.lang.Object#hashCode()
     * @since 4.3
     */
    @Override
    public int hashCode() {
        int result = this.sql.hashCode();
        
        if (this.session != null) {
            result += this.session.hashCode();
        }
        
        return result;
    }
    
    public SessionTreeNode getSession() {
        return this.session;
    }

    private void validateId(Object theId) throws IllegalArgumentException {
        if ((theId == null)
            || ((theId instanceof String) && StringUtilities.isEmpty((String)theId))) {
            throw new IllegalArgumentException("ID can't be null or empty");
        }
    }
    
    private void validateSql(String theSql) throws IllegalArgumentException {
        if (StringUtilities.isEmpty(theSql)) {
            throw new IllegalArgumentException("SQL can't be null or empty");
        }
    }
    
}
