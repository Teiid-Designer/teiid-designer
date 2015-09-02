/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import static org.teiid.designer.core.ModelerCore.Util;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.workspace.ModelObjectAnnotationHelper;
import org.teiid.designer.metamodels.core.Annotation;


/**
 * Rule to check for valid REST property combinations.
 * 
 * @since 8.0
 */
public final class RestPropertiesRule implements ObjectValidationRule {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RestPropertiesRule.class);

    /**
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      org.teiid.designer.core.validation.ValidationContext)
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

                methodSet = method != null && !method.trim().equals(StringConstants.EMPTY_STRING) ? true : false;
                uriSet = uri != null && !uri.trim().equals(StringConstants.EMPTY_STRING) ? true : false;

                String message = StringConstants.EMPTY_STRING;
                if (methodSet & !uriSet) {
                    message = Util.getString(I18N_PREFIX + "uriRequired"); //$NON-NLS-1$
                }

                if (uriSet & !methodSet) {
                    message = Util.getString(I18N_PREFIX + "methodRequired"); //$NON-NLS-1$
                }

                if (!message.equals(StringConstants.EMPTY_STRING)) {
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
