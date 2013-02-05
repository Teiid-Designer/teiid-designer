/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp;

import java.io.InputStream;
import org.komodo.common.i18n.I18n;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.RepositoryI18n;
import org.komodo.repository.SoaRepository;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.Artifact.Type;
import org.komodo.repository.artifact.ArtifactResultSet;
import org.komodo.repository.sramp.teiid.SrampTeiidArtifactFactory;
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
public class SrampRepository implements SoaRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SrampRepository.class);

    private final SrampAtomApiClient client;
    private final SrampTeiidArtifactFactory factory;
    private String name;
    private final String url;

    /**
     * @param url the URL to the S-RAMP server (cannot be <code>null</code> or empty)
     * @throws Exception if there is a problem connecting to the server
     */
    SrampRepository(final String url) throws Exception {
        Precondition.notEmpty(url, "url"); //$NON-NLS-1$
        LOGGER.debug("Constructing S-RAMP repository with url '{}'", url); //$NON-NLS-1$

        this.client = new SrampAtomApiClient(url, false);
        this.url = url;
        this.factory = new SrampTeiidArtifactFactory(this);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#add(java.io.InputStream, org.komodo.repository.artifact.Artifact.Type)
     */
    @Override
    public Artifact add(final InputStream content,
                        final Type type) throws Exception {
        Precondition.notNull(content, "content"); //$NON-NLS-1$
        Precondition.notNull(type, "type"); //$NON-NLS-1$

        final ArtifactType artifact = ArtifactType.valueOf(type.getId());
        //        artifact.setMimeType("application/xml"); //$NON-NLS-1$
        LOGGER.debug("SrampRepository:Adding artifact of type '{}' ...", type.getId()); //$NON-NLS-1$
        final BaseArtifactType srampArtifact = this.client.uploadArtifact(artifact, content, null);
        LOGGER.debug("SrampRepository:Artifact with name '{}' and UUID '{}' was created", //$NON-NLS-1$
                     srampArtifact.getName(),
                     srampArtifact.getUuid());
        return this.factory.create(srampArtifact);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#connect()
     */
    @Override
    public void connect() throws Exception {
        try {
            this.client.getServiceDocument();
        } catch (final Exception e) {
            throw new Exception(I18n.bind(RepositoryI18n.repositoryConnectionFailure, getUrl()), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#disconnect()
     */
    @Override
    public void disconnect() throws Exception {
        // nothing to do
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

        final SrampRepository that = (SrampRepository)obj;
        return this.url.equals(that.url);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#get(java.lang.String)
     */
    @Override
    public Artifact get(final String uuid) throws Exception {
        Precondition.notEmpty(uuid, "uuid"); //$NON-NLS-1$
        final QueryResultSet result = this.client.query(SrampRepositoryUtil.getUuidQueryString(uuid));

        if (result.size() == 1) {
            final ArtifactSummary summary = result.get(0);
            final BaseArtifactType srampArtifact = this.client.getArtifactMetaData(summary.getType(), uuid);
            return this.factory.create(srampArtifact);
        }

        return null;
    }

    SrampAtomApiClient getClient() {
        return this.client;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#getDerivedArtifacts(java.lang.String)
     */
    @Override
    public ArtifactResultSet getDerivedArtifacts(final String parentArtifactUuid) throws Exception {
        Precondition.notNull(parentArtifactUuid, "parentArtifactUuid"); //$NON-NLS-1$
        LOGGER.debug("SrampRepository:Getting derived artifacts using query: '{}'", //$NON-NLS-1$
                     SrampRepositoryUtil.getDerivedArtifactsQueryString(parentArtifactUuid));
        return query(SrampRepositoryUtil.getDerivedArtifactsQueryString(parentArtifactUuid));
    }

    SrampTeiidArtifactFactory getFactory() {
        return this.factory;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#getName()
     */
    @Override
    public String getName() {
        return (StringUtil.isEmpty(this.name) ? this.url : this.name);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#getUrl()
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
     * @see org.komodo.repository.SoaRepository#query(org.komodo.repository.SoaRepository.QuerySettings)
     */
    @Override
    public ArtifactResultSet query(final QuerySettings settings) throws Exception {
        final String query = SrampRepositoryUtil.buildQuery(settings);
        LOGGER.debug("SrampRepository:query built from settings '{}'", query); //$NON-NLS-1$

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

        return new SrampArtifactResultSet(clientQuery.query(), this);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#query(java.lang.String)
     */
    @Override
    public ArtifactResultSet query(final String statement) throws Exception {
        Precondition.notNull(statement, "statement"); //$NON-NLS-1$
        return new SrampArtifactResultSet(this.client.query(statement), this);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepository#setName(java.lang.String)
     */
    @Override
    public void setName(final String newName) {
        this.name = newName;
    }

}
