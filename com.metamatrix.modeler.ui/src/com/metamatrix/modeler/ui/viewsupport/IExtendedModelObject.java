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

package com.metamatrix.modeler.ui.viewsupport;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.views.properties.IPropertySource;


/** 
 * Interface which provides extended model objects that are NOT EObjects to be displayed. This is required to prevent filtering
 * via the ModelWorkspaceViewerFilter for non-EObjects
 * @since 5.0
 */
public interface IExtendedModelObject {
    
    /**
     * Required method to generically delegate the property source for contributed/extended model objects 
     * @return
     * @since 5.0
     */
    IPropertySource getPropertySource();

    /**
     * Required method to contribute a label to the Status Bar on selection. May return NULL string 
     * @return
     * @since 5.0
     */
    String getStatusLabel();
    
    
    /**
     * Required method to contribute menu items to context menu on selection of an IExtendedModelObject in Modeler Explorer 
     * @param theMenu
     * @since 5.0
     */
    void fillContextMenu(IMenuManager theMenu);
    
    /**
     * Implementers should return TRUE to utilize fillContextMenu() 
     * @return
     * @since 5.0
     */
    boolean overrideContextMenu();
}
