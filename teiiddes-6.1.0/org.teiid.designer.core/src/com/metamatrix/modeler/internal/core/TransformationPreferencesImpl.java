/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import org.eclipse.core.runtime.Preferences;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.TransformationPreferences;

/**
 * ValidationPreferencesImpl
 */
public class TransformationPreferencesImpl implements TransformationPreferences {

    //Used to enable Unit Testing.
    public static boolean HEADLESS = false;
    
    private Preferences preferences; 
    private boolean hasInitialized = false;

    private static int DEFAULT_LENGTH = 10;
    private static int UPPER_RECURSION_LIMIT = 10;
    private static boolean DEFAULT_REMOVE_ATTRIBUTES_VALUE = false;
    
    private void initializeDefaultsIfNeeded() {
    	if(HEADLESS) return;
    	
        int val = getPreferences().getInt(DEFAULT_STRING_LENGTH_KEY);
        if (val == 0) {
            getPreferences().setValue(DEFAULT_STRING_LENGTH_KEY, DEFAULT_LENGTH);
        }
        val = getPreferences().getInt(UPPER_RECURSION_LIMIT_KEY);
        if (val == 0) {
            getPreferences().setValue(UPPER_RECURSION_LIMIT_KEY, UPPER_RECURSION_LIMIT);
        }
        
        boolean bol = getPreferences().getBoolean(REMOVE_DUPLICATE_ATTRIBUTES_KEY);
        if (!bol) {
            getPreferences().setValue(REMOVE_DUPLICATE_ATTRIBUTES_KEY, DEFAULT_REMOVE_ATTRIBUTES_VALUE);
        }  
    }
    

    
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.TransformationPreferences#getDefaultStringLength()
     */
    public int getDefaultStringLength() {
        if(HEADLESS) return DEFAULT_LENGTH;
        if (!hasInitialized) {
            initializeDefaultsIfNeeded();
            hasInitialized = true;
        }
        return getPreferences().getInt(DEFAULT_STRING_LENGTH_KEY); 
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.TransformationPreferences#setDefaultStringLength()
     */
    public void setDefaultStringLength(int val) {
    	if(HEADLESS) return;
        getPreferences().setValue(DEFAULT_STRING_LENGTH_KEY, val);
        ModelerCore.getPlugin().savePluginPreferences();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.TransformationPreferences#setUpperRecursionLimit()
     */
    public void setUpperRecursionLimit(int val) {
    	if(HEADLESS) return;
        getPreferences().setValue(UPPER_RECURSION_LIMIT_KEY, val);
        ModelerCore.getPlugin().savePluginPreferences();
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.TransformationPreferences#getUpperRecursionLimit()
     */
    public int getUpperRecursionLimit() {
        if(HEADLESS) return UPPER_RECURSION_LIMIT;
        if (!hasInitialized) {
            initializeDefaultsIfNeeded();
            hasInitialized = true;
        }
        return getPreferences().getInt(UPPER_RECURSION_LIMIT_KEY); 
    }

    public boolean getRemoveDuplicateAttibutes() {
        if(HEADLESS) return DEFAULT_REMOVE_ATTRIBUTES_VALUE;
        if (!hasInitialized) {
            initializeDefaultsIfNeeded();
            hasInitialized = true;
        }
        return getPreferences().getBoolean(REMOVE_DUPLICATE_ATTRIBUTES_KEY);
    }
    
    public void setRemoveDuplicateAttibutes(boolean val) {
    	if(HEADLESS) return;
        getPreferences().setValue(REMOVE_DUPLICATE_ATTRIBUTES_KEY, val);
        ModelerCore.getPlugin().savePluginPreferences();
    }
    
    private Preferences getPreferences() {
        if (preferences == null) {
            preferences = ModelerCore.getPlugin().getPluginPreferences();
        }
        return preferences;
    }
}
