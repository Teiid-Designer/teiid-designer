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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
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
    private final Set<String> allowedValues;
    private String defaultValue;
    private final Set<Translation> descriptions;
    private final Set<Translation> displayNames;
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
        this.allowedValues = new HashSet<String>();
        this.descriptions = new HashSet<Translation>();
        this.displayNames = new HashSet<Translation>();
    }

    /**
     * @param namespacePrefix the namespace prefix (can be <code>null</code> or empty)
     * @param simpleId the property identifier without the namespace prefix (can be <code>null</code> or empty)
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
     * @param allowedValues the allowed property values (can be <code>null</code> or empty)
     * @param descriptions the one or more translations of the property description (can be <code>null</code> or empty)
     * @param displayNames the one or more translations of the property display name (can be <code>null</code> or empty)
     */
    public ModelExtensionPropertyDefinitionImpl( String namespacePrefix,
                                                 String simpleId,
                                                 String runtimeType,
                                                 String required,
                                                 String defaultValue,
                                                 String fixedValue,
                                                 String advanced,
                                                 String masked,
                                                 String index,
                                                 Set<String> allowedValues,
                                                 Set<Translation> descriptions,
                                                 Set<Translation> displayNames ) {
        this();
        this.namespacePrefix = namespacePrefix;
        this.simpleId = simpleId;
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
            this.allowedValues.add(BOOLEAN_ALLOWED_VALUES[0]);
            this.allowedValues.add(BOOLEAN_ALLOWED_VALUES[1]);
        }

        if (descriptions != null) {
            this.descriptions.addAll(descriptions);
        }

        if (displayNames != null) {
            this.displayNames.addAll(displayNames);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#addAllowedValue(java.lang.String)
     */
    @Override
    public boolean addAllowedValue( String newAllowedValue ) {
        CoreStringUtil.isEmpty(newAllowedValue);
        return this.allowedValues.add(newAllowedValue);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#addDescription(org.teiid.designer.extension.properties.Translation)
     */
    @Override
    public boolean addDescription( Translation newDescription ) {
        CoreArgCheck.isNotNull(newDescription, "newDescription is null"); //$NON-NLS-1$
        return this.descriptions.add(newDescription);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#addDisplayName(org.teiid.designer.extension.properties.Translation)
     */
    @Override
    public boolean addDisplayName( Translation newDisplayName ) {
        CoreArgCheck.isNotNull(newDisplayName, "newDisplayName is null"); //$NON-NLS-1$
        return this.displayNames.add(newDisplayName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#addListener(java.beans.PropertyChangeListener)
     */
    @Override
    public boolean addListener( PropertyChangeListener newListener ) {
        CoreArgCheck.isNotNull(newListener, "newListener is null"); //$NON-NLS-1$
        return this.listeners.addIfAbsent(newListener);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#allowedValues()
     */
    @Override
    public Set<String> allowedValues() {
        return new HashSet<String>(this.allowedValues);
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
        return this.allowedValues.toArray(new String[this.allowedValues.size()]);
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
     * Obtains the localized description for the current locale.
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        for (Translation description : this.descriptions) {
            if (Locale.getDefault().equals(description.getLocale())) {
                return description.getTranslation();
            }
        }

        // no translation found
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getDescriptions()
     */
    @Override
    public Set<Translation> getDescriptions() {
        return this.descriptions;
    }

    /**
     * {@inheritDoc}
     * 
     * Obtains the localized display name for the current locale. If no translation is available then the property identifier is
     * returned.
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getDisplayName()
     * @see #getId()
     */
    @Override
    public String getDisplayName() {
        for (Translation displayName : this.displayNames) {
            if (Locale.getDefault().equals(displayName.getLocale())) {
                return displayName.getTranslation();
            }
        }

        // no translation found
        return getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getDisplayNames()
     */
    @Override
    public Set<Translation> getDisplayNames() {
        return this.displayNames;
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
        String errorMsg = Utils.isValidValue(this.type, proposedValue, this.required, getAllowedValues());

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
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#removeAllowedValue(java.lang.String)
     */
    @Override
    public boolean removeAllowedValue( String allowedValue ) {
        CoreStringUtil.isEmpty(allowedValue);
        return this.allowedValues.remove(allowedValue);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#removeDescription(org.teiid.designer.extension.properties.Translation)
     */
    @Override
    public boolean removeDescription( Translation description ) {
        CoreArgCheck.isNotNull(description, "description is null"); //$NON-NLS-1$
        return this.descriptions.remove(description);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#removeDisplayName(org.teiid.designer.extension.properties.Translation)
     */
    @Override
    public boolean removeDisplayName( Translation displayName ) {
        CoreArgCheck.isNotNull(displayName, "displayName is null"); //$NON-NLS-1$
        return this.displayNames.remove(displayName);
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
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setAllowedValues(java.util.Set)
     */
    @Override
    public void setAllowedValues( Set<String> newAllowedValues ) {
        boolean clearAllowedValues = ((newAllowedValues == null) || newAllowedValues.isEmpty());

        if (Type.BOOLEAN == this.type) {
            boolean error = false;

            if (clearAllowedValues || (BOOLEAN_ALLOWED_VALUES.length != newAllowedValues.size())) {
                error = true;
            } else {
                String[] booleanValues = newAllowedValues.toArray(new String[BOOLEAN_ALLOWED_VALUES.length]);

                if (CoreStringUtil.isEmpty(booleanValues[0]) || CoreStringUtil.isEmpty(booleanValues[1])) {
                    error = true;
                } else if (!booleanValues[0].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[0].toLowerCase())
                        || !booleanValues[0].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[1].toLowerCase())) {
                    error = true;
                } else if (!booleanValues[1].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[0].toLowerCase())
                        || !booleanValues[1].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[1].toLowerCase())) {
                    error = true;
                }
            }

            if (error) {
                Util.log(IStatus.WARNING, NLS.bind(Messages.invalidBooleanAllowedValue, getId()));
            }

            return;
        }

        boolean changed = false;

        if (this.allowedValues.isEmpty() && !clearAllowedValues) {
            changed = true;
        } else if (!this.allowedValues.isEmpty() && clearAllowedValues) {
            changed = true;
        } else if (this.allowedValues.size() != newAllowedValues.size()) {
            changed = true;
        } else {
            // sizes are the same so see if values have changed
            for (String value : this.allowedValues) {
                if (!newAllowedValues.contains(value)) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            Object oldValue = new HashSet<String>(this.allowedValues);
            this.allowedValues.clear();
            this.allowedValues.addAll(newAllowedValues);
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
            notifyChangeListeners(PropertyName.DEFAULT_VALUE, oldValue, this.defaultValue);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setDescriptions(java.util.Set)
     */
    @Override
    public void setDescriptions( Set<Translation> newDescriptions ) {
        boolean changed = true;

        if ((newDescriptions != null) && (this.descriptions.size() == newDescriptions.size())
                && this.descriptions.containsAll(newDescriptions)) {
            changed = false;
        }

        if (changed) {
            Object oldValue = new HashSet<Translation>(this.descriptions);
            this.descriptions.clear();

            if (newDescriptions != null) {
                this.descriptions.addAll(newDescriptions);
            }

            notifyChangeListeners(PropertyName.DESCRIPTION, oldValue, this.descriptions);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setDisplayNames(java.util.Set)
     */
    @Override
    public void setDisplayNames( Set<Translation> newDisplayNames ) {
        boolean changed = true;

        if ((newDisplayNames != null) && (this.displayNames.size() == newDisplayNames.size())
                && this.displayNames.containsAll(newDisplayNames)) {
            changed = false;
        }

        if (changed) {
            Object oldValue = new HashSet<Translation>(this.displayNames);
            this.displayNames.clear();

            if (newDisplayNames != null) {
                this.displayNames.addAll(newDisplayNames);
            }

            notifyChangeListeners(PropertyName.DISPLAY_NAME, oldValue, this.displayNames);
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
