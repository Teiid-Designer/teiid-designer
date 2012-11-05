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

    /**
     * @return display name of data source
     */
    String getDisplayName();

    /**
     * @return real name of data source, maybe different from display name
     */
    String getName();

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
