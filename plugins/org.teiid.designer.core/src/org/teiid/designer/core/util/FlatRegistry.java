/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.util;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.PropertyChangePublisher;
import org.teiid.designer.core.Registry;
import org.teiid.designer.core.spi.RegistrySPI;


/**
 *
 * @since 8.0
 */
public class FlatRegistry implements RegistrySPI {

	private Map entries;

	/**
	 * Constructor for RegistryImpl.
	 */
	public FlatRegistry() {
		this.entries = new HashMap();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		CoreArgCheck.isNotNull(evt.getOldValue());
		CoreArgCheck.isNotNull(evt.getNewValue());
		CoreArgCheck.isNotNull(evt.getPropertyName());
		CoreArgCheck.isNotNull(evt.getSource());
		
		
		// Add this to the Registry using the current (new) key
		register(evt.getNewValue().toString(), (PropertyChangePublisher) evt.getSource(), evt.getPropertyName());

		// If there is an old key, then unregister this ...
		if (evt.getOldValue() != null && lookup(evt.getOldValue().toString()) == evt.getSource()) {
			this.entries.remove(evt.getOldValue().toString());
		}
	}

	/**
	 * @see Registry#lookup(String)
	 */
	@Override
	public Object lookup(String key) {
		CoreArgCheck.isNotNull(key);
		
		return this.entries.get(key);
	}

	/**
	 * @see Registry#lookup(String, Class)
	 */
	@Override
	public <T> T lookup(String key, Class<T> klazz) {
		CoreArgCheck.isNotNull(key);
		CoreArgCheck.isNotNull(klazz);
		
		Object obj = this.entries.get(key);
		if (obj == null) {
			return null;
		}

		if (!klazz.isInstance(obj)) {
			return null;
		}

		return (T) obj;
	}

	/**
	 * @see RegistrySPI#register(String, Object)
	 */
	@Override
	public <T> T register(String key, T obj) {
		CoreArgCheck.isNotNull(key);
		
		this.entries.put(key, obj);
		return obj;
	}

	@Override
	public PropertyChangePublisher register(String key,
			PropertyChangePublisher propertyChangePublisher, String propertyName) {
		CoreArgCheck.isNotNull(key);
		CoreArgCheck.isNotNull(propertyChangePublisher);
		CoreArgCheck.isNotNull(propertyName);
		
		this.entries.put(key, propertyChangePublisher);

		propertyChangePublisher.addPropertyChangeListener(propertyName, this);
		return propertyChangePublisher;
	}

	/**
	 * @see RegistrySPI#unregister(String)
	 */
	@Override
	public Object unregister(String key) {
		CoreArgCheck.isNotNull(key);
	
		System.out.println("Unregistering " + key);
		
		Object object = this.entries.remove(key);
		
		if (object instanceof PropertyChangePublisher) {
			((PropertyChangePublisher) object).removePropertyChangeListener(this);
		}
		
		return object;
	}

	/**
	 * Return size of registry
	 * 
	 * @return size of registry
	 */
	public int size() {
		return this.entries.size();
	}
}
