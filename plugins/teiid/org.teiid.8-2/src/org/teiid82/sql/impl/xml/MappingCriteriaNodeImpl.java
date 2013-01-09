/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.xml;

import org.teiid.designer.xml.IMappingCriteriaNode;
import org.teiid.query.mapping.xml.MappingCriteriaNode;

/**
 *
 */
public class MappingCriteriaNodeImpl extends MappingNodeImpl implements IMappingCriteriaNode {

    /**
     * @param mappingElement
     */
    protected MappingCriteriaNodeImpl(MappingCriteriaNode mappingCriteriaNode) {
        super(mappingCriteriaNode);
    }
    
    /**
     * @param name
     * @param isDefault
     */
    public MappingCriteriaNodeImpl(String name, boolean isDefault) {
        this(new MappingCriteriaNode(name, isDefault));
    }

    @Override
    MappingCriteriaNode getMappingNode() {
        return (MappingCriteriaNode) super.getMappingNode();
    }
    
    @Override
    public MappingCriteriaNodeImpl clone() {
        return new MappingCriteriaNodeImpl((MappingCriteriaNode) getMappingNode().clone());
    }
}
