/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import org.junit.Before;
import org.komodo.repository.SoaRepository;
import org.komodo.repository.sramp.SrampTest;
import org.komodo.shell.ShellConstants;
import org.overlord.sramp.shell.api.ShellContext;
import org.overlord.sramp.shell.api.SimpleShellContext;

/**
 * The base class for Komodo S-RAMP shell command tests.
 */
@SuppressWarnings( {"javadoc"} )
public abstract class ShellCommandTest<T> extends SrampTest implements ShellConstants {

    protected ShellContext context;
    protected SoaRepository.QuerySettings settings;

    protected abstract KomodoCommand<T> getCommand();

    protected String getFileName(final String projectRelativePathToResource) {
        return getClass().getClassLoader().getResource(projectRelativePathToResource).getFile();
    }

    @Before
    public void setupContext() {
        this.settings = new SoaRepository.QuerySettings();

        this.context = new SimpleShellContext();
        this.context.setVariable(SOA_REPOSITORIES, _repositories);
        this.context.setVariable(CONNECTED_SOA_REPOSITORY, _repository);
        this.context.setVariable(DEFAULT_REPOSITORY_URL, _repository.getUrl());
        getCommand().setContext(this.context);
    }

}
