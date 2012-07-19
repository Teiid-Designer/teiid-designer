/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.schema.impl;

import org.teiid.designer.schema.tools.model.schema.SchemaObject;

/**
 * @since 8.0
 */
public class SimpleRelationship extends BaseRelationship {
    private SchemaObject parent; // a parent of null means the child can be
    // global

    private SchemaObject child;

    // Relates to the cardinality that the child appears with inside the parent
    private int minOccurs;

    private int maxOccurs;

    public SimpleRelationship( SchemaObject parent,
                               SchemaObject child,
                               int minOccurs,
                               int maxOccurs ) {
        this.parent = parent;
        this.child = child;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
    }

    @Override
	public String getParentRelativeXpath() {
        return ".."; //$NON-NLS-1$
    }

    @Override
	public String getChildRelativeXpath() {
        String xpath = child.getRelativeXpath();
        return xpath;
    }

    @Override
	public SchemaObject getParent() {
        return parent;
    }

    @Override
	public SchemaObject getChild() {
        return child;
    }

    @Override
	public int getMinOccurs() {
        return minOccurs;
    }

    @Override
	public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setMinOccurs( int minOccurs ) {
        this.minOccurs = minOccurs;
    }

    public void setMaxOccurs( int maxOccurs ) {
        this.maxOccurs = maxOccurs;
    }

    @Override
    public String toString() {
        String childName = getChild().getSimpleName();
        String parentName = getParent().getSimpleName();
        return parentName + "_" + childName; //$NON-NLS-1$
    }

    public Object copy( SchemaModelCopyTraversalContext ctx ) {
        SchemaObject parentCopy = ctx.getElement(parent);
        SchemaObject childCopy = ctx.getElement(child);
        return new SimpleRelationship(parentCopy, childCopy, minOccurs, maxOccurs);
    }

    @Override
	public void printDebug() {
        StringBuffer buff = new StringBuffer();
        buff.append("\t \t SimpleRelationship:"); //$NON-NLS-1$
        buff.append("ParentSimpleName = " + parent.getSimpleName()); //$NON-NLS-1$
        buff.append(" "); //$NON-NLS-1$
        buff.append("ChildSimpleName = " + child.getSimpleName()); //$NON-NLS-1$
        buff.append(" "); //$NON-NLS-1$
        buff.append("MinOccurs = " + minOccurs); //$NON-NLS-1$
        buff.append(" "); //$NON-NLS-1$
        buff.append("MaxOccurs = " + maxOccurs); //$NON-NLS-1$
        buff.append(" "); //$NON-NLS-1$
        buff.append("Type = " + getType()); //$NON-NLS-1$
        System.out.println(buff.toString());
    }

    @Override
    public void setType( int relationshipType ) {
        super.setType(relationshipType);
    }

    @Override
    public int getType() {
        return super.getType();
    }
}
