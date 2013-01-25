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
public interface IMappingChoiceNode<CN extends IMappingCriteriaNode, N extends IMappingNode>
    extends IMappingNode<N> {

    /**
     * @param criteria
     */
    void addCriteriaNode(CN criteria);

    /**
     * @param excludeFromDocument
     */
    void setExclude(boolean excludeFromDocument);

    /**
     * @param source
     */
    void setSource(String source);
    
    /**
     * @param stagingTable
     */
    void addStagingTable(String stagingTable);

}
