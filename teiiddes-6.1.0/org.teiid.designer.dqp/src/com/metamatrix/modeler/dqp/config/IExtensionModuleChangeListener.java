/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.config;

/**
 * A listener that will process changes to extension modules.
 * 
 * @since 5.5.3
 */
public interface IExtensionModuleChangeListener {

    /**
     * @param event
     *            the event being processed
     * @since 5.5.3
     */
    void extensionModulesChanged(ExtensionModuleChangeEvent event);

}
