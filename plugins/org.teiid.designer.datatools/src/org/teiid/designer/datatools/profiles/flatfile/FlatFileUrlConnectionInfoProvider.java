/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.flatfile;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;


/**
 * @since 8.0
 */
public class FlatFileUrlConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider {

    public final static String WS_CLASSNAME = "class-name"; //$NON-NLS-1$
    public final static String WS_CONNECTION_FACTORY = "org.teiid.resource.adapter.ws.WSManagedConnectionFactory"; //$NON-NLS-1$

    @Override
	public String getDataSourcePasswordPropertyKey() {
		return null;
	}

	@Override
	public String getDataSourceType() {
	    return ModelerCore.getTeiidDataTypeManagerService().getDataSourceType(DataSourceTypes.WS);
	}

	@Override
	public String getPasswordPropertyKey() {
		return "bogus_xxxx"; //$NON-NLS-1$
	}

	@Override
	public Properties getTeiidRelatedProperties(IConnectionProfile connectionProfile) {
        Properties connectionProps = new Properties();
        
        Properties props =  connectionProfile.getBaseProperties();
        
        String fileUrl = props.getProperty(IFlatFileProfileConstants.URL_PROP_ID);
        if( fileUrl != null ) {
			IPath fullPath = new Path(fileUrl);
			String fileURL = fullPath.toString();
        	connectionProps.setProperty(IFlatFileProfileConstants.WS_ENDPOINT_KEY, fileURL);
        }
        connectionProps.setProperty(WS_CLASSNAME, WS_CONNECTION_FACTORY);

        return connectionProps;
	}

	@Override
	public void setConnectionInfo(ModelResource modelResource, IConnectionProfile connectionProfile)
			throws ModelWorkspaceException {
		Properties connectionProps = getCommonProfileProperties(connectionProfile);

		Properties props = connectionProfile.getBaseProperties();

		String result = props.getProperty(IFlatFileProfileConstants.URL_PROP_ID);
		if (null != result) {
			connectionProps.setProperty(CONNECTION_NAMESPACE + IFlatFileProfileConstants.URL_PROP_ID, result);
		}


		getHelper().removeProperties(modelResource,
				CONNECTION_PROFILE_NAMESPACE);
		getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
		getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

		connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "ws"); //$NON-NLS-1$
		getHelper().setProperties(modelResource, connectionProps);

	}

	@Override
	public Properties getCommonProfileProperties(IConnectionProfile profile) {
		return super.getCommonProfileProperties(profile);
	}

	@Override
	public Properties getConnectionProperties(ModelResource modelResource)
			throws ModelWorkspaceException {
		Properties modelProps = super.getConnectionProperties(modelResource);
		
		Properties connProps = new Properties();
		// Search for "URL" value
		
		String url = (String)modelProps.get(IFlatFileProfileConstants.URL_PROP_ID);
		if( url != null ) {
			connProps.put(IFlatFileProfileConstants.WS_ENDPOINT_KEY, url);
			connProps.put(WS_CLASSNAME, WS_CONNECTION_FACTORY);
		}
		
		return connProps;
	}

	@Override
	public String getTranslatorName(ModelResource modelResource) {
		return "ws"; //$NON-NLS-1$
	}

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return false;
	}
}
