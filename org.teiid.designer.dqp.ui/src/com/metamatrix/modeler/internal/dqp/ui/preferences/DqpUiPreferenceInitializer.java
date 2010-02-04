/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.preferences;

import java.util.Properties;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import com.metamatrix.common.util.PropertiesUtils;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;


/** 
 * @since 5.0
 */
public class DqpUiPreferenceInitializer extends AbstractPreferenceInitializer
                                          implements DqpUiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Properties props;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public DqpUiPreferenceInitializer() {
        final String PROPS_NAME = "preferenceInitializer.properties"; //$NON-NLS-1$
        
        try {
            this.props = PropertiesUtils.loadAsResource(this.getClass(), PROPS_NAME);
        } catch (Exception theException) {
            UTIL.log(theException);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    

    private int getInt(String thePropName,
                       int theDefaultValue) {
        return (this.props != null) ? PropertiesUtils.getIntProperty(this.props, thePropName, theDefaultValue)
                                    : theDefaultValue;
    }

    /** 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     * @since 4.3
     */
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences prefs = new DefaultScope().getNode(DqpUiPlugin.getDefault().getBundle().getSymbolicName());

        //
        // assign values from the properties file. Default values only used when property key not found or when properties file not found.
        //

        int rowLimit = getInt("generalPreference.previewRowLimit", 10); //$NON-NLS-1$
        int resultsLimit = getInt("generalPreference.previewResultsLimit", 10); //$NON-NLS-1$
        
        //
        // initialize properties
        //

        prefs.putInt(Preferences.ID_PREVIEW_ROW_LIMIT, rowLimit);
        prefs.putInt(Preferences.ID_PREVIEW_RESULTS_LIMIT, resultsLimit);

        this.props = null;
    }

}
