/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

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
