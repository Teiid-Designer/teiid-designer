/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.schema.impl;

import org.teiid.designer.schema.tools.model.schema.Relationship;
import org.teiid.designer.schema.tools.model.schema.SchemaObject;

/**
 * @since 8.0
 */
public abstract class BaseRelationship implements Relationship {

	private int relationshipType;

	@Override
	public void setType(int relationshipType) {
		this.relationshipType = relationshipType;
	}

	@Override
	public int getType() {
		return relationshipType;
	}

	public BaseRelationship() {
	}
	
	int multiplyCardinalities(int i1, int i2) {
		int retval;
		if (i1 == UNBOUNDED) {
			retval = UNBOUNDED;
		} else if (i2 == UNBOUNDED) {
			retval = UNBOUNDED;
		} else {
			retval = i1 * i2;
		}
		return retval;
	}
	
	@Override
	public void removeRelationship() {
		SchemaObject parent = this.getParent();
		SchemaObject child = this.getChild();
		child.getParents().remove(this);
		parent.getChildren().remove(this);
	}

	@Override
	public void addNewRelationship() {
		SchemaObject parent = this.getParent();
		SchemaObject child = this.getChild();
		child.getParents().add(this);
		parent.getChildren().add(this);
	}
	
	@Override
	public Relationship merge(Relationship grandChildRelation) {
		return new MergedRelationship(this, grandChildRelation);
	}
}
