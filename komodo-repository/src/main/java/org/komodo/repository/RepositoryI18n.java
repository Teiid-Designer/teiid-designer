/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import org.komodo.common.i18n.I18n;

/**
 * Messages for the komodo-repository module.
 */
@SuppressWarnings( {"javadoc"} )
public class RepositoryI18n extends I18n {

    public static String missingVdbRootElement;
    public static String notVdbArtifact;
    public static String repositoryConnectionFailure;

    static {
        final RepositoryI18n i18n = new RepositoryI18n();
        i18n.initialize();
    }

    /**
     * Don't allow public construction.
     */
    private RepositoryI18n() {
        // nothing to do
    }

}
