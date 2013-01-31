/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.komodo.repository.artifact.VdbArtifact;

/**
 * A test class of a {@link AddVdbCommand}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class AddVdbCommandTest extends ShellCommandTest {

    private KomodoCommand command;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.ShellCommandTest#getCommand()
     */
    @Override
    protected KomodoCommand getCommand() {
        if (this.command == null) {
            this.command = new AddVdbCommand();
        }

        return this.command;
    }

    @Test
    public void shouldAddTwitterVdb() throws Exception {
        this.command.doExecute(getFileName("vdb/twitterVdb.xml"));
        this.settings.artifactType = VdbArtifact.TYPE;
        assertThat(_repoMgr.query(this.settings).size(), is(1L));
    }

}
