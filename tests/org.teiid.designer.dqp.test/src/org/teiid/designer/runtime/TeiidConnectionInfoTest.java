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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IStatus;
import org.junit.Before;
import org.junit.Test;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.designer.runtime.spi.ITeiidConnectionInfo;

/**
 * 
 */
@SuppressWarnings( "javadoc" )
public class TeiidConnectionInfoTest {

    private static final String INITIAL_PORT = "31000";
    private static final String INITIAL_PSWD = "pswd";
    private static final boolean INITIAL_SECURE = true;
    private static final String INITIAL_USER = "user";

    private static final String NEW_HOST = "newHost";
    private static final String NEW_PORT = "31443";
    private static final String NEW_PSWD = "newPswd";
    private static final String NEW_USER = "newUser";
    
    private static final String EXTRA_URL_PARAMETER = "MyExtraUrlPrefix";

    static final String PASSWORD_KEY = ConnectionInfo.class.getName() + ".password";

    DefaultStorageProvider defaultStorageProvider;

    class ConnectionInfo extends TeiidAdminInfo {

        public ConnectionInfo( String host,
        					   String port,
                               String username,
                               String password,
                               boolean secure ) {
            super(host, port, username, defaultStorageProvider, password, secure);
        }
        
        public ConnectionInfo( String port,
			                String username,
			                String password,
			                boolean secure ) {
        	super(ITeiidConnectionInfo.DEFAULT_HOST, port, username, defaultStorageProvider, password, secure);
		}
        
        @Override
        protected String getPasswordKey() {
            return PASSWORD_KEY;
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
        
        @Override
        public String getUrl() {
            return super.getUrl();
        }

    }

    private TeiidConnectionInfo connectionInfo;

    @Before
    public void beforeEach() {
        defaultStorageProvider = new DefaultStorageProvider();
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldAllowNullPortAtConstruction() {
        new ConnectionInfo(null, INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldAllowEmptyPortAtConstruction() {
        new ConnectionInfo("", INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
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
        this.connectionInfo = new ConnectionInfo(null, INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }
    
    @Test
    public void shouldFailValidateAfterConstructingWithEmptyPort() {
        this.connectionInfo = new ConnectionInfo("", INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
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
        new ConnectionInfo(INITIAL_PORT, null, INITIAL_PSWD, INITIAL_SECURE);
    }

    @Test
    public void shouldAllowEmptyUserAtConstruction() {
        new ConnectionInfo(INITIAL_PORT, "", INITIAL_PSWD, INITIAL_SECURE);
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
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, null, INITIAL_PSWD, INITIAL_SECURE);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.ERROR));
    }
    
    @Test
    public void shouldFailValidateAfterConstructingWithEmptyUser() {
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, "", INITIAL_PSWD, INITIAL_SECURE);
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
        new ConnectionInfo(INITIAL_PORT, INITIAL_USER, null, INITIAL_SECURE);
    }

    @Test
    public void shouldAllowEmptyPasswordAtConstruction() {
        new ConnectionInfo(INITIAL_PORT, INITIAL_USER, "", INITIAL_SECURE);
    }

    @Test
    public void shouldSetPassword() {
        this.connectionInfo.setPassword(NEW_PSWD);
        assertThat(this.connectionInfo.getPassword(), is(NEW_PSWD));
    }

    @Test
    public void shouldSetPasswordToNull() {
        this.connectionInfo.setPassword(null);
        assertNull(this.connectionInfo.getPassword());
    }

    @Test
    public void shouldValidateSetPassword() {
        this.connectionInfo.setPassword(NEW_PSWD);
        IStatus status = this.connectionInfo.validate();
        assertThat(status.getSeverity(), is(IStatus.OK));
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
        assertThat(this.connectionInfo.getUrl(), is("mms://" + ITeiidConnectionInfo.DEFAULT_HOST + ':' + INITIAL_PORT));
    }

    @Test
    public void shouldGetCorrectUrlWithNotSecureAfterConstruction() {
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, !INITIAL_SECURE);
        assertThat(this.connectionInfo.getUrl(), is("mm://" + ITeiidConnectionInfo.DEFAULT_HOST + ':' + INITIAL_PORT));
    }

    @Test
    public void shouldGetSamePassTokenForSamePassword() {
        this.connectionInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, !INITIAL_SECURE);
        String passToken1 = connectionInfo.getPassToken();
        connectionInfo.setPassword(INITIAL_PSWD);
        String passToken2 = connectionInfo.getPassToken();
        assertEquals(passToken1, passToken2);
    }

