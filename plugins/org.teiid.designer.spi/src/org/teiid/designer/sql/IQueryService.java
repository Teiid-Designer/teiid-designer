/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.sql;

import java.util.Set;

/**
 *
 */
public interface IQueryService {

    /**
     * Is the given word a reserved part of the SQL syntax
     * 
     * @param word
     * 
     * @return true if the word is reserved.
     */
    boolean isReservedWord(final String word);
    
    /**
     * Is the given word a reserved part of the Procedure SQL syntax
     * 
     * @param word
     * 
     * @return true if the word is reserved.
     */
    boolean isProcedureReservedWord(final String word);

    /**
     * Get the SQL reserved words
     * 
     * @return set of reserved words
     */
    Set<String> getReservedWords();

    /**
     * Get the SQL non-reserved words
     * 
     * @return set of non-reserved words
     */
    Set<String> getNonReservedWords();

    /**
     * Get the name of the JDCB type that conforms to the
     * given index number
     * 
     * @param jdbcType
     * 
     * @return type name
     */
    String getJDBCSQLTypeName(int jdbcType);
}
