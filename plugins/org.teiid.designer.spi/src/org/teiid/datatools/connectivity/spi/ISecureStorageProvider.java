/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.datatools.connectivity.spi;



/**
 * Interface that should be implemented by classes providing
 * implementations of secure storage.
 * 
 * @since 8.0
 */
public interface ISecureStorageProvider {

    /**
     * @param nodeKey
     * @param key
     * @return node with given nodeKey exists in secure storage
     *
     * @throws Exception
     */
    boolean existsInSecureStorage(String nodeKey, String key) throws Exception;

    /**
     * Retrieve the value stored against the given key from 
     * the secure storage.
     * 
     * @param nodeKey 
     * @param key
     * @return value stored against key
     * @throws Exception 
     */
    String getFromSecureStorage(String nodeKey, String key) throws Exception;
    
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
    
    /**
     * Remove the given node from eclipse's secure
     * storage.
     * 
     * @param nodeKey 
     * @throws Exception
     */
    void removeFromSecureStorage(String nodeKey) throws Exception;

}
