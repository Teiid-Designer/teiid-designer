/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;

/**
 * JdbcManager
 */
public interface JdbcManager {
    
    /**
     * Return the logical name assigned to this manager.
     * @return the name of the manager.
     */
    public String getName();
    
    /**
     * Return whether there are unsaved changes in the {@link #getJdbcDrivers() drivers} or 
     * {@link #getJdbcSources() sources}.
     * @return true if there are changes and {@link #saveChanges(IProgressMonitor)} should be called
     * to persist the changes, or false if there are no changes.
     */
    public boolean hasChanges();
    
    /**
     * Save any changes that have been made.
     * @param monitor
     */
    public void saveChanges( IProgressMonitor monitor ) throws IOException;
    
    /**
     * Reload the saved {@link #getJdbcDrivers() drivers} and 
     * {@link #getJdbcSources() sources}, losing any changes that have been made so far.  All existing
     * references will be invalid and will no longer reference the persistent objects.
     * @param monitor
     * @throws JdbcException if there is an error reloading the {@link #getJdbcDrivers() drivers} and 
     * {@link #getJdbcSources() sources}
     */
    public void reload( IProgressMonitor monitor ) throws JdbcException;
    
    /**
     * Return the factory that can be used to create new objects for this manager.  The created 
     * {@link JdbcDriver} objects must be added to {@link #getJdbcDrivers() this manager}.
     * @return the factory; never null
     */
    public JdbcFactory getFactory();
    
    /**
     * Return the {@link JdbcDriver} instances that are known to and managed by this manager.
     * This list is directly modifiable.
     * @return the list of {@link JdbcDriver} instances; never null
     */
    public List getJdbcDrivers();
    
    /**
     * Return the {@link JdbcSource} instances that are known to and managed by this manager.
     * Note that the manager does <i>not</i> know about {@link JdbcSource} objects that are stored within
     * {@link com.metamatrix.modeler.core.workspace.ModelResource ModelResources}.
     * This list is directly modifiable.
     * @return the list of {@link JdbcSource} instances; never null
     */
    public List getJdbcSources();
    
    /**
     * Find the {@link JdbcDriver} instances that have the supplied name.
     * @param JdbcDriver the name of the driver
     * @return all {@link JdbcDriver} instances that have a {@link JdbcDriver#getName() name} that matches
     * the supplied name.
     */
    public JdbcDriver[] findDrivers( final String driverName );
    
    /**
     * Find the {@link JdbcSource} instances that have the supplied name.
     * @param sourceName the name of the source
     * @return all {@link JdbcSource} instances that have a {@link JdbcSource#getName() name} that matches
     * the supplied name.
     */
    public JdbcSource[] findSources( final String sourceName );
    
    /**
     * Return whether the supplied driver is considered valid.
     * <p>
     * A {@link JdbcSource} is considered valid if all of the following conditions are true:
     * <ul>
     *   <li>There is a {@link JdbcSource#getName() name} that is not zero-length.</li>
     *   <li>There is a valid {@link JdbcSource#getDriverClass() driver class} specified (this does not
     *       check the classpath)</li>
     * </ul>
     * </p>
     * @param jdbcSource the JdbcSource
     * @return a status describing if valid (e.g., {@link IStatus#isOK()} returns true) or the reason
     * why invalid.
     */
    public IStatus isValid( JdbcSource jdbcSource );
    
    /**
     * Return whether the supplied driver is considered valid.
     * <p>
     * A {@link JdbcDriver} is considered valid if all of the following conditions are true:
     * <ul>
     *   <li>There is a {@link JdbcDriver#getName() name} that is not zero-length.</li>
     *   <li>There is at least one {@link JdbcDriver#getAvailableDriverClassNames() driver class}.</li>
     *   <li>There is a {@link JdbcDriver#getPreferredDriverClassNames() preferred driver class}.</li>
     *   <li>There {@link JdbcDriver#getPreferredDriverClassNames() preferred driver class} is one of the
     *       {@link JdbcDriver#getAvailableDriverClassNames() available driver classes}</li>
     * </ul>
     * </p>
     * <p>
     * Additionally, a warning is included if any of the following are true:
     * <ul>
     *   <li>There are no {@link JdbcDriver#getJarFileUris() JAR file URIs}.</li>
     * </ul>
     * </p>
     * @param driver the driver
     * @return a status describing if valid (e.g., {@link IStatus#isOK()} returns true) or the reason
     * why invalid.
     */
    public IStatus isValid( JdbcDriver driver );
    
    /**
     * Create a connection to the data source described by the supplied {@link JdbcSource}.
     * @param jdbcSource the object that describes the data source; may not be null
     * {@link #findBestDriver(JdbcSource) best available driver} is found and used
     * @param password optional password that would be used to create a connection; null only if no password
     * should be supplied
     * @param monitor the monitor that is used to cancel the operation; may be null
     * @return a connection to the data source described by <code>jdbcSource</code>
     * @throws SQLException if there is an error
     * @throws JdbcException if the source is not valid
     * @throws InterruptedException if the monitor cancelled the operation
     */
    public Connection createConnection( JdbcSource jdbcSource, String password, IProgressMonitor monitor ) throws JdbcException, SQLException;

    /**
     * Create a connection to the data source described by the supplied {@link JdbcSource}.
     * @param jdbcSource the object that describes the data source; may not be null
     * {@link #findBestDriver(JdbcSource) best available driver} is found and used
     * @param password optional password that would be used to create a connection; null only if no password
     * should be supplied
     * @return a connection to the data source described by <code>jdbcSource</code>
     * @throws SQLException if there is an error
     * @throws JdbcException if the source is not valid
     * @throws InterruptedException if the monitor cancelled the operation
     */
    
	public Connection createConnection(JdbcSource src, String password) throws JdbcException, SQLException;

	public JdbcSource getJdbcSource(IConnectionProfile profile);
	
	/**
	 * Find a connection profile with given name.
	 * 
	 * @param profileName
	 * @return a conneciton profile
	 */
	public IConnectionProfile getConnectionProfile(String profileName);

}
