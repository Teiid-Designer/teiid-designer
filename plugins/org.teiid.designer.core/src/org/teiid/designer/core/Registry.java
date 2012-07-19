/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

/**
 * A Registry represents a single naming/directory service through which objects
 * can be registered and discovered.
 *
 * @since 8.0
 */
public interface Registry {

	/**
	 * Look up an object by key in the registry.
	 * 
	 * @param key
	 *            the key the object is registered under. Cannot be null.
	 *            
	 * @return the Object registered under that key; may be null if no register
	 *         entry could be found
	 */
	Object lookup(String key);

	/**
	 * Lookup an object registered to the given key and of the given class
	 * 
	 * @param key
	 * 				the key the object is registered under. Cannot be null.
	 * 
	 * @param klazz
	 * 				the class of the registered object. If the registered object is not an instance of this class
	 * 				then nothing will be returned. Cannot be null.
	 * 
	 * @return the registered object or null
	 */
	<T> T lookup(String key, Class<T> klazz);

}
