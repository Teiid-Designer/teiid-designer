/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static org.teiid.designer.extension.ExtensionPlugin.Util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osgi.util.NLS;
import org.teiid.core.HashCodeUtil;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.NamespaceProvider;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * A <code>ModelExtensionDefinition</code> defines extension properties for metaclasses within a metamodel.
 */
public class ModelExtensionDefinition implements NamespaceProvider, PropertyChangeListener {

    /**
     * The model extension assistant (never <code>null</code>).
     */
    private final ModelExtensionAssistant assistant;

    /**
     * Indicates if this MED is a built-in and therefore should not be unregistered. Defaults to {@value} .
     */
    private boolean builtIn;

    /**
     * The registered property change listeners (never <code>null</code>).
     */
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners;

    /**
     * Key is metaclass name, value is a collection of property definitions.
     */
    private final Map<String, Collection<ModelExtensionPropertyDefinition>> properties;

    /**
     * The ModelExtensionDefinitionHeader of this definition (never <code>null</code>).
     */
    private final ModelExtensionDefinitionHeader header;

    public ModelExtensionDefinition( ModelExtensionAssistant assistant ) {
        CoreArgCheck.isNotNull(assistant, "assistant is null"); //$NON-NLS-1$

        this.assistant = assistant;
        this.properties = new HashMap<String, Collection<ModelExtensionPropertyDefinition>>();
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
        this.header = new ModelExtensionDefinitionHeader();
        this.header.addListener(this);
    }

    /**
     * @param assistant the model extension assist (cannot be <code>null</code>)
     * @param namespacePrefix the unique namespace prefix (can be <code>null</code> or empty)
     * @param namespaceUri the unique namespace URI (can be <code>null</code> or empty)
     * @param metamodelUri the metamodel URI that is being extended (can be <code>null</code> or empty)
     * @param description the description of the definition (can be <code>null</code> or empty)
     * @param version the definition version (can be <code>null</code> or empty)
     */
    public ModelExtensionDefinition( ModelExtensionAssistant assistant,
                                     String namespacePrefix,
                                     String namespaceUri,
                                     String metamodelUri,
                                     String description,
                                     String version ) {
        this(assistant);

        int versionNumber = ModelExtensionDefinitionHeader.DEFAULT_VERSION;

        if (!CoreStringUtil.isEmpty(version)) {
            try {
                versionNumber = Integer.parseInt(version);
                this.header.setVersion(versionNumber);
            } catch (Exception e) {
                ExtensionConstants.UTIL.log(NLS.bind(Messages.invalidDefinitionFileNewVersion, namespacePrefix, version));
            }
        }

        this.header.setNamespacePrefix(namespacePrefix);
        this.header.setNamespaceUri(namespaceUri);
        this.header.setMetamodelUri(metamodelUri);
        this.header.setDescription(description);
    }

    /**
     * @param listener the listener being added (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was successfully added
     */
    public boolean addListener( PropertyChangeListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener is null"); //$NON-NLS-1$
        boolean added = this.listeners.addIfAbsent(listener);

        if (added) {
            added = this.header.addListener(listener);
        }

        return added;
    }

    /**
     * @param metaclassName the name of the metaclass that can be extended (never <code>null</code> or empty)
     * @return <code>true</code> if the metaclass was added
     */
    public boolean addMetaclass( String metaclassName ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is empty"); //$NON-NLS-1$

        if (this.properties.containsKey(metaclassName)) {
            return false;
        }

        this.properties.put(metaclassName, new ArrayList<ModelExtensionPropertyDefinition>());
        notifyChangeListeners(PropertyName.METACLASS, null, metaclassName);
        return true;
    }

    /**
     * @param modelType the model type being added (cannot be <code>null</code> or empty)
     * @return <code>true</code> if the model type was added
     */
    public boolean addModelType( String modelType ) {
        return getHeader().addModelType(modelType);
    }

    /**
     * @param metaclassName the metaclass name to which the extension property definition pertains to (cannot be <code>null</code>
     *            or empty)
     * @param propDefn the property definition being added (cannot be <code>null</code>)
     * @return <code>true</code> if the property definition was successfully added to the metaclass name
     */
    public boolean addPropertyDefinition( String metaclassName,
                                          ModelExtensionPropertyDefinition propDefn ) {
        CoreArgCheck.isNotNull(propDefn, "propDefn is null"); //$NON-NLS-1$

        Collection<ModelExtensionPropertyDefinition> props = internalGetProperties(metaclassName);

        if (props == null) {
            if (addMetaclass(metaclassName)) {
                props = internalGetProperties(metaclassName);
            } else {
                return false;
            }
        }

        // add new property and alert listeners
        if (props.contains(propDefn)) {
            return false;
        }

        props.add(propDefn);
        notifyChangeListeners(PropertyName.PROPERTY_DEFINITION, null, propDefn);

        return true;
    }

