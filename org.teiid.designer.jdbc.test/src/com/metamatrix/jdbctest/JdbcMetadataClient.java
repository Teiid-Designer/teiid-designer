/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.jdbctest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * This is a self-contained test case for demonstrating the calls made during import.  Currently,
 * not all of the functionality exercised during import is tested (e.g., primary/foreign keys, procedures).
 */
public class JdbcMetadataClient {
    
    /** 
     * @since 4.3
     */
    private static final String STAT_FILE_DATE_FORMAT = "yyyyMMdd_HH_mm_ss"; //$NON-NLS-1$
    public static final String EXCLUDED_PATTERN = null;
    public static final String WILDCARD_PATTERN = "%"; //$NON-NLS-1$
    public static final String NOT_APPLICABLE = ""; //$NON-NLS-1$
    public static final String DELIMITER = "."; //$NON-NLS-1$

    protected static boolean DEBUG = false;
    private static PrintStream logStream;
    
//    private String driverClassName;
    private String url;
//    private String username;
//    private String password;
    private ClassLoader driverClassLoader;
    private Connection connection;
    private DatabaseMetaData metadata;
    
    private boolean supportsSchemas;
    private boolean supportsCatalogs;
//    private String catalogTerm;
//    private String schemaTerm;
    private final List catalogs;
    private final List schemas;
    private final List tableTypes;
    private final List tableInfos;
    private final List allTableInfos;   // the list of TableInfo, results of getTables("","","%",null);

    private int numTableRemarks;
    private int numColumnRemarks;


    private final Stopwatch stopwatchCatalogs;
    private final Stopwatch stopwatchSchemas;
    private final Stopwatch stopwatchTableInfos;
    private final Stopwatch stopwatchAllTableInfos;
    private final Stopwatch stopwatchColumns;

    /**
     * Construct an instance of JdbcMetadataClient.
     * 
     */
    public JdbcMetadataClient() {
        super();
        this.catalogs = new LinkedList();
        this.schemas = new LinkedList();
        this.tableTypes = new LinkedList();
        this.tableInfos = new LinkedList();
        this.allTableInfos = new LinkedList();

        this.stopwatchCatalogs = new Stopwatch();
        this.stopwatchSchemas = new Stopwatch();
        this.stopwatchTableInfos = new Stopwatch();
        this.stopwatchAllTableInfos = new Stopwatch();
        this.stopwatchColumns = new Stopwatch();
    }
    
    /**
     * Set the class loader that should be used to load the driver class.  This is an optional method
     * that, if used, should be called prior to {@link #connect(String, String, String, String)}.
     * @param loader the class loader; may be null if the class loader for this class should be used
     */
    public void setDriverClassLoader( final ClassLoader loader ) {
        this.driverClassLoader = loader;
    }
    
    /**
     * Return the class loader that would be used to load the driver class.
     * @return the class loader; if null, then the class loader for this class is used
     */
    public ClassLoader getDriverClassLoader() {
        return this.driverClassLoader;
    }
    
    /**
     * Connect to the JDBC database using the supplied driver and connection information.
     * @param driverClassName the name of the {@link java.sql.Driver} implementation; may not be null or zero length
     * @param url the url of the JDBC database; may not be null or zero length
     * @param username the username to be used to connect to the database; may be null if not needed
     * @param password the password to be used to connect to the database; may be null if not needed
     * @throws IllegalArgumentException if any of the arguments are invalid
     * @throws SQLException if there is a problem establishing a connection to the database with the supplied
     * information
     * @throws ClassNotFoundException if the supplied class could not be loaded on this object's class loader
     * @throws InstantiationException if the supplied class could not be instantiated
     * @throws IllegalAccessException if the supplied class could not be accessed
     * @see #setDriverClassLoader(ClassLoader)
     */
    public void connect( final String driverClassName, final String url, final String username, final String password )
                         throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        connect(driverClassName,url,username,password,null);
    }


    /**
     * Connect to the JDBC database using the supplied driver and connection information.
     * @param driverClassName the name of the {@link java.sql.Driver} implementation; may not be null or zero length
     * @param url the url of the JDBC database; may not be null or zero length
     * @param username the username to be used to connect to the database; may be null if not needed
     * @param password the password to be used to connect to the database; may be null if not needed
     * @param additionalProperties additional properties to be specified on the connect call; may be null if not needed
     * @throws IllegalArgumentException if any of the arguments are invalid
     * @throws SQLException if there is a problem establishing a connection to the database with the supplied
     * information
     * @throws ClassNotFoundException if the supplied class could not be loaded on this object's class loader
     * @throws InstantiationException if the supplied class could not be instantiated
     * @throws IllegalAccessException if the supplied class could not be accessed
     * @see #setDriverClassLoader(ClassLoader)
     */
    public void connect( final String driverClassName, final String url, final String username, final String password,
                         final Properties additionalProperties )
                         throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        assertNotZeroLength(driverClassName);
        assertNotZeroLength(url);
//        this.driverClassName = driverClassName;
        this.url = url;
