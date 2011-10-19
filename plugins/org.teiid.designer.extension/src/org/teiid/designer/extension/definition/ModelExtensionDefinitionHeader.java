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
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.HashCodeUtil;
import org.teiid.designer.extension.definition.ModelExtensionDefinition.PropertyName;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public class ModelExtensionDefinitionHeader {

    /**
     * The default version number. Value is {@value} .
     */
    public static final int DEFAULT_VERSION = 1;

    /**
     * The definition description (can be <code>null</code> or empty).
     */
    private String description;

    /**
     * The registered property change listeners (never <code>null</code>).
     */
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners;

    /**
     * The metamodel URI that this definition is extended (can be <code>null</code> or empty).
     */
    private String metamodelUri;

    /**
     * The unique namespace prefix of this definition (can be <code>null</code> or empty).
     */
    private String namespacePrefix;

    /**
     * The unique namespace URI of this definition (can be <code>null</code> or empty).
     */
    private String namespaceUri;

    /**
     * The version number. Defaults to {@value} .
     */
    private int version = DEFAULT_VERSION;

    public ModelExtensionDefinitionHeader() {
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    }

    public ModelExtensionDefinitionHeader( String namespacePrefix,
                                           String namespaceUri,
                                           String metamodelUri,
                                           String description,
                                           int version ) {
        this();
        this.namespacePrefix = namespacePrefix;
        this.namespaceUri = namespaceUri;
        this.metamodelUri = metamodelUri;
        this.description = description;
        this.version = version;
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
    public final boolean equals( final Object object ) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final ModelExtensionDefinitionHeader other = (ModelExtensionDefinitionHeader)object;

        return CoreStringUtil.equals(this.namespacePrefix, other.namespacePrefix)
               && CoreStringUtil.equals(this.namespaceUri, other.namespaceUri)
               && CoreStringUtil.equals(this.metamodelUri, other.metamodelUri)
               && CoreStringUtil.equals(this.description, other.description) && this.version == other.version;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return metamodelUri
     */
    public String getMetamodelUri() {
        return metamodelUri;
    }

    /**
     * @return namespacePrefix
     */
    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    /**
     * @return namespaceUri
     */
    public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * @return version
     */
    public int getVersion() {
        return version;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = HashCodeUtil.hashCode(0, getVersion());

        // string properties
        if (getNamespacePrefix() != null) {
            result = HashCodeUtil.hashCode(result, getNamespacePrefix());
        }

        if (getMetamodelUri() != null) {
            result = HashCodeUtil.hashCode(result, getMetamodelUri());
        }

        if (getNamespaceUri() != null) {
            result = HashCodeUtil.hashCode(result, getNamespaceUri());
        }

        if (getDescription() != null) {
            result = HashCodeUtil.hashCode(result, getDescription());
        }
        return result;
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
        String currentValue = getDescription();

        if (!CoreStringUtil.equals(currentValue, newDescription)) {
            Object oldValue = newDescription;
            this.description = newDescription;

            // alert listeners
            notifyChangeListeners(PropertyName.DESCRIPTION, oldValue, newDescription);
        }
    }

    /**
     * @param newMetamodelUri the new metamodel URI (can be <code>null</code> or empty)
     */
    public void setMetamodelUri( String newMetamodelUri ) {
        String currentValue = getMetamodelUri();

        if (!CoreStringUtil.equals(currentValue, newMetamodelUri)) {
            Object oldValue = currentValue;
            this.metamodelUri = newMetamodelUri;

            // alert listeners
            notifyChangeListeners(PropertyName.METAMODEL_URI, oldValue, newMetamodelUri);
        }
    }

    /**
     * @param newNamespacePrefix the new namespace prefix (can be <code>null</code> or empty)
     */
    public void setNamespacePrefix( String newNamespacePrefix ) {
        String currentValue = getNamespacePrefix();

        if (!CoreStringUtil.equals(currentValue, newNamespacePrefix)) {
            Object oldValue = currentValue;
            this.namespacePrefix = newNamespacePrefix;

            // alert listeners
            notifyChangeListeners(PropertyName.NAMESPACE_PREFIX, oldValue, newNamespacePrefix);
        }
    }

    /**
     * @param newNamespaceUri the new namespace URI (can be <code>null</code> or empty)
     */
    public void setNamespaceUri( String newNamespaceUri ) {
        String currentValue = getNamespaceUri();

        if (!CoreStringUtil.equals(currentValue, newNamespaceUri)) {
            Object oldValue = currentValue;
            this.namespaceUri = newNamespaceUri;

            // alert listeners
            notifyChangeListeners(PropertyName.NAMESPACE_URI, oldValue, newNamespaceUri);
        }
    }

    /**
     * If the new version is an invalid version number the version remains unchanged.
     * 
     * @param newVersion the new version
     */
    public void setVersion( int newVersion ) {
        int currentValue = getVersion();

        if (currentValue != newVersion) {
            if (newVersion < ModelExtensionDefinitionHeader.DEFAULT_VERSION) {
                Util.log(IStatus.ERROR, NLS.bind(invalidDefinitionFileNewVersion, new Object[] { getNamespacePrefix(), newVersion,
                        currentValue }));
                return;
            }

            Object oldValue = currentValue;
            this.version = newVersion;

            // alert listeners
            notifyChangeListeners(PropertyName.VERSION, oldValue, newVersion);
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
        text.append("Model Extension Definition Header: "); //$NON-NLS-1$
        text.append("namespacePrefix=").append(getNamespacePrefix()); //$NON-NLS-1$
        text.append(", namespaceUri=").append(getNamespaceUri()); //$NON-NLS-1$
        text.append(", metamodelUri=").append(getMetamodelUri()); //$NON-NLS-1$
        text.append(", version=").append(getVersion()); //$NON-NLS-1$

        return text.toString();
    }

}
