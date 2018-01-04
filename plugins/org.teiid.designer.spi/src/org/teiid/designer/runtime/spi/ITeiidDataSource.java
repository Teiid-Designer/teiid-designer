/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import java.util.Properties;

/**
 * @since 8.0
 */
public interface ITeiidDataSource {
	
	public interface ERROR_CODES {
		// Data Source issues
		int JDBC_DRIVER_SOURCE_NOT_FOUND = 101; // JDBC Source for Driver class {0} was not found on server {1}
		int DATA_SOURCE_TYPE_DOES_NOT_EXIST_ON_SERVER = 102; // Data Source Type {0} does not exist on server {1}
		int DATA_SOURCE_COULD_NOT_BE_CREATED = 103; // errorCreatingDataSource = Data Source {0} could not be created for type {1}
		int NO_CONNECTION_PROVIDER = 104; //
		int NO_CONNECTION_PROFILE_DEFINED_IN_MODEL = 105; // 
		int NO_TEIID_RELATED_PROPERTIES_IN_PROFILE = 106; // 
		int COULD_NOT_GET_OR_CREATE_DATASOURCE = 107; // 
		int DATASOURCE_REQUIRED_PASSWORD_NOT_DEFINED = 108; // 
	}

    /**
     * @return display name of data source
     */
    String getDisplayName();

    /**
     * @return real name of data source, maybe different from display name
     */
    String getName();
    
    /**
     * @return pool name of data source, maybe different from display name
     */
    String getPoolName();

    /**
     * Returns the data source type name
     * 
     * @return the type
     */
    String getType();

    /**
     * @return properties of data source
     */
    Properties getProperties();

    /**
     * @param name
     * 
     * @return value of named property
     */
    String getPropertyValue(String name);

    /**
     * Set the profile name
     * 
     * @param name
     */
    void setProfileName(String name);

    /**
     * @return profile name
     */
    String getProfileName();

    /**
     * @return isPreview
     */
    boolean isPreview();

    /**
     * @param isPreview Sets isPreview to the specified value.
     */
    void setPreview(boolean isPreview);

}
