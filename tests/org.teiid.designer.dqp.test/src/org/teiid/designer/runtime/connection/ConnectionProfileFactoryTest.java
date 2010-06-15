package org.teiid.designer.runtime.connection;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.Properties;

import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.junit.Before;
import org.junit.Test;

import com.metamatrix.core.util.StringUtilities;

public class ConnectionProfileFactoryTest {
	private ConnectionProfileFactory connectionProfileFactory;
	private static final String EMPTY_STRING = StringUtilities.EMPTY_STRING;
	
	private static String NAME_VALUE = "connectionProfileName"; //$NON-NLS-1$
	private static String DESC_VALUE = "connectionProfileDesc"; //$NON-NLS-1$
	private static String PROVIDER_ID_VALUE = "connectionProfileProviderId"; //$NON-NLS-1$  

	
	@Before
    public void beforeEach() {
		connectionProfileFactory = new ConnectionProfileFactory();
	}
	
	@Test ( expected = IllegalArgumentException.class)
	public void shouldFailCreateConnectionProfileWithEmtpyName() {
		connectionProfileFactory.createConnectionProfile(EMPTY_STRING, null, null, null);
	}
	
	@Test ( expected = IllegalArgumentException.class)
	public void shouldFailCreateConnectionProfileWithNullName() {
		connectionProfileFactory.createConnectionProfile(null, DESC_VALUE, null, null);
	}
	
	
	@Test ( expected = IllegalArgumentException.class)
	public void shouldFailCreateConnectionProfileWithEmtpyId() {
		connectionProfileFactory.createConnectionProfile(NAME_VALUE, null, EMPTY_STRING, null);
	}
	
	@Test ( expected = IllegalArgumentException.class)
	public void shouldFailCreateConnectionProfileWithNullId() {
		connectionProfileFactory.createConnectionProfile(NAME_VALUE, null, null, null);
	}
	
	@Test ( expected = IllegalArgumentException.class)
	public void shouldFailCreateConnectionProfileWithEmtpyProperties() {
		connectionProfileFactory.createConnectionProfile(NAME_VALUE, null, EMPTY_STRING, new Properties());
	}
	
	@Test ( expected = IllegalArgumentException.class)
	public void shouldFailCreateConnectionProfileWithNullProperties() {
		connectionProfileFactory.createConnectionProfile(NAME_VALUE, null, PROVIDER_ID_VALUE, null);
	}
	
	@Test ( expected = IllegalArgumentException.class)
	public void shouldFailGetNamespacedPropertiesWithProfileWithNullConnectionProfile() {
		connectionProfileFactory.getNamespacedProperties(null);
	}
	
	@Test 
	public void shouldGetNamespacedPropertiesWithProfileWithConnectionProfile() {
		ConnectionProfile cp = mock(ConnectionProfile.class);
		when(cp.getBaseProperties()).thenReturn(new Properties());
		when(cp.getName()).thenReturn(NAME_VALUE);
		when(cp.getDescription()).thenReturn(DESC_VALUE);
		when(cp.getProviderId()).thenReturn(PROVIDER_ID_VALUE);
		connectionProfileFactory.getNamespacedProperties(cp);
	}

}
