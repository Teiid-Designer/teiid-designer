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

package org.teiid.query.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.symbol.ElementSymbol;


public class AbstractValidationVisitor extends LanguageVisitor {
    
    // Exception handling
    private Exception exception;
    private LanguageObject exceptionObject;
        
    // Validation error handling
    protected ValidatorReport report;
    
    private IQueryMetadataInterface metadata;
    
    protected Command currentCommand;
    protected Stack<LanguageObject> stack = new Stack<LanguageObject>();
    
    /**
     * @param teiidVersion
     */
    public AbstractValidationVisitor(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
        this.report = new ValidatorReport();
    }

    public void setMetadata(IQueryMetadataInterface metadata) {
        this.metadata = metadata;
    }
    
    protected IQueryMetadataInterface getMetadata() {
        return this.metadata;
    } 
    
    /**
     * Reset so visitor can be used on a different language object.  This does 
     * not wipe the report.
     */
    public void reset() {
        this.currentCommand = null;
        this.stack.clear();
    }
    
    // ######################### Store results info #########################

    protected void handleValidationError(String message) {
        this.report.addItem(new ValidatorFailure(message));
    }

    protected void handleValidationError(String message, LanguageObject invalidObj) {
        this.report.addItem(new ValidatorFailure(message, invalidObj));
    }

    protected void handleValidationError(String message, Collection invalidObjs) {
        this.report.addItem(new ValidatorFailure(message, invalidObjs));
    }

    protected void handleException(Exception e) { 
        handleException(e, null);
    }

    protected void handleException(Exception e, LanguageObject obj) { 
        // Store exception information
        this.exceptionObject = obj;
        this.exception = e;
        
        // Abort the validation process
        setAbort(true);
    }

    // ######################### Report results info #########################

    public Exception getException() { 
        return this.exception;
    }
    
    public LanguageObject getExceptionObject() { 
        return this.exceptionObject;
    }
    
    public ValidatorReport getReport() { 
        return this.report;
    }
    
    // ######################### Helper methods for validation #########################
    /**
	 * Check to verify if the query would return XML results.
     * @param query the query to check
	 */
	protected boolean isXMLCommand(Command command) {
		if (command instanceof Query) {
		    return ((Query)command).getIsXML();
        }
        return false;
	}   
	
    protected Collection<ElementSymbol> validateElementsSupport(Collection<ElementSymbol> elements, int supportsFlag) {
	    // Collect any identifiers not supporting flag
	    List<ElementSymbol> dontSupport = null;  
        ElementSymbol symbol = null;              

        try {
	        Iterator<ElementSymbol> elemIter = elements.iterator();
            while(elemIter.hasNext()) {
		    symbol = elemIter.next();
               if(! getMetadata().elementSupports(symbol.getMetadataID(), supportsFlag)) {
                    if(dontSupport == null) { 
                        dontSupport = new ArrayList<ElementSymbol>();
                    } 
                    dontSupport.add(symbol);    
                }            
		    }
        } catch(Exception e) {
            handleException(e, symbol);
        } 

        return dontSupport;
    }

}

