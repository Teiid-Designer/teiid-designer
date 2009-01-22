/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.emf.ecore.resource.Resource;


/** 
 * @since 5.0
 */
public interface VdbContextValidator {

    /**
     * Validate the specified array of resources. 
     * @param monitor the progress monitor; may be null
     * @param models the resources to validate; may not be null
     * @return validation result
     * @since 5.0
     */
    VdbContextValidatorResult validate(IProgressMonitor monitor, Resource[] models);
    
}
