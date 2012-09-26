/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.security;



/**
 * Interface that should be implemented by classes providing
 * implementations of secure storage.
 * 
 * @since 8.0
 */
public interface ISecureStorageProvider {
    
    /**
     * Retrieve the value stored against the given key from 
     * the secure storage.
     * 
     * @param nodeKey 
     * @param key
     * @return value stored against key
     */
    String getFromSecureStorage(String nodeKey, String key);
    
    /**
     * Store the given value against the key in eclipse's secure
     * storage.
     * 
     * @param nodeKey 
     * @param key
     * @param value
     * @throws Exception
     */
    void storeInSecureStorage(String nodeKey, String key, String value) throws Exception;
}
