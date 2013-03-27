/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IExpression;

/**
 * Utility class for helping condense or find SQL command info
 */
public class CommandHelper {

	/**
	 * Combines the command projected symbols and result set columns into projected symbols
	 * 
	 * @param command
	 * @return
	 */
	public static List<IExpression> getProjectedSymbols(final ICommand command) {
        Set<IExpression> theSymbols = new LinkedHashSet<IExpression>();
        
        theSymbols.addAll(command.getProjectedSymbols());
        theSymbols.addAll(command.getResultSetColumns());
        
        List<IExpression> symbols = new ArrayList(theSymbols.size());
        symbols.addAll(theSymbols);
        
        return symbols;
	}

}
