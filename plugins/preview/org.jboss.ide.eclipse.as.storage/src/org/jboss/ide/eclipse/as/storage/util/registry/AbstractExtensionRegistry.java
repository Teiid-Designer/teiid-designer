/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.util.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @param <K> 
 * @param <V>
 */
public abstract class AbstractExtensionRegistry<K, V> {

    protected final Map<K, V> extensions;

    private final String extPointId;

    private final String elementId;

    protected AbstractExtensionRegistry(String extPointId, String elementId, Map<K, V> extensionMap) throws Exception {
        this.extPointId = extPointId;
        this.elementId = elementId;
        this.extensions = extensionMap;
        load();
    }

    protected AbstractExtensionRegistry(String extPointId, String elementId) throws Exception {
        this(extPointId, elementId, new HashMap<K, V>());
    }

    private void load() throws Exception {
        IExtensionRegistryCallback<V> callback = new IExtensionRegistryCallback<V>() {

            @Override
            public String getExtensionPointId() {
                return extPointId;
            }

            @Override
            public String getElementId() {
                return elementId;
            }

            @Override
            public String getAttributeId() {
                return CLASS_ATTRIBUTE_ID;
            }

            @Override
            public boolean isSingle() {
                return false;
            }

            @Override
            public void process(V instance, IConfigurationElement element) {
                register(element, instance);
            }
        };

        ExtensionRegistryUtils.createExtensionInstances(callback);
    }
    
    protected abstract void register(IConfigurationElement configurationElement, V extension);

    protected void register(K key, V value) {
        extensions.put(key, value);
    }

    /**
     * Get a register value applicable for the given key
     * 
     * @param key
     * 
     * @return instance of V
     */
    public V getRegistered(K key) {
        return extensions.get(key);
    }

    /**
     * @return all the registered keys
     */
    public Collection<K> getRegisteredKeys() {
        return Collections.unmodifiableCollection(extensions.keySet());
    }

    /**
     * @return all the registered extensions
     */
    public Collection<V> getRegistered() {
        return Collections.unmodifiableCollection(extensions.values());
    }

    protected Collection<Map.Entry<K, V>> getRegisteredEntries() {
        return Collections.unmodifiableCollection(extensions.entrySet());
    }
}
