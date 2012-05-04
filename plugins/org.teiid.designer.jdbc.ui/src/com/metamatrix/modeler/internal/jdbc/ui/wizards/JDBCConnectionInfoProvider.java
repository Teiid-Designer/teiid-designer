/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.datatools.DatatoolsPlugin;
import org.teiid.designer.datatools.JdbcTranslatorHelper;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionConstants;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.jdbc.ui.ModelerJdbcUiPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * 
 */
public class JDBCConnectionInfoProvider extends ConnectionInfoHelper implements IConnectionInfoProvider {

    // The constants used by DTP JDBC ConnectionProfiles
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

    // the constants used by the Teiid JDBC datasources
    String DRIVER_CLASS = "driver-class"; //$NON-NLS-1$
    String VENDOR = "vendor"; //$NON-NLS-1$
    String VERSION = "version"; //$NON-NLS-1$
    String DATABASE_NAME = "databaseName"; //$NON-NLS-1$
    String URL = "connection-url"; //$NON-NLS-1$
    String USERNAME = "user-name"; //$NON-NLS-1$
    String PASSWORD = "password"; //$NON-NLS-1$
    String UNKNOWN = "unknown"; //$NON-NLS-1$

    /**
     * These are the property keys used for the jdbc source settings of physical models that were created in the legacy MMX JDBC
     * Import Wizard
     */
    public static final String JDBC_IMPORT_DRIVER_CLASS = "com.metamatrix.modeler.jdbc.JdbcSource.driverClass"; //$NON-NLS-1$
    public static final String JDBC_IMPORT_URL = "com.metamatrix.modeler.jdbc.JdbcSource.url"; //$NON-NLS-1$  
    public static final String JDBC_IMPORT_USERNAME = "com.metamatrix.modeler.jdbc.JdbcSource.username"; //$NON-NLS-1$
    public static final String JDBC_IMPORT_DRIVER_NAME = "com.metamatrix.modeler.jdbc.JdbcSource.driverName"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.ConnectionInfoHelper#setConnectionInfo(com.metamatrix.modeler.core.workspace.ModelResource,
     *      org.eclipse.datatools.connectivity.IConnectionProfile)
     */
    @Override
    public void setConnectionInfo( ModelResource modelResource,
                                   IConnectionProfile connectionProfile ) {
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(connectionProfile, "connectionProfile"); //$NON-NLS-1$

        try {
            Properties connectionProps = getCommonProfileProperties(connectionProfile);

            connectionProps.put(CONNECTION_NAMESPACE + PROFILE_ID_KEY, connectionProfile.getProviderId());
            Properties baseProps = connectionProfile.getBaseProperties();

            boolean enoughProps = true;
            if (baseProps.get(DRIVER_CLASS_KEY) != null) {
                connectionProps.put(CONNECTION_NAMESPACE + DRIVER_CLASS, baseProps.get(DRIVER_CLASS_KEY));
            } else {
                enoughProps = false;
            }

            if (baseProps.get(URL_KEY) != null) {
                connectionProps.put(CONNECTION_NAMESPACE + URL, baseProps.get(URL_KEY));
            } else {
                enoughProps = false;
            }

            if (baseProps.get(USERNAME_KEY) != null) {
                connectionProps.put(CONNECTION_NAMESPACE + USERNAME, baseProps.get(USERNAME_KEY));
            } else {
                enoughProps = false;
            }

//            if (baseProps.get(PASSWORD_KEY) != null) {
//                connectionProps.put(CONNECTION_NAMESPACE + PASSWORD, baseProps.get(PASSWORD_KEY));
//            }

            if (!enoughProps) {
                throw new ModelWorkspaceException(
                                                  ModelerJdbcUiPlugin.Util.getString("JDBCConnectionInfoProvider.notEnoughConnectionProviders", //$NON-NLS-1$
                                                                                     modelResource.getItemName()));
            }
            // Remove old connection properties
            getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
            getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);
            getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);

            // Add JDBC translator
            connectionProps.put(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, JdbcTranslatorHelper.getTranslator(connectionProfile));

            // Add new connection properties
            getHelper().setProperties(modelResource, connectionProps);

        } catch (ModelWorkspaceException e) {
            DatatoolsPlugin.Util.log(IStatus.ERROR,
                                     e,
                                     DatatoolsPlugin.Util.getString("errorSettingConnectionProfilePropertiesForModelResource", //$NON-NLS-1$
                                                                    modelResource.getItemName()));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getTeiidRelatedProperties(org.eclipse.datatools.connectivity.IConnectionProfile)
     */
    @Override
    public Properties getTeiidRelatedProperties( IConnectionProfile connectionProfile ) {
        Properties connectionProps = new Properties();
        
        Properties baseProps = connectionProfile.getBaseProperties();
        if (baseProps.get(DRIVER_CLASS) != null) {
            connectionProps.put(DRIVER_CLASS, baseProps.get(DRIVER_CLASS));
        } else if( baseProps.get(DRIVER_CLASS_KEY) != null) {
            connectionProps.put(DRIVER_CLASS, baseProps.get(DRIVER_CLASS_KEY));
        }
        
        if (baseProps.get(URL) != null) {
            connectionProps.put(URL, baseProps.get(URL));
        } else if (baseProps.get(URL_KEY) != null) {
            connectionProps.put(URL, baseProps.get(URL_KEY));
        }

        if (baseProps.get(USERNAME) != null) {
            connectionProps.put(USERNAME, baseProps.get(USERNAME));
        } else if (baseProps.get(USERNAME_KEY) != null) {
            connectionProps.put(USERNAME, baseProps.get(USERNAME_KEY));
        }

        if (baseProps.get(PASSWORD) != null) {
            connectionProps.put(PASSWORD, baseProps.get(PASSWORD));
        } else if (baseProps.get(PASSWORD_KEY) != null) {
            connectionProps.put(PASSWORD, baseProps.get(PASSWORD_KEY));
        }

        return connectionProps;
    }

    /**
     * @param resource
     * @return the JdbcSource object
     * @throws ModelWorkspaceException
     */
    private JdbcSource findJdbcSource( final IResource resource ) throws ModelWorkspaceException {

        ModelResource mr = ModelUtil.getModelResource((IFile)resource, true);
        if (mr != null) {
            Collection allEObjects = mr.getEObjects();
            for (Iterator iter = allEObjects.iterator(); iter.hasNext();) {
                EObject nextEObject = (EObject)iter.next();
                if (nextEObject instanceof JdbcSource) {
                    return (JdbcSource)nextEObject;
                }
            }
        } else {
            throw new ModelWorkspaceException(
                                              ModelerJdbcUiPlugin.Util.getString("JDBCConnectionInfoProvider.errorFindingModelResourceForModelFile", //$NON-NLS-1$
                                                                                 resource.getName()));
        }

        return null;
    }

    /**
     * Get a set of property name to values for JDBC connection properties in a model's JdbcSource object. These properties are
     * "legacy" properties and should be treated as deprecated and may or may not exist.
     * 
     * @param modelResource the model resource containing the JDBC properties being requested
     * @return properties, the JDBC connection properties (never <code>null</code> but maybe empty)
     * @throws ModelWorkspaceException
     * @since 5.0
     */
    public Properties getModelJdbcConnectionProperties( ModelResource modelResource ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        Properties result = new Properties();

        if (ModelUtil.isPhysical(modelResource.getEmfResource())) {

            JdbcSource jdbcSource = findJdbcSource(modelResource.getCorrespondingResource());
            if (jdbcSource != null) {

                if (jdbcSource.getDriverClass() != null) {
                    result.put(DRIVER_CLASS, jdbcSource.getDriverClass());
                }

                if (jdbcSource.getUrl() != null) {
                    result.put(URL, jdbcSource.getUrl());
                }

                if (jdbcSource.getUsername() != null) {
                    result.put(USERNAME, jdbcSource.getUsername());
                }

                if (jdbcSource.getPassword() != null) {
                    result.put(PASSWORD, jdbcSource.getPassword());
                }
            } else {

            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getPasswordPropertyKey()
     */
    @Override
    public String getPasswordPropertyKey() {
        return IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getDataSourcePasswordPropertyKey()
     */
    @Override
    public String getDataSourcePasswordPropertyKey() {
        return PASSWORD;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoProvider#getDataSourceType()
     */
    @Override
    public String getDataSourceType() {
        return DataSourceConnectionConstants.DataSource.JDBC;
    }

	@Override
	public boolean requiresPassword(IConnectionProfile connectionProfile) {
		return true;
	}
    
}
