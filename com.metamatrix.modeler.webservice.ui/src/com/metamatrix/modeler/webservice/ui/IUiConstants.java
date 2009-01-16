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
