/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.resource;

import org.eclipse.emf.ecore.EObject;

/**
 * @since 5.0.3
 */
public interface XResource {

	/**
	 * @param eObject
	 * @return UUID
	 */
	String getUuid( EObject eObject );

	/**
	 * @return True if this resource is in the process of loading.
	 */
	boolean isLoading();

	/**
	 * @return True if this resource is in the process of unloading.
	 */
	boolean isUnloading();

	/**
	 * @param eObject
	 * @param uuid
	 */
	void setUuid( EObject eObject,
	              String uuid );
}
