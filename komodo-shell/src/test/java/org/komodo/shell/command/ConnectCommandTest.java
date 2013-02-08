/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import static org.junit.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import java.net.UnknownHostException;
import org.junit.Test;
import org.komodo.repository.SoaRepository;

/**
 * A test class of a {@link ConnectCommand}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class ConnectCommandTest extends ShellCommandTest<SoaRepository> {

    private ConnectCommand command;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.ShellCommandTest#getCommand()
     */
    @Override
    protected ConnectCommand getCommand() {
        if (this.command == null) {
            this.command = new ConnectCommand();
        }

        return this.command;
    }

    @Test( expected = InvalidNumberArgsException.class )
    public void shouldHaveErrorIfMoreThanOneArg() throws Exception {
        this.command.doExecute("one", "two");
    }

    @Test
    public void shouldAllowNoArgs() throws Exception {
        final SoaRepository repository = this.command.doExecute();
        assertNotNull(repository);
    }

    @Test
    public void shouldHaveErrorIfRepositoryNotFound() {
        Throwable cause = null;

        try {
            this.command.doExecute("bogusRepositoryUrl");
        } catch (Throwable e) {
            cause = e;
            while (cause != null) {
                if (cause instanceof UnknownHostException) {
                    break;
                }

                cause = cause.getCause();
            }
        }

        assertThat(cause, is(instanceOf(UnknownHostException.class)));
    }

}
