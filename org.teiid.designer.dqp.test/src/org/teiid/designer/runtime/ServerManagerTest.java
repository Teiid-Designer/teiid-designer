/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * 
 */
public class ServerManagerTest {
    private static final String RESTORED_SERVER1_URL = "mm://localhost:8080";
    private static final String RESTORED_SERVER1_USER = "user8080";

    private static final String RESTORED_SERVER2_URL = "mm://localhost:8180";
    private static final String RESTORED_SERVER2_USER = "user8180";

    private static final String RESTORED_SERVER3_URL = "mm://localhost:8280";
    private static final String RESTORED_SERVER3_USER = "user8280";
    
    private static final String SERVER1_URL = "mm://server:4321";
    private static final Server SERVER1 = new Server(SERVER1_URL, "userA", "pwdA", false, mock(EventManager.class));

    private ServerManager mgr;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        this.mgr = new ServerManager(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullListenerForAddListener() {
        this.mgr.addListener(null);
    }

    @Test
    public void shouldConfirmRegisteredListenerIsNotified() {
        TestListener listener = new TestListener();
        this.mgr.addListener(listener);

        // generate event
        this.mgr.addServer(SERVER1);
        
        // make sure listener is notified
        assertThat(listener.wasNotified(), is(true));
    }

    @Test
    public void shouldConfirmUnregisteredListenerIsNotNotified() {
        // assume adding a listener and getting notified works as that is another test
        TestListener listener = new TestListener();
        this.mgr.addListener(listener);
        listener.clearNotifiedFlag();
        
        // remove listener
        this.mgr.removeListener(listener);

        // generate event
        this.mgr.addServer(SERVER1);
        
        // make sure listener is NOT notified
        assertThat(listener.wasNotified(), is(false));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForAddServer() {
        this.mgr.addServer(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullUrlForGetServer() {
        this.mgr.getServer(null);
    }

    @Test
    public void shouldAllowGetServers() {
        this.mgr.getServers();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForIsRegistered() {
        this.mgr.isRegistered(null);
    }

    @Test
    public void shouldConfirmServerIsRegistered() {
        assertThat(this.mgr.addServer(SERVER1).isOK(), is(true));
        assertThat(this.mgr.isRegistered(SERVER1), is(true));
    }

    @Test
    public void shouldConfirmServerIsRemoved() {
        // first add
        this.mgr.addServer(SERVER1);
        assertThat(this.mgr.isRegistered(SERVER1), is(true));

        // now remove
        assertThat(this.mgr.removeServer(SERVER1).isOK(), is(true));
        assertThat(this.mgr.isRegistered(SERVER1), is(false));
    }
    
    @Test
    public void shouldConfirmServerIsNotAddedMultipleTimes() {
        // add
        this.mgr.addServer(SERVER1);
        assertThat(this.mgr.getServers().size(), is(1));

        // add again
        this.mgr.addServer(SERVER1);
        assertThat(this.mgr.getServers().size(), is(1));
    }

    @Test
    public void shouldConfirmServerIsNotRegistered() {
        assertThat(this.mgr.isRegistered(SERVER1), is(false));
    }
    
    @Test
    public void shouldGetServerByUrl() {
        this.mgr.addServer(SERVER1);
        assertThat(this.mgr.getServer(SERVER1_URL), is(SERVER1));
    }
    
    @Test
    public void shouldRestoreServerRegistry() {
        this.mgr = new ServerManager("testdata");
        this.mgr.restoreState();
        assertThat(this.mgr.getServers().size(), is(3));
        
        Server server = this.mgr.getServer(RESTORED_SERVER1_URL);
        assertThat(server, notNullValue());
        assertThat(server.getUser(), is(RESTORED_SERVER1_USER));
        
        server = this.mgr.getServer(RESTORED_SERVER2_URL);
        assertThat(server, notNullValue());
        assertThat(server.getUser(), is(RESTORED_SERVER2_USER));
        
        server = this.mgr.getServer(RESTORED_SERVER3_URL);
        assertThat(server, notNullValue());
        assertThat(server.getUser(), is(RESTORED_SERVER3_USER));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForRemoveServer() {
        this.mgr.removeServer(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullListenerForRemoveListener() {
        this.mgr.removeListener(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullReplacedServerForUpdateServer() {
        this.mgr.updateServer(null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullUpdatedServerForUpdateServer() {
        this.mgr.updateServer(mock(Server.class), null);
    }

    @Test
    public void shouldAllowSaveState() {
        this.mgr.saveState();
    }

    class TestListener implements IExecutionConfigurationListener {

        private boolean notified;
        
        public void clearNotifiedFlag() {
            this.notified = false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.runtime.IExecutionConfigurationListener#configurationChanged(org.teiid.designer.runtime.ExecutionConfigurationEvent)
         */
        @Override
        public void configurationChanged( ExecutionConfigurationEvent event ) {
            this.notified = true;
        }

        public boolean wasNotified() {
            return this.notified;
        }
    }

}
