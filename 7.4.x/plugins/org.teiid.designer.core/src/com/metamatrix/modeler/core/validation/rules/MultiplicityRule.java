/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * MultiplicityRule, this is a core rule that needs to be run for all features, this rule checks the multiplicity of a feature
 * with the upper and lower bounds of multiplicity allowed for that feature.
 */
public class MultiplicityRule implements StructuralFeatureValidationRule {

    private static final String NONE_STRING = ModelerCore.Util.getString("MultiplicityRule.NO_VALUE_STRING"); //$NON-NLS-1$
    private static final String ONE_VALUE_STRING = ModelerCore.Util.getString("MultiplicityRule.ONE_VALUE_STRING"); //$NON-NLS-1$

    public MultiplicityRule() {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature, org.eclipse.emf.ecore.EObject, java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate( EStructuralFeature eStructuralFeature,
                          EObject eObject,
                          Object value,
                          ValidationContext context ) {

        // get the upper and lower bounds on this feature
        int lowerBound = eStructuralFeature.getLowerBound();
        int upperBound = eStructuralFeature.getUpperBound();

        // get the total number og references for this feature
        int featureReferences = 0;
        if (value instanceof EList) {
            EList eList = (EList)value;
            featureReferences = eList.size();
        } else if (value != null) {
            featureReferences = 1;
        }

        // Compare the lower and upper bounds with the actual
        final boolean lowerViolated = lowerBound != ETypedElement.UNBOUNDED_MULTIPLICITY && featureReferences < lowerBound;
        final boolean upperViolated = upperBound != ETypedElement.UNBOUNDED_MULTIPLICITY && featureReferences > upperBound;
        if (lowerViolated || upperViolated) {
            String msg = null;
            // Handle the case when lowerBound == upperBound == 1
            if (lowerBound == 1 && upperBound == 1) {
                final String featureName = eStructuralFeature.getName();
                msg = ModelerCore.Util.getString("MultiplicityRule.Missing_value", featureName); //$NON-NLS-1$
            } else {

                // Get the parameter names for the message ...
                final String featureName = eStructuralFeature.getName();
                final String className = ModelerCore.getMetamodelRegistry().getMetaClassLabel(eObject.eClass());
                String oppositeName = null;
                boolean containment = false;
                if (eStructuralFeature instanceof EReference) {
                    final EReference eRef = (EReference)eStructuralFeature; // this reference
                    final EReference oppositeRef = eRef.getEOpposite(); // opposite reference
                    if (oppositeRef != null) {
                        oppositeName = oppositeRef.getContainerClass().getName();
                        containment = oppositeRef.isContainment();
                    }
                }

                String actual = NONE_STRING;
                if (featureReferences == 0) {
                    actual = NONE_STRING;
                } else if (featureReferences == 1) {
                    actual = ONE_VALUE_STRING;
                } else {
                    actual = ModelerCore.Util.getString("MultiplicityRule.MULTI_VALUE_STRING", featureReferences); //$NON-NLS-1$
                }

                // Figure out which message to use ...
                if (lowerViolated) {
                    if (oppositeName != null) {
                        if (containment) {
                            if (upperBound == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                                final Object[] params = new Object[] {className, new Integer(lowerBound), featureName, actual};
                                msg = ModelerCore.Util.getString("MultiplicityRule.The_METACLASSNAME_must_contain_LOWERBOUND_or_more_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                            } else {
                                final Object[] params = new Object[] {className, new Integer(lowerBound),
                                    new Integer(upperBound), featureName, actual};
                                msg = ModelerCore.Util.getString("MultiplicityRule.The_METACLASSNAME_must_contain_between_LOWERBOUND_and_UPPERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                            }
                        } else {// No containment ...
                            if (upperBound == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                                final Object[] params = new Object[] {className, new Integer(lowerBound), featureName, actual};
                                msg = ModelerCore.Util.getString("MultiplicityRule.The_METACLASSNAME_must_reference_LOWERBOUND_or_more_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                            } else {
                                final Object[] params = new Object[] {className, new Integer(lowerBound),
                                    new Integer(upperBound), featureName, actual};
                                msg = ModelerCore.Util.getString("MultiplicityRule.The_METACLASSNAME_must_reference_between_LOWERBOUND_and_UPPERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                            }
                        }
                    } else {
                        // There is no opposite (and thus no containment) ...
                        if (upperBound == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                            final Object[] params = new Object[] {new Integer(lowerBound), featureName, actual};
                            msg = ModelerCore.Util.getString("MultiplicityRule.There_must_be_LOWERBOUND_or_more_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                        } else {
                            final Object[] params = new Object[] {new Integer(lowerBound), new Integer(upperBound), featureName,
                                actual};
                            msg = ModelerCore.Util.getString("MultiplicityRule.There_must_be_between_LOWERBOUND_and_UPPERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                        }
                    }
                } else if (upperViolated) {
                    if (oppositeName != null) {
                        if (containment) {
                            if (lowerBound == 0) {
                                final Object[] params = new Object[] {className, new Integer(upperBound), featureName, actual};
                                msg = ModelerCore.Util.getString("MultiplicityRule.The_METACLASSNAME_must_contain_no_more_than_UPPERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                            } else {
                                final Object[] params = new Object[] {className, new Integer(lowerBound),
                                    new Integer(upperBound), featureName, actual};
                                msg = ModelerCore.Util.getString("MultiplicityRule.The_METACLASSNAME_must_contain_between_LOWERBOUND_and_UPPERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                            }
                        } else {// No containment ...
                            if (lowerBound == 0) {
                                final Object[] params = new Object[] {className, new Integer(upperBound), featureName, actual};
                                msg = ModelerCore.Util.getString("MultiplicityRule.The_METACLASSNAME_must_reference_no_more_than_UPPERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                            } else {
                                final Object[] params = new Object[] {className, new Integer(lowerBound),
                                    new Integer(upperBound), featureName, actual};
                                msg = ModelerCore.Util.getString("MultiplicityRule.The_METACLASSNAME_must_reference_between_LOWERBOUND_and_UPPERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                            }
                        }
                    } else {
                        // There is no opposite (and thus no containment) ...
                        if (lowerBound == 0) {
                            final Object[] params = new Object[] {new Integer(upperBound), featureName, actual};
                            msg = ModelerCore.Util.getString("MultiplicityRule.There_must_be_less_than_LOWERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                        } else {
                            final Object[] params = new Object[] {new Integer(lowerBound), new Integer(upperBound), featureName,
                                actual};
                            msg = ModelerCore.Util.getString("MultiplicityRule.There_must_be_between_LOWERBOUND_and_UPPERBOUND_FEATURENAME_when_there_are_ACTUAL", params); //$NON-NLS-1$
                        }
                    }
                }
            }
            CoreArgCheck.isNotNull(msg);

            // create validation problem and addit to the resuls
            ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            ValidationResult result = new ValidationResultImpl(eObject);
            result.addProblem(problem);
            context.addResult(result);
        }
    }

}
