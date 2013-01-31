/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import java.util.HashMap;
import java.util.Map;
import org.komodo.shell.ShellConstants;
import org.overlord.sramp.shell.api.ShellCommand;
import org.overlord.sramp.shell.api.ShellCommandProvider;

/**
 * Provides the Komodo commands used by the shell.
 */
public class KomodoCommandProvider implements ShellCommandProvider {

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommandProvider#getNamespace()
     */
    @Override
    public String getNamespace() {
        return ShellConstants.NAMESPACE;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommandProvider#provideCommands()
     */
    @Override
    public Map<String, Class<? extends ShellCommand>> provideCommands() {
        final Map<String, Class<? extends ShellCommand>> commands = new HashMap<String, Class<? extends ShellCommand>>();
        commands.put(AddVdbCommand.NAME, AddVdbCommand.class);
        commands.put(ConnectKomodoCommand.NAME, ConnectKomodoCommand.class);
        commands.put(GetVdbCommand.NAME, GetVdbCommand.class);
        return commands;
    }

}
