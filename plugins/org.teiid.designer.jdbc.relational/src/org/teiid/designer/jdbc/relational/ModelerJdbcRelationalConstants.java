/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational;

import java.util.ResourceBundle;

import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.PluginUtilImpl;


/**<p>
 * </p>
 * @since 8.0
 */
public interface ModelerJdbcRelationalConstants {
    //============================================================================================================================
    // Constants
	

	/**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    
    String PLUGIN_ID = "org.teiid.designer.jdbc.relational"; //$NON-NLS-1$
    
    String PACKAGE_ID = ModelerJdbcRelationalConstants.class.getPackage().getName();
    
    /**
	 * Contains private constants and utility methods used by other constants within this class.
	 * 
	 * @since 4.0
	 */
	class PC {
	    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelerJdbcRelationalConstants.class);
	
	    public static final String I18N_NAME = PACKAGE_ID
	                                      + ".i18n"; //$NON-NLS-1$
	
	    /**
	     * @since 4.0
	     */
	    static String getString( final String id ) {
	        return Util.getString(I18N_PREFIX + id);
	    }
	}

	/**
	 * Common messages.
	 * 
	 * @since 4.0
	 */
	public static interface Messages {
	    String MODEL_NOT_RELATIONAL_MESSAGE = PC.getString("modelNotRelationalMessage"); //$NON-NLS-1$
	}


	/**
	 * Provides access to the plugin's log, internationalized properties, and debugger.
	 * 
	 * @since 4.0
	 */
	PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));
	
	interface Processors {
		String JDBC = "jdbc"; //$NON-NLS-1$
		String ORACLE = "oracle"; //$NON-NLS-1$
		String SQLSERVER = "sqlserver"; //$NON-NLS-1$
		String SYBASE = "Sybase"; //$NON-NLS-1$
		String DB2 = "DB2"; //$NON-NLS-1$
		String POSTGRES = "postgres"; //$NON-NLS-1$
		String ODBC = "ODBC"; //$NON-NLS-1$
		String MODESHAPE = "modeshape"; //$NON-NLS-1$
		String TEIID = "teiid"; //$NON-NLS-1$
		String EXCEL = "excel"; //$NON-NLS-1$
	}
	
	/**
	 * SQL defines distinct data types named by the following <key word>s:
		CHARACTER, CHARACTER VARYING, BIT, BIT VARYING, NUMERIC, DECIMAL,
		INTEGER, SMALLINT, FLOAT, REAL, DOUBLE PRECISION, DATE, TIME,
		TIMESTAMP, and INTERVAL.
	 *
	 */
	interface SQL_92_DATATYPES {
		String CHARACTER = "CHARACTER"; //$NON-NLS-1$
		String CHARACTER_VARYING = "CHARACTER VARYING"; //$NON-NLS-1$
		String BIT = "BIT"; //$NON-NLS-1$
		String BIT_VARYING = "BIT VARYING"; //$NON-NLS-1$
		String NUMERIC = "NUMERIC"; //$NON-NLS-1$
		String DECIMAL = "DECIMAL"; //$NON-NLS-1$
		String INTEGER = "INTEGER"; //$NON-NLS-1$
		String SMALLINT = "SMALLINT"; //$NON-NLS-1$
		String FLOAT = "FLOAT"; //$NON-NLS-1$
		String REAL = "REAL"; //$NON-NLS-1$
		String DOUBLE_PRECISION = "DOUBLE PRECISION"; //$NON-NLS-1$
		String DATE = "DATE"; //$NON-NLS-1$
		String TIME = "TIME"; //$NON-NLS-1$
		String TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
		String INTERVAL = "INTERVAL"; //$NON-NLS-1$
	}
    
}
