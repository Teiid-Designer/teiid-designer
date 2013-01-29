/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.xml;

/**
 * @param <N> 
 *
 */
public interface IMappingBaseNode<N extends IMappingNode> extends IMappingNode {

    /**
     * Add a child to this node
     * 
     * @param childNode
     */
    void addChildNode(N childNode);
    
    /**
     * @param minOccurrences
     */
    void setMinOccurrs(int minOccurrences);

    /**
     * @param maxOccurrences
     */
    void setMaxOccurrs(int maxOccurrences);
    
    /**
     * @param source
     */
    void setSource(String source);
    
    /**
     * @param stagingTable
     */
    void addStagingTable(String stagingTable);
}
