package org.teiid.designer.runtime.connection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Properties;

import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.designer.core.ModelResourceMockFactory;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelper;

@RunWith( PowerMockRunner.class )
@PrepareForTest( {ModelerCore.class, ModelResourceContainerFactory.class, ModelUtil.class} )
public class ConnectionInfoHelperTest {
    
    private ConnectionInfoHelper helper;
    
    private ModelResource modelResource;
    
    @Mock
    private ResourceAnnotationHelper resourceHelper;
    @Mock
    private ConnectionProfile connectionProfile;
    @Mock
    private ConnectionProfileFactory connectionProfileFactory;
    
	private static String CP_NAME_VALUE = "connectionProfileName"; //$NON-NLS-1$
	private static String CP_NAME_KEY = ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + ConnectionInfoHelper.PROFILE_NAME_KEY;
	private static String CP_DESC_VALUE = "connectionProfileDesc"; //$NON-NLS-1$
	private static String CP_DESC_KEY = ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + ConnectionInfoHelper.PROFILE_DESCRIPTION_KEY;
	private static String CP_PROVIDER_ID_VALUE = "connectionProfileProviderId"; //$NON-NLS-1$  
	private static String CP_PROVIDER_ID_KEY = ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + ConnectionInfoHelper.PROFILE_PROVIDER_ID_KEY;
    private static final String KEY_1 = ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + ConnectionInfoHelper.DATABASE_NAME_KEY;
    private static final String VALUE_1 = "partssupplier"; //$NON-NLS-1$
    private static final String KEY_2 = ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + ConnectionInfoHelper.DRIVER_CLASS_KEY;
    private static final String VALUE_2 = "org.bogus.company.mydb.MyDbDriver"; //$NON-NLS-1$
    
    private static final String EMPTY_STRING = StringUtilities.EMPTY_STRING;
    
    private Properties connectionProps;
    
	@Before
    public void beforeEach() throws ModelWorkspaceException {
        MockitoAnnotations.initMocks(this);
        

        connectionProps = new Properties();
        connectionProps.put(KEY_1, VALUE_1); 
        connectionProps.put(KEY_2, VALUE_2);
        connectionProps.put(CP_NAME_KEY, CP_NAME_VALUE); 
        connectionProps.put(CP_DESC_KEY, CP_DESC_VALUE);
        connectionProps.put(CP_PROVIDER_ID_KEY, CP_PROVIDER_ID_VALUE); 
        
        // Set up ModelResource
        modelResource = ModelResourceMockFactory.createModelResource("SourceA", "ProjectA");
		// Actually set up the ResourceAnnotation with the helper class
        resourceHelper.getResourceAnnotation(modelResource, true);
		// Construct a ConnectionInfoHelper with a mock resource helper & mock CP factory
        helper = new ConnectionInfoHelper(resourceHelper, connectionProfileFactory);
	}
	
	@Test
	public void shouldCreateConnectionProfile() {
		when(connectionProfileFactory.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps)).thenReturn(connectionProfile);
		
