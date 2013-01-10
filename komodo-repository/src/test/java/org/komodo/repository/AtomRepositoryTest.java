/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;


/**
 * Base class for tests using the default embedded S-RAMP repository.
 */
public abstract class AtomRepositoryTest extends RepositoryTest {

    private static final RepositoryManager ATOM_REP_MGR = new AtomRepositoryManager();

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryTest#getRepositoryManager()
     */
    @Override
    protected RepositoryManager getRepositoryManager() {
        return ATOM_REP_MGR;
    }
}
