package org.teiid.designer.teiidimporter.ui.panels;

import java.util.Collection;
import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.ui.common.UiConstants.ConnectionProfileIds;

/**
 * Lookup the translator name given the IConnectionProfile
 * @since 8.1
 */
@SuppressWarnings("javadoc")
public class TranslatorHelper implements UiConstants {
	public static final String VENDOR_KEY = "org.eclipse.datatools.connectivity.db.vendor";  //$NON-NLS-1$
	public static final String VERSION_KEY = "org.eclipse.datatools.connectivity.db.version";  //$NON-NLS-1$

	public static final String GOOGLE_SOURCE_PROPERTY_KEY_AUTH_METHOD = "AuthMethod"; //$NON-NLS-1$
	public static final String GOOGLE_SOURCE_PROPERTY_VALUE_AUTH_CLIENT_LOGIN = "ClientLogin"; //$NON-NLS-1$
	public static final String GOOGLE_SOURCE_PROPERTY_VALUE_AUTH_OAUTH2 = "OAuth2"; //$NON-NLS-1$
	public static final String GOOGLE_SOURCE_PROPERTY_KEY_REFRESH_TOKEN = "RefreshToken"; //$NON-NLS-1$
	public static final String GOOGLE_SOURCE_PROPERTY_KEY_USERNAME = "Username"; //$NON-NLS-1$
	public static final String GOOGLE_SOURCE_PROPERTY_KEY_PASSWORD = "Password"; //$NON-NLS-1$
	
    public static final String TEIID_FILE_DRIVER = "teiid-connector-file.rar"; //$NON-NLS-1$
    public static final String TEIID_GOOGLE_DRIVER = "teiid-connector-google.rar"; //$NON-NLS-1$
    public static final String TEIID_INFINISPAN_DRIVER = "teiid-connector-infinispan.rar"; //$NON-NLS-1$
    public static final String TEIID_LDAP_DRIVER = "teiid-connector-ldap.rar"; //$NON-NLS-1$
    public static final String TEIID_SALESORCE_DRIVER = "teiid-connector-salesforce.rar"; //$NON-NLS-1$
    public static final String TEIID_WEBSERVICE_DRIVER = "teiid-connector-ws.rar"; //$NON-NLS-1$
    public static final String TEIID_FILE_DRIVER_84UP = "file"; //$NON-NLS-1$
    public static final String TEIID_GOOGLE_DRIVER_84UP = "google"; //$NON-NLS-1$
    public static final String TEIID_INFINISPAN_DRIVER_84UP = "infinispan"; //$NON-NLS-1$
    public static final String TEIID_LDAP_DRIVER_84UP = "ldap"; //$NON-NLS-1$
    public static final String TEIID_SALESORCE_DRIVER_84UP = "salesforce"; //$NON-NLS-1$
    public static final String TEIID_WEBSERVICE_DRIVER_84UP = "webservice"; //$NON-NLS-1$
    public static final String TEIID_MONGODB_DRIVER_84UP = "mongodb"; //$NON-NLS-1$
    public static final String TEIID_FILE_DRIVER_DISPLAYNAME = "FlatFile"; //$NON-NLS-1$
    public static final String TEIID_GOOGLE_DRIVER_DISPLAYNAME = "Google"; //$NON-NLS-1$
    public static final String TEIID_INFINISPAN_DRIVER_DISPLAYNAME = "Infinispan"; //$NON-NLS-1$
    public static final String TEIID_LDAP_DRIVER_DISPLAYNAME = "LDAP"; //$NON-NLS-1$
    public static final String TEIID_SALESORCE_DRIVER_DISPLAYNAME = "Salesforce"; //$NON-NLS-1$
    public static final String TEIID_WEBSERVICE_DRIVER_DISPLAYNAME = "WebService"; //$NON-NLS-1$

    public static final String TEIID_GOOGLE_CLASS = "org.teiid.resource.adapter.google.SpreadsheetManagedConnectionFactory"; //$NON-NLS-1$
    
