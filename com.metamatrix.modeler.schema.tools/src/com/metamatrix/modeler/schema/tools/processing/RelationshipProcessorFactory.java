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
