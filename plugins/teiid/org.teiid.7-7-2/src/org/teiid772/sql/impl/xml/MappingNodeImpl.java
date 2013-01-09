/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.xml;

import org.teiid.designer.xml.IMappingAllNode;
import org.teiid.designer.xml.IMappingChoiceNode;
import org.teiid.designer.xml.IMappingCriteriaNode;
import org.teiid.designer.xml.IMappingElement;
import org.teiid.designer.xml.IMappingNode;
import org.teiid.designer.xml.IMappingSequenceNode;
import org.teiid.designer.xml.IMappingSourceNode;
import org.teiid.query.mapping.xml.MappingBaseNode;
import org.teiid.query.mapping.xml.MappingNode;

/**
 *
 */
public class MappingNodeImpl implements IMappingNode {

    protected final MappingNode mappingNode;
    
    /**
     * @param mappingNode
     */
    public MappingNodeImpl(MappingNode mappingNode) {
        this.mappingNode = mappingNode;
    }
    
    MappingNode getMappingNode() {
        return mappingNode;
    }
    
    @Override
    public MappingNodeImpl clone() {
        return new MappingNodeImpl(mappingNode.clone());
    }
    
    @Override
    public IMappingNode addChild(IMappingNode node) {
        if (node instanceof IMappingElement) {
            addChildElement((IMappingElement) node);
        } else if (node instanceof IMappingChoiceNode) {
            addChoiceNode((IMappingChoiceNode) node);
        } else if (node instanceof IMappingCriteriaNode) {
            addCriteriaNode((IMappingCriteriaNode) node);
        } else if (node instanceof IMappingSequenceNode) {
            addSequenceNode((IMappingSequenceNode) node);
        } else if (node instanceof IMappingSourceNode) {
            addSourceNode((IMappingSourceNode) node);
        } else if (node instanceof IMappingAllNode) {
            addAllNode((IMappingAllNode) node);    
        }
        
        return node;
    }

    private void addChildElement(IMappingElement element) {
        if (mappingNode instanceof MappingBaseNode) {
            MappingElementImpl elementImpl = (MappingElementImpl) element;
            ((MappingBaseNode)mappingNode).addChildElement(elementImpl.getMappingNode());
            return;
        }
        
        throw new UnsupportedOperationException();
    }
    
    private void addCriteriaNode(IMappingCriteriaNode node) {
        if (mappingNode instanceof MappingBaseNode) {
            MappingCriteriaNodeImpl nodeImpl = (MappingCriteriaNodeImpl) node;
            ((MappingBaseNode)mappingNode).addCriteriaNode(nodeImpl.getMappingNode());
            return;
        }
        
        throw new UnsupportedOperationException();
    }

    private void addChoiceNode(IMappingChoiceNode node) {
        if (mappingNode instanceof MappingBaseNode) {
            MappingChoiceNodeImpl nodeImpl = (MappingChoiceNodeImpl) node;
            ((MappingBaseNode)mappingNode).addChoiceNode(nodeImpl.getMappingNode());
            return;
        }
        
        throw new UnsupportedOperationException();
    }
    
    private void addSequenceNode(IMappingSequenceNode node) {
        if (mappingNode instanceof MappingBaseNode) {
            MappingSequenceNodeImpl nodeImpl = (MappingSequenceNodeImpl) node;
            ((MappingBaseNode)mappingNode).addSequenceNode(nodeImpl.getMappingNode());
            return;
        }
        
        throw new UnsupportedOperationException();
    }
    
    private void addAllNode(IMappingAllNode node) {
        if (mappingNode instanceof MappingBaseNode) {
            MappingAllNodeImpl nodeImpl = (MappingAllNodeImpl) node;
            ((MappingBaseNode)mappingNode).addAllNode(nodeImpl.getMappingNode());
            return;
        }
        
        throw new UnsupportedOperationException();
    }     
    
    private void addSourceNode(IMappingSourceNode node) {
        if (mappingNode instanceof MappingBaseNode) {
            MappingSourceNodeImpl nodeImpl = (MappingSourceNodeImpl) node;
            ((MappingBaseNode)mappingNode).addSourceNode(nodeImpl.getMappingNode());
            return;
        }
        
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExclude(boolean excludeFromDocument) {
        getMappingNode().setExclude(excludeFromDocument);
    }

    @Override
    public void setSource(String source) {
        if (mappingNode instanceof MappingBaseNode) {
            ((MappingBaseNode)mappingNode).setSource(source);
            return;
        }
        
        throw new UnsupportedOperationException();
    }

    @Override
    public void addStagingTable(String stagingTable) {
        if (mappingNode instanceof MappingBaseNode) {
            ((MappingBaseNode)mappingNode).addStagingTable(stagingTable);
            return;
        }
        
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return mappingNode.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.mappingNode == null) ? 0 : this.mappingNode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MappingNodeImpl other = (MappingNodeImpl)obj;
        if (this.mappingNode == null) {
            if (other.mappingNode != null) return false;
        } else if (!this.mappingNode.equals(other.mappingNode)) return false;
        return true;
    }
    
    
}
