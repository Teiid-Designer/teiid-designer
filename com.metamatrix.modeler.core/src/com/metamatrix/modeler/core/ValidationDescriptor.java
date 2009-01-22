/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

/**
 * ValidationDescriptor
 */
public interface ValidationDescriptor {

    /**
     * Possible  configurable option value.
     */
    String INFO = "info"; //$NON-NLS-1$

    /**
     * Possible  configurable option value.
     */
    String ERROR = "error"; //$NON-NLS-1$

    /**
     * Possible  configurable option value.
     */
    String WARNING = "warning"; //$NON-NLS-1$

    /**
     * Possible  configurable option value.
     */
    String IGNORE = "ignore"; //$NON-NLS-1$

    /**
     * Possible  configurable option value.
     */
    String TRUE = "true"; //$NON-NLS-1$

    /**
     * Possible  configurable option value.
     */
    String FALSE = "false"; //$NON-NLS-1$

    /**
     * Possible  configurable option value.
     */
    String NOT_SET = ""; //$NON-NLS-1$     

    /**
     * Return the identifier of the validation extension
     * @return String or null if no extension ID exists.
     */
    String getExtensionID();
    
    /**
     * Return the name of the validation preference
     * @return String or null if no name exists.
     */
    String getPreferenceName();
    
    /**
     * Return the label of the validation preference
     * @return String or null if no name exists.
     */
    String getPreferenceLabel();
    
    /**
     * Return the tool tip for the validation preference
     * @return String or null if no name exists.
     */
    String getPreferenceToolTip();
    
    /**
     * Return the category of the validation preference
     * @return String or null if no name exists.
     */
    String getPreferenceCategory();

    /**
     * Return the default option to be chosen for this validation preference
     * @return String the default preference option from the among the
     * constants defined on this class
     */    
    String getDefaultOption();

}