    public static final String ACCESS = "access"; //$NON-NLS-1$
    public static final String DB2 = "db2"; //$NON-NLS-1$
    public static final String DERBY = "derby"; //$NON-NLS-1$
    public static final String FILE = "file"; //$NON-NLS-1$
    public static final String GOOGLE_SPREADSHEET = "google-spreadsheet"; //$NON-NLS-1$
    public static final String H2 = "h2"; //$NON-NLS-1$
    public static final String HIVE = "hive"; //$NON-NLS-1$
    public static final String HSQL = "hsql"; //$NON-NLS-1$
    public static final String INFINISPAN = "infinispan-cache"; //$NON-NLS-1$
	public static final String INFORMIX = "informix"; //$NON-NLS-1$
    public static final String INGRES = "ingres"; //$NON-NLS-1$
    public static final String INGRES93 = "ingres93"; //$NON-NLS-1$
    public static final String INTERSYSTEMS_CACHE = "intersystems-cache"; //$NON-NLS-1$
    public static final String JDBC_ANSI = "jdbc-ansi"; //$NON-NLS-1$
    public static final String JDBC_SIMPLE = "jdbc-simple"; //$NON-NLS-1$
    public static final String JPA2 = "jpa2"; //$NON-NLS-1$
    public static final String LDAP = "ldap"; //$NON-NLS-1$
    public static final String LOOPBACK = "loopback"; //$NON-NLS-1$
    public static final String MAP_CACHE = "map-cache"; //$NON-NLS-1$
    public static final String METAMATRIX = "metamatrix"; //$NON-NLS-1$
    public static final String MODESHAPE = "modeshape"; //$NON-NLS-1$
    public static final String MONGODB = "mongodb"; //$NON-NLS-1$
    public static final String MYSQL = "mysql"; //$NON-NLS-1$
    public static final String MYSQL5 = "mysql5"; //$NON-NLS-1$
    public static final String NETEZZA = "netezza"; //$NON-NLS-1$
    public static final String OLAP = "olap"; //$NON-NLS-1$
    public static final String ORACLE = "oracle"; //$NON-NLS-1$
	// NOTE: For PostgreSQL vendor leaves off the QL, so we need to be careful to map this correctly
	public static final String POSTGRES = "postgres"; //$NON-NLS-1$
	public static final String POSTGRESQL = "postgresql"; //$NON-NLS-1$
    public static final String SALESFORCE = "salesforce"; //$NON-NLS-1$
	public static final String SQLSERVER = "sqlserver"; //$NON-NLS-1$
    public static final String SYBASE = "sybase"; //$NON-NLS-1$
    public static final String TEIID = "teiid"; //$NON-NLS-1$
	public static final String TERADATA = "teradata"; //$NON-NLS-1$
	public static final String WS = "ws"; //$NON-NLS-1$
	
	public static final String URL_DB2 = "jdbc:db2://<host>:50000/<dbName>"; //$NON-NLS-1$
    public static final String URL_DERBY = "jdbc:derby://<host>:1527/<dbName>;create=true"; //$NON-NLS-1$
    public static final String URL_INFORMIX = "jdbc:informix-sqli://<host>:1526/<dbName>:INFORMIXSERVER=server"; //$NON-NLS-1$
    public static final String URL_INGRES = "jdbc:ingres://<host>:117/<dbName>"; //$NON-NLS-1$
    public static final String URL_MODESHAPE = "jdbc:jcr:http://<host>:8080/modeshape-rest/"; //$NON-NLS-1$
    public static final String URL_MYSQL = "jdbc:mysql://<host>:3306/<dbName>"; //$NON-NLS-1$
    public static final String URL_ORACLETHIN = "jdbc:oracle:thin:@<host>:1521:<dbName>"; //$NON-NLS-1$
    public static final String URL_POSTGRES = "jdbc:postgresql://<host>:5432/<dbName>"; //$NON-NLS-1$
    public static final String URL_SQLSERVER = "jdbc:sqlserver://<host>:1433;databaseName=<dbName>"; //$NON-NLS-1$
    public static final String URL_TEIID = "jdbc:teiid:<vdbName>@mms://<host>:31000"; //$NON-NLS-1$
    public static final String URL_JDBC = "jdbc://<host>:<port>"; //$NON-NLS-1$
    public static final String URL_SAP_HANA = "jdbc:sap://<host>:3<instance number>15"; //$NON-NLS-1$
	
