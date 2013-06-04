/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * @param <K> 
 * @param <V>
 */
public abstract class AbstractExtensionRegistry<K, V> {

    private static final String CLASS_ATTRIBUTE_ID = "class"; //$NON-NLS-1$

    private Map<K, V> extensions = new HashMap<K, V>();

    private String extPointId;

    private String elementId;

    protected AbstractExtensionRegistry(String extPointId, String elementId) throws Exception {
        this.extPointId = extPointId;
        this.elementId = elementId;
        load();
    }

    private void load() throws Exception {
        IExtensionRegistry extRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = extRegistry.getConfigurationElementsFor(extPointId);

        for (IConfigurationElement element : extensions) {
            if (elementId != null && !elementId.equals(element.getName()))
                continue;
            
            V extension = (V) element.createExecutableExtension(CLASS_ATTRIBUTE_ID);
            register(element, extension);
        }
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
