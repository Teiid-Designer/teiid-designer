/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui;


/** 
 * Public constants.
 * @since 4.4
 */
public interface IUiConstants {

    /**
     * The plug-in ID where this interface is located.
     * @since 4.4
     */
    String PLUGIN_ID = IUiConstants.class.getPackage().getName();
    
    String WSDL_FILE_EXTENSION = "wsdl"; //$NON-NLS-1$

    /**
     * Constants related to extensions, including all extension ID's.
     * @since 4.4
     */
    interface Extensions {
        String ADVISOR_VIEW_ID = PLUGIN_ID + ".advisor.AdvisorView"; //$NON-NLS-1$
    }
}
