/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlMessage;
import com.metamatrix.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * GlobalXSDElementRule
 * 
 * @since 4.2
 */
public class GlobalSchemaReferenceRule implements ObjectValidationRule {

    /**
     * This rule validates that an XSD Element is a globally scoped referenced in a schema.
     * 
     * @since 4.2
     */
    public GlobalSchemaReferenceRule() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final EObject eObject,
                         final ValidationContext context) {
        ArgCheck.isInstanceOf(XmlMessage.class, eObject);
        ArgCheck.isNotNull(context);

        final XmlMessage message = (XmlMessage)eObject;
        // get the content element for the message
        final XSDElementDeclaration xsdElement = message.getContentElement();

        // create a validation result
        final ValidationResult result = new ValidationResultImpl(eObject);

        if (xsdElement != null && !(xsdElement.eContainer() instanceof XSDSchema)) {
            String msg = null;
            if (eObject instanceof XmlInput) {
                msg = XmlServiceMetamodelPlugin.Util.getString("GlobalSchemaReferenceRule.InputXSDElementIsNotGlobal"); //$NON-NLS-1$
            } else {
                msg = XmlServiceMetamodelPlugin.Util.getString("GlobalSchemaReferenceRule.OutputXSDElementIsNotGlobal"); //$NON-NLS-1$
            }

            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
        }

        // add the results to the context
        context.addResult(result);
    }
}
