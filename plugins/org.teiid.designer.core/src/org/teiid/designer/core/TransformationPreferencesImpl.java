/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

/**
 * ValidationPreferencesImpl
 *
 * @since 8.0
 */
public class TransformationPreferencesImpl implements TransformationPreferences {

    //Used to enable Unit Testing.
    public static boolean HEADLESS = false;
    private static int DEFAULT_LENGTH = 0;
    private static int UPPER_RECURSION_LIMIT = 10;
    private static boolean DEFAULT_REMOVE_ATTRIBUTES_VALUE = false;

    private boolean hasInitialized = false;
    
    private void initializeDefaultsIfNeeded() {
    	if(HEADLESS) return;
    	
    	IEclipsePreferences defaultPrefs = ModelerCore.getDefaultPreferences(ModelerCore.PLUGIN_ID);

    	int val = defaultPrefs.getInt(DEFAULT_STRING_LENGTH_KEY, 0);
        if (val == 0) {
            defaultPrefs.putInt(DEFAULT_STRING_LENGTH_KEY, DEFAULT_LENGTH);
        }

        val = defaultPrefs.getInt(UPPER_RECURSION_LIMIT_KEY, 0);
        if (val == 0) {
            defaultPrefs.putInt(UPPER_RECURSION_LIMIT_KEY, UPPER_RECURSION_LIMIT);
        }
        
        boolean bol = defaultPrefs.getBoolean(REMOVE_DUPLICATE_ATTRIBUTES_KEY, false);
        if (!bol) {
            defaultPrefs.putBoolean(REMOVE_DUPLICATE_ATTRIBUTES_KEY, DEFAULT_REMOVE_ATTRIBUTES_VALUE);
        }
        
        save();
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.TransformationPreferences#getDefaultStringLength()
     */
    @Override
	public int getDefaultStringLength() {
        if (HEADLESS) return DEFAULT_LENGTH;

        if (!hasInitialized) {
            initializeDefaultsIfNeeded();
            hasInitialized = true;
        }

        return getPreferences().getInt(DEFAULT_STRING_LENGTH_KEY, DEFAULT_LENGTH);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.TransformationPreferences#getDefaultStringLengthDefault()
     */
    @Override
	public int getDefaultStringLengthDefault() {
        return DEFAULT_LENGTH;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.TransformationPreferences#setDefaultStringLength(int)
     */
    @Override
	public void setDefaultStringLength( int val ) {
        if (HEADLESS) return;
        getPreferences().putInt(DEFAULT_STRING_LENGTH_KEY, val);
        save();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.TransformationPreferences#setUpperRecursionLimit(int)
     */
    @Override
	public void setUpperRecursionLimit( int val ) {
        if (HEADLESS) return;
        getPreferences().putInt(UPPER_RECURSION_LIMIT_KEY, val);
        save();
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.TransformationPreferences#getUpperRecursionLimit()
     */
    @Override
	public int getUpperRecursionLimit() {
        if (HEADLESS) return UPPER_RECURSION_LIMIT;

        if (!hasInitialized) {
            initializeDefaultsIfNeeded();
            hasInitialized = true;
        }

        return getPreferences().getInt(UPPER_RECURSION_LIMIT_KEY, UPPER_RECURSION_LIMIT);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.TransformationPreferences#getUpperRecursionLimitDefault()
     */
    @Override
	public int getUpperRecursionLimitDefault() {
        return UPPER_RECURSION_LIMIT;
    }

    @Override
	public boolean getRemoveDuplicateAttibutes() {
        if (HEADLESS) return DEFAULT_REMOVE_ATTRIBUTES_VALUE;

        if (!hasInitialized) {
            initializeDefaultsIfNeeded();
            hasInitialized = true;
        }

        return getPreferences().getBoolean(REMOVE_DUPLICATE_ATTRIBUTES_KEY, DEFAULT_REMOVE_ATTRIBUTES_VALUE);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.TransformationPreferences#getRemoveDuplicateAttibutesDefault()
     */
    @Override
	public boolean getRemoveDuplicateAttibutesDefault() {
        return DEFAULT_REMOVE_ATTRIBUTES_VALUE;
    }
    
    @Override
	public void setRemoveDuplicateAttibutes( boolean val ) {
        if (HEADLESS) return;
        getPreferences().putBoolean(REMOVE_DUPLICATE_ATTRIBUTES_KEY, val);
        save();
    }
    
    private IEclipsePreferences getPreferences() {
        return ModelerCore.getPreferences(ModelerCore.PLUGIN_ID);
    }
    
    private void save() {
        try {
            ModelerCore.savePreferences(ModelerCore.PLUGIN_ID);
        } catch (BackingStoreException e) {
            ModelerCore.Util.log(e);
        }
    }

}
