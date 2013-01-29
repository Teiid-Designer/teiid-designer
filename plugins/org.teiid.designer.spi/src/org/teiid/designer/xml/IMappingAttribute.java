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
public interface IMappingAttribute extends IMappingNode {

    /**
     * The XML Schema namespace for attribute instances
     */
    public static final String NAMESPACE_DECLARATION_ATTRIBUTE_NAMESPACE = "xmlns"; //$NON-NLS-1$
    
    /**
     * @param nameInSource
     */
    void setNameInSource(String nameInSource);
    
    /**
     * @param defaultValue
     */
    void setDefaultValue(String defaultValue);
    
    /**
     * @param value
     */
    void setValue(String value);

    /**
     * @param b
     */
    void setOptional(boolean b);

    /**
     * @param b
     */
    void setAlwaysInclude(boolean b);
    
    /**
     * @param excludeFromDocument
     */
    void setExclude(boolean excludeFromDocument);
    
    /**
     * @param normalization
     */
    void setNormalizeText(String normalization);

}
