/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * ValidationRuleSet
 */
public interface ValidationRuleSet {
    
    /**
     * Add a {@link ValidationRule} instance to the set of rules
     * @param rule
     */
    void addRule(ValidationRule rule);
    
    /**
     * Return the array of {@link ValidationRule} instances for this set
     */
    ValidationRule[] getRules();
    
    /**
     * Returns true if the rule set is not null or empty
     */
    boolean hasRules();
    
    /**
     * Execute the validation rule set
     * @param eObject
     * @param context
     * @param progressMonitor
     */
    void validate(IProgressMonitor progressMonitor, Object eObject, ValidationContext context);

}
