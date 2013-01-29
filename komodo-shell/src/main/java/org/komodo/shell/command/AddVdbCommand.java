/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import java.io.InputStream;
import org.komodo.common.i18n.I18n;
import org.komodo.common.util.Precondition;
import org.komodo.repository.RepositoryManager;
import org.komodo.shell.ShellI18n;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A shell command that adds a VDB to the repository.  
 */
public class AddVdbCommand extends KomodoCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddVdbCommand.class);

    /**
     * The unqualified name of the add VDB command. Value is {@value}.
     */
    public static final String NAME = "addVdb"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.KomodoCommand#doExecute(java.lang.String[])
     */
    @Override
    protected void doExecute(final String... args) throws Exception {
        Precondition.notNull(args, "args"); //$NON-NLS-1$
        Precondition.sizeIs(args, 1, "args"); //$NON-NLS-1$
        Precondition.notEmpty(args[0], "fileName"); //$NON-NLS-1$

        final String fileName = args[0];
        LOGGER.debug("Executing AddVdbCommand.doExcecute with file name '{}'", fileName); //$NON-NLS-1$

        InputStream vdbStream = null;

        try {
            vdbStream = getResourceAsStream(fileName);
        } catch (final Exception e) {
            print(e.getMessage());
            LOGGER.error(I18n.bind(ShellI18n.failedToOpenStream, fileName), e);
            return;
        }

        final RepositoryManager repoMgr = getRepositoryManager();

        if (repoMgr == null) {
            print(ShellI18n.connectionNotFound);
            return;
        }

        final BaseArtifactType vdbArtifact = repoMgr.addVdb(vdbStream);

        if (vdbArtifact == null) {
            print(I18n.bind(ShellI18n.vdbArtifactMissingAfterAdd, fileName));
        } else {
            print(I18n.bind(ShellI18n.vdbAddedToRepository, vdbArtifact.getName()));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#printHelp()
     */
    @Override
    public void printHelp() {
        print(ShellI18n.addVdbCommandHelp);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#printUsage()
     */
    @Override
    public void printUsage() {
        print(ShellI18n.addVdbCommandUsage);
    }

}
