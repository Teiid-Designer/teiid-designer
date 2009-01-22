/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.logview;

import com.metamatrix.ui.UiConstants;


/** 
 * @since 4.3
 */
public class LogViewMessages {
    public static String column_severity = getString("LogView_column_severity"); //$NON-NLS-1$
    public static String column_message = getString("LogView_column_message"); //$NON-NLS-1$
    public static String column_plugin = getString("LogView_column_plugin"); //$NON-NLS-1$
    public static String column_date = getString("LogView_column_date"); //$NON-NLS-1$
    public static String clear_text = getString("LogView_clear"); //$NON-NLS-1$
    public static String clear_tooltip = getString("LogView_clear_tooltip"); //$NON-NLS-1$
    public static String copy_text = getString("LogView_copy"); //$NON-NLS-1$
    public static String delete_text = getString("LogView_delete"); //$NON-NLS-1$
    public static String delete_tooltip = getString("LogView_delete_tooltip"); //$NON-NLS-1$
    public static String export = getString("LogView_export"); //$NON-NLS-1$
    public static String exportLog = getString("LogView_exportLog"); //$NON-NLS-1$
    public static String export_tooltip = getString("LogView_export_tooltip"); //$NON-NLS-1$
    public static String import_text = getString("LogView_import"); //$NON-NLS-1$
    public static String import_tooltip = getString("LogView_import_tooltip"); //$NON-NLS-1$
    public static String filter = getString("LogView_filter"); //$NON-NLS-1$
    public static String readLog_reload = getString("LogView_readLog_reload"); //$NON-NLS-1$
    public static String readLog_restore = getString("LogView_readLog_restore"); //$NON-NLS-1$
    public static String readLog_restore_tooltip = getString("LogView_readLog_restore_tooltip"); //$NON-NLS-1$
    public static String severity_error = getString("LogView_severity_error"); //$NON-NLS-1$
    public static String severity_warning = getString("LogView_severity_warning"); //$NON-NLS-1$
    public static String severity_info = getString("LogView_severity_info"); //$NON-NLS-1$
    public static String severity_ok = getString("LogView_severity_ok"); //$NON-NLS-1$
    public static String confirmDelete_title = getString("LogView_confirmDelete_title"); //$NON-NLS-1$
    public static String confirmDelete_message = getString("LogView_confirmDelete_message"); //$NON-NLS-1$
//    public static String confirmOverwrite_message = getString("LogView_confirmOverwrite_message"); //$NON-NLS-1$
    public static String operation_importing = getString("LogView_operation_importing"); //$NON-NLS-1$
    public static String operation_reloading = getString("LogView_operation_reloading"); //$NON-NLS-1$
    public static String activate = getString("LogView_activate"); //$NON-NLS-1$
    public static String view_currentLog = getString("LogView_view_currentLog"); //$NON-NLS-1$
    public static String view_currentLog_tooltip = getString("LogView_view_currentLog_tooltip"); //$NON-NLS-1$
    public static String properties_tooltip = getString("LogView_properties_tooltip"); //$NON-NLS-1$

    public static String FilterDialog_title = getString("LogView_FilterDialog_title"); //$NON-NLS-1$
    public static String FilterDialog_eventTypes = getString("LogView_FilterDialog_eventTypes"); //$NON-NLS-1$
    public static String FilterDialog_information = getString("LogView_FilterDialog_information"); //$NON-NLS-1$
    public static String FilterDialog_warning = getString("LogView_FilterDialog_warning"); //$NON-NLS-1$
    public static String FilterDialog_error = getString("LogView_FilterDialog_error"); //$NON-NLS-1$
    public static String FilterDialog_limitTo = getString("LogView_FilterDialog_limitTo"); //$NON-NLS-1$
    public static String FilterDialog_eventsLogged = getString("LogView_FilterDialog_eventsLogged"); //$NON-NLS-1$
    public static String FilterDialog_allSessions = getString("LogView_FilterDialog_allSessions"); //$NON-NLS-1$
    public static String FilterDialog_recentSession = getString("LogView_FilterDialog_recentSession"); //$NON-NLS-1$
    
    public static String EventDetailsDialog_title = getString("EventDetailsDialog_title"); //$NON-NLS-1$
    public static String EventDetailsDialog_date = getString("EventDetailsDialog_date"); //$NON-NLS-1$
    public static String EventDetailsDialog_severity = getString("EventDetailsDialog_severity"); //$NON-NLS-1$
    public static String EventDetailsDialog_message = getString("EventDetailsDialog_message"); //$NON-NLS-1$
    public static String EventDetailsDialog_exception = getString("EventDetailsDialog_exception"); //$NON-NLS-1$
    public static String EventDetailsDialog_session = getString("EventDetailsDialog_session"); //$NON-NLS-1$
    public static String EventDetailsDialog_noStack = getString("EventDetailsDialog_noStack"); //$NON-NLS-1$
    public static String EventDetailsDialog_previous = getString("EventDetailsDialog_previous"); //$NON-NLS-1$
    public static String EventDetailsDialog_next = getString("EventDetailsDialog_next"); //$NON-NLS-1$
    public static String EventDetailsDialog_copy = getString("EventDetailsDialog_copy"); //$NON-NLS-1$

    public static String OpenLogDialog_title = getString("OpenLogDialog_title"); //$NON-NLS-1$
    public static String OpenLogDialog_message = getString("OpenLogDialog_message"); //$NON-NLS-1$
    public static String OpenLogDialog_cannotDisplay = getString("OpenLogDialog_cannotDisplay"); //$NON-NLS-1$
    
    public static String getString(String string, Object object) {
        return UiConstants.Util.getString(string, object);
    }
    
    public static String getString(String string) {
        return UiConstants.Util.getString(string);
    }
}
