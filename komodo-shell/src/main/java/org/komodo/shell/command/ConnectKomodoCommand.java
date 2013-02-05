/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import java.util.List;
import org.komodo.common.i18n.I18n;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.SoaRepositories;
import org.komodo.repository.SoaRepository;
import org.komodo.shell.ShellI18n;

/**
 * Connects to an s-ramp server using a Komodo client.
 */
public class ConnectKomodoCommand extends KomodoCommand {

    /**
     * The unqualified name of the connect to Komodo server command. Value is {@value}.
     */
    public static final String NAME = "connect"; //$NON-NLS-1$

    /**
     * Constructs a non-cancelable command.
     */
    public ConnectKomodoCommand() {
        super();
    }

    /**
     * @param cancelable indicates if the command is cancelable
     */
    public ConnectKomodoCommand(final boolean cancelable) {
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
        Precondition.notEmpty(args[0], "S-RAMP URL"); //$NON-NLS-1$

        this.logger.debug("executing '{}' with cancelable '{}' and params '{}'", new Object[] {getClass().getSimpleName(), //$NON-NLS-1$
            isCancelable(), StringUtil.createDelimitedString(args)});

        String endpointUrlArg = args[0];

        if (!endpointUrlArg.startsWith("http")) { //$NON-NLS-1$
            endpointUrlArg = "http://" + endpointUrlArg; //$NON-NLS-1$
        }

        final SoaRepositories repositories = new SoaRepositories();
        final SoaRepository repository = repositories.get(endpointUrlArg);
        getContext().setVariable(KOMODO_REPOSITORY_QNAME, repository);
        print(I18n.bind(ShellI18n.successfulConnection, endpointUrlArg));
        return repository;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommand#printHelp()
     */
    @Override
    public void printHelp() {
        print(ShellI18n.connectKomodoCommandHelp);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommand#printUsage()
     */
    @Override
    public void printUsage() {
        print(ShellI18n.connectKomodoCommandUsage);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.AbstractShellCommand#tabCompletion(java.lang.String, java.util.List)
     */
    @Override
    public int tabCompletion(final String lastArgument,
                             final List<CharSequence> candidates) {
        if (getArguments().isEmpty()) {
            candidates.add("http://localhost:8080/s-ramp-server"); //$NON-NLS-1$
            return 0;
        }

        return -1;
    }

}
