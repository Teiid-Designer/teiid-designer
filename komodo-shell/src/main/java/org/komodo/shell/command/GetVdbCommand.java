/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import java.util.HashMap;
import org.komodo.common.i18n.I18n;
import org.komodo.common.util.Precondition;
import org.komodo.repository.RepositoryManager;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.VdbArtifact;
import org.komodo.shell.ShellI18n;
import org.overlord.sramp.client.query.QueryResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A shell command that finds all VDBs in a repository with a matching name and version. 
 */
public class GetVdbCommand extends KomodoCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetVdbCommand.class);

    /**
     * The unqualified name of the get VDB command. Value is {@value}.
     */
    public static final String NAME = "getVdb"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.KomodoCommand#doExecute(java.lang.String[])
     */
    @Override
    void doExecute(final String... args) throws Exception {
        Precondition.notNull(args, "args"); //$NON-NLS-1$
        Precondition.notEmpty(args[0], "vdbName"); //$NON-NLS-1$
        Precondition.notEmpty(args[1], "version"); //$NON-NLS-1$

        final String vdbName = args[0];
        final String version = args[1];
        LOGGER.debug("Executing GetVdbCommand.doExcecute with VDB name '{}' and version '{}'", vdbName, version); //$NON-NLS-1$

        final RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        settings.params = new HashMap<String, String>();
        settings.params.put(Artifact.Property.NAME, vdbName);
        settings.params.put(Artifact.Property.VERSION, version);

        final RepositoryManager repoMgr = getRepositoryManager();

        if (repoMgr == null) {
            print(ShellI18n.connectionNotFound);
            return;
        }

        final QueryResultSet results = repoMgr.query(settings);

        if (results.size() == 0) {
            print(I18n.bind(ShellI18n.noMatchingVdbsFoundInRepository, vdbName, version));
        } else {
            print(I18n.bind(ShellI18n.matchingVdbsFoundInRepository, results.size(), vdbName, version));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#printHelp()
     */
    @Override
    public void printHelp() {
        print(ShellI18n.getVdbCommandHelp);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#printUsage()
     */
    @Override
    public void printUsage() {
        print(ShellI18n.getVdbCommandUsage);
    }

}
