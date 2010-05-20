/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.CoreArgCheck;
import org.teiid.core.util.LRUCache;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.core.validation.rules.EObjectUuidRule;
import com.metamatrix.modeler.core.validation.rules.EmfResourceValidationRule;
import com.metamatrix.modeler.core.validation.rules.ModelFileExtensionRule;
import com.metamatrix.modeler.core.validation.rules.MultiplicityRule;
import com.metamatrix.modeler.core.validation.rules.ResourceInScopeValidationRule;
import com.metamatrix.modeler.core.validation.rules.TargetTransformationRule;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * ValidationRuleManager
 */
public class ValidationRuleManager {

    private static final ValidationRule MULTIPLICITY_RULE = new MultiplicityRule();
    private static final ValidationRule FILE_EXTENSION_RULE = new ModelFileExtensionRule();
    private static final ValidationRule TABLE_TRANFORMATION_RULE = new TargetTransformationRule();
    private static final ValidationRule EOBJECT_UUID_RULE = new EObjectUuidRule();
    private static final ValidationRule EMF_RESOURCE_RULE = new EmfResourceValidationRule();
    private static final ValidationRule RESOURCE_IN_SCOPE_RULE = new ResourceInScopeValidationRule();
    private LRUCache cache;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================    

    /**
     * Construct an instance of ValidationRuleManager.
     * 
     */
    public ValidationRuleManager() {
        this.cache = new LRUCache();
    }

    /**
     * Register a {@link com.metamatrix.modeler.core.validation.ValidationRuleSet} for
     * the specified EClass instance to use as the applicable set of rules
     * executed during validation.
     * @param eClass the {@link org.eclipse.emf.ecore.EClass} instance - may not be null
     * @param ruleSet the set of validation rules to associate with this EClass - may not be null.
     */
    public void addRuleSet(final EClass eClass, final ValidationRuleSet ruleSet) {
        CoreArgCheck.isNotNull(eClass);
        CoreArgCheck.isNotNull(ruleSet);

        this.cache.put(eClass,ruleSet);
    }

    /**
     * Register a {@link com.metamatrix.modeler.core.validation.ValidationRuleSet} for
     * the specified Resource instance to use as the applicable set of rules
     * executed during validation.
     * @param eResource the {@link org.eclipse.emf.ecore.resource.Resource} instance - may not be null
     * @param ruleSet the set of validation rules to associate with this Resource - may not be null.
     */
    public void addRuleSet(final Resource eResource, final ValidationRuleSet ruleSet) {
        CoreArgCheck.isNotNull(eResource);
        CoreArgCheck.isNotNull(ruleSet);

        this.cache.put(eResource.getClass(),ruleSet);
    }

    /**
     * Return the {@link com.metamatrix.modeler.core.validation.ValidationRuleSet}
     * for object to use during validation, may be null
     * @param eObject the model entity to obtain validation rules for - may not be null.
     * @return the validation rule set
     */
    public ValidationRuleSet getRuleSet(final Object object, final ValidationContext context) {

		ValidationRuleSet ruleSet = null;
		if(object instanceof EObject) {
			final ValidationAspect validationAspect = AspectManager.getValidationAspect((EObject)object);
			if(validationAspect != null) {			
				validationAspect.updateContext((EObject)object, context);
			}

			// if there is no validation aspect then apply the core rules, if there is a validationaspect
			// and it says the eobject should be validated apply all rules.
			if(validationAspect == null || validationAspect.shouldValidate((EObject)object, context)) {
		        final EClass key = ((EObject)object).eClass();
		        // Retrieve the rule set from the cache
		        ruleSet = (ValidationRuleSet) this.cache.get(key);
				// If the rule set does not exist then retrieve it from the
				// MetamodelAspect associated with that object
				if (ruleSet == null) {
					if (validationAspect != null) {
						ruleSet = validationAspect.getValidationRules();
					}
					// If the rule set is still null, then the metamodel didn't provide any validation rules
					// for the metaclass, so create an empty rule set
					if ( ruleSet == null ) {
						ruleSet = new ValidationRuleSetImpl();
					}
					// add all the core rules
					addCoreRules(ruleSet);
					// and put it into the cache                
					addRuleSet(key,ruleSet);            
				}
			} else {
			    if(ModelerCore.DEBUG_VALIDATION) {
			        ModelerCore.Util.log(IStatus.INFO, "Excluding from validation:"+object); //$NON-NLS-1$
			    }
			}
		} else if (object instanceof Resource){
            final Class key = object.getClass();
            
            // Retrieve the rule set from the cache
            ruleSet = (ValidationRuleSet) this.cache.get(key);
            
            // If the rule set does not exist then recreate it
            if (ruleSet == null) {
                ruleSet = new ValidationRuleSetImpl();
                // add the appropriate resource rules to the set
                addResourceRules((Resource)object, ruleSet);
                // and put it into the cache                
                addRuleSet((Resource)object,ruleSet);            
            }
		}
        return ruleSet;
    }

    /**
     * Add all the core rules to the ruleSet.
     * @param ruleSet ValidationRuleSet object to be updated.
     */
    private void addCoreRules(ValidationRuleSet ruleSet) {
        ruleSet.addRule(RESOURCE_IN_SCOPE_RULE);
    	ruleSet.addRule(MULTIPLICITY_RULE);
		ruleSet.addRule(EOBJECT_UUID_RULE);
    }
    
    /**
     * Add all the resource rules to the ruleSet.
     * @param ruleSet ValidationRuleSet object to be updated.
     */
    private void addResourceRules(final Resource eResource, final ValidationRuleSet ruleSet) {
        // Customize the rule set based on the type of Resource we are validating
        if (eResource instanceof EmfResource) {
            ruleSet.addRule(FILE_EXTENSION_RULE);
            ruleSet.addRule(TABLE_TRANFORMATION_RULE);
            ruleSet.addRule(EMF_RESOURCE_RULE);
        } else if (eResource instanceof XSDResourceImpl) {
            //ruleSet.addRule(FILE_EXTENSION_RULE);
        }
    }     

}
