/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.xml;

import org.teiid.designer.xml.IMappingAttribute;
import org.teiid.query.mapping.xml.MappingAttribute;
import org.teiid.query.mapping.xml.Namespace;

/**
 *
 */
public class MappingAttributeImpl extends MappingNodeImpl implements IMappingAttribute {

    /**
     * @param attribute
     */
    public MappingAttributeImpl(MappingAttribute attribute) {
        super(attribute);
    }
    
    /**
     * @param name
     * @param namespace
     */
    public MappingAttributeImpl(String name, Namespace namespace) {
        this(new MappingAttribute(name, namespace));
    }

    @Override
    MappingAttribute getMappingNode() {
        return (MappingAttribute) super.getMappingNode();
    }
    
    @Override
    public MappingAttributeImpl clone() {
        return new MappingAttributeImpl((MappingAttribute) getMappingNode().clone());
    }

    @Override
    public void setValue(String value) {
        getMappingNode().setValue(value);
    }

    @Override
    public void setOptional(boolean b) {
        getMappingNode().setOptional(b);
    }

    @Override
    public void setAlwaysInclude(boolean b) {
        getMappingNode().setAlwaysInclude(b);
    }

    @Override
    public void setNameInSource(String nameInSource) {
        getMappingNode().setNameInSource(nameInSource);
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        getMappingNode().setDefaultValue(defaultValue);
    }

    @Override
    public void setExclude(boolean excludeFromDocument) {
        getMappingNode().setExclude(excludeFromDocument);
    }

    @Override
    public void setNormalizeText(String normalization) {
        getMappingNode().setNormalizeText(normalization);
    }

}
