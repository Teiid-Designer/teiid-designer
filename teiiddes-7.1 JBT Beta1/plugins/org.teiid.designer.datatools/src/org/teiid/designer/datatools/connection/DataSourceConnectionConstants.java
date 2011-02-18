package org.teiid.designer.datatools.connection;

public interface DataSourceConnectionConstants {

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
        String UNKNOWN = "unknown"; //$NON-NLS-1$

        final static String[] JDBC_TRANSLATORS = {INFORMIX, SYBASE, TEIID, POSTGRESQL, ACCESS, HSQL, ORACLE, MYSQL, MYSQL5,
            JDBC_ANSI, JDBC_SIMPLE, SQLSERVER, DB2, H2, DERBY};

        final static String[] ALL_TRANSLATORS = {INFORMIX, SYBASE, TEIID, POSTGRESQL, ACCESS, HSQL, ORACLE, MYSQL, MYSQL5,
            JDBC_ANSI, JDBC_SIMPLE, SQLSERVER, DB2, H2, DERBY, METAMATRIX, SALESFORCE, TERADATA, LOOPBACK, LDAP, FILE, WS};

    }

    public interface DataSource {
        /*
         *  [connector-jdbc-xa, connector-file, connector-ldap, connector-salesforce, connector-jdbc]
         */

        String JDBC = "connector-jdbc"; //$NON-NLS-1$
        String SALESFORCE = "connector-salesforce"; //$NON-NLS-1$ 
        String LDAP = "connector-ldap"; //$NON-NLS-1$ 
        String FILE = "connector-file"; //$NON-NLS-1$ 
        String JDBC_XA = "connector-jdbc-xa"; //$NON-NLS-1$
        String WS = "connector-ws"; //$NON-NLS-1$
        String UNKNOWN = "connector-unknown"; //$NON-NLS-1$
        

        public final static String[] DATA_SOURCES = {JDBC, JDBC_XA, SALESFORCE, LDAP, FILE};
    }
}
