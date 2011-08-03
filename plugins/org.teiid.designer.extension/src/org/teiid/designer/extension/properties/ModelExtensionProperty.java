/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

import static org.teiid.designer.extension.ExtensionPlugin.Util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CopyOnWriteArrayList;

import org.teiid.core.properties.Property;
import org.teiid.core.properties.PropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * A <code>ModelExtensionProperty</code> is a model extension property that may have an overridden value.
 */
public class ModelExtensionProperty implements Property {

    /**
     * A collection of registered property changed listeners.
     */
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners;

    /**
     * A non-<code>null</code> value if the value is different than the default value.
     */
    private String overriddenValue;

    /**
     * The property definition (never <code>null</code>).
     */
    private final ModelExtensionPropertyDefinition propDefn;

    /**
     * The initial value is set to the default value.
     * 
     * @param propDefn the property definition (cannot be <code>null</code>)
     * @throws IllegalArgumentException if <code>propDefn</code> is <code>null</code>
     */
    public ModelExtensionProperty( ModelExtensionPropertyDefinition propDefn ) {
        CoreArgCheck.isNotNull(propDefn, "propDef is null"); //$NON-NLS-1$
        this.propDefn = propDefn;
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    }

    /**
     * @param propDefn the property definition (cannot be <code>null</code>)
     * @param initialValue the initial property value (can be <code>null</code> or empty)
     */
    public ModelExtensionProperty( ModelExtensionPropertyDefinition propDefn,
                                   String initialValue ) {
        this(propDefn);
        setValue(initialValue);
    }

    /**
     * @param listener the listener being added (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was successfully added
     */
    public boolean addListener( PropertyChangeListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener is null"); //$NON-NLS-1$
        return this.listeners.addIfAbsent(listener);
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

        return this.propDefn.getId().equals(((ModelExtensionProperty)obj).getPropertyDefinition().getId());
    }

    /**
     * @return the property definition's identifier which includes the namespace prefix and the simple property identifier (never
     *         <code>null</code>)
     */
    public String getId() {
        return this.propDefn.getId();
    }

    /**
     * @return the model extension property definition (never <code>null</code>)
     */
    public ModelExtensionPropertyDefinition getModelExtensionPropertyDefinition() {
        return this.propDefn;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This returns a {@link ModelExtensionPropertyDefinition}.
     * 
     * @see org.teiid.core.properties.Property#getPropertyDefinition()
     */
    @Override
    public PropertyDefinition getPropertyDefinition() {
        return this.propDefn;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.Property#getValue()
     */
    @Override
    public String getValue() {
        return (CoreStringUtil.isEmpty(this.overriddenValue) ? getPropertyDefinition().getDefaultValue() : this.overriddenValue);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getPropertyDefinition().getId().hashCode();
    }

    /**
     * @return <code>true</code> if the current value is different than the default value
     */
    public boolean hasOverridenValue() {
        return (this.overriddenValue != null);
    }

    /**
     * Broadcasts property change to registered listeners.
     * 
     * @param oldValue the old value (can be <code>null</code> or empty)
     * @param newValue the new value (can be <code>null</code> or empty)
     */
    private void notifyChangeListeners( final Object oldValue,
                                        final Object newValue ) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, this.propDefn.getId(), oldValue, newValue);

        for (final Object listener : this.listeners.toArray()) {
            try {
                ((PropertyChangeListener)listener).propertyChange(event);
            } catch (Exception e) {
                Util.log(e);
                this.listeners.remove(listener);
            }
        }
    }

    /**
     * @param listener the listener being removed (cannot be <code>null</code>)
     * @return <code>true</code> if listener was successfully removed
     */
    public boolean removeListener( PropertyChangeListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener is null"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * @param newValue the new property value (can be <code>null</code> or empty)
     */
    public void setValue( String newValue ) {
        String oldValue = getValue();

        // do nothing if setting to same value
        if (CoreStringUtil.equals(oldValue, newValue)) {
            return;
        }

        if (CoreStringUtil.equals(newValue, getPropertyDefinition().getDefaultValue())) {
            // make sure overriddenValue is null when setting to default value
            this.overriddenValue = null;
        } else {
            this.overriddenValue = newValue;
        }

        // alert listeners
        notifyChangeListeners(oldValue, getValue());
    }

    /**
     * The properties that can be changed.
     */
    public enum PropertyName {
        /**
         * The property definition.
         */
        PROPERTY_DEFINITION,

        /**
         * The property value.
         */
        VALUE
    }

}