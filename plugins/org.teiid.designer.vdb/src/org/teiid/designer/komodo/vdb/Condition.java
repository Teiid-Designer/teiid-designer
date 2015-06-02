/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

/**
 * Represents a VDB permission condition.
 */
public class Condition extends VdbObject {
	boolean constraint;

    /**
     * The type identifier.
     */
    int TYPE_ID = Condition.class.hashCode();

    /**
     * Identifier of this object
     */
    TeiidType IDENTIFIER = TeiidType.VDB_CONDITION;

    /**
     * The default value indicating if this condition is a constraint. Value is {@value} .
     */
    boolean DEFAULT_CONSTRAINT = true;

    /**
     * An empty array of conditions.
     */
    Condition[] NO_CONDITIONS = new Condition[0];


    /**
     * @param expression
     * @param constraint
     */
    public Condition(String expression, boolean constraint) {
    	super();
    	setName(expression);
    	this.constraint = constraint;
    }
    
    /**
     * @return <code>true</code> if this condition is a constraint
     * @see #DEFAULT_CONSTRAINT
     */
    public boolean isConstraint() {
    	
    	return this.constraint;
    }

    /**
     * @param newConstraint
     *        the new value for the <code>constraint</code> property
     * @see #DEFAULT_CONSTRAINT
     */
    public void setConstraint( final boolean newConstraint ) {
    	setChanged(this.constraint, newConstraint);
    	this.constraint = newConstraint;
    }

}
