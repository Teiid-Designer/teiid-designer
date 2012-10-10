/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.teiid.core.designer.id.IDGenerator;
import org.teiid.designer.core.resource.xmi.MtkXmiResourceImpl;

/**
 * @author John Verhaeg
 *
 * @since 8.0
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
