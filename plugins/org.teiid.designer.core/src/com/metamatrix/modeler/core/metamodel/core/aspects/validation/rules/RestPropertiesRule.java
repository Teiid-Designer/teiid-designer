/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import static com.metamatrix.modeler.core.ModelerCore.Util;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelObjectAnnotationHelper;

/**
 * Rule to check for valid REST property combinations.
 * 
 * @since 7.5
 */
public final class RestPropertiesRule implements ObjectValidationRule {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RestPropertiesRule.class);

    /**
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 7.5
     */
    @Override
    public void validate( EObject theObject,
                          ValidationContext theContext ) {

        CoreArgCheck.isInstanceOf(Annotation.class, theObject);

        ModelObjectAnnotationHelper moah = new ModelObjectAnnotationHelper();
        Properties props = null;

        try {
            props = moah.getProperties(theObject, "rest:"); //$NON-NLS-1$

            boolean methodSet = false;
            boolean uriSet = false;

            if (props != null && props.size() > 0) {
                // check for valid REST properties
                String method = props.getProperty("rest:restMethod"); //$NON-NLS-1$
                String uri = props.getProperty("rest:uri"); //$NON-NLS-1$

                methodSet = method != null && !method.trim().equals(StringUtilities.EMPTY_STRING) ? true : false;
                uriSet = uri != null && !uri.trim().equals(StringUtilities.EMPTY_STRING) ? true : false;

                String message = StringUtilities.EMPTY_STRING;
                if (methodSet & !uriSet) {
                    message = Util.getString(I18N_PREFIX + "uriRequired"); //$NON-NLS-1$
                }

                if (uriSet & !methodSet) {
                    message = Util.getString(I18N_PREFIX + "methodRequired"); //$NON-NLS-1$
                }

                if (!message.equals(StringUtilities.EMPTY_STRING)) {
                    ValidationResult result = new ValidationResultImpl(theObject, theObject);
                    ValidationProblem problem = new ValidationProblemImpl(IStatus.ERROR, IStatus.ERROR, message);
                    result.addProblem(problem);
                    theContext.addResult(result);
                }
            }

        } catch (RuntimeException theException) {
            ValidationResult result = new ValidationResultImpl(theObject, theObject);
            ValidationProblem problem = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, theException.getLocalizedMessage());
            result.addProblem(problem);
            theContext.addResult(result);
        } catch (ModelerCoreException e) {
            ValidationResult result = new ValidationResultImpl(theObject, theObject);
            ValidationProblem problem = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, e.getLocalizedMessage());
            result.addProblem(problem);
            theContext.addResult(result);
        }
    }
}
