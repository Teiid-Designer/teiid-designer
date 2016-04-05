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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionConstants;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;
import org.teiid.designer.ui.common.ICredentialsCommon;


/**
 * @since 8.0
 */
public class WSSoapConnectionInfoProvider  extends ConnectionInfoHelper implements IConnectionInfoProvider, IWSProfileConstants {

    public final static String WS_CLASSNAME = "class-name"; //$NON-NLS-1$
    public final static String WS_CONNECTION_FACTORY = "org.teiid.resource.adapter.ws.WSManagedConnectionFactory"; //$NON-NLS-1$

    /**
     * Adds properties relating to the security access of the wsdl from the source set of properties
     * to the target set of properties.
     * 
     * When adding these properties to the resource the namespace is required yet when gathering
     * the properties for the teiid -ds xml, the namespace is stripped. Thus, a flag is added to handle
     * the two slightly different property keys
     * 
     * @param source
     * @param target
     * @param includeNameSpace
     */
    private void addSecurityProperties(Properties source, Properties target, boolean includeNameSpace) {
        String securityTypeId = source.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
        SecurityType securityType = SecurityType.retrieveValue(securityTypeId);
        switch (securityType) {
            case HTTPBasic: case HTTPDigest:
                String username = source.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
                if (username == null) {
                    username = source.getProperty(IWSProfileConstants.DS_AUTH_USER_NAME);
                }
                
                String key = includeNameSpace ? CONNECTION_NAMESPACE + DS_AUTH_USER_NAME : DS_AUTH_USER_NAME; 
                target.put(key, username);
                
                String password = source.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
                if (password == null) {
                    password = source.getProperty(IWSProfileConstants.DS_AUTH_PASSWORD);
                }
                
                key = includeNameSpace ? CONNECTION_NAMESPACE + DS_AUTH_PASSWORD : DS_AUTH_PASSWORD;
                target.put(key, password);
                break;
            default:
                // Do Nothing
        }
        
        /* Add the security type even if none */
        String key = includeNameSpace ? CONNECTION_NAMESPACE + DS_SECURITY_TYPE : DS_SECURITY_TYPE; 
        target.put(key, securityType.name());
    }

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
            connectionProps.put(CONNECTION_NAMESPACE + END_POINT_URI_PROP_ID, nameInSource);
        }

        String wsdlURI = props.getProperty(WSDL_URI_PROP_ID);
        if (wsdlURI != null) {
            connectionProps.put(CONNECTION_NAMESPACE + DS_ENDPOINT, wsdlURI);
        }
        
        // Security
        addSecurityProperties(props, connectionProps, true);
        
        String endPointURI = readEndPointProperty(props);
		if (endPointURI != null) {
            connectionProps.put(CONNECTION_NAMESPACE + END_POINT_URI_PROP_ID, endPointURI);
        }
              
        String securityType = props.getProperty(SECURITY_TYPE_ID);
        if (null != securityType) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + SECURITY_TYPE_ID, securityType);
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

        if (rawConnectionProps.get(WSDL_URI_PROP_ID) != null) {
            connectionProps.put(DS_ENDPOINT, rawConnectionProps.get(WSDL_URI_PROP_ID));
        }
        
        if (rawConnectionProps.get(END_POINT_URI_PROP_ID) != null) {
            connectionProps.put(END_POINT_URI_PROP_ID, rawConnectionProps.get(END_POINT_URI_PROP_ID));
        }
        
        
        if (rawConnectionProps.get(SECURITY_TYPE_ID) != null) {
            connectionProps.put(SECURITY_TYPE_ID, rawConnectionProps.get(SECURITY_TYPE_ID));
        }
        
        Properties rawTranslatorConnectionProps = removeNamespaces(getHelper().getProperties(modelResource, TRANSLATOR_NAMESPACE));
        
        if( rawTranslatorConnectionProps.getProperty(SOAP_BINDING) != null) {
        	connectionProps.put(SOAP_BINDING, rawTranslatorConnectionProps.getProperty(SOAP_BINDING));
        }
        
        // Security
        addSecurityProperties(rawConnectionProps, connectionProps, false);        

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
        Properties profileProperties = connectionProfile.getBaseProperties();
        
        String endPointProperty = ConnectionInfoHelper.readEndPointProperty(profileProperties);
        if (endPointProperty != null) {
            connectionProps.put(END_POINT_URI_PROP_ID, endPointProperty);
        }
        
        // Security
        addSecurityProperties(profileProperties, connectionProps, false);        

        connectionProps.setProperty(WS_CLASSNAME, WS_CONNECTION_FACTORY);

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
        return ModelerCore.getTeiidDataTypeManagerService().getDataSourceType(DataSourceTypes.WS);
    }
    
	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return false;
	}
}
