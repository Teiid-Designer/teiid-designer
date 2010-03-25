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
import org.teiid.adminapi.AdminComponentException;

/**
 * 
 */
public class ServerTest {

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateServerWithNullUrl() {
        new Server(null, null, null, false, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateServerWithNullUser() {
        new Server("", null, null, false, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateServerWithNullEventManager() {
        new Server("", "", null, false, null);
    }

    @Test
    public void shouldCreateServer() {
        new Server("", "", null, false, mock(EventManager.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetAdminWithEmptyUrl() throws Exception {
        new Server("", "", null, false, mock(EventManager.class)).getAdmin();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetAdminWithEmptyUser() throws Exception {
        new Server("mm://server:1234", "", null, false, mock(EventManager.class)).getAdmin();
    }

    @Test( expected = AdminComponentException.class )
    public void shouldAllowGetAdmin() throws Exception {
        new Server("mm://server:1234", "user", null, false, mock(EventManager.class)).getAdmin();
    }

    @Test
    public void shouldAllowGetPassword() {
        assertThat(new Server("mm://server:1234", "user", "pwd", false, mock(EventManager.class)).getPassword(), notNullValue());
    }

    @Test
    public void shouldAllowGetUrl() {
        assertThat(new Server("mm://server:1234", "user", "pwd", false, mock(EventManager.class)).getUrl(), notNullValue());
    }

    @Test
    public void shouldAllowGetUser() {
        assertThat(new Server("mm://server:1234", "user", "pwd", false, mock(EventManager.class)).getUser(), notNullValue());
    }

    @Test
    public void shouldReturnFalseForEqualsWithNull() {
        assertThat(new Server("mm://server:1234", "user", "pwd", false, mock(EventManager.class)).equals(null), is(false));
    }

    @Test
    public void shouldReturnTrueForEqualsWithSelf() {
        Server server = new Server("mm://server:1234", "user", "pwd", false, mock(EventManager.class));
        assertThat(server.equals(server), is(true));
    }

    @Test
    public void shouldReturnTrueForEqualsWithSameInfoServer() {
        Server server1 = new Server("mm://server:1234", "userA", "pwdA", false, mock(EventManager.class));
        Server server2 = new Server("mm://server:1234", "userA", "pwdA", false, mock(EventManager.class));
        assertThat(server1.equals(server2), is(true));
    }

    @Test
    public void shouldReturnFalseForEqualsWithDifferentURLServer() {
        Server server1 = new Server("mm://server:4321", "userA", "pwdA", false, mock(EventManager.class));
        Server server2 = new Server("mm://server:1234", "userA", "pwdA", false, mock(EventManager.class));
        assertThat(server1.equals(server2), is(false));
    }

    @Test
    public void shouldReturnFalseForEqualsWithDifferentUserServer() {
        Server server1 = new Server("mm://server:1234", "userA", "pwdA", false, mock(EventManager.class));
        Server server2 = new Server("mm://server:1234", "userB", "pwdA", false, mock(EventManager.class));
        assertThat(server1.equals(server2), is(false));
    }

    @Test
    public void shouldReturnFalseForEqualsWithDifferentPasswordsServer() {
        Server server1 = new Server("mm://server:1234", "userA", "pwdA", false, mock(EventManager.class));
        Server server2 = new Server("mm://server:1234", "userA", null, false, mock(EventManager.class));
        assertThat(server1.equals(server2), is(false));
    }

    @Test
    public void shouldReturnFalseForEqualsWithDifferentPersistPasswordServer() {
        Server server1 = new Server("mm://server:1234", "userA", "pwdA", false, mock(EventManager.class));
        Server server2 = new Server("mm://server:1234", "userA", "pwdA", true, mock(EventManager.class));
        assertThat(server1.equals(server2), is(false));
    }
}
