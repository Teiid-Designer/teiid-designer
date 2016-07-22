/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.language;

/**
 *
 */
public interface SortSpecification {

    /**
     * How to sort on a specific expression, eg. {code:sql}col1 NULLS FIRST{code}
     */
    enum NullOrdering {
        FIRST,
        LAST;
    }
}
