/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import org.jboss.resteasy.test.EmbeddedContainer;
import org.overlord.sramp.repository.PersistenceFactory;
import org.overlord.sramp.repository.jcr.JCRRepositoryCleaner;

/**
 * A repository manager that uses the S-RAMP atom interface that can be used for testing.
 */
public class CleanableRepositoryManager extends AtomRepositoryManager implements Cleanable {

    private final JCRRepositoryCleaner cleaner = new JCRRepositoryCleaner();

    /**
     * Constructs a default s-ramp jetty/atom repository manager using localhost and default port.
     */
    public CleanableRepositoryManager() {
        super(String.format("%s:%d", "http://localhost", 8081)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.Cleanable#clean()
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

}
