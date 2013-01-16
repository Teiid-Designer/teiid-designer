/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.io.InputStream;
import org.overlord.sramp.client.query.QueryResultSet;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * Manages the interaction with the artifact repository.
 */
public interface RepositoryManager {

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
     * @param uuid the UUID of the artifact being requested (cannot be <code>null</code> or empty)
     * @return the artifact or <code>null</code> if not found
     * @throws Exception if there is a problem trying to obtain the artifact
     */
    BaseArtifactType get(final String uuid) throws Exception;

    /**
     * @param artifact the artifact whose derived artifacts are being requested (cannot be <code>null</code>)
     * @return the derived artifacts (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the derived artifacts
     */
    QueryResultSet getDerivedArtifacts(final BaseArtifactType artifact) throws Exception;

    /**
     * @return the repository name (may be <code>null</code> or empty)
     */
    String getName();

    /**
     * @param newName the new name of the repository manager (can be <code>null</code> or empty)
     */
    void setName(String newName);

    /**
     * @return the repository URL (never <code>null</code> or empty)
     */
    String getUrl();

}
