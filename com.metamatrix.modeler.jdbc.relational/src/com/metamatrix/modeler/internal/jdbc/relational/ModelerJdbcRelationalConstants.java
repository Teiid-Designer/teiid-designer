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
package com.metamatrix.modeler.internal.jdbc.relational;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * @since 4.0
 */
public interface ModelerJdbcRelationalConstants extends com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants {

    /**
     * Contains private constants and utility methods used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelerJdbcRelationalConstants.class);

        static final String BUNDLE_NAME = com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID
                                          + ".i18n"; //$NON-NLS-1$

        /**
         * @since 4.0
         */
        static String getString( final String id ) {
            return Util.getString(I18N_PREFIX + id);
        }
    }

    /**
     * Provides access to the plugin's log, internationalized properties, and debugger.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

    /**
     * Common messages.
     * 
     * @since 4.0
     */
    public static interface Messages {
        String MODEL_NOT_RELATIONAL_MESSAGE = PC.getString("modelNotRelationalMessage"); //$NON-NLS-1$
    }
}
