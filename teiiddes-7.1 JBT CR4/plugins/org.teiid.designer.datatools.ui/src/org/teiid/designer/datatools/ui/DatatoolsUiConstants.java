package org.teiid.designer.datatools.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;



public interface DatatoolsUiConstants {    

	// The plug-in ID
	public static final String PLUGIN_ID = "org.teiid.designer.datatools.ui"; //$NON-NLS-1$

    /**
     * The package identifier.
     */
    public static final String PACKAGE_ID = DatatoolsUiPlugin.class.getPackage().getName();
    
    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * 
     * @since 4.3
     */
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));
	
	/**
	 * Private constants used by other constants within this class.
	 * 
	 * @since 4.3
	 */
	class PC {
	    public static final String I18N_NAME = DatatoolsUiPlugin.PACKAGE_ID + ".i18n"; //$NON-NLS-1$
	
	    public static final String ICON_PATH = "icons/full/"; //$NON-NLS-1$
	
	    public static final String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$
	
	    public static final String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$
	
	    public static final String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$
	
	    public static final String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
	}
	
	interface Images {
		public static final String SOURCE_BINDING_ICON = PC.OBJ16 + "sourceBinding.gif"; //$NON-NLS-1$
	}
}