    /**
     * Get the best fit translator, given the driverName and list of translator names
     * @param driverName the driver name
     * @param translatorNames the list of current translators
     * @param teiidVersion the Teiid Version
     * @return the best fit translator for the provided driver
     */
	public static String getTranslator(String driverName, Collection<String> translatorNames, ITeiidServerVersion teiidVersion) {
	    CoreArgCheck.isNotEmpty(driverName,"driverName is Empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(translatorNames,"translatorNames is Empty"); //$NON-NLS-1$
	    
        boolean isTeiid84OrHigher = isTeiid84OrHigher(teiidVersion);
        
        if(!isTeiid84OrHigher) {
        	if(driverName.equals(TEIID_FILE_DRIVER) && translatorNames.contains(FILE)) {
        		return FILE;
        	}

        	if(driverName.equals(TEIID_GOOGLE_DRIVER) && translatorNames.contains(GOOGLE_SPREADSHEET)) {
        		return GOOGLE_SPREADSHEET;
        	}

        	if(driverName.equals(TEIID_INFINISPAN_DRIVER) && translatorNames.contains(INFINISPAN)) {
        		return INFINISPAN;
        	}

        	if(driverName.equals(TEIID_LDAP_DRIVER) && translatorNames.contains(LDAP)) {
        		return LDAP;
        	}

        	if(driverName.equals(TEIID_SALESORCE_DRIVER) && translatorNames.contains(SALESFORCE)) {
        		return SALESFORCE;
        	}

        	if(driverName.equals(TEIID_WEBSERVICE_DRIVER) && translatorNames.contains(WS)) {
        		return WS;
        	}
        } else {
        	if(driverName.equals(TEIID_FILE_DRIVER_84UP) && translatorNames.contains(FILE)) {
        		return FILE;
        	}

        	if(driverName.equals(TEIID_GOOGLE_DRIVER_84UP) && translatorNames.contains(GOOGLE_SPREADSHEET)) {
        		return GOOGLE_SPREADSHEET;
        	}

        	if(driverName.equals(TEIID_INFINISPAN_DRIVER_84UP) && translatorNames.contains(INFINISPAN)) {
        		return INFINISPAN;
        	}

        	if(driverName.equals(TEIID_LDAP_DRIVER_84UP) && translatorNames.contains(LDAP)) {
        		return LDAP;
        	}

        	if(driverName.equals(TEIID_SALESORCE_DRIVER_84UP) && translatorNames.contains(SALESFORCE)) {
        		return SALESFORCE;
        	}

        	if(driverName.equals(TEIID_WEBSERVICE_DRIVER_84UP) && translatorNames.contains(WS)) {
        		return WS;
        	}
        	
        	if(driverName.equals(TEIID_MONGODB_DRIVER_84UP) && translatorNames.contains(MONGODB)) {
        		return MONGODB;
        	}
        }
        
        // Attempt name matching to guess any drivers that are not built in.
        String driverNameLC = driverName.toLowerCase();
        if(driverNameLC.startsWith("derby")) { //$NON-NLS-1$
            return DERBY;
        }
        
        if(driverNameLC.startsWith("mysql")) { //$NON-NLS-1$
            return MYSQL;
        }

        if(driverNameLC.startsWith("ojdbc")) { //$NON-NLS-1$
            return ORACLE;
        }
        
        if(driverNameLC.startsWith("db2")) { //$NON-NLS-1$
            return DB2;
        }
        
        if(driverNameLC.startsWith("postgresql")) { //$NON-NLS-1$
            return POSTGRESQL;
        }

        if(driverNameLC.startsWith("sqljdbc")) { //$NON-NLS-1$
            return SQLSERVER;
        }
        
        if(driverNameLC.startsWith("teiid")) { //$NON-NLS-1$
            return TEIID;
        }
        
        if(driverNameLC.startsWith("modeshape")) { //$NON-NLS-1$
            return MODESHAPE;
        }
        
        if(driverNameLC.startsWith("ifxjdbc")) { //$NON-NLS-1$
            return INFORMIX;
        }
        
        if(driverNameLC.startsWith("iijdbc")) { //$NON-NLS-1$
            return INGRES;
        }
        
        if(driverNameLC.startsWith("jconn")) { //$NON-NLS-1$
            return SYBASE;
        }
        
        if(driverNameLC.contains("hive")) { //$NON-NLS-1$
            return HIVE;
        }

        return JDBC_ANSI; 
	}
	
	/**
	 * Test whether the version is 8.4 or higher.  (.rar deployments were changed in 8.4)
	 * We assume false, and may not be able to determine - if wildcards are used.
	 * @param teiidVersion the Teiid Instance version
	 * @return 'true' if the version is 8.4 or higher, 'false' if lower or unknown
	 */
	public static boolean isTeiid84OrHigher(ITeiidServerVersion teiidVersion) {
        CoreArgCheck.isNotNull(teiidVersion,"teiidVersion is null"); //$NON-NLS-1$
		try {
			// Seven or lower
			String major = teiidVersion.getMajor();
			if(Integer.parseInt(major) <= 7) {
				return false;
			}

			// Nine or higher
			if(Integer.parseInt(major) >= 9) {
				return true;
			}

			// Version 8 - check minor version
			String minor = teiidVersion.getMinor();
			if(Integer.parseInt(minor) >=4 ) {
				return true;
			}
		} catch (Exception e) {
            UTIL.log(e);
			return false;
		}
		return false;
	}
	
	/**
	 * Get the URL Template given a driver name
	 * @param driverName the driver name
	 * @return the URL Template
	 */
	public static String getUrlTemplate(String driverName) {
        CoreArgCheck.isNotEmpty(driverName,"driverName is Empty"); //$NON-NLS-1$
        
        if(driverName.startsWith("derby")) { //$NON-NLS-1$
            return URL_DERBY;
        }
        
        if(driverName.startsWith("mysql")) { //$NON-NLS-1$
            return URL_MYSQL;
        }

        if(driverName.startsWith("ojdbc")) { //$NON-NLS-1$
            return URL_ORACLETHIN;
        }
        
        if(driverName.startsWith("db2")) { //$NON-NLS-1$
            return URL_DB2;
        }
        
        if(driverName.startsWith("postgresql")) { //$NON-NLS-1$
            return URL_POSTGRES;
        }

        if(driverName.startsWith("sqljdbc")) { //$NON-NLS-1$
            return URL_SQLSERVER;
        }
        
        if(driverName.startsWith("ifxjdbc")) { //$NON-NLS-1$
            return URL_INFORMIX;
        }
        
        if(driverName.startsWith("iijdbc")) { //$NON-NLS-1$
            return URL_INGRES;
        }
        
        if(driverName.startsWith("teiid")) { //$NON-NLS-1$
            return URL_TEIID;
        }
        
        if(driverName.startsWith("modeshape")) { //$NON-NLS-1$
            return URL_MODESHAPE;
        }
        
        
        if( driverName.startsWith("sap") || driverName.contains("ngdbc")) {  //$NON-NLS-1$ //$NON-NLS-2$
        	return URL_SAP_HANA;
        }

        return URL_JDBC; 
	}
	
	/**
     * Returns the JDBC Simple translator if a vendor-specific one cannot be determined.
     * 
     * @param connectionProfile the {@link IConnectionProfile} whose translator is being requested
     * @return the translator name (never <code>null</code>)
     */
	public static String getTranslator(IConnectionProfile connectionProfile) {
		Properties baseProps = connectionProfile.getBaseProperties();
		
		String providerId = connectionProfile.getProviderId();
		if(providerId!=null) {
		    if(providerId.equalsIgnoreCase(ConnectionProfileIds.CATEGORY_SALESFORCE_CONNECTION)) {
		        return SALESFORCE;
		    } else if(providerId.equalsIgnoreCase(ConnectionProfileIds.CATEGORY_LDAP_CONNECTION)) {
		        return LDAP;
		    }
		}
		
		String vendor = baseProps.getProperty(VENDOR_KEY);
		String version = baseProps.getProperty(VERSION_KEY);
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
			
			if( vendor.toUpperCase().startsWith(POSTGRES.toUpperCase()) ) {
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
	
}
