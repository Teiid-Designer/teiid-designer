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

package com.metamatrix.modeler.schema.tools.processing;

import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
/**
 *Determines the relationships between SchemaObjects based upon an instance
 *of the RelationshipRules Interface and performs the folding of the children
 *into parents as needed.
 */
public interface RelationshipProcessor {

	/**
	 * Performs the folding operation on the provided SchemaModel.
	 * @param model - the SchemModel to perform the folding operation on.
	 */
	public abstract void calculateRelationshipTypes(SchemaModel model);

	/**
	 * Adds a relationship between SchemaObjects to the set of relationships
	 * maintained by the processor.
	 * @param key - the name of the child table in the relationship.
	 * @param value - the value of the Relationship
	 */
	public abstract void addRelationship(String key, Integer value);
	
	/**
	 * Returns the value of the Child Limit value for this RelationshipProcessor 
	 * @return Returns the value of the Child Limit value
	 */
	public int C_threshold();
	
	/**
	 * Returns the value of the Parent Limit value for this RelationshipProcessor 
	 * @return Returns the value of the Parent Limit value
	 */
	public int P_threshold();
	
	/**
	 * Returns the value of the Field Limit value for this RelationshipProcessor 
	 * @return Returns the value of the Field Limit value
	 */
	public int F_threshold();
	
	/**
	 * Accessor to provide the RelationshipRules that the processor will use
	 * to perform the forlding operations.
	 * @param rules - the RelationshipRules
	 */
	public abstract void setRelationshipRules(RelationshipRules rules);
}