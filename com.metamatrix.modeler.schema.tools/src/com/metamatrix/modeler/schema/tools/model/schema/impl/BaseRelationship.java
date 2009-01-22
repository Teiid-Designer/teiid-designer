/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public abstract class BaseRelationship implements Relationship {

	private int relationshipType;

	public void setType(int relationshipType) {
		this.relationshipType = relationshipType;
	}

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
	
	public void removeRelationship() {
		SchemaObject parent = this.getParent();
		SchemaObject child = this.getChild();
		child.getParents().remove(this);
		parent.getChildren().remove(this);
	}

	public void addNewRelationship() {
		SchemaObject parent = this.getParent();
		SchemaObject child = this.getChild();
		child.getParents().add(this);
		parent.getChildren().add(this);
	}
	
	public Relationship merge(Relationship grandChildRelation) {
		return new MergedRelationship(this, grandChildRelation);
	}
}
