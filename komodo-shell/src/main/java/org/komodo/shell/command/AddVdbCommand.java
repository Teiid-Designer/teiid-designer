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
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.SoaRepository;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.teiid.VdbArtifact;
import org.komodo.shell.ShellI18n;

/**
 * A shell command that adds a VDB to the repository.  
 */
public class AddVdbCommand extends KomodoCommand<Artifact> {

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
    protected Artifact doExecute(final String... args) throws Exception {
        // check for incorrect number of args
        if (CollectionUtil.isEmpty(args) || (args.length > 1)) {
            throw new InvalidNumberArgsException(this, (CollectionUtil.isEmpty(args) ? 0 : args.length));
        }

        this.logger.debug("executing '{}' with cancelable '{}' and params '{}'", new Object[] {getClass().getSimpleName(), //$NON-NLS-1$
            isCancelable(), StringUtil.createDelimitedString(args)});

        final SoaRepository repository = getRepository();
        final String vdbFileName = args[0];

        try {
            final InputStream vdbStream = getResourceAsStream(vdbFileName);

            if (vdbStream == null) {
                throw new CommandException(this, I18n.bind(ShellI18n.failedToOpenStream, vdbFileName));
            }

            final Artifact vdbArtifact = repository.add(vdbStream, VdbArtifact.TYPE);

            if (vdbArtifact == null) {
                throw new CommandException(this, I18n.bind(ShellI18n.vdbArtifactMissingAfterAdd, vdbFileName));
            }

            print(I18n.bind(ShellI18n.vdbAddedToRepository, vdbArtifact.getArtifactName()));
            return vdbArtifact;
        } catch (final Exception e) {
            throw new CommandException(this, I18n.bind(ShellI18n.errorAddingVdb, vdbFileName), e);
        }
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
