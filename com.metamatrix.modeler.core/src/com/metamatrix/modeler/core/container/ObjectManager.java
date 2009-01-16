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
