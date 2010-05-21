/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.config;

import java.util.Collection;
import java.util.Map;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.vdb.VdbModelEntry;

/**
 * ModelConnectorMapper provides the mapping between physical (i.e. source) models and connectors.
 */
public interface ModelConnectorMapper {

    /**
     * Create a connector reading the properties on the modelReference and given the connector type. This method will return a
     * null if the driverClass specified on the ConnectorType does not match the one on the modelReference.
     * 
     * @param modelEntry The model entry whose JDBC import properties are to be read.
     * @param ConnectorType The type of the connector to create
     * @param theName the name the new connector should be called
     * @return the connector with properties on the modelEntry.
     */
    Connector createConnector( VdbModelEntry modelEntry,
                               ConnectorType ConnectorType,
                               String theName ) throws Exception;

    /**
     * Get a map of model entries for physical models to collection of connectors. Values in the map may be <code>null</code> if
     * no connectors matches are found.
     * 
     * @return Map of model entries to collection of connector matches.
     */
    Map<VdbModelEntry, Collection<Connector>> findAllConnectorMatches();

    /**
     * Find the connector whose properties match the connection properties for a given model entry. If the model entry is not for
     * a physical model or is no connector match found a <code>null</code> is returned.
     * 
     * @param modelEntry The model entry for a model in the VDB.
     * @return The connectors matching the connection properties for the model entry.
     * @since 4.3
     */
    Collection<Connector> findConnectorMatches( VdbModelEntry modelEntry );

    /**
     * Find the connector types whose properties match the connection properties for a given model entry. If the model entry is
     * not for a physical model or is no connector type match found a <code>null</code> is returned.
     * 
     * @param modelEntry The model entry for a model in the VDB.
     * @return The connector types matching the connection properties for the model entry.
     * @since 4.3
     */
    Collection<ConnectorType> findConnectorTypeMatches( VdbModelEntry modelEntry );

    /**
     * Get a map of model entries for physical models to connectors. Values in the map may be null if connectors are not defined
     * yet.
     * 
     * @return Map of model entries to their connectors.
     * @since 4.3
     */
    Map<VdbModelEntry, Connector> getAllConnectors();

    /**
     * Get the connector that is mapped to the given model entry in the VDB. If the model entry is not for a physical model there
     * is not connector available a <code>null</code> is returned.
     * 
     * @param modelEntry The model entry for a model in the VDB.
     * @return The connector for the model entry
     * @since 4.3
     */
    Connector getConnector( VdbModelEntry modelEntry );

    /**
     * Set the Connector for the given model entry, this is not going to persist the connectors to the VDB definition file but
     * would just update the in memory connector map. If the model entry passed in is for anything other than for a physical model
     * the the no information is stored.
     * 
     * @param modelEntry The model entry for a physical model.
     * @since 4.3
     */
    void setConnector( VdbModelEntry modelEntry,
                       Connector connector );

}
