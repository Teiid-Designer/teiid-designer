/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class ServerTest {

    private static final String PORT = "31000";
    private static final String USER = "user";
    private static final String PSWD = "pswd";
    private static final boolean PERSIST = true;
    private static final boolean SECURE = true;

    private TeiidAdminInfo adminInfo;
    private TeiidJdbcInfo jdbcInfo;
    private EventManager eventMgr;
    private Server server;

    @Before
    public void beforeEach() {
        this.adminInfo = mock(TeiidAdminInfo.class);
        this.jdbcInfo = mock(TeiidJdbcInfo.class);
        this.eventMgr = mock(EventManager.class);
        this.server = new Server(null, adminInfo, jdbcInfo, eventMgr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerWithNullAdminInfo() {
        new Server(null, null, this.jdbcInfo, this.eventMgr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerWithNullJdbcInfo() {
        new Server(null, this.adminInfo, null, this.eventMgr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerWithNullEventManager() {
        new Server(null, this.adminInfo, this.jdbcInfo, null);
    }

    @Test
    public void shouldReturnFalseForEqualsWithNull() {
        assertThat(this.server.equals(null), is(false));
    }

    @Test
    public void shouldReturnTrueForEqualsWithSelf() {
        assertThat(this.server.equals(this.server), is(true));
    }

    @Test
    public void shouldSetCustomLabelWithNonNullValue() {
        String CUSTOM_LABEL = "customLabel";
        this.server.setCustomLabel(CUSTOM_LABEL);
        assertThat(this.server.getCustomLabel(), is(CUSTOM_LABEL));
    }

    @Test
    public void shouldSetCustomLabelWithNullValue() {
        this.server.setCustomLabel("oldLabel");
        this.server.setCustomLabel(null);
        assertThat(this.server.getCustomLabel(), nullValue());
    }

    @Test
    public void shouldVerifyCustomLabelIsNullAfterConstruction() {
        assertThat(this.server.getCustomLabel(), nullValue());
    }

    @Test
    public void shouldVerifyDefaultHostAfterConstruction() {
        assertThat(this.server.getHost(), is(HostProvider.DEFAULT_HOST));
    }

    @Test
    public void shouldSetHost() {
        String newHost = "newHost";
        this.server.setHost(newHost);
        assertThat(this.server.getHost(), is(newHost));
    }

    @Test
    public void shouldVerifySettingNullHostSetsToDefaultHost() {
        this.server.setHost(null);
        assertThat(this.server.getHost(), is(HostProvider.DEFAULT_HOST));
    }

    @Test
    public void shouldBeEqualsWhenAllPropertiesAreTheSame() {
        Server server1 = new Server(null,
                                    new TeiidAdminInfo(PORT, USER, PSWD, PERSIST, SECURE),
                                    new TeiidJdbcInfo(PORT, USER, PSWD, PERSIST, SECURE),
                                    this.eventMgr);
        Server server2 = new Server(null,
                                    new TeiidAdminInfo(PORT, USER, PSWD, PERSIST, SECURE),
                                    new TeiidJdbcInfo(PORT, USER, PSWD, PERSIST, SECURE),
                                    this.eventMgr);
        assertThat(server1.equals(server2), is(true));
    }

}
