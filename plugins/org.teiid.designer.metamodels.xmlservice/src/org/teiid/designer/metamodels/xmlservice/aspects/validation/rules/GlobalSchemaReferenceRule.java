/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xmlservice.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.xmlservice.XmlInput;
import org.teiid.designer.metamodels.xmlservice.XmlMessage;
import org.teiid.designer.metamodels.xmlservice.XmlServiceMetamodelPlugin;


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
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(final EObject eObject,
                         final ValidationContext context) {
        CoreArgCheck.isInstanceOf(XmlMessage.class, eObject);
        CoreArgCheck.isNotNull(context);

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
