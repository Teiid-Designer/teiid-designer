/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing.internal;

import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;
import com.metamatrix.modeler.schema.tools.processing.RelationshipRules;

public class RequestRelationshipRules implements RelationshipRules {

	RelationshipProcessor processor;

	public RequestRelationshipRules(RelationshipProcessor processor) {
		this.processor = processor;
	}

	public int calculateRelationship(int P_value, int C_value,
			boolean canBeRoot, int F_value) {
		boolean P_eq_0 = P_value == 0;
		boolean P_gt_threshold = P_value > processor.P_threshold();
		boolean P_eq_1 = !P_gt_threshold && P_value == 1;
		boolean P_le_threshold = P_value > 1
				&& P_value <= processor.P_threshold();

		boolean C_le_threshold = C_value > 1
				&& C_value <= processor.C_threshold();
		boolean C_gt_threshold = C_value > processor.C_threshold();
		boolean C_unbounded = C_value == -1;
		boolean C_mixed = C_value == -2;
		boolean C_eq_1 = !C_gt_threshold && C_value == 1;

		int representation;

		if (P_eq_0) {
			// no parents to set so it doesn't matter
			representation = Relationship.MERGE_IN_PARENT_SINGLE;
		} else if (P_eq_1 && C_eq_1) {
				representation = Relationship.MERGE_IN_PARENT_SINGLE;
		} else if (P_eq_1 && C_le_threshold) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_eq_1 && C_gt_threshold) {
			representation = Relationship.MERGE_IN_PARENT_SINGLE;
		} else if (P_eq_1 && C_unbounded) {
			representation = Relationship.MERGE_IN_PARENT_SINGLE;
		} else if (P_le_threshold && C_eq_1) {
			representation = Relationship.MERGE_IN_PARENT_SINGLE;
		} else if (P_le_threshold && C_le_threshold) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_le_threshold && C_gt_threshold) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_le_threshold && C_unbounded) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_le_threshold && C_mixed) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_gt_threshold && C_eq_1) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_gt_threshold && C_le_threshold) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_gt_threshold && C_gt_threshold) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_gt_threshold && C_unbounded) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else if (P_gt_threshold && C_mixed) {
			representation = Relationship.MERGE_IN_PARENT_MULTIPLE;
		} else {
			representation = Relationship.RELATIONSHIP_TABLE;
		}
		return representation;
	}
}
