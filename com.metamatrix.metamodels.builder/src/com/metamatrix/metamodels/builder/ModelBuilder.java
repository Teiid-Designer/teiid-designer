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

package com.metamatrix.metamodels.builder;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;


/** 
 * ModelBuilder interface
 */
public interface ModelBuilder {
	
    /**
     * Create a resource from the supplied record
     * @param modelRecord the record description of the model to be created.
     * @return Resource the generated resource
     */
    public Resource create(ModelRecord modelRecord);  
    
    /**
     * Create list of resources from the supplied record List
     * @param modelRecords the list of records describing the models to be created.
     * @return List the generated resources
     */
    public List create(List modelRecords);  
}
