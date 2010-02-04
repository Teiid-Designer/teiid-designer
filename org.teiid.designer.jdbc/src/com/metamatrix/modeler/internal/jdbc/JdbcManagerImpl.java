/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.classloader.URLClassLoaderRegistry;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.ClassLoaderUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcDriverContainer;
import com.metamatrix.modeler.jdbc.JdbcDriverProperty;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcFactory;
import com.metamatrix.modeler.jdbc.JdbcManager;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.JdbcSourceContainer;
import com.metamatrix.modeler.jdbc.JdbcSourceProperty;
import com.metamatrix.modeler.jdbc.custom.ExcelConnectionHandler;

/**
 * JdbcManagerImpl
 */
public class JdbcManagerImpl implements JdbcManager {

    // ===========================================================================================================================
    // Constants

    public static final int MISSING_DRIVER_CLASS = 1001;
    public static final int VALID_SOURCE = 1002;
    public static final int NAME_NOT_SPECIFIED = 1003;
    public static final int NAME_MUST_BEGIN_WITH_LETTER_OR_NUMBER = 1004;
    public static final int NO_AVAILABLE_DRIVER_CLASS_NAMES = 1005;
    public static final int PREFERRED_NOT_SPECIFIED = 1006;
    public static final int PREFERRED_NOT_IN_AVAILABLE = 1007;
    public static final int NO_JARS_SPECIFIED = 1008;
    public static final int VALID_DRIVER = 1009;
    public static final int ILLEGAL_CHAR_AT_START_OF_CLASS_NAME = 1010;
    public static final int ILLEGAL_CHAR_IN_CLASS_NAME = 1011;
    public static final int JAR_FILE_URI_NOT_SPECIFIED = 1012;
    public static final int JAR_FILE_DOESNT_EXIST = 1013;
    public static final int MALFORMED_URL = 1014;
    public static final int PROPERTY_NAME_MISSING = 1015;
    public static final int PROPERTY_VALUE_MISSING = 1016;
    public static final int ERROR_BUILDING_CLASSLOADER = 1017;
    public static final int UNABLE_TO_FIND_DRIVER = 1018;
    public static final int ERROR_CHECKING_DRIVER_CLASS = 1019;
    public static final int URL_NOT_SPECIFIED = 1020;
    public static final int URL_MUST_START_WITH_JDBC = 1021;
    public static final int ERROR_FINDING_DRIVER_CLASS = 1022;
    public static final int MALFORMED_URL_SYNTAX = 1023;

    public static final int AVAILABLE_CLASSES_COMPLETE = 1100;
    public static final int AVAILABLE_CLASSES_WITH_WARNINGS = 1102;
    public static final int AVAILABLE_CLASSES_WITH_ERRORS = 1103;
    public static final int AVAILABLE_CLASSES_WITH_WARNINGS_AND_ERRORS = 1104;

    private static final String PID = JdbcPlugin.PLUGIN_ID;

    /**
     * @since 5.0
     */
    public static final String JDBC_MODEL = "jdbcModel" + ModelerCore.MODEL_FILE_EXTENSION; //$NON-NLS-1$

    // ===========================================================================================================================
    // Static Variables

    private static JdbcManager shared;

    // ===========================================================================================================================
    // Static Model Methods

    /**
     * @param name
     * @param folder
     * @param container
     * @return The shared instance of this class.
     * @throws JdbcException
     * @since 5.0
     */
    public static JdbcManager create( final String name,
                                      final File folder,
                                      final Container container ) throws JdbcException {
        if (JdbcManagerImpl.shared == null) {
            // Create and start JDBC manager (which loads JDBC driver model)
            final URI uri = URI.createFileURI(new File(folder, JDBC_MODEL).getAbsolutePath());
            final Resource model = container.getResource(uri, true);
            final JdbcManagerImpl mgr = new JdbcManagerImpl(name, model);
            mgr.start();
            JdbcManagerImpl.shared = mgr;
        }
        return JdbcManagerImpl.shared;
    }

    /**
     * @return The shared instance of this class.
     * @since 5.0
     */
    public static JdbcManager get() {
        return JdbcManagerImpl.shared;
    }

    // ===========================================================================================================================
    // Variables

    private final String name;
    private Resource resource;
    private JdbcDriverContainer drivers;
    private JdbcSourceContainer sources;
    private final Object driversLock = new Object();
    private final Object sourcesLock = new Object();
    private final URLClassLoaderRegistry classLoaderRegistry;

    // ===========================================================================================================================
    // Constructors

