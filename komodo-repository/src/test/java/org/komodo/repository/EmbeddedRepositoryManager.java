/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.io.InputStream;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.ArtifactFactory;
import org.komodo.teiid.model.vdb.Vdb;
import org.overlord.sramp.repository.DerivedArtifacts;
import org.overlord.sramp.repository.DerivedArtifactsFactory;
import org.overlord.sramp.repository.PersistenceFactory;
import org.overlord.sramp.repository.PersistenceManager;
import org.overlord.sramp.repository.QueryManager;
import org.overlord.sramp.repository.QueryManagerFactory;
import org.overlord.sramp.repository.jcr.JCRRepositoryCleaner;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The artifact repository manager. 
 */
public class EmbeddedRepositoryManager implements CleanableRepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedRepositoryManager.class);

    private final JCRRepositoryCleaner cleaner = new JCRRepositoryCleaner();
    private final DerivedArtifacts derivedArtifacts;
    private final PersistenceManager persistenceManager;
    private final QueryManager queryManager;

    /**
     * Constructs a default embedded s-ramp repository manager.
     */
    public EmbeddedRepositoryManager() {
        this.derivedArtifacts = DerivedArtifactsFactory.newInstance();
        this.persistenceManager = PersistenceFactory.newInstance();
        this.queryManager = QueryManagerFactory.newInstance();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#addVdb(java.io.InputStream, java.lang.String)
     */
    @Override
    public BaseArtifactType addVdb(final InputStream content,
                                   final String fileName) throws Exception {
        final UserDefinedArtifactType artifact = ArtifactFactory.create(Artifact.Type.VDB);
        LOGGER.debug("Adding VDB with file name \"" + fileName + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        return this.persistenceManager.persistArtifact(artifact, content);
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

    //
    //    private ArtifactSet executeQuery(final String xpathTemplate) throws Exception {
    //        final SrampQuery query = getQueryManager().createQuery(xpathTemplate);
    //        LOGGER.debug("Executing query: '{}'", xpathTemplate); // TODO i18n
    //        final ArtifactSet results = query.executeQuery();
    //        LOGGER.debug("Query returned '{}' result(s)", results.size()); // TODO i18n
    //        return results;
    //    }
    //
    //    /**
    //     * @param artifact the artifact being checked (cannot be <code>null</code>)
    //     * @return <code>true</code> if the artifact exists in the repository
    //     * @throws Exception if there is a problem accessing the repository
    //     */
    //    public boolean exists(final Artifact artifact) throws Exception {
    //        Precondition.notNull(artifact, "artifact"); //$NON-NLS-1$
    //        return exists(artifact.getFullName(), artifact.getType());
    //    }
    //
    //    /**
    //     * @param artifactFullName the name of artifact, include parent path, being checked (cannot be <code>null</code>)
    //     * @param artifactType the type of the artifact (cannot be <code>null</code> or empty)
    //     * @return <code>true</code> if the artifact exists in the repository
    //     * @throws Exception if there is a problem accessing the repository
    //     */
    //    public boolean exists(final String artifactFullName,
    //                          final String artifactType) throws Exception {
    //        return (get(artifactFullName, artifactType) != null);
    //    }
    //
    //    /**
    //     * @param artifactFullName the name of artifact, include parent path, being checked (cannot be <code>null</code>)
    //     * @param artifactType the type of the artifact (cannot be <code>null</code> or empty)
    //     * @return the artifact or <code>null</code> if it does not exist in the repository
    //     * @throws Exception if there is a problem accessing the repository
    //     */
    //    public Artifact get(final String artifactFullName,
    //                        final String artifactType) throws Exception {
    //        final String xpathTemplate = getXpathTemplate(artifactFullName, artifactType);
    //        final ArtifactSet results = executeQuery(xpathTemplate);
    //
    //        if (results.size() == 1) {
    //            final BaseArtifactType artifact = results.iterator().next();
    //
    //            if ((artifact instanceof UserDefinedArtifactType)
    //                && artifactType.equals(((UserDefinedArtifactType)artifact).getUserType())) {
    //                return ArtifactFactory.create((UserDefinedArtifactType)artifact);
    //            }
    //        }
    //
    //        return null;
    //    }
    //
    //    /**
    //     * @param artifact the artifact whose content is being requested (cannot be <code>null</code>) 
    //     * @return the content (must be closed by caller)
    //     * @throws Exception if there is a problem obtaining the content
    //     */
    //    public InputStream get(Artifact artifact) throws Exception {
    //        Precondition.notNull(artifact, "artifact"); //$NON-NLS-1$
    //        return getPersistenceManager().getArtifactContent(artifact.getDelegate().getUuid(),
    //                                                          ArtifactType.valueOf(artifact.getDelegate()));
    //    }
    //
    //    /**
    //     * @return the derived artifacts (never <code>null</code>)
    //     */
    //    public DerivedArtifacts getDerivedArtifacts() {
    //        return this.derivedArtifacts;
    //    }
    //
    //    /**
    //     * @return the persistence manager (never <code>null</code>_
    //     */
    //    public PersistenceManager getPersistenceManager() {
    //        return this.persistenceManager;
    //    }
    //
    //    /**
    //     * @return the query manager (never <code>null</code>)
    //     */
    //    public QueryManager getQueryManager() {
    //        return this.queryManager;
    //    }
    //
    //    /**
    //     * @param artifactFullName the name of the artifact including parent path (cannot be <code>null</code> or empty)
    //     * @param artifactType the artifact user type (cannot be <code>null</code> or empty)
    //     * @return the XPath template (never <code>null</code>)
    //     */
    //    public String getXpathTemplate(final String artifactFullName,
    //                                   final String artifactType) {
    //        Precondition.notEmpty(artifactFullName, "artifactFullName"); //$NON-NLS-1$
    //        Precondition.notEmpty(artifactType, "artifactType"); //$NON-NLS-1$
    //
    //        return RepositoryConstants.Sramp.USER_DEFINED_ARTIFACT_PATH + artifactType + "[@name = '" //$NON-NLS-1$
    //               + artifactFullName + "' ]"; //$NON-NLS-1$
    //    }
    //
    //    /**
    //     * @param artifact the artifact being persisted (cannot be <code>null</code>)
    //     * @param content the artifact content being saved (cannot be <code>null</code>)
    //     * @throws Exception if there is a problem saving the artifact to the repository
    //     */
    //    public void persist(final Artifact artifact,
    //                        final InputStream content) throws Exception {
    //        Precondition.notNull(artifact, "artifact"); //$NON-NLS-1$
    //        Precondition.notNull(content, "content "); //$NON-NLS-1$
    //        getPersistenceManager().persistArtifact(artifact.getDelegate(), content);
    //    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#shutdown()
     */
    @Override
    public void shutdown() throws Exception {
        this.persistenceManager.shutdown();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryManager#start()
     */
    @Override
    public void start() throws Exception {
        // nothing to do
    }
}
