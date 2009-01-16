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

package com.metamatrix.ui.internal.preferences;

import org.eclipse.swt.widgets.Composite;

/**
 * @author SDelap
 * Interface for contributed editor preferences
 */
public interface IEditorPreferencesComponent {
    /**
     * Builds the editor in the composite passed in.
     * @param composite
     * @return
     */
    public Composite createEditorPreferencesComponent(Composite parent);

    /**
     * Returns the name of the component for optional use.  Example Tab Name.
     * @return Name of the component
     */
    public String getName();
    
    /**
     * Returns the name of the component tooltip for optional use.  Example Tab Name.
     * @return Name of the component
     */
    public String getTooltip();

    /**
     * This is called when restore defaults is pressed in the preferences page.
     * @return
     */    
    public void performDefaults();

    /**
     * This is called when ok or apply is pressed
     * @return
     */    
    public boolean performOk();
    
    /**
     * Editors can have validation listeners added which are notified when validation events occur.
     * @return
     */        
    public void addValidationListener(IEditorPreferencesValidationListener listener);
    public void removeValidationListener(IEditorPreferencesValidationListener listener);
    
    
    /**
     * Force validation on a new component.
     * @return
     */        
    public void validate();
}
