/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.webservice.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
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
 * GlobalXSDElementRule
 * 
 * @since 4.2
 */
public class GlobalSchemaReferenceRule implements
                                      ObjectValidationRule {

    /**
     * This rule validates that an XSD Element, Simple Type or Complex Type is a globally scoped referenced in a schema.
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
        ArgCheck.isInstanceOf(Message.class, eObject);
        ArgCheck.isNotNull(context);

        final Message message = (Message)eObject;
        // get the content element for the message
        final XSDElementDeclaration xsdElement = message.getContentElement();
        // get the content type for the message
        final XSDSimpleTypeDefinition xsdSimpleType = message.getContentSimpleType();
        // get the content type for the message
        final XSDComplexTypeDefinition xsdComplexType = message.getContentComplexType();

        // create a validation result
        final ValidationResult result = new ValidationResultImpl(eObject);

        if (xsdElement != null && !(xsdElement.eContainer() instanceof XSDSchema)) {
            String msg = null;
            if (eObject instanceof Input) {
                msg = WebServiceMetamodelPlugin.Util.getString("GlobalSchemaReferenceRule.InputXSDElementIsNotGlobal"); //$NON-NLS-1$
            } else {
                msg = WebServiceMetamodelPlugin.Util.getString("GlobalSchemaReferenceRule.OutputXSDElementIsNotGlobal"); //$NON-NLS-1$
            }

            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
        }

        if (xsdSimpleType != null && !(xsdSimpleType.eContainer() instanceof XSDSchema)) {
            String msg = null;
            if (eObject instanceof Input) {
                msg = WebServiceMetamodelPlugin.Util.getString("GlobalSchemaReferenceRule.InputSimpleTypeIsNotGlobal"); //$NON-NLS-1$
            } else {
                msg = WebServiceMetamodelPlugin.Util.getString("GlobalSchemaReferenceRule.OutputSimpleTypeIsNotGlobal"); //$NON-NLS-1$
            }

            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
        }

        if (xsdComplexType != null && !(xsdComplexType.eContainer() instanceof XSDSchema)) {
            String msg = null;
            if (eObject instanceof Input) {
                msg = WebServiceMetamodelPlugin.Util.getString("GlobalSchemaReferenceRule.InputComplexTypeIsNotGlobal"); //$NON-NLS-1$
            } else {
                msg = WebServiceMetamodelPlugin.Util.getString("GlobalSchemaReferenceRule.OutputComplexTypeIsNotGlobal"); //$NON-NLS-1$
            }

            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
        }

        // add the results to the context
        context.addResult(result);
    }
}
