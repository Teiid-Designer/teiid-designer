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
import org.teiid.designer.xml.IMappingElement;
import org.teiid.designer.xml.IMappingNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingOutputter;

/**
 *
 */
public class MappingDocumentImpl implements IMappingDocument {

    private final MappingDocument document;
    
    /**
     * @param encoding
     * @param formatted
     */
    public MappingDocumentImpl(String encoding, boolean formatted) {
        document = new MappingDocument(encoding, formatted);
    }

    @Override
    public IMappingNode getRootElement() {
        return new MappingNodeImpl(document.getRootNode());
    }

    @Override
    public void addChildElement(IMappingElement element) {
        MappingElementImpl elementImpl = (MappingElementImpl) element;
        document.addChildElement(elementImpl.getMappingNode());
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
            outputter.write(document, pw); // TODO FIX/REPLACE??? , isNewlines(), isIndent());
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
    public String toString() {
        return this.document.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.document == null) ? 0 : this.document.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MappingDocumentImpl other = (MappingDocumentImpl)obj;
        if (this.document == null) {
            if (other.document != null) return false;
        } else if (!this.document.equals(other.document)) return false;
        return true;
    }
    
    

}
