/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.connection;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.vdb.VdbModelEntry;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DataSourceConnectionConstants;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * @since 4.3
 */
public final class ConnectionUtils {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Don't allow construction.
     */
    private ConnectionUtils() {
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param msgKey the properties file key
     * @param params the optional message data parameters
     * @return the error status object with the localized message
     * @since 6.0.0
     */
    public static IStatus createErrorStatus( String msgKey,
                                             String... params ) {
        String msg = (params == null) ? DqpPlugin.Util.getString(msgKey) : DqpPlugin.Util.getString(msgKey, (Object[])params);
        return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, msg);
    }

    /**
     * Get a <code>Map</code> of property name to values for JDBC connection properties stored on a model reference on the vdb
     * manifest model.
     * 
     * @param theModelRef the model reference whose connection properties are being requested
     * @return a map of JDBC connection properties (never <code>null</code> but maybe empty)
     * @throws ModelWorkspaceException 
     * @since 5.0
     */
    public static Properties getModelJdbcProperties( VdbModelEntry modelEntry ) throws ModelWorkspaceException {
    	Properties result = new Properties();
        IPath modelPath = modelEntry.getName();

        IResource resource = WorkspaceResourceFinderUtil.findIResourceByPath(modelPath);

        if ((modelEntry.getType() == ModelType.PHYSICAL_LITERAL) && (resource != null)) {

			// TODO: Find Model's JDBC PRoperties here!!!!
			JdbcSource jdbcSource = JdbcSourceUtils.findJdbcSource(resource);
			if (jdbcSource != null) {

				if ( jdbcSource.getDriverClass() != null ) {
					result.put(DataSourceConnectionConstants.DRIVER_CLASS, jdbcSource.getDriverClass());
				}
				
				if ( jdbcSource.getUrl() != null ) {
					result.put(DataSourceConnectionConstants.URL, jdbcSource.getUrl());
				}
				
				if (jdbcSource.getUsername() != null) {
					result.put(DataSourceConnectionConstants.USERNAME, jdbcSource.getUsername());
				}
				
				if (jdbcSource.getPassword() != null) {
					result.put(DataSourceConnectionConstants.PASSWORD, jdbcSource.getPassword());
				}
			}
        }

        return result;
    }
    
    /**
     * Get a <code>Map</code> of property name to values for JDBC connection properties stored on a model reference on the vdb
     * manifest model.
     * 
     * @param theModelRef the model reference whose connection properties are being requested
     * @return a map of JDBC connection properties (never <code>null</code> but maybe empty)
     * @throws ModelWorkspaceException 
     * @since 5.0
     */
    public static Properties getModelJdbcProperties( ModelResource resource ) throws ModelWorkspaceException {
    	Properties result = new Properties();

        if ( ModelUtil.isPhysical(resource.getEmfResource()) && (resource != null)) {

			// TODO: Find Model's JDBC PRoperties here!!!!
			JdbcSource jdbcSource = JdbcSourceUtils.findJdbcSource(resource.getCorrespondingResource());
			if (jdbcSource != null) {

				if ( jdbcSource.getDriverClass() != null ) {
					result.put(DataSourceConnectionConstants.DRIVER_CLASS, jdbcSource.getDriverClass());
				}
				
				if ( jdbcSource.getUrl() != null ) {
					result.put(DataSourceConnectionConstants.URL, jdbcSource.getUrl());
				}
				
				if (jdbcSource.getUsername() != null) {
					result.put(DataSourceConnectionConstants.USERNAME, jdbcSource.getUsername());
				}
				
				if (jdbcSource.getPassword() != null) {
					result.put(DataSourceConnectionConstants.PASSWORD, jdbcSource.getPassword());
				}
			}
        }

        return result;
    }

    /**
     * Creates a unique JNDI name for a <code>Vdb</code>'s Source model based on a <code>VdbModelEntry</code>
     * 
     * @param vdbModelEntry
     * @return the JNDI name
     */
    public static String generateUniqueConnectionJndiName(VdbModelEntry vdbModelEntry) {
    	return generateUniqueConnectionJndiName(vdbModelEntry.getDataSource(), vdbModelEntry.getName());
    }
    
    /**
     * Creates a unique JNDI name for a <code>Vdb</code>'s Source model based on a <code>ModelResource</code>
     * 
     * @param modelResource
     * @return the JNDI name
     */
    public static String generateUniqueConnectionJndiName(ModelResource modelResource) {
    	CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
    	return generateUniqueConnectionJndiName(modelResource.getItemName(), modelResource.getPath());
    }
    
    public static String generateUniqueConnectionJndiName(String name, IPath path) {
        final StringBuilder builder = new StringBuilder(DqpPlugin.workspaceUuid.toString());
        for (final String segment : path.removeLastSegments(1).segments())
            builder.append('.').append(segment);
        
        return builder.append(name).toString();
    }
    
    /**
     * Given a set of matchable strings and a set of data source type strings, find a suitable match.
     * 
     * If a match isn't found, then <unknown> string is returned.
     * 
     * @param matchableStrings
     * @param dataSourceTypeNames
     * @return
     */
    public static String findMatchingDataSourceTypeName(Collection<String> matchableStrings, Set<String> dataSourceTypeNames) {
    	for( String dsTypeName : dataSourceTypeNames ) {
    		for( String keyword : matchableStrings) {
	    		if( keyword.toUpperCase().contains(dsTypeName.toUpperCase())) {
	    			return dsTypeName;
	    		}
    		}
    	}
    	
    	return DataSourceConnectionConstants.UNKNOWN;
    }
    
    public static String findMatchingDefaultTranslatorName(Collection<String> matchableStrings, Collection<TeiidTranslator> translators) {
    	for( TeiidTranslator translator : translators ) {
    		for( String keyword : matchableStrings) {
	    		if( keyword.toUpperCase().contains(translator.getName().toUpperCase())) {
	    			return translator.getName();
	    		}
    		}
    	}
    	
    	return DataSourceConnectionConstants.UNKNOWN;
    }
}
