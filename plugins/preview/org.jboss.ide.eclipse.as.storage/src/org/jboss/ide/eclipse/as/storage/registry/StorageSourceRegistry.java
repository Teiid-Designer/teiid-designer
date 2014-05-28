/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.jboss.ide.eclipse.as.storage.IStorageSource;
import org.jboss.ide.eclipse.as.storage.util.KeyInValueHashMap;
import org.jboss.ide.eclipse.as.storage.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.jboss.ide.eclipse.as.storage.util.registry.AbstractExtensionRegistry;

/**
 *
 */
public class StorageSourceRegistry extends AbstractExtensionRegistry<String, IStorageSource> {

    private static final String EXT_POINT_ID = "org.jboss.ide.eclipse.as.core.storage.configurationStorage"; //$NON-NLS-1$

    private static final String STORAGE_SOURCE_ID = "storageSource"; //$NON-NLS-1$

    private static class StorageSourceMapAdapter implements KeyFromValueAdapter<String, IStorageSource> {

        @Override
        public String getKey(IStorageSource value) {
            return value.id();
        }
    }

    private static StorageSourceRegistry instance;

    /**
     * @return singleton instance
     * @throws Exception
     */
    public static StorageSourceRegistry getInstance() throws Exception {
        if (instance == null)
            instance = new StorageSourceRegistry();

        return instance;
    }

    /**
     * @throws Exception
     */
    private StorageSourceRegistry() throws Exception {
        super(EXT_POINT_ID, STORAGE_SOURCE_ID, new KeyInValueHashMap<String, IStorageSource>(new StorageSourceMapAdapter()));
    }

    @Override
    protected void register(IConfigurationElement configurationElement, IStorageSource extension) {
        ((KeyInValueHashMap<String, IStorageSource>) this.extensions).add(extension);
    }

}