//        this.username = username;
//        this.password = password;
        
        // Load the driver class
        log("Loading the driver: " + driverClassName); //$NON-NLS-1$
        ClassLoader loader = this.getDriverClassLoader();
        if ( loader == null ) {
            log("Using the default class loader"); //$NON-NLS-1$
            loader = this.getClass().getClassLoader();
        } else {
            log("Using the supplied class loader: " + loader.toString()); //$NON-NLS-1$
        }
        final Class driverClass = loader.loadClass(driverClassName);
        assertNotNull(driverClass);
        final Driver driver = (Driver) driverClass.newInstance();
        assertNotNull(driver);
        
        // Establish the connection ...
        final Properties info = new Properties();
        if ( additionalProperties != null ) {
            info.putAll(additionalProperties);
        }
        if ( username != null && username.trim().length() != 0 ) {
            log("Using username: " + username); //$NON-NLS-1$
            info.put("user",username); //$NON-NLS-1$
        }
        if ( password != null ) {
            log("Using password: *******"); //$NON-NLS-1$
            info.put("password",password); //$NON-NLS-1$
        }
        log("Connecting to URL [" + url + "] additional properties: [" + stringifyProps(info) + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        this.connection = driver.connect(url,info);
        assertNotNull(this.connection);
        log("Connection established"); //$NON-NLS-1$
        
        // Getting metadata ...
        log("Getting database metadata object"); //$NON-NLS-1$
        this.metadata = this.connection.getMetaData();
        assertNotNull(this.metadata);
    }
    
    /**
     * Disconnect from the JDBC database by closing the connection, if there is one established.
     * This method should be called after use; failing to do so will result in unclosed database connections.
     * @throws SQLException if there is a problem closing the connection
     */
    public void disconnect() throws SQLException {
        if ( this.connection != null ) {
            try {
                log("Disconnecting from " + this.url); //$NON-NLS-1$
                this.connection.close();
                log("Disconnected from " + this.url); //$NON-NLS-1$
                log(""); //$NON-NLS-1$
            } finally {
                doClear();
            }
        }
    }
    
    /**
     * Method called when disconnecting.  This method should clear all cached information.  This method
     * can be overwritten by subclasses.
     */
    protected void doClear() {
//        this.driverClassName = null;
        this.url = null;
//        this.username = null;
//        this.password = null;
        this.catalogs.clear();
        this.schemas.clear();
        this.tableTypes.clear();
        this.tableInfos.clear();
        this.allTableInfos.clear();
//        this.catalogTerm = null;
//        this.schemaTerm = null;
        
        this.stopwatchCatalogs.reset();
        this.stopwatchSchemas.reset();
        this.stopwatchTableInfos.reset();
        this.stopwatchAllTableInfos.reset();
        this.stopwatchColumns.reset();
    }
    
    //-------------------------------------------------------------------------
    //                   Main methods
    //-------------------------------------------------------------------------
    
    /**
     * Connect to the JDBC database using the driver and connection information supplied in the arguments,
     * and import the metadata from the database.
     * @param driverClass the name of the {@link java.sql.Driver} implementation; may not be null or zero length
     * @param url the url of the JDBC database; may not be null or zero length
     * @param username the username to be used to connect to the database; may be null if not needed
     * @param password the password to be used to connect to the database; may be null if not needed
     * @param additionalProps contains properties for DB connection
     * @param loader the class loader for the driver; may be null if this class' class loader should be used
     * @param outfileName optional file to output statistics.  Date/time will be appended to prohibit overwrite.
     * Default is to write stats to STDOUT.
     * @param debug optional arg that, if true, will log debugging info to STDOUT.  Default is false.
     * @throws IllegalArgumentException if any of the arguments are invalid
     * @throws SQLException if there is a problem establishing a connection to the database with the supplied
     * information
     * @throws ClassNotFoundException if the supplied class could not be loaded on this object's class loader
     * @throws InstantiationException if the supplied class could not be instantiated
     * @throws IllegalAccessException if the supplied class could not be accessed
     */
    public static void execute( final String driverClass, final String url, final String username, 
                                final String password, final Properties additionalProps, 
                                final ClassLoader loader, final String outfileName, final boolean debug )
                            throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        
        // Set up logging
        setLogStream(System.out);
        if ( debug ) {
            setDEBUG();
        }

        Date now = new Date();
        PrintStream outStream = System.out;
        if (outfileName != null && outfileName.trim().length() > 0) {
            SimpleDateFormat format = new SimpleDateFormat(STAT_FILE_DATE_FORMAT); 
            try {
                String statsfileName = outfileName + "_" + format.format(now) + ".log"; //$NON-NLS-1$ //$NON-NLS-2$
                File outFile = new File(statsfileName);
                outStream = new PrintStream(new FileOutputStream(outFile));
                System.out.println("Stats will be written to " + statsfileName); //$NON-NLS-1$
            } catch (Throwable err) {
                outStream = System.out;
                System.err.println("ERROR: " + err.getMessage() + " Stats will be written to STDOUT"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } 
        
        // Test the file stream
        outStream.println("# " + now); //$NON-NLS-1$
        outStream.println("# Connecting to URL [" + url + "] additional properties: [" + stringifyProps(additionalProps) + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        final JdbcMetadataClient db = new JdbcMetadataClient();

        // Establish the connection ...
        log("Connecting..."); //$NON-NLS-1$
        
        if ( loader != null ) {
            db.setDriverClassLoader(loader);
        }
        db.connect(driverClass,url,username,password,additionalProps);

        // Perform the test
        try {
            log("Performing the test"); //$NON-NLS-1$
            
            db.loadCapabilities();
            db.loadTableTypes();
            db.loadCatalogs();
            db.loadSchemas();
            db.loadAllTables();
            db.loadTables();
            db.calculateStats();
            
//            db.loadColumns(db.allTableInfos);
            db.loadColumns(db.tableInfos);
            
            db.printResults(outStream);
        } finally {
            db.disconnect();
        }
    }
    
    /**
     * Connect to the JDBC database using the driver and connection information supplied in the arguments,
     * and import the metadata from the database.
     * <p>
     * Usage:
     * </p><p>
     * <code> JdbcMetadataClient driver={driverClassName} url={url} [user={username}] [password={password}] [name1=value1 name2=value2 ...]</code> 
     * </p><p>
     * where:
     * <ul>
     *  <li>driverClassName the name of the {@link java.sql.Driver} implementation; required (and expected
     *      to be on the classpath)</li>
     *  <li>url the url of the JDBC database; required</li>
     *  <li>username the username to be used to connect to the database; optional</li>
     *  <li>password the password to be used to connect to the database; optional</li>
     *  <li>"name[n]=value[n]" the name and value of 0 or more JDBC properties to use when connecting; optional"</li>
     *  <li>outfile a file path to write the statistics; optional (default is System.out)</li>
     *  <li>DEBUG if <code>true</code>, will log debugging info to STDOUT; optional (default is false)</li>
     * </ul>
     * </p><p>
     * This method parses the arguments and then calls {@link #execute(String, String, String, String)}.
     * </p>
     */
    public static void main(final String[] args) {
        // Print help 
        if ( args.length == 0 ) {
            printUsage(System.out);
            return;
        }
        
        // Parse the arguments ...
        String driver = null;
        String url = null;
        String username = null;
        String password = null;
        String outFile = null;
        boolean debug = false;
        Properties additionalProps = new Properties();
        for (int i = 0; i < args.length; ++i) {
            final String arg = args[i];
            if ( arg.startsWith("driver=") ) { //$NON-NLS-1$
                driver = arg.substring("driver=".length()); //$NON-NLS-1$
            } else if ( arg.startsWith("url=") ) { //$NON-NLS-1$
                url = arg.substring("url=".length()); //$NON-NLS-1$
            } else if ( arg.startsWith("user=") ) { //$NON-NLS-1$
                username = arg.substring("user=".length()); //$NON-NLS-1$
            } else if ( arg.startsWith("password=") ) { //$NON-NLS-1$
                password = arg.substring("password=".length()); //$NON-NLS-1$
            } else if ( arg.startsWith("outfile=") ) { //$NON-NLS-1$
                outFile = arg.substring("outfile=".length()); //$NON-NLS-1$
            } else if ( arg.equalsIgnoreCase("DEBUG") ) { //$NON-NLS-1$
                debug = true;
            } else {
                final int index = arg.indexOf("="); //$NON-NLS-1$
                if ( index > 1 && arg.length() > (index + 1) ) {
                    final String key = arg.substring(0,index);
                    final String value = arg.substring(index+1);
                    if ( key != null && key.trim().length() != 0 && value != null && value.trim().length() != 0 ) {
                        additionalProps.setProperty(key,value);
                    }
                }
            }
        }

        // Execute the test ...
        try {
            execute(driver,url,username,password,additionalProps,null,outFile,debug);
        } catch ( Throwable e ) {
            fail(e);
        }
        // Perform the test
    }

    /** 
     * Print directions for running this tool.
     * @since 4.3
     */
    private static void printUsage(PrintStream out) {
        out.println("Usage:"); //$NON-NLS-1$
        out.println("  com.metamatrix.jdbctest.JdbcMetadataClient driver={driverClassName} url={url} [\"user={username}] [password={password}] [name1=value1 name2=value2 ...]\" [outfile={fileName}] [DEBUG]"); //$NON-NLS-1$ 
        out.println("where"); //$NON-NLS-1$
        out.println("  driverClassName  - the name of the {@link java.sql.Driver} implementation; required (and expected to be on the classpath)"); //$NON-NLS-1$
        out.println("  url              - the url of the JDBC database; required"); //$NON-NLS-1$
        out.println("  username         - the username to be used to connect to the database; optional"); //$NON-NLS-1$
        out.println("  password         - the password to be used to connect to the database; optional"); //$NON-NLS-1$
        out.println("  name[n]=value[n] - the name and value of 0 or more JDBC properties to use when connecting; optional - should be in quotes"); //$NON-NLS-1$
        out.println("  outfile          - a file path prefix to write the statistics - \"" +STAT_FILE_DATE_FORMAT + ".log\" will be appended to prevent overwrite; optional (default is System.out)"); //$NON-NLS-1$ //$NON-NLS-2$
        out.println("  DEBUG if <code>true</code>, will log debugging info to STDOUT; optional (default is false)"); //$NON-NLS-1$
    }

    /** 
     * @param logStream The logStream to set.
     * @since 4.3
     */
    public static void setLogStream(PrintStream logStream) {
        JdbcMetadataClient.logStream = logStream;
    }
    
    /** 
     * @param debug The dEBUG to set.
     * @since 4.3
     */
    public static void setDEBUG() {
        DEBUG = true;
    }

    //-------------------------------------------------------------------------
    //                 Methods to load metadata
    //-------------------------------------------------------------------------
    
    protected void loadCapabilities() throws SQLException {
        assertConnected();
        // Supports Catalogs ...
        final boolean supportsCatalogsInTableDefinitions = this.metadata.supportsCatalogsInTableDefinitions();
        final boolean supportsCatalogsInDataManipulation = this.metadata.supportsCatalogsInDataManipulation();
        final boolean supportsCatalogsInProcedureCalls   = this.metadata.supportsCatalogsInProcedureCalls();
        this.supportsCatalogs = supportsCatalogsInTableDefinitions
                             || supportsCatalogsInDataManipulation
                             || supportsCatalogsInProcedureCalls;

        // Supports Schemas ...
        final boolean supportsSchemasInTableDefinitions = this.metadata.supportsSchemasInTableDefinitions();
        final boolean supportsSchemasInDataManipulation = this.metadata.supportsSchemasInDataManipulation();
        final boolean supportsSchemasInProcedureCalls   = this.metadata.supportsSchemasInProcedureCalls();
        this.supportsSchemas = supportsSchemasInTableDefinitions
                            || supportsSchemasInDataManipulation
                            || supportsSchemasInProcedureCalls;

        log("  DatabaseMetaData.supportsCatalogsInTableDefinitions() = " + supportsCatalogsInTableDefinitions); //$NON-NLS-1$
        log("  DatabaseMetaData.supportsCatalogsInDataManipulation() = " + supportsCatalogsInDataManipulation); //$NON-NLS-1$
        log("  DatabaseMetaData.supportsCatalogsInProcedureCalls() = " + supportsCatalogsInProcedureCalls); //$NON-NLS-1$
        log("  DatabaseMetaData.supportsSchemasInTableDefinitions() = " + supportsSchemasInTableDefinitions); //$NON-NLS-1$
        log("  DatabaseMetaData.supportsSchemasInDataManipulation() = " + supportsSchemasInDataManipulation); //$NON-NLS-1$
        log("  DatabaseMetaData.supportsSchemasInProcedureCalls() = " + supportsSchemasInProcedureCalls); //$NON-NLS-1$
    }
    
    protected void loadCatalogs() throws SQLException {
        assertConnected();
        if ( this.supportsCatalogs ) {
            log("  calling:  DatabaseMetaData.getCatalogs()"); //$NON-NLS-1$
            stopwatchCatalogs.start();
            final ResultSet resultSet = this.metadata.getCatalogs();
            stopwatchCatalogs.stop();
            try {
                while( resultSet.next() ) {
                    final String name = resultSet.getString(1);
                    this.catalogs.add(name);
                }
            } finally {
                if ( resultSet != null ) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        log(e,"Error while closing the ResultSet returned from 'getCatalogs()'"); //$NON-NLS-1$
                    }
                }
            }
//            this.catalogTerm =
            this.metadata.getCatalogTerm();
        }
    }
    
    protected void loadSchemas() throws SQLException {
        assertConnected();
        if ( this.supportsSchemas ) {
            log("  calling:  DatabaseMetaData.getSchemas()"); //$NON-NLS-1$
            stopwatchSchemas.start();
            final ResultSet resultSet = this.metadata.getSchemas();
            stopwatchSchemas.stop();
            try {
                while( resultSet.next() ) {
                    final String name = resultSet.getString(1);
                    this.schemas.add(name);
                }
            } finally {
                if ( resultSet != null ) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        log(e,"Error while closing the ResultSet returned from 'getCatalogs()'"); //$NON-NLS-1$
                    }
                }
            }
        }
