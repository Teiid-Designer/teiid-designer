/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.RepositoryManager;
import org.komodo.shell.ShellConstants;
import org.overlord.sramp.shell.AbstractShellCommand;
import org.overlord.sramp.shell.commands.Arguments;

/**
 * A base class for Komodo commands.
 */
public abstract class KomodoCommand extends AbstractShellCommand implements ShellConstants {

    /**
     * @param args the command arguments (can be <code>null</code> or empty)
     * @throws Exception if there is a problem executing the command
     */
    abstract void doExecute(final String... args) throws Exception;

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#execute()
     */
    @Override
    public final void execute() throws Exception {
        final Arguments arguments = getArguments();

        if (arguments.isEmpty()) {
            doExecute(StringUtil.EMPTY_ARRAY);
        } else {
            doExecute(arguments.toArray(new String[arguments.size()]));
        }
    }

    protected RepositoryManager getRepositoryManager() {
        return (RepositoryManager)getContext().getVariable(KOMODO_CLIENT_QNAME);
    }

    /**
     * Obtains the content of a file resource.
     * 
     * @param fileName the file name relative to the calling class (cannot be <code>null</code> or empty)
     * @return the input stream to the content; may be <code>null</code> if the resource does not exist
     * @throws FileNotFoundException 
     */
    protected InputStream getResourceAsStream(final String fileName) throws FileNotFoundException {
        Precondition.notEmpty(fileName, "fileName"); //$NON-NLS-1$
        return new FileInputStream(fileName);
    }

}
