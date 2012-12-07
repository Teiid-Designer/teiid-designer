/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import java.sql.Driver;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;

/**
 * @since 8.0
 */
public interface IExecutionAdmin {

    /**
     * Type of ping to be performed
     */
    enum PingType {
        /**
         * Ping the admin port of the server
         */
        ADMIN, 
        
        /**
         * Ping the JDBC port of the server
         */
        JDBC;
    }
    
    /**
     * Create a connection
     * 
     * @throws Exception
     */
    void connect() throws Exception;
    
    /**
     * Disconnects the connection
     */
    void disconnect();
    
    /**
     * @param name the name of the data source
     * @return true if data source exists with the provided name. else false.
     * @throws Exception 
     */
     boolean dataSourceExists(String name) throws Exception;

    /**
     * Removes the data source from the teiid server (if exists)
     * 
     * @param jndiName the source jndi name
     * @throws Exception if failer in deleting data source on server
     */
     void deleteDataSource(String jndiName) throws Exception;

    /**
     * Deploys the VDB (IFile) to the related Teiid server
     * 
     * @param vdbFile the vdb file
     * 
     * @throws Exception if deployment fails
     */
     void deployVdb(IFile vdbFile) throws Exception;

    /**
     * Returns a teiid data source object if it exists in this server
     * 
     * @param name the data source name
     * @return the teiid data source object (can be <code>null</code>)
     * @throws Exception 
     */
     ITeiidDataSource getDataSource(String name) throws Exception;

     /**
      * Returns all teiid data source object if any on this server
      * 
      * @return collection of {@link ITeiidDataSource}
      * 
      * @throws Exception
      */
     Collection<ITeiidDataSource> getDataSources() throws Exception;

     /**
      * Get the type names of the data sources
      * 
      * @return set of names
     * @throws Exception 
      */
     Set<String> getDataSourceTypeNames() throws Exception;

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
     * @param name the translator name (never <code>null</code> or empty)
     * @return a TeiidTranslator
     * @throws Exception 
     * @since 7.0
     */
     ITeiidTranslator getTranslator(String name) throws Exception;

    /**
     * 
     * @return collection of Teiid translators
     * @throws Exception 
     */
     Collection<ITeiidTranslator> getTranslators() throws Exception;

    /**
     * @return an unmodifiable collection of VDBs deployed on the server
     * @throws Exception 
     */
     Collection<ITeiidVdb> getVdbs() throws Exception;

     /**
      * @param name 
      * @return the {@link ITeiidVdb} with the given name
      * @throws Exception 
      */
     ITeiidVdb getVdb(String name) throws Exception;
     
     /**
      * @param name 
      * @return whether server contains a vdb with the given name
      * @throws Exception 
      */
     boolean hasVdb( String name ) throws Exception;
     
     /**
      * @param vdbName
      *  
      * @return <code>true</code> if the vdb is active
      * @throws Exception 
      */
     boolean isVdbActive(String vdbName) throws Exception;
     
     /**
      * @param vdbName
      *  
      * @return <code>true</code> if the vdb is loading
      * @throws Exception
      */
     boolean isVdbLoading(String vdbName) throws Exception;
     
     /**
      * @param vdbName
      *  
      * @return <code>true</code> if the vdb failed
      * @throws Exception
      */
     boolean hasVdbFailed(String vdbName) throws Exception;
     
     /**
      * @param vdbName 
      * 
      * @return <code>true</code> if the vdb was removed
      * @throws Exception
      */
     boolean wasVdbRemoved(String vdbName) throws Exception;
     
     /**
      * @param vdbName
      * 
      * @return any validity errors from the vdb when it was deployed
      * @throws Exception
      */
     List<String> retrieveVdbValidityErrors(String vdbName) throws Exception;
     
     /**
      * 
      * @param vdbName
      * @throws Exception
      */
     void undeployVdb(String vdbName) throws Exception;
     
    /**
     * Ping the admin client to determine whether if is still connected
     * @param pingType 
     * 
     * @return {@link IStatus} describing state of ping
     * 
     * @throws Exception 
     */
     IStatus ping(PingType pingType) throws Exception;
     
     /**
      * Get the location of the the admin driver class. Implementations have historically
      * derived this from the Admin class in the form:
      * 
      * Admin.class.getProtectionDomain().getCodeSource().getLocation().getFile();
      * 
      * @return {@link String} representation of location
      * 
      * @throws Exception 
      */
     String getAdminDriverPath() throws Exception;
     
     /**
      * Get the teiid server driver for the given class
      * 
     * @param driverClass 
      * 
      * @return instance of {@link Driver}
      * 
     * @throws Exception 
      */
     Driver getTeiidDriver(String driverClass) throws Exception;
}
