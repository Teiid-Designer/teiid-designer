/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.io.InputStream;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.komodo.common.util.Precondition;
import org.komodo.repository.artifact.Artifact.Type;
import org.komodo.repository.deriver.DeriverUtil;
import org.overlord.sramp.ArtifactType;
import org.overlord.sramp.atom.providers.HttpResponseProvider;
import org.overlord.sramp.atom.providers.OntologyProvider;
import org.overlord.sramp.atom.providers.SrampAtomExceptionProvider;
import org.overlord.sramp.atom.services.ArtifactResource;
import org.overlord.sramp.atom.services.BatchResource;
import org.overlord.sramp.atom.services.FeedResource;
import org.overlord.sramp.atom.services.OntologyResource;
import org.overlord.sramp.atom.services.QueryResource;
import org.overlord.sramp.atom.services.ServiceDocumentResource;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A repository manager that uses the S-RAMP atom interface.
 */
public class AtomRepositoryManager implements RepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtomRepositoryManager.class);

    private final SrampAtomApiClient client;

    /**
     * @param url the URL to the S-RAMP server (cannot be <code>null</code> or empty)
     */
    public AtomRepositoryManager(final String url) {
        Precondition.notEmpty(url, "url"); //$NON-NLS-1$
        LOGGER.debug("Constructing repository manager with url '{}'", url); //$NON-NLS-1$
        this.client = new SrampAtomApiClient(url);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#addVdb(java.io.InputStream, java.lang.String)
     */
    @Override
    public BaseArtifactType addVdb(final InputStream content,
                                   final String fileName) throws Exception {
        final ArtifactType artifact = ArtifactType.valueOf(Type.VDB.getName());
        LOGGER.debug("Adding VDB with file name '{}'", fileName); //$NON-NLS-1$
        return this.client.uploadArtifact(artifact, content, fileName);
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
        LOGGER.debug("Getting derived artifacts using query: '{}'", //$NON-NLS-1$
                     DeriverUtil.getDerivedArtifactsQueryString(artifact.getUuid()));
        return this.client.query(DeriverUtil.getDerivedArtifactsQueryString(artifact.getUuid()));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#shutdown()
     */
    @Override
    public void shutdown() throws Exception {
        // TODO figure out what to do here
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#start()
     */
    @Override
    public void start() throws Exception {
        final ResteasyDeployment deployment = EmbeddedContainer.start();
        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(ServiceDocumentResource.class);
        registry.addPerRequestResource(ArtifactResource.class);
        registry.addPerRequestResource(FeedResource.class);
        registry.addPerRequestResource(QueryResource.class);
        registry.addPerRequestResource(BatchResource.class);
        registry.addPerRequestResource(OntologyResource.class);

        final ResteasyProviderFactory providerFactory = deployment.getProviderFactory();
        providerFactory.registerProvider(SrampAtomExceptionProvider.class);
        providerFactory.registerProvider(HttpResponseProvider.class);
        providerFactory.registerProvider(OntologyProvider.class);
    }

}