//        this.schemaTerm =
        this.metadata.getSchemaTerm();
    }
    
    protected void loadTableTypes() throws SQLException {
        assertConnected();
        log("  calling:  DatabaseMetaData.getTableTypes()"); //$NON-NLS-1$
        final ResultSet resultSet = this.metadata.getTableTypes();
        try {
            while( resultSet.next() ) {
                final String tableType = resultSet.getString(1);
                this.tableTypes.add(tableType);
            }
        } finally {
            if ( resultSet != null ) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log(e,"Error while closing the ResultSet returned from 'getTableTypes()'"); //$NON-NLS-1$
                }
            }
        }
    }
    
    protected void calculateStats() {
        this.numTableRemarks = 0;
        this.numTableRemarks = 0;
        
        // Iterate through the tables and count the remarks non-trivial remarks:
        final Iterator iter = this.allTableInfos.iterator();
        while (iter.hasNext()) {
            final TableInfo tableInfo = (TableInfo)iter.next();
            if ( tableInfo.remarks != null && tableInfo.remarks.trim().length() != 0 ) {
                ++this.numTableRemarks;
            }
            
            // Look at the columns ...
            final Iterator columnIter = tableInfo.columnInfos.iterator();
            while (columnIter.hasNext()) {
                final ColumnInfo columnInfo = (ColumnInfo)columnIter.next();
                if ( columnInfo.remarks != null && columnInfo.remarks.trim().length() != 0 ) {
                    ++this.numColumnRemarks;
                }
            }
        }
    }
    
    protected void loadTables() throws SQLException {
        assertConnected();
        // Iterate through the catalogs ...
        if ( this.supportsCatalogs ) {
            final Iterator catalogIter = this.catalogs.iterator();
            while (catalogIter.hasNext()) {
                final String catalogName = (String)catalogIter.next();
                
                // Iterate through the schemas ...
                if ( this.supportsSchemas ) {
                    final Iterator schemaIter = this.schemas.iterator();
                    while (schemaIter.hasNext()) {
                        final String schemaName = (String)schemaIter.next();
                        
                        // Iterate through the table types ...
                        final Iterator typeIter = tableTypes.iterator();
                        while (typeIter.hasNext()) {
                            final String tableType = (String)typeIter.next();
                            
                            // Load the tables in this namespace ...
                            loadTables(catalogName,schemaName,new String[]{tableType});
                        }
                    }
                }
            }
        } else {
            // Catalogs are not supported ...
            final String catalogName = EXCLUDED_PATTERN;
            
            // Iterate through the schemas ...
            if ( this.supportsSchemas ) {
                final Iterator schemaIter = this.schemas.iterator();
                while (schemaIter.hasNext()) {
                    final String schemaName = (String)schemaIter.next();
                        
                    // Iterate through the table types ...
                    final Iterator typeIter = tableTypes.iterator();
                    while (typeIter.hasNext()) {
                        final String tableType = (String)typeIter.next();
                            
                        // Load the tables in this namespace ...
                        loadTables(catalogName,schemaName,new String[]{tableType});
                    }
                }
            } else {
                // Neither catalogs nor schemas are supported ...
                final String schemaName = EXCLUDED_PATTERN;
                
                // Iterate through the table types ...
                final Iterator typeIter = tableTypes.iterator();
                while (typeIter.hasNext()) {
                    final String tableType = (String)typeIter.next();
                            
                    // Load the tables in this namespace ...
                    loadTables(catalogName,schemaName,new String[]{tableType});
                }
            }
        }
    }
    
    protected void loadTables( final String catalogPattern, final String schemaPattern, final String[] tableTypes ) throws SQLException {
        assertConnected();
        log("  calling:  DatabaseMetaData.getTables(\""+catalogPattern+"\",\""+schemaPattern+"\",\""+WILDCARD_PATTERN+"\","+toString(tableTypes)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        stopwatchTableInfos.start();
        final ResultSet resultSet = this.metadata.getTables(catalogPattern,schemaPattern,WILDCARD_PATTERN,tableTypes);  
        stopwatchTableInfos.stop();
        try {
            while( resultSet.next() ) {
                final TableInfo tableInfo = getTableInfo(resultSet);
                this.tableInfos.add(tableInfo);
            }
        } finally {
            if ( resultSet != null ) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log(e,"Error while closing the ResultSet returned from 'getTables(\""+catalogPattern+"\",\""+schemaPattern+"\",\""+WILDCARD_PATTERN+"\","+toString(tableTypes)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                }
            }
        }
    }
    
    protected void loadAllTables() throws SQLException {
        assertConnected();
        log("  calling:  DatabaseMetaData.getTables("+EXCLUDED_PATTERN+","+EXCLUDED_PATTERN+",\""+WILDCARD_PATTERN+"\",null)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        stopwatchAllTableInfos.start();
        final ResultSet resultSet = this.metadata.getTables(EXCLUDED_PATTERN,EXCLUDED_PATTERN,WILDCARD_PATTERN,null);  
        stopwatchAllTableInfos.stop();
        try {
            while( resultSet.next() ) {
                final TableInfo tableInfo = getTableInfo(resultSet);
                this.allTableInfos.add(tableInfo);
            }
        } finally {
            if ( resultSet != null ) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log(e,"Error while closing the ResultSet returned from 'getTables("+EXCLUDED_PATTERN+","+EXCLUDED_PATTERN+",\""+WILDCARD_PATTERN+"\",null)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                }
            }
        }
    }
    
    protected void loadColumns( final List tableInfos ) throws SQLException {
        assertConnected();
        final Iterator iter = tableInfos.iterator();
        while (iter.hasNext()) {
            final TableInfo tableInfo = (TableInfo)iter.next();
            final String catalog = tableInfo.catalog;
            final String schema = tableInfo.schema;
            final String tableName = tableInfo.name;
            assertTrue(tableInfo.columnInfos.isEmpty(),"Table info already had columns: " + tableInfo ); //$NON-NLS-1$
            log("  calling:  DatabaseMetaData.getColumns("+catalog+","+schema+","+tableName+"\""+WILDCARD_PATTERN+"\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            stopwatchColumns.start();
            final ResultSet resultSet = this.metadata.getColumns(catalog,schema,tableName,WILDCARD_PATTERN);
            stopwatchColumns.stop();
            try {
                while( resultSet.next() ) {
                    final ColumnInfo columnInfo = getColumnInfo(resultSet);
                    tableInfo.columnInfos.add(columnInfo);
                }
            } finally {
                if ( resultSet != null ) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        log(e,"Error while closing the ResultSet returned from getColumns("+catalog+","+schema+","+tableName+"\""+WILDCARD_PATTERN+"\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                    }
                }
            }
        }
    }
    
    protected TableInfo getTableInfo( final ResultSet resultSet ) throws SQLException {
        final String catalogName = resultSet.getString(1);
        final String schemaName  = resultSet.getString(2);
        final String tableName   = resultSet.getString(3);
        final String tableType   = resultSet.getString(4);
        final String remarks     = resultSet.getString(5);
        return new TableInfo(catalogName,schemaName,tableName,tableType,remarks);
    }
    
    protected ColumnInfo getColumnInfo( final ResultSet resultSet ) throws SQLException {
        final String catalogName    = resultSet.getString(1);
        final String schemaName     = resultSet.getString(2);
        final String tableName      = resultSet.getString(3);
        final String columnName     = resultSet.getString(4);
        final short  datatype       = resultSet.getShort(5);
        final String typeName       = resultSet.getString(6);
        final int    size           = resultSet.getInt(7);
        final int    bufferLength   = resultSet.getInt(8);// not used
        final int    decimalDigits  = resultSet.getInt(9);
        final int    numPrecRadix   = resultSet.getInt(10);
        final int    nullable       = resultSet.getInt(11);
        final String remarks        = resultSet.getString(12);
        final String defaultValue   = resultSet.getString(13);
        final int    sqlDataType    = resultSet.getInt(14);// not used
        final int    sqlDatetimeSub = resultSet.getInt(15);// not used
        final int    charOctetLength= resultSet.getInt(16);
        final int    position       = resultSet.getInt(17);
        final String isNullable     = resultSet.getString(18);

        // Use the variables ...
        if ( catalogName != null && schemaName != null && tableName != null ) {
            // do nothing ...
        }
        return new ColumnInfo(columnName,datatype,typeName,size,bufferLength,decimalDigits,numPrecRadix,
                              nullable,remarks,defaultValue,sqlDataType,sqlDatetimeSub,charOctetLength,
                              position,isNullable);
    }
    
    //-------------------------------------------------------------------------
    //                   Utility methods
    //-------------------------------------------------------------------------
    
    protected void printResults(PrintStream outStream) {
        outStream.println("----------- METADATA ---------------"); //$NON-NLS-1$
        if (DEBUG) {
            outStream.println("  Catalogs: "); //$NON-NLS-1$
            final Iterator catalogIter = this.catalogs.iterator();
            while (catalogIter.hasNext()) {
                outStream.println("    - " + catalogIter.next().toString()); //$NON-NLS-1$
            }
            outStream.println("  Schemas: "); //$NON-NLS-1$
            final Iterator schemaIter = this.schemas.iterator();
            while (schemaIter.hasNext()) {
                outStream.println("    - " + schemaIter.next().toString()); //$NON-NLS-1$
            }
        } // if
        outStream.println("  # Tables loaded with non-restrictive getTables: " + this.allTableInfos.size() ); //$NON-NLS-1$
        outStream.println("  # Tables loaded with restrictive getTables: " + this.tableInfos.size() ); //$NON-NLS-1$
        outStream.println("----------- STATISTICS -------------"); //$NON-NLS-1$
        outStream.println(" getCatalogs: "); //$NON-NLS-1$
        this.stopwatchCatalogs.printStatistics(outStream);
        outStream.println(" getSchemas: "); //$NON-NLS-1$
        this.stopwatchSchemas.printStatistics(outStream);
        outStream.println(" getTables (non-restrictive): "); //$NON-NLS-1$
        this.stopwatchAllTableInfos.printStatistics(outStream);
        outStream.println(" getTables (restrictive): "); //$NON-NLS-1$
        this.stopwatchTableInfos.printStatistics(outStream);
        outStream.println(" getColumns: "); //$NON-NLS-1$
        this.stopwatchColumns.printStatistics(outStream);
        outStream.println(" # Number of non-null and non-trivial remarks on Tables:  " + this.numTableRemarks); //$NON-NLS-1$
        outStream.println(" # Number of non-null and non-trivial remarks on Columns: " + this.numColumnRemarks); //$NON-NLS-1$
    }
    
    protected static void log( final String msg ) {
        if ( DEBUG && msg != null ) {
            logStream.println(msg);
        }
    }
    
    protected static void log( final Throwable t, final String msg ) {
        if ( DEBUG ) {
            if ( msg != null ) {
                logStream.println(msg);
            }
            if ( t != null ) {
                t.printStackTrace(logStream);
            }
        }
    }
    
    /** 
     * @param info
     * @return
     * @since 4.3
     */
    private static String stringifyProps(Properties info) {
        StringBuffer buf = new StringBuffer();
        for ( Iterator itr = info.keySet().iterator(); itr.hasNext(); ) {
            String name = (String)itr.next();
            buf.append(name);
            buf.append(" = "); //$NON-NLS-1$
            buf.append(info.get(name));
            buf.append(';');
        }
        return buf.toString();
    }

    protected static void fail( final Throwable t, final String msg ) {
        if ( msg != null ) {
            System.err.println(msg);
        }
        t.printStackTrace();
    }
    protected static void fail( final Throwable t ) {
        t.printStackTrace();
    }
    
    protected void assertConnected() {
        assertNotNull(this.connection);
        assertNotNull(this.metadata);
    }
    
    protected static void assertTrue( final boolean value, final String msg ) {
        if ( value != true ) {
            throw new IllegalArgumentException(msg);
        }
    }
    protected static void assertNotNull( final Object value ) {
        if ( value == null ) {
            throw new IllegalArgumentException("Unexpected null value"); //$NON-NLS-1$
        }
    }
    protected static void assertNotZeroLength( final String value ) {
        if ( value == null ) {
            throw new IllegalArgumentException("Unexpected null string value"); //$NON-NLS-1$
        }
        if ( value.length() == 0 ) {
            throw new IllegalArgumentException("String may not be empty"); //$NON-NLS-1$
        }
    }
    protected String toString( final String[] stringArray ) {
        final StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (int i = 0; i < stringArray.length; ++i) {
            if ( i!=0 ) {
                sb.append(',');
            }
            String str = stringArray[i];
            sb.append(str);
        }
        sb.append(']');
        return sb.toString();
    }
    
    //-------------------------------------------------------------------------
    //                   Utility classes
    //-------------------------------------------------------------------------
    
    protected class TableInfo {
        public final String catalog;
        public final String schema;
        public final String name;
        public final String tableType;
        public final String remarks;
        public final List columnInfos;
        public TableInfo( final String catalog, final String schema, final String name, final String type,
                          final String remarks ) {
            this.catalog = catalog;
            this.schema = schema;
            this.name = name;
            this.tableType = type;
            this.remarks = remarks;
            this.columnInfos = new LinkedList();
        }
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            if ( catalog != null && catalog.trim().length() != 0 ) {
                sb.append(catalog);
                sb.append(DELIMITER);
            }
            if ( schema != null && schema.trim().length() != 0 ) {
                sb.append(schema);
                sb.append(DELIMITER);
            }
            sb.append(name);
            return sb.toString(); 
        }
    }
    
    protected class ColumnInfo {
        public final String name;
//        public final String catalogName    = resultSet.getString(1);
//        public final String schemaName     = resultSet.getString(2);
//        public final String tableName      = resultSet.getString(3);
        public final short  datatype;
        public final String typeName;
        public final int    size;
        public final int    bufferLength;// not used
        public final int    decimalDigits;
        public final int    numPrecRadix;
        public final int    nullable;
        public final String remarks;
        public final String defaultValue;
        public final int    sqlDataType;// not used
        public final int    sqlDatetimeSub;// not used
        public final int    charOctetLength;
        public final int    position;
        public final String isNullable;
        public ColumnInfo( final String name, final short datatype, final String typeName, final int size,
                           final int bufferLength, final int decimalDigits,final int numPrecRadix,
                           final int nullable, final String remarks, final String defaultValue,
                           final int sqlDataType,final int sqlDatetimeSub,final int charOctetLength,
                           final int position,final String isNullable ) {
            this.name = name;
            this.datatype = datatype;
            this.typeName = typeName;
            this.size = size;
            this.bufferLength = bufferLength;// not used
            this.decimalDigits = decimalDigits;
            this.numPrecRadix = numPrecRadix;
            this.nullable = nullable;
            this.remarks = remarks;
            this.defaultValue = defaultValue;
            this.sqlDataType = sqlDataType;// not used
            this.sqlDatetimeSub = sqlDatetimeSub;// not used
            this.charOctetLength = charOctetLength;
            this.position = position;
            this.isNullable = isNullable;
        }
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(name);
            sb.append(':');
            sb.append(typeName);
            sb.append('(');
            sb.append(size);
            if ( this.decimalDigits != 0 ) {
                sb.append('.');
                sb.append(decimalDigits);
            }
            sb.append(')');
            switch(this.nullable) {
                case DatabaseMetaData.columnNoNulls:
                    sb.append("NOT NULLABLE"); //$NON-NLS-1$
                    break;
                case DatabaseMetaData.columnNullable:
                    sb.append("NULLABLE"); //$NON-NLS-1$
                    break;
                case DatabaseMetaData.columnNullableUnknown:
                    sb.append("NULLABLE UNKNOWN"); //$NON-NLS-1$
                    break;
            }
            sb.append(')');
            return sb.toString(); 
        }
    }
    
    public class Stopwatch implements Serializable {
        /**
         */
        private static final long serialVersionUID = 1L;
        private long start = 0;
        private long stop = 0;
        private Statistics stats = new Statistics();
        private boolean active = true;

        private static final String SECONDS      = "sec"; //$NON-NLS-1$
        private static final String MILLISECONDS = "ms"; //$NON-NLS-1$
        private static final int VALUE_LENGTH = 10;

        /**
         * Return whether the stopwatch is active.  When the stopwatch is active,
         * it is recording the time durations (via <code>start</code> and <code>stop</code>)
         * and will print duration statistics (via <code>printDuration</code>).
         * When the stopwatch is inactive, invoking these methods does nothing
         * but return immediately.
         * @return true if the stopwatch is active, or false if it is inactive.
         */
        public boolean isActive() {
            return active;
        }

        /**
         * Set the stopwatch as inactive.
         * @see isActive
         */
        public void setInactive() {
            active = false;
        }
        /**
         * Set the stopwatch as active.
         * @see isActive
         */
        public void setActive() {
            active = true;
        }

        /**
         * If the stopwatch is active, record the starting time for a time segment.
         * If the stopwatch is inactive, the method returns immediately.
         * @see isActive
         */
        public void start() {
            if ( active ) {
                start = System.currentTimeMillis();
            }
        }
        /**
         * If the stopwatch is active, record the ending time for a time segment.
         * If the stopwatch is inactive, the method returns immediately.
         * @see isActive
         */
        public void stop() {
            if ( active ) {
                stop = System.currentTimeMillis();
                stats.add( stop - start );
            }
        }
        /**
         * Reset the statistics for this stopwatch, regardless of the active state.
         */
        public void reset() {
            start    = 0;
            stop     = 0;
            stats.reset();
        }
        /**
         * Return the total duration recorded, in milliseconds.
         * @return the total number of milliseconds that have been recorded
         */
        public long getTotalDuration() {
            return stats.getTotal();
        }
        /**
         * Return the average duration recorded as a date.
         * @return the number of milliseconds that have been recorded averaged
         * over the number of segments
         */
        public float getAverageDuration() {
            return stats.getAverage();
        }
        /**
         * Return the number of segments that have been recorded.
         * @return the number of segments
         */
        public int getSegmentCount() {
            return stats.getCount();
        }

        @Override
        public String toString() {
            String units = MILLISECONDS;
            StringBuffer valueString = null;
            long value = getTotalDuration();
            if ( value >= 1000 ) {
                float fvalue = value / 1000.0f;
                units = SECONDS;
                valueString = new StringBuffer(Float.toString(fvalue));
            } else {
                valueString = new StringBuffer(Long.toString(value));
            }
            valueString.append(units);
            return valueString.toString();
        }

        public String getTimeValueAsString( long value ) {
            String units = MILLISECONDS;
            StringBuffer valueString = null;
            if ( value >= 1000 ) {
                float fvalue = value / 1000.0f;
                units = SECONDS;
                valueString = new StringBuffer(Float.toString(fvalue));
            } else {
                valueString = new StringBuffer(Long.toString(value));
            }
            while ( valueString.length() < VALUE_LENGTH ) valueString.insert(0,' ');
            return "" + valueString + units; //$NON-NLS-1$
        }
        public String getTimeValueAsString( float value ) {
            String units = MILLISECONDS;
            if ( value >= 1000.0f ) {
                value = value / 1000.0f;
                units = SECONDS;
            }
            StringBuffer valueString = new StringBuffer(Float.toString(value));
            while ( valueString.length() < VALUE_LENGTH ) valueString.insert(0,' ');
            return "" + valueString + units; //$NON-NLS-1$
        }
        public String getValueAsString( int value ) {
            StringBuffer valueString = new StringBuffer(Integer.toString(value));
            while ( valueString.length() < VALUE_LENGTH ) valueString.insert(0,' ');
            return "" + valueString; //$NON-NLS-1$
        }

        /**
         * Print the current statistics
         * @param stream the stream to which the statistics should be printed
         */
        public void printStatistics(java.io.PrintStream stream) {
            if ( active ) {
                stream.println("  Statistics"); //$NON-NLS-1$
                stream.println("    total:    " + this.getTimeValueAsString( stats.getTotal() ) ); //$NON-NLS-1$
                stream.println("    previous: " + this.getTimeValueAsString( stats.getLast() ) ); //$NON-NLS-1$
                stream.println("    count:    " + this.getValueAsString( stats.getCount() ) ); //$NON-NLS-1$
                if ( stats.getCount() > 1 ) {
                    stream.println("    average:  " + this.getTimeValueAsString( stats.getAverage() ) ); //$NON-NLS-1$
                    stream.println("    minimum:  " + this.getTimeValueAsString( stats.getMinimum() ) ); //$NON-NLS-1$
                    stream.println("    maximum:  " + this.getTimeValueAsString( stats.getMaximum() ) ); //$NON-NLS-1$
                }
            }
        }

        /**
         * Print the current statistics to System.out
         */
        public void printStatistics() {
            printStatistics(System.out);
        }


        public class Statistics implements Serializable {
            /**
             */
            private static final long serialVersionUID = 1L;
            private long minimum = 0;
            private long maximum = 0;
            private long last = 0;
            private long total = 0;
            private int count = 0;
            private boolean minimumInitialized = false;

            public long getMinimum() { return minimum; }
            public long getMaximum() { return maximum; }
            public long getLast() { return last; }
            public float getAverage() { return ( (float)total / (float)count ); }
            public long getTotal() { return total; }
            public int getCount() { return count; }
            public void add( long duration ) {
                ++count;
                total += duration;
                last = duration;
                if ( duration > maximum ) {
                    maximum = duration;
                }
                else if ( !minimumInitialized || duration < minimum ) {
                    minimum = duration;
                    minimumInitialized = true;
                }
            }
            public void reset() {
                minimum = 0;
                maximum = 0;
                last = 0;
                total = 0;
                count = 0;
                minimumInitialized = false;
            }
        }
    }

    
}
