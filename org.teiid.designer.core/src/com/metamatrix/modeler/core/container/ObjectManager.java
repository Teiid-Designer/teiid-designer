/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.container;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * ObjectManager - manager of maps containing EObject instances for models in the workspace. The maps are keyed on the UUID
 * associated with the EObject. Models not found in the "Model Container" cannot be found through the ObjectManager.
 */
public interface ObjectManager {

	EObject findEObject( String id );

	EObject findEObject( String id,
	                     Resource resource );

	/**
	 * Gets the UUID for an object, creating the UUID if one doesn't exist.
	 *
	 * @param object
	 * @return the UUID
	 */
	String getObjectId( EObject object );

	void setObjectId( EObject object,
	                  String id );
}
