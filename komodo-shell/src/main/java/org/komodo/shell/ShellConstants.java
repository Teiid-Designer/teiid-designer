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
     * The shell context variable name for the command's repository.
     */
    QName KOMODO_REPOSITORY_QNAME = new QName(NAMESPACE, "soa-repository"); //$NON-NLS-1$

}
