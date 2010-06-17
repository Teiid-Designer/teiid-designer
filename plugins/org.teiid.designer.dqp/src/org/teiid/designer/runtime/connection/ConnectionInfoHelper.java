package org.teiid.designer.runtime.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DataSourceConnectionConstants;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelper;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * This utility class provides a mechanism to retrieve and store Connection information in a model resource.
 * 
 * The concept utilizes an <code>Annotation</code> referenced on the <code>ModelAnnotation</code> object and contains
 * properties (aka tags).
 * 
 * These tags can be used for properties other than Connection info, so the properties are key'd based on a namespace
 * prefix.
 * 
 *
 */
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
	
	
	public static final String TRANSLATOR_NAME_KEY = "translator.name"; //$NON-NLS-1$
	public static final String TRANSLATOR_TYPE_KEY= "translator.type"; //$NON-NLS-1$
	
	public static final String TRANSLATOR_NAMESPACE = "translator:"; //$NON-NLS-1$
	
	public static final String TRANSLATOR_NAME = "translator"; //$NON-NLS-1$
	
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
	
	private TranslatorProfileFactory translatorProfileFactory;

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
			DqpPlugin.Util.log(IStatus.ERROR, e, 
					DqpPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
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
	protected ConnectionProfile createConnectionProfile(String name, String description, String id, Properties props) {
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
			DqpPlugin.Util.log(IStatus.ERROR, e, 
					DqpPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
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
			// Add Translator Properties
			String tName = findTranslatorName(modelResource);
			if( tName != null ) {
				getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
				getHelper().setProperty(modelResource, TRANSLATOR_NAMESPACE + TRANSLATOR_NAME, tName);
			}
		} catch (ModelWorkspaceException e) {
			DqpPlugin.Util.log(IStatus.ERROR, e, 
					DqpPlugin.Util.getString("errorSettingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
													modelResource.getItemName()));
		}
	}
	
	
	/**
	 * Stores the critical connection profile information within a model resource.
	 * 
	 * @param modelResource the <code>ModelResource</code>
	 * @param connectionProfile the connection profile
	 */
	public void setTranslatorInfo(ModelResource modelResource, TranslatorProfile translatorProfile) {	
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(translatorProfile, "translatorProfile"); //$NON-NLS-1$
		
		try {
			// get name-spaced properties
			Properties props = getProperties(translatorProfile);
			// Remove old connection properties
			getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
			// Add new connection properties
			getHelper().setProperties(modelResource, props);
			// Add Translator Properties
			//getHelper().setProperty(modelResource, TRANSLATOR_NAMESPACE + TRANSLATOR_NAME, DataSourceConnectionConstants.Translators.);
		} catch (ModelWorkspaceException e) {
			DqpPlugin.Util.log(IStatus.ERROR, e, 
					DqpPlugin.Util.getString("errorSettingTranslatorProfilePropertiesForModelResource", //$NON-NLS-1$
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
	protected Properties getProperties(IConnectionProfile connectionProfile) {
		CoreArgCheck.isNotNull(connectionProfile, "connectionProfile"); //$NON-NLS-1$
		
		if( this.connectionProfileFactory == null ) {
			this.connectionProfileFactory = new ConnectionProfileFactory();
		}
		return this.connectionProfileFactory.getNamespacedProperties(connectionProfile);
	}
	

	protected Properties getProperties(TranslatorProfile translatorProfile) {
		CoreArgCheck.isNotNull(translatorProfile, "translatorProfile"); //$NON-NLS-1$
		
		if( this.translatorProfileFactory == null ) {
			this.translatorProfileFactory = new TranslatorProfileFactory();
		}
		return this.translatorProfileFactory.getNamespacedProperties(translatorProfile);
	}
	
	/**
	 * Method returns a <code>Properties</code> object containing the Teiid-specific properties required
	 * to create a data source instance via Admin API createDataSource().  This will be accomplished via the 
	 * <code>ExecutionAdmin</code>'s getOrCreateDataSource(String name, String typeName, Properties properties) method.
	 * 
	 * @param modelResource
	 * @return
	 * @throws ModelWorkspaceException
	 */
    public Properties getDataSourceProperties( ModelResource modelResource ) throws ModelWorkspaceException {
    	CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
    	
    	Properties result = new Properties();

        if ( (modelResource != null) && ModelUtil.isPhysical(modelResource.getEmfResource())) {

        	IConnectionProfile profile = getConnectionProfile(modelResource);
        	Properties props = profile.getBaseProperties();
        	
			if ( props.get(DRIVER_CLASS_KEY) != null ) {
				result.put(DataSourceConnectionConstants.DRIVER_CLASS, props.get(DRIVER_CLASS_KEY));
			}
			
			if ( props.get(URL_KEY) != null ) {
				result.put(DataSourceConnectionConstants.URL, props.get(URL_KEY));
			}
			
			if (props.get(USERNAME_KEY) != null) {
				result.put(DataSourceConnectionConstants.USERNAME, props.get(USERNAME_KEY));
			}
			
			if (props.get(PASSWORD_KEY) != null) {
				result.put(DataSourceConnectionConstants.PASSWORD, props.get(PASSWORD_KEY));
			}
        	
        }

        return result;
    }
    
    /**
     * Creates a unique JNDI name for a source model based on a <code>ModelResource</code>
     * 
     * @param modelResource
     * @return the JNDI name
     */
    public String generateUniqueConnectionJndiName(ModelResource modelResource, String uuid) {
    	CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
    	CoreArgCheck.isNotEmpty(uuid, "uuid"); //$NON-NLS-1$
    	return generateUniqueConnectionJndiName(modelResource.getItemName(), modelResource.getPath(), uuid);
    }
    
    /**
     * Creates a unique JNDI name for a source model based on a name and path in the workspace
     * 
     * @param name
     * @param path
     * @return
     */
    public String generateUniqueConnectionJndiName(String name, IPath path, String uuid) {
//    	return name;
    	
    	CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
    	CoreArgCheck.isNotNull(path, "path"); //$NON-NLS-1$
    	CoreArgCheck.isNotEmpty(uuid, "uuid"); //$NON-NLS-1$
    	
        final StringBuilder builder = new StringBuilder(uuid + "__"); //$NON-NLS-1$
        for (final String segment : path.removeLastSegments(1).segments())
            builder.append(segment).append('_');

        return builder.append(name).toString();
    }
	
    /**
     * Given a set of matchable strings and a set of data source type strings, find a suitable match.
     * 
     * If a match isn't found, then <unknown> string is returned.
     * 
     * @param matchableStrings
     * @param dataSourceTypeNames
     * @return
     */
    public String findMatchingDataSourceTypeName(Collection<String> matchableStrings, Set<String> dataSourceTypeNames) {
    	for( String dsTypeName : dataSourceTypeNames ) {
    		for( String keyword : matchableStrings) {
	    		if( keyword.toUpperCase().contains(dsTypeName.toUpperCase())) {
	    			return dsTypeName;
	    		}
    		}
    	}
    	
    	return DataSourceConnectionConstants.UNKNOWN;
    }
    
    public String findMatchingDataSourceTypeName(ModelResource modelResource) throws ModelWorkspaceException {
    	Properties properties = getDataSourceProperties(modelResource);
    	
    	return findMatchingDataSourceTypeName(properties);
    }
    
    public String findMatchingDataSourceTypeName(Properties properties) {
    	Collection<String> matchableStrings = new ArrayList<String>();
    	matchableStrings.add(properties.getProperty(DataSourceConnectionConstants.DRIVER_CLASS));
    	matchableStrings.add(properties.getProperty(DataSourceConnectionConstants.URL));
    	
    	if( isJdbcDataSource(matchableStrings)) {
    		return DataSourceConnectionConstants.DataSource.JDBC;
    	}
    	
    	return DataSourceConnectionConstants.DataSource.UNKNOWN;
    }
    
    public boolean isJdbcDataSource(Collection<String> matchableStrings) {
    	for(String translatorName : DataSourceConnectionConstants.Translators.JDBC_TRANSLATORS) {
    		for( String keyword : matchableStrings) {
	    		if( keyword.toUpperCase().contains(translatorName.toUpperCase())) {
	    			return true;
	    		}
    		}
    	}
    	return false;
    }
    
    public String findTranslatorName(ModelResource modelResource) throws ModelWorkspaceException {
    	Properties properties = getDataSourceProperties(modelResource);
    	
    	Collection<String> matchableStrings = new ArrayList<String>();
    	matchableStrings.add(properties.getProperty(DataSourceConnectionConstants.DRIVER_CLASS));
    	matchableStrings.add(properties.getProperty(DataSourceConnectionConstants.URL));
    	
    	for(String translatorName : DataSourceConnectionConstants.Translators.JDBC_TRANSLATORS) {
    		for( String keyword : matchableStrings) {
	    		if( keyword.toUpperCase().contains(translatorName.toUpperCase())) {
	    			return translatorName;
	    		}
    		}
    	}
    	
    	return null;
    }
    
    /**
     * Given a set of properties, find a suitable translator name.
     * 
     * @param matchableStrings
     * @param translators
     * @return
     */
    public String findTranslatorName(Properties properties) {
    	Collection<String> matchableStrings = new ArrayList<String>();
    	matchableStrings.add(properties.getProperty(ConnectionInfoHelper.DRIVER_CLASS_KEY));
    	matchableStrings.add(properties.getProperty(ConnectionInfoHelper.URL_KEY));
    	
    	for(String translatorName : DataSourceConnectionConstants.Translators.ALL_TRANSLATORS) {
    		for( String keyword : matchableStrings) {
	    		if( keyword.toUpperCase().contains(translatorName.toUpperCase())) {
	    			return translatorName;
	    		}
    		}
    	}
    	
    	return null;
    }
    
    /**
     * Get a set of property name to values for JDBC connection properties in a model's JdbcSource object.
     * 
     * These properties are "legacy" properties and should be treated as deprecated and may or may not
     * exist.
     * 
     * @param modelResource the model resource containing the JDBC properties being requested
     * @return properties, the JDBC connection properties (never <code>null</code> but maybe empty)
     * @throws ModelWorkspaceException 
     * @since 5.0
     */
    public Properties getModelJdbcConnectionProperties( ModelResource modelResource ) throws ModelWorkspaceException {
    	CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
    	Properties result = new Properties();

        if ( ModelUtil.isPhysical(modelResource.getEmfResource()) && (modelResource != null)) {

			// TODO: Find Model's JDBC PRoperties here!!!!
			JdbcSource jdbcSource = findJdbcSource(modelResource.getCorrespondingResource());
			if (jdbcSource != null) {

				if ( jdbcSource.getDriverClass() != null ) {
					result.put(DataSourceConnectionConstants.DRIVER_CLASS, jdbcSource.getDriverClass());
				}
				
				if ( jdbcSource.getUrl() != null ) {
					result.put(DataSourceConnectionConstants.URL, jdbcSource.getUrl());
				}
				
				if (jdbcSource.getUsername() != null) {
					result.put(DataSourceConnectionConstants.USERNAME, jdbcSource.getUsername());
				}
				
				if (jdbcSource.getPassword() != null) {
					result.put(DataSourceConnectionConstants.PASSWORD, jdbcSource.getPassword());
				}
			} else {
				
			}
        }

        return result;
    }
    
    public Properties getModelJdbcProperties( ModelResource modelResource ) throws ModelWorkspaceException {
    	CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
    	Properties result = new Properties();

        if ( ModelUtil.isPhysical(modelResource.getEmfResource()) && (modelResource != null)) {

			JdbcSource jdbcSource = findJdbcSource(modelResource.getCorrespondingResource());
			if (jdbcSource != null) {
		         if (jdbcSource.getDriverClass() != null) {
		        	 result.put(JDBC_IMPORT_DRIVER_CLASS, jdbcSource.getDriverClass());
		         } 
		         
		         if (jdbcSource.getUrl() != null) {
		        	 result.put(JDBC_IMPORT_URL, jdbcSource.getUrl());
		         }
		         
		         if (jdbcSource.getUsername() != null) {
		        	 result.put(JDBC_IMPORT_USERNAME, jdbcSource.getUsername());
		         }
		         
		         if (jdbcSource.getDriverName() != null) {
		        	 result.put(JDBC_IMPORT_DRIVER_NAME, jdbcSource.getDriverName());
		         }
			}
        }

        return result;
    }
    
    /**
     * @param resource
     * @return the JdbcSource object
     * @throws ModelWorkspaceException
     */
    public JdbcSource findJdbcSource(final IResource resource) throws ModelWorkspaceException {
    	
    	ModelResource mr = ModelUtil.getModelResource((IFile)resource, true);
    	if( mr != null ) {
    		Collection allEObjects = mr.getEObjects();
    		for( Iterator iter = allEObjects.iterator(); iter.hasNext();) {
    			EObject nextEObject = (EObject)iter.next();
    			if( nextEObject instanceof JdbcSource ) {
    				return (JdbcSource)nextEObject;
    			}
    		}
    	} else {
    		throw new ModelWorkspaceException(DqpPlugin.Util.getString("errorFindingModelResourceForModelFile", resource.getName())); //$NON-NLS-1$
    	}
    	
    	return null;
    }
}
