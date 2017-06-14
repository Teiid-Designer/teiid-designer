package org.teiid.designer.datatools.profiles.mongodb;

import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.DatatoolsPlugin;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;


/**
 * @since 8.0
 */
public class MongoDbConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider {

    public final static String MONGODB_CLASSNAME = "class-name"; //$NON-NLS-1$
    public final static String MONGODB_CONNECTION_FACTORY = "org.teiid.resource.adapter.mongodb.MongoDBManagedConnectionFactory"; //$NON-NLS-1$

    @Override
	public String getDataSourcePasswordPropertyKey() {
		// MongoDb connection profile doesn't use password, but need one to prevent NPE
		return null;
	}

	@Override
	public String getDataSourceType() {
	    return ModelerCore.getTeiidDataTypeManagerService().getDataSourceType(DataSourceTypes.MONGODB);
	}

	@Override
	public String getPasswordPropertyKey() {
		// Flat file connection profile doesn't use password, but need one to prevent NPE
		return "bogus_xxxx"; //$NON-NLS-1$
	}

	@Override
	public Properties getTeiidRelatedProperties(IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties baseProps = connectionProfile.getBaseProperties();

        // ---------------------------------------------------------------------
        // Base properties may already include the Teiid Connection properties
        // ---------------------------------------------------------------------
        
        // Database name
        if( !CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.PROP_DATABASE)) ) {
        	connectionProps.put(IMongoDbProfileConstants.PROP_DATABASE, baseProps.get(IMongoDbProfileConstants.PROP_DATABASE));
        } else if( !CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.CP_DATABASE_NAME_KEY)) ) {
            connectionProps.put(IMongoDbProfileConstants.PROP_DATABASE, baseProps.get(IMongoDbProfileConstants.CP_DATABASE_NAME_KEY));
        }

        // Username
        if( !CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.PROP_USERNAME)) ) {
        	connectionProps.put(IMongoDbProfileConstants.PROP_USERNAME, baseProps.get(IMongoDbProfileConstants.PROP_USERNAME));
        } else if( !CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.CP_USERNAME_KEY)) ) {
        	connectionProps.put(IMongoDbProfileConstants.PROP_USERNAME, baseProps.get(IMongoDbProfileConstants.CP_USERNAME_KEY));
        }

        // Password
        if( !CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.PROP_PASSWORD)) ) {
        	connectionProps.put(IMongoDbProfileConstants.PROP_PASSWORD, baseProps.get(IMongoDbProfileConstants.PROP_PASSWORD));
        } else if( !CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.CP_PASSWORD_KEY)) ) {
        	connectionProps.put(IMongoDbProfileConstants.PROP_PASSWORD, baseProps.get(IMongoDbProfileConstants.CP_PASSWORD_KEY));
        }

        // Remote Server List
        if( !CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.PROP_REMOTE_SERVER_LIST)) ) {
           	connectionProps.put(IMongoDbProfileConstants.PROP_REMOTE_SERVER_LIST, baseProps.get(IMongoDbProfileConstants.PROP_REMOTE_SERVER_LIST));
        } else if( (!CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.CP_SERVER_HOST_KEY))) && (!CoreStringUtil.isEmpty(baseProps.getProperty(IMongoDbProfileConstants.CP_SERVER_PORT_KEY))) ) {
        	String host = baseProps.getProperty(IMongoDbProfileConstants.CP_SERVER_HOST_KEY);
        	String port = baseProps.getProperty(IMongoDbProfileConstants.CP_SERVER_PORT_KEY);
           	connectionProps.put(IMongoDbProfileConstants.PROP_REMOTE_SERVER_LIST, host+":"+port); //$NON-NLS-1$
        }
        
        connectionProps.setProperty(MONGODB_CLASSNAME, MONGODB_CONNECTION_FACTORY);
        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile) throws ModelWorkspaceException {
		
		// Properties common to connection profiles
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		// This gets the MongoDB CP properties
		Properties props = connectionProfile.getBaseProperties();

		// The MongoDB CP properties are converted into the corresponding properties expected by the Teiid DS
		String result = props.getProperty(IMongoDbProfileConstants.CP_DATABASE_NAME_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + IMongoDbProfileConstants.PROP_DATABASE, result);
		} 
		
		result = props.getProperty(IMongoDbProfileConstants.CP_USERNAME_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + IMongoDbProfileConstants.PROP_USERNAME, result);
		}

		result = props.getProperty(IMongoDbProfileConstants.CP_PASSWORD_KEY);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + IMongoDbProfileConstants.PROP_PASSWORD, result);
		}
		
		String host = props.getProperty(IMongoDbProfileConstants.CP_SERVER_HOST_KEY);
		String port = props.getProperty(IMongoDbProfileConstants.CP_SERVER_PORT_KEY);
		if ( (null!=host) && (null!=port) ) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + IMongoDbProfileConstants.PROP_REMOTE_SERVER_LIST, host+":"+port); //$NON-NLS-1$
		}
		
		getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
		getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
		getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

		connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "mongodb"); //$NON-NLS-1$
		getHelper().setProperties(modelResource, connectionProps);

	}

	@Override
	public Properties getCommonProfileProperties(IConnectionProfile profile) {
		return super.getCommonProfileProperties(profile);
	}

	@Override
	public IConnectionProfile getConnectionProfile(ModelResource modelResource) {
		IConnectionProfile profile = super.getConnectionProfile(modelResource);
		
		Properties baseProps = profile.getBaseProperties();
		Properties mongoProps = new Properties();
		
		try {
			mongoProps = getConnectionProperties(modelResource);
		} catch (ModelWorkspaceException e) {
			DatatoolsPlugin.Util.log(e);
		}
		
		String result = mongoProps.getProperty(IMongoDbProfileConstants.PROP_DATABASE);
		if( result != null ) {
			baseProps.put(IMongoDbProfileConstants.CP_DATABASE_NAME_KEY, result);
		}
		result = mongoProps.getProperty(IMongoDbProfileConstants.PROP_USERNAME);
		if( result != null ) {
			baseProps.put(IMongoDbProfileConstants.CP_USERNAME_KEY, result);
		}
		result = mongoProps.getProperty(IMongoDbProfileConstants.PROP_PASSWORD);
		if( result != null ) {
			baseProps.put(IMongoDbProfileConstants.CP_PASSWORD_KEY, result);
		}
		String serverList = mongoProps.getProperty(IMongoDbProfileConstants.PROP_REMOTE_SERVER_LIST);
		if( serverList != null ) {
			String token;
			int i=0;
	        for (final StringTokenizer iter = new StringTokenizer(serverList, ":"); iter.hasMoreTokens();) { //$NON-NLS-1$
	            token = iter.nextToken();
	            if(i==0) {
	    			baseProps.put(IMongoDbProfileConstants.CP_SERVER_HOST_KEY, token);
	            } else if(i==1) {
	    			baseProps.put(IMongoDbProfileConstants.CP_SERVER_PORT_KEY, token);
	            }
	            i++;
	        }
		}
		
		return profile;
	}

	@Override
	public String getTranslatorName(ModelResource modelResource) {
		return "mongodb"; //$NON-NLS-1$
	}

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return false;
	}
}
