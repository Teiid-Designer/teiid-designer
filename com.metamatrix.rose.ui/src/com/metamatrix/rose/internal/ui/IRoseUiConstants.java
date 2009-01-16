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
package com.metamatrix.rose.internal.ui;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * Constants intended for use only by classes within this plug-in.
 * 
 * @since 4.1
 */
public interface IRoseUiConstants extends com.metamatrix.rose.ui.IRoseUiConstants, FileUtils.Constants {

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.1
     */
    class PC {
        static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

        private static final String ICON_PATH = "icons/full/"; //$NON-NLS-1$

        private static final String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$

        private static final String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$

        private static final String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * 
     * @since 4.1
     */
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

    /** Model unit file extension (mdl). */
    String MODEL_UNIT_EXTENSION = "mdl"; //$NON-NLS-1$

    /** Cat unit file extension (cat). */
    String CAT_UNIT_EXTENSION = "cat"; //$NON-NLS-1$

    /**
     * Collection of file extensions for all importable Rose unit types. Each element in the collection can be used used in dialog
     * file chooser's to filter out resources.
     */
    String[] FILE_EXTENSIONS = new String[] {FILE_NAME_WILDCARD + FILE_EXTENSION_SEPARATOR_CHAR + MODEL_UNIT_EXTENSION,
        FILE_NAME_WILDCARD + FILE_EXTENSION_SEPARATOR_CHAR + CAT_UNIT_EXTENSION};

    /**
     * Keys for images stored in the plug-in's image registry.
     * 
     * @since 4.1
     */
    interface Images {
        /**
         * @since 4.1
         */
        String IMPORT_ICON = PC.WIZBAN + "import.gif"; //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String SHOW_PATH_VARIABLES_ICON = PC.CVIEW16 + "showPathVariables.gif"; //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String CAT_FILE_ICON = PC.OBJ16 + "catFile.gif"; //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String MODEL_FILE_ICON = PC.OBJ16 + "modelFile.gif"; //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String PROBLEMS_VIEW_ICON = PC.CVIEW16 + "problems_view.gif"; //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String CLOSE_EDITOR = PC.CVIEW16 + "closeEditor.gif"; //$NON-NLS-1$

        /**
         * @since 4.1
         */
        String TARGET_MODEL_EDITOR = PC.CVIEW16 + "targetModelEditor.gif"; //$NON-NLS-1$
    }
}
