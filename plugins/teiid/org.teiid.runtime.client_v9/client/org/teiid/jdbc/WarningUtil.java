/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.jdbc;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;
import org.teiid.client.SourceWarning;
import org.teiid.runtime.client.Messages;



/**
 * Utilities for creating SQLWarnings.  
 */
class WarningUtil {

    private WarningUtil() {
    }
    
    /**
     * Used to wrap warnings/exceptions into SQLWarning.
     * The chain of warnings is translated into a chain of SQLWarnings.
     * @param reason String object which is the description of the warning.
     * @param ex Throwable object which needs to be wrapped.
     */
    static SQLWarning createWarning(Throwable ex) {
    	String sourceName = null;
    	String modelName = null;
        if(ex instanceof SourceWarning) {
        	SourceWarning exception = (SourceWarning)ex;
        	if (exception.isPartialResultsError()) {
        		PartialResultsWarning warning = new PartialResultsWarning(Messages.getString(Messages.JDBC.WarningUtil_Failures_occurred));
        		warning.addConnectorFailure(exception.getConnectorBindingName(), new SQLException(exception));
        		return warning;
        	}
        	ex = exception.getCause();
        	sourceName = exception.getConnectorBindingName();
        	modelName = exception.getModelName();
        }
        return new SQLWarning(sourceName, modelName, ex);
    }

    /**
     * Convert a List of warnings from the server into a single SQLWarning chain.
     * @param exceptions List of exceptions from server
     * @return Chain of SQLWarning corresponding to list of exceptions
     */
    static SQLWarning convertWarnings(List<Throwable> exceptions) {
        if(exceptions == null || exceptions.size() == 0) {
            return null;    
        }

        SQLWarning root = createWarning(exceptions.get(0));
        SQLWarning current = root;
        for (int i = 1; i < exceptions.size(); i++) {
            SQLWarning newWarning = createWarning(exceptions.get(i)); 
            current.setNextWarning(newWarning);
            current = newWarning;
        }
        return root; 
    }
}
