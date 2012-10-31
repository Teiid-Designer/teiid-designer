/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.teiid.adminapi.VDB;
import org.teiid.designer.runtime.connection.IPasswordProvider;
import org.teiid.designer.vdb.Vdb;

/**
 * @since 8.0
 */
public interface IExecutionAdmin {

    enum PingType {
        ADMIN, JDBC;
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
     VDB deployVdb(IFile vdbFile) throws Exception;

    /**
     * Deploys the input Vdb archive file to the related Teiid server
     * 
     * @param vdb the local Vdb to deploy
     * 
     * @throws Exception if deployment fails
     */
     VDB deployVdb(Vdb vdb) throws Exception;

    /**
     * Returns a teiid data source object if it exists in this server
     * 
     * @param name the data source name
     * @return the teiid data source object (can be <code>null</code>)
     * @throws Exception 
     */
     TeiidDataSource getDataSource(String name) throws Exception;

     /**
      * Returns all teiid data source object if any on this server
      * 
      * @return collection of {@link TeiidDataSource}
      * 
      * @throws Exception
      */
     Collection<TeiidDataSource> getDataSources() throws Exception;

     /**
      * Get the type names of the data sources
      * 
      * @return set of names
     * @throws Exception 
      */
     Set<String> getDataSourceTypeNames() throws Exception;

    /**
     * @param model the model containing source info
     * @param jndiName the JNDI name
     * @param previewVdb the model's preview vdb
     * @param passwordProvider the password provider
     * @return Teiid data source object
     * @throws Exception if data source creation fails
     */
     TeiidDataSource getOrCreateDataSource(IFile model,
                                                          String jndiName,
                                                          boolean previewVdb,
                                                          IPasswordProvider passwordProvider) throws Exception;

    /**
     * @param displayName the JNDI display name
     * @param jndiName the JNDI name
     * @param typeName the translator type name
     * @param properties the list of teiid-related connection properties
     * @return true if data source is created. false if it already exists
     * @throws Exception if data source creation fails
     */
     TeiidDataSource getOrCreateDataSource(String displayName,
                                                          String jndiName,
                                                          String typeName,
                                                          Properties properties) throws Exception;

    /**
     * @param name the translator name (never <code>null</code> or empty)
     * @return a TeiidTranslator
     * @throws Exception 
     * @since 7.0
     */
     TeiidTranslator getTranslator(String name) throws Exception;

    /**
     * 
     * @return collection of Teiid translators
     * @throws Exception 
     */
     Collection<TeiidTranslator> getTranslators() throws Exception;

    /**
     * @return an unmodifiable set of VDBs deployed on the server
     * @throws Exception 
     */
     Set<TeiidVdb> getVdbs() throws Exception;

     /**
      * @param name 
      * @return the {@link TeiidVdb} with the given name
      * @throws Exception 
      */
     VDB getVdb(String name) throws Exception;
     
     /**
      * 
      * @param vdbName
      * @throws Exception
      */
     void undeployVdb(String vdbName) throws Exception;
     
    /**
     * 
     * @param vdb 
     * @throws Exception if undeploying vdb fails
     */
     void undeployVdb(VDB vdb) throws Exception;

    /**
     * Ping the admin client to determine whether if is still connected
     * @param pingType 
     * 
     * @return
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
}
