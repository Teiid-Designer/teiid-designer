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
public interface IMappingDocument {

    /**
     * @return the root element
     */
    IMappingNode getRootElement();

    /**
     * @param element
     */
    void addChildElement(IMappingElement element);

    /**
     * Generate a string from the mapping stream
     * 
     * @return xml mapping string
     * @throws Exception 
     */
    String getMappingString() throws Exception;

}
