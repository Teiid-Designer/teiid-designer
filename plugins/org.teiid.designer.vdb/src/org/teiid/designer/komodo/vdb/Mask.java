/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

/**
 * Represents a VDB permission mask.
 */
public class Mask extends VdbObject {
	int order;
    /**
     * The type identifier.
     */
    int TYPE_ID = Mask.class.hashCode();

    /**
     * Identifier of this object
     */
    TeiidType IDENTIFIER = TeiidType.VDB_MASK;

    /**
     * An empty array of masks.
     */
    Mask[] NO_MASKS = new Mask[0];

    /**
     * A name used by Teiid to reference this VDB.
     *
     * @return the value of the <code>order</code> property (can be empty)
     */
    public int getOrder() {
    	return this.order;
    }

    /**
     * Sets the name used by Teiid to reference this VDB.
     *
     * @param newOrder
     *        the new value of the <code>order</code> property
     */
    public void setOrder(final int newOrder ) {
    	setChanged(this.order, newOrder);
    	this.order = newOrder;
    }

    /**
     * @return string value or order
     */
    public String getOrderString() {
    	return Integer.toString(this.order);
    }

}
