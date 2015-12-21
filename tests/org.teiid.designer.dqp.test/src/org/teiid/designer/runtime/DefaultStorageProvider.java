/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import java.util.HashMap;
import java.util.Map;

import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;

/**
 * Implementation of {@link ISecureStorageProvider} that uses a simple
 * map for storage of the properties
 */
public class DefaultStorageProvider implements ISecureStorageProvider {

    private Map<String, Map<String, String>> storageMap = new HashMap<String, Map<String, String>>();

    @Override
    public boolean existsInSecureStorage(String nodeKey, String key) throws Exception {
        if (! storageMap.containsKey(nodeKey))
            return false;

        Map<String, String> nodeMap = storageMap.get(nodeKey);
        return nodeMap.containsKey(key);
    }

    @Override
    public String getFromSecureStorage(String nodeKey, String key) {
        Map<String, String> nodeMap = storageMap.get(nodeKey);
        if (nodeMap == null)
            return null;
        
        return nodeMap.get(key);
    }

    @Override
    public void storeInSecureStorage(String nodeKey, String key, String value) throws Exception {
        Map<String, String> nodeMap = storageMap.get(nodeKey);
        if (nodeMap == null) {
            nodeMap = new HashMap<String, String>();
            storageMap.put(nodeKey, nodeMap);
        }
        
        nodeMap .put(key, value);
    }

    public void clear() {
        storageMap.clear();
    }

	@Override
	public void removeFromSecureStorage(String nodeKey) throws Exception {
		storageMap.remove(nodeKey);
	}
}