    /**
     * @param metaclassName the name of the metaclass that the properties will be added to (cannot be <code>null</code> or empty)
     * @param propDefns the property definitions being added (cannot be <code>null</code>)
     * @return <code>true</code> if one or more property definitions were added
     */
    private boolean addPropertyDefinitions( String metaclassName,
                                            Collection<ModelExtensionPropertyDefinition> propDefns ) {
        CoreArgCheck.isNotNull(propDefns, "propDefns is null"); //$NON-NLS-1$
        boolean added = false;

        for (ModelExtensionPropertyDefinition propDefn : propDefns) {
            if (addPropertyDefinition(metaclassName, propDefn)) {
                added = true;
            }
        }

        return added;
    }

    /**
     * @param metaclassName the metaclass name being checked (cannot be <code>null</code> or empty)
     * @return <code>true</code> if this definition has extension properties for the specified metaclass name
     */
    public boolean extendsMetaclass( String metaclassName ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is null"); //$NON-NLS-1$
        return this.properties.containsKey(metaclassName);
    }

    /**
     * @param metamodelUri the metamodel URI being checked (cannot be <code>null</code> or empty)
     * @return <code>true</code> if this definition extends the specified metamodel URI
     */
    public boolean extendsMetamodelUri( String metamodelUri ) {
        CoreArgCheck.isNotEmpty(metamodelUri, "metamodelUri is null"); //$NON-NLS-1$
        return getMetamodelUri().equals(metamodelUri);
    }

    /**
     * @return the description (can be <code>null</code> or empty)
     */
    public String getDescription() {
        return this.header.getDescription();
    }

    /**
     * @return the metaclass names that have extended properties defined (never <code>null</code>)
     */
    public String[] getExtendedMetaclasses() {
        return this.properties.keySet().toArray(new String[this.properties.size()]);
    }

    /**
     * @return the metamodel URI (can be <code>null</code> or empty)
     */
    public String getMetamodelUri() {
        return this.header.getMetamodelUri();
    }

