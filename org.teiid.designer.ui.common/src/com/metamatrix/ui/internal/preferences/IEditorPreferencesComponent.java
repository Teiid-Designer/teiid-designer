/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
