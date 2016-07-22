/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.xml;

import java.io.InputStream;
import org.teiid.designer.xml.IMappingAllNode;
import org.teiid.designer.xml.IMappingAttribute;
import org.teiid.designer.xml.IMappingChoiceNode;
import org.teiid.designer.xml.IMappingCriteriaNode;
import org.teiid.designer.xml.IMappingDocument;
import org.teiid.designer.xml.IMappingDocumentFactory;
import org.teiid.designer.xml.IMappingElement;
import org.teiid.designer.xml.IMappingRecursiveElement;
import org.teiid.designer.xml.IMappingSequenceNode;
import org.teiid.query.mapping.xml.MappingAllNode;
import org.teiid.query.mapping.xml.MappingAttribute;
import org.teiid.query.mapping.xml.MappingChoiceNode;
import org.teiid.query.mapping.xml.MappingCriteriaNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingElement;
import org.teiid.query.mapping.xml.MappingLoader;
import org.teiid.query.mapping.xml.MappingNodeConstants;
import org.teiid.query.mapping.xml.MappingRecursiveElement;
import org.teiid.query.mapping.xml.MappingSequenceNode;
import org.teiid.query.mapping.xml.Namespace;
import org.teiid.query.parser.TeiidParser;

/**
 *
 */
public class MappingDocumentFactory implements IMappingDocumentFactory {

    private final TeiidParser teiidParser;

    /**
     * @param teiidParser
     */
    public MappingDocumentFactory(TeiidParser teiidParser) {
        this.teiidParser = teiidParser;
    }

    /**
     * @return the queryFactory
     */
    public TeiidParser getTeiidParser() {
        return this.teiidParser;
    }

    @Override
    public IMappingDocument loadMappingDocument(InputStream inputStream, String documentName) throws Exception {
        MappingLoader reader = new MappingLoader(getTeiidParser());
        MappingDocument mappingDoc = null;
        mappingDoc = reader.loadDocument(inputStream);
        mappingDoc.setName(documentName);
        
        return mappingDoc;
    }
    
    @Override
    public IMappingDocument createMappingDocument(String encoding, boolean formatted) {
        return new MappingDocument(getTeiidParser(), encoding, formatted);
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
        ((MappingElement) element).addNamespace(namespace);
    }

    @Override
    public IMappingElement createMappingElement(String name, String nsPrefix) {
        Namespace namespace = getNamespace(nsPrefix);
        return new MappingElement(getTeiidParser(), name, namespace);
    }

    @Override
    public IMappingRecursiveElement createMappingRecursiveElement(String name,
                                                                  String nsPrefix,
                                                                  String recursionMappingClass) {
        Namespace namespace = getNamespace(nsPrefix);
        return new MappingRecursiveElement(getTeiidParser(), name, namespace, recursionMappingClass);
    }

    @Override
    public IMappingAttribute createMappingAttribute(String name, String nsPrefix) {
        Namespace namespace = getNamespace(nsPrefix);
        return new MappingAttribute(getTeiidParser(), name, namespace);
    }

    @Override
    public IMappingCriteriaNode createMappingCriteriaNode(String criteria, boolean isDefault) {
        return new MappingCriteriaNode(getTeiidParser(), criteria, isDefault); 
    }

    @Override
    public IMappingChoiceNode createMappingChoiceNode(boolean exceptionOnDefault) {
        return new MappingChoiceNode(getTeiidParser(), exceptionOnDefault);
    }
    
    @Override
    public IMappingAllNode createMappingAllNode() {
        return new MappingAllNode(getTeiidParser());
    }
    
    @Override
    public IMappingSequenceNode createMappingSequenceNode() {
        return new MappingSequenceNode(getTeiidParser());
    }
  
}
