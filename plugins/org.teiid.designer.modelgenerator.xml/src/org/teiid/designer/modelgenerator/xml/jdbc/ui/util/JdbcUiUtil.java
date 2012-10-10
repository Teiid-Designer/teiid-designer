/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.jdbc.ui.util;

import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.modelgenerator.xml.XmlImporterUiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;


/**
 * @since 8.0
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
