/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.xml;

import org.teiid.designer.xml.IMappingAttribute;
import org.teiid.designer.xml.IMappingElement;
import org.teiid.query.mapping.xml.MappingCommentNode;
import org.teiid.query.mapping.xml.MappingElement;
import org.teiid.query.mapping.xml.Namespace;

/**
 *
 */
public class MappingElementImpl extends MappingNodeImpl implements IMappingElement {

    /**
     * @param mappingElement
     */
    protected MappingElementImpl(MappingElement mappingElement) {
        super(mappingElement);
    }
    
    /**
     * @param name
     * @param namespace
     */
    public MappingElementImpl(String name, Namespace namespace) {
        this(new MappingElement(name, namespace));
    }

    @Override
    MappingElement getMappingNode() {
        return (MappingElement) super.getMappingNode();
    }
    
    @Override
    public MappingElementImpl clone() {
        return new MappingElementImpl((MappingElement) getMappingNode().clone());
    }

    @Override
    public void setMinOccurrs(int minOccurrences) {
        getMappingNode().setMinOccurrs(minOccurrences);
    }

    @Override
    public void setMaxOccurrs(int maxOccurrences) {
        getMappingNode().setMaxOccurrs(maxOccurrences);
    }

    @Override
    public void setNameInSource(String nameInSource) {
        getMappingNode().setNameInSource(nameInSource);
    }

    @Override
    public void setSource(String source) {
        getMappingNode().setSource(source);
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        getMappingNode().setDefaultValue(defaultValue);
    }

    @Override
    public void setValue(String fixedValue) {
        getMappingNode().setValue(fixedValue);
    }

    @Override
    public void setNillable(boolean nillable) {
        getMappingNode().setNillable(nillable);
    }

    @Override
    public void setExclude(boolean excludeFromDocument) {
        getMappingNode().setExclude(excludeFromDocument);
    }

    @Override
    public void setType(String buitInType) {
        getMappingNode().setType(buitInType);
    }

    @Override
    public void setNormalizeText(String xsiTypeTextNormalization) {
        getMappingNode().setNormalizeText(xsiTypeTextNormalization);
    }

    @Override
    public void addStagingTable(String stagingTable) {
        getMappingNode().addStagingTable(stagingTable);
    }
    
    @Override
    public void addAttribute(IMappingAttribute attribute) {
        MappingAttributeImpl attributeImpl = (MappingAttributeImpl) attribute;
        getMappingNode().addAttribute(attributeImpl.getMappingNode());
    }

    @Override
    public void addCommentNode(String text) {
        getMappingNode().addCommentNode(new MappingCommentNode(text));
    }
    
}
