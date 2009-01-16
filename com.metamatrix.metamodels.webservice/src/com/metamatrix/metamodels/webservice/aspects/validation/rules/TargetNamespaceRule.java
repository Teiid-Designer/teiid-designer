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
import org.eclipse.xsd.XSDComponent;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.WebServiceMetamodelPlugin;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

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
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.3
     */
    public void validate(final EObject eObject,
                         final ValidationContext context) {
        ArgCheck.isInstanceOf(Message.class, eObject);
        ArgCheck.isNotNull(context);

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
