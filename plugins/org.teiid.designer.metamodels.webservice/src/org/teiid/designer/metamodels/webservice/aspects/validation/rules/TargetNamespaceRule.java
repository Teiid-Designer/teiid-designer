/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDComponent;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.webservice.Message;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;


/**
 * @since 4.3
 */
public final class TargetNamespaceRule implements
                                      ObjectValidationRule {

    // ===========================================================================================================================
    // Constants

    private static final String NO_TARGET_NAMESPACE_KEY = WebServiceMetamodelPlugin.Util.getString("TargetNamespaceRule.noTargetNamespace"); //$NON-NLS-1$

    // ===========================================================================================================================
    // Controller Methods

    /**
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      org.teiid.designer.core.validation.ValidationContext)
     * @since 4.3
     */
    public void validate(final EObject eObject,
                         final ValidationContext context) {
        CoreArgCheck.isInstanceOf(Message.class, eObject);
        CoreArgCheck.isNotNull(context);

        final Message msg = (Message)eObject;
        XSDComponent comp = msg.getContentElement();
        if (comp == null) {
            comp = msg.getContentSimpleType();
            if (comp == null) {
                comp = msg.getContentComplexType();
                if (comp == null) {
                    return;
                }
            }
        }
        if (comp.getSchema().getTargetNamespace() == null) {
            final ValidationResult result = new ValidationResultImpl(eObject);
            result.addProblem(new ValidationProblemImpl(0, IStatus.ERROR, NO_TARGET_NAMESPACE_KEY));
            context.addResult(result);
        }
    }

}
