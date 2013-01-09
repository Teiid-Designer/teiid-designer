/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.xml;

import org.teiid.designer.xml.IMappingChoiceNode;
import org.teiid.designer.xml.IMappingCriteriaNode;
import org.teiid.query.mapping.xml.MappingChoiceNode;

/**
 *
 */
public class MappingChoiceNodeImpl extends MappingNodeImpl implements IMappingChoiceNode {

    /**
     * @param mappingChoiceNode
     */
    public MappingChoiceNodeImpl(MappingChoiceNode mappingChoiceNode) {
        super(mappingChoiceNode);
    }

    /**
     * @param exceptionOnDefault
     */
    public MappingChoiceNodeImpl(boolean exceptionOnDefault) {
        this(new MappingChoiceNode(exceptionOnDefault));
    }
    
    @Override
    MappingChoiceNode getMappingNode() {
        return (MappingChoiceNode) super.getMappingNode();
    }
    
    @Override
    public MappingChoiceNodeImpl clone() {
        return new MappingChoiceNodeImpl((MappingChoiceNode) getMappingNode().clone());
    }

    @Override
    public void addCriteriaNode(IMappingCriteriaNode criteria) {
        MappingCriteriaNodeImpl criteriaImpl = (MappingCriteriaNodeImpl) criteria;
        getMappingNode().addCriteriaNode(criteriaImpl.getMappingNode());
    }

    @Override
    public void setExclude(boolean excludeFromDocument) {
        getMappingNode().setExclude(excludeFromDocument);
    }

    @Override
    public void setSource(String source) {
        getMappingNode().setSource(source);
    }
    
    @Override
    public void addStagingTable(String stagingTable) {
        getMappingNode().addStagingTable(stagingTable);
    }

}
