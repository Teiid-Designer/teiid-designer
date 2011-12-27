/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
