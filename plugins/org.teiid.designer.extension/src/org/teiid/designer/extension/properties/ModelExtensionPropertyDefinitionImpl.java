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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.HashCodeUtil;
import org.teiid.designer.extension.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * The <code>ModelExtensionPropertyDefinitionImpl</code> is a default implementation of a model extension property definition.
 */
public class ModelExtensionPropertyDefinitionImpl implements ModelExtensionPropertyDefinition {

    private boolean advanced = ADVANCED_DEFAULT;
    private Set<String> allowedValues;
    private String defaultValue;
    private Set<Translation> descriptions;
    private Set<Translation> displayNames;
    private String fixedValue;
    private boolean index = INDEX_DEFAULT;
    private CopyOnWriteArrayList<PropertyChangeListener> listeners;
    private boolean masked = MASKED_DEFAULT;
    private NamespacePrefixProvider namespacePrefixProvider;
    private boolean required = REQUIRED_DEFAULT;
    private String simpleId;
    private Type type = TYPE_DEFAULT;

    /**
     * @param namespacePrefixProvider the namespace prefix provider (cannot be <code>null</code>)
     */
    public ModelExtensionPropertyDefinitionImpl( NamespacePrefixProvider namespacePrefixProvider ) {
        CoreArgCheck.isNotNull(namespacePrefixProvider, "namespacePrefixProvider is null"); //$NON-NLS-1$
        this.namespacePrefixProvider = namespacePrefixProvider;

        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
        this.allowedValues = new HashSet<String>();
        this.descriptions = new HashSet<Translation>();
        this.displayNames = new HashSet<Translation>();
    }

