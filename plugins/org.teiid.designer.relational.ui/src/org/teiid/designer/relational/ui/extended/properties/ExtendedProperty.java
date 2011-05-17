/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.extended.properties;

import static com.metamatrix.modeler.relational.ui.UiConstants.Util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.ListenerList;
import org.teiid.core.properties.PropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;



/**
 * An extended property for a model object.
 */
public class ExtendedProperty {

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExtendedProperty.class);
    private final ListenerList listeners;
    private String value;
    private ExtendedPropertyDefinition propDefn;

    /**
     * @param propDefn the property definition (may not be <code>null</code>)
     * @param overriddenValue the property value override (can be <code>null</code> or empty)
     * @throws IllegalArgumentException if <code>propDefn</code> is <code>null</code>
     */
    public ExtendedProperty( ExtendedPropertyDefinition propDefn,
                                       String value ) {
        CoreArgCheck.isNotNull(propDefn);
        this.propDefn = propDefn;
        this.value = value;
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

        return this.propDefn.getId().equals(((ExtendedProperty)obj).getDefinition().getId());
    }

    /**
     * @return the property definition (never <code>null</code>)
     */
    public ExtendedPropertyDefinition getDefinition() {
        return this.propDefn;
    }

    /**
     * @return the property value override (may be <code>null</code> or empty)
     */
    public String getValue() {
        // don't allow a custom property to have an empty value
        return ((isCustom() && StringUtilities.isEmpty(this.value)) ? getDefinition().getDefaultValue()
                                                                             : this.value);
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
     * @return <true> if not a known server property
     * @see ExtendedPropertyDefinition#isUserDefined()
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
        this.propDefn = new ExtendedPropertyDefinition(propDefn);
    }

    /**
     * @param newValue the new property value (cannot be <code>null</code> or empty)
     */
    public void setValue( String newValue ) {
        if (!StringUtilities.equals(this.value, newValue)) {
            String oldValue = this.value;
            this.value = newValue;

            // notify listeners of change
            PropertyChangeEvent event = new PropertyChangeEvent(this, this.propDefn.getId(), oldValue, newValue);

            for (Object listener : this.listeners.getListeners()) {
                ((PropertyChangeListener)listener).propertyChange(event);
            }
        }
    }
    
    /**
     * @param proposedName the proposed property
     * @return an error message or <code>null</code> if name is valid
     */
    public static String validateName( String proposedName ) {
       
    	// must have a name
        if (StringUtilities.isEmpty(proposedName)) {
            return Util.getString(I18N_PREFIX + "emptyPropertyName"); //$NON-NLS-1$
        }
        
        // valid name
        return null;
    }

    /**
     * @param proposedValue the proposed property
     * @return an error message or <code>null</code> if value is valid
     */
    public static String validateValue( String proposedValue ) {
       
    	// must have a value
        if (StringUtilities.isEmpty(proposedValue)) {
            return Util.getString(I18N_PREFIX + "emptyPropertyValue"); //$NON-NLS-1$
        }
        
        // valid value
        return null;
    }

}