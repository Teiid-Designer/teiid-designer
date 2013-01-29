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
    extends IMappingBaseNode<N> {

    /**
     * @param nameInSource
     */
    void setNameInSource(String nameInSource);

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
     * @param buitInType
     */
    void setType(String buitInType);

    /**
     * @param xsiTypeTextNormalization
     */
    void setNormalizeText(String xsiTypeTextNormalization);

    /**
     * @param attribute
     */
    void addAttribute(A attribute);

    /**
     * @param text
     */
    void addCommentNode(String text);

}
