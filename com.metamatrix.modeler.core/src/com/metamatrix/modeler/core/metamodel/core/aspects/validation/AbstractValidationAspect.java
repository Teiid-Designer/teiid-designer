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

package com.metamatrix.modeler.core.metamodel.core.aspects.validation;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.internal.core.validation.ValidationRuleSetImpl;

/**
 * AbstractValidationAspect.java
 */
public abstract class AbstractValidationAspect extends AbstractMetamodelAspect implements ValidationAspect {

	public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.VALIDATION_ASPECT.ID;

	protected ValidationRuleSet ruleSet;

	protected AbstractValidationAspect(final MetamodelEntity entity) {
		super.setMetamodelEntity(entity);
		setID(ASPECT_ID);
	}

	public ValidationRuleSet getValidationRules() {
		return ruleSet;
	}    

	protected void addRule(final ValidationRule rule) {
		ArgCheck.isNotNull(rule);
		if(ruleSet == null) {
			ruleSet = new ValidationRuleSetImpl();
		}
		ruleSet.addRule(rule);
	}

	protected void addRules(final Collection rules) {
		ArgCheck.isNotNull(rules);
		if(rules == null) {
			return;
		}
		Iterator ruleIter = rules.iterator();
		while(ruleIter.hasNext()) {
			ValidationRule rule = (ValidationRule) ruleIter.next();
			addRule(rule);   
		}
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#updateContext(com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	public void updateContext(final EObject eObject, final ValidationContext context) {
		// individual aspects can override this as needed
	}

	/** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        return true;
    }
}
