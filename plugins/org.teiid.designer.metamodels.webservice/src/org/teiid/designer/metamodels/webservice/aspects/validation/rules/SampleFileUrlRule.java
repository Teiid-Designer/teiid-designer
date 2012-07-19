/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.validation.rules;

import java.net.URL;
import java.net.URLConnection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.designer.core.validation.StructuralFeatureValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;


/**
 * @since 8.0
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
     * @see org.teiid.designer.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature,
     *      org.eclipse.emf.ecore.EObject, java.lang.Object, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
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
