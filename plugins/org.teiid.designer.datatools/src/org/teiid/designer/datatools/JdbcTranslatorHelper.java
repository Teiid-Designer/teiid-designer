package org.teiid.designer.datatools;

import org.eclipse.datatools.connectivity.IConnectionProfile;

public class JdbcTranslatorHelper {
	public static final String VENDOR_KEY = "org.eclipse.datatools.connectivity.db.vendor";  //$NON-NLS-1$
	public static final String VERSION_KEY = "org.eclipse.datatools.connectivity.db.version";  //$NON-NLS-1$
	
	public static final String INFORMIX = "informix"; //$NON-NLS-1$
	public static final String SYBASE = "sybase"; //$NON-NLS-1$
	public static final String TEIID = "teiid"; //$NON-NLS-1$
	public static final String POSTGRESQL = "postgresql"; //$NON-NLS-1$
	public static final String ACCESS = "access"; //$NON-NLS-1$
	public static final String HSQL = "hsql"; //$NON-NLS-1$
	public static final String ORACLE = "oracle"; //$NON-NLS-1$
	public static final String MYSQL5 = "mysql5"; //$NON-NLS-1$
	public static final String JDBC_SIMPLE = "jdbc-simple"; //$NON-NLS-1$
	public static final String JDBC_ANSI = "jdbc-ansi"; //$NON-NLS-1$
	public static final String SQLSERVER = "sqlserver"; //$NON-NLS-1$
	public static final String DB2 = "db2"; //$NON-NLS-1$
	public static final String MYSQL = "mysql"; //$NON-NLS-1$
	public static final String H2 = "h2"; //$NON-NLS-1$
	public static final String DERBY = "derby"; //$NON-NLS-1$


    /**
     * Returns the JDBC Simple translator if a vendor-specific one cannot be determined.
     * 
     * @param connectionProfile the {@link IConnectionProfile} whose translator is being requested
     * @return the translator name (never <code>null</code>)
     */
	public static String getTranslator(IConnectionProfile connectionProfile) {
		
		String vendor = connectionProfile.getBaseProperties().getProperty(VENDOR_KEY);
		String version = connectionProfile.getBaseProperties().getProperty(VERSION_KEY);
		// Assume that vendor has to be a property
		if( vendor != null ) {
			if( vendor.toUpperCase().startsWith(INFORMIX.toUpperCase()) ) {
				return INFORMIX;
			}
			
			if( vendor.toUpperCase().startsWith(ORACLE.toUpperCase()) ) {
				return ORACLE;
			}
			
			if( vendor.toUpperCase().startsWith("SQL SERVER".toUpperCase()) ) { //$NON-NLS-1$
				return SQLSERVER;
			}
			
			if( vendor.toUpperCase().startsWith(DERBY.toUpperCase()) ) {
				return DERBY;
			}
			
			if( vendor.toUpperCase().startsWith(POSTGRESQL.toUpperCase()) ) {
				return POSTGRESQL;
			}
			
			if( vendor.toUpperCase().startsWith(SYBASE.toUpperCase()) ) {
				return SYBASE;
			}
			
			if( vendor.toUpperCase().startsWith(TEIID.toUpperCase()) ) {
				return TEIID;
			}
			
			if( vendor.toUpperCase().startsWith(DB2.toUpperCase()) ) {
				return DB2;
			}
			
			if( vendor.toUpperCase().startsWith(MYSQL.toUpperCase()) ) {
				if( version != null && version.startsWith("5")) { //$NON-NLS-1$
					return MYSQL5;
				}
				return MYSQL;
			}
		}
		
		return JDBC_SIMPLE;
	}
	
	/*
	 * Strings taken from DTP 
	 * 
	 * 		DB2 for
			derby
			informix
			ingres
			"MaxDB"
			MySQL (version 4.0, 4.1, 5.0, 5.1)
			oracle
			PostgreSQL (8.x)
			SQL Server
			SQLite
			Sybase Adaptive Server Anywhere
			Sybase Adaptive Server Enterprise
			Teiid
	
	 */
}
