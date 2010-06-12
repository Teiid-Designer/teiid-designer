package org.teiid.designer.runtime.connection;

import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.teiid.designer.vdb.VdbModelEntry;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DataSourceConnectionConstants;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelper;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

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
	
	public static final String CONNECTION_PROFILE_NAMESPACE = "connection.profile:"; //$NON-NLS-1$
	public static final String TRANSLATOR_NAMESPACE = "connection.translator:"; //$NON-NLS-1$
	
	public static final String TRANSLATOR_NAME = "translator-name"; //$NON-NLS-1$
	
	private ResourceAnnotationHelper resourceAnnotationHelper;

	public ConnectionInfoHelper() {
		super();
	}
	
	public ConnectionInfoHelper(ResourceAnnotationHelper resourceAnnotationHelper) {
		super();
		this.resourceAnnotationHelper = resourceAnnotationHelper;
	}
	
	/**
	 * Provides means to find a connection profile for a supplied <code>VdbModelEntry</code>
	 * 
	 * @param vdbModelEntry
	 * @return the connection profile
	 */
	public IConnectionProfile getConnectionProfile(VdbModelEntry vdbModelEntry) {
        IResource resource = WorkspaceResourceFinderUtil.findIResourceByPath(vdbModelEntry.getName());

        if ((vdbModelEntry.getType() == ModelType.PHYSICAL_LITERAL) && (resource != null)) {
        	ModelResource mr = getModelResource(resource);
        	return getConnectionProfile(mr);
        }
        
        return null;
	}
	
	protected ResourceAnnotationHelper getHelper() {
		if( resourceAnnotationHelper == null ) {
			resourceAnnotationHelper = new ResourceAnnotationHelper();
		}
		
		return resourceAnnotationHelper;
	}
	
	/**
	 * Provides means to find a connection profile for a supplied <code>ModelResource</code>
	 * @param modelResource
	 * @return
	 */
	public IConnectionProfile getConnectionProfile(ModelResource modelResource) {
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
			baseProps.put(nextKey, removeNamespace((String)props.getProperty((String)nextKey)));
		}

		return createConnectionProfile(name, desc, id, baseProps);
	}
	
	public ConnectionProfile createConnectionProfile(String name, String description, String id, Properties props) {
		ConnectionProfile profile = new ConnectionProfile(name, description, id);
		profile.setProperties(id, props);
		return profile;
	}
	
	private String removeNamespace(String str) {
		
		int semiColonIndex = str.indexOf(':');
		if( semiColonIndex > 0 ) {
			return str.substring(semiColonIndex, str.length());
		}
		
		return str;
	}
	
	/**
	 * Verifies if a <code>VdbModelentry</code> contains a model that contains connection information
	 * 
	 * @param vdbModelEntry the <code>VdbModelEntry</code>
	 * @return true if vdb model entry contains connection info. false if not.
	 */
	public boolean hasConnectionInfo(VdbModelEntry vdbModelEntry) {
		CoreArgCheck.isNotNull(vdbModelEntry, "vdbModelEntry"); //$NON-NLS-1$
		
        IResource resource = WorkspaceResourceFinderUtil.findIResourceByPath(vdbModelEntry.getName());

        if ((vdbModelEntry.getType() == ModelType.PHYSICAL_LITERAL) && (resource != null)) {
        	ModelResource mr = getModelResource(resource);
        	if( mr != null ) {
        		return hasConnectionInfo(mr);
        	}
        }
        
        return false;
	}

	/**
	 * Verifies if a <code>ModelResource</code> contains a model that contains connection information
	 * 
	 * @param modelResource the <code>ModelResource</code>
	 * @return true if model resource contains connection info. false if not.
	 */
	public boolean hasConnectionInfo(ModelResource modelResource) {
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
		
		try {
			// get name-spaced properties
			Properties props = getProperties(connectionProfile);
			// Remove old connection properties
			getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
			// Add new connection properties
			getHelper().setProperties(modelResource, props);
			// Add Translator Properties
			getHelper().setProperty(modelResource, TRANSLATOR_NAMESPACE + TRANSLATOR_NAME, DataSourceConnectionConstants.Translators.JDBC);
		} catch (ModelWorkspaceException e) {
			DqpPlugin.Util.log(IStatus.ERROR, e, 
					DqpPlugin.Util.getString("errorSettingConnectionProfilePropertiesForModelResource",  //$NON-NLS-1$
													modelResource.getItemName()));
		}
	}
	
	private Properties getProperties(IConnectionProfile connectionProfile) {
		Properties baseProps = connectionProfile.getBaseProperties();
		Properties connProps = new Properties();
		connProps.put(CONNECTION_PROFILE_NAMESPACE + PROFILE_NAME_KEY, connectionProfile.getName());
		connProps.put(CONNECTION_PROFILE_NAMESPACE + PROFILE_DESCRIPTION_KEY, connectionProfile.getDescription());
		connProps.put(CONNECTION_PROFILE_NAMESPACE + PROFILE_PROVIDER_ID_KEY, connectionProfile.getProviderId());
		Set<Object> keys = baseProps.keySet();
		for(Object  nextKey : keys ) {
			connProps.put(CONNECTION_PROFILE_NAMESPACE + nextKey, baseProps.get(nextKey));
		}
		return connProps;
	}
	
    public Properties getDataSourceProperties( ModelResource modelResource ) throws ModelWorkspaceException {
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

	private ModelResource getModelResource(IResource modelFile) {
		if (modelFile == null) {
			return null;
		}
		ModelResource mr = null;
		
		try {
			mr = ModelerCore.getModelEditor().findModelResource((IFile)modelFile);
		} catch (ModelWorkspaceException e) {
			DqpPlugin.Util.log(IStatus.ERROR, e, 
					DqpPlugin.Util.getString("errorFindingModelResourceForModelFile",  //$NON-NLS-1$
							modelFile.getName()));
		}
		
		return mr;
	}
}
