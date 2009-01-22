/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * StringNameRule, rule that validates the string name
 */
public class NullModelTypeRule implements StructuralFeatureValidationRule {
    
    // id of the feature being validated
    private int featureID;    
    
    /**
    * Construct an instance of RuntimeTypeRule.
    * @param featureID ID of the feature to validate 
    */
   public NullModelTypeRule(int featureID)  {
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

        // Check that the EObject is an instanceof ModelAnnotation
        // otherwise we cannot apply this rule
        if (eObject == null || !(eObject instanceof ModelAnnotation)) {
            return;
        }

        ValidationResult result = new ValidationResultImpl(eObject,eObject.eResource());
        
        // The model type reference cannot be null
        if (value == null) {
            // create validation problem and add it to the result
            final URI uri = eObject.eResource().getURI();
            Object[] params = new Object[]{URI.decode(uri.toString())};
            String msg = ModelerCore.Util.getString("NullModelTypeRule.The_model_type_value_may_not_be_null_1",params); //$NON-NLS-1$
            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
            context.addResult(result);
            return;
        }
    }

}
