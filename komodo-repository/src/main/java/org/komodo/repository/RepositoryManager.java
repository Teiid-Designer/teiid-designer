/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.komodo.repository.artifact.Artifact;
import org.overlord.sramp.client.query.QueryResultSet;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * Manages the interaction with the artifact repository.
 */
public interface RepositoryManager {

    /**
     * The settings to use for querying.
     */
    public class QuerySettings {

        /**
         * The type of artifacts being queried for (can be <code>null</code>)
         */
        public Artifact.Type artifactType;

        /**
         * Indicates if the results should be sorted in ascending order.
         */
        public boolean ascending = true;

        /**
         * The max number of results to return.
         */
        public int count = -1;

        /**
         * The property the results should be sorted by (can be <code>null</code> or empty).
         */
        public String orderBy;

        /**
         * The property criteria of the artifacts to be returned in the results (can be <code>null</code> or empty).
         */
        public Map<String, String> params;

        /**
         * The properties to return in the results (can be <code>null</code> or empty).
         */
        public List<String> resultColumns;

        /**
         * The start index.
         */
        public int startIndex = -1;
    }

    /**
     * Adds a VDB to the repository. Caller should set additional artifact properties based on the VDB business object created
     * from this artifact. Caller should ensure stream is closed.
     * 
     * @param content the resource content (cannot be <code>null</code>)
     * @return the artifact (never <code>null</code>)
     * @throws Exception if there is a problem creating the artifact in the repository
     */
    BaseArtifactType addVdb(final InputStream content) throws Exception;

    /**
     * @param uuid the UUID of the artifact being requested (cannot be <code>null</code> or empty)
     * @return the artifact or <code>null</code> if not found
     * @throws Exception if there is a problem trying to obtain the artifact
     */
    BaseArtifactType get(final String uuid) throws Exception;

    /**
     * @param artifact the artifact whose derived artifacts are being requested (cannot be <code>null</code>)
     * @return the query results containing the derived artifacts (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the derived artifacts
     */
    QueryResultSet getDerivedArtifacts(final BaseArtifactType artifact) throws Exception;

    /**
     * @return the repository name (may be <code>null</code> or empty)
     */
    String getName();

    /**
     * @return the repository URL (never <code>null</code> or empty)
     */
    String getUrl();

    /**
     * @param settings the query settings (cannot be <code>null</code>)
     * @return the query results containing the artifacts (never <code>null</code>)
     * @throws Exception if there is a problem running query
     */
    QueryResultSet query(final QuerySettings settings) throws Exception;

    /**
     * @param srampQuery the query statement (cannot be <code>null</code> or empty)
     * @return the query results containing the artifacts (never <code>null</code>)
     * @throws Exception if there is a problem running query
     */
    QueryResultSet query(final String srampQuery) throws Exception;

    /**
     * @param newName the new name of the repository manager (can be <code>null</code> or empty)
     */
    void setName(final String newName);

}
