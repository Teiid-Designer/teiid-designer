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

package com.metamatrix.modeler.tools.genericimport.ui;

import com.metamatrix.ui.UiConstants;


/**
 * PluginConstants for the GenericImportUiPlugin.
 */
public interface PluginConstants {
    //============================================================================================================================
    // Constants

    public static final String EMPTY_STRING = "";  //$NON-NLS-1$
    
    //============================================================================================================================
    // Image constants
    
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images
    extends UiConstants.Images {
        String IMPORT_PROJECT_ICON  = WIZBAN   + "import_project.gif"; //$NON-NLS-1$
    }

}