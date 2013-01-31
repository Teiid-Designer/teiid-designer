/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import org.junit.Before;
import org.komodo.repository.RepositoryManager;
import org.komodo.repository.RepositoryTest;
import org.komodo.shell.ShellConstants;
import org.overlord.sramp.shell.api.ShellContext;
import org.overlord.sramp.shell.api.SimpleShellContext;

/**
 * The base class for Komodo S-RAMP shell command tests.
 */
@SuppressWarnings( {"javadoc"} )
public abstract class ShellCommandTest extends RepositoryTest implements ShellConstants {

    protected ShellContext context;
    protected RepositoryManager.QuerySettings settings;

    protected abstract KomodoCommand getCommand();

    protected String getFileName(final String projectRelativePathToResource) {
        return getClass().getClassLoader().getResource(projectRelativePathToResource).getFile();
    }

    @Before
    public void setupContext() {
        this.settings = new RepositoryManager.QuerySettings();

        this.context = new SimpleShellContext();
        this.context.setVariable(KOMODO_CLIENT_QNAME, _repoMgr);
        getCommand().setContext(this.context);
    }

}
