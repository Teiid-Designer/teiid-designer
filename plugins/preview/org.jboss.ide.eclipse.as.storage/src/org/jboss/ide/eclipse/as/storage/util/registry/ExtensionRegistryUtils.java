/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.util.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;

/**
 * Utilities for interfacing with the extension registry
 */
public class ExtensionRegistryUtils {

    /**
     * Creates an extension instance using the metadata from the given {@link IExtensionRegistryCallback}
     *
     * @param callback
     * @throws Exception
     */
    public static <T> void createExtensionInstances(IExtensionRegistryCallback<T> callback) throws Exception {
        IExtensionRegistry extRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = extRegistry.getConfigurationElementsFor(callback.getExtensionPointId());

        int extCount = 0;
        for (IConfigurationElement element : extensions) {
            if (callback.getElementId() != null && ! callback.getElementId().equals(element.getName()))
                continue;

            // Found at least 1 implementation of this extension
            extCount++;

            T extension = (T) element.createExecutableExtension(callback.getAttributeId());
            callback.process(extension, element);

            if (callback.isSingle())
                return;
        }

        if (extCount > 0)
            return;

        throw new IllegalStateException(NLS.bind(Messages.ExtensionRegistryUtilsNoRegisteredExtension, callback.getExtensionPointId()));
    }
}
