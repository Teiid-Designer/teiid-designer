/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;


/**
 * JdbcTable
 */
public interface JdbcTable extends JdbcNode {

    /**
     * Get the remarks for this table.
     * @return the remarks; may be null or empty
     */
    public String getRemarks();
    
}
