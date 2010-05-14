/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.WebServiceMetamodelPlugin;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * MessageContentRule
 * @since 4.2
 */
public class MessageContentRule implements ObjectValidationRule {

    /** 
     * MessageContentRule
     * @since 4.2
     */
    public MessageContentRule() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isInstanceOf(Message.class, eObject);
        CoreArgCheck.isNotNull(context);
        
        final Message message = (Message) eObject;
        // get the content element for the message
        final XSDElementDeclaration xsdElement = message.getContentElement();
        // get the content type for the message
        final XSDSimpleTypeDefinition xsdSimpleType = message.getContentSimpleType();
        // get the content type for the message
        final XSDComplexTypeDefinition xsdComplexType = message.getContentComplexType();

        // create a validation result
        final ValidationResult result = new ValidationResultImpl(eObject);
        if(xsdElement == null && xsdSimpleType == null && xsdComplexType == null) {
            String msg = null;
            if ( eObject instanceof Input ) {
                msg = WebServiceMetamodelPlugin.Util.getString("MessageContentRule.InputMissingTypeAndElement"); //$NON-NLS-1$
            } else {
                msg = WebServiceMetamodelPlugin.Util.getString("MessageContentRule.OutputMissingTypeAndElement"); //$NON-NLS-1$
            }
            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR,msg);
            result.addProblem(problem);
        } else if( (xsdElement != null && xsdSimpleType != null ) || 
                    (xsdElement != null && xsdComplexType != null ) ||
                    (xsdSimpleType != null && xsdComplexType != null ) ) {
            String msg = null;
            if ( eObject instanceof Input ) {
                msg = WebServiceMetamodelPlugin.Util.getString("MessageContentRule.InputHasSimpleTypeAndComplexTypeAndElement"); //$NON-NLS-1$
            } else {
                msg = WebServiceMetamodelPlugin.Util.getString("MessageContentRule.OutputHasSimpleTypeAndComplexTypeAndElement"); //$NON-NLS-1$
            }
            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR,msg);
            result.addProblem(problem);            
        } else {
            // There is only one set ...
            if ( xsdSimpleType != null ) {
                // Denote that a message defined by a simple type is not a valid XML document ...
                String msg = null;
                if ( eObject instanceof Input ) {
                    msg = WebServiceMetamodelPlugin.Util.getString("MessageContentRule.InputHasSimpleType"); //$NON-NLS-1$
                } else {
                    msg = WebServiceMetamodelPlugin.Util.getString("MessageContentRule.OutputHasSimpleType"); //$NON-NLS-1$
                }
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR,msg);
                result.addProblem(problem);            
            }
        }
        // add the results to the context
        context.addResult(result);
    }
}
