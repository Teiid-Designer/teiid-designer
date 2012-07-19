/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core;

import java.beans.PropertyChangeListener;

/**
 * Implemented by objects that publish property changes
 *
 * @since 8.0
 */
public interface PropertyChangePublisher {

	/**
	 * Add a property change listener
	 * 
	 * @param listener
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Add a property change listener for the given property
	 * 
	 * @param propertyName
	 * @param listener
	 */
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
	
	/**
	 * Remove a property change listener
	 * 
	 * @param listener
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Remove a property change listener
	 * 
	 * @param propertyName 
	 * @param listener
	 */
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
