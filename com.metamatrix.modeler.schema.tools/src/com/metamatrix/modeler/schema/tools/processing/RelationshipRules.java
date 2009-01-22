/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
