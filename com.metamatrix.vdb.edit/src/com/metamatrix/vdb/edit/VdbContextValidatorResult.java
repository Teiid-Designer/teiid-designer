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
