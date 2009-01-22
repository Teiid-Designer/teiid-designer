/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
