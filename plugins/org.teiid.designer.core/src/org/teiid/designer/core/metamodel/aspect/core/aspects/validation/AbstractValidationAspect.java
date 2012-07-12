/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.ValidationAspect;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.core.validation.ValidationRuleSet;
import org.teiid.designer.core.validation.ValidationRuleSetImpl;


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
	 * @See org.teiid.designer.core.metamodel.aspect.ValidationAspect#updateContext(org.teiid.designer.core.validation.ValidationContext)
	 */
	public void updateContext(final EObject eObject, final ValidationContext context) {
		// individual aspects can override this as needed
	}

	/** 
     * @see org.teiid.designer.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        return true;
    }
}
