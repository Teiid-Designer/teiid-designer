/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import org.komodo.common.util.StringUtil;

/**
 * An implementation of a {@link SoaRepositories} that can be seeded with {@link SoaRepository repositories}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class RepositoryTestCache extends SoaRepositories {

    public RepositoryTestCache() {
        this.repositories = new ConcurrentHashMap<String, SoaRepository>();
    }

    public RepositoryTestCache(final SoaRepository repository) {
        this();
        add(repository);
    }

    public void add(final SoaRepository repository) {
        assert (repository != null) : "repository cannot be null when adding to the repository test cache";
        this.repositories.putIfAbsent(repository.getUrl(), repository);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.repository.SoaRepositories#get(java.lang.String, boolean)
     */
    @Override
    public SoaRepository get(final String url,
                             final boolean connectWhenCreating) throws Exception {
        assert !StringUtil.isEmpty(url) : "url is null";
        final SoaRepository repository = this.repositories.get(url);

        if (repository == null) {
            throw new UnknownHostException(url);
        }

        return repository;
    }

}
