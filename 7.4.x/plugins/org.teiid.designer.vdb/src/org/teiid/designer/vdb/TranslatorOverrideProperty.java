/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.ListenerList;
import org.teiid.core.properties.PropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;

/**
 * A translator property that may or may not have an overridden value.
 */
public class TranslatorOverrideProperty {

    private final ListenerList listeners;
    private String overriddenValue;
    private TranslatorPropertyDefinition propDefn;

    /**
     * @param propDefn the property definition (may not be <code>null</code>)
     * @param overriddenValue the property value override (can be <code>null</code> or empty)
     * @throws IllegalArgumentException if <code>propDefn</code> is <code>null</code>
     */
    public TranslatorOverrideProperty( TranslatorPropertyDefinition propDefn,
                                       String overriddenValue ) {
        CoreArgCheck.isNotNull(propDefn);
        this.propDefn = propDefn;
        this.overriddenValue = overriddenValue;
        this.listeners = new ListenerList();
    }

    /**
     * @param listener the listener being added (may not be <code>null</code>)
     * @throws IllegalArgumentException if <code>listener</code> is <code>null</code>
     */
    public void addListener( PropertyChangeListener listener ) {
        this.listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
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

        return this.propDefn.getId().equals(((TranslatorOverrideProperty)obj).getDefinition().getId());
    }

    /**
     * @return the property definition (never <code>null</code>)
     */
    public TranslatorPropertyDefinition getDefinition() {
        return this.propDefn;
    }

    /**
     * @return the property value override (may be <code>null</code> or empty)
     */
    public String getOverriddenValue() {
        // don't allow a custom property to have an empty value
        return ((isCustom() && StringUtilities.isEmpty(this.overriddenValue)) ? getDefinition().getDefaultValue()
                                                                             : this.overriddenValue);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getDefinition().getId().hashCode();
    }

    /**
     * @return <code>true</code> if the property value is not <code>null</code> and not empty
     */
    public boolean hasOverridenValue() {
        // custom properties always have a value
        if (isCustom()) {
            assert (!StringUtilities.isEmpty(getOverriddenValue()));
            return true;
        }

        return !StringUtilities.isEmpty(getOverriddenValue());
    }

    /**
     * @return <true> if not a known server property
     * @see TranslatorPropertyDefinition#isUserDefined()
     */
    public boolean isCustom() {
        return this.propDefn.isUserDefined();
    }

    /**
     * @param listener the listener being removed (may not be <code>null</code>)
     * @throws IllegalArgumentException if <code>listener</code> is <code>null</code>
     */
    public void removeListener( PropertyChangeListener listener ) {
        this.listeners.remove(listener);
    }

    /**
     * @param propDefn the new property definition from the server (may not be <code>null</code>)
     */
    public void setDefinition( PropertyDefinition propDefn ) {
        assert (propDefn != null);
        this.propDefn = new TranslatorPropertyDefinition(propDefn);
    }

    /**
     * @param newValue the new property value override (can be <code>null</code> or empty)
     */
    public void setValue( String newValue ) {
        if (!StringUtilities.equals(this.overriddenValue, newValue)) {
            String oldValue = this.overriddenValue;
            this.overriddenValue = newValue;

            // notify listeners of change
            PropertyChangeEvent event = new PropertyChangeEvent(this, this.propDefn.getId(), oldValue, newValue);

            for (Object listener : this.listeners.getListeners()) {
                ((PropertyChangeListener)listener).propertyChange(event);
            }
        }
    }

}