    /**
     * Construct an instance of JdbcManagerImpl. This form of the contructor is useful when the resource is already opened and
     * known.
     * 
     * @param name the name of this manager; may not be null or zero-length
     * @param resource the EMF resource that this manager will use
     */
    public JdbcManagerImpl( final String name,
                            final Resource resource ) {
        super();
        ArgCheck.isNotNull(name);
        ArgCheck.isNotZeroLength(name);
        ArgCheck.isNotNull(resource);
        this.name = name;
        this.resource = resource;
        this.classLoaderRegistry = new URLClassLoaderRegistry();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method is not synchronized and is not thread safe.
     */
    public void start() throws JdbcException {
        if (!this.resource.isLoaded()) {
            final URI uri = this.resource.getURI();
            final File f = new File(uri.toFileString());
            if (f.canRead() && f.exists() && f.length() != 0) {
                // Do this only if the file exists and is empty ...
                try {
                    Map options = (this.resource.getResourceSet() != null ? this.resource.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
                    this.resource.load(options);
                } catch (IOException e) {
                    final Object[] params = new Object[] {this.resource.getURI()};
                    final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Error_loading_resource", params); //$NON-NLS-1$
                    throw new JdbcException(e, msg);
                }
            }
        }
    }

    /**
     * This method is not synchronized and is not thread safe.
     */
    public void shutdown() {
        if (this.resource != null) {
            this.resource.unload();
        }
        sources = null; // no need to synchronize since nulling reference is atomic
        drivers = null; // no need to synchronize since nulling reference is atomic
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#hasChanges()
     */
    public boolean hasChanges() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#saveChanges(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void saveChanges( IProgressMonitor monitor ) throws IOException {
        // If there are no changes, then return
        if (!hasChanges()) {
            return;
        }
        Assertion.isNotNull(this.resource);
        this.resource.save(new HashMap());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#reload(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void reload( IProgressMonitor monitor ) throws JdbcException {
        shutdown();
        start();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#getJdbcDrivers()
     */
    public List getJdbcDrivers() {
        if (drivers == null) {
            synchronized (driversLock) {
                if (drivers == null) {
                    // Iterate over the roots to find the FIRST JdbcDriverContainer
                    final List roots = getResource().getContents();
                    final Iterator iter = roots.iterator();
                    while (iter.hasNext()) {
                        final EObject root = (EObject)iter.next();
                        if (root instanceof JdbcDriverContainer) {
                            drivers = (JdbcDriverContainer)root;
                            break;
                        }
                    }

                    // If there still isn't one, then create a container ...
                    if (drivers == null) {
                        drivers = this.getFactory().createJdbcDriverContainer();
                        roots.add(drivers);
                    }
                }
            }
        }
        return drivers.getJdbcDrivers();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#getJdbcSources()
     */
    public List getJdbcSources() {
        if (sources == null) {
            synchronized (sourcesLock) {
                if (sources == null) {
                    // Iterate over the roots to find the FIRST JdbcSourceContainer
                    final List roots = getResource().getContents();
                    final Iterator iter = roots.iterator();
                    while (iter.hasNext()) {
                        final EObject root = (EObject)iter.next();
                        if (root instanceof JdbcSourceContainer) {
                            sources = (JdbcSourceContainer)root;
                            break;
                        }
                    }

                    // If there still isn't one, then create a container ...
                    if (sources == null) {
                        sources = this.getFactory().createJdbcSourceContainer();
                        roots.add(sources);
                    }
                }
            }
        }
        return sources.getJdbcSources();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#getFactory()
     */
    public JdbcFactory getFactory() {
        return JdbcFactory.eINSTANCE;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#findBestDriver(com.metamatrix.modeler.jdbc.JdbcSource)
     */
    public JdbcDriver findBestDriver( final JdbcSource source ) {
        ArgCheck.isNotNull(source);
        return findBestDriver(source, false);
    }

    public JdbcDriver findBestDriver( final JdbcSource source,
                                      final boolean requireDriverClass ) {
        ArgCheck.isNotNull(source);
        final JdbcDriver[] bestDrivers = findDrivers(source, requireDriverClass);
        return bestDrivers.length != 0 ? bestDrivers[0] : null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#findDrivers(com.metamatrix.modeler.jdbc.JdbcSource)
     */
    public JdbcDriver[] findDrivers( final JdbcSource source ) {
        return findDrivers(source, false);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#findDrivers(com.metamatrix.modeler.jdbc.JdbcSource)
     */
    protected JdbcDriver[] findDrivers( final JdbcSource source,
                                        final boolean requireDriverClass ) {
        ArgCheck.isNotNull(source);
        final String sourceDriverName = source.getDriverName();
        final String sourceDriverClass = source.getDriverClass();
        final JdbcDriver sourceDriver = source.getJdbcDriver();

        final List results = new ArrayList();
        final List allDrivers = new ArrayList(getJdbcDrivers());
        for (int i = 0; i < 6; ++i) {
            final Iterator iter = allDrivers.iterator();
            while (iter.hasNext()) {
                boolean nameMatch = false;
                boolean classMatch = false;
                boolean prefClassMatch = false;
                boolean refMatch = false;

                final JdbcDriver driver = (JdbcDriver)iter.next();
                if (sourceDriverName != null) {
                    // If the name matches, add it ...
                    if (sourceDriverName.equals(driver.getName())) {
                        nameMatch = true;
                    }
                }
                if (sourceDriverClass != null) {
                    // If the preferred class matches
                    if (sourceDriverClass.equals(driver.getPreferredDriverClassName())) {
                        prefClassMatch = true;
                        classMatch = true;
                    }

                    if (!prefClassMatch) {
                        // If the class name matches one in the driver, add it ...
                        final List availableClasses = driver.getAvailableDriverClassNames();
                        final Iterator classIter = availableClasses.iterator();
                        while (classIter.hasNext()) {
                            final String className = (String)classIter.next();
                            if (sourceDriverClass.equals(className)) {
                                classMatch = true;
                                break;
                            }
                        }
                    }
                }
                if (sourceDriver == driver) {
                    refMatch = true;
                }

                // If a class match is required but the preferred class or available class doesn't match
                // any existing, then nothing else to check ...
                if (requireDriverClass && !classMatch && !prefClassMatch) {
                    continue;
                }

                // See if there is a match ...
                if (i == 0 && nameMatch && prefClassMatch && classMatch && refMatch) {
                    if (!results.contains(driver)) {
                        results.add(driver);
                    }
                } else if (i == 1 && nameMatch && prefClassMatch && classMatch) {
                    if (!results.contains(driver)) {
                        results.add(driver);
                    }
                } else if (i == 2 && nameMatch && classMatch) {
                    if (!results.contains(driver)) {
                        results.add(driver);
                    }
                } else if (i == 3 && prefClassMatch) {
                    if (!results.contains(driver)) {
                        results.add(driver);
                    }
                } else if (i == 4 && classMatch) {
                    if (!results.contains(driver)) {
                        results.add(driver);
                    }
                } else if (i == 5 && nameMatch) {
                    if (!results.contains(driver)) {
                        results.add(driver);
                    }
                }
            }

        }

        // If the referenced driver is in the available list, and there is no other better driver
        // then add it to the results ...
        if (sourceDriver != null && results.isEmpty() && allDrivers.contains(sourceDriver)) {
            results.add(sourceDriver);
        }

        return (JdbcDriver[])results.toArray(new JdbcDriver[results.size()]);
    }

    public Resource getResource() {
        return resource;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#getClassLoader(com.metamatrix.modeler.jdbc.JdbcDriver, boolean)
     */
    public URLClassLoader getClassLoader( final JdbcDriver driver ) throws JdbcException {
        ArgCheck.isNotNull(driver);
        // We actually do want to create a class loader for a potentially invalid driver -
        // otherwise, there's no way to get the available driver/datasource classes in the jars
        // and set them on the JdbcDriver object to make it valid!
        //
        // So, we don't want to do the following check:
        // if ( !isValid(driver,false).isOK() ) {
        // return null;
        // }

        // Create the array of String URLs ...
        final List jarFileUriList = driver.getJarFileUris();
        final String[] jarFileUrls = (String[])jarFileUriList.toArray(new String[jarFileUriList.size()]);
        return getClassLoader(jarFileUrls);
    }

    protected URLClassLoader getClassLoader( final String[] urlStrings ) throws JdbcException {
        URLClassLoader loader = null;
        try {
            // Get the class loader of this plugin, and use as the parent class loader ...
            loader = this.classLoaderRegistry.getClassLoader(urlStrings, this.getClass().getClassLoader());
        } catch (MalformedURLException err) {
            final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Unable_to_create_class_loader_for_the_driver")/*,driver)*/; //$NON-NLS-1$
            throw new JdbcException(err, msg);
        }
        return loader;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#computeAvailableDriverClasses(com.metamatrix.modeler.jdbc.JdbcDriver, java.lang.ClassLoader)
     */
    public IStatus computeAvailableDriverClasses( final JdbcDriver driver,
                                                  final boolean driverOnly ) {
        ArgCheck.isNotNull(driver);
        final URLClassLoader loader;
        try {
            loader = getClassLoader(driver);
        } catch (JdbcException e) {
            return new Status(IStatus.OK, PID, ERROR_BUILDING_CLASSLOADER, e.getMessage(), e);
        }
        return computeAvailableDriverClasses(driver, loader, driverOnly);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#computeAvailableDriverClasses(com.metamatrix.modeler.jdbc.JdbcDriver, java.lang.ClassLoader)
     */
    protected IStatus computeAvailableDriverClasses( final JdbcDriver driver,
                                                     final URLClassLoader loader,
                                                     boolean driverOnly ) {
        final List statuses = new ArrayList();
        final ClassLoaderUtil helper = new ClassLoaderUtil(loader);

        // Find the Driver implementation(s)
        final Class[] assignables = driverOnly ? new Class[] {Driver.class} : new Class[] {Driver.class, DataSource.class,
            XADataSource.class};
        final Class[] driverClasses = helper.getAssignablePublicClassesWithNoArgConstructors(assignables);
        if (helper.hasProblems()) {
            statuses.addAll(helper.getProblems());
        }

        // Figure out the IStatus to return ...
        final int numClasses = driverClasses.length;
        boolean updateAvailableClasses = true;
        IStatus resultStatus = null;
        // If there are no statuses
        if (statuses.isEmpty()) {
            // Create one ...
            final Object[] params = new Object[] {new Integer(numClasses)};
            final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Computed_available_classes_with_no_warnings_or_errors", params); //$NON-NLS-1$
            resultStatus = new Status(IStatus.OK, PID, AVAILABLE_CLASSES_COMPLETE, msg, null);
        } else {
            // There were statuses, so determine whether there were warnings and errors ...
            int numErrors = 0;
            int numWarnings = 0;
            final Iterator iter = statuses.iterator();
            while (iter.hasNext()) {
                final IStatus aStatus = (IStatus)iter.next();
                if (aStatus.getSeverity() == IStatus.WARNING) {
                    ++numWarnings;
                } else if (aStatus.getSeverity() == IStatus.ERROR) {
                    ++numErrors;
                }
            }

            // Create the final status ...
            final IStatus[] statusArray = (IStatus[])statuses.toArray(new IStatus[statuses.size()]);
            if (numWarnings != 0 && numErrors == 0) {
                final Object[] params = new Object[] {new Integer(numClasses), new Integer(numWarnings)};
                final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Computed_available_classes_with_warnings", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PID, AVAILABLE_CLASSES_WITH_WARNINGS, statusArray, msg, null);
            } else if (numWarnings == 0 && numErrors != 0) {
                updateAvailableClasses = false;
                final Object[] params = new Object[] {new Integer(numClasses), new Integer(numErrors)};
                final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Computing_available_classes_resulted_in_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PID, AVAILABLE_CLASSES_WITH_ERRORS, statusArray, msg, null);
            } else if (numWarnings != 0 && numErrors != 0) {
                updateAvailableClasses = false;
                final Object[] params = new Object[] {new Integer(numClasses), new Integer(numWarnings), new Integer(numErrors)};
                final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Computing_available_classes_resulted_in_warnings_and_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PID, AVAILABLE_CLASSES_WITH_WARNINGS_AND_ERRORS, statusArray, msg, null);
            } else {
                final Object[] params = new Object[] {new Integer(numClasses)};
                final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Computed_available_classes_with_no_warnings_or_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PID, AVAILABLE_CLASSES_COMPLETE, statusArray, msg, null);
            }
        }

        // Update the driver object
        if (updateAvailableClasses) {
            driver.getAvailableDriverClassNames().clear();
            for (int i = 0; i < driverClasses.length; i++) {
                driver.getAvailableDriverClassNames().add(driverClasses[i].getName());
            }
        }
        return resultStatus;
    }

    private class ConnectionThread extends Thread {
        private final JdbcSource jdbcSource;
        private final JdbcDriver jdbcDriver;
        private final String password;
        private Connection connection;
        private Throwable throwable;

        protected ConnectionThread( final JdbcSource jdbcSource,
                                    final JdbcDriver jdbcDriver,
                                    final String password ) {
            super("JdbcConnectionThread"); //$NON-NLS-1$
            this.jdbcSource = jdbcSource;
            this.jdbcDriver = jdbcDriver;
            this.password = password;
        }

        @Override
        public void run() {
            try {
                this.connection = createConnection(this.jdbcSource, this.jdbcDriver, this.password);
            } catch (Throwable t) {
                this.throwable = t;
            }
        }

        protected Connection getConnection() {
            return this.connection;
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#createConnection(com.metamatrix.modeler.jdbc.JdbcSource)
     */
    public Connection createConnection( final JdbcSource jdbcSource,
                                        final JdbcDriver jdbcDriver,
                                        final String password,
                                        final IProgressMonitor monitor ) throws JdbcException, SQLException {

        // Create a listener thread to poll for cancel, then interrupt the thread on which this method was called ...
        final ConnectionThread connectionThread = new ConnectionThread(jdbcSource, jdbcDriver, password);
        connectionThread.start(); // will connect ...

        // Poll for cancellation ...
        try {
            while ((monitor == null || !monitor.isCanceled()) && connectionThread.isAlive()) {
                Thread.sleep(100);
            }
            if (monitor != null && monitor.isCanceled() && connectionThread.isAlive()) {
                try {
                    connectionThread.interrupt();
                } catch (Throwable t) {
                    // do nothing ...
                }
            }
        } catch (InterruptedException e) {
            // shouldn't really happen!
        } finally {
            // Looks for an exception in the connection thread ...
            final Throwable error = connectionThread.getThrowable();
            if (error != null) {
                if (error instanceof InterruptedException) {
                    return null; // canceled!!!
                }
                if (error instanceof JdbcException) {
                    throw (JdbcException)error;
                }
                if (error instanceof SQLException) {
                    throw (SQLException)error;
                }
                if (error instanceof RuntimeException) {
                    throw (RuntimeException)error;
                }
            }
        }

        // If we've made it this far, then we should have a connection
        return connectionThread.getConnection();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#createConnection(com.metamatrix.modeler.jdbc.JdbcSource)
     */
    public Connection createConnection( final JdbcSource jdbcSource,
                                        final JdbcDriver jdbcDriver,
                                        final String password ) throws JdbcException, SQLException {
        ArgCheck.isNotNull(jdbcSource);
        // By default, use the non-null one passed in
        JdbcDriver theDriver = jdbcDriver;
        if (theDriver == null) {
            // If no driver passed in, find and use the best one ...
            theDriver = findBestDriver(jdbcSource);
        }
        ArgCheck.isNotNull(theDriver);
        final URLClassLoader loader = getClassLoader(theDriver);
        ArgCheck.isNotNull(loader);
        // need to handle excel in a special way
        if (jdbcSource.getDriverName().equalsIgnoreCase("Microsoft Excel")) {//$NON-NLS-1$
            return (Connection)Proxy.newProxyInstance(loader,
                                                      new Class[] {Connection.class},
                                                      new ExcelConnectionHandler(createConnection(jdbcSource, loader, password),
                                                                                 jdbcSource.getUrl()));
        }
        return createConnection(jdbcSource, loader, password);
    }

    protected Connection createConnection( final JdbcSource jdbcSource,
                                           final ClassLoader loader,
                                           final String password ) throws JdbcException, SQLException {
        ArgCheck.isNotNull(jdbcSource);
        ArgCheck.isNotNull(loader);
        Connection result = null;
        final Object driverObject = createDriverObject(jdbcSource, loader, password);
        if (driverObject instanceof DataSource) {
            final DataSource source = (DataSource)driverObject;
            result = source.getConnection();
        } else if (driverObject instanceof Driver) {
            final Driver driver = (Driver)driverObject;
            final String url = jdbcSource.getUrl();
            final Properties props = createProperties(jdbcSource);
            final String username = jdbcSource.getUsername();
            if (username != null && username.trim().length() != 0) {
                props.put("user", username); //$NON-NLS-1$
            }
            if (password != null) {
                props.put("password", password); //$NON-NLS-1$
            }
            result = driver.connect(url, props);
        } else {
            Assertion.assertTrue(false, JdbcPlugin.Util.getString("JdbcManagerImpl.Unexpected_driver_object")); //$NON-NLS-1$
            return null; // never gets here
        }
        // Check that the connection is not null. The Oracle driver returns null if the "jdbc:oracle:thin:" is
        // left off of the URL
        if (result == null) {
            throw new JdbcException(JdbcPlugin.Util.getString("JdbcManagerImpl.NullConnectionFromDriver")); //$NON-NLS-1$
        }
        return result;
    }

    /**
     * Creates either a {@link javax.sql.DataSource} or {@link java.sql.Driver} object given the supplied {@link JdbcSource} and
     * {@link ClassLoader}.
     * 
     * @param jdbcSource
     * @param loader
     * @param password
     * @return either a DataSource or Driver instance
     * @throws JdbcException if the source is not valid, or if the {@link JdbcSource#getDriverClass() driver class} does not
     *         implement the {@link javax.sql.DataSource} or {@link java.sql.Driver} interfaces
     * @throws SQLException
     */
    protected Object createDriverObject( final JdbcSource jdbcSource,
                                         final ClassLoader loader,
                                         final String password ) throws JdbcException {
        final IStatus status = isValid(jdbcSource, loader);
        if (status.getSeverity() == IStatus.ERROR) {
            throw new JdbcException(status);
        }

        // Load the driver class ...
        try {
            final Class driverClass = Class.forName(jdbcSource.getDriverClass(), true, loader);
            final Class javaSqlDriverClass = Driver.class;
            final Class javaxSqlDataSourceClass = DataSource.class;
            // If instanceof Driver ...
            if (javaSqlDriverClass.isAssignableFrom(driverClass)) {
                final Driver driver = (Driver)driverClass.newInstance();
                return driver;
            }
            // If instanceof DataSource
            else if (javaxSqlDataSourceClass.isAssignableFrom(driverClass)) {
                final DataSource dataSource = (DataSource)driverClass.newInstance();
                final Properties props = createProperties(jdbcSource);
                if (password != null) {
                    props.put("password", password); //$NON-NLS-1$
                }
                setProperties(props, dataSource);
                return dataSource;
            } else {
                final Object[] params = new Object[] {driverClass.getName()};
                final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.The_class_{0}_does_not_implement_Driver_or_DataSource", params); //$NON-NLS-1$
                throw new JdbcException(msg);
            }
        } catch (ClassNotFoundException e) {
            throw new JdbcException(e);
        } catch (InstantiationException e) {
            throw new JdbcException(e);
        } catch (IllegalAccessException e) {
            throw new JdbcException(e);
        }
    }

    /**
     * @param jdbcSource
     * @return
     */
    protected Properties createProperties( final JdbcSource jdbcSource ) {
        final Properties props = new Properties();
        final Iterator iter = jdbcSource.getProperties().iterator();
        while (iter.hasNext()) {
            final JdbcSourceProperty prop = (JdbcSourceProperty)iter.next();
            props.put(prop.getName().toLowerCase(), prop.getValue());
        }
        return props;
    }

    /**
     * @param dataSource
     */
    protected void setProperties( final Properties props,
                                  final DataSource dataSource ) throws JdbcException {
        final Method[] methods = dataSource.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            final String methodName = method.getName();
            // If setter ...
            if (methodName.startsWith("set") && method.getParameterTypes().length == 1) { //$NON-NLS-1$
                // Get the property name
                final String propertyName = methodName.substring(3); // remove the "set"
                String propertyValue = (String)props.get(propertyName.toLowerCase());
                if (propertyValue != null) {
                    final Class argType = method.getParameterTypes()[0];
                    final Object[] params = new Object[1];
                    if (argType == Integer.TYPE) {
                        params[0] = Integer.decode(propertyValue);
                    } else if (argType == Boolean.TYPE) {
                        params[0] = Boolean.valueOf(propertyValue);
                    } else if (argType == String.class) {
                        params[0] = propertyValue;
                    }

                    // Actually set the property ...
                    try {
                        method.invoke(dataSource, params);
                    } catch (Throwable e) {
                        final Object[] msgParams = new Object[] {propertyName, propertyValue};
                        final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Unable_to_set_property_to_value", msgParams); //$NON-NLS-1$
                        throw new JdbcException(msg);
                    }
                }
            }
        }
    }

    /**
     * Obtain the list of properties used to connect to the supplied source.
     * 
     * @param jdbcSource
     * @return the array of {@link JdbcDriverProperty} instances; may be empty if none could be obtained from the driver, but
     *         never null
     * @throws JdbcException if there is an error
     * @since 4.2
     */
    public JdbcDriverProperty[] getPropertyDescriptions( final JdbcSource jdbcSource ) throws JdbcException {
        ArgCheck.isNotNull(jdbcSource);

        final JdbcDriver jdbcDriver = jdbcSource.getJdbcDriver();
        if (jdbcDriver != null) {
            final URLClassLoader loader = getClassLoader(jdbcDriver);
            if (loader != null) {
                try {
                    // Create a Driver or DataSource object for the source ...
                    final Object jdbcDriverObj = createDriverObject(jdbcSource, loader, null);
                    if (jdbcDriverObj instanceof Driver) {
                        final Driver driver = (Driver)jdbcDriverObj;
                        final JdbcDriverProperty[] props = getPropertyDescriptions(driver, jdbcSource);
                        return props;
                    } else if (jdbcDriverObj instanceof DataSource) {
                        final DataSource dataSource = (DataSource)jdbcDriverObj;
                        final JdbcDriverProperty[] props = getPropertyDescriptions(dataSource, jdbcSource);
                        return props;
                    }
                } catch (SQLException e) {
                    throw new JdbcException(e);
                }
            }
        }
        return new JdbcDriverProperty[] {};
    }

    protected JdbcDriverProperty[] getPropertyDescriptions( final Driver driver,
                                                            final JdbcSource jdbcSource ) throws SQLException {
        ArgCheck.isNotNull(driver);
        ArgCheck.isNotNull(jdbcSource);

        DriverPropertyInfo[] propInfo = null;
        final String url = jdbcSource.getUrl() != null ? jdbcSource.getUrl() : ""; //$NON-NLS-1$
        try {
            propInfo = driver.getPropertyInfo(url, null);
        } catch (RuntimeException err) {
            // Some drivers (Sun's ODBC-JDBC) throw null pointer exceptions ...

            // Try again, but with an empty properties ...
            try {
                propInfo = driver.getPropertyInfo(url, new Properties());
            } catch (RuntimeException err2) {
                // Okay, give up
            }
        }
        if (propInfo == null || propInfo.length == 0) {
            return new JdbcDriverProperty[] {};
        }

        final List props = new ArrayList(propInfo.length);
        for (int i = 0; i < propInfo.length; i++) {
            final DriverPropertyInfo info = propInfo[i];
            final JdbcDriverProperty prop = new JdbcDriverProperty(info);
            props.add(prop);
        }

        return (JdbcDriverProperty[])props.toArray(new JdbcDriverProperty[props.size()]);
    }

    protected JdbcDriverProperty[] getPropertyDescriptions( final DataSource dataSource,
                                                            final JdbcSource jdbcSource ) {
        final Method[] methods = dataSource.getClass().getMethods();
        final List props = new ArrayList();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            final String methodName = method.getName();
            // If setter ...
            if (methodName.startsWith("set") && method.getParameterTypes().length == 1) { //$NON-NLS-1$
                // Get the property name
                final String name = methodName.substring(3); // remove the "set"
                final String desc = null;
                final String[] allowableValues = null;
                final boolean required = true;
                final JdbcDriverProperty prop = new JdbcDriverProperty(name, desc, allowableValues, required);
                props.add(prop);
            }
        }

        return (JdbcDriverProperty[])props.toArray(new JdbcDriverProperty[props.size()]);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#isValid(com.metamatrix.modeler.jdbc.JdbcDriver)
     */
    public IStatus isValid( final JdbcSource jdbcSource ) {
        ArgCheck.isNotNull(jdbcSource);
        return isValid(jdbcSource, null);
    }

    protected IStatus isValid( final JdbcSource jdbcSource,
                               ClassLoader loader ) {
        ArgCheck.isNotNull(jdbcSource);

        // A JdbcDriver is considered valid if all of the following conditions are true:
        // - There is a {@link JdbcSource#getName() name} that is not zero-length.
        final String name = jdbcSource.getName();
        if (name == null || name.trim().length() == 0) {
            final int code = NAME_NOT_SPECIFIED;
            return createError(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_name_is_empty")); //$NON-NLS-1$
        }
        if (!Character.isLetterOrDigit(name.charAt(0))) {
            final int code = NAME_MUST_BEGIN_WITH_LETTER_OR_NUMBER;
            return createError(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_name_must_begin_with_a_letter_or_digit")); //$NON-NLS-1$
        }

        // - There is a {@link JdbcSource#get preferred driver class}.
        final String driverClass = jdbcSource.getDriverClass();
        if (driverClass == null || driverClass.trim().length() == 0) {
            final int code = MISSING_DRIVER_CLASS;
            return createError(code,
                               JdbcPlugin.Util.getString("JdbcManagerImpl.A_driver_class_must_be_specified", jdbcSource.getName())); //$NON-NLS-1$
        }
        final IStatus error = checkClassNameForError(driverClass);
        if (error != null) {
            return error;
        }

        // - There are no null property names or values
        final Iterator iter = jdbcSource.getProperties().iterator();
        while (iter.hasNext()) {
            final JdbcSourceProperty prop = (JdbcSourceProperty)iter.next();
            final String propName = prop.getName();
            final String value = prop.getValue();
            if (propName == null || propName.trim().length() == 0) {
                final int code = PROPERTY_NAME_MISSING;
                return createError(code, JdbcPlugin.Util.getString("JdbcManagerImpl.Property_names_may_not_be_null_or_empty")); //$NON-NLS-1$
            }
            if (value == null || value.trim().length() == 0) {
                final int code = PROPERTY_VALUE_MISSING;
                return createError(code, JdbcPlugin.Util.getString("JdbcManagerImpl.Property_values_may_not_be_null_or_empty")); //$NON-NLS-1$
            }
        }

        // -----------------------------------------------------------------------
        // If a java.sql.Driver is the driver class, then a URL should be provided
        // -----------------------------------------------------------------------
        if (loader == null) {
            // Create a class loader
            final JdbcDriver driver = this.findBestDriver(jdbcSource);
            if (driver == null) {
                final int code = UNABLE_TO_FIND_DRIVER;
                final Object[] params = new Object[] {driverClass};
                return createWarning(code,
                                     JdbcPlugin.Util.getString("JdbcManagerImpl.Unable_to_find_the_JDBC_driver_containing_driver_class", params)); //$NON-NLS-1$
            }
            try {
                loader = this.getClassLoader(driver);
            } catch (Throwable e) {
                final int code = ERROR_CHECKING_DRIVER_CLASS;
                final Object[] params = new Object[] {driverClass};
                return createWarning(code,
                                     e,
                                     JdbcPlugin.Util.getString("JdbcManagerImpl.Error_while_validating_driver_class", params)); //$NON-NLS-1$
            }
        }
        try {
            final Class driverClassClass = Class.forName(driverClass, true, loader);
            if (Driver.class.isAssignableFrom(driverClassClass)) {
                // Check that the URL is not null or zero length
                final String url = jdbcSource.getUrl();
                if (url == null || url.trim().length() == 0) {
                    final int code = URL_NOT_SPECIFIED;
                    return createError(code,
                                       JdbcPlugin.Util.getString("JdbcManagerImpl.A_URL_is_required_since_the_driver_class_implements_java.sql.Driver")); //$NON-NLS-1$
                }
                if (!url.startsWith("jdbc:")) { //$NON-NLS-1$
                    final int code = URL_MUST_START_WITH_JDBC;
                    return createWarning(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_URL_must_begin_with_jdbc")); //$NON-NLS-1$
                }
            }
        } catch (ClassNotFoundException e) {
            final int code = ERROR_FINDING_DRIVER_CLASS;
            final Object[] params = new Object[] {driverClass};
            return createWarning(code, e, JdbcPlugin.Util.getString("JdbcManagerImpl.Error_finding_driver_class", params)); //$NON-NLS-1$
        }

        final int code = VALID_SOURCE;
        return createOK(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_data_source_is_valid")); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#isValid(com.metamatrix.modeler.jdbc.JdbcDriver)
     */
    public IStatus isValid( final JdbcDriver driver ) {
        ArgCheck.isNotNull(driver);
        return isValid(driver, true);
    }

    /**
     * Helper method to actually do the checking of the JdbcDriver. This is also used by the {@link #getClassLoader(JdbcDriver)}
     * method, but that doesn't want to check driver class names.
     */
    protected IStatus isValid( final JdbcDriver driver,
                               final boolean checkDriverClasses ) {
        ArgCheck.isNotNull(driver);

        // A JdbcDriver is considered valid if all of the following conditions are true:
        // - There is a {@link JdbcDriver#getName() name} that is not zero-length.
        final String name = driver.getName();
        if (name == null || name.trim().length() == 0) {
            final int code = NAME_NOT_SPECIFIED;
            return createError(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_name_is_empty")); //$NON-NLS-1$
        }
        if (!Character.isLetterOrDigit(name.charAt(0))) {
            final int code = NAME_MUST_BEGIN_WITH_LETTER_OR_NUMBER;
            return createError(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_name_must_begin_with_a_letter_or_digit")); //$NON-NLS-1$
        }

        if (checkDriverClasses) {
            // - There is at least one {@link JdbcDriver#getAvailableDriverClassNames() driver class}.
            final List availableClasses = driver.getAvailableDriverClassNames();
            if (availableClasses.isEmpty()) {
                final int code = NO_AVAILABLE_DRIVER_CLASS_NAMES;
                return createError(code,
                                   JdbcPlugin.Util.getString("JdbcManagerImpl.No_java.sql.Driver_or_javax.sql.DataSource_classes_were_found", name)); //$NON-NLS-1$
            }
            final Iterator iter = availableClasses.iterator();
            while (iter.hasNext()) {
                final String className = (String)iter.next();
                final IStatus error = checkClassNameForError(className);
                if (error != null) {
                    return error;
                }
            }

            // - There is a {@link JdbcDriver#getPreferredDriverClassName() preferred driver class}.
            final String preferred = driver.getPreferredDriverClassName();
            if (preferred == null || preferred.trim().length() == 0) {
                final int code = PREFERRED_NOT_SPECIFIED;
                return createError(code,
                                   JdbcPlugin.Util.getString("JdbcManagerImpl.A_preferred_driver_class_must_be_chosen", name)); //$NON-NLS-1$
            }
            final IStatus error = checkClassNameForError(preferred);
            if (error != null) {
                return error;
            }

            // - There {@link JdbcDriver#getPreferredDriverClassNames() preferred driver class} is one of the
            // {@link JdbcDriver#getAvailableDriverClassNames() available driver classes}
            if (!availableClasses.contains(preferred)) {
                final int code = PREFERRED_NOT_IN_AVAILABLE;
                return createError(code,
                                   JdbcPlugin.Util.getString("JdbcManagerImpl.The_preferred_driver_class_is_not_available", name)); //$NON-NLS-1$
            }
        }

        // Additionally, a warning is included if any of the following are true:
        // - There are no {@link JdbcDriver#getJarFileUris() JAR file URIs}.
        if (driver.getJarFileUris().isEmpty()) {
            final int code = NO_JARS_SPECIFIED;
            return createWarning(code,
                                 JdbcPlugin.Util.getString("JdbcManagerImpl.There_are_no_JAR_files_specified.__Only_the_system_classpath_will_be_used", name)); //$NON-NLS-1$
        }
        final Iterator jarFileUriIter = driver.getJarFileUris().iterator();
        while (jarFileUriIter.hasNext()) {
            String jarFileUri = (String)jarFileUriIter.next();
            if (jarFileUri == null || jarFileUri.trim().length() == 0) {
                final int code = JAR_FILE_URI_NOT_SPECIFIED;
                return createError(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_jar_file_uri_is_empty", name)); //$NON-NLS-1$
            }
            // Try resolving the URL to an input stream ...
            boolean validUrl = false;
            InputStream stream = null;
            try {
                final URL url = new URL(jarFileUri);
                stream = url.openStream();
                validUrl = true;
            } catch (Throwable e) {
                // Let the rest of the patterns try ...
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e1) {
                        // do nothing, because we don't care
                    }
                }
            }

            if (!validUrl) {
                // Try to resolve the URL to a file ...
                try {
                    final File jarFile = new File(jarFileUri);
                    if (!jarFile.exists()) {
                        final int code = JAR_FILE_DOESNT_EXIST;
                        final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Supplied_path_does_not_exist", name, jarFileUri); //$NON-NLS-1$
                        return createError(code, msg);
                    }
                    jarFile.toURI().toURL();
                    validUrl = true;
                } catch (MalformedURLException e) {
                    // Let the rest of the patterns try ...
                }
            }
            if (!validUrl) {
                final int code = MALFORMED_URL;
                final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.Unable_to_create_class_loader_for_the_driver", name); //$NON-NLS-1$
                return createError(code, msg);
            }
        }

        // Check the URL syntax
        final String urlSyntax = driver.getUrlSyntax();
        if (urlSyntax != null && urlSyntax.trim().length() != 0) {
            // See if the URL syntax begin with "jdbc:"
            if (!urlSyntax.startsWith("jdbc:")) { //$NON-NLS-1$
                final int code = MALFORMED_URL_SYNTAX;
                final String msg = JdbcPlugin.Util.getString("JdbcManagerImpl.malformedUrlSyntax", name); //$NON-NLS-1$
                return createWarning(code, msg);
            }
        }

        final int code = VALID_DRIVER;
        return createOK(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_driver_specification_is_valid", name)); //$NON-NLS-1$
    }

    protected IStatus checkClassNameForError( final String className ) {
        if (className == null || className.trim().length() == 0) {
            final int code = NAME_NOT_SPECIFIED;
            return createError(code, JdbcPlugin.Util.getString("JdbcManagerImpl.The_class_name_is_empty")); //$NON-NLS-1$
        }
        if (!Character.isJavaIdentifierStart(className.charAt(0))) {
            final Object params = new Object[] {new Character(className.charAt(0))};
            final int code = ILLEGAL_CHAR_AT_START_OF_CLASS_NAME;
            return createError(code,
                               JdbcPlugin.Util.getString("JdbcManagerImpl.A_Java_class_may_not_begin_with_the_{0}_character", params)); //$NON-NLS-1$
        }
        CharacterIterator iter = new StringCharacterIterator(className);
        char c = iter.next(); // skip the first character
        while ((c = iter.next()) != CharacterIterator.DONE) {
            if (c != '.' && !Character.isJavaIdentifierPart(c)) {
                final Object params = new Object[] {new Character(className.charAt(0))};
                final int code = ILLEGAL_CHAR_IN_CLASS_NAME;
                return createError(code,
                                   JdbcPlugin.Util.getString("JdbcManagerImpl.A_Java_class_may_not_contain_the_{0}_character", params)); //$NON-NLS-1$
            }
        }
        return null;
    }

    protected IStatus createError( final int code,
                                   final String msg ) {
        return new Status(IStatus.ERROR, JdbcPlugin.PLUGIN_ID, code, msg, null);
    }

    protected IStatus createWarning( final int code,
                                     final String msg ) {
        return new Status(IStatus.WARNING, JdbcPlugin.PLUGIN_ID, code, msg, null);
    }

    protected IStatus createWarning( final int code,
                                     final Throwable t,
                                     final String msg ) {
        return new Status(IStatus.WARNING, JdbcPlugin.PLUGIN_ID, code, msg, t);
    }

    protected IStatus createOK( final int code,
                                String msg ) {
        return new Status(IStatus.OK, JdbcPlugin.PLUGIN_ID, code, msg, null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#findDrivers(java.lang.String)
     */
    public JdbcDriver[] findDrivers( String driverName ) {
        final List result = new ArrayList();
        final List instances = getJdbcDrivers();
        final Iterator iter = instances.iterator();
        while (iter.hasNext()) {
            final JdbcDriver instance = (JdbcDriver)iter.next();
            if (instance.getName().equalsIgnoreCase(driverName)) {
                result.add(instance);
            }
        }
        return (JdbcDriver[])result.toArray(new JdbcDriver[result.size()]);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#findSources(java.lang.String)
     */
    public JdbcSource[] findSources( String sourceName ) {
        final List result = new ArrayList();
        final List instances = getJdbcSources();
        final Iterator iter = instances.iterator();
        while (iter.hasNext()) {
            final JdbcSource instance = (JdbcSource)iter.next();
            if (instance.getName().equalsIgnoreCase(sourceName)) {
                result.add(instance);
            }
        }
        return (JdbcSource[])result.toArray(new JdbcSource[result.size()]);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#saveConnections()
     */
    public void saveConnections( OutputStream fileOutputStream ) throws IOException {
        this.resource.save(fileOutputStream, new HashMap());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.JdbcManager#loadConnections()
     */
    public List loadConnections( Resource resrc ) throws IOException {
        // Iterate over the roots to find the FIRST JdbcSourceContainer
        final List roots = resrc.getContents();
        final Iterator iter = roots.iterator();
        JdbcSourceContainer sourceContainer = null;
        while (iter.hasNext()) {
            final EObject root = (EObject)iter.next();
            if (root instanceof JdbcSourceContainer) {
                sourceContainer = (JdbcSourceContainer)root;
                break;
            }
        }

        if (sourceContainer != null) {
            List newSourceList = sourceContainer.getJdbcSources();
            List result = new ArrayList(newSourceList);
            List driversToBeMoved = new ArrayList();
            synchronized (sourcesLock) {
                for (Iterator srcIter = result.iterator(); srcIter.hasNext();) {
                    JdbcSource incomingSrc = (JdbcSource)srcIter.next();
                    if (incomingSrc != null) {
                        // find the JDBC driver from the incoming source ...
                        final JdbcDriver incomingDriver = incomingSrc.getJdbcDriver();
                        // find a local JDBC driver (based upon the driver name) ...
                        // but require the source's driver class to match one in the driver
                        final JdbcDriver localDriver = findBestDriver(incomingSrc, true);
                        if (localDriver != null) {
                            // Change the incoming to point to the local driver ...
                            incomingSrc.setJdbcDriver(localDriver);
                        } else if (!driversToBeMoved.contains(incomingDriver)) {
                            // Move the incoming driver to this (if not already going to be moved...
                            driversToBeMoved.add(incomingDriver);
                        }

                        this.sources.getJdbcSources().add(incomingSrc);
                    }
                }
            }

            // Move the drivers that are needed ...
            synchronized (driversLock) {
                this.drivers.getJdbcDrivers().addAll(driversToBeMoved);
            }

            this.resource.save(Collections.EMPTY_MAP);

            // We've scavenged from this model, so unload it and force it gone ...
            resrc.unload();
            resrc.getResourceSet().getResources().remove(resrc);

            return result;
        }

        return Collections.EMPTY_LIST;
    }
}
