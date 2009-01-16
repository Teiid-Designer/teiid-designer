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

package com.metamatrix.modeler.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * ModelEditorPageOutline is an interface for contributing a control to the
 * Outline PageBook.
 */
public interface ModelEditorPageOutline {

    /**
     * Create the control to be placed in the outline page when the corresponding ModelEditorPage
     * is activated.
     * @param parent the Composite parent to be used to construct the Control.
     */
    void createControl(Composite parent);
    

    /**
     * Obtain the control to be placed in the outline page when the corresponding ModelEditorPage
     * is activated.
     * @return 
     */
    Control getControl();
    
    /**
     * Obtain the tool tip text to be placed on the toolbar button for displaying this outline
     * page contribution.
     * @return
     */
    String getToolTipText();
    
    /**
     * Obtain the icon to be placed on the toolbar button for displaying this outline
     * page contribution.
     * @return
     */
    ImageDescriptor getIcon();
    
    /**
     * Determine whether or not this contribution should be enabled.  This method will be called
     * each time an the ModelEditorPage is activated or a new object is sent to it's openObject
     * method.
     * @return true if the contribution may be viewed, false if it should be hidden and the show
     * action disabled.
     */
    boolean isEnabled();
    
    /**
     * Callback from the ModelOutlinePage to notify this ModelEditorPageOutline that it has either
     * become visble or hidden.
     * @param isVisible true if the page has become visible, false if another page has been shown.
     */
    void setVisible(boolean isVisible);

    void dispose();

}
