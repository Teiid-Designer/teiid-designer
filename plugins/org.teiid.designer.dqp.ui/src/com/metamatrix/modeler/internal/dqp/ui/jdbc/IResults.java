/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.jdbc;

import java.sql.Statement;
import org.eclipse.core.runtime.IStatus;


/** 
 * @since 4.3
 */
public interface IResults {
    
    /**
     * Obtains the SQL that was executed to produce these results. 
     * @return the SQL
     * @since 4.3
     */
    String getSql();
    
    /**
     * Obtains the <code>Statement</code> that was used to produce these results. 
     * @return the statement
     * @since 4.3
     */
    Statement getStatement();
    
    /**
     * Obtains the current status of the results.
     * @return the status
     * @since 4.3
     */
    IStatus getStatus();

}
