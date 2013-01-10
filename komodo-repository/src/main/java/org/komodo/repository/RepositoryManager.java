/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.io.InputStream;
import org.komodo.teiid.model.vdb.Vdb;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * Manages the interaction with the artifact repository.
 */
public interface RepositoryManager {

    /**
     * The name of the property whose value is the JSON ModeShape repository configuration file.
     */
    String MODESHAPE_CONFIG_URL = "sramp.modeshape.config.url"; //$NON-NLS-1$

    /**
     * Adds a VDB to the repository. Caller should set additional artifact properties based on the VDB business object created
     * from this artifact. Caller should ensure stream is closed. The file name is used to determine mime type.
     * 
     * @param content the resource content (cannot be <code>null</code>)
     * @param fileName the file name associated with the content (cannot be <code>null</code> or empty)
     * @return the artifact (never <code>null</code>)
     * @throws Exception if there is a problem creating the artifact in the repository
     */
    BaseArtifactType addVdb(final InputStream content,
                            final String fileName) throws Exception;

    /**
     * Adds a VDB to the repository. Sets artifact properties based on the VDB parameter. Caller should ensure stream is closed. 
     * The file name is used to determine mime type.
     * 
     * @param vdb the VDB whose content being added as an artifact (cannot be <code>null</code> or empty)
     * @param content the resource content (cannot be <code>null</code>)
     * @param fileName the file name associated with the content (cannot be <code>null</code> or empty)
     * @return the artifact (never <code>null</code>)
     * @throws Exception if there is a problem creating the artifact in the repository
     */
    BaseArtifactType addVdb(final Vdb vdb,
                            final InputStream content,
                            final String fileName) throws Exception;

    /**
     * @param query the query to execute (cannot be <code>null</code> or empty)
     * @return the results (never <code>null</code>)
     * @throws Exception if there is a problem running the specified query
     */
    //    QueryResultSet query(final String query) throws Exception;

    /**
     * @throws Exception if there is a problem shutting down the repository
     */
    void shutdown() throws Exception;

    /**
     * @throws Exception if there is a problem starting the repository
     */
    void start() throws Exception;

}
