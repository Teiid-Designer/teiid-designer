/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import org.teiid.designer.query.sql.lang.ILanguageObject;

/**
 *
 */
public interface ISymbol extends ILanguageObject {

    /**
     * Character used to delimit name components in a symbol
     */
    String SEPARATOR = "."; //$NON-NLS-1$
    
    /**
     * Get the name of the symbol
     * 
     * @return Name of the symbol, never null
     */
    String getName();
    
    /**
     * Get the short name of the element
     * 
     * @return Short name of the symbol (un-dotted)
     */
    String getShortName();
    
    /**
     * Change the symbol's name.  This will change the symbol's hash code
     * and canonical name!!!!!!!!!!!!!!!!!  If this symbol is in a hashed
     * collection, it will be lost!
     * 
     * @param name
     */
    void setShortName(String name);
    
    /**
     * Get the output name
     * 
     * @return output name
     */
    String getOutputName();
    
    /**
     * Set the output name
     * 
     * @param outputName
     */
    void setOutputName(String outputName);
}
