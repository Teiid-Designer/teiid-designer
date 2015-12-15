/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.proc;

import org.eclipse.core.runtime.IStatus;

/**
 *
 */
public interface ITeiidColumnInfo {

    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
    public static final String INTEGER_DATATYPE = "integer"; //$NON-NLS-1$
    public static final int DEFAULT_WIDTH = 10;

    /**
     * Get the fully validated column name. This should be used in SQL string
     * generation.
     * 
     * @return name the column name
     */
    String getSymbolName();

    /**
     * Get the column name for display in the UI. This removes any quotes for
     * aesthetic reasons. Use {@link #getSymbolName()} for retrieving the 
     * fully validated column name.
     * 
     * @return the column name sans quotes.
     */
    String getName();

    /**
     * 
     * @return datatype the column datatype
     */
    String getDatatype();

    /**
     * 
     * @return name the column name
     */
    int getWidth();

    /**
     * 
     * @return defaultValue the column defaultValue
     */
    String getDefaultValue();

    /**
     * 
     * @return xmlPath the column xmlPath
     */
    String getRelativePath();

    /**
     * 
     * @return forOrdinality the column forOrdinality
     */
    boolean getOrdinality();
    
	/**
	 * 
	 * @return no trim value
	 */
	public boolean isNoTrim();

    /**
     * 
     * @return status the <code>IStatus</code> representing the validity of the data in this info object
     */
    IStatus getStatus();

}
