/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.util;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionConstants;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.salesforce.ISalesForceProfileConstants;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * 
 */
public class SalesForceConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {

    public final static String SALESFORCE_DATASOURCE_URL = "url"; //$NON-NLS-1$
    public final static String SALESFORCE_DATASOURCE_USERNAME = "username"; //$NON-NLS-1$
    public final static String SALESFORCE_DATASOURCE_PASSWORD = "password"; //$NON-NLS-1$
    public final static String SALESFORCE_TRANSLATOR_NAME = "salesforce"; //$NON-NLS-1$
    public final static String SALESFORCE_TRANSLATOR_TYPE = "salesforce"; //$NON-NLS-1$
    public final static String SALESFORCE_PASSWORD_KEY = "password"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.ConnectionInfoHelper#setConnectionInfo(org.teiid.designer.datatools.connection.ModelResource,
     *      org.eclipse.datatools.connectivity.IConnectionProfile)
     */
    @Override
    public void setConnectionInfo( ModelResource modelResource,
                                   IConnectionProfile connectionProfile ) throws ModelWorkspaceException {
        Properties connectionProps = getCommonProfileProperties(connectionProfile);

        Properties props = connectionProfile.getBaseProperties();

        // Don't put the password in the model
        String url = props.getProperty(ISalesForceProfileConstants.URL_PROP_ID);
        if (null != url) {
            connectionProps.setProperty(CONNECTION_NAMESPACE + SALESFORCE_DATASOURCE_URL, url);
        }
        String username = props.getProperty(ISalesForceProfileConstants.USERNAME_PROP_ID);
        connectionProps.setProperty(CONNECTION_NAMESPACE + SALESFORCE_DATASOURCE_USERNAME, username);

        // Remove old connection properties
        getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
        getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
        getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

        connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, SALESFORCE_TRANSLATOR_NAME);
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

        Properties props = connectionProfile.getBaseProperties();

        // Don't put the password in the model
        String url = props.getProperty(ISalesForceProfileConstants.URL_PROP_ID);
        if (null != url) {
            connectionProps.setProperty(SALESFORCE_DATASOURCE_URL, url);
        }
        String username = props.getProperty(ISalesForceProfileConstants.USERNAME_PROP_ID);
        if( username != null ) {
        	connectionProps.setProperty(SALESFORCE_DATASOURCE_USERNAME, username);
        }
        String password = props.getProperty(ISalesForceProfileConstants.PASSWORD_PROP_ID);
        if( password != null ) {
        	connectionProps.setProperty(SALESFORCE_DATASOURCE_PASSWORD, password);
        }
        return connectionProps;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getPasswordPropertyKey()
     */
    @Override
    public String getPasswordPropertyKey() {
        return SALESFORCE_PASSWORD_KEY;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getDataSourcePasswordPropertyKey()
     */
    @Override
    public String getDataSourcePasswordPropertyKey() {
        return SALESFORCE_PASSWORD_KEY;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getDataSourceType()
     */
    @Override
    public String getDataSourceType() {
        return DataSourceConnectionConstants.DataSource.SALESFORCE;
    }

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return true;
	}
}
