/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * @since 4.2
 */
public class XAttributeMaxOccursRule implements StructuralFeatureValidationRule {

    /** 
     * 
     * @since 4.2
     */
    public XAttributeMaxOccursRule() {
    }

    /** 
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(EStructuralFeature eStructuralFeature, EObject eObject, Object value, ValidationContext context) {
        ArgCheck.isInstanceOf(XAttribute.class, eObject);
        if ( eStructuralFeature.getFeatureID() != ExtensionPackage.XATTRIBUTE__UPPER_BOUND ) {
            return;
        }

        final XAttribute xattribute = (XAttribute) eObject;

        // Make sure the upper bound is 1 or smaller ...
        final int upperBound = xattribute.getUpperBound();
        if ( upperBound > 1 ) {
            final ValidationResult result = new ValidationResultImpl(xattribute);
            final String msg = ModelerCore.Util.getString("XAttributeMaxOccursRule.AttributeUpperBoundMoreThanOneNotSupported"); //$NON-NLS-1$
            final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
            result.addProblem(problem);
            context.addResult(result);
            return;
        }
        
    }

}
