/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.VdbPlugin.UTIL;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teiid.core.designer.properties.PropertyDefinition;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.core.translators.TranslatorPropertyDefinition;
import org.teiid.designer.vdb.Vdb.Event;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.TranslatorElement;


/**
 * A VDB translator override.
 *
 * @since 8.0
 */
public class TranslatorOverride extends VdbUnit implements Comparable<TranslatorOverride>, PropertyChangeListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(TranslatorOverride.class);

    /**
     * An empty array of translators.
     */
    public static final TranslatorOverride[] NO_TRANSLATORS = new TranslatorOverride[0];
    
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

    private final Map<String, TranslatorOverrideProperty> overrideProps = new HashMap<String, TranslatorOverrideProperty>();

    private final String type;

    /**
     * @param name the name of the overridden translator (may not be <code>null</code>)
     * @param type the translator type being overridden (may not be <code>null</code>)
     * @param vdb the VDB this override is contained in
     * @param description the translator override description (may be <code>null</code> or empty)
     */
    public TranslatorOverride( Vdb vdb,
                               String name,
                               String type,
                               String description ) {
        assert (name != null);
        assert (type != null);
        this.type = type;
        setVdb(vdb);
        setName(name == null ? EMPTY_STRING : name);
        setDescription(description == null ? EMPTY_STRING : description);
    }

    /**
     * @param vdb
     * @param element
     */
    public TranslatorOverride( Vdb vdb, TranslatorElement element ) {
        this(vdb, element.getName(), element.getType(), element.getDescription());

        for (PropertyElement property : element.getProperties()) {
            TranslatorPropertyDefinition propDefn = new TranslatorPropertyDefinition(property.getName(), property.getValue());
            TranslatorOverrideProperty prop = new TranslatorOverrideProperty(propDefn, property.getValue());
            this.overrideProps.put(propDefn.getId(), prop);
            prop.addListener(this);
        }
    }

    /**
     * Adds the property and fires a VDB property changed event.
     * 
     * @param newProperty the property being added (may not be <code>null</code>)
     */
    public void addProperty( TranslatorOverrideProperty newProperty ) {
        addProperty(newProperty, true);
    }

    /**
     * @param newProperty the property being added (may not be <code>null</code>)
     * @param firePropertyEvent <code>true</code> if a VDB property changed event should be fired
     */
    public void addProperty( TranslatorOverrideProperty newProperty,
                             boolean firePropertyEvent ) {
        assert (newProperty != null);

        Object obj = this.overrideProps.put(newProperty.getDefinition().getId(), newProperty);
        newProperty.addListener(this);

        if (firePropertyEvent) {
            setModified(this, Event.TRANSLATOR_PROPERTY, obj, newProperty);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( TranslatorOverride vdbTranslator ) {
        return getName().compareTo(vdbTranslator.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.AbstractMap#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        // only care if name is the same
        return getName().equals(((TranslatorOverride)obj).getName());
    }

    /**
     * Obtains all the properties of the translator even those properties whose values have not been overridden.
     * 
     * @return all translator properties (never <code>null</code>)
     */
    public TranslatorOverrideProperty[] getOverrideProperties() {
        TranslatorOverrideProperty[] props = new TranslatorOverrideProperty[this.overrideProps.size()];
        int i = 0;

        for (TranslatorOverrideProperty property : this.overrideProps.values()) {
            props[i++] = property;
        }

        return props;
    }

    /**
     * @return a list of all property names (never <code>null</code> but can be empty)
     */
    public List<String> getPropertyNames() {
        return Arrays.asList(this.overrideProps.keySet().toArray(new String[this.overrideProps.size()]));
    }

    /**
     * @return the translator type being overridden (never <code>null</code>)
     */
    public String getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.AbstractMap#hashCode()
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * @param property the property being marked as a user-defined or custom property (may not be <code>null</code>)
     */
    public void markAsUserDefined( TranslatorOverrideProperty property ) {
        property.getDefinition().markAsUserDefined(property.getOverriddenValue());
    }

    /**
     * {@inheritDoc}
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange( PropertyChangeEvent event ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)event.getSource();
        setModified(this, Event.TRANSLATOR_PROPERTY, event.getOldValue(), property);
    }

    /**
     * Removes the property and fires a VDB property changed event
     * 
     * @param propName the name of the property being removed (may not be <code>null</code> or empty)
     * @return <code>true</code> if the property was removed
     */
    public boolean removeOverrideProperty( String propName ) {
        return removeOverrideProperty(propName, true);
    }

    /**
     * @param propName the name of the property being removed (may not be <code>null</code> or empty)
     * @param firePropertyEvent <code>true</code> if a VDB property changed event should be fired
     * @return <code>true</code> if the property was removed
     */
    public boolean removeOverrideProperty( String propName, boolean firePropertyEvent ) {
        assert (propName != null);
        assert (this.overrideProps.containsKey(propName));

        TranslatorOverrideProperty prop = this.overrideProps.remove(propName);
        prop.removeListener(this);

        if (prop.getDefinition().isUserDefined()) {
            setModified(this, Event.TRANSLATOR_PROPERTY, prop, null);
        }

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
        assert (this.overrideProps.containsKey(propDefn.getId()));

        TranslatorOverrideProperty prop = this.overrideProps.get(propDefn.getId());
        String oldValue = prop.getOverriddenValue();

        // don't set if nothing has changed
        if (StringUtilities.equals(newValue, oldValue)) {
            return;
        }

        prop.setValue(newValue);

        if (firePropertyEvent) {
            setModified(this, Event.TRANSLATOR_PROPERTY, null, prop);
        }
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

        TranslatorOverrideProperty prop = this.overrideProps.get(propertyName);

        if (prop != null) {
            prop.setDefinition(newServerPropDefn);

            if (firePropertyEvent) {
                setModified(this, Event.TRANSLATOR_PROPERTY, null, prop);
            }
        }
    }

    @Override
    public TranslatorOverride clone() {
        TranslatorOverride clone = new TranslatorOverride(getVdb(), getName(), getType(), getDescription());
        cloneVdbObject(clone);

        for (TranslatorOverrideProperty property : overrideProps.values()) {
            TranslatorPropertyDefinition definition = property.getDefinition();
            TranslatorPropertyDefinition cloneDefn = new TranslatorPropertyDefinition(definition.getId(), definition.getDefaultValue());

            TranslatorOverrideProperty cloneProp = new TranslatorOverrideProperty(cloneDefn, property.getOverriddenValue());
            clone.addProperty(cloneProp, false);
        }

        return clone;
    }
}