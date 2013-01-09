/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.teiid.designer.xml.IMappingDocument;
import org.teiid.designer.xml.IMappingNode;
import org.teiid.query.mapping.xml.MappingAllNode;
import org.teiid.query.mapping.xml.MappingChoiceNode;
import org.teiid.query.mapping.xml.MappingCriteriaNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingElement;
import org.teiid.query.mapping.xml.MappingOutputter;
import org.teiid.query.mapping.xml.MappingRecursiveElement;
import org.teiid.query.mapping.xml.MappingSequenceNode;
import org.teiid.query.mapping.xml.MappingSourceNode;

/**
 *
 */
public class MappingDocumentImpl extends MappingNodeImpl implements IMappingDocument {

    /**
     * @param mappingDocument
     */
    public MappingDocumentImpl(MappingDocument mappingDocument) {
        super(mappingDocument);
    }
    
    /**
     * @param encoding
     * @param formatted
     */
    public MappingDocumentImpl(String encoding, boolean formatted) {
        this(new MappingDocument(encoding, formatted));
    }

    @Override
    public MappingDocument getMappingNode() {
        return (MappingDocument) super.getMappingNode();
    }

    @Override
    public IMappingNode getRootElement() {
        if (getMappingNode().getRootNode() instanceof MappingAllNode) {
            return new MappingAllNodeImpl((MappingAllNode) getMappingNode().getRootNode());
        }
        if (getMappingNode().getRootNode() instanceof MappingChoiceNode) {
            return new MappingChoiceNodeImpl((MappingChoiceNode) getMappingNode().getRootNode());
        }
        if (getMappingNode().getRootNode() instanceof MappingCriteriaNode) {
            return new MappingCriteriaNodeImpl((MappingCriteriaNode) getMappingNode().getRootNode());
        }
        if (getMappingNode().getRootNode() instanceof MappingRecursiveElement) {
            return new MappingRecursiveElementImpl((MappingRecursiveElement) getMappingNode().getRootNode());
        }
        if (getMappingNode().getRootNode() instanceof MappingElement) {
            return new MappingElementImpl((MappingElement) getMappingNode().getRootNode());
        }
        if (getMappingNode().getRootNode() instanceof MappingSequenceNode) {
            return new MappingSequenceNodeImpl((MappingSequenceNode) getMappingNode().getRootNode());
        }
        if (getMappingNode().getRootNode() instanceof MappingSourceNode) {
            return new MappingSourceNodeImpl((MappingSourceNode) getMappingNode().getRootNode());
        }

        return new MappingNodeImpl(getMappingNode().getRootNode());
    }

    @Override
    public IMappingNode addChild(IMappingNode node) {
        MappingNodeImpl nodeImpl = (MappingNodeImpl) node;
        getMappingNode().addChild(nodeImpl.getMappingNode());
        return node;
    }

    @Override
    public String getMappingString() throws Exception{
     // Output the mapping objects to a stream
        String result = null;
        OutputStream moStream = null;
        try {
            moStream = new ByteArrayOutputStream();
            final PrintWriter pw = new PrintWriter(moStream, true);
            final MappingOutputter outputter = new MappingOutputter();
            outputter.write(getMappingNode(), pw); // TODO FIX/REPLACE??? , isNewlines(), isIndent());
            pw.flush();

            result = moStream.toString();
        } catch (final Exception e) {
            throw e;
        } finally {
            if (moStream != null) {
                try {
                    moStream.close();
                } catch (final IOException e1) {
                    throw e1;
                }
                moStream = null;
            }
        }
        return result;
    }
    
    @Override
    public MappingDocumentImpl clone() {
        return new MappingDocumentImpl((MappingDocument) getMappingNode().clone());
    }
}