    /**
     * @param namespacePrefixProvider the namespace prefix provider (cannot be <code>null</code>)
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
    public ModelExtensionPropertyDefinitionImpl( NamespacePrefixProvider namespacePrefixProvider,
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
        this(namespacePrefixProvider);

        this.simpleId = simpleId;
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;

        if (!CoreStringUtil.isEmpty(runtimeType)) {
            try {
                Type newType = ModelExtensionPropertyDefinition.Utils.convertRuntimeType(runtimeType);
                setType(newType);
            } catch (IllegalArgumentException e) {
                Util.log(e);
            }
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

        if (allowedValues != null) {
            this.allowedValues.addAll(allowedValues);
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
        boolean added = this.allowedValues.add(newAllowedValue);

        if (added) {
            notifyChangeListeners(PropertyName.ALLOWED_VALUES, null, newAllowedValue);
        }

        return added;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#addDescription(org.teiid.designer.extension.properties.Translation)
     */
    @Override
    public boolean addDescription( Translation newDescription ) {
        CoreArgCheck.isNotNull(newDescription, "newDescription is null"); //$NON-NLS-1$
        boolean added = this.descriptions.add(newDescription);

        if (added) {
            notifyChangeListeners(PropertyName.DESCRIPTION, null, newDescription);
        }

        return added;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#addDisplayName(org.teiid.designer.extension.properties.Translation)
     */
    @Override
    public boolean addDisplayName( Translation newDisplayName ) {
        CoreArgCheck.isNotNull(newDisplayName, "newDisplayName is null"); //$NON-NLS-1$
        boolean added = this.displayNames.add(newDisplayName);

        if (added) {
            notifyChangeListeners(PropertyName.DISPLAY_NAME, null, newDisplayName);
        }

        return added;
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
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        try {
            ModelExtensionPropertyDefinitionImpl copy = (ModelExtensionPropertyDefinitionImpl)super.clone();

            // no listeners
            copy.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();

            // deep copy of allowed values
            copy.allowedValues = new HashSet<String>(this.allowedValues);

            // deep copy of descriptions
            copy.descriptions = new HashSet<Translation>();

            for (Translation description : this.descriptions) {
                copy.descriptions.add((Translation)description.clone());
            }

            // deep copy of display names
            copy.displayNames = new HashSet<Translation>(this.displayNames);

            for (Translation description : this.descriptions) {
                copy.descriptions.add((Translation)description.clone());
            }

            return copy;
        } catch (CloneNotSupportedException e) {
            throw new Error("should never happen"); //$NON-NLS-1$
        }
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

        ModelExtensionPropertyDefinition that = (ModelExtensionPropertyDefinition)obj;

        // boolean properties
        if ((getType() != that.getType()) || (isAdvanced() != that.isAdvanced()) || (shouldBeIndexed() != that.shouldBeIndexed())
                || (isMasked() != that.isMasked()) || (isRequired() != that.isRequired())) {
            return false;
        }

        // string properties
        if (!CoreStringUtil.valuesAreEqual(getSimpleId(), that.getSimpleId())
                || !CoreStringUtil.valuesAreEqual(getNamespacePrefix(), that.getNamespacePrefix())
                || !CoreStringUtil.valuesAreEqual(getFixedValue(), that.getFixedValue())
                || !CoreStringUtil.valuesAreEqual(getDefaultValue(), that.getDefaultValue())) {
            return false;
        }

        // allowed values
        Set<String> thisValues = allowedValues();
        Set<String> thatValues = that.allowedValues();

        if (thisValues.size() != thatValues.size()) {
            return false;
        }
        
        if (!thisValues.isEmpty() && !thisValues.containsAll(thatValues)) {
            return false;
        }

        // descriptions
        Set<Translation> thisDescriptions = getDescriptions();
        Set<Translation> thatDescriptions = that.getDescriptions();

        if (thisDescriptions.size() != thatDescriptions.size()) {
            return false;
        }
        
        if (!thisDescriptions.isEmpty() && !thisDescriptions.containsAll(thatDescriptions)) {
            return false;
        }

        // display names
        Set<Translation> thisDisplayNames = getDisplayNames();
        Set<Translation> thatDisplayNames = that.getDisplayNames();

        if (thisDisplayNames.size() != thatDisplayNames.size()) {
            return false;
        }
        
        if (!thisDisplayNames.isEmpty() && !thisDisplayNames.containsAll(thatDisplayNames)) {
            return false;
        }

        return true;
    }

    private String findTranslationMatch( Set<Translation> translations ) {
        Locale defaultLocale = Locale.getDefault();
        String defaultLanguage = defaultLocale.getLanguage();
        String defaultCountry = defaultLocale.getCountry();

        String languageCountryMatch = null;
        String languageMatch = null;

        for (Translation translation : translations) {
            Locale locale = translation.getLocale();

            // return exact match
            if (defaultLocale.equals(translation.getLocale())) {
                return translation.getTranslation();
            }

            // look for language and country match
            String language = locale.getLanguage();
            String country = locale.getCountry();

            if (CoreStringUtil.equals(defaultLanguage, language) && CoreStringUtil.equals(defaultCountry, country)) {
                languageCountryMatch = translation.getTranslation();
                continue;
            }

            // look for language match
            if (CoreStringUtil.equals(defaultLanguage, language)) {
                languageMatch = translation.getTranslation();
            }
        }

        return ((languageCountryMatch == null) ? languageMatch : languageCountryMatch);
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
        return findTranslationMatch(this.descriptions);
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
        return findTranslationMatch(this.displayNames);
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
        return Utils.getPropertyId(getNamespacePrefix(), this.simpleId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the {@link NamespacePrefixProvider namespace prefix provider} has been set, it will be used to provide the namespace
     * prefix.
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getNamespacePrefix()
     */
    @Override
    public String getNamespacePrefix() {
        return this.namespacePrefixProvider.getNamespacePrefix();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#getRuntimeType()
     */
    @Override
    public String getRuntimeType() {
        return ((this.type == null) ? null : this.type.getRuntimeType());
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
        int result = HashCodeUtil.hashCode(0, getType());

        // boolean properties
        result = HashCodeUtil.hashCode(result, isAdvanced());
        result = HashCodeUtil.hashCode(result, shouldBeIndexed());
        result = HashCodeUtil.hashCode(result, isMasked());
        result = HashCodeUtil.hashCode(result, isRequired());

        // string properties
        if (!CoreStringUtil.isEmpty(getSimpleId())) {
            result = HashCodeUtil.hashCode(result, getSimpleId());
        }

        if (!CoreStringUtil.isEmpty(getNamespacePrefix())) {
            result = HashCodeUtil.hashCode(result, getNamespacePrefix());
        }

        if (!CoreStringUtil.isEmpty(getFixedValue())) {
            result = HashCodeUtil.hashCode(result, getFixedValue());
        }

        if (!CoreStringUtil.isEmpty(getDefaultValue())) {
            result = HashCodeUtil.hashCode(result, getDefaultValue());
        }

        // allowed values
        if (!allowedValues().isEmpty()) {
            List<String> sortedValues = new ArrayList<String>(allowedValues());
            Collections.sort(sortedValues);

            for (String allowedValue : sortedValues) {
                result = HashCodeUtil.hashCode(result, allowedValue);
            }
        }

        // descriptions
        Set<Translation> translations = getDescriptions();

        if (!translations.isEmpty()) {
            List<Translation> sortedDescriptions = new ArrayList<Translation>(translations);
            Collections.sort(sortedDescriptions);

            for (Translation description : sortedDescriptions) {
                result = HashCodeUtil.hashCode(result, description);
            }
        }

        // display names
        translations = getDisplayNames();

        if (!translations.isEmpty()) {
            List<Translation> sortedDisplayNames = new ArrayList<Translation>(translations);
            Collections.sort(sortedDisplayNames);

            for (Translation description : sortedDisplayNames) {
                result = HashCodeUtil.hashCode(result, description);
            }
        }

        return result;
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
        return CoreStringUtil.isEmpty(this.fixedValue);
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
    public boolean removeAllowedValue( String allowedValueBeingRemoved ) {
        CoreStringUtil.isEmpty(allowedValueBeingRemoved);
        Object oldValue = allowedValueBeingRemoved;
        boolean removed = this.allowedValues.remove(allowedValueBeingRemoved);

        if (removed) {
            notifyChangeListeners(PropertyName.ALLOWED_VALUES, oldValue, null);
        }

        return removed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#removeDescription(org.teiid.designer.extension.properties.Translation)
     */
    @Override
    public boolean removeDescription( Translation descriptionBeingRemoved ) {
        CoreArgCheck.isNotNull(descriptionBeingRemoved, "description is null"); //$NON-NLS-1$
        Object oldValue = descriptionBeingRemoved;
        boolean removed = this.descriptions.remove(descriptionBeingRemoved);

        if (removed) {
            notifyChangeListeners(PropertyName.DESCRIPTION, oldValue, null);
        }

        return removed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#removeDisplayName(org.teiid.designer.extension.properties.Translation)
     */
    @Override
    public boolean removeDisplayName( Translation displayNameBeingRemoved ) {
        CoreArgCheck.isNotNull(displayNameBeingRemoved, "displayName is null"); //$NON-NLS-1$
        Object oldValue = displayNameBeingRemoved;
        boolean removed = this.displayNames.remove(displayNameBeingRemoved);

        if (removed) {
            notifyChangeListeners(PropertyName.DISPLAY_NAME, oldValue, null);
        }

        return removed;
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
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setAdvanced(boolean)
     */
    @Override
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
                        && !booleanValues[0].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[1].toLowerCase())) {
                    error = true;
                } else if (!booleanValues[1].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[0].toLowerCase())
                        && !booleanValues[1].toLowerCase().equals(BOOLEAN_ALLOWED_VALUES[1].toLowerCase())) {
                    error = true;
                }
            }

            if (error) {
                Util.log(IStatus.WARNING, NLS.bind(Messages.invalidBooleanAllowedValue, getId()));
                newAllowedValues = new HashSet<String>(2);
                newAllowedValues.add(BOOLEAN_ALLOWED_VALUES[0]);
                newAllowedValues.add(BOOLEAN_ALLOWED_VALUES[1]);
            }
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
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setDefaultValue(java.lang.String)
     */
    @Override
    public void setDefaultValue( String newDefaultValue ) {
        if (!CoreStringUtil.valuesAreEqual(this.defaultValue, newDefaultValue)) {
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
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setFixedValue(java.lang.String)
     */
    @Override
    public void setFixedValue( String newFixedValue ) {
        if (!CoreStringUtil.valuesAreEqual(this.fixedValue, newFixedValue)) {
            String oldValue = this.fixedValue;
            this.fixedValue = newFixedValue;
            notifyChangeListeners(PropertyName.FIXED_VALUE, oldValue, this.fixedValue);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setIndex(boolean)
     */
    @Override
    public void setIndex( boolean newIndex ) {
        if (this.index != newIndex) {
            this.index = newIndex;
            notifyChangeListeners(PropertyName.INDEX, !this.index, this.index);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setMasked(boolean)
     */
    @Override
    public void setMasked( boolean newMasked ) {
        if (this.masked != newMasked) {
            this.masked = newMasked;
            notifyChangeListeners(PropertyName.MASKED, !this.masked, this.masked);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setNamespacePrefixProvider(org.teiid.designer.extension.properties.NamespacePrefixProvider)
     */
    @Override
    public void setNamespacePrefixProvider( NamespacePrefixProvider newNamespacePrefixProvider ) {
        this.namespacePrefixProvider = newNamespacePrefixProvider;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setRequired(boolean)
     */
    @Override
    public void setRequired( boolean newRequired ) {
        if (this.required != newRequired) {
            this.required = newRequired;
            notifyChangeListeners(PropertyName.REQUIRED, !this.required, this.required);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition#setSimpleId(java.lang.String)
     */
    @Override
    public void setSimpleId( String newSimpleId ) {
        if (!CoreStringUtil.valuesAreEqual(this.simpleId, newSimpleId)) {
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
    public void setType( Type newRuntimeType ) {
        if (this.type != newRuntimeType) {
            Type oldValue = this.type;
            this.type = newRuntimeType;
            notifyChangeListeners(PropertyName.TYPE, oldValue, this.type);

            if (Type.BOOLEAN == this.type) {
                Set<String> newAllowedValues = new HashSet<String>(2);
                newAllowedValues.add(BOOLEAN_ALLOWED_VALUES[0]);
                newAllowedValues.add(BOOLEAN_ALLOWED_VALUES[1]);
                setAllowedValues(newAllowedValues);
            }
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
