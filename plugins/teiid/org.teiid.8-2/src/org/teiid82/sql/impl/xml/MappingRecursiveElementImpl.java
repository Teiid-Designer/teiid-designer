/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.xml;

import org.teiid.designer.xml.IMappingRecursiveElement;
import org.teiid.query.mapping.xml.MappingRecursiveElement;
import org.teiid.query.mapping.xml.Namespace;

/**
 *
 */
public class MappingRecursiveElementImpl extends MappingElementImpl implements IMappingRecursiveElement {

    /**
     * @param name
     * @param namespace
     * @param recursionMappingClass 
     */
    public MappingRecursiveElementImpl(String name, Namespace namespace, String recursionMappingClass) {
        super(new MappingRecursiveElement(name, namespace, recursionMappingClass));
    }
    
    /**
     * @param element
     */
    public MappingRecursiveElementImpl(MappingRecursiveElement element) {
        super(element);
    }

    @Override
    MappingRecursiveElement getMappingNode() {
        return (MappingRecursiveElement) super.getMappingNode();
    }
    
    @Override
    public MappingRecursiveElementImpl clone() {
        return new MappingRecursiveElementImpl((MappingRecursiveElement) getMappingNode().clone());
    }
    
    @Override
    public void setCriteria(String recursionCriteria) {
        getMappingNode().setCriteria(recursionCriteria);
    }

    @Override
    public void setRecursionLimit(int recursionLimit, boolean throwExceptionOnRecursionLimit) {
        getMappingNode().setRecursionLimit(recursionLimit, throwExceptionOnRecursionLimit);
    }

}
