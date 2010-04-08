/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationDescriptor;
import com.metamatrix.modeler.core.ValidationPreferences;

/**
 * ValidationPreferencesImpl
 */
public class ValidationPreferencesImpl implements ValidationPreferences {

    //############################################################################################################################
    //# Constants                                                                                                                #
    //############################################################################################################################

    //Used to enable Unit Testing.
    public static boolean HEADLESS = false;
	
    private static char DELIMITER = '.';    

    private static final List validationDescriptors = EclipseConfigurationBuilder.readValidationDescriptors();
    private static final Set optionNames = getOptionNames(validationDescriptors);

    // defect 19167 - initialize valid option names from descriptors:
    private static Set getOptionNames(List descriptors) {
        Set rv = new HashSet();
        Iterator itor = descriptors.iterator();
        while (itor.hasNext()) {
            ValidationDescriptor vdesc = (ValidationDescriptor) itor.next();
            String name = getFullName(vdesc);
            rv.add(name);
        } // endwhile

        return rv;
    }

    private static String getFullName(ValidationDescriptor vdesc) {
        String descID = vdesc.getExtensionID();
        String descName = vdesc.getPreferenceName();
        return descID+DELIMITER+descName;
    }
    
    public ValidationPreferencesImpl() {
        List descriptors = getValidationDescriptors();
        
        // validation uses the options map from this class. an initial options map is needed that
        // contains all the validation descriptors to ensure all will be used during validation.
        if ((descriptors != null) && !descriptors.isEmpty()) {
            Preferences preferences = ModelerCore.getPlugin().getPluginPreferences();
            int size = descriptors.size();
            Map changeMap = new HashMap(size);
            
            for (int i = 0; i < size; ++i) {
                ValidationDescriptor descriptor = (ValidationDescriptor)descriptors.get(i);
                
                // get current value of preference. if one does not exist use the default
                String value = getCurrentPreferenceValue(preferences, descriptor);
                
                if (CoreStringUtil.isEmpty(value)) {
                    value = descriptor.getDefaultOption();
                }
                
                changeMap.put(descriptor, value);
            }
            
            setOptions(changeMap);
        }
    }
    
    /**
     * Obtains the current preference value for the specified descriptor.
     * @param thePreferences the preferences containing the specified preference
     * @param theDescriptor the descriptor whose value is being requested
     * @return the current value or an empty string if no value exists
     * @since 5.0.1
     */
    private String getCurrentPreferenceValue(Preferences thePreferences,
                                             ValidationDescriptor theDescriptor) {
        return thePreferences.getString(getFullName(theDescriptor));
    }

    public List getValidationDescriptors() {
        return validationDescriptors;
    }

    /**
     * Sets the current table of options. All and only the options explicitly included in the given table 
     * are remembered; all previous option settings are forgotten, including ones not explicitly
     * mentioned.
     * <p>
     * For a complete description of the configurable options, see <code>getDefaultOptions</code>.
     * </p>
     * 
     * @param newOptions the new options (key type: <code>ValidationDescriptor</code>; value type: <code>String</code>),
     *   or <code>null</code> to reset all options to their default values
     */
    public void setOptions(Map newOptions) {

        // see #initializeDefaultPluginPreferences() for changing default settings
        Preferences preferences = ModelerCore.getPlugin().getPluginPreferences();

        Iterator keyIter = newOptions.keySet().iterator();
        while (keyIter.hasNext()){
            ValidationDescriptor key = (ValidationDescriptor)keyIter.next();
            String name = getFullName(key);
            if (!optionNames.contains(name)) continue; // unrecognized option
            Object value = newOptions.get(key);
            if(value == null) continue;
            preferences.setValue(name, getValidOption((String)value));
        }

        // persist options
        ModelerCore.getPlugin().savePluginPreferences();
    }

    /**
     * Returns the table of the current options. Initially, all options have their default values,
     * and this method returns a table that includes all known options.
     * <p>
     * For a complete description of the configurable options, see <code>getDefaultOptions</code>.
     * </p>
     * 
     * @return Map of current settings of all options 
     *   (key type: <code>String</code>; value type: <code>String</code>)
     */
    public Map getOptions() {
        Map options = new HashMap();

        // see #initializeDefaultPluginPreferences() for changing default settings
        Plugin plugin = ModelerCore.getPlugin();
        if (plugin != null) {
            Preferences preferences = plugin.getPluginPreferences();

            // get preferences set to their default
//            String[] defaultPropertyNames = preferences.defaultPropertyNames();
//            for (int i = 0; i < defaultPropertyNames.length; i++){
//                String propertyName = defaultPropertyNames[i];
//                if (optionNames.contains(propertyName)){
//                    options.put(propertyName, preferences.getDefaultString(propertyName));
//                }
//            }
            // get preferences not set to their default
            String[] propertyNames = preferences.propertyNames();
            for (int i = 0; i < propertyNames.length; i++){
                String propertyName = propertyNames[i];
                if (optionNames.contains(propertyName)){
                    String value = preferences.getString(propertyName).trim();
                    options.put(propertyName, value);
                }
            }       
        }
        return options;
    }

    /**
     * Checks the given value against a set of known options, if the value is not among the known
     * options, a known option value IGNORE is returned.
     */
    private String getValidOption(String value) {
        if(value.equalsIgnoreCase(ValidationDescriptor.ERROR) || value.equalsIgnoreCase(ValidationDescriptor.IGNORE)
            || value.equalsIgnoreCase(ValidationDescriptor.INFO) || value.equalsIgnoreCase(ValidationDescriptor.WARNING)) {
            return value;    
        }

        return ValidationDescriptor.IGNORE;
    }

}
