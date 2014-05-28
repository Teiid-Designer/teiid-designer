/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.jboss.ide.eclipse.as.storage.IStorageUnit;
import org.jboss.ide.eclipse.as.storage.IStorageUnit.Category;
import org.jboss.ide.eclipse.as.storage.util.KeyInValueHashMap;
import org.jboss.ide.eclipse.as.storage.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.jboss.ide.eclipse.as.storage.util.registry.AbstractExtensionRegistry;

/**
 *
 */
public class StorageUnitRegistry extends AbstractExtensionRegistry<Category, IStorageUnit> {

    private static final String EXT_POINT_ID = "org.jboss.ide.eclipse.as.core.storage.configurationStorage"; //$NON-NLS-1$

    private static final String STORAGE_UNIT_ID = "storageUnit"; //$NON-NLS-1$

    private static class StorageUnitMapAdapter implements KeyFromValueAdapter<Category, IStorageUnit> {

        @Override
        public Category getKey(IStorageUnit value) {
            return value.getCategory();
        }
    }

    private static StorageUnitRegistry instance;

    /**
     * @return singleton instance
     * @throws Exception
     */
    public static StorageUnitRegistry getInstance() throws Exception {
        if (instance == null)
            instance = new StorageUnitRegistry();

        return instance;
    }

    /**
     * @throws Exception
     */
    private StorageUnitRegistry() throws Exception {
        super(EXT_POINT_ID, STORAGE_UNIT_ID, new KeyInValueHashMap<Category, IStorageUnit>(new StorageUnitMapAdapter()));
    }

    @Override
    protected void register(IConfigurationElement configurationElement, IStorageUnit extension) {
        ((KeyInValueHashMap<Category, IStorageUnit>) this.extensions).add(extension);
    }

}
