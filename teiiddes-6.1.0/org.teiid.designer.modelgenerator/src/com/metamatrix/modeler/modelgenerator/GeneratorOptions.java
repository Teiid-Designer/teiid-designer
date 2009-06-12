/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator;

import org.eclipse.core.runtime.IStatus;

/**
 * GeneratorOptions
 */
public interface GeneratorOptions {

    /**
     * Validate the current option settings, and return an {@link IStatus} denoting whether
     * additional options must be set and whether any current settings are invalid.
     * <p>
     * The resulting IStatus should return true for {@link IStatus#isOK()} with {@link IStatus#getSeverity() severity} 
     * of {@link IStatus#INFO INFO} if the current option settings are considered valid.  
     * Otherwise, the {@link IStatus#getSeverity() severity} should be:
     * <ul>
     *  <li>{@link IStatus#WARNING WARNING} if the current settings may be used, but also may
     *      cause unexpected or unanticipated results.</li>
     *  <li>{@link IStatus#WARNING ERROR} if the current settings may not be used as is and must
     *      be corrected.</li>
     * </ul> 
     * </p>
     * @return an IStatus denoting whether the current option settings are considered valid.
     */
    IStatus validate();

}
