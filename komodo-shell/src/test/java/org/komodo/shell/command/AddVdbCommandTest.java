/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.teiid.VdbArtifact;

/**
 * A test class of a {@link AddVdbCommand}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class AddVdbCommandTest extends ShellCommandTest<Artifact> {

    private AddVdbCommand command;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.ShellCommandTest#getCommand()
     */
    @Override
    protected AddVdbCommand getCommand() {
        if (this.command == null) {
            this.command = new AddVdbCommand();
        }

        return this.command;
    }

    @Test
    public void shouldAddTwitterVdb() throws Exception {
        // run command
        final Artifact result = this.command.doExecute(getFileName("vdb/twitterVdb.xml"));

        // verify result
        assertThat(result, is(notNullValue()));

        // verify in repo
        this.settings.artifactType = VdbArtifact.TYPE;
        assertThat(_repository.query(this.settings).size(), is(1));
    }

    @Test( expected = InvalidNumberArgsException.class )
    public void shouldHaveErrorIfMoreThanOneArg() throws Exception {
        this.command.doExecute("one", "two");
    }

    @Test( expected = InvalidNumberArgsException.class )
    public void shouldHaveErrorIfNoArgs() throws Exception {
        this.command.doExecute();
    }

    @Test( expected = CommandException.class )
    public void shouldHaveErrorIfVdbNotFound() throws Exception {
        this.command.doExecute("bogusPath");
    }

}
