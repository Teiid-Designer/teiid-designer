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
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.ArtifactResultSet;
import org.komodo.teiid.model.vdb.Vdb;

/**
 * A test class of a {@link GetVdbCommand}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class GetVdbCommandTest extends ShellCommandTest<ArtifactResultSet> {

    private GetVdbCommand command;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.ShellCommandTest#getCommand()
     */
    @Override
    protected GetVdbCommand getCommand() {
        if (this.command == null) {
            this.command = new GetVdbCommand();
        }

        return this.command;
    }

    @Test
    public void shouldAllowOnlyOneArg() throws Exception {
        // add VDB to repo
        final AddVdbCommand addCmd = new AddVdbCommand();
        addCmd.setContext(this.context);
        addCmd.doExecute(getFileName("vdb/twitterVdb.xml"));

        // run command
        final String vdbName = "twitter";
        final ArtifactResultSet resultSet = this.command.doExecute(vdbName);

        // verify results
        assertThat(resultSet.size(), is(1));

        final Artifact vdbArtifact = resultSet.next();
        assertThat(vdbArtifact.getArtifactName(), is(vdbName));
        assertThat(vdbArtifact.getArtifactVersion(), is(Vdb.DEFAULT_VERSION));
    }

    @Test( expected = InvalidNumberArgsException.class )
    public void shouldHaveErrorIfMoreThanTwoArgs() throws Exception {
        this.command.doExecute("arg1", "arg2", "arg3");
    }

    @Test( expected = InvalidNumberArgsException.class )
    public void shouldHaveErrorIfNoArgs() throws Exception {
        this.command.doExecute();
    }

    @Test
    public void shouldObtainOneTwitterVdb() throws Exception {
        // add VDB to repo
        final AddVdbCommand addCmd = new AddVdbCommand();
        addCmd.setContext(this.context);
        addCmd.doExecute(getFileName("vdb/twitterVdb.xml"));

        // run command
        final String vdbName = "twitter";
        final String version = "1";
        final ArtifactResultSet resultSet = this.command.doExecute(vdbName, version);

        // verify results
        assertThat(resultSet.size(), is(1));

        final Artifact vdbArtifact = resultSet.next();
        assertThat(vdbArtifact.getArtifactName(), is(vdbName));
        assertThat(vdbArtifact.getArtifactVersion(), is(version));
    }

    @Test
    public void shouldObtainTwoTwitterVdbs() throws Exception {
        { // add VDBs to repo
            final String fileName = getFileName("vdb/twitterVdb.xml");
            final AddVdbCommand addCmd = new AddVdbCommand();
            addCmd.setContext(this.context);
            addCmd.doExecute(fileName);
            addCmd.doExecute(fileName);
        }

        // run command
        final String vdbName = "twitter";
        final String version = "1";
        final ArtifactResultSet resultSet = this.command.doExecute(vdbName, version);

        // verify results
        assertThat(resultSet.size(), is(2));

        { // first VDB
            final Artifact vdbArtifact = resultSet.next();
            assertThat(vdbArtifact.getArtifactName(), is(vdbName));
            assertThat(vdbArtifact.getArtifactVersion(), is(version));
        }

        { // second VDB
            final Artifact vdbArtifact = resultSet.next();
            assertThat(vdbArtifact.getArtifactName(), is(vdbName));
            assertThat(vdbArtifact.getArtifactVersion(), is(version));
        }
    }

}
