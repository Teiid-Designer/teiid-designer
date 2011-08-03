/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.registry;

/**
 * A registered <codeRegistryListener</code> will be notified of registry events.
 */
public interface RegistryListener {

    /**
     * @param event the event to process (never <code>null</code>)
     */
    void process(RegistryEvent event);

}
