/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin.v9;

public class AdminUtil {
	
	public enum DataSourceType {
	    DATA_SOURCE,
	    XA_DATA_SOURCE,
	    RESOURCE_ADAPTER;
	}
	
	public static String JAVA_PREFIX = "java:/";
	
    interface Translators {

        String INFORMIX = "informix"; //$NON-NLS-1$
        String SYBASE = "sybase"; //$NON-NLS-1$
        String TEIID = "teiid"; //$NON-NLS-1$
        String POSTGRESQL = "postgresql"; //$NON-NLS-1$
        String ACCESS = "access"; //$NON-NLS-1$
        String HSQL = "hsql"; //$NON-NLS-1$
        String ORACLE = "oracle"; //$NON-NLS-1$
        String MYSQL5 = "mysql5"; //$NON-NLS-1$
        String JDBC_SIMPLE = "jdbc-simple"; //$NON-NLS-1$
        String JDBC_ANSI = "jdbc-ansi"; //$NON-NLS-1$
        String SQLSERVER = "sqlserver"; //$NON-NLS-1$
        String DB2 = "db2"; //$NON-NLS-1$
        String MYSQL = "mysql"; //$NON-NLS-1$
        String H2 = "h2"; //$NON-NLS-1$
        String DERBY = "derby"; //$NON-NLS-1$
        String METAMATRIX = "metamatrix"; //$NON-NLS-1$
        String SALESFORCE = "salesforce"; //$NON-NLS-1$
        String TERADATA = "teradata"; //$NON-NLS-1$
        String LOOPBACK = "loopback"; //$NON-NLS-1$
        String LDAP = "ldap"; //$NON-NLS-1$
        String FILE = "file"; //$NON-NLS-1$
        String WS = "ws"; //$NON-NLS-1$
        String ODATA = "odata"; //$NON-NLS-1$
        String UNKNOWN = "unknown"; //$NON-NLS-1$

        final static String[] JDBC_TRANSLATORS = {INFORMIX, SYBASE, TEIID, POSTGRESQL, ACCESS, HSQL, ORACLE, MYSQL, MYSQL5,
            JDBC_ANSI, JDBC_SIMPLE, SQLSERVER, DB2, H2, DERBY};

        final static String[] ALL_TRANSLATORS = {INFORMIX, SYBASE, TEIID, POSTGRESQL, ACCESS, HSQL, ORACLE, MYSQL, MYSQL5,
            JDBC_ANSI, JDBC_SIMPLE, SQLSERVER, DB2, H2, DERBY, METAMATRIX, SALESFORCE, TERADATA, LOOPBACK, LDAP, FILE, WS, ODATA};

    }
	
    public static String addJavaPrefix(String name) {
    	if( !name.startsWith(JAVA_PREFIX) ) {
    		return JAVA_PREFIX + name;
    	}
    	
    	return name;
    }
    
	public static String removeJavaPrefix(String nameWithPrefix) {
		if (nameWithPrefix.startsWith(JAVA_PREFIX)) {
			nameWithPrefix = nameWithPrefix.substring(6);
		}
		return nameWithPrefix;
	}
	
	public static boolean excludeProperty(String name) {
		String[] names = { "jndi-name",
				"pool-name",
				"driver-name",
				"reauth-plugin-class-name", "enabled",
				"valid-connection-checker-class-name",
				"valid-connection-checker-properties",
				"stale-connection-checker-class-name",
				"stale-connection-checker-properties",
				"exception-sorter-class-name",
				"exception-sorter-properties",
				"use-try-lock",
				"allocation-retry",
				"allocation-retry-wait-millis",
				"jta",
				"use-java-context",
				"url-selector-strategy-class-name",
				"driver-class",
				"datasource-class",
				"use-ccm"};
		for (String n:names) {
			if (n.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasJavaPrefix(String jndiName) {
		return jndiName.startsWith(JAVA_PREFIX);
	}
	
	public static boolean isJdbcTranslator(String type) {
		for( String jdbcType : Translators.JDBC_TRANSLATORS) {
			if( jdbcType.equalsIgnoreCase(type)) {
				return true;
			}
		}
		
		return false;
	}

}
