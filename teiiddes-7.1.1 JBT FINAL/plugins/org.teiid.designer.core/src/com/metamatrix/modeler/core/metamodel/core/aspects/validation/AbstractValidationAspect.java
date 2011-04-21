/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
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
		CoreArgCheck.isNotNull(rule);
		if(ruleSet == null) {
			ruleSet = new ValidationRuleSetImpl();
		}
		ruleSet.addRule(rule);
	}

	protected void addRules(final Collection rules) {
		CoreArgCheck.isNotNull(rules);
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
