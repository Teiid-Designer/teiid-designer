/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.core.ModelerCore;


/**
 * ValidationRuleSet
 */
public class ValidationRuleSetImpl implements ValidationRuleSet {

    private static final String EXCEPTION_DURING_VALIDATION_MSG     = ModelerCore.Util.getString("ValidationRuleSetImpl.An_exception_was_encountered_during_validation._Check_the_log_for_details._1"); //$NON-NLS-1$
    private static final String EXCEPTION_DURING_VALIDATION_LOG_MSG = ModelerCore.Util.getString("ValidationRuleSetImpl.An_exception_was_encountered_during_validation._2"); //$NON-NLS-1$

    private static final ValidationRule[] EMPTY_ARRAY = new ValidationRule[0];
    private ArrayList ruleSet;

    /* (non-Javadoc)
     * @See org.teiid.designer.core.validation.ValidationRuleSet#addRule(org.teiid.designer.core.validation.ValidationRule)
     */
    public void addRule(final ValidationRule rule) {
        if (rule == null) {
            return;
        }
        if (ruleSet == null) {
            ruleSet = new ArrayList(5);
        }
        boolean exists = false;
		for (Object existingRule : ruleSet) {
			if (existingRule == rule) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			ruleSet.add(rule);
        }
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.validation.ValidationRuleSet#getRules()
     */
    public ValidationRule[] getRules() {
        if ( !hasRules() ) {
            return EMPTY_ARRAY;
        }
        ValidationRule[] result = new ValidationRule[ruleSet.size()];
        ruleSet.toArray(result);
        return result;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.validation.ValidationRuleSet#hasRules()
     */
    public boolean hasRules() {
        return (ruleSet != null && ruleSet.size() > 0);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.validation.ValidationRuleSet#validate(java.lang.Object, org.teiid.designer.core.validation.ValidationContext)
     */
    public void validate(final IProgressMonitor progressMonitor, final Object object, final ValidationContext context) {
        if ( !hasRules() ) {
            return;
        }
		final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();

        try {
            // Run the ObjectValidationRule set
            for (int i = 0, n = ruleSet.size(); i < n; i++) {
            	if(monitor.isCanceled()) {
            		return;
            	}

                // see if context already has a fatal error for this object or it's resource.
                // if it does don't validate object further
                if (object instanceof EObject) {
                    Collection results = context.getTargetResults((EObject)object);

                    if ((results != null) && !results.isEmpty()) {
                        Iterator itr = results.iterator();

                        while (itr.hasNext()) {
                            ValidationResult result = (ValidationResult)itr.next();

                            if (result.isFatalResource() || result.isFatalObject(object)) {
                                return;
                            }
                        }
                    }
                }

				final ValidationRule rule = (ValidationRule)ruleSet.get(i);
				if(object instanceof EObject) {
					final EObject target = (EObject)object;
	                if (rule instanceof ObjectValidationRule) {
	                    final ObjectValidationRule objRule = (ObjectValidationRule) rule;
	                    // Apply this rule to the EObject
	                    objRule.validate(target,context);
	                // Run the StructuralFeatureValidationRule set
	                } else if (rule instanceof StructuralFeatureValidationRule) {
	                    final StructuralFeatureValidationRule featureRule = (StructuralFeatureValidationRule) rule;
	                    // Apply this rule to all features associated with this EObject
	                    final List features = target.eClass().getEAllStructuralFeatures();
	                    for (Iterator iter = features.iterator(); iter.hasNext();) {
	                        // Apply this rule to the EObject
	                        EStructuralFeature feature = (EStructuralFeature)iter.next();

	                        // don't validate volatile or transit features. validating these types of features
	                        // took enormous amounts of time (see Defect 15699). Since these features
	                        // are normally derived and/or not persisted, not validating them usually won't
	                        // be a problem. readdress if it becomes a problem.
	                        if (!feature.isVolatile() || !feature.isTransient()) {
							    featureRule.validate(feature, target, target.eGet(feature), context);
							}
	                    }
	                }
				} else if(object instanceof Resource) {
					final Resource resource = (Resource)object;
					if (rule instanceof ResourceValidationRule) {
						final ResourceValidationRule rscRule = (ResourceValidationRule) rule;
						// Apply this rule to the resource
						rscRule.validate(resource,context);
					}
				}
            }
        } catch (Throwable e) {
            String errMsg = e.getMessage();
            if (errMsg != null && errMsg.length() > 0) {
                addProblem(object, 0, IStatus.ERROR, e.getMessage(), context);
            } else {
                addProblem(object, 0, IStatus.ERROR, EXCEPTION_DURING_VALIDATION_MSG, context);
                ModelerCore.Util.log(IStatus.ERROR,e,EXCEPTION_DURING_VALIDATION_LOG_MSG);
            }
        }
    }

    private void addProblem(final Object object, final int code, final int severity,
                            final String msg,  final ValidationContext context) {
        ValidationProblem problem  = new ValidationProblemImpl(code, severity, msg);
        ValidationResult result = new ValidationResultImpl(object);
        result.addProblem(problem);
        context.addResult(result);
    }
}
