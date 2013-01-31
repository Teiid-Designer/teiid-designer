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
import org.komodo.common.util.StringUtil;
import org.komodo.repository.RepositoryManager;
import org.komodo.shell.ShellI18n;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * A shell command that adds a VDB to the repository.  
 */
public class AddVdbCommand extends KomodoCommand {

    /**
     * The unqualified name of the add VDB command. Value is {@value}.
     */
    public static final String NAME = "addVdb"; //$NON-NLS-1$

    /**
     * Constructs a non-cancelable command.
     */
    public AddVdbCommand() {
        super();
    }

    /**
     * @param cancelable indicates if the command is cancelable
     */
    public AddVdbCommand(final boolean cancelable) {
        super(cancelable);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.KomodoCommand#doExecute(java.lang.String[])
     */
    @Override
    protected Object doExecute(final String... args) throws Exception {
        Precondition.notNull(args, "args"); //$NON-NLS-1$
        Precondition.sizeIs(args, 1, "args"); //$NON-NLS-1$
        Precondition.notEmpty(args[0], "fileName"); //$NON-NLS-1$

        this.logger.debug("executing '{}' with cancelable '{}' and params '{}'", new Object[] {getClass().getSimpleName(), //$NON-NLS-1$
            isCancelable(), StringUtil.createDelimitedString(args)});

        final String fileName = args[0];
        final InputStream vdbStream = getResourceAsStream(fileName);

        if (vdbStream == null) {
            this.logger.error(I18n.bind(ShellI18n.failedToOpenStream, fileName));
            return ERROR;
        }

        final RepositoryManager repoMgr = getRepositoryManager();

        if (repoMgr == null) {
            print(ShellI18n.connectionNotFound);
            return ERROR;
        }

        final BaseArtifactType vdbArtifact = repoMgr.addVdb(vdbStream);

        if (vdbArtifact == null) {
            print(I18n.bind(ShellI18n.vdbArtifactMissingAfterAdd, fileName));
            return ERROR;
        }

        print(I18n.bind(ShellI18n.vdbAddedToRepository, vdbArtifact.getName()));

        return vdbArtifact;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommand#printHelp()
     */
    @Override
    public void printHelp() {
        print(ShellI18n.addVdbCommandHelp);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommand#printUsage()
     */
    @Override
    public void printUsage() {
        print(ShellI18n.addVdbCommandUsage);
    }

}
