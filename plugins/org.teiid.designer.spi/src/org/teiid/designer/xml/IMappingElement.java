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
public interface IMappingElement<A extends IMappingAttribute, N extends IMappingNode>
    extends IMappingNode<N> {

    /**
     * @param minOccurrences
     */
    void setMinOccurrs(int minOccurrences);

    /**
     * @param maxOccurrences
     */
    void setMaxOccurrs(int maxOccurrences);

    /**
     * @param nameInSource
     */
    void setNameInSource(String nameInSource);

    /**
     * @param source
     */
    void setSource(String source);

    /**
     * @param defaultValue
     */
    void setDefaultValue(String defaultValue);

    /**
     * @param fixedValue
     */
    void setValue(String fixedValue);

    /**
     * @param nillable
     */
    void setNillable(boolean nillable);

    /**
     * @param excludeFromDocument
     */
    void setExclude(boolean excludeFromDocument);

    /**
     * @param buitInType
     */
    void setType(String buitInType);

    /**
     * @param xsiTypeTextNormalization
     */
    void setNormalizeText(String xsiTypeTextNormalization);

    /**
     * @param stagingTable
     */
    void addStagingTable(String stagingTable);

    /**
     * @param attribute
     */
    void addAttribute(A attribute);

    /**
     * @param text
     */
    void addCommentNode(String text);

}
