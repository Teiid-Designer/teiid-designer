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
    
}
