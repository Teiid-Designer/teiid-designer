/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.symbol.IGroupSymbol;

/**
 *
 */
public interface IInto extends ILanguageObject {

    /**
     * Get group held by clause
     * 
     * @return Group held by clause
     */
    IGroupSymbol getGroup();

}
