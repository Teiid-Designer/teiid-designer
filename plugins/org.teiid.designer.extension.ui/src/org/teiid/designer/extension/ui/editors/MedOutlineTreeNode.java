/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import org.teiid.core.HashCodeUtil;
import org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider;

/**
 * TreeNodes for the MED Outline viewer
 */
public class MedOutlineTreeNode {

    public enum NodeType {
        NAMESPACE_PREFIX,
        NAMESPACE_URI,
        MODEL_CLASS,
        VERSION,
        DESCRIPTION,
        EXTENDED_METACLASS,
        PROPERTY_DEFN
    }

    private String name;
    private NodeType type;
    private Object parent;

    MedOutlineTreeNode( Object parent,
                    NodeType type ) {
        this.parent = parent;
        this.type = type;
        switch (type) {
            case NAMESPACE_PREFIX:
                this.name = "Namespace Prefix"; //$NON-NLS-1$
                break;
            case NAMESPACE_URI:
                this.name = "Namespace URI"; //$NON-NLS-1$
                break;
            case MODEL_CLASS:
                this.name = "Model Class"; //$NON-NLS-1$
                break;
            case VERSION:
                this.name = "Version"; //$NON-NLS-1$
                break;
            case DESCRIPTION:
                this.name = "Description"; //$NON-NLS-1$
                break;
            default:
                this.name = ""; //$NON-NLS-1$
                break;
        }
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getName( ExtendableMetaclassNameProvider nameProvider ) {
        if (this.type == NodeType.EXTENDED_METACLASS) {
            return nameProvider.getLabelText(this.name);
        }
        return ""; //$NON-NLS-1$
    }

    public NodeType getType() {
        return type;
    }

    public Object getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals( final Object object ) {
        if (this == object) return true;
        if (object == null) return false;
        if (getClass() != object.getClass()) return false;

        final MedOutlineTreeNode other = (MedOutlineTreeNode)object;
        if (getType() != other.getType()) {
            return false;
        }
        if (!getName().equals(other.getName())) {
            return false;
        }
        if (!getParent().equals(other.getParent())) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = HashCodeUtil.hashCode(0, getName());

        result = HashCodeUtil.hashCode(result, getType());

        result = HashCodeUtil.hashCode(result, getParent());

        return result;
    }

}
