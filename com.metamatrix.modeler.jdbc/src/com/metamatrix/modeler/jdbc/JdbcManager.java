/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.jdbc;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;

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
     * Find all {@link JdbcDriver} objects that satisfy the needs of the supplied source.
     * <p>
     * The {@link JdbcDriver} instances are considered a match if some or all of the following criteria
     * are true:
     * <ol>
     *   <li>The source's {@link JdbcSource#getDriverName() driver name} matches the {@link JdbcDriver#getName() driver name}</li>
     *   <li>The source's {@link JdbcSource#getDriverClass() driver class} matches the {@link JdbcDriver#getPreferredDriverClassName() preferred driver class}</li>
     *   <li>The source's {@link JdbcSource#getDriverClass() driver class} matches one of the {@link JdbcDriver#getAvailableDriverClassNames() available driver classes}</li>
     *   <li>The source's {@link JdbcSource#getJdbcDriver() driver reference} matches the {@link JdbcDriver}</li>
     * </ol>
     * </p>
     * <p>
     * The {@link JdbcDriver} instances are ordered in the following order:
     * <ol>
     *   <li>First those drivers where conditions #1, #2, #3, and #4 are true</li>
     *   <li>Then those drivers where conditions #1, #2, and #3 are true; next</li>
     *   <li>Then those drivers where conditions #1 and #3 are true; next</li>
     *   <li>Then those drivers where conditions #2 and #3 are true</li>
     *   <li>Then those drivers where condition #3 is true</li>
     *   <li>Then those drivers where condition #1 is true</li>
     *   <li>Then those drivers where condition #4 is true</li>
     * </ol>
     * </p>
     * @param source the source
     * @return the list of JdbcDriver instances, if any, that satisfy the needs of the source; never null.
     * The {@link JdbcDriver} that best matches the information specified by the source
     * will be first, while the {@link JdbcDriver} that matches the information the least
     * will be last.
     */
    public JdbcDriver[] findDrivers( JdbcSource source );

    /**
     * Find the {@link JdbcDriver} that satisfy most exactly matches the {@link JdbcSource#getName() name}
     * and {@link JdbcSource#getDriverClass() driver class} of the supplied source.
     * This method may choose a different one than what is referenced by the source.
     * <p>
     * This is a convenience method that simply returns the first {@link JdbcDriver} from
     * the result of {@link #findDrivers(JdbcSource)}.
     * </p>
     * @param source the source
     * @return the JdbcDriver instances, if any, that best satisfies the needs of the source; never null
     */
    public JdbcDriver findBestDriver( JdbcSource source );
    
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
     * Compute the {@link java.sql.Driver}, {@link javax.sql.DataSource) and {@link javax.sql.XADataSource}
     * implementations in the supplied class loader and (re)set the 
     * {@link JdbcDriver#getAvailableDriverClassNames() available driver class names}
     * in the supplied <code>driver</code>.
     * @param driver the JdbcDriver; may not be null
     * @param driverOnly true if only {@link java.sql.Driver} implementations should be found, or false
     * if implementations of {@link java.sql.Driver}, {@link javax.sql.DataSource) and 
     * {@link javax.sql.XADataSource} should be found
     * @return a status describing the activity, where {@link IStatus#isOK()} returns true if available classes
     * were found with no problems
     */
    public IStatus computeAvailableDriverClasses( JdbcDriver driver, boolean driverOnly ) throws JdbcException;

    /**
     * Create a connection to the data source described by the supplied {@link JdbcSource}.
     * @param jdbcSource the object that describes the data source; may not be null
     * @param jdbcDriver the driver object that should be used; if null, then the 
     * {@link #findBestDriver(JdbcSource) best available driver} is found and used
     * @param password optional password that would be used to create a connection; null only if no password
     * should be supplied
     * @param monitor the monitor that is used to cancel the operation; may be null
     * @return a connection to the data source described by <code>jdbcSource</code>
     * @throws SQLException if there is an error
     * @throws JdbcException if the source is not valid
     * @throws InterruptedException if the monitor cancelled the operation
     */
    public Connection createConnection( JdbcSource jdbcSource, JdbcDriver driver, String password, IProgressMonitor monitor ) throws JdbcException, SQLException;

    /**
     * Create a connection to the data source described by the supplied {@link JdbcSource}.
     * @param jdbcSource the object that describes the data source; may not be null
     * @param jdbcDriver the driver object that should be used; if null, then the 
     * {@link #findBestDriver(JdbcSource) best available driver} is found and used
     * @param password optional password that would be used to create a connection; null only if no password
     * should be supplied
     * @return a connection to the data source described by <code>jdbcSource</code>
     * @throws SQLException if there is an error
     * @throws JdbcException if the source is not valid
     */
    public Connection createConnection( JdbcSource jdbcSource, JdbcDriver driver, String password ) throws JdbcException, SQLException;

    /**
     * Obtain the list of descriptors for the properties used to connect to the supplied source. 
     * @param jdbcSource the object that describes the data source; may not be null
     * @return the array of {@link JdbcDriverProperty} instances; may be empty if none could be obtained
     * from the driver, but never null
     * @throws JdbcException if there is an error obtaining the properties
     * @since 4.2
     */
    public JdbcDriverProperty[] getPropertyDescriptions( final JdbcSource jdbcSource ) throws JdbcException;
    
    /**
     * Save the JdbcSources managed by this instance to the specifed OutputStream. 
     * @param fileOutputStream
     * @throws IOException
     * @since 4.2
     */
    public void saveConnections(OutputStream fileOutputStream) throws IOException;
    
    /**
     * Load the JdbcSource configurations from the specified external Resource into this instance. 
     * @param sourceConnectionResource
     * @return a List of the newly loaded JdbcSource instances
     * @throws IOException if there is a file error
     * @throws JdbcException if the Resource to be loaded is not valid.
     * @since 4.2
     */
    public List loadConnections(Resource sourceConnectionResource) throws IOException, JdbcException;
}
