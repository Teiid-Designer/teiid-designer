/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionConstants;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;


/**
 * @since 8.0
 */
public class WSSoapConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider, IWSProfileConstants {

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#setConnectionInfo(ModelResource, IConnectionProfile)
     */
    @Override
    public void setConnectionInfo( ModelResource modelResource,
                                   IConnectionProfile connectionProfile ) throws ModelWorkspaceException {
        Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();

        String nameInSource = modelResource.getModelAnnotation().getNameInSource();
        if (nameInSource != null) {
            connectionProps.put(CONNECTION_NAMESPACE + SOURCE_ENDPOINT, nameInSource);
        }

        if (props.getProperty(SOAP_ENDPOINT_KEY) != null) {
            connectionProps.put(CONNECTION_NAMESPACE + DS_ENDPOINT, props.getProperty(SOAP_ENDPOINT_KEY));
        }
        
        String url = readURLProperty(props);
		if (url != null) {
            connectionProps.put(CONNECTION_NAMESPACE + END_POINT_URI_PROP_ID, url);
        }
		
        if (props.getProperty(CONNECTION_CLASS_KEY) != null) {
            connectionProps.put(CONNECTION_NAMESPACE + CONNECTION_CLASS_KEY, props.getProperty(CONNECTION_CLASS_KEY));
        }
        if (props.getProperty(DRIVER_CLASS_PATH_KEY) != null) {
            connectionProps.put(CONNECTION_NAMESPACE + DRIVER_CLASS_PATH_KEY, props.getProperty(DRIVER_CLASS_PATH_KEY));
        }

        // get the name in source, it's the Endpoint that teiid needs

        getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
        getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
        getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

        connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, DataSourceConnectionConstants.Translators.WS);
        if( props.getProperty(SOAP_SERVICE_MODE) != null) {
        	connectionProps.put(TRANSLATOR_NAMESPACE + SOAP_SERVICE_MODE, props.getProperty(SOAP_SERVICE_MODE));
        }
        if( props.getProperty(SOAP_BINDING) != null) {
        	connectionProps.put(TRANSLATOR_NAMESPACE + SOAP_BINDING, props.getProperty(SOAP_BINDING));
        }
        getHelper().setProperties(modelResource, connectionProps);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.ConnectionInfoHelper#getConnectionProperties(org.teiid.designer.core.workspace.ModelResource)
     */
    @Override
    public Properties getConnectionProperties( ModelResource modelResource ) throws ModelWorkspaceException {
        Properties rawConnectionProps = removeNamespaces(getHelper().getProperties(modelResource, CONNECTION_NAMESPACE));
        Properties connectionProps = new Properties();

        if (rawConnectionProps.get(SOURCE_ENDPOINT) != null) {
            connectionProps.put(DS_ENDPOINT, rawConnectionProps.get(SOURCE_ENDPOINT));
        }

        return connectionProps;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getTeiidRelatedProperties(org.eclipse.datatools.connectivity.IConnectionProfile)
     */
    @Override
    public Properties getTeiidRelatedProperties( IConnectionProfile connectionProfile ) {
        Properties connectionProps = new Properties();
        // connectionProps.put(IConnectionInfoHelper.PROFILE_PROVIDER_ID_KEY, connectionProfile.getProviderId());

        Properties props = connectionProfile.getBaseProperties();
        if (props.get(DS_ENDPOINT) != null) {
            connectionProps.put(SOURCE_ENDPOINT, props.get(DS_ENDPOINT));
        } else if (props.get(SOURCE_ENDPOINT) != null) {
            connectionProps.put(SOURCE_ENDPOINT, props.get(SOURCE_ENDPOINT));
        }

        return connectionProps;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getPasswordPropertyKey()
     */
    @Override
    public String getPasswordPropertyKey() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getDataSourcePasswordPropertyKey()
     */
    @Override
    public String getDataSourcePasswordPropertyKey() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getDataSourceType()
     */
    @Override
    public String getDataSourceType() {
        return DataSourceConnectionConstants.DataSource.WS;
    }
    
	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return false;
	}
}
