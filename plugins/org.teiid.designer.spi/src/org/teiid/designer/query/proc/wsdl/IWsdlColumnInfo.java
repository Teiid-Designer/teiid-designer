/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.proc.wsdl;

import org.eclipse.core.runtime.IStatus;

/**
 *
 */
public interface IWsdlColumnInfo {

    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
    public static final String BIGINTEGER_DATATYPE = "biginteger"; //$NON-NLS-1$
    public static final String INTEGER_DATATYPE = "integer"; //$NON-NLS-1$
    public static final int DEFAULT_WIDTH = 10;

    /**
     * Get the column name for display in the UI. This removes any quotes for
     * aesthetic reasons. Use {@link #getSymbolName()} for retrieving the 
     * fully validated column name.
     * 
     * @return the column name sans quotes.
     */
    String getName();

    /**
     * Get the fully validated column name. This should be used in SQL string
     * generation.
     *
     * @return name the column name
     */
    String getSymbolName();

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
     * @return xmlPath the column xmlPath
     */
    String getFullXmlPath();

    /**
     * 
     * @return forOrdinality the column forOrdinality
     */
    boolean getOrdinality();

    String getNamespace();
    
    IWsdlAttributeInfo[] getAttributeInfoArray();

    String getUniqueAttributeName(String proposedName);

    /**
     * 
     * @return status the <code>IStatus</code> representing the validity of the data in this info object
     */
    IStatus getStatus();

}
