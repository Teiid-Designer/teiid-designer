package org.teiid.designer.datatools.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.datatools.DatatoolsPlugin;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ResourceAnnotationHelper;

public class ConnectionInfoHelper implements IConnectionInfoHelper {

    private ResourceAnnotationHelper resourceAnnotationHelper;

    private ConnectionProfileFactory connectionProfileFactory;

    public ConnectionInfoHelper() {
        super();
    }

    public ConnectionInfoHelper( ResourceAnnotationHelper resourceAnnotationHelper,
                                 ConnectionProfileFactory connectionProfileFactory ) {
        super();
        CoreArgCheck.isNotNull(resourceAnnotationHelper, "resourceAnnotationHelper"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(connectionProfileFactory, "connectionProfileFactory"); //$NON-NLS-1$
        this.resourceAnnotationHelper = resourceAnnotationHelper;
        this.connectionProfileFactory = connectionProfileFactory;
    }

    protected ResourceAnnotationHelper getHelper() {
        if (resourceAnnotationHelper == null) {
            resourceAnnotationHelper = new ResourceAnnotationHelper();
        }

        return resourceAnnotationHelper;
    }

    /**
     * Provides means to find a connection profile for a supplied <code>ModelResource</code>
     * 
     * @param modelResource
     * @return
     */
    @Override
    public IConnectionProfile getConnectionProfile( ModelResource modelResource ) {
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$

        Properties connectionProps = null;
        Properties profileProps = null;

        try {
            profileProps = getHelper().getProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
            connectionProps = getHelper().getProperties(modelResource, CONNECTION_NAMESPACE);
        } catch (Exception e) {
            DatatoolsPlugin.Util.log(IStatus.ERROR,
                                     e,
                                     DatatoolsPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource", //$NON-NLS-1$
                                                                    modelResource.getItemName()));
        }
        if (connectionProps == null || connectionProps.isEmpty() || profileProps == null || profileProps.isEmpty()) {
            return null;
        }

        // Now we need to
        // cache non-base-property values
        String name = profileProps.getProperty(CONNECTION_PROFILE_NAMESPACE + PROFILE_NAME_KEY);
        String desc = profileProps.getProperty(CONNECTION_PROFILE_NAMESPACE + PROFILE_DESCRIPTION_KEY);
        String id = profileProps.getProperty(CONNECTION_PROFILE_NAMESPACE + PROFILE_PROVIDER_ID_KEY);

        // Reconstruct the set of base-properties for the ConnectionProfile
        // Need to swap out the property keys
        Properties baseProps = new Properties();
        Set<Object> keys = connectionProps.keySet();
        for (Object nextKey : keys) {
            baseProps.put(removeNamespace((String)nextKey), connectionProps.getProperty((String)nextKey));
        }

        return createConnectionProfile(name, desc, id, baseProps);
    }

    /**
     * Helper method to create an instance of a <code>ConnectionProfileFactory</code>.
     * 
     * @param name the name of the connection profile
     * @param description the description
     * @param id the provider ID
     * @param props the base properties
     * @return the connection profile
     */
    @Override
    public ConnectionProfile createConnectionProfile( String name,
                                                      String description,
                                                      String id,
                                                      Properties props ) {
        if (this.connectionProfileFactory == null) {
            this.connectionProfileFactory = new ConnectionProfileFactory();
        }

        return this.connectionProfileFactory.createConnectionProfile(name, description, id, props);
    }

    protected String removeNamespace( String str ) {

        int semiColonIndex = str.indexOf(':') + 1;
        if (semiColonIndex > 0) {
            return str.substring(semiColonIndex, str.length());
        }

        return str;
    }

    protected Properties removeNamespaces( Properties props ) {
        Properties result = new Properties();
        Set<Object> keys = props.keySet();
        for (Object nextKey : keys) {
            result.put(removeNamespace((String)nextKey), props.getProperty((String)nextKey));
        }
        return result;
    }

