/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

/**
 * The <code>IServerRegistryListener</code> interface defines the API for a server registry listener.
 */
public interface IServerRegistryListener {

    /**
     * @param event the event being processed (never <code>null</code>)
     * @return any errors caught during the processing or <code>null</code>
     */
    Exception[] serverRegistryChanged( ServerRegistryEvent event );

}
