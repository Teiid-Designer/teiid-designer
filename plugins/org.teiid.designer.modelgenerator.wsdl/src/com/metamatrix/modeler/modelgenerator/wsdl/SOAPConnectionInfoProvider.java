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
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * 
 */
public class SOAPConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {

	public static final String ENDPOINT = "Endpoint";
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

        connectionProps.put(CONNECTION_NAMESPACE + ENDPOINT, modelResource.getModelAnnotation().getNameInSource());
        // get the name in source, it's the Endpoint that teiid needs

        getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
        getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
        getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);

        connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, "ws");
        getHelper().setProperties(modelResource, connectionProps);
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
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getDataSourceType()
     */
    @Override
    public String getDataSourceType() {
        return "connector-ws";
    }

}
