/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.komodo.common.util.Precondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A cached set o repositories.
 */
public class SoaRepositories {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoaRepositories.class);

    private ConcurrentMap<String, RepositoryProvider> providers;
    private ConcurrentMap<String, SoaRepository> repositories;

    /**
     * If a repository is created, a connection to the repository is attempted.
     * 
     * @param url the URL of the repository being requested (cannot be <code>null</code> or empty)
     * @return the repository (never <code>null</code>)
     * @throws Exception if there is a problem with obtaining the repository
     */
    public SoaRepository get(final String url) throws Exception {
        return get(url, true);
    }

    /**
     * @param url the URL of the repository being requested (cannot be <code>null</code> or empty)
     * @param connectWhenCreating <code>true</code> if a connection should be attempted when creating the repository
     * @return the repository (never <code>null</code>)
     * @throws Exception if there is a problem with obtaining the repository
     */
    public SoaRepository get(final String url,
                             final boolean connectWhenCreating) throws Exception {
        Precondition.notEmpty(url, "url"); //$NON-NLS-1$

        if (this.repositories == null) {
            this.providers = new ConcurrentHashMap<String, RepositoryProvider>();

            // load repository providers
            for (final RepositoryProvider provider : ServiceLoader.load(RepositoryProvider.class)) {
                this.providers.putIfAbsent(provider.getType(), provider);
                LOGGER.debug("Added repository provider with type '{}' and class '{}'", provider.getType(), provider.getClass()); //$NON-NLS-1$
            }

            if (this.providers.isEmpty()) {
                throw new IllegalStateException(); // TODO add message
            }

            this.repositories = new ConcurrentHashMap<String, SoaRepository>();
        }

        SoaRepository repository = this.repositories.get(url);

        if (repository == null) {
            // TODO would be nice to be able to choose provider but just take first one which is only one for now
            final RepositoryProvider provider = this.providers.values().iterator().next();
            repository = provider.create(url);
            LOGGER.debug("Added repository '{}' of type '{}' to repositories cache", url, repository.getClass()); //$NON-NLS-1$
            this.repositories.putIfAbsent(url, repository);

            if (connectWhenCreating) {
                LOGGER.debug("Connecting to repository '{}' ...", url); //$NON-NLS-1$
                repository.connect();
                LOGGER.debug("Successfully connected to repository '{}'", url); //$NON-NLS-1$
            }
        }

        return repository;
    }

    /**
     * Removes the repository from the cache and then calls disconnect.
     * 
     * @param url the URL of the repository being removed (cannot be <code>null</code> or empty)
     * @return <code>true</code> if the repository was removed
     * @throws Exception if there was a problem removing the repository from the cache
     */
    public boolean remove(final String url) throws Exception {
        Precondition.notEmpty(url, "url"); //$NON-NLS-1$
        final SoaRepository repository = this.repositories.get(url);

        if (repository == null) {
            return false;
        }

        if (this.repositories.remove(url, repository)) {
            LOGGER.debug("Removing repository '{}' of type '{}' from repositories cache", url, repository.getClass()); //$NON-NLS-1$
            repository.disconnect();
            return true;
        }

        return false;
    }

}
