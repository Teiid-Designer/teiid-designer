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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * The <code>ModelExtensionPropertyDefinitionImpl</code> is a default implementation of a model extension property definition.
 */
public class ModelExtensionPropertyDefinitionImpl implements ModelExtensionPropertyDefinition {

    private final boolean advanced;
    private String[] allowedValues;
    private String defaultValue;
    private String description;
    private String displayName;
    private String fixedValue;
    private final boolean index;
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners;
    private final boolean masked;
    private final String namespacePrefix;
    private final boolean required;
    private final String simpleId;
    private final Type type;

    /**
     * @param namespacePrefix the namespace prefix (cannot be <code>null</code> or empty)
     * @param simpleId the property identifier without the namespace prefix (cannot be <code>null</code> or empty)
     * @param displayName the display name (may be <code>null</code> or empty)
     * @param runtimeType the Teiid runtime type (cannot be <code>null</code> or empty)
     * @param required <code>true</code> string if this property must have a value (cannot be <code>null</code> or empty)
     * @param defaultValue a default value (can be <code>null</code> or empty)
     * @param fixedValue a constant value, when non-<code>null</code> and non-empty, indicates the property value cannot be changed
     *            (can be <code>null</code> or empty)
     * @param advanced <code>true</code> string if this property should only be shown to advances users (cannot be <code>null</code>
     *            or empty)
     * @param masked <code>true</code> string if this property value must be masked (cannot be <code>null</code> or empty)
     * @param index <code>true</code> string if this property should be indexed for use by the Teiid server (cannot be
     *            <code>null</code> or empty)
     */
    public ModelExtensionPropertyDefinitionImpl( String namespacePrefix,
                                                 String simpleId,
                                                 String displayName,
                                                 String runtimeType,
                                                 String required,
                                                 String defaultValue,
                                                 String fixedValue,
                                                 String advanced,
                                                 String masked,
                                                 String index ) {
        CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(simpleId, "simpleId is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(runtimeType, "runtimeType is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(required, "required is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(advanced, "advanced is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(masked, "masked is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(index, "index is empty"); //$NON-NLS-1$

        this.namespacePrefix = namespacePrefix;
        this.simpleId = simpleId;
        this.displayName = displayName;
        this.type = ModelExtensionPropertyDefinition.Utils.convertRuntimeType(runtimeType);
        this.required = Boolean.parseBoolean(required);
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;
        this.advanced = Boolean.parseBoolean(advanced);
        this.masked = Boolean.parseBoolean(masked);
        this.index = Boolean.parseBoolean(index);
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();

        if (Type.BOOLEAN == this.type) {
            this.allowedValues = BOOLEAN_ALLOWED_VALUES;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#addListener(java.beans.PropertyChangeListener)
     */
    @Override
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

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        return getId().equals(((ModelExtensionPropertyDefinitionImpl)obj).getId());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getAllowedValues()
     */
    @Override
    public String[] getAllowedValues() {
        return this.allowedValues;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getDefaultValue()
     */
    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * {@inheritDoc}
     * 
     * Note: If a localized display name is not set the ID is used.
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return (CoreStringUtil.isEmpty(this.displayName) ? getId() : this.displayName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getId()
     */
    @Override
    public String getId() {
        return Utils.getPropertyId(this.namespacePrefix, this.simpleId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getNamespacePrefix()
     */
    @Override
    public String getNamespacePrefix() {
        return this.namespacePrefix;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getRuntimeType()
     */
    @Override
    public String getRuntimeType() {
        return this.type.getRuntimeType();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getSimpleId()
     */
    @Override
    public String getSimpleId() {
        return this.simpleId;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#isAdvanced()
     */
    @Override
    public boolean isAdvanced() {
        return this.advanced;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#isMasked()
     */
    @Override
    public boolean isMasked() {
        return this.masked;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#isModifiable()
     */
    @Override
    public boolean isModifiable() {
        return (this.fixedValue == null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#isRequired()
     */
    @Override
    public boolean isRequired() {
        return this.required;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#isValidValue(java.lang.String)
     */
    @Override
    public String isValidValue( String proposedValue ) {
        // must have a value
        if (CoreStringUtil.isEmpty(proposedValue) && this.required) {
            return NLS.bind(Messages.emptyPropertyValue, getId());
        }

        // validate against allowed values first
        if ((this.allowedValues != null) && (this.allowedValues.length != 0)) {
            for (String allowedValue : this.allowedValues) {
                if (allowedValue.equals(proposedValue)) {
                    // valid
                    return null;
                }
            }

            // must match an allowed value
            return NLS.bind(Messages.valueDoesNotMatchAnAllowedValue, proposedValue, getId());
        }

        // no validation done on these types
        if ((Type.STRING == this.type) || (Type.BLOB == this.type) || (Type.CLOB == this.type) || (Type.OBJECT == this.type)
                || (Type.XML == this.type)) {
            return null; // valid
        }

        if (Type.BOOLEAN == this.type) {
            if (!proposedValue.equalsIgnoreCase(Boolean.TRUE.toString())
                    && !proposedValue.equalsIgnoreCase(Boolean.FALSE.toString())) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.BOOLEAN);
            }
        } else if (Type.CHAR == this.type) {
            if (proposedValue.length() != 1) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.CHAR);
            }
        } else if (Type.BYTE == this.type) {
            try {
                Byte.parseByte(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.BYTE);
            }
        } else if (Type.SHORT == this.type) {
            try {
                Short.parseShort(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.SHORT);
            }
        } else if (Type.INTEGER == this.type) {
            try {
                Integer.parseInt(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.INTEGER);
            }
        } else if (Type.LONG == this.type) {
            try {
                Long.parseLong(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.LONG);
            }
        } else if (Type.FLOAT == this.type) {
            try {
                Float.parseFloat(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.FLOAT);
            }
        } else if (Type.DOUBLE == this.type) {
            try {
                Double.parseDouble(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.DOUBLE);
            }
        } else if (Type.BIG_INTEGER == this.type) {
            try {
                new BigInteger(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.BIG_INTEGER);
            }
        } else if (Type.BIG_DECIMAL == this.type) {
            try {
                new BigDecimal(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.BIG_DECIMAL);
            }
        } else if (Type.DATE == this.type) {
            try {
                Date.valueOf(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.DATE);
            }
        } else if (Type.TIME == this.type) {
            try {
                Time.valueOf(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.TIME);
            }
        } else if (Type.TIMESTAMP == this.type) {
            try {
                Timestamp.valueOf(proposedValue);
            } catch (Exception e) {
                return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, Type.TIMESTAMP);
            }
        } else {
            // unknown property type
            return NLS.bind(Messages.unknownPropertyType, getId(), this.type);
        }

        return null;
    }

    /**
     * @param property the property that was changed (never <code>null</code>)
     * @param oldValue the old value (can be <code>null</code>)
     * @param newValue the new value (can be <code>null</code>)
     */
    private void notifyChangeListeners( final PropertyName property,
                                        final Object oldValue,
                                        final Object newValue ) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property.toString(), oldValue, newValue);

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
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#removeListener(java.beans.PropertyChangeListener)
     */
    @Override
    public boolean removeListener( PropertyChangeListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener is null"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setAllowedValues(java.lang.String[])
     */
    @Override
    public void setAllowedValues( String[] newAllowedValues ) {
        if (Type.BOOLEAN == this.type) {
            boolean error = false;

            if ((newAllowedValues == null) || (BOOLEAN_ALLOWED_VALUES.length != newAllowedValues.length)) {
                error = true;
            } else {
                if (CoreStringUtil.isEmpty(newAllowedValues[0]) || CoreStringUtil.isEmpty(newAllowedValues[1])) {
                    error = true;
                } else if (!newAllowedValues[0].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[0].toLowerCase())
                        || !newAllowedValues[0].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[1].toLowerCase())) {
                    error = true;
                } else if (!newAllowedValues[1].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[0].toLowerCase())
                        || !newAllowedValues[1].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[1].toLowerCase())) {
                    error = true;
                }
            }

            if (error) {
                Util.log(IStatus.WARNING, NLS.bind(Messages.invalidBooleanAllowedValue, getId()));
            }

            return;
        }

        Object oldValue = this.allowedValues;
        boolean changed = false;

        if (this.allowedValues == null) {
            if (newAllowedValues != null) {
                changed = true;
            }
        } else {
            if (newAllowedValues == null) {
                changed = true;
            } else if (this.allowedValues.length == newAllowedValues.length) {
                // see if values have changed
                Collection<String> newValuesCollection = Arrays.asList(newAllowedValues);

                for (String value : this.allowedValues) {
                    if (!newValuesCollection.contains(value)) {
                        changed = true;
                        break;
                    }
                }
            }
        }

        if (changed && (newAllowedValues != null)) {
            Collection<String> newValues = new ArrayList<String>(newAllowedValues.length);

            for (String value : newAllowedValues) {
                String errorMsg = isValidValue(value);

                if (CoreStringUtil.isEmpty(errorMsg)) {
                    newValues.add(value);
                } else {
                    Util.log(IStatus.ERROR, NLS.bind(Messages.invalidAllowedValues, getId(), errorMsg));
                    break;
                }
            }

            if (newValues.size() != newAllowedValues.length) {
                changed = newValues.isEmpty();

                if (changed) {
                    newAllowedValues = newValues.toArray(new String[newValues.size()]);
                }
            }
        }

        if (changed) {
            this.allowedValues = newAllowedValues;
            notifyChangeListeners(PropertyName.ALLOWED_VALUES, oldValue, this.allowedValues);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setDescription(java.lang.String)
     */
    @Override
    public void setDescription( String newDescription ) {
        if (!CoreStringUtil.equals(this.description, newDescription)) {
            Object oldValue = this.description;
            this.description = newDescription;

            // alert listeners
            notifyChangeListeners(PropertyName.DESCRIPTION, oldValue, this.description);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#shouldBeIndexed()
     */
    @Override
    public boolean shouldBeIndexed() {
        return this.index;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Model Extension Property: id=" + getId(); //$NON-NLS-1$
    }

}
