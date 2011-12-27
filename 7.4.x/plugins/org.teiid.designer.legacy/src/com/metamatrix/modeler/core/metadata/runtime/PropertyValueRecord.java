/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

/**
 * PropertyValueRecord
 */
public interface PropertyValueRecord {

	/**
	 * Get the property name
	 * @return the name of the property
	 */
	String getProperty();

	/**
	 * Get the property value
	 * @return value of the property
	 */
	String getValue();
}
