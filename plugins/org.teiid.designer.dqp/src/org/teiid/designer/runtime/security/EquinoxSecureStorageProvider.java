/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.teiid.designer.runtime.DqpPlugin;

/**
 * Implementation of {@link ISecureStorageProvider} which uses Eclipse's Equinox
 * secure storage node system
 * 
 * @since 8.0
 *
 */
public class EquinoxSecureStorageProvider implements ISecureStorageProvider {

    private static EquinoxSecureStorageProvider instance;

    private EquinoxSecureStorageProvider() {}
    
    /**
     * Get the singleton instance of this provider
     * 
     * @return the one instance of this provider
     */
    public static EquinoxSecureStorageProvider getInstance() {
        if (instance == null) {
            instance = new EquinoxSecureStorageProvider();
        }
        
        return instance;
    }
    
    @Override
    public String getFromSecureStorage(String nodeKey, String key) {
        try {
            ISecurePreferences node = getNode(nodeKey);
            String val = node.get(key, null);
            if (val == null) {
                return null;
            }
            return new String(EncodingUtils.decodeBase64(val));
        } catch (Exception e) {
            DqpPlugin.Util.log(e);
            return null;
        }
    }

    @Override
    public void storeInSecureStorage(String nodeKey, String key, String value) throws Exception {
        ISecurePreferences node = getNode(nodeKey);
        if (value == null) node.put(key, value, true);
        else node.put(key, EncodingUtils.encodeBase64(value.getBytes()), true /* encrypt */);
    }

    /**
     * Get the secure preferences node for this connection instance.
     * The node is keyed to the url of this connection, allowing individual
     * connection's properties to be separately stored.
     * 
     * @param nodeKey base key for the preference node
     * 
     * @return
     * @throws UnsupportedEncodingException
     */
    private ISecurePreferences getNode(String nodeKey) throws Exception {
        ISecurePreferences root = SecurePreferencesFactory.getDefault();
        String encoded = URLEncoder.encode(nodeKey, "UTF-8"); //$NON-NLS-1$
        return root.node(encoded);
    }
}
