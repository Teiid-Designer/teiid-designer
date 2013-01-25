/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell;

import org.komodo.common.i18n.I18n;

/**
 * Messages for the komodo-shell module.
 */
@SuppressWarnings( {"javadoc"} )
public class ShellI18n extends I18n {

    public static String addVdbCommandHelp;
    public static String addVdbCommandUsage;
    public static String connectionNotFound;
    public static String connectKomodoCommandHelp;
    public static String connectKomodoCommandUsage;
    public static String failedConnection;
    public static String failedToOpenStream;
    public static String getVdbCommandHelp;
    public static String getVdbCommandUsage;
    public static String matchingVdbsFoundInRepository;
    public static String noMatchingVdbsFoundInRepository;
    public static String successfulConnection;
    public static String vdbAddedToRepository;
    public static String vdbArtifactMissingAfterAdd;

    static {
        final ShellI18n i18n = new ShellI18n();
        i18n.initialize();
    }

    /**
     * Don't allow public construction.
     */
    private ShellI18n() {
        // nothing to do
    }

}
