/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.xml;

import org.teiid.designer.xml.IMappingSequenceNode;
import org.teiid.query.mapping.xml.MappingSourceNode;

/**
 *
 */
public class MappingSourceNodeImpl extends MappingNodeImpl implements IMappingSequenceNode {

    public MappingSourceNodeImpl(MappingSourceNode mappingSourceNode) {
        super(mappingSourceNode);
    }
    
    public MappingSourceNodeImpl(String source) {
        this(new MappingSourceNode(source));
    }
    
    @Override
    MappingSourceNode getMappingNode() {
        return (MappingSourceNode) super.getMappingNode();
    }
    
    @Override
    public MappingSourceNodeImpl clone() {
        return new MappingSourceNodeImpl((MappingSourceNode) getMappingNode().clone());
    }

}
