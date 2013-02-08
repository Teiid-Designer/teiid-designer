/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import org.komodo.common.i18n.I18n;
import org.komodo.repository.SoaRepository;
import org.komodo.shell.ShellI18n;

/**
 * An error when the command needs a {@link SoaRepository repository} and one can't be found.
 */
@SuppressWarnings( "serial" )
class RepositoryNotFoundException extends Exception {

    RepositoryNotFoundException(final KomodoCommand<?> command) {
        super(I18n.bind(ShellI18n.repositoryNotFound, command.getClass().getSimpleName()));
    }

}
