/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionConstants;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * 
 */
public class SOAPConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {

    /*
     * Teiid Data Source property key.
     * 
     * Currently only EndPoint is the only property provided by Data Tools connection profile that matches up.
     */
    public static final String DS_ENDPOINT = "wsdlURI"; //$NON-NLS-1$
    public static final String DS_SECURITY_TYPE = "SecurityType"; //$NON-NLS-1$";
    public static final String DS_AUTH_USER_NAME = "AuthUserName"; //$NON-NLS-1$
    public static final String DS_AUTH_PASSWORD = "AuthPassword"; //$NON-NLS-1$
    public static final String DS_WS_SECURITY_CONFIG_URL = "WsSecurityConfigURL"; //$NON-NLS-1$
    public static final String DS_WS_SECURITY_CONFIG_NAME = "WsSecurityConfigName"; //$NON-NLS-1$

    public static final String SOURCE_ENDPOINT = "EndPoint"; //$NON-NLS-1$
    public static final String SOAP_SERVICE_MODE = "DefaultServiceMode";  //$NON-NLS-1$
    public static final String SOAP_BINDING = "DefaultBinding";  //$NON-NLS-1$

    /*
     * The Web Services Data Source object contains the following properties
     * 
     * connectionClass=org.my.custom.driver.Class
     * soapEndPoint=http://my.soap.endpoint.url
     * driverClassPath=org.my.first.jar;org.my.second.jar;
     * 
     * The only property that matches up with the teiid-connector-ws.jar definition is the soapEndPoint
     * 
     */
    public static final String SOAP_ENDPOINT_KEY = "soapEndPoint"; //$NON-NLS-1$
    public static final String WSDL_URI_KEY = "EndPoint"; //$NON-NLS-1$
    public static final String CONNECTION_CLASS_KEY = "connectionClass"; //$NON-NLS-1$
    public static final String DRIVER_CLASS_PATH_KEY = "driverClassPath"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#setConnectionInfo(com.metamatrix.modeler.core.workspace.ModelResource,
     *      org.teiid.designer.datatools.connection.IConnectionProfile)
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
        if (props.getProperty(WSDL_URI_KEY) != null) {
            connectionProps.put(CONNECTION_NAMESPACE + WSDL_URI_KEY, props.getProperty(WSDL_URI_KEY));
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
     * @see org.teiid.designer.datatools.connection.ConnectionInfoHelper#getConnectionProperties(com.metamatrix.modeler.core.workspace.ModelResource)
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
