/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import org.komodo.common.i18n.I18n;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.SoaRepository;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.ArtifactResultSet;
import org.komodo.repository.artifact.teiid.VdbArtifact;
import org.komodo.shell.ShellI18n;

/**
 * A shell command that finds all VDBs in a repository with a matching name and version. 
 */
public class GetVdbCommand extends KomodoCommand {

    /**
     * The unqualified name of the get VDB command. Value is {@value}.
     */
    public static final String NAME = "getVdb"; //$NON-NLS-1$

    /**
     * Constructs a non-cancelable command.
     */
    public GetVdbCommand() {
        super();
    }

    /**
     * @param cancelable indicates if the command is cancelable
     */
    public GetVdbCommand(final boolean cancelable) {
        super(cancelable);
    }

    @Override
    protected Object doExecute(final String... args) throws Exception {
        Precondition.notNull(args, "args"); //$NON-NLS-1$
        Precondition.notEmpty(args[0], "vdbName"); //$NON-NLS-1$
        Precondition.notEmpty(args[1], "version"); //$NON-NLS-1$

        this.logger.debug("executing '{}' with cancelable '{}' and params '{}'", new Object[] {getClass().getSimpleName(), //$NON-NLS-1$
            isCancelable(), StringUtil.createDelimitedString(args)});

        final String vdbName = args[0];
        final String version = args[1];

        final SoaRepository.QuerySettings settings = new SoaRepository.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        settings.params.put(Artifact.Property.NAME, vdbName);
        settings.params.put(Artifact.Property.VERSION, version);

        final SoaRepository repository = getRepository();

        if (repository == null) {
            print(ShellI18n.connectionNotFound);
            return ERROR;
        }

        final ArtifactResultSet results = repository.query(settings);

        if (results.size() == 0) {
            print(I18n.bind(ShellI18n.noMatchingVdbsFoundInRepository, vdbName, version));
        } else {
            print(I18n.bind(ShellI18n.matchingVdbsFoundInRepository, results.size(), vdbName, version));
        }

        return results;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommand#printHelp()
     */
    @Override
    public void printHelp() {
        print(ShellI18n.getVdbCommandHelp);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommand#printUsage()
     */
    @Override
    public void printUsage() {
        print(ShellI18n.getVdbCommandUsage);
    }

}
