/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.xml;

import org.teiid.designer.xml.IMappingAllNode;
import org.teiid.query.mapping.xml.MappingAllNode;

/**
 *
 */
public class MappingAllNodeImpl extends MappingNodeImpl implements IMappingAllNode {

    public MappingAllNodeImpl(MappingAllNode mappingAllNode) {
        super(mappingAllNode);
    }
    
    public MappingAllNodeImpl() {
        this(new MappingAllNode());
    }
    
    @Override
    MappingAllNode getMappingNode() {
        return (MappingAllNode) super.getMappingNode();
    }
    
    @Override
    public MappingAllNodeImpl clone() {
        return new MappingAllNodeImpl((MappingAllNode) getMappingNode().clone());
    }
    
}
