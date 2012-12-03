/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;


/**
 *
 */
public interface ISQLStringVisitorCallback {

    /**
     * @param obj
     */
    void visitNode(ILanguageObject obj);
    
    /**
     * @param obj
     */
    void append(Object obj);

    /**
     * @param level
     */
    void addTabs(int level);

    /**
     * Allows for the creation of having/where nodes
     * even though there isn't a direct language representation
     * 
     * @param keyWord
     * @param crit
     */
    void visitCriteria(String keyWord, ICriteria crit);

    /**
     * @param level
     */
    void beginClause(int level);

}
