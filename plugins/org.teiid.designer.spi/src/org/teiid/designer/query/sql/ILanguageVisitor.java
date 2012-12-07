/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import org.teiid.designer.query.sql.lang.ILanguageObject;

/**
 *
 */
public interface ILanguageVisitor {
     
    /**
     * @param languageObject
     */
    void visit(ILanguageObject languageObject);    

}
