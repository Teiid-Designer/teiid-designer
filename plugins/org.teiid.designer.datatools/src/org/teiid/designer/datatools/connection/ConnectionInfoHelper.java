package org.teiid.designer.datatools.connection;

import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.teiid.designer.datatools.DatatoolsPlugin;
import org.teiid.designer.datatools.JdbcTranslatorHelper;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelper;

public class ConnectionInfoHelper {
	/*
	 * === Static Property Keys for IConnectionProfile property storage
	 * values based on plugin.xml extension contributions within the datatools connectivity plugins
	 */
	public static final String PROFILE_NAME_KEY = "connectionProfileName"; //$NON-NLS-1$
	public static final String PROFILE_PROVIDER_ID_KEY= "connectionProfileProviderId"; //$NON-NLS-1$
	public static final String PROFILE_DESCRIPTION_KEY = "connectionProfileDescription"; //$NON-NLS-1$
	public static final String SAVE_PWD_KEY = "org.eclipse.datatools.connectivity.db.savePWD"; //$NON-NLS-1$
	public static final String DRIVER_DEFN_TYPE_KEY = "org.eclipse.datatools.connectivity.drivers.defnType"; //$NON-NLS-1$
	public static final String USERNAME_KEY = "org.eclipse.datatools.connectivity.db.username"; //$NON-NLS-1$
	public static final String DRIVER_CLASS_KEY = "org.eclipse.datatools.connectivity.db.driverClass"; //$NON-NLS-1$
	public static final String DRIVER_DEFN_ID_KEY = "org.eclipse.datatools.connectivity.db.driverDefinitionID"; //$NON-NLS-1$
	public static final String DATABASE_NAME_KEY = "org.eclipse.datatools.connectivity.db.databaseName"; //$NON-NLS-1$
	public static final String PASSWORD_KEY = "org.eclipse.datatools.connectivity.db.password"; //$NON-NLS-1$
	public static final String URL_KEY = "org.eclipse.datatools.connectivity.db.URL"; //$NON-NLS-1$
	public static final String VERSION_KEY = "org.eclipse.datatools.connectivity.db.version"; //$NON-NLS-1$
	public static final String VENDOR_KEY = "org.eclipse.datatools.connectivity.db.vendor"; //$NON-NLS-1$
	
	public static final String CONNECTION_PROFILE_NAMESPACE = "connection:"; //$NON-NLS-1$
	
	
	public static final String TRANSLATOR_NAMESPACE = "translator:"; //$NON-NLS-1$
	
	public static final String TRANSLATOR_NAME_KEY = "name"; //$NON-NLS-1$
	public static final String TRANSLATOR_TYPE_KEY= "type"; //$NON-NLS-1$
	
    /**
     * These are the property keys used for the jdbc source settings of physical models that were created in the legacy
     * MMX JDBC Import Wizard
     */
    public static final String JDBC_IMPORT_DRIVER_CLASS = "com.metamatrix.modeler.jdbc.JdbcSource.driverClass"; //$NON-NLS-1$
    public static final String JDBC_IMPORT_URL = "com.metamatrix.modeler.jdbc.JdbcSource.url"; //$NON-NLS-1$  
    public static final String JDBC_IMPORT_USERNAME = "com.metamatrix.modeler.jdbc.JdbcSource.username"; //$NON-NLS-1$
    public static final String JDBC_IMPORT_DRIVER_NAME = "com.metamatrix.modeler.jdbc.JdbcSource.driverName"; //$NON-NLS-1$
	
	private ResourceAnnotationHelper resourceAnnotationHelper;
	
	private ConnectionProfileFactory connectionProfileFactory;

	public ConnectionInfoHelper() {
		super();
	}
	
	public ConnectionInfoHelper(ResourceAnnotationHelper resourceAnnotationHelper, ConnectionProfileFactory connectionProfileFactory) {
		super();
		CoreArgCheck.isNotNull(resourceAnnotationHelper, "resourceAnnotationHelper"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(connectionProfileFactory, "connectionProfileFactory"); //$NON-NLS-1$
		this.resourceAnnotationHelper = resourceAnnotationHelper;
		this.connectionProfileFactory = connectionProfileFactory;
	}
	
	protected ResourceAnnotationHelper getHelper() {
		if( resourceAnnotationHelper == null ) {
			resourceAnnotationHelper = new ResourceAnnotationHelper();
		}
		
		return resourceAnnotationHelper;
	}
	
	/**
	 * Provides means to find a connection profile for a supplied <code>ModelResource</code>
	 * 
	 * @param modelResource
	 * @return
	 */
	public IConnectionProfile getConnectionProfile(ModelResource modelResource) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		
		Properties props = null;
		
		try {
			props = getHelper().getProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
		} catch (Exception e) {
			DatatoolsPlugin.Util.log(IStatus.ERROR, e, 
					DatatoolsPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
													modelResource.getItemName()));
		}
		if( props == null || props.isEmpty()) {
			return null;
		}
		
