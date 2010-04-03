/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.ui.connection;

import org.teiid.designer.vdb.Vdb;

/**
 * @since 5.0
 */
public interface IVdbConnectionMgr {

    /**
     * Closes all existing VDB connections.
     * 
     * @since 5.0
     */
    void closeAllConnections();

    /**
     * Closes the connection for the specified <code>VdbEditingContext</code>.
     * 
     * @param theVdbContext the context whose connection is being closed
     * @return <code>true</code> if connection successfully closed or if no connection was open; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean closeConnection( Vdb theVdbContext );

    /**
     * Obtains the name of the specified connection.
     * 
     * @param theConnection the connection whose name is being requested
     * @return the name (never <code>null</code>)
     * @since 5.0.1
     */
    String getConnectionName( Object theConnection );

    /**
     * Obtains the <code>Vdb</code> for the specified connection.
     * 
     * @param theConnection the connection whose VDB is being requested
     * @return the context or <code>null</code> if not found
     * @since 5.0.1
     */
    Vdb getVdb( Object theConnection );

    /**
     * Indicates if the specified connection is open.
     * 
     * @param theConnection the connection being checked
     * @return <code>true</code> if open; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean isConnectionOpen( Object theConnection );

    /**
     * Indicates if the specified connection is stale.
     * 
     * @param theConnection the connection being checked
     * @return <code>true</code> if the connection is stale; <code>false</code> if no connection exists or connection is
     *         up-to-date.
     * @since 5.0.1
     */
    boolean isConnectionStale( Object theConnection );

    /**
     * Indicates if a connection for the specified <code>VdbEditingContext</code> is open.
     * 
     * @param theVdb the context being checked
     * @return <code>true</code> if a connection is open; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean isVdbConnectionOpen( Vdb theVdb );

    /**
     * Indicates if the current connection for the specified <code>Vdb</code> does not represent the latest VDB state.
     * 
     * @param theVdb the context whose connection is being checked
     * @return <code>true</code> if the connection is stale; <code>false</code> if no connection exists or connection is
     *         up-to-date.
     * @since 5.0
     */
    boolean isVdbConnectionStale( Vdb theVdb );

    /**
     * Indicates if any of the extension module is stale for the specified connection.
     * 
     * @param theVdbContext the context being checked
     * @return <code>true</code> if a connection is open; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean isExtensionModuleStale( Object theConnection );

}
