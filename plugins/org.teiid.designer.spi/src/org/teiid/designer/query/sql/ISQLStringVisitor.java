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
public interface ISQLStringVisitor<LO extends ILanguageObject> extends ILanguageVisitor {

    /**
     * Should the visitor fail to evaluate then this
     * text is returned
     */
    public static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$
    
    /**
     * Find the string representation of the given object
     * 
     * @param languageObject
     * 
     * @return SQL string
     */
    String returnSQLString(LO languageObject);

    /**
     * @param token
     * @return id tokens properly escaped
     */
    String displayName(IToken token);
}
