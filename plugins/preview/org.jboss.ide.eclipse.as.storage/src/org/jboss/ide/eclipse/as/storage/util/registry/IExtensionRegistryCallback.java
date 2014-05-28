/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.util.registry;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Callback that provides metadata for the creation of an object instance
 * from an extension point.
 *
 * Implementations should override {@link #process(Object, IConfigurationElement)} in order to
 * register, assign or otherwise process the created instances.
 *
 * @param <T> Class of the required extension instance
 */
public interface IExtensionRegistryCallback<T> {

    /**
     * Class attribute commonly used for defining a java class in an extension point
     */
    String CLASS_ATTRIBUTE_ID = "class"; //$NON-NLS-1$

    /**
     * @return extension point identifier
     */
    String getExtensionPointId();

    /**
     * @return extension point element identifier
     */
    String getElementId();

    /**
     * @return extension point element attribute identifier
     */
    String getAttributeId();

    /**
     * Callback method providing the created instance executable and its
     * related configuration element.
     *
     * Note: this will be called for each implementation of the extension point
     * hence it is expected that implementations of this method should
     * handle multiple calls appropriately.
     *
     * @param instance
     * @param element
     */
    void process(T instance, IConfigurationElement element);

    /**
     * Whether this extension expects a single implementation
     *
     * @return true for single implementations and false for multiple 
     */
    boolean isSingle();
}
