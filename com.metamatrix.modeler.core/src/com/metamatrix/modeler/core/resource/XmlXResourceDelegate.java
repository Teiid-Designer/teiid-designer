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

package com.metamatrix.modeler.core.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLResource;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;

/**
 * @author John Verhaeg
 */
public class XmlXResourceDelegate {

	private boolean loading;
	private boolean unloading;

	@SuppressWarnings( "deprecation" )
	public void attachedHelper( XMLResource resource,
	                            EObject eObject ) {
		// Ensure if eObject is new, it gets added to the detached map with our UUID instead of EMF's.
		String uuid = resource.getID(eObject);
		if (uuid == null) {
			uuid = MtkXmiResourceImpl.DETACHED_EOBJECT_TO_UUID_MAP.remove(eObject);
			if (uuid == null) {
				uuid = IDGenerator.getInstance().create().toString();
			} else {
				MtkXmiResourceImpl.DETACHED_UUID_TO_EOBJECT_MAP.remove(uuid);
			}
			resource.setID(eObject, uuid);
		} else {
	        resource.getIDToEObjectMap().put(uuid, eObject);
		}
	}

	public void detachedHelper( XMLResource resource,
	                            EObject eObject ) {
		String uuid = resource.getID(eObject);
		MtkXmiResourceImpl.DETACHED_EOBJECT_TO_UUID_MAP.put(eObject, uuid);
		MtkXmiResourceImpl.DETACHED_UUID_TO_EOBJECT_MAP.put(uuid, eObject);
		resource.setID(eObject, null);
	}

	@SuppressWarnings( "deprecation" )
	public void initialize( XMLResource resource ) {
		// Create EObject to ID map so underlying implementation calls setID when appropriate
		resource.getEObjectToIDMap();
		resource.getIDToEObjectMap();
	}

	public boolean isLoading() {
		return loading;
	}

	public boolean isUnloading() {
		return unloading;
	}

	public void setLoading( boolean loading ) {
		this.loading = loading;
	}

	public void setUnloading( boolean unloading ) {
		this.unloading = unloading;
	}
}
