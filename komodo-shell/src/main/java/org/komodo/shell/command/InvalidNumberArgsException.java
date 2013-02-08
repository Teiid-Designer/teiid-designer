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
 * An error when the number of arguments passed to a command is invalid.
 */
@SuppressWarnings( "serial" )
class InvalidNumberArgsException extends Exception {

    InvalidNumberArgsException(final KomodoCommand<?> command,
                               final int numberOfArgs) {
        super(I18n.bind(ShellI18n.invalidNumberCommandArgs, command.getClass().getSimpleName(), numberOfArgs));
    }

}
