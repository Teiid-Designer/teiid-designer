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
import org.komodo.repository.AtomRepositoryManager;
import org.komodo.shell.ShellI18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connects to an s-ramp server using a Komodo client.
 *
 */
public class ConnectKomodoCommand extends KomodoCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectKomodoCommand.class);

    /**
     * The unqualified name of the connect to Komodo server command. Value is {@value}.
     */
    public static final String NAME = "connectKomodo"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.KomodoCommand#doExecute(java.lang.String[])
     */
    @Override
    void doExecute(final String... args) throws Exception {
        Precondition.notNull(args, "args"); //$NON-NLS-1$
        Precondition.notEmpty(args[0], "S-RAMP URL"); //$NON-NLS-1$

        String endpointUrlArg = args[0];
        LOGGER.debug("Executing ConnectKomodoCommand.doExcecute with URL '{}'", endpointUrlArg); //$NON-NLS-1$

        if (!endpointUrlArg.startsWith("http")) { //$NON-NLS-1$
            endpointUrlArg = "http://" + endpointUrlArg; //$NON-NLS-1$
        }

        try {
            final AtomRepositoryManager client = new AtomRepositoryManager(endpointUrlArg, true);
            getContext().setVariable(KOMODO_CLIENT_QNAME, client);
            print(I18n.bind(ShellI18n.successfulConnection, endpointUrlArg));
        } catch (final Exception e) {
            final String msg = I18n.bind(ShellI18n.failedConnection, endpointUrlArg);
            print(msg);
            print("\t" + e.getMessage()); //$NON-NLS-1$
            LOGGER.error(msg, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#printHelp()
     */
    @Override
    public void printHelp() {
        print(ShellI18n.connectKomodoCommandHelp);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#printUsage()
     */
    @Override
    public void printUsage() {
        print(ShellI18n.connectKomodoCommandUsage);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.AbstractShellCommand#tabCompletion(java.lang.String, java.util.List)
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
