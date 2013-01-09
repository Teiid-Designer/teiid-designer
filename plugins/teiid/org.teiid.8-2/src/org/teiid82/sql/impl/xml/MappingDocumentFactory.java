/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.xml;

import java.io.InputStream;
import org.teiid.designer.xml.IMappingAllNode;
import org.teiid.designer.xml.IMappingAttribute;
import org.teiid.designer.xml.IMappingChoiceNode;
import org.teiid.designer.xml.IMappingCriteriaNode;
import org.teiid.designer.xml.IMappingDocument;
import org.teiid.designer.xml.IMappingDocumentFactory;
import org.teiid.designer.xml.IMappingElement;
import org.teiid.designer.xml.IMappingNode;
import org.teiid.designer.xml.IMappingRecursiveElement;
import org.teiid.designer.xml.IMappingSequenceNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingLoader;
import org.teiid.query.mapping.xml.MappingNode;
import org.teiid.query.mapping.xml.MappingNodeConstants;
import org.teiid.query.mapping.xml.Namespace;

/**
 *
 */
public class MappingDocumentFactory implements IMappingDocumentFactory {

    @Override
    public IMappingDocument loadMappingDocument(InputStream inputStream, String documentName) throws Exception {
        MappingLoader reader = new MappingLoader();
        MappingDocument mappingDoc = null;
        mappingDoc = reader.loadDocument(inputStream);
        mappingDoc.setName(documentName);
        
        return new MappingDocumentImpl(mappingDoc);
    }
    
    @Override
    public IMappingDocument createMappingDocument(String encoding, boolean formatted) {
        return new MappingDocumentImpl(encoding, formatted);
    }
    
    private Namespace getNamespace(final String prefix) {
        if (prefix != null) 
            return new Namespace(prefix);
        
        return MappingNodeConstants.NO_NAMESPACE;
    }
    
    private Namespace getNamespace(final String prefix, final String uri) {
        return new Namespace(prefix, uri);
    }
    
    @Override
    public void addNamespace(IMappingElement element, String prefix, String uri) {
        Namespace namespace = getNamespace(prefix, uri);
        MappingElementImpl elementImpl = (MappingElementImpl) element;
        elementImpl.getMappingNode().addNamespace(namespace);
    }

    @Override
    public IMappingElement createMappingElement(String name, String nsPrefix) {
        Namespace namespace = getNamespace(nsPrefix);
        return new MappingElementImpl(name, namespace);
    }

    @Override
    public IMappingRecursiveElement createMappingRecursiveElement(String name,
                                                                  String nsPrefix,
                                                                  String recursionMappingClass) {
        Namespace namespace = getNamespace(nsPrefix);
        return new MappingRecursiveElementImpl(name, namespace, recursionMappingClass);
    }

    @Override
    public IMappingAttribute createMappingAttribute(String name, String nsPrefix) {
        Namespace namespace = getNamespace(nsPrefix);
        return new MappingAttributeImpl(name, namespace);
    }

    @Override
    public IMappingCriteriaNode createMappingCriteriaNode(String criteria, boolean isDefault) {
        return new MappingCriteriaNodeImpl(criteria, isDefault); 
    }

    @Override
    public IMappingChoiceNode createMappingChoiceNode(boolean exceptionOnDefault) {
        return new MappingChoiceNodeImpl(exceptionOnDefault);
    }
    
    @Override
    public IMappingAllNode createMappingAllNode() {
        return new MappingAllNodeImpl();
    }
    
    @Override
    public IMappingSequenceNode createMappingSequenceNode() {
        return new MappingSequenceNodeImpl();
    }

    /**
     * Unwrap the mapping node from the wrapper class
     * 
     * @param mappingNode
     * @return delegate mapping node
     */
    public MappingNode convert(IMappingNode mappingNode) {
        return ((MappingNodeImpl) mappingNode).getMappingNode();
    }
  
}
