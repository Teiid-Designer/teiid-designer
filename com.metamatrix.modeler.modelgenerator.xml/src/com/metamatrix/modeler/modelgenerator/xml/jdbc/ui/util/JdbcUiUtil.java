/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
