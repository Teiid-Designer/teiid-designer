/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static org.teiid.designer.extension.ExtensionPlugin.Util;
import static org.teiid.designer.extension.Messages.invalidDefinitionFileNewVersion;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * A <code>ModelExtensionDefinition</code> defines extension properties for metaclasses within a metamodel.
 */
public class ModelExtensionDefinition {

    /**
     * The default version number. Value is {@value} .
     */
    public static final int DEFAULT_VERSION = 1;

    /**
     * The model extension assistant (never <code>null</code>).
     */
    private final ModelExtensionAssistant assistant;

    /**
     * The definition description (can be <code>null</code> or empty).
     */
    private String description;

    /**
     * The registered property change listeners (never <code>null</code>).
     */
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners;

    /**
     * The metamodel URI that this definition is extended.
     */
    private final String metamodelUri;

    /**
     * The unique namespace prefix of this definition (never <code>null</code> or empty).
     */
    private final String namespacePrefix;

    /**
     * The unique namespace URI of this definition (never <code>null</code> or empty).
     */
    private final String namespaceUri;

    /**
     * Key is metaclass name, value is a collection of property definitions key by property name.
     */
    private final Map<String, Map<String, ModelExtensionPropertyDefinition>> properties;

    /**
     * The version number. Defaults to {@value} .
     */
    private int version = DEFAULT_VERSION;

    /**
     * @param assistant the model extension assist (cannot be <code>null</code>)
     * @param namespacePrefix the unique namespace prefix (cannot be <code>null</code> or empty)
     * @param namespaceUri the unique namespace URI (cannot be <code>null</code> or empty)
     * @param metamodelUri the metamodel URI that is being extended (cannot be <code>null</code> or empty)
     */
    public ModelExtensionDefinition( ModelExtensionAssistant assistant,
                                     String namespacePrefix,
                                     String namespaceUri,
                                     String metamodelUri ) {
        CoreArgCheck.isNotNull(assistant, "assistant is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(namespaceUri, "namespaceUri is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(metamodelUri, "metamodelUri is null"); //$NON-NLS-1$

        this.assistant = assistant;
        this.namespacePrefix = namespacePrefix;
        this.namespaceUri = namespaceUri;
        this.metamodelUri = metamodelUri;
        this.properties = new HashMap<String, Map<String, ModelExtensionPropertyDefinition>>();
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
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
     * @param metaclassName the metaclass name to which the extension property definition pertains to (cannot be <code>null</code>
     *            or empty)
     * @param propDefn the property definition being added (cannot be <code>null</code>)
     * @return <code>true</code> if the property definition was successfully added to the metaclass name
     */
    public boolean addPropertyDefinition( String metaclassName,
                                          ModelExtensionPropertyDefinition propDefn ) {
        CoreArgCheck.isNotNull(propDefn, "propDefn is null"); //$NON-NLS-1$

        Map<String, ModelExtensionPropertyDefinition> props = internalGetProperties(metaclassName);

        if (props == null) {
            props = new HashMap<String, ModelExtensionPropertyDefinition>();
            this.properties.put(metaclassName, props);
        }

        String key = propDefn.getId();

        // don't add if already exists
        if (props.containsKey(key)) {
            return false;
        }

        // add new property and alert listeners
        props.put(key, propDefn);
        notifyChangeListeners(PropertyName.ADD_PROPERTY_DEFINITION, null, propDefn);

        return true;
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
        return this.description;
    }

    /**
     * @return the metaclass names that have extended properties defined (never <code>null</code>)
     */
    public String[] getExtendedMetaclasses() {
        return this.properties.keySet().toArray(new String[this.properties.size()]);
    }

    /**
     * @return the metamodel URI (never <code>null</code> or empty)
     */
    public String getMetamodelUri() {
        return this.metamodelUri;
    }

    /**
     * @return the model extension assistant (never <code>null</code>)
     */
    public ModelExtensionAssistant getModelExtensionAssistant() {
        return this.assistant;
    }

    /**
     * @return the namespace prefix (never <code>null</code> or empty)
     */
    public String getNamespacePrefix() {
        return this.namespacePrefix;
    }

    /**
     * @return the namespace URI (never <code>null</code> or empty)
     */
    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    /**
     * @param metaclassName the metaclass name whose extended property is being requested (cannot be <code>null</code> or empty)
     * @param propId the identifier of the property definition being requested (cannot be <code>null</code> or empty)
     * @return the property definition or <code>null</code> if not found
     */
    public ModelExtensionPropertyDefinition getPropertyDefinition( String metaclassName,
                                                                   String propId ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(propId, "propId is null"); //$NON-NLS-1$

        Map<String, ModelExtensionPropertyDefinition> props = internalGetProperties(metaclassName);

        if (props == null) {
            return null;
        }

        return props.get(propId);
    }

    /**
     * @param metaclassName the metaclass name whose extended properties are being requested (cannot be <code>null</code> or empty)
     * @return the extension properties (never <code>null</code> but can be empty)
     */
    public Collection<ModelExtensionPropertyDefinition> getPropertyDefinitions( String metaclassName ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is null"); //$NON-NLS-1$
        Map<String, ModelExtensionPropertyDefinition> props = internalGetProperties(metaclassName);

        if (props == null) {
            return Collections.emptyList();
        }

        return new ArrayList<ModelExtensionPropertyDefinition>(props.values());
    }

    /**
     * @return the version (a positive integer)
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * @param metaclassName the metaclass name whose extension properties are being requested (cannot be <code>null</code>)
     * @return the extension properties (never <code>null</code>)
     */
    private Map<String, ModelExtensionPropertyDefinition> internalGetProperties( String metaclassName ) {
        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is null"); //$NON-NLS-1$
        return this.properties.get(metaclassName);
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
     * @param listener the listener being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was successfully removed
     */
    public boolean removeListener( PropertyChangeListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener is null"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * @param newDescription the new description (can be <code>null</code> or empty)
     */
    public void setDescription( String newDescription ) {
        if (!CoreStringUtil.equals(this.description, newDescription)) {
            Object oldValue = this.description;
            this.description = newDescription;

            // alert listeners
            notifyChangeListeners(PropertyName.DESCRIPTION, oldValue, this.description);
        }
    }

    /**
     * If the new version is an invalid version number the version remains unchanged.
     * 
     * @param newVersion the new version
     */
    public void setVersion( int newVersion ) {
        if (this.version != newVersion) {
            if (newVersion < DEFAULT_VERSION) {
                Util.log(IStatus.ERROR,
                         NLS.bind(invalidDefinitionFileNewVersion, new Object[] { getNamespacePrefix(), newVersion, this.version }));
                return;
            }

            Object oldValue = this.version;
            this.version = newVersion;

            // alert listeners
            notifyChangeListeners(PropertyName.VERSION, oldValue, this.description);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("Model Extension Definition: "); //$NON-NLS-1$
        text.append("namespacePrefix=").append(getNamespacePrefix()); //$NON-NLS-1$
        text.append(", namespaceUri=").append(getNamespaceUri()); //$NON-NLS-1$
        text.append(", metamodelUri=").append(getMetamodelUri()); //$NON-NLS-1$
        text.append(", version=").append(getVersion()); //$NON-NLS-1$

        return text.toString();
    }

    /**
     * The properties that can be changed.
     */
    public enum PropertyName {

        /**
         * An added property definition.
         */
        ADD_PROPERTY_DEFINITION,

        /**
         * The description.
         */
        DESCRIPTION,

        /**
         * The version.
         */
        VERSION
    }

}
