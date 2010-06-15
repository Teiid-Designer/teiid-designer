/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.Path;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.vdb.connections.VdbSourceConnection;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DataSourceConnectionConstants;

/**
 * 
 */
public class ModelConnectionMapper {

	public static final String JDBC_DS_TYPE = "connector-jdbc"; //$NON-NLS-1$
    String modelName;
    Properties properties;
    
    private ModelResource modelResource;
    
    private ConnectionInfoHelper connectionInfoHelper;
    
    /**
     * ModelConnectionFactoryMapper
     * 
     * @param modelName
     * @param properties
     */
    public ModelConnectionMapper( String modelName,
                                         Properties properties ) {
    	this(new ConnectionInfoHelper());
    	
        CoreArgCheck.isNotEmpty(modelName);
        CoreArgCheck.isNotEmpty(properties);
        this.modelName = modelName;
        this.properties = (Properties)properties.clone();
    }
    
    public ModelConnectionMapper( String modelName,
            Properties properties, ConnectionInfoHelper connectionInfoHelper ) {
		this(connectionInfoHelper);
		
		CoreArgCheck.isNotEmpty(modelName);
		CoreArgCheck.isNotEmpty(properties);
		this.modelName = modelName;
		this.properties = (Properties)properties.clone();
	}
    
    public ModelConnectionMapper( ModelResource modelResource) {
    	this(new ConnectionInfoHelper());
    	
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		this.modelResource = modelResource;
    }
    
    
    public ModelConnectionMapper( ModelResource modelResource, ConnectionInfoHelper connectionInfoHelper) {
    	this(connectionInfoHelper);
    	
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		this.modelResource = modelResource;
    }
    
    private ModelConnectionMapper( ConnectionInfoHelper connectionInfoHelper) {
		CoreArgCheck.isNotNull(connectionInfoHelper, "connectionInfoHelper"); //$NON-NLS-1$
		this.connectionInfoHelper = connectionInfoHelper;
    }

    /**
     * @return modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }
    
    public VdbSourceConnection getVdbSourceConnection(ExecutionAdmin executionAdmin, String workspaceUuid) throws Exception {
    	if( executionAdmin == null ) {
    		return null;
    	}
    	
    	VdbSourceConnection sourceConnection = null;
    	String translatorName = null;
    	String jndiName = null;
    	
    	
    	if( this.modelResource != null ) {
    		Properties jdbcSourceProps = this.connectionInfoHelper.getModelJdbcConnectionProperties(modelResource); 
	    	jndiName = this.connectionInfoHelper.generateUniqueConnectionJndiName(modelResource, workspaceUuid);
	    	
	    	Collection<String> matchableStrings = new ArrayList<String>();
	    	matchableStrings.add(jdbcSourceProps.getProperty(DataSourceConnectionConstants.DRIVER_CLASS));
	    	matchableStrings.add(jdbcSourceProps.getProperty(DataSourceConnectionConstants.URL));
	    	
	    	// Insure this name exists as data source on server
	    	// TODO:  Need to match up with Teiid DS TYPE Names?
	    	String dsTypeName = JDBC_DS_TYPE; //ModelerDqpUtils.findMatchingDataSourceTypeName(matchableStrings, defaultAdmin.getDataSourceTypeNames());
	    	executionAdmin.getOrCreateDataSource(jndiName, dsTypeName, jdbcSourceProps);
	    	
	    	// Select a translator type;
	    	translatorName = this.connectionInfoHelper.findMatchingDefaultTranslatorName(matchableStrings, executionAdmin.getTranslators());
	    	
	    	sourceConnection = new VdbSourceConnection(modelName, translatorName, jndiName);
    	} else {
    		// TODO: Figure out how to create the source connection object
	    	
	    	translatorName = properties.getProperty(ConnectionInfoHelper.TRANSLATOR_NAMESPACE + ConnectionInfoHelper.TRANSLATOR_NAME);
	    	if( translatorName == null ) {
		    	Collection<String> matchableStrings = new ArrayList<String>();
		    	matchableStrings.add(properties.getProperty(ConnectionInfoHelper.DRIVER_CLASS_KEY));
		    	matchableStrings.add(properties.getProperty(ConnectionInfoHelper.URL_KEY));
		    	translatorName = this.connectionInfoHelper.findMatchingDefaultTranslatorName(matchableStrings, executionAdmin.getTranslators());
	    	}
	    	
	    	jndiName = this.connectionInfoHelper.generateUniqueConnectionJndiName(modelName, new Path(StringUtilities.EMPTY_STRING), workspaceUuid);
	    	
	    	Properties dsProps = new Properties();
	    	boolean enoughProps = true;
        	
			if ( properties.get(ConnectionInfoHelper.DRIVER_CLASS_KEY) != null ) {
				dsProps.put(DataSourceConnectionConstants.DRIVER_CLASS, properties.get(ConnectionInfoHelper.DRIVER_CLASS_KEY));
			} else {
				enoughProps = false;
			}
			
			if ( properties.get(ConnectionInfoHelper.URL_KEY) != null ) {
				dsProps.put(DataSourceConnectionConstants.URL, properties.get(ConnectionInfoHelper.URL_KEY));
			} else {
				enoughProps = false;
			}
			
			if (properties.get(ConnectionInfoHelper.USERNAME_KEY) != null) {
				dsProps.put(DataSourceConnectionConstants.USERNAME, properties.get(ConnectionInfoHelper.USERNAME_KEY));
			} else {
				enoughProps = false;
			}
			
			if (properties.get(ConnectionInfoHelper.PASSWORD_KEY) != null) {
				dsProps.put(DataSourceConnectionConstants.PASSWORD, properties.get(ConnectionInfoHelper.PASSWORD_KEY));
			}
			
			if( enoughProps ) {
		    	// Insure this name exists as data source on server
		    	String dsTypeName = JDBC_DS_TYPE; //ModelerDqpUtils.findMatchingDataSourceTypeName(matchableStrings, defaultAdmin.getDataSourceTypeNames());
		    	executionAdmin.getOrCreateDataSource(jndiName, dsTypeName, dsProps);
			}
	    	

    	}
    	sourceConnection = new VdbSourceConnection(modelName, translatorName, jndiName);
    	return sourceConnection;
    }
    

}
