/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * 
 */
public class ServerManagerTest {

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    private ServerManager getNewManager() {
        return new ServerManager(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullListenerForAddListener() {
        getNewManager().addListener(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForAddServer() {
        getNewManager().addServer(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullUrlForGetServer() {
        getNewManager().getServer(null);
    }

    @Test
    public void shouldAllowGetServers() {
        getNewManager().getServers();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForIsRegistered() {
        getNewManager().isRegistered(null);
    }

    @Test
    public void shouldConfirmServerIsRegistered() {
        getNewManager().isRegistered(mock(Server.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForRemoveServer() {
        getNewManager().removeServer(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullListenerForRemoveListener() {
        getNewManager().removeListener(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullReplacedServerForUpdateServer() {
        getNewManager().updateServer(null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullUpdatedServerForUpdateServer() {
        getNewManager().updateServer(mock(Server.class), null);
    }

    @Test
    public void shouldAllowSaveState() {
        getNewManager().saveState();
    }

}
