/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.komodo.common.util.Precondition;

/**
 * A cached set o repository managers.
 */
public class Repositories {

    private final ConcurrentMap<String, RepositoryManager> repositories = new ConcurrentHashMap<String, RepositoryManager>();

    /**
     * If repository manager is created, a connection to the repository is attempted.
     * 
     * @param url the URL of the repository whose repository manager is being requested (cannot be <code>null</code> or empty)
     * @return the repository manager (never <code>null</code>)
     * @throws Exception if there is a problem with obtaining the repository manager
     */
    public RepositoryManager get(final String url) throws Exception {
        return get(url, true);
    }

    /**
     * @param url the URL of the repository whose repository manager is being requested (cannot be <code>null</code> or empty)
     * @param connectWhenCreating <code>true</code> if when creating the repository manager a connection should be attempted
     * @return the repository manager (never <code>null</code>)
     * @throws Exception if there is a problem with obtaining the repository manager
     */
    public RepositoryManager get(final String url,
                                 final boolean connectWhenCreating) throws Exception {
        Precondition.notEmpty(url, "url"); //$NON-NLS-1$
        RepositoryManager repoMgr = this.repositories.get(url);

        if (repoMgr == null) {
            repoMgr = new AtomRepositoryManager(url, connectWhenCreating);
            this.repositories.putIfAbsent(url, repoMgr);
        }

        return repoMgr;
    }

    /**
     * @param url the URL of the repository whose repository manager is being removed (cannot be <code>null</code> or empty)
     * @return <code>true</code> if the repository manager was removed
     */
    public boolean remove(String url) {
        Precondition.notEmpty(url, "url"); //$NON-NLS-1$
        RepositoryManager repoMgr = this.repositories.get(url);
        return ((repoMgr == null) ? false : this.repositories.remove(url, repoMgr));
    }

}