    /**
     * Verifies if a <code>ModelResource</code> contains a model that contains connection information
     * 
     * @param modelResource the <code>ModelResource</code>
     * @return true if model resource contains connection info. false if not.
     */
    @Override
    public boolean hasConnectionInfo( ModelResource modelResource ) {
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$

        Properties props = null;

        try {
            props = getHelper().getProperties(modelResource, CONNECTION_NAMESPACE);
        } catch (Exception e) {
            DatatoolsPlugin.Util.log(IStatus.ERROR,
                                     e,
                                     DatatoolsPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource", //$NON-NLS-1$
                                                                    modelResource.getItemName()));
        }
        if (props == null || props.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Returns the collective properties of a <code>ConnectionProfile</code> to include name, description and provider id in
     * addition to it's base properties. These properties are also prefixed with a custom namespace for storage in a model
     * resource "annotation"
     * 
     * @param connectionProfile the connection profile
     * @return the name-spaced properties for the connection profile
     */
    @Override
    public Properties getProperties( IConnectionProfile connectionProfile ) {
        CoreArgCheck.isNotNull(connectionProfile, "connectionProfile"); //$NON-NLS-1$

        if (this.connectionProfileFactory == null) {
            this.connectionProfileFactory = new ConnectionProfileFactory();
        }
        return this.connectionProfileFactory.getNamespacedProperties(connectionProfile);
    }

    @Override
    public Properties getCommonProfileProperties( IConnectionProfile profile ) {
        Properties commonProps = new Properties();
        commonProps.put(IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + IConnectionInfoHelper.PROFILE_NAME_KEY,
                        profile.getName());
        commonProps.put(IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + IConnectionInfoHelper.PROFILE_DESCRIPTION_KEY,
                        profile.getDescription());
        commonProps.put(IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + IConnectionInfoHelper.PROFILE_PROVIDER_ID_KEY,
                        profile.getProviderId());
        commonProps.put(IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + IConnectionInfoHelper.PROFILE_ID_KEY,
                        profile.getInstanceID());
        commonProps.put(IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE + IConnectionInfoHelper.CATEGORY_ID_KEY,
                        profile.getCategory().getId());
        return commonProps;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getConnectionProperties(com.metamatrix.modeler.core.workspace.ModelResource)
     */
    @Override
    public Properties getConnectionProperties( ModelResource modelResource ) throws ModelWorkspaceException {
        return removeNamespaces(getHelper().getProperties(modelResource, CONNECTION_NAMESPACE));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getConnectionProperties(com.metamatrix.modeler.core.workspace.ModelResource)
     */
    @Override
    public Properties getProfileProperties( ModelResource modelResource ) throws ModelWorkspaceException {
        return removeNamespaces(getHelper().getProperties(modelResource, CONNECTION_PROFILE_NAMESPACE));
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.datatools.connection.IConnectionInfoHelper#getTranslatorProperties(com.metamatrix.modeler.core.workspace.ModelResource)
     */
    @Override
    public Properties getTranslatorProperties( ModelResource modelResource ) {
    	Properties props = null;
    	
        try {
            props = removeNamespaces(getHelper().getProperties(modelResource, TRANSLATOR_NAMESPACE));
        } catch (Exception e) {
            DatatoolsPlugin.Util.log(IStatus.ERROR,
                                     e,
                                     DatatoolsPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource", //$NON-NLS-1$
                                                                    modelResource.getItemName()));
        }
    	
        return props;
    }

    @Override
    public String getTranslatorName( ModelResource modelResource ) {
        if( modelResource == null ) {
        	return StringUtil.Constants.EMPTY_STRING;
        }

        Properties props = null;

        try {
            props = getHelper().getProperties(modelResource, TRANSLATOR_NAMESPACE);
        } catch (Exception e) {
            DatatoolsPlugin.Util.log(IStatus.ERROR,
                                     e,
                                     DatatoolsPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource", //$NON-NLS-1$
                                                                    modelResource.getItemName()));
        }

        return props.getProperty(TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, StringUtil.Constants.EMPTY_STRING);
    }
    
    public void setTranslatorName( ModelResource modelResource, String translatorName ) {
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(translatorName, "translatorName"); //$NON-NLS-1$
        
        try {
            getHelper().setProperty(modelResource, TRANSLATOR_NAMESPACE + TRANSLATOR_NAME_KEY, translatorName);
        } catch (Exception e) {
            DatatoolsPlugin.Util.log(IStatus.ERROR,
                                     e,
                                     DatatoolsPlugin.Util.getString("errorFindingConnectionProfilePropertiesForModelResource", //$NON-NLS-1$
                                                                    modelResource.getItemName()));
        }
    }

    /**
     * @param model the model whose JNDI data source name is being requested
     * @param uuid the workspace UUID
     * @return the JNDI name (never <code>null</code>)
     */
    public String generateUniqueConnectionJndiName( IFile model,
                                                    String uuid ) {
        return generateUniqueConnectionJndiName(model.getFullPath().removeFileExtension().lastSegment(),
                                                model.getFullPath(),
                                                ModelerCore.workspaceUuid().toString());
    }

    /**
     * Creates a unique JNDI name for a source model based on a <code>ModelResource</code>
     * 
     * @param modelResource
     * @return the JNDI name
     */
    @Override
    public String generateUniqueConnectionJndiName( ModelResource modelResource,
                                                    String uuid ) {
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(uuid, "uuid"); //$NON-NLS-1$
        return generateUniqueConnectionJndiName(modelResource.getItemName(),
                                                modelResource.getPath(),
                                                ModelerCore.workspaceUuid().toString());
    }

    /**
     * Creates a unique JNDI name for a source model based on a name and path in the workspace
     * 
     * @param name
     * @param path
     * @return
     */
    @Override
    public String generateUniqueConnectionJndiName( String name,
                                                    IPath path,
                                                    String uuid ) {

        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(path, "path"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(uuid, "uuid"); //$NON-NLS-1$

        final StringBuilder builder = new StringBuilder(uuid + '_');
        for (final String segment : path.removeLastSegments(1).segments())
            builder.append(segment).append('_');

        return builder.append(name).toString();
    }

    public String findTranslatorName( ModelResource modelResource ) {
        String translator = getTranslatorName(modelResource);
        for (String translatorName : DataSourceConnectionConstants.Translators.JDBC_TRANSLATORS) {
            if (translator.toUpperCase().contains(translatorName.toUpperCase())) {
                return translatorName;
            }
        }

        return null;
    }

    /**
     * Given a set of properties, find a suitable translator name.
     * 
     * @param matchableStrings
     * @param translators
     * @return
     */
    public String findTranslatorName( Properties properties ) {
        Collection<String> matchableStrings = new ArrayList<String>();
        matchableStrings.add(properties.getProperty(IConnectionInfoHelper.TRANSLATOR_NAME_KEY));

        for (String translatorName : DataSourceConnectionConstants.Translators.ALL_TRANSLATORS) {
            for (String keyword : matchableStrings) {
                if (keyword.toUpperCase().contains(translatorName.toUpperCase())) {
                    return translatorName;
                }
            }
        }
        return null;
    }

    /**
     * Given a set of matchable strings and a set of data source type strings, find a suitable match. If a match isn't found, then
     * <unknown> string is returned.
     * 
     * @param matchableStrings
     * @param dataSourceTypeNames
     * @return
     */
    @Override
    public String findMatchingDataSourceTypeName( Collection<String> matchableStrings,
                                                  Set<String> dataSourceTypeNames ) {
        for (String dsTypeName : dataSourceTypeNames) {
            for (String keyword : matchableStrings) {
                if (keyword.toUpperCase().contains(dsTypeName.toUpperCase())) {
                    return dsTypeName;
                }
            }
        }

        return DataSourceConnectionConstants.Translators.UNKNOWN;
    }

    @Override
    public String findMatchingDataSourceTypeName( ModelResource modelResource ) throws ModelWorkspaceException {
        ResourceAnnotationHelper helper = new ResourceAnnotationHelper();
        Properties properties = helper.getProperties(modelResource, IConnectionInfoHelper.TRANSLATOR_NAMESPACE);
        return findMatchingDataSourceTypeName(properties);
    }

    @Override
    public String findMatchingDataSourceTypeName( Properties properties ) {
        for (String translatorName : DataSourceConnectionConstants.Translators.ALL_TRANSLATORS) {
            String translator = properties.getProperty(IConnectionInfoHelper.TRANSLATOR_NAME_KEY);
            if (translator.toUpperCase().contains(translatorName.toUpperCase())) {
                return translator;
            }

        }
        return DataSourceConnectionConstants.DataSource.UNKNOWN;
    }
    
    /**
     * Utility method to clear all connection info properties from a source model
     * 
     * @param modelResource
     * @throws ModelWorkspaceException
     */
	public void clearConnectionInfo(ModelResource modelResource) throws ModelWorkspaceException {
		// Remove old connection properties
        getHelper().removeProperties(modelResource, CONNECTION_PROFILE_NAMESPACE);
        getHelper().removeProperties(modelResource, CONNECTION_NAMESPACE);
        getHelper().removeProperties(modelResource, TRANSLATOR_NAMESPACE);
	}
}
