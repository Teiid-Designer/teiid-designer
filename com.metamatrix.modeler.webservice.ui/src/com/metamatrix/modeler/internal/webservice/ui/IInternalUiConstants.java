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

package com.metamatrix.modeler.internal.webservice.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.webservice.ui.IUiConstants;


/** 
 * The internal UI constants.
 * @since 4.2
 */
public interface IInternalUiConstants extends IUiConstants {

    /**
     * The resource bundle path/filename.
     * @since 4.2
     */
    String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    
    /** The dialog settings section to use for any settings saved. */
    String DIALOG_SETTINGS_SECTION = "WebServiceModel"; //$NON-NLS-1$
    
    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * @since 4.2
     */
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, BUNDLE_NAME, ResourceBundle.getBundle(BUNDLE_NAME));
    
    /**
     * Contains constants for accessing images.
     * @since 4.2
     */
    interface Images {
        String IMG_PATH = "icons/full/"; //$NON-NLS-1$
        
        String CTOOL16 = IMG_PATH + "ctool16/"; //$NON-NLS-1$
        String CVIEW16 = IMG_PATH + "cview16/"; //$NON-NLS-1$
        String DTOOL16 = IMG_PATH + "dtool16/"; //$NON-NLS-1$
        String OBJ16   = IMG_PATH + "obj16/"; //$NON-NLS-1$
        String OVR16   = IMG_PATH + "ovr16/"; //$NON-NLS-1$
        String WIZBAN  = IMG_PATH + "wizban/"; //$NON-NLS-1$

        String DEPLOY          = CTOOL16 + "exportWAR.png"; //$NON-NLS-1$
        String IMPORT_WSDL     = CTOOL16 + "import_wsdl.gif"; //$NON-NLS-1$
        String NEW_WEB_SERVICE = CTOOL16 + "NewWebService.png"; //$NON-NLS-1$
        String CREATE_WEB_SERVICE = CTOOL16 + "CreateWebService.png"; //$NON-NLS-1$
        String OPEN_VDB        = CTOOL16 + "openVDB.gif"; //$NON-NLS-1$
        String PROBLEM_ERROR   = CTOOL16 + "ProblemMarker_error.gif"; //$NON-NLS-1$
        String PROBLEM_INFO    = CTOOL16 + "ProblemMarker_info.gif"; //$NON-NLS-1$
        String PROBLEM_WARNING = CTOOL16 + "ProblemMarker_warning.gif"; //$NON-NLS-1$
        String PROBLEMS_VIEW   = CTOOL16 + "problems_view.gif"; //$NON-NLS-1$
        String STATUS_GOOD     = CTOOL16 + "status_icon_good.gif"; //$NON-NLS-1$
        
        String CLOSE_EDITOR        = CVIEW16 + "closeEditor.gif"; //$NON-NLS-1$
        String DOWN                = CVIEW16 + "down.gif"; //$NON-NLS-1$
        String PROBLEM_INDICATOR   = CVIEW16 + "problem_indicator.gif"; //$NON-NLS-1$
        String RESOLUTION_STATUS   = CVIEW16 + "resolution_status.gif"; //$NON-NLS-1$
        String SCHEMA_EDITOR       = CVIEW16 + "schemaEditor.gif"; //$NON-NLS-1$
        String SHOW_CHECKED_ONLY   = CVIEW16 + "showCheckedOnly.gif"; //$NON-NLS-1$
        String SHOW_DEPENDENCIES   = CVIEW16 + "show_dependencies.gif"; //$NON-NLS-1$
        String UNRESOLVE_NAMESPACE = CVIEW16 + "unresolve_namespace.gif"; //$NON-NLS-1$
        String UP                  = CVIEW16 + "up.gif"; //$NON-NLS-1$

        String DEPLOY_GRAY = DTOOL16 + "exportWARgray.png"; //$NON-NLS-1$

        String ERROR                    = OVR16 + "error.gif"; //$NON-NLS-1$
        String OCCURS_N                 = OVR16 + "occursN.gif"; //$NON-NLS-1$
        String OCCURS_N_TO_M            = OVR16 + "occursNToM.gif"; //$NON-NLS-1$
        String OCCURS_N_TO_UNBOUNDED    = OVR16 + "occursNToUnbounded.gif"; //$NON-NLS-1$
        String OCCURS_ONE_TO_N          = OVR16 + "occursOneToN.gif"; //$NON-NLS-1$
        String OCCURS_ONE_TO_UNBOUNDED  = OVR16 + "occursOneToUnbounded.gif"; //$NON-NLS-1$
        String OCCURS_ZERO              = OVR16 + "occursZero.gif"; //$NON-NLS-1$
        String OCCURS_ZERO_TO_ONE       = OVR16 + "occursZeroToOne.gif"; //$NON-NLS-1$
        String OCCURS_ZERO_TO_N         = OVR16 + "occursZeroToN.gif"; //$NON-NLS-1$
        String OCCURS_ZERO_TO_UNBOUNDED = OVR16 + "occursZeroToUnbounded.gif"; //$NON-NLS-1$
        String RECURSIVE                = OVR16 + "recursive.gif"; //$NON-NLS-1$
        String WARNING                  = OVR16 + "warning.gif"; //$NON-NLS-1$

        String NEW_MODEL_BANNER = WIZBAN + "WebService.gif"; //$NON-NLS-1$
        
        String SERVICE_ICON             = OBJ16   + "service_obj.gif"; //$NON-NLS-1$
        String OPERATION_ICON           = OBJ16   + "operation_obj.gif"; //$NON-NLS-1$
        String PORT_ICON                = OBJ16   + "port_obj.gif"; //$NON-NLS-1$
        String BINDING_ICON             = OBJ16   + "binding_obj.gif"; //$NON-NLS-1$
    }
    
    /**
     * Contains constants for the available context help identifiers found in the helpContexts.xml file. 
     * @since 4.2
     */
    interface HelpContexts {
        String PREFIX = PLUGIN_ID + '.';
        String NAMESPACE_RESOLUTION_PAGE = PREFIX + "namespaceResolutionPage"; //$NON-NLS-1$
        String SCHEMA_LOCATION_PAGE = PREFIX + "schemaLocationPage"; //$NON-NLS-1$
        String SCHEMA_LOCATION_EDITOR = PREFIX + "schemaLocationPage_schemaLocationEditor"; //$NON-NLS-1$
        String WSDL_SELECTION_PAGE = PREFIX + "wsdlSelectionPage"; //$NON-NLS-1$
        String XML_MODEL_SELECTION_PAGE = PREFIX + "xmlModelSelectionPage"; //$NON-NLS-1$
        String INTERFACE_DEFINITION_PAGE = PREFIX + "interfaceDefinitionPage"; //$NON-NLS-1$
    }

    interface CheatSheets {
        String CHEAT_SHEET_WS_OVERVIEW     = "com.metamatrix.modeler.webservice.ui.cheat.WebServiceOverview"; //$NON-NLS-1$
        String CHEAT_SHEET_WS_FROM_JDBC    = "com.metamatrix.modeler.webservice.ui.cheat.JDBCToWebService"; //$NON-NLS-1$
        String CHEAT_SHEET_WS_FROM_XSD     = "com.metamatrix.modeler.webservice.ui.cheat.XSDToWebService"; //$NON-NLS-1$
        String CHEAT_SHEET_WS_FROM_WSDL    = "com.metamatrix.modeler.webservice.ui.cheat.WSDLToWebService"; //$NON-NLS-1$
    }
}
