/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.jdbc.ui.util;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 4.0
 */
public final class JdbcUiUtil
{
    //============================================================================================================================
    // Constants
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcUiUtil.class);
        
    private static PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();
    private static final String ERROR_ACCESSING_DATABASE_MESSAGE = util.getString(I18N_PREFIX + "errorAccessingDatabaseMessage"); //$NON-NLS-1$
    
    
    //============================================================================================================================
	// Static Methods
        
    /**<p>
	 * </p>
	 * @since 4.0
	 */
	public static void showAccessError(final Throwable error) {
        util.log(error);
		WidgetUtil.showCause(ERROR_ACCESSING_DATABASE_MESSAGE, error);
	}
    
    /**<p>
	 * </p>
	 * @since 4.0
	 */
	public static void showError(final Throwable error, final String message) {
        util.log(error);
        WidgetUtil.showError(message);
	}
}
