/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.resource.Resource;


/** 
 * @since 5.0
 */
public interface VdbContextValidatorResult {
    
    final int RESOURCE_VALIDATION_ERROR_CODE = 100001;
    
    /**
     * Return any problems associated with validating the specified resource.  If
     * the resource has no validation errors, warnings, or info status then
     * an empty array is returned.
     * @param model the model to retrieve problems for; may not be null
     * @return array of IStatus instances;  
     * @since 5.0
     */
    IStatus[] getProblems(Resource model);

}
