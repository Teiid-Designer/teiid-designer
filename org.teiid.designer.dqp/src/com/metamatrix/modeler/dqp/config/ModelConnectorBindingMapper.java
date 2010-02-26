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
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * ModelConnectorBindingMapper provide the mapping between modelReferences(for physical models) and connector bindings
 * stored on the vdb definition files, if connector bindings are not available on the definition file
 * its helps suggest potential bindings by reading the JDBC connection properties stored on the
 * manifest model of a vdb for each of the physical models that are part of the vdb. If it finds
 * any physical modes with connection properties defined, it then reads connector binding
 * information from the configuration file and maps the connection properties found to existing
 * connector bindings.
 *   
 * @since 4.3
 */
public interface ModelConnectorBindingMapper {

    /**
     * Get the connector binding that is mapped to the given modelreference in the
     * vdb. If the modelReference is not for a physical model there is not binding
     * available a null is returned.
     * @param modelReference The ModelReference for a model in the vdb.
     * @return The connector binding for the model reference
     * @since 4.3
     */
    Connector getConnector(ModelReference modelReference);

    /**
     * Find the connector binding whose properties match the connection properties
     * for a given modelReference. If the modelReference is not for a physical model
     * or is no binding match found a null is returned.
     * @param modelReference The ModelReference for a model in the vdb.
     * @return The connector bindings matching the connection properties for the model reference.
     * @since 4.3
     */
    Collection findConnectorBindingMatches(ModelReference modelReference);

    /**
     * Find the connector types whose properties match the connection properties
     * for a given modelReference. If the modelReference is not for a physical model
     * or is no connector type match found a null is returned.
     * @param modelReference The ModelReference for a model in the vdb.
     * @return The connector types matching the connection properties for the model reference.
     * @since 4.3
     */
    Collection findConnectorTypeMatches(ModelReference modelReference);

    /**
     * Get a map of ModelReferences for physical models to connector bindings. Values
     * in the map may be null if bindings are not defined yet.
     * @return Map of model references to their connector bindings.
     * @since 4.3
     */
    Map getAllConnectorBindings();

    /**
     * Get a map of ModelReferences for physical models to collection of connector bindings. Values
     * in the map may be null if no binding matches are found.
     * @return Map of model references to collection of connector binding matches.
     * @since 4.3
     */
    Map findAllConnectorBindingMatches();

    /**
     * Set the ConnectorBinding for the given modelReference, this is not going to persist
     * the bindings to the vdb definition file but would just update the in memory binding map.
     * If the modelReference passed in is for anything other than for a physical model the
     * the no information is stored.
     * @param modelReference The modelReference for a physical model.
     * @since 4.3
     */
    void setConnector(ModelReference modelReference, Connector connector);

    /**
     * Create a connector binding reading the properties on the modelReference and given the
     * connector binding type. This method will return a null if the driverClass specified on the
     * ConnectorType does not match the one on the modelReference.
     * @param modelReference The modelReference whose JDBC import properties are to be read.
     * @param ConnectorType The type of the connector binding to create
     * @param theName the name the new binding should be called
     * @return the connector  with properties on the modelReference.
     * @since 4.3
     */
    Connector createConnectorBinding( ModelReference modelReference,
                                      ConnectorType ConnectorType,
                                      String theName ) throws Exception;

}
