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
