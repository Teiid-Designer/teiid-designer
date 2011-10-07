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

    public static final boolean ADVANCED_DEFAULT = false;
    public static final boolean INDEX_DEFAULT = true;
    public static final boolean MASKED_DEFAULT = false;
    public static final boolean REQUIRED_DEFAULT = false;
    public static final Type TYPE_DEFAULT = Type.STRING;

    private boolean advanced = ADVANCED_DEFAULT;
    private String[] allowedValues;
    private String defaultValue;
    private String description;
    private String displayName;
    private String fixedValue;
    private boolean index = INDEX_DEFAULT;
    private CopyOnWriteArrayList<PropertyChangeListener> listeners;
    private boolean masked = MASKED_DEFAULT;
    private String namespacePrefix;
    private boolean required = REQUIRED_DEFAULT;
    private String simpleId;
    private Type type = TYPE_DEFAULT;

    public ModelExtensionPropertyDefinitionImpl() {
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    }

    /**
     * @param namespacePrefix the namespace prefix (can be <code>null</code> or empty)
     * @param simpleId the property identifier without the namespace prefix (can be <code>null</code> or empty)
     * @param displayName the display name (can be <code>null</code> or empty)
     * @param runtimeType the Teiid runtime type (can be <code>null</code> or empty). Default value is {@value Type#STRING}.
     * @param required <code>true</code> string if this property must have a value (can be <code>null</code> or empty). Default
     *            value is {@value #REQUIRED_DEFAULT}.
     * @param defaultValue a default value (can be <code>null</code> or empty)
     * @param fixedValue a constant value, when non-<code>null</code> and non-empty, indicates the property value cannot be changed
     *            (can be <code>null</code> or empty)
     * @param advanced <code>true</code> string if this property should only be shown to advances users (can be <code>null</code> or
     *            empty). Default value is {@value #ADVANCED_DEFAULT}.
     * @param masked <code>true</code> string if this property value must be masked (can be <code>null</code> or empty). Default
     *            value is {@value #MASKED_DEFAULT}.
     * @param index <code>true</code> string if this property should be indexed for use by the Teiid server (can be
     *            <code>null</code> or empty). Default value is {@value #INDEX_DEFAULT}.
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
        this();
        this.namespacePrefix = namespacePrefix;
        this.simpleId = simpleId;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;

        if (!CoreStringUtil.isEmpty(runtimeType)) {
            this.type = ModelExtensionPropertyDefinition.Utils.convertRuntimeType(runtimeType);
        }

        if (!CoreStringUtil.isEmpty(required)) {
            this.required = Boolean.parseBoolean(required);
        }

        if (!CoreStringUtil.isEmpty(advanced)) {
            this.advanced = Boolean.parseBoolean(advanced);
        }

        if (!CoreStringUtil.isEmpty(masked)) {
            this.masked = Boolean.parseBoolean(masked);
        }

        if (!CoreStringUtil.isEmpty(index)) {
            this.index = Boolean.parseBoolean(index);
        }

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
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getFixedValue()
     */
    @Override
    public String getFixedValue() {
        return this.fixedValue;
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
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getType()
     */
    @Override
    public Type getType() {
        return this.type;
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
        String errorMsg = Utils.isValidValue(this.type, proposedValue, this.required, this.allowedValues);

        if (!CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = NLS.bind(Messages.appendPropertyId, errorMsg, getId());
        }

        return errorMsg;
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
     * @param newAdvanced the new advanced value
     */
    public void setAdvanced( boolean newAdvanced ) {
        if (this.advanced != newAdvanced) {
            this.advanced = newAdvanced;
            notifyChangeListeners(PropertyName.ADVANCED, !this.advanced, this.advanced);
        }
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
                    Util.log(IStatus.ERROR, NLS.bind(Messages.appendPropertyId, errorMsg, getId()));
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
     * @param newDefaultValue the new default value (can be <code>null</code> or empty)
     */
    public void setDefaultValue( String newDefaultValue ) {
        if (!CoreStringUtil.equals(this.defaultValue, newDefaultValue)) {
            String oldValue = this.defaultValue;
            this.defaultValue = newDefaultValue;
            notifyChangeListeners(PropertyName.DEFAULT_VALUE, oldValue, this.description);
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
            notifyChangeListeners(PropertyName.DESCRIPTION, oldValue, this.description);
        }
    }

    /**
     * @param newDisplayName the new display name (can be <code>null</code> or empty)
     */
    public void setDisplayName( String newDisplayName ) {
        if (!CoreStringUtil.equals(this.displayName, newDisplayName)) {
            String oldValue = this.displayName;
            this.displayName = newDisplayName;
            notifyChangeListeners(PropertyName.DISPLAY_NAME, oldValue, this.displayName);
        }
    }

    /**
     * @param newFixedValue the new fixed value (can be <code>null</code> or empty)
     */
    public void setFixedValue( String newFixedValue ) {
        if (!CoreStringUtil.equals(this.fixedValue, newFixedValue)) {
            String oldValue = this.fixedValue;
            this.fixedValue = newFixedValue;
            notifyChangeListeners(PropertyName.FIXED_VALUE, oldValue, this.fixedValue);
        }
    }

    /**
     * @param newIndex the new index value
     */
    public void setIndex( boolean newIndex ) {
        if (this.index != newIndex) {
            this.index = newIndex;
            notifyChangeListeners(PropertyName.INDEX, !this.index, this.index);
        }
    }

    /**
     * @param newMasked the new masked value
     */
    public void setMasked( boolean newMasked ) {
        if (this.masked != newMasked) {
            this.masked = newMasked;
            notifyChangeListeners(PropertyName.MASKED, !this.masked, this.masked);
        }
    }

    /**
     * @param newNamespacePrefix the new namespace prefix (can be <code>null</code> or empty)
     */
    public void setNamespacePrefix( String newNamespacePrefix ) {
        if (!CoreStringUtil.equals(this.namespacePrefix, newNamespacePrefix)) {
            String oldValue = this.namespacePrefix;
            this.namespacePrefix = newNamespacePrefix;
            notifyChangeListeners(PropertyName.FIXED_VALUE, oldValue, this.namespacePrefix);
        }
    }

    /**
     * @param newRequired the new required value
     */
    public void setRequired( boolean newRequired ) {
        if (this.required != newRequired) {
            this.required = newRequired;
            notifyChangeListeners(PropertyName.REQUIRED, !this.required, this.required);
        }
    }

    /**
     * @param newSimpleId the new simpleId (can be <code>null</code> or empty)
     */
    public void setSimpleId( String newSimpleId ) {
        if (!CoreStringUtil.equals(this.simpleId, newSimpleId)) {
            String oldValue = this.simpleId;
            this.simpleId = newSimpleId;
            notifyChangeListeners(PropertyName.SIMPLE_ID, oldValue, this.simpleId);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setType(org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Type)
     */
    @Override
    public void setType( Type runtimeType ) {
        this.type = runtimeType;
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
