/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import java.util.HashMap;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import org.komodo.repository.artifact.Artifact;

/**
 * A test class of a {@link GetVdbCommand}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class GetVdbCommandTest extends ShellCommandTest {

    private KomodoCommand command;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.command.ShellCommandTest#getCommand()
     */
    @Override
    protected KomodoCommand getCommand() {
        if (this.command == null) {
            this.command = new GetVdbCommand();
        }

        return this.command;
    }

    @Test
    public void shouldObtainOneTwitterVdb() throws Exception {
        final AddVdbCommand addCmd = new AddVdbCommand();
        addCmd.setContext(this.context);
        addCmd.doExecute(getFileName("vdb/twitterVdb.xml"), "twitterVdb.xml");

        final String vdbName = "twitter";
        final String version = "1";
        this.command.doExecute(vdbName, version);

        this.settings.artifactType = Artifact.Type.VDB;
        this.settings.params = new HashMap<String, String>();
        this.settings.params.put(Artifact.Property.NAME, vdbName);
        this.settings.params.put(Artifact.Property.VERSION, version);
        assertThat(_repoMgr.query(this.settings).size(), is(1L));
    }

    @Test
    public void shouldObtainTwoTwitterVdbs() throws Exception {
        { // add VDBs to repo
            final String vdbName = "twitterVdb.xml";
            final String fileName = getFileName("vdb/twitterVdb.xml");
            final AddVdbCommand addCmd = new AddVdbCommand();
            addCmd.setContext(this.context);
            addCmd.doExecute(fileName, vdbName);
            addCmd.doExecute(fileName, vdbName);
        }

        final String name = "twitter";
        final String version = "1";
        this.command.doExecute(name, "1");

        this.settings.artifactType = Artifact.Type.VDB;
        this.settings.params = new HashMap<String, String>();
        this.settings.params.put(Artifact.Property.NAME, name);
        this.settings.params.put(Artifact.Property.VERSION, version);
        assertThat(_repoMgr.query(this.settings).size(), is(2L));
    }

}
