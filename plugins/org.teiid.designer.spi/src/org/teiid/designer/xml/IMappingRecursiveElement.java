/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.xml;

/**
 *
 */
public interface IMappingRecursiveElement extends IMappingElement {

    /**
     * @param recursionCriteria
     */
    void setCriteria(String recursionCriteria);

    /**
     * @param recursionLimit
     * @param throwExceptionOnRecursionLimit
     */
    void setRecursionLimit(int recursionLimit, boolean throwExceptionOnRecursionLimit);

}
