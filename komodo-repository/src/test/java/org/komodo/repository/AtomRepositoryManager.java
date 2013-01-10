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
import org.komodo.repository.artifact.Artifact;
import org.komodo.teiid.model.vdb.Vdb;
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
import org.overlord.sramp.repository.PersistenceFactory;
import org.overlord.sramp.repository.jcr.JCRRepositoryCleaner;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class AtomRepositoryManager implements CleanableRepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtomRepositoryManager.class);

    private final JCRRepositoryCleaner cleaner = new JCRRepositoryCleaner();
    private final SrampAtomApiClient client;

    /**
     * Constructs a default s-ramp jetty/atom repository manager using localhost and default port.
     */
    public AtomRepositoryManager() {
        this(String.format("%s:%d", "http://localhost", 8081)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param url the URL to the S-RAMP server (cannot be <code>null</code> or empty)
     */
    public AtomRepositoryManager(final String url) {
        Precondition.notEmpty(url, "url"); //$NON-NLS-1$
        LOGGER.debug("Constructing repository manager with url \"" + url + "\""); //$NON-NLS-1$ //$NON-NLS-2$
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
        final ArtifactType artifact = ArtifactType.valueOf(Artifact.Type.VDB.getName());
        LOGGER.debug("Adding VDB with file name \"" + fileName + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        return this.client.uploadArtifact(artifact, content, fileName);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#addVdb(org.komodo.teiid.model.vdb.Vdb, java.io.InputStream, java.lang.String)
     */
    @Override
    public BaseArtifactType addVdb(final Vdb vdb,
                                   final InputStream content,
                                   final String fileName) throws Exception {
        final BaseArtifactType vdbArtifact = addVdb(content, fileName);
        vdbArtifact.setDescription(vdb.getDescription());
        vdbArtifact.setName(vdb.getId());
        vdbArtifact.setVersion(Integer.toString(vdb.getVersion()));

        return vdbArtifact;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.CleanableRepositoryManager#clean()
     */
    @Override
    public void clean() throws Exception {
        this.cleaner.clean();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#shutdown()
     */
    @Override
    public void shutdown() throws Exception {
        EmbeddedContainer.stop();
        PersistenceFactory.newInstance().shutdown();
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
