/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.metamodels.xsd.XsdPlugin;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * StringNameRule, rule that validates the string name
 */
public class ItemTypeRule implements StructuralFeatureValidationRule {
    
    // id of the feature being validated
    private int featureID;    
    
    /**
    * Construct an instance of ItemTypeRule.
    * @param featureID ID of the feature to validate 
    */
   public ItemTypeRule(int featureID)  {
       this.featureID = featureID;
   }    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.validation.ValidationRule#validate(java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EStructuralFeature eStructuralFeature, EObject eObject, Object value, ValidationContext context) {
        // check if the feature matches the given feature
        if (eStructuralFeature.getFeatureID() != this.featureID) {
            return;
        }

        // Check that the EObject is an instanceof SimpleDatatype
        // otherwise we cannot apply this rule
        if (eObject == null || !(eObject instanceof XSDSimpleTypeDefinition)) {
            return;
        }
        ValidationResult result = new ValidationResultImpl(eObject);
        
        // The itemtype reference may be null - if so return
        if (value == null) {
            return;
        }

        // Check that the value is an instance of XSDSimpleTypeDefinition
        // otherwise we cannot apply this rule
        if (!(value instanceof XSDSimpleTypeDefinition)) {
            // create validation problem and add it to the result
            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, XsdPlugin.Util.getString("ItemTypeRule.The_itemtype_must_be_a_simple_type_definition_2")); //$NON-NLS-1$
            result.addProblem(problem);
            context.addResult(result);
            return;
        }

        // Check that the basetype is not the same instance as the
        // basetype (i.e. a datatype cannot have itself as its basetype)
        if (eObject == value) {
            // create validation problem and add it to the result
            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, XsdPlugin.Util.getString("ItemTypeRule.The_datatype_cannot_have_itself_as_its_itemtype_1")); //$NON-NLS-1$
            result.addProblem(problem);
            context.addResult(result);
            return;
        }

		// add the result to the context
		context.addResult(result);
    }

}