    @Test
    public void shouldSetAll() {
        TeiidAdminInfo anotherConnectionInfo = new ConnectionInfo(NEW_HOST, NEW_PORT,
                                                                       NEW_USER,
                                                                       NEW_PSWD,
                                                                       !INITIAL_SECURE);

        this.connectionInfo.setAll(anotherConnectionInfo);
        assertSame(this.connectionInfo.getHost(), NEW_HOST);
        assertThat(this.connectionInfo.getPort(), is(NEW_PORT));
        assertThat(this.connectionInfo.getUsername(), is(NEW_USER));
        assertThat(this.connectionInfo.getPassword(), is(NEW_PSWD));
        assertThat(this.connectionInfo.isSecure(), is(!INITIAL_SECURE));
    }

    @Test
    public void shouldBeEqualIfPropertiesAreTheSame() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(true));
    }
    
    @Test
    public void shouldNotBeEqualIfPasswordIsNullAndOtherPasswordIsEmpty() {
        ConnectionInfo info1 = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, "", INITIAL_SECURE);
        ConnectionInfo info2 = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, null, INITIAL_SECURE);
        assertThat(info1.equals(info2), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfSecureIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, !INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }

    @Test
    public void shouldNotBeEqualIfPersistPasswordIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, !INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfPasswordIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, INITIAL_USER, NEW_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfPortIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(NEW_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }
    
    @Test
    public void shouldNotBeEqualIfUsernameIsDifferent() {
        ConnectionInfo otherInfo = new ConnectionInfo(INITIAL_PORT, NEW_USER, INITIAL_PSWD, INITIAL_SECURE);
        assertThat(this.connectionInfo.equals(otherInfo), is(false));
    }

    /**
     * Sub classes demonstrating that the password reference will be broken
     * if extra url parameter are set in the constructor and initPassword is
     * not re-called. The url must be 'complete' for the passToken to be
     * correctly generated and the password correctly stored under the right
     * reference in the secure storage.
     */
    private class LostPasswdSubConnectionInfo extends ConnectionInfo {

        public LostPasswdSubConnectionInfo(String port, String username, String password, boolean secure) {
            super(port, username, password, secure);
        }

        @Override
        protected void generateUrl() {
        	super.generateUrl();
        	
            this.url = "<" + EXTRA_URL_PARAMETER + ">" + super.getUrl();
        }
    }

    private class FoundPasswdSubConnectionInfo extends ConnectionInfo {

        public FoundPasswdSubConnectionInfo(String port, String username, String password, boolean secure) {
            super(port, username, password, secure);

            /*
             * Note this init method has been called after the setting of the
             * extra url parameter because the url parameter changes the
             * value of the url which is required for generating the pass token
             * used to reference the password in storage
             */
            setPassword(password);
        }

        @Override
        protected void generateUrl() {
        	super.generateUrl();
        	
            this.url = "<" + EXTRA_URL_PARAMETER + ">" + super.getUrl();
        }
    }

    @Test
    public void shouldStillFindPasswordIfUpdatingUrlInConstructor() throws Exception {
        /*
         * Do it like this and we lose the link to the password!
         */
        LostPasswdSubConnectionInfo subInfo1 = new LostPasswdSubConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
        assertNotNull(subInfo1.getPassword());

        /*
         * Do it like this and the password link is maintained
         */
        FoundPasswdSubConnectionInfo subInfo2 = new FoundPasswdSubConnectionInfo(INITIAL_PORT, INITIAL_USER, INITIAL_PSWD, INITIAL_SECURE);
        assertEquals(INITIAL_PSWD, subInfo2.getPassword());
    }
    
    @Test
    public void shouldReplacePasswordWithHostChange() throws Exception {

    	String originalToken = connectionInfo.getPassToken();
    	String originalPWD = connectionInfo.getPassword();
        connectionInfo.setHost(NEW_HOST);
        
        assertNotNull(connectionInfo.getPassToken());
        assertFalse(connectionInfo.getPassToken().equals(originalToken));
        assertNotNull(connectionInfo.getPassword());
        assertTrue(connectionInfo.getPassword().equals(originalPWD));
    }
    
    @Test
    public void shouldReplacePasswordWithPortChange() throws Exception {

    	String originalToken = connectionInfo.getPassToken();
    	String originalPWD = connectionInfo.getPassword();
        connectionInfo.setPort(NEW_PORT);
        
        assertNotNull(connectionInfo.getPassToken());
        assertFalse(connectionInfo.getPassToken().equals(originalToken));
        assertNotNull(connectionInfo.getPassword());
        assertTrue(connectionInfo.getPassword().equals(originalPWD));
    }
}
