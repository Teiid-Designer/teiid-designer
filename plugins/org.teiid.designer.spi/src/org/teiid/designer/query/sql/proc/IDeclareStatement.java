/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.proc;


/**
 *
 */
public interface IDeclareStatement extends IAssignmentStatement {

    /**
     * Get the type of this variable declared in this statement.
     * 
     * @return A string giving the variable type
     */
    String getVariableType();

}
