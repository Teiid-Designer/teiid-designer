/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;



/**
 *
 */
public interface IFromClause extends ILanguageObject {

    /**
     * Is the clause optional
     * 
     * @return true if optional
     */
    boolean isOptional();
    
    /**
     * Set whether the clause is optional
     * 
     * @param optional
     */
    void setOptional(boolean optional);
    
    /**
     * Is make dependent
     * 
     * @return true if make dependent
     */
    boolean isMakeDep();

    /**
     * Set make dependent
     * 
     * @param makeDep
     */
    void setMakeDep(boolean makeDep);
    
    /**
     * Is make not dependent
     * 
     * @return true if make not dependent
     */
    boolean isMakeNotDep();
    
    /**
     * Set make not dependent
     * 
     * @param makeNotDep
     */
    void setMakeNotDep(boolean makeNotDep);
}
