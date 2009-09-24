/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * @since 4.2
 */
public class XClassExtendedClassRule implements ObjectValidationRule {

    /** 
     * 
     * @since 4.2
     */
    public XClassExtendedClassRule() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(XClass.class, eObject);

        final XClass xclass = (XClass) eObject;
        //final Resource resource = xpackage.eResource();
        //if (resource == null) {
        //    return;
        //}
        
        // Make sure the extended class is set ...
        final EClass extendedClass = xclass.getExtendedClass();
        if ( extendedClass == null ) {
            final ValidationResult result = new ValidationResultImpl(xclass);
            final String msg = ModelerCore.Util.getString("XClassExtendedClassRule.MissingExtendedClass"); //$NON-NLS-1$
            final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
            result.addProblem(problem);
            context.addResult(result);
        }
        
    }

}
