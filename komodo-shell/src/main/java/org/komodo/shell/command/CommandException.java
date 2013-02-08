/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import org.komodo.common.i18n.I18n;
import org.komodo.shell.ShellI18n;

/**
 * An error running a command.
 */
@SuppressWarnings( "serial" )
class CommandException extends Exception {

    /**
     * @param command the command where the error occurred (cannot be <code>null</code>)
     * @param message the error message (can be <code>null</code> or empty)
     */
    CommandException(final KomodoCommand<?> command,
                     final String message) {
        this(command, message, null);
    }

    /**
     * @param command the command where the error occurred (cannot be <code>null</code>)
     * @param message the error message (can be <code>null</code> or empty)
     * @param error the error (can be <code>null</code>)
     */
    CommandException(final KomodoCommand<?> command,
                     final String message,
                     final Throwable error) {
        super(I18n.bind(ShellI18n.commandError, command.getClass().getSimpleName(), message), error);
    }

}
