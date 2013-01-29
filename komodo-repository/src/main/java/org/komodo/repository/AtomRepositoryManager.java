/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.io.InputStream;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.artifact.VdbArtifact;
import org.komodo.repository.deriver.DeriverUtil;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientQuery;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A repository manager that uses the S-RAMP atom interface.
 */
public class AtomRepositoryManager implements RepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtomRepositoryManager.class);

    private final SrampAtomApiClient client;

    private String name;

    private final String url;

    /**
     * @param url the URL to the S-RAMP server (cannot be <code>null</code> or empty)
     * @throws Exception if there is a problem connecting to the server
     */
    AtomRepositoryManager(final String url) throws Exception {
        this(url, true);
    }

    /**
     * @param url the URL to the S-RAMP server (cannot be <code>null</code> or empty)
     * @param connect <code>true</code> if a connection to the repository should be made at construction
     * @throws Exception if there is a problem connecting to the server
     */
    public AtomRepositoryManager(final String url,
                                 final boolean connect) throws Exception {
        Precondition.notEmpty(url, "url"); //$NON-NLS-1$
        LOGGER.debug("Constructing repository manager with url '{}'", url); //$NON-NLS-1$

        this.client = new SrampAtomApiClient(url, connect);
        this.url = url;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#addVdb(java.io.InputStream)
     */
    @Override
    public BaseArtifactType addVdb(final InputStream content) throws Exception {
        final ArtifactType artifact = ArtifactType.valueOf(VdbArtifact.TYPE.getId());
        artifact.setMimeType("application/xml"); //$NON-NLS-1$
        LOGGER.debug("RepositoryManager:Adding VDB ..."); //$NON-NLS-1$
        final BaseArtifactType newArtifact = this.client.uploadArtifact(artifact, content, null);
        LOGGER.debug("RepositoryManager:VDB artifact with name '{}' was created", newArtifact.getName()); //$NON-NLS-1$
        return newArtifact;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        final AtomRepositoryManager that = (AtomRepositoryManager)obj;
        return this.url.equals(that.url);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#get(java.lang.String)
     */
    @Override
    public BaseArtifactType get(final String uuid) throws Exception {
        Precondition.notEmpty(uuid, "uuid"); //$NON-NLS-1$
        final QueryResultSet result = this.client.query(DeriverUtil.getUuidQueryString(uuid));

        if (result.size() == 1) {
            final ArtifactSummary summary = result.get(0);
            return this.client.getArtifactMetaData(summary.getType(), uuid);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#getDerivedArtifacts(org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType)
     */
    @Override
    public QueryResultSet getDerivedArtifacts(final BaseArtifactType artifact) throws Exception {
        Precondition.notNull(artifact, "artifact"); //$NON-NLS-1$
        LOGGER.debug("RepositoryManager:Getting derived artifacts using query: '{}'", //$NON-NLS-1$
                     DeriverUtil.getDerivedArtifactsQueryString(artifact.getUuid()));
        return this.client.query(DeriverUtil.getDerivedArtifactsQueryString(artifact.getUuid()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns URL if no name is set.
     *
     * @see org.komodo.repository.RepositoryManager#getName()
     */
    @Override
    public String getName() {
        return (StringUtil.isEmpty(this.name) ? this.url : this.name);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#getUrl()
     */
    @Override
    public String getUrl() {
        return this.url;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(this.url);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#query(org.komodo.repository.RepositoryManager.QuerySettings)
     */
    @Override
    public QueryResultSet query(final QuerySettings settings) throws Exception {
        final String query = DeriverUtil.buildQuery(settings);
        LOGGER.debug("RepositoryManager:query built from settings '{}'", query); //$NON-NLS-1$

        final SrampClientQuery clientQuery = this.client.buildQuery(query);

        if (settings.ascending) {
            clientQuery.ascending();
        } else {
            clientQuery.descending();
        }

        if (settings.count != -1) {
            clientQuery.count(settings.count);
        }

        if (!StringUtil.isEmpty(settings.orderBy)) {
            clientQuery.orderBy(settings.orderBy);
        }

        if (settings.startIndex != -1) {
            clientQuery.startIndex(settings.startIndex);
        }

        if (!CollectionUtil.isEmpty(settings.resultColumns)) {
            for (final String column : settings.resultColumns) {
                clientQuery.propertyName(column);
            }
        }

        return clientQuery.query();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#setName(java.lang.String)
     */
    @Override
    public void setName(final String newName) {
        this.name = newName;
    }

}
