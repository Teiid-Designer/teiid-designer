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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

/** 
 * MetamodelEntityBuilder interface
 */
public interface MetamodelEntityBuilder {
	
    /**
     * Create a Metamodel entity from the supplied record - Using the record the entity
     * will be created, added to the appropriate parent and have the given propery values set.
     * @param MetamodelEntityRecord the record description of the entity to be created.
     * @return EObject the generated EObject
     */
    public EObject create(MetamodelEntityRecord entityRecord, IProgressMonitor monitor);  

    /**
     * Create list of entities from the supplied record List
     * @param entityRecords the list of records describing the entities to be created.
     * @return List the generated EObjects
     */
    public List create(List entityRecords, IProgressMonitor monitor);  

}
