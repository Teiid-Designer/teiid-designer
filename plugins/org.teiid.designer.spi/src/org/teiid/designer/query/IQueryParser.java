/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query;


import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IExpression;


/**
 *
 */
public interface IQueryParser {

    /**
     * Parse the given criteria string
     * 
     * @param criteriaString
     * 
     * @return  an instance of {@link ICriteria}
     * @throws Exception 
     */
    ICriteria parseCriteria(String criteriaString) throws Exception;

    /**
     * Parse the given command string
     * 
     * @param commandString
     * 
     * @return an instance of {@link ICommand}
     * @throws Exception 
     */
    ICommand parseCommand(String commandString) throws Exception;
    
    /**
     * Parse the given command string
     * 
     * @param commandString
     * 
     * @return an instance of {@link ICommand}
     * @throws Exception 
     */
    ICommand parseDesignerCommand(String commandString) throws Exception;

    /**
     * Parse the given expression string
     * 
     * @param expressionString
     * 
     * @return an instance of {@link IExpression}
     * @throws Exception 
     */
    IExpression parseExpression(String expressionString) throws Exception;

}
