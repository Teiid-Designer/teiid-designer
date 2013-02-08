package org.komodo.shell;

import javax.xml.namespace.QName;

/**
 * Komodo shell constants.
 */
public interface ShellConstants {

    /**
     * The shell namespace used when creating the qualified command name. Value is {@value}.
     */
    String NAMESPACE = "komodo"; //$NON-NLS-1$ 

    /**
     * The shell context variable name used for obtaining the SOA repository the shell is connected to.
     */
    QName CONNECTED_SOA_REPOSITORY = new QName(NAMESPACE, "connected-soa-repository"); //$NON-NLS-1$

    /**
     * The shell context variable name used for obtaining the default SOA repository URL.
     */
    QName DEFAULT_REPOSITORY_URL = new QName(NAMESPACE, "default-soa-repository-url"); //$NON-NLS-1$

    /**
     * The shell context variable name used for obtaining the command's SOA repository cache.
     */
    QName SOA_REPOSITORIES = new QName(NAMESPACE, "soa-repositories"); //$NON-NLS-1$

}
