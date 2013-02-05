/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp;

import org.komodo.repository.RepositoryProvider;
import org.komodo.repository.SoaRepository;

/**
 * Provides S-RAMP repository clients.
 */
public final class SrampRepositoryProvider implements RepositoryProvider {

    static final String TYPE = "S-RAMP"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryProvider#create(java.lang.String)
     */
    @Override
    public SoaRepository create(final String url) throws Exception {
        return new SrampRepository(url);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.RepositoryProvider#getType()
     */
    @Override
    public String getType() {
        return TYPE;
    }

}
