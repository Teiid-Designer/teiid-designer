package com.metamatrix.modeler.modelgenerator.wsdl.datatools;

import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

public class WSDLConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {
    public static final String WSDL_URI = "wsdlURI";
    public static final String WS_TRANSLATOR_NAME = "ws";
    public static final String ENDPOINT = "Endpoint";

    @Override
    public void setConnectionInfo( ModelResource modelResource,
                                   IConnectionProfile connectionProfile ) throws ModelWorkspaceException {
        Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();

        // Don't put the password in the model
        String uri = props.getProperty(WSDL_URI);
        if (null != uri) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + ENDPOINT, uri);
        }

        // Remove old connection properties
        getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
        getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
        getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

        connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, WS_TRANSLATOR_NAME);
        // connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_TYPE_KEY, SALESFORCE_TRANSLATOR_TYPE);
        getHelper().setProperties(modelResource, connectionProps);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getTeiidRelatedProperties(org.eclipse.datatools.connectivity.IConnectionProfile)
     */
    @Override
    public Properties getTeiidRelatedProperties( IConnectionProfile connectionProfile ) {
        Properties connectionProps = new Properties();
        connectionProps.put(IConnectionInfoHelper.PROFILE_PROVIDER_ID_KEY, connectionProfile.getProviderId());

        Properties props = connectionProfile.getBaseProperties();

        // Don't put the password in the model
        String uri = props.getProperty(WSDL_URI);
        if (null != uri) {
            connectionProps.setProperty(ENDPOINT, uri);
        }
        return connectionProps;
    }

    @Override
    public String getPasswordPropertyKey() {
        return null;
    }

    @Override
    public String getDataSourceType() {
        return WS_TRANSLATOR_NAME;
    }

}