		// Now we need to 
		// cache non-base-property values
		String name = props.getProperty(CONNECTION_PROFILE_NAMESPACE + PROFILE_NAME_KEY);
		String desc = props.getProperty(CONNECTION_PROFILE_NAMESPACE + PROFILE_DESCRIPTION_KEY);
		String id = props.getProperty(CONNECTION_PROFILE_NAMESPACE + PROFILE_PROVIDER_ID_KEY);
		
		// Remove these from stored props
		props.remove(CONNECTION_PROFILE_NAMESPACE + PROFILE_NAME_KEY);
		props.remove(CONNECTION_PROFILE_NAMESPACE + PROFILE_DESCRIPTION_KEY);
		props.remove(CONNECTION_PROFILE_NAMESPACE + PROFILE_PROVIDER_ID_KEY);
		
		// Reconstruct the set of base-properties for the ConnectionProfile
		// Need to swap out the property keys
		Properties baseProps = new Properties();
		Set<Object> keys = props.keySet();
		for(Object  nextKey : keys ) {
			baseProps.put(removeNamespace((String)nextKey), props.getProperty((String)nextKey));
		}
		
		return createConnectionProfile(name, desc, id, baseProps);
	}
	
	/**
	 * Helper method to create an instance of a <code>ConnectionProfileFactory</code>.
	 * 
	 * @param name the name of the connection profile
	 * @param description the description
	 * @param id the provider ID
	 * @param props the base properties
	 * @return the connection profile
	 */
	public ConnectionProfile createConnectionProfile(String name, String description, String id, Properties props) {
		if( this.connectionProfileFactory == null ) {
			this.connectionProfileFactory = new ConnectionProfileFactory();
		}
		
		return this.connectionProfileFactory.createConnectionProfile(name, description, id, props);
	}
	
	private String removeNamespace(String str) {
		
		int semiColonIndex = str.indexOf(':') + 1;
		if( semiColonIndex > 0 ) {
			return str.substring(semiColonIndex, str.length());
		}
		
		return str;
	}

	/**
	 * Verifies if a <code>ModelResource</code> contains a model that contains connection information
	 * 
	 * @param modelResource the <code>ModelResource</code>
	 * @return true if model resource contains connection info. false if not.
	 */
	public boolean hasConnectionInfo(ModelResource modelResource) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		
		Properties props = null;
		
		try {
			props = getHelper().getProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
		} catch (Exception e) {
			DatatoolsPlugin.Util.log(IStatus.ERROR, e, 
					DatatoolsPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
													modelResource.getItemName()));
		}
		if( props == null || props.isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Stores the critical connection profile information within a model resource.
	 * 
	 * @param modelResource the <code>ModelResource</code>
	 * @param connectionProfile the connection profile
	 */
	public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile) {	
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(connectionProfile, "connectionProfile"); //$NON-NLS-1$
		
		try {
			// get name-spaced properties
			Properties props = getProperties(connectionProfile);
			// Remove old connection properties
			getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
			// Add new connection properties
			getHelper().setProperties(modelResource, props);

		} catch (ModelWorkspaceException e) {
			DatatoolsPlugin.Util.log(IStatus.ERROR, e, 
					DatatoolsPlugin.Util.getString("errorSettingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
													modelResource.getItemName()));
		}
	}
	
	/**
	 * Stores the critical connection profile information within a model resource.
	 * 
	 * @param modelResource the <code>ModelResource</code>
	 * @param connectionProfile the connection profile
	 */
	public void setJdbcConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile) {	
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(connectionProfile, "connectionProfile"); //$NON-NLS-1$
		
		try {
			// get name-spaced properties
			Properties props = getProperties(connectionProfile);
			// Remove old connection properties
			getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
			
			// Add JDBC translator
			props.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, JdbcTranslatorHelper.getTranslator(connectionProfile));
			// Add new connection properties
			getHelper().setProperties(modelResource, props);
			
		} catch (ModelWorkspaceException e) {
			DatatoolsPlugin.Util.log(IStatus.ERROR, e, 
					DatatoolsPlugin.Util.getString("errorSettingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
													modelResource.getItemName()));
		}
	}
	
	
	
	/**
	 * Returns the collective properties of a <code>ConnectionProfile</code> to include name, description and provider id
	 * in addition to it's base properties. These properties are also prefixed with a custom namespace for storage in
	 * a model resource "annotation"
	 * 
	 * @param connectionProfile the connection profile
	 * @return the name-spaced properties for the connection profile
	 */
	public Properties getProperties(IConnectionProfile connectionProfile) {
		CoreArgCheck.isNotNull(connectionProfile, "connectionProfile"); //$NON-NLS-1$
		
		if( this.connectionProfileFactory == null ) {
			this.connectionProfileFactory = new ConnectionProfileFactory();
		}
		return this.connectionProfileFactory.getNamespacedProperties(connectionProfile);
	}
	

	public String getTranslatorName(ModelResource modelResource ) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		
		Properties props = null;
		
		try {
			props = getHelper().getProperties(modelResource, TRANSLATOR_NAMESPACE);
		} catch (Exception e) {
			DatatoolsPlugin.Util.log(IStatus.ERROR, e, 
					DatatoolsPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
													modelResource.getItemName()));
		}
		
		return props.getProperty(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY);
	}

}