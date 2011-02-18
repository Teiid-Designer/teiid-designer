/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * ImportsAspect
 */
public interface ImportsAspect extends MetamodelAspect {

    /**
     * Get the location of the model import to the model this EObject
     * belongs. 
     * @param eObject The EObject that is contained in the resource
     * that contains that whose import info is returned. 
     * @return The location for the modelImport.
     */
    String getModelLocation(EObject eObject);

	/**
	 * Get the IPath for the model import to the model this EObject
	 * belongs. 
	 * @param eObject The EObject that is contained in the resource
	 * that contains that whose import info is returned. 
	 * @return The IPath for the modelImport.
	 */
	IPath getModelPath(EObject eObject);

	/**
	 * Get the uuid of the imported model.
	 * @param eObject The <code>EObject</code> 
	 * @return uuid of the imported model.
	 */
	String getModelUuid(EObject eObject);

	/**
	 * Get the metamodel URI of the imported model.
	 * @param eObject The <code>EObject</code> 
	 * @return The uri of the metamodel
	 */
	String getPrimaryMetaModelUri(EObject eObject);

	/**
	 * Get the modelType of the imported model.
	 * @param eObject The <code>EObject</code> 
	 * @return model of the imported model.
	 */
	String getModelType(EObject eObject);
	
//	/**
//	 * Set the path on the imported model.
//	 * @param eObject The EObject that is contained in the resource
//	 * that contains that whose import info
//	 * @param modelPath The path to be set on the imported model.
//	 */
//	void setModelPath(EObject eObject, IPath modelPath);
    
    /**
     * Set the model location on the imported model.
     * @param eObject The EObject that is contained in the resource
     * that contains that whose import info
     * @param uri The URI for the imported model.
     */
    void setModelLocation(EObject eObject, URI uri);

}
