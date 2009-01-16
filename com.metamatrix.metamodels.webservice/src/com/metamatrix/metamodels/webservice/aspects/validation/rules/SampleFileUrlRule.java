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

import java.net.URL;
import java.net.URLConnection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.metamodels.webservice.WebServiceMetamodelPlugin;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * @since 4.2
 */
public class SampleFileUrlRule implements StructuralFeatureValidationRule {

    // id of the feature being validated
    private final int featureID;

    /**
     * Construct an instance of SampleFileUrlRule.
     * 
     * @param featureID ID of the feature to validate
     */
    public SampleFileUrlRule( final int featureID ) {
        this.featureID = featureID;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature,
     *      org.eclipse.emf.ecore.EObject, java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate( final EStructuralFeature eStructuralFeature,
                          final EObject eObject,
                          final Object value,
                          final ValidationContext context ) {
        // check if the feature matches the given feature
        if (eStructuralFeature.getFeatureID() != this.featureID) {
            return;
        }

        // Check that the value is an instance of String(url)
        // otherwise we cannot apply this rule
        if (!(value instanceof String)) {
            return;
        }

        // Apply the length validation to the string
        final String urlString = (String)value;

        boolean validUrl = false;
        URLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = url.openConnection();
        } catch (Exception e) {
        }// ignore
        if (connection != null) {
            validUrl = true;
        }

        if (!validUrl) {
            final String msg = WebServiceMetamodelPlugin.Util.getString("SampleFileUrlRule.SampleFileHasInvalidUrl"); //$NON-NLS-1$
            ValidationResult validationResult = new ValidationResultImpl(eObject);
            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            validationResult.addProblem(problem);
            context.addResult(validationResult);
        }
    }

}
