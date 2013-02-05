/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.Artifact.Type;
import org.komodo.repository.artifact.ArtifactResultSet;

/**
 * A repository of SOA artifacts.
 */
public interface SoaRepository {

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
        public final Map<String, String> params = new HashMap<String, String>();

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
     * The name of the system property whose value is the configured port of the server running S-RAMP.
     */
    public static final String SERVER_PORT_SYS_PROP = "org.jboss.resteasy.port"; //$NON-NLS-1$

    /**
     * Adds an artifact to the repository. Caller should set additional properties on the artifact created. Caller should ensure 
     * the stream is closed.
     * 
     * @param content the resource content (cannot be <code>null</code>)
     * @param type the artifact type being created (cannot be <code>null</code>)
     * @return the new artifact (never <code>null</code>)
     * @throws Exception if there is a problem creating the artifact in the repository
     */
    Artifact add(final InputStream content,
                 final Type type) throws Exception;

    /**
     * @throws Exception if there is a problem connecting
     */
    void connect() throws Exception;

    /**
     * @throws Exception if there is a problem disconnecting
     */
    void disconnect() throws Exception;

    /**
     * @param uuid the UUID of the artifact being requested (cannot be <code>null</code> or empty)
     * @return the artifact or <code>null</code> if not found
     * @throws Exception if there is a problem trying to obtain the artifact
     */
    Artifact get(final String uuid) throws Exception;

    /**
     * @param parentArtifactUuid the UUID of the artifact whose derived artifacts are being requested (cannot be <code>null</code> or empty)
     * @return the query results containing the derived artifacts (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the derived artifacts
     */
    ArtifactResultSet getDerivedArtifacts(final String parentArtifactUuid) throws Exception;

    /**
     * @return the repository name (may be <code>null</code> or empty)
     */
    String getName();

    /**
     * @return the repository URL (never <code>null</code> or empty)
     */
    String getUrl();

    /**
     * @param settings the query settings (cannot be <code>null</code> or empty)
     * @return the query results containing the artifacts (never <code>null</code>)
     * @throws Exception if there is a problem running query
     */
    ArtifactResultSet query(final QuerySettings settings) throws Exception;

    /**
     * @param query the query statement (cannot be <code>null</code> or empty)
     * @return the query results containing the artifacts (never <code>null</code>)
     * @throws Exception if there is a problem running query
     */
    ArtifactResultSet query(final String query) throws Exception;

    /**
     * @param newName the new name of the repository (can be <code>null</code> or empty)
     */
    void setName(String newName);

}
