/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.sramp.shell.commands;

import java.util.HashMap;
import java.util.Map;
import org.overlord.sramp.shell.ShellCommand;
import org.overlord.sramp.shell.ShellCommandProvider;

/**
 *
 */
public class KomodoCommandProvider implements ShellCommandProvider {

    /**
     * Constructor.
     */
    public KomodoCommandProvider() {
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommandProvider#getNamespace()
     */
    @Override
    public String getNamespace() {
        return "komodo";
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommandProvider#provideCommands()
     */
    @Override
    public Map<String, Class<? extends ShellCommand>> provideCommands() {
        final Map<String, Class<? extends ShellCommand>> rval = new HashMap<String, Class<? extends ShellCommand>>();
        rval.put("addVdb", AddVdbCommand.class);
        rval.put("connectKomodo", ConnectKomodoCommand.class);
        rval.put("getVdb", QueryForVdbCommand.class);
        return rval;
    }

}
