/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.extension.XClass;



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
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(XClass.class, eObject);

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
