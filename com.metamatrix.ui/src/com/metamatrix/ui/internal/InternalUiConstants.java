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
package com.metamatrix.ui.internal;

import java.util.ResourceBundle;
import org.eclipse.jface.dialogs.IDialogConstants;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.UiConstants;

/**
 * PluginConstants
 * 
 * @since 4.0
 */
public interface InternalUiConstants extends UiConstants {

    /**
     * Contains private constants used by other constants within this interface.
     * 
     * @since 4.0
     */
    class PC {
        private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log, internationalized properties, and debugger.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

    /**
     * ResourceBundle action property keys will be formed by concatenating the action identifier, delimiter, and property
     * identifier.
     */
    interface Actions {
        /** The delimiter between the action ID and the property. */
        String DELIMITER = "."; //$NON-NLS-1$

        String ACCELERATOR = "accelerator"; //$NON-NLS-1$
        String DESCRIPTION = "description"; //$NON-NLS-1$
        String DISABLED_IMAGE = "disabledImage"; //$NON-NLS-1$
        String HELP = "helpId"; //$NON-NLS-1$
        String HOVER_IMAGE = "hoverImage"; //$NON-NLS-1$
        String IMAGE = "image"; //$NON-NLS-1$
        String TEXT = "text"; //$NON-NLS-1$
        String TOOLTIP = "toolTip"; //$NON-NLS-1$
    }

    /**
     * Contains debug contexts defined by this plug-in.
     * 
     * @since 4.0
     */
    interface Debug {
        interface Metrics {
            // debug setting that should be used only by aspects
            String ACTION_RUN = "metricsTimingActionRun"; //$NON-NLS-1$

            // debug setting that should be used only by aspects
            String ACTION_SELECTION = "metricsTimingActionSelection"; //$NON-NLS-1$

            String NOTIFICATIONS = "metricsTimingNotifications"; //$NON-NLS-1$
            String CONSTRUCTOR = "metricsTimingConstructor"; //$NON-NLS-1$
            String METHOD = "metricsTimingMethod"; //$NON-NLS-1$
        }
    }

    /**
     * Contains widget constants.
     * 
     * @since 4.0
     */
    interface Widgets extends IDialogConstants {
        /**
         * Contains private constants used by other constants within this interface.
         * 
         * @since 4.0
         */
        class PC {
            private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(Widgets.class);

            static String getString( final String id ) {
                return Util.getString(I18N_PREFIX + id);
            }

            static String getString( final String id,
                                     final Object parameter ) {
                return Util.getString(I18N_PREFIX + id, parameter);
            }

            static String getString( final String id,
                                     final Object parameter1,
                                     final Object parameter2 ) {
                return Util.getString(I18N_PREFIX + id, parameter1, parameter2);
            }
        }

        int IMAGE_ICON_GAP = 5;

        String OUR_NEXT_LABEL = PC.getString("nextLabel"); //$NON-NLS-1$
        String OUR_FINISH_LABEL = PC.getString("finishLabel"); //$NON-NLS-1$

        String ADD_BUTTON = PC.getString("addButton"); //$NON-NLS-1$
        String BROWSE_BUTTON = PC.getString("browseButton"); //$NON-NLS-1$
        String DESELECT_ALL_BUTTON = PC.getString("deselectAllButton"); //$NON-NLS-1$
        String DOWN_BUTTON = PC.getString("downButton"); //$NON-NLS-1$
        String EDIT_BUTTON = PC.getString("editButton"); //$NON-NLS-1$
        String REMOVE_BUTTON = PC.getString("removeButton"); //$NON-NLS-1$
        String SELECT_ALL_BUTTON = PC.getString("selectAllButton"); //$NON-NLS-1$
        String UP_BUTTON = PC.getString("upButton"); //$NON-NLS-1$

        String PASSWORD_LABEL = PC.getString("passwordLabel"); //$NON-NLS-1$

        String CONFIRM_MESSAGE_TITLE = PC.getString("confirmMessageTitle"); //$NON-NLS-1$
        String ERROR_MESSAGE_TITLE = PC.getString("errorMessageTitle"); //$NON-NLS-1$
        String NOTIFICATION_MESSAGE_TITLE = PC.getString("notificationMessageTitle"); //$NON-NLS-1$
        String WARNING_MESSAGE_TITLE = PC.getString("warningMessageTitle"); //$NON-NLS-1$

        String INVALID_FILE_MESSAGE = PC.getString("invalidFileMessage"); //$NON-NLS-1$
        String VALID_DIALOG_MESSAGE = PC.getString("validDialogMessage", OK_LABEL); //$NON-NLS-1$
        String VALID_LAST_OR_MIDDLE_PAGE_MESSAGE = PC.getString("validLastOrMiddlePageMessage", //$NON-NLS-1$
                                                                OUR_NEXT_LABEL,
                                                                OUR_FINISH_LABEL);
        String VALID_LAST_PAGE_MESSAGE = PC.getString("validLastPageMessage", OUR_FINISH_LABEL); //$NON-NLS-1$
        String VALID_PAGE_MESSAGE = PC.getString("validPageMessage", OUR_NEXT_LABEL); //$NON-NLS-1$

        String CHECKED_STATE_PROPERTY = "checkedState"; //$NON-NLS-1$

        int UNCHECKED = 1;
        int CHECKED = 1 << 1;
        int PARTIALLY_CHECKED = 1 << 2;
    }
}
