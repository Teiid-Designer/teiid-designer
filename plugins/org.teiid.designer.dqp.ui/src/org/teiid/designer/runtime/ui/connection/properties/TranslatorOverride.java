package org.teiid.designer.runtime.ui.connection.properties;

import static org.teiid.designer.vdb.VdbPlugin.UTIL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.teiid.core.designer.properties.PropertyDefinition;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.core.translators.TranslatorPropertyDefinition;

public class TranslatorOverride {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(TranslatorOverride.class);

    /**
     * @param proposedName the proposed translator name
     * @return an error message or <code>null</code> if name is valid
     */
    public static String validateName( String proposedName ) {
        // must have a name
        if (StringUtilities.isEmpty(proposedName)) {
            return UTIL.getString(PREFIX + "emptyTranslatorName"); //$NON-NLS-1$
        }

        // make sure only letters, digits, or dash
        for (char c : proposedName.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c)
                    && (c != '-')) {
                return UTIL.getString(PREFIX + "invalidTranslatorName", proposedName); //$NON-NLS-1$
            }
        }

        // first char must be letter
        if (!Character.isLetter(proposedName.charAt(0))) {
            return UTIL.getString(PREFIX + "translatorNameMustStartWithLetter"); //$NON-NLS-1$
        }

        // valid name
        return null;
    }

    /**
     * @param proposedType the proposed translator type
     * @return an error message or <code>null</code> if type is valid
     */
    public static String validateType( String proposedType ) {
        // must have a type
        if (StringUtilities.isEmpty(proposedType)) {
            return UTIL.getString(PREFIX + "emptyTranslatorType"); //$NON-NLS-1$
        }

        // make sure only letters, digits, or dash
        for (char c : proposedType.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '-') {
                return UTIL.getString(PREFIX + "invalidTranslatorType", proposedType); //$NON-NLS-1$
            }
        }

        // first char must be letter
        if (!Character.isLetter(proposedType.charAt(0))) {
            return UTIL.getString(PREFIX + "translatorTypeMustStartWithLetter"); //$NON-NLS-1$
        }

        // valid type
        return null;
    }


    private final Map<String, TranslatorOverrideProperty> properties;

    private final String type;

    /**
     * @param name the name of the overridden translator (may not be <code>null</code>)
     * @param type the translator type being overridden (may not be <code>null</code>)
     * @param vdb the VDB this override is contained in
     * @param description the translator override description (may be <code>null</code> or empty)
     */
    public TranslatorOverride( String type, Properties properties ) {
        assert (type != null);

        this.type = type;
        this.properties = new HashMap<String, TranslatorOverrideProperty>();
    }

    /**
     * @param newProperty the property being added (may not be <code>null</code>)
	*/
    public void addProperty( TranslatorOverrideProperty newProperty) {
        assert (newProperty != null);

        this.properties.put(newProperty.getDefinition().getId(), newProperty);

    }

    /**
     * Obtains all the properties of the translator even those properties whose values have not been overridden.
     * 
     * @return all translator properties (never <code>null</code>)
     */
    public TranslatorOverrideProperty[] getProperties() {
        TranslatorOverrideProperty[] props = new TranslatorOverrideProperty[this.properties.size()];
        int i = 0;

        for (TranslatorOverrideProperty property : this.properties.values()) {
            props[i++] = property;
        }

        return props;
    }

    /**
     * @return a list of all property names (never <code>null</code> but can be empty)
     */
    public List<String> getPropertyNames() {
        return Arrays.asList(this.properties.keySet().toArray(new String[this.properties.size()]));
    }

    /**
     * @return the translator type being overridden (never <code>null</code>)
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param property the property being marked as a user-defined or custom property (may not be <code>null</code>)
     */
    public void markAsUserDefined( TranslatorOverrideProperty property ) {
        property.getDefinition().markAsUserDefined(property.getOverriddenValue());
    }

    /**
     * Removes the property and fires a VDB property changed event
     * 
     * @param propName the name of the property being removed (may not be <code>null</code> or empty)
     * @return <code>true</code> if the property was removed
     */
    public boolean removeProperty( String propName ) {
        return removeProperty(propName, true);
    }

    /**
     * @param propName the name of the property being removed (may not be <code>null</code> or empty)
     * @param firePropertyEvent <code>true</code> if a VDB property changed event should be fired
     * @return <code>true</code> if the property was removed
     */
    public boolean removeProperty( String propName,
                                   boolean firePropertyEvent ) {
        assert (propName != null);
        assert (this.properties.containsKey(propName));

        this.properties.remove(propName);

        return false;
    }


    /**
     * Updates the overridden value and fires a VDB property changed event.
     * 
     * @param propDefn the property definition whose overridden value is being changed (may not be <code>null</code>)
     * @param newValue the new value (may be <code>null</code> or empty)
     */
    public void setOverrideValue( TranslatorPropertyDefinition propDefn,
                                  String newValue ) {
        setOverrideValue(propDefn, newValue, true);
    }

    /**
     * @param propDefn the property definition whose overridden value is being changed (may not be <code>null</code>)
     * @param newValue the new value (may be <code>null</code> or empty)
     * @param firePropertyEvent <code>true</code> if a VDB property changed event should be fired
     */
    public void setOverrideValue( TranslatorPropertyDefinition propDefn,
                                  String newValue,
                                  boolean firePropertyEvent ) {
        assert (propDefn != null);
        assert (this.properties.containsKey(propDefn.getId()));

        TranslatorOverrideProperty prop = this.properties.get(propDefn.getId());
        String oldValue = prop.getOverriddenValue();

        // don't set if nothing has changed
        if (StringUtilities.equals(newValue, oldValue)) {
            return;
        }

        prop.setValue(newValue);
    }

    /**
     * Updates the property definition and fires a VDB property changed event.
     * 
     * @param propertyName the property whose definition is being updated (may not be <code>null</code> or empty)
     * @param newServerPropDefn the new definition (may not be <code>null</code>)
     */
    public void updatePropertyDefinition( String propertyName,
                                          PropertyDefinition newServerPropDefn ) {
        updatePropertyDefinition(propertyName, newServerPropDefn, true);
    }

    /**
     * @param propertyName the property whose definition is being updated (may not be <code>null</code> or empty)
     * @param newServerPropDefn the new definition (may not be <code>null</code>)
     * @param firePropertyEvent <code>true</code> if a VDB property changed event should be fired
     */
    public void updatePropertyDefinition( String propertyName,
                                          PropertyDefinition newServerPropDefn,
                                          boolean firePropertyEvent ) {
        assert (!StringUtilities.isEmpty(propertyName));
        assert (newServerPropDefn != null);

        TranslatorOverrideProperty prop = this.properties.get(propertyName);

        if (prop != null) {
            prop.setDefinition(newServerPropDefn);
        }
    }

}