/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing;

import com.metamatrix.modeler.schema.tools.processing.internal.DefaultRelationshipRules;
import com.metamatrix.modeler.schema.tools.processing.internal.RelationshipProcessorImpl;
import com.metamatrix.modeler.schema.tools.processing.internal.RequestProcessorImpl;
import com.metamatrix.modeler.schema.tools.processing.internal.RequestRelationshipRules;

public class RelationshipProcessorFactory {
	
	/**
	 * Returns a RelationshipProcessor optimized for creating a request table.  All
	 * children and fields are folded into a single table regardless of depth or 
	 * occurance.
	 * @return the RelationshipProcessor
	 */
	public static RelationshipProcessor getRequestProcessor() {
		RelationshipProcessor processor;
		processor  = new RequestProcessorImpl();
		processor.setRelationshipRules(new RequestRelationshipRules(processor));
		return processor;
	}
	
	/**
	 * Returns a RelationshipProcessor configured with the passed thresholds
	 * and the default relationship rules that optimize for a querable model.
	 * @param c_threshold
	 * @param p_threshold
	 * @param f_threshold
	 * @return the RelationshipProcessor
	 */
	public static RelationshipProcessor getQueryOptimizingProcessor(int c_threshold, int p_threshold, int f_threshold) {
		RelationshipProcessor processor = new RelationshipProcessorImpl(c_threshold, p_threshold, f_threshold);
		processor.setRelationshipRules(new DefaultRelationshipRules(processor));
		return processor;
	}
}
