/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelResource;

public class BindingGenerator {

    public static void createConnectorBinding( IConnectionProfile connectionProfile,
                                               ModelResource modelResource ) {

        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(connectionProfile, "connectionProfile"); //$NON-NLS-1$

        ConnectionInfoHelper helper = new ConnectionInfoHelper();
        helper.setConnectionInfo(modelResource, connectionProfile);

    }

}