    /**
     * @return the model extension assistant (never <code>null</code>)
     */
    public ModelExtensionAssistant getModelExtensionAssistant() {
        return this.assistant;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespacePrefix()
     */
    @Override
    public String getNamespacePrefix() {
        return this.header.getNamespacePrefix();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
     */
    @Override
    public String getNamespaceUri() {
        return this.header.getNamespaceUri();
    }

    /**
     * @return the header (never <code>null</code> or empty)
     */
    public ModelExtensionDefinitionHeader getHeader() {
        return this.header;
    }

    /**
     * @param metaclassName the metaclass name whose extended property is being requested (cannot be <code>null</code> or empty)
     * @param propId the identifier of the property definition being requested (cannot be <code>null</code> or empty)
     * @return the property definition or <code>null</code> if not found
     */
    public ModelExtensionPropertyDefinition getPropertyDefinition( String metaclassName,
                                                                   String propId ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(propId, "propId is empty"); //$NON-NLS-1$

        // make sure property ID is one for this definition
        if (!ModelExtensionPropertyDefinition.Utils.isExtensionPropertyId(propId, this)) {
            return null;
        }

        Collection<ModelExtensionPropertyDefinition> props = internalGetProperties(metaclassName);

        // definition does not have any properties for that metaclass
        if (props == null) {
            return null;
        }

        // find property
        for (ModelExtensionPropertyDefinition propDefn : props) {
            if (propId.equals(propDefn.getId())) {
                return propDefn;
            }
        }

        // not found
        return null;
    }

    /**
     * @return a copy of the collection of extension property definitions (never <code>null</code> but can be empty)
     */
    public Map<String, Collection<ModelExtensionPropertyDefinition>> getPropertyDefinitions() {
        Map<String, Collection<ModelExtensionPropertyDefinition>> properties = new HashMap<String, Collection<ModelExtensionPropertyDefinition>>();

        for (Map.Entry<String, Collection<ModelExtensionPropertyDefinition>> entry : this.properties.entrySet()) {
            Collection<ModelExtensionPropertyDefinition> propDefns = new ArrayList<ModelExtensionPropertyDefinition>();
            propDefns.addAll(entry.getValue());

            String metaclassName = entry.getKey();
            properties.put(metaclassName, propDefns);
        }

        return properties;
    }

    /**
     * @param metaclassName the metaclass name whose extended property definitions are being requested (cannot be <code>null</code>
     *            or empty)
     * @return the extension property definitions (never <code>null</code> but can be empty)
     */
    public Collection<ModelExtensionPropertyDefinition> getPropertyDefinitions( String metaclassName ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is null"); //$NON-NLS-1$
        Collection<ModelExtensionPropertyDefinition> props = internalGetProperties(metaclassName);

        if (props == null) {
            return Collections.emptyList();
        }

        return props;
    }

    /**
     * If an empty collection is returned then all model types are supported.
     * 
     * @return an unmodifiable collection of supported model types (never <code>null</code> but can be empty)
     */
    public Set<String> getSupportedModelTypes() {
        return getHeader().getSupportedModelTypes();
    }

    /**
     * @return the version (a positive integer)
     */
    public int getVersion() {
        return this.header.getVersion();
    }

    /**
     * @param metaclassName the metaclass name whose extension properties are being requested (cannot be <code>null</code>)
     * @return the extension properties (never <code>null</code>)
     */
    private Collection<ModelExtensionPropertyDefinition> internalGetProperties( String metaclassName ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is null"); //$NON-NLS-1$
        return this.properties.get(metaclassName);
    }

    /**
     * @return <code>true</code> if this MED is a built-in and therefore should not be unregistered
     */
    public boolean isBuiltIn() {
        return this.builtIn;
    }

    /**
     * Marks this MED as being a built-in and therefore cannot be unregistered.
     */
    public void markAsBuiltIn() {
        this.builtIn = true;
    }

    /**
     * Broadcasts the property change to all registered listeners.
     * 
     * @param property the property that has been changed (cannot be <code>null</code>)
     * @param oldValue the old value (can be <code>null</code>)
     * @param newValue (can be <code>null</code>)
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
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange( PropertyChangeEvent e ) {
        if (PropertyName.METAMODEL_URI.toString().equals(e.getPropertyName())) {
            // delete all metaclasses and properties
            String[] metaclasses = getExtendedMetaclasses();

            if (metaclasses.length != 0) {
                this.properties.clear();
                notifyChangeListeners(PropertyName.METACLASS, metaclasses, null);
            }

        }
    }

    /**
     * @param listener the listener being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was successfully removed
     */
    public boolean removeListener( PropertyChangeListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener is null"); //$NON-NLS-1$
        boolean removed = this.listeners.remove(listener);

        if (removed) {
            removed = this.header.removeListener(listener);
        }

        return removed;
    }

    /**
     * @param modelType the model type being removed (cannot be <code>null</code> or empty)
     * @return <code>true</code> if the model type was removed
     */
    public boolean removeModelType( String modelType ) {
        return getHeader().removeModelType(modelType);
    }

    /**
     * @param metaclassName the name of the metaclass whose property definition is being removed (cannot be <code>null</code> or
     *            empty)
     * @param propDefn the property definition being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the property definition has been removed
     */
    public boolean removePropertyDefinition( String metaclassName,
                                             ModelExtensionPropertyDefinition propDefn ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(propDefn, "propDefn is null"); //$NON-NLS-1$

        Collection<ModelExtensionPropertyDefinition> propDefns = getPropertyDefinitions(metaclassName);
        boolean removed = propDefns.remove(propDefn);

        if (removed) {
            notifyChangeListeners(PropertyName.PROPERTY_DEFINITION, propDefn, null);
        }

        return removed;
    }

    /**
     * @param metaclassName the name of the metaclass being removed (cannot be <code>null</code> or empty)
     * @return the property definitions of the metaclass being removed (can be <code>null</code> if metaclass was not found or
     *         didn't have properties)
     */
    public Collection<ModelExtensionPropertyDefinition> removeMetaclass( String metaclassName ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is empty"); //$NON-NLS-1$

        if (this.properties.containsKey(metaclassName)) {
            Collection<ModelExtensionPropertyDefinition> propDefns = this.properties.remove(metaclassName);
            notifyChangeListeners(PropertyName.METACLASS, metaclassName, null);
            return propDefns;
        }

        // metaclass not found
        return null;
    }

    /**
     * @param newDescription the new description (can be <code>null</code> or empty)
     */
    public void setDescription( String newDescription ) {
        this.header.setDescription(newDescription);
    }

    /**
     * @param newMetamodelUri the new metamodel URI (can be <code>null</code> or empty)
     */
    public void setMetamodelUri( String newMetamodelUri ) {
        this.header.setMetamodelUri(newMetamodelUri);
    }

    /**
     * @param newNamespacePrefix the new namespace prefix (can be <code>null</code> or empty)
     */
    public void setNamespacePrefix( String newNamespacePrefix ) {
        this.header.setNamespacePrefix(newNamespacePrefix);
    }

    /**
     * @param newNamespaceUri the new namespace URI (can be <code>null</code> or empty)
     */
    public void setNamespaceUri( String newNamespaceUri ) {
        this.header.setNamespaceUri(newNamespaceUri);
    }

    /**
     * If the new version is an invalid version number the version remains unchanged.
     * 
     * @param newVersion the new version
     */
    public void setVersion( int newVersion ) {
        this.header.setVersion(newVersion);
    }

    /**
     * Changes the original metaclass name to the new name and keeps all the property definitions.
     * 
     * @param originalMetaclass the name of the metaclass that is being changed (cannot be <code>null</code> or empty)
     * @param newMetaclass the new name of the metaclass (cannot be <code>null</code> or empty)
     */
    public void updateMetaclass( String originalMetaclass,
                                 String newMetaclass ) {
        CoreStringUtil.isEmpty(originalMetaclass);
        CoreStringUtil.isEmpty(newMetaclass);
        Collection<ModelExtensionPropertyDefinition> propDefns = removeMetaclass(originalMetaclass);

        if ((propDefns == null) || propDefns.isEmpty()) {
            addMetaclass(newMetaclass);
        } else {
            addPropertyDefinitions(newMetaclass, propDefns);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals( final Object object ) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        final ModelExtensionDefinition other = (ModelExtensionDefinition)object;

        // Check MED headers equal
        if (!getHeader().equals(other.getHeader())) {
            return false;
        }

        // Verify same number of extended metaclasses
        String[] extendedMetaclassNames = this.getExtendedMetaclasses();
        String[] otherMetaclassNames = other.getExtendedMetaclasses();
        if (extendedMetaclassNames.length != otherMetaclassNames.length)
            return false;
        if (extendedMetaclassNames.length == 0 && otherMetaclassNames.length == 0)
            return true;

        // Check that metaClasses extended are same, and Property Definitions are the same for each
        boolean areEqual = true;
        for (int i = 0; i < extendedMetaclassNames.length; i++) {
            Set<ModelExtensionPropertyDefinition> metaClassPropertyDefns = new HashSet<ModelExtensionPropertyDefinition>(this.getPropertyDefinitions(extendedMetaclassNames[i]));
            Set<ModelExtensionPropertyDefinition> otherMetaClassPropertyDefns = new HashSet<ModelExtensionPropertyDefinition>(other.getPropertyDefinitions(extendedMetaclassNames[i]));

            // If sets are different size, or they are equal size (not empty) but dont contain the same elements - not equal
            if ((metaClassPropertyDefns.size() != otherMetaClassPropertyDefns.size())
                    || (!metaClassPropertyDefns.isEmpty() && !metaClassPropertyDefns.containsAll(otherMetaClassPropertyDefns))) {
                areEqual = false;
                break;
            }
        }

        return areEqual;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = HashCodeUtil.hashCode(0, this.builtIn);

        result = HashCodeUtil.hashCode(result, getHeader());

        String[] extendedMetaclassNames = this.getExtendedMetaclasses();
        for (int i = 0; i < extendedMetaclassNames.length; i++) {
            Collection<ModelExtensionPropertyDefinition> metaclassPropertyDefns = this.getPropertyDefinitions(extendedMetaclassNames[i]);
            for (ModelExtensionPropertyDefinition propDefn : metaclassPropertyDefns) {
                result = HashCodeUtil.hashCode(result, propDefn);
            }
        }

        return result;
    }

    /**
     * @param modelType the model type being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model type is supported by the MED
     */
    public boolean supportsModelType( String modelType ) {
        return getHeader().supportsModelType(modelType);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getHeader().toString();
    }

    /**
     * The properties that can be changed.
     */
    public enum PropertyName {

        /**
         * A property definition.
         */
        PROPERTY_DEFINITION,

        /**
         * The description.
         */
        DESCRIPTION,

        /**
         * The metamodel URI.
         */
        METAMODEL_URI,

        /**
         * The metaclass name being extended.
         */
        METACLASS,

        /**
         * The list of applicable model types.
         */
        MODEL_TYPES,

        /**
         * The namespace prefix.
         */
        NAMESPACE_PREFIX,

        /**
         * The namespace URI.
         */
        NAMESPACE_URI,

        /**
         * The version.
         */
        VERSION
    }

}
