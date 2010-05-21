/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.connections;

import java.util.Properties;

/**
 * Interface intended to provide a mechanism for the org.teiid.designer.dqp plugin to contribute connection management to the
 * org.teiid.designer.vdb plugin. This keeps the VDB plugin a design-time plugin and the DQP a run-time plugin.
 * 
 * All the VDB needs is source connection "names" and not concrete connection objects, so the only interface method is findConnectioName()
 */
public interface ConnectionFinder {

    /**
     * Method to find a connection name for the supplied model name and properties. These properties could represent JDBC
     * or Text or any other connector type information.
     * 
     * @param modelName
     * @param properties
     * @return name of the connection
     * @throws Exception
     */
    public String findConnectionName( String modelName,
                                             Properties properties ) throws Exception;
}
