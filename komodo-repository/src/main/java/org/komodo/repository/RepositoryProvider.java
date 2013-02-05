/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

/**
 * A provider of repository instances.
 */
public interface RepositoryProvider {

    /**
     * @param url the URL of the repository being created (cannot be <code>null</code>)
     * @return the repository (never <code>null</code>)
     * @throws Exception if there was a problem creating the repository
     */
    SoaRepository create(final String url) throws Exception;

    /**
     * @return the type of repository (never <code>null</code> or empty)
     */
    String getType();

}
