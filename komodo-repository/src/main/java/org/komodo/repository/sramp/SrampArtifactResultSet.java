/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp;

import java.util.Iterator;
import org.komodo.common.util.Precondition;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.ArtifactResultSet;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * A collection of artifacts that are a result of a query to a S-RAMP repository.
 */
final class SrampArtifactResultSet implements ArtifactResultSet {

    final SrampRepository repository;
    private final Iterator<ArtifactSummary> delegate;
    private final int size;

    SrampArtifactResultSet(final QueryResultSet resultSet,
                           final SrampRepository repository) {
        Precondition.notNull(resultSet, "resultSet"); //$NON-NLS-1$
        Precondition.notNull(repository, "repository"); //$NON-NLS-1$

        this.delegate = resultSet.iterator();
        this.repository = repository;
        this.size = (int)resultSet.size();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Artifact> iterator() {
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Iterator#next()
     */
    @Override
    public Artifact next() {
        final ArtifactSummary summary = this.delegate.next();
        BaseArtifactType srampArtifact = null;

        try {
            srampArtifact = this.repository.getClient().getArtifactMetaData(summary.getType(), summary.getUuid());
        } catch (final Exception e) {
            throw new RuntimeException(e); // TODO add message here
        }

        return this.repository.getFactory().create(srampArtifact);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        this.delegate.remove();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.artifact.ArtifactResultSet#size()
     */
    @Override
    public int size() {
        return this.size;
    }

}