		IConnectionProfile profile =  helper.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps);
		
		assertNotNull(profile);
	}
	
	@Test
	public void shouldNotCreateConnectionProfileWithNullName() {
		when(connectionProfileFactory.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps)).thenReturn(connectionProfile);
		
		IConnectionProfile profile =  helper.createConnectionProfile(null, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps);
		
		assertNull(profile);
	}
	
	@Test
	public void shouldNotCreateConnectionProfileWithEmptyName() {
		when(connectionProfileFactory.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps)).thenReturn(connectionProfile);
		
		IConnectionProfile profile =  helper.createConnectionProfile(EMPTY_STRING, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps);
		
		assertNull(profile);
	}
	
	@Test
	public void shouldNotCreateConnectionProfileWithNullId() {
		when(connectionProfileFactory.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps)).thenReturn(connectionProfile);
		
		IConnectionProfile profile =  helper.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, null, connectionProps);
		
		assertNull(profile);
	}
	
	@Test
	public void shouldNotCreateConnectionProfileWithEmptyId() {
		when(connectionProfileFactory.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps)).thenReturn(connectionProfile);
		
		IConnectionProfile profile =  helper.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, EMPTY_STRING, connectionProps);
		
		assertNull(profile);
	}
	
	@Test
	public void shouldGetConnectionProfile() throws ModelWorkspaceException {
		when(resourceHelper.getProperties(modelResource, ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE)).thenReturn(connectionProps);
		when(connectionProfileFactory.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps)).thenReturn(connectionProfile);
		
		IConnectionProfile profile =  helper.getConnectionProfile(modelResource);
		
		assertNotNull(profile);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGetConnectionProfileWithNullModelResource() {
		ModelResource nullMR = null;
		helper.getConnectionProfile(nullMR);
	}
	
	@Test
	public void modelResourceShouldHaveConnectionInfo() throws ModelWorkspaceException {
		when(resourceHelper.getProperties(modelResource, ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE)).thenReturn(connectionProps);
		
		assertTrue(helper.hasConnectionInfo(modelResource));
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotHaveConnectionInfoWithNullModelResource() {
		ModelResource nullMR = null;
		helper.hasConnectionInfo(nullMR);
	}
	
	@Test
	public void modelResourceShouldNotHaveConnectionInfo() throws ModelWorkspaceException {
		when(resourceHelper.getProperties(modelResource, ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE)).thenReturn(new Properties());
		
		assertFalse(helper.hasConnectionInfo(modelResource));
	}
	
	@Test
	public void shouldSetConnectionInfo() throws ModelWorkspaceException {
		when(resourceHelper.getProperties(modelResource, ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE)).thenReturn(new Properties());
		when(helper.getProperties(connectionProfile)).thenReturn(connectionProps);
		
		
		helper.setConnectionInfo(modelResource, connectionProfile);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotSetConnectionInfoNullModelResource() {
		helper.setConnectionInfo(null, null);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotSetConnectionInfoNullConnectionProfile() {
		helper.setConnectionInfo(modelResource, null);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGetPropertiesWithNullConnectionProfile() {
		helper.getProperties(null);
	}
	
	
	@Test
	public void shouldGetDataSourceProperties() throws ModelWorkspaceException {
		when(connectionProfileFactory.createConnectionProfile(CP_NAME_VALUE, CP_DESC_VALUE, CP_PROVIDER_ID_VALUE, connectionProps)).thenReturn(connectionProfile);
		when(resourceHelper.getProperties(modelResource, ConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE)).thenReturn(connectionProps);
		when(helper.getProperties(connectionProfile)).thenReturn(connectionProps);
		when(connectionProfile.getBaseProperties()).thenReturn(new Properties());
		mockStatic(ModelUtil.class);
		Resource emfResource = mock(Resource.class);
		when(modelResource.getEmfResource()).thenReturn(emfResource);
		when(ModelUtil.isPhysical(emfResource)).thenReturn(true);
		
		helper.getDataSourceProperties(modelResource);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGetDataSourcePropertiesWithNullModelResource() throws ModelWorkspaceException {
		ModelResource nullMR = null;
		helper.getDataSourceProperties(nullMR);
	}
	
	@Test
	public void shouldGenerateUniqueConnectionJndiNameWithNamePathUUID() {
		String jndiName = helper.generateUniqueConnectionJndiName("modelname", new Path("L/MyProject/"), "uuid_AAAA");
		
		assertNotNull(jndiName);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGenerateUniqueConnectionJndiNameWithNullName() {
		helper.generateUniqueConnectionJndiName(null, null, null);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGenerateUniqueConnectionJndiNameWithEmptyName() {
		helper.generateUniqueConnectionJndiName(EMPTY_STRING, null, null);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGenerateUniqueConnectionJndiNameWithNullPath() {
		helper.generateUniqueConnectionJndiName("someName", null, null);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGenerateUniqueConnectionJndiNameWithNullUuid() {
		helper.generateUniqueConnectionJndiName("someName", new Path("L/MyProject/"), null);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGenerateUniqueConnectionJndiNameWithEmptyUuid() {
		helper.generateUniqueConnectionJndiName("someName", new Path("L/MyProject/"), EMPTY_STRING);
	}
	
	@Test
	public void shouldGenerateUniqueConnectionJndiNameWithModelResourceUUID() {
		when(modelResource.getItemName()).thenReturn("modelname)");
		when(modelResource.getPath()).thenReturn(new Path("L/MyProject/"));
		String jndiName = helper.generateUniqueConnectionJndiName(modelResource, "uuid_AAAA");
		
		assertNotNull(jndiName);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGenerateUniqueConnectionJndiNullModelResource() {
		ModelResource nullMR = null;
		helper.generateUniqueConnectionJndiName(nullMR, EMPTY_STRING);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGenerateUniqueConnectionJndiModelResourceNullUuid() {
		helper.generateUniqueConnectionJndiName(modelResource, null);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGenerateUniqueConnectionJndiModelResourceEmptyUuid() {
		helper.generateUniqueConnectionJndiName(modelResource, EMPTY_STRING);
	}
	
	@Test ( expected = IllegalArgumentException.class )
	public void shouldNotGetModelJdbcPropertiesNullModelResource() throws ModelWorkspaceException {
		ModelResource nullMR = null;
		helper.getModelJdbcConnectionProperties(nullMR);
	}
}
