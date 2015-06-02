/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import static org.teiid.designer.vdb.VdbPlugin.UTIL;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb.Event;


/**
 * Represents a VDB translator.
 */
public class Translator extends VdbObject implements Comparable<TranslatorOverride> {
	private static final String PREFIX = I18nUtil.getPropertyPrefix(Translator.class);
	
	String type;
    /**
     * The type identifier.
     */
    int TYPE_ID = Translator.class.hashCode();

    /**
     * Identifier of this object
     */
    TeiidType IDENTIFIER = TeiidType.VDB_TRANSLATOR;

    /**
     * The default value for a translator type. Value is {@value} .
     */
    String DEFAULT_TYPE = "oracle"; //$NON-NLS-1$

    /**
     * An empty array of translators.
     */
    Translator[] NO_TRANSLATORS = new Translator[0];
    

    private final Map<String, TranslatorOverrideProperty> properties;
    
    /**
     * 
     */
    public Translator() {
		super();
		this.type = DEFAULT_TYPE;
        this.properties = new HashMap<String, TranslatorOverrideProperty>();
	}

    /**
     * @return the value of the <code>type</code> property (never empty)
     */
    public String getType() {
    	return this.type;
    }

    /**
     * @param newType
     *        the new value of the <code>type</code> property (cannot be empty)
     */
    public void setType( final String newType ) {
    	setChanged(this.type, newType);
		this.type = newType;
    }
    
    /**
     * @param proposedName the proposed translator name
     * @return an error message or <code>null</code> if name is valid
     */
    public static String validateName( String proposedName ) {
        // must have a name
        if (StringUtilities.isEmpty(proposedName)) {
            return UTIL.getString(PREFIX + "emptyTranslatorName"); //$NON-NLS-1$
        }

        // make sure only letters, digits, or dash
        for (char c : proposedName.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c)
                    && (c != '-')) {
                return UTIL.getString(PREFIX + "invalidTranslatorName", proposedName); //$NON-NLS-1$
            }
        }

        // first char must be letter
        if (!Character.isLetter(proposedName.charAt(0))) {
            return UTIL.getString(PREFIX + "translatorNameMustStartWithLetter"); //$NON-NLS-1$
        }

        // valid name
        return null;
    }

    /**
     * @param proposedType the proposed translator type
     * @return an error message or <code>null</code> if type is valid
     */
    public static String validateType( String proposedType ) {
        // must have a type
        if (StringUtilities.isEmpty(proposedType)) {
            return UTIL.getString(PREFIX + "emptyTranslatorType"); //$NON-NLS-1$
        }

        // make sure only letters, digits, or dash
        for (char c : proposedType.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '-') {
                return UTIL.getString(PREFIX + "invalidTranslatorType", proposedType); //$NON-NLS-1$
            }
        }

        // first char must be letter
        if (!Character.isLetter(proposedType.charAt(0))) {
            return UTIL.getString(PREFIX + "translatorTypeMustStartWithLetter"); //$NON-NLS-1$
        }

        // valid type
        return null;
    }
    
    /**
     * Adds the property and fires a VDB property changed event.
     * 
     * @param newProperty the property being added (may not be <code>null</code>)
     */
    public void addOverrideProperty( TranslatorOverrideProperty newProperty ) {
    	addOverrideProperty(newProperty, true);
    }

    /**
     * @param newProperty the property being added (may not be <code>null</code>)
     * @param firePropertyEvent <code>true</code> if a VDB property changed event should be fired
     */
    public void addOverrideProperty( TranslatorOverrideProperty newProperty,
                             boolean firePropertyEvent ) {
        assert (newProperty != null);

        Object obj = this.properties.put(newProperty.getDefinition().getId(), newProperty);
//        newProperty.addListener(this);

        if (firePropertyEvent) {
//         TODO:  this.vdb.setModified(this, Event.TRANSLATOR_PROPERTY, obj, newProperty);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( TranslatorOverride vdbTranslator ) {
        return getName().compareTo(vdbTranslator.getName());
    }
    
    /**
     * Removes the property and fires a VDB property changed event
     * 
     * @param propName the name of the property being removed (may not be <code>null</code> or empty)
     * @return <code>true</code> if the property was removed
     */
    public boolean removeOverrideProperty( String propName ) {
        return removeOverrideProperty(propName, true);
    }

    /**
     * @param propName the name of the property being removed (may not be <code>null</code> or empty)
     * @param firePropertyEvent <code>true</code> if a VDB property changed event should be fired
     * @return <code>true</code> if the property was removed
     */
    public boolean removeOverrideProperty( String propName,
                                   boolean firePropertyEvent ) {
        assert (propName != null);
        assert (this.properties.containsKey(propName));

        TranslatorOverrideProperty prop = this.properties.remove(propName);
//        prop.removeListener(this);

        if (prop.getDefinition().isUserDefined()) {
//            this.vdb.setModified(this, Event.TRANSLATOR_PROPERTY, prop, null);
        }

        return false;
    }
    
    /**
     * Obtains all the properties of the translator even those properties whose values have not been overridden.
     * 
     * @return all translator properties (never <code>null</code>)
     */
    public TranslatorOverrideProperty[] getOverrideProperties() {
        TranslatorOverrideProperty[] props = new TranslatorOverrideProperty[this.properties.size()];
        int i = 0;

        for (TranslatorOverrideProperty property : this.properties.values()) {
            props[i++] = property;
        }

        return props;
    }
}
