/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.spi;

import java.beans.PropertyChangeListener;

import org.teiid.designer.core.PropertyChangePublisher;
import org.teiid.designer.core.Registry;

/**
 *
 *
 * @since 8.0
 */
public interface RegistrySPI extends Registry, PropertyChangeListener {

	/**
	 * Register the specified object under the supplied key.
	 * 
	 * @param key
	 *            the key under which the object is to be registered. Cannot be null.
	 * @param obj
	 *            the object to be registered. Cannot be null
	 * @return the object currently being registered. 
	 *                 Note. this is a change in API since this used to 
	 *                 return the object previously registered
	 */
	<T> T register(String key, T obj);

	/**
	 * Register the specified {@link PropertyChangePublisher} against the key.
	 * The given propertyName signifies the property on the {@link PropertyChangePublisher}
	 * that the registry should listen for in order to keep the registry updated.
	 * 
	 * @param key
	 * 				the key under which the object is to be registered. Cannot be null.
	 * @param propertyChangePublisher
	 * 				the object, implementing the {@link PropertyChangePublisher} interface, to be
	 * 				registered. Cannot be null.
	 * 
	 * @param propertyName
	 * 				the property name that the registry listens to when the object's value is changed
	 * 
	 * @return propertyChangePublisher
	 */
	PropertyChangePublisher register(String key, PropertyChangePublisher propertyChangePublisher, String propertyName);
	
	/**
	 * Unregister the object registered under the supplied key.
	 * 
	 * @param key
	 *            the registration key. Cannot be null.
	 *            
	 * @return the object currently registered under the supplied key, or null
	 *         if there is no object currently registered
	 */
	Object unregister(String key);

}