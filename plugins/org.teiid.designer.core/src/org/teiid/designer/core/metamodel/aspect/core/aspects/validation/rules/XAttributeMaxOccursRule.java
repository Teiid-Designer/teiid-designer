/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.StructuralFeatureValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.extension.ExtensionPackage;
import org.teiid.designer.metamodels.core.extension.XAttribute;



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
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(EStructuralFeature eStructuralFeature, EObject eObject, Object value, ValidationContext context) {
        CoreArgCheck.isInstanceOf(XAttribute.class, eObject);
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
