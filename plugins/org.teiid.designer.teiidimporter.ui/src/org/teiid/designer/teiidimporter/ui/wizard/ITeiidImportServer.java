/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui.wizard;

import java.io.File;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 *  Interface for TeiidImporter.  Essentially a partial implementation of the ITeiidServer
 */
public interface ITeiidImportServer {

    /**
     * Get the display name for the teiid instance
     * @return the server display name
     * @throws Exception
     */
    String getDisplayName() throws Exception;
    
    /**
     * Removes the data source from the teiid instance (if exists)
     * 
     * @param jndiName the source jndi name
     * @throws Exception if failer in deleting data source on server
     */
     void deleteDataSource(String jndiName) throws Exception;

     /**
      * Deploys a driver (jar or rar) to the related Teiid instance
      * 
      * @param jarOrRarFile the file to deploy
      * 
      * @throws Exception if deployment fails
      */
     void deployDriver(File jarOrRarFile) throws Exception;

     /**
      * Get Model Schema DDL from the VDB
      * 
      * @param vdbName the name of the VDB
      * @param vdbVersion the VDB version
      * @param modelName the model name
      * @return the Schema DDL for the model
      * @throws Exception if deployment fails
      */
     String getSchema(String vdbName, String vdbVersion, String modelName) throws Exception;

     /**
      * Returns all teiid data source object if any on this server
      * 
      * @return collection of {@link ITeiidDataSource}
      * 
      * @throws Exception
      */
     Collection<ITeiidDataSource> getDataSources() throws Exception;

     /**
     * @return the data source template names
     * @throws Exception
     */
     Set<String> getDataSourceTemplateNames() throws Exception;
    
     /**
      * @param templateName
      * @return the template property definitions
      * @throws Exception
      */
     Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception;

     /**
      * @param sourceName the data source name
      * @return the datasource properties
      * @throws Exception
      */
     Properties getDataSourceProperties(String sourceName) throws Exception;

     /**
      * @param displayName the JNDI display name
      * @param jndiName the JNDI name
      * @param typeName the translator type name
      * @param properties the list of teiid-related connection properties
      * @return true if data source is created. false if it already exists
      * @throws Exception if data source creation fails
      */
     ITeiidDataSource getOrCreateDataSource(String displayName,
                                            String jndiName,
                                            String typeName,
                                            Properties properties) throws Exception;

    /**
     * 
     * @return collection of Teiid translators
     * @throws Exception 
     */
     Collection<ITeiidTranslator> getTranslators() throws Exception;

     /**
      * 
      * @param vdbName
      * @throws Exception
      */
     void undeployDynamicVdb(String vdbName) throws Exception;
     
     /**
      * Get the Teiid Instance Version
      * @return the Teiid Instance version
      * @throws Exception
      */
     ITeiidServerVersion getTeiidServerVersion() throws Exception;

}
