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
public interface IMappingDocumentFactory {

    /**
     * Add a namespace to the given element
     * 
     * @param element
     * @param prefix 
     * @param uri
     */
    void addNamespace(IMappingElement element, String prefix, String uri);

    /**
     * Create an XML Mapping Document
     * 
     * @param encoding 
     * @param formatted 
     * 
     * @return instance of {@link IMappingDocument} 
     */
    IMappingDocument createMappingDocument(String encoding, boolean formatted);

    /**
     * Create a mapping element
     * 
     * @param name
     * @param nsPrefix
     * 
     * @return instance of {@link IMappingElement}
     */
    IMappingElement createMappingElement(String name, String nsPrefix);

    /**
     * Create a recursive mapping element
     * 
     * @param name
     * @param nsPrefix
     * @param recursionMappingClass
     * 
     * @return instance of {@link IMappingRecursiveElement}
     */
    IMappingRecursiveElement createMappingRecursiveElement(String name,
                                                           String nsPrefix,
                                                           String recursionMappingClass);

    /**
     * Create a mapping attribute
     * 
     * @param name
     * @param nsPrefix
     * 
     * @return instance of {@link IMappingAttribute}
     */
    IMappingAttribute createMappingAttribute(String name, String nsPrefix);

    /**
     * Create a mapping criteria node
     * 
     * @param criteria
     * @param isDefault
     * 
     * @return instance of {@link IMappingCriteriaNode}
     */
    IMappingCriteriaNode createMappingCriteriaNode(String criteria, boolean isDefault);
    
    /**
     * Create a mapping choice node
     * 
     * @param exceptionOnDefault
     * 
     * @return instance of {@link IMappingChoiceNode}
     */
    IMappingChoiceNode createMappingChoiceNode(boolean exceptionOnDefault);

    /**
     * Create a mapping sequence node
     * 
     * @return instance of {@link IMappingSequenceNode}
     */
    IMappingSequenceNode createMappingSequenceNode();

    /**
     * Create a mapping all node
     *
     * @return instance of {@link IMappingAllNode}
     */
    IMappingAllNode createMappingAllNode();

}
