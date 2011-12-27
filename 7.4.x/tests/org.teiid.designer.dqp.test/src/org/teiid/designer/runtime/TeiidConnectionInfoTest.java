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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.IStatus;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class TeiidConnectionInfoTest {

    private static final boolean INITIAL_PERSIST_PSWD = true;
    private static final String INITIAL_PORT = "31000";
    private static final String INITIAL_PSWD = "pswd";
    private static final boolean INITIAL_SECURE = true;
    private static final String INITIAL_USER = "user";

    private static final HostProvider NEW_HOST_PROVIDER = new HostProvider() {
        @Override
        public String getHost() {
            return NEW_HOST;
        }
    };
    private static final String NEW_HOST = "newHost";
    private static final String NEW_PORT = "31443";
    private static final String NEW_PSWD = "newPswd";
    private static final String NEW_USER = "newUser";

    class ConnectionInfo extends TeiidConnectionInfo {
        public ConnectionInfo( String port,
                               String username,
                               String password,
                               boolean persistPassword,
                               boolean secure ) {
            super(port, username, password, persistPassword, secure);
        }
        
        /**
         * {@inheritDoc}
         *
         * @see org.teiid.designer.runtime.TeiidConnectionInfo#getType()
         */
        @Override
        public String getType() {
            return "Test";
        }

    }

    private TeiidConnectionInfo connectionInfo;

    @Before
    public void beforeEach() {
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldSetHostProvider() {
        this.connectionInfo.setHostProvider(NEW_HOST_PROVIDER);
        assertSame(this.connectionInfo.getHostProvider(), NEW_HOST_PROVIDER);
    }

    @Test
    public void shouldAllowNullPortAtConstruction() {
        new ConnectionInfo(null, INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldAllowEmptyPortAtConstruction() {
        new ConnectionInfo("", INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldNullPort() {
        this.connectionInfo.setPort(null);
        assertThat(this.connectionInfo.getPort(), nullValue());
    }

    @Test
    public void shoulSetEmptyPort() {
        this.connectionInfo.setPort("");
        assertThat(this.connectionInfo.getPort(), is(""));
    }

    @Test
    public void shouldSetPort() {
        this.connectionInfo.setPort(NEW_PORT);
        assertThat(this.connectionInfo.getPort(), is(NEW_PORT));
    }

    @Test
    public void shouldPassValidationAFterSetPort() {
        this.connectionInfo.setPort(NEW_PORT);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.isOK(), is(true));
    }
    
    @Test
    public void shouldFailValidateAfterConstructingWithNullPort() {
        this.connectionInfo = new ConnectionInfo(null, INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }
    
    @Test
    public void shouldFailValidateAfterConstructingWithEmptyPort() {
        this.connectionInfo = new ConnectionInfo("", INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }
    
    @Test
    public void shouldFailValidateAfterSettingNullPort() {
        this.connectionInfo.setPort(null);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }
    
    @Test
    public void shouldFailValidateAfterSettingEmptyPort() {
        this.connectionInfo.setPort("");
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }

    @Test
    public void shouldAllowNullUserAtConstruction() {
        new ConnectionInfo(INITIAL_PORT, null, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldAllowEmptyUserAtConstruction() {
        new ConnectionInfo(INITIAL_PORT, "", INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldSetUser() {
        this.connectionInfo.setUsername(NEW_USER);
        assertThat(this.connectionInfo.getUsername(), is(NEW_USER));
    }

    @Test
    public void shouldSetNullUser() {
        this.connectionInfo.setUsername(null);
        assertThat(this.connectionInfo.getUsername(), nullValue());
    }

    @Test
    public void shouldSetEmptyUser() {
        this.connectionInfo.setUsername("");
        assertThat(this.connectionInfo.getUsername(), is(""));
    }
    
    @Test
    public void shouldFailValidateAfterConstructingWithNullUser() {
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, null, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }
    
    @Test
    public void shouldFailValidateAfterConstructingWithEmptyUser() {
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, "", INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }
    
    @Test
    public void shouldFailValidateAfterSettingNullUser() {
        this.connectionInfo.setUsername(null);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }
    
    @Test
    public void shouldFailValidateAfterSettingEmptyUser() {
        this.connectionInfo.setUsername("");
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }

    @Test
    public void shouldAllowNullPasswordAtConstruction() {
        new ConnectionInfo(INITIAL_PORT, INITIAL_USER, null, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldAllowEmptyPasswordAtConstruction() {
        new ConnectionInfo(INITIAL_PORT, INITIAL_USER, "", INITIAL_PERSIST_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldSetPassword() {
        this.connectionInfo.setPassword(NEW_PSWD);
        assertThat(this.connectionInfo.getPassword(), is(NEW_PSWD));
    }
    
    @Test
    public void shouldValidateSetPassword() {
        this.connectionInfo.setPassword(NEW_PSWD);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.OK));
    }

    @Test
    public void shouldSetPersistPasswordAtConstructon() {
        assertThat(this.connectionInfo.isPasswordBeingPersisted(), is(INITIAL_PERSIST_PSWD));
    }

    @Test
    public void shouldSetPersistPassword() {
        this.connectionInfo.setPersistPassword(!INITIAL_PERSIST_PSWD);
        assertThat(this.connectionInfo.isPasswordBeingPersisted(), is(!INITIAL_PERSIST_PSWD));
    }

    @Test
    public void shouldSetSecureAtConstruction() {
        assertThat(this.connectionInfo.isSecure(), is(INITIAL_SECURE));
    }

    @Test
    public void shouldSetSecure() {
        this.connectionInfo.setSecure(!INITIAL_SECURE);
        assertThat(this.connectionInfo.isSecure(), is(!INITIAL_SECURE));
    }

    @Test
    public void shouldGetCorrectUrlAfterConstruction() {
        assertThat(this.connectionInfo.getUrl(), is("mms://" + HostProvider.DEFAULT_HOST + ':' + INITIAL_PORT));
    }

    @Test
    public void shouldGetCorrectUrlAfterChangingHostProvider() {
        this.connectionInfo.setHostProvider(NEW_HOST_PROVIDER);
        assertThat(this.connectionInfo.getUrl(), is("mms://" + NEW_HOST + ':' + INITIAL_PORT));
    }

    @Test
    public void shouldGetCorrectUrlWithNotSecureAfterConstruction() {
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, !INITIAL_SECURE);
        assertThat(this.connectionInfo.getUrl(), is("mm://" + HostProvider.DEFAULT_HOST + ':' + INITIAL_PORT));
    }

    @Test
    public void shouldSetAll() {
        TeiidConnectionInfo anotherConnectionInfo = new ConnectionInfo(NEW_PORT,
                                                                       NEW_USER,
                                                                       NEW_PSWD,
                                                                       !INITIAL_PERSIST_PSWD,
                                                                       !INITIAL_SECURE);
        anotherConnectionInfo.setHostProvider(NEW_HOST_PROVIDER);

        this.connectionInfo.setAll(anotherConnectionInfo);
        assertSame(this.connectionInfo.getHostProvider(), NEW_HOST_PROVIDER);
        assertThat(this.connectionInfo.getPort(), is(NEW_PORT));
        assertThat(this.connectionInfo.getUsername(), is(NEW_USER));
        assertThat(this.connectionInfo.getPassword(), is(NEW_PSWD));
        assertThat(this.connectionInfo.isPasswordBeingPersisted(), is(!INITIAL_PERSIST_PSWD));
        assertThat(this.connectionInfo.isSecure(), is(!INITIAL_SECURE));
    }

    @Test
    public void shouldBeEqualIfPropertiesAreTheSame() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(true));
    }
    
    @Test
    public void shouldNotBeEqualIfPasswordIsNullAndOtherPasswordIsEmpty() {
        ConnectionInfo info1 = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, "", INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        ConnectionInfo info2 = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, null, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        assertThat(info1.equals(info2), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfSecureIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, !INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfPersistPasswordIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, !INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfPasswordIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, NEW_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfPortIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(NEW_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfUsernameIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, NEW_USER, INITIAL_PSWD, INITIAL_PERSIST_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }

}
