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
/**
 *The determination of relationships by implementations of 
 *RelationshipProcessor are made around a number of fixed 
 *parameters.  The determination of how to relate SchemaObjects
 *can be different for different purposes with the same values.
 *This interface provides a place to change the way relations
 *are determined.  
 */
public interface RelationshipRules {
	
	/**
	 * Determines the relationship between a SchemaObject and its parents
	 * based upon the values of the input parameters. 
	 * @param P_value - the Parent limit threshold.
	 * @param C_value - the Child limit threshold.
	 * @param canBeRoot - is the SchemaObject selected as a root.
	 * @param F_value - the Field limit threshold.
	 * @return - the value calculated for the Relationship.
	 */
	public int calculateRelationship(int P_value, int C_value,
			boolean canBeRoot, int F_value);
}
