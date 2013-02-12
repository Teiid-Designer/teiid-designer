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
public interface IWsdlAttributeInfo {

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
     * @return name the attribute alias
     */
    String getAlias();

    String getSignature();

    /**
     * 
     * @return status the <code>IStatus</code> representing the validity of the data in this info object
     */
    IStatus getStatus();

}
