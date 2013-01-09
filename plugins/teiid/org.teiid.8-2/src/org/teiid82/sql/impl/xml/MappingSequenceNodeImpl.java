/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.xml;

import org.teiid.designer.xml.IMappingSequenceNode;
import org.teiid.query.mapping.xml.MappingSequenceNode;

/**
 *
 */
public class MappingSequenceNodeImpl extends MappingNodeImpl implements IMappingSequenceNode {

    public MappingSequenceNodeImpl(MappingSequenceNode mappingSequenceNode) {
        super(mappingSequenceNode);
    }
    
    public MappingSequenceNodeImpl() {
        this(new MappingSequenceNode());
    }
    
    @Override
    MappingSequenceNode getMappingNode() {
        return (MappingSequenceNode) super.getMappingNode();
    }
    
    @Override
    public MappingSequenceNodeImpl clone() {
        return new MappingSequenceNodeImpl((MappingSequenceNode) getMappingNode().clone());
    }

}
