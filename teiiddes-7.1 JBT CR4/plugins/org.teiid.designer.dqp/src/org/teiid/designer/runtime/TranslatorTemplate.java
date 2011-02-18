/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;

import java.util.ArrayList;
import java.util.Properties;

import org.teiid.adminapi.PropertyDefinition;

/**
 * The <code>TranslatorTemplate</code> class is an implementation of {@link TeiidTranslator} that does not communicate with a Teiid
 */
public class TranslatorTemplate extends TeiidTranslator {

    private final Properties changedProperties;

    /**
     * @param name the name of the new translator (never <code>null</code>)
     * @param type the type of the new translator (never <code>null</code>)
     */
    public TranslatorTemplate( String name,
                              String type ) {
        super(new PseudoTranslator(name, type, null), new ArrayList<PropertyDefinition>(), null);
        this.changedProperties = new Properties();
    }

    /**
     * @param translator the translator whose properties are used to create this template (never <code>null</code>)
     */
    public TranslatorTemplate( TeiidTranslator translator ) {
        super(new PseudoTranslator(translator), translator.getPropertyDefinitions(), translator.getAdmin());
        this.changedProperties = new Properties();
    }

    /**
     * @return the properties that have changed values since being constructed
     */
    public Properties getChangedProperties() {
        return this.changedProperties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidTranslator#getPropertyValue(java.lang.String)
     */
    @Override
    public String getPropertyValue( String name ) {
        if (this.changedProperties.containsKey(name)) {
            return this.changedProperties.getProperty(name);
        }

        return super.getPropertyValue(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidTranslator#getProperties()
     */
    @Override
    public Properties getProperties() {
        Properties props = new Properties(super.getProperties());
        props.putAll(this.changedProperties);
        return props;
    }

    /**
     * Modifies the name of the translator.
     * 
     * @param name the new name (may be <code>null</code>)
     */
    public void setName( String name ) {
        ((PseudoTranslator)getTranslator()).setName(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidTranslator#setProperties(java.util.Properties)
     * @throws UnsupportedOperationException if called
     */
    @Override
    public void setProperties( Properties changedProperties ) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidTranslator#setPropertyValue(java.lang.String, java.lang.String)
     */
    @Override
    public void setPropertyValue( String name,
                                  String value ) throws Exception {
        if (isValidPropertyValue(name, value) == null) {
            this.changedProperties.setProperty(name, value);
        } else {
            throw new Exception(Util.getString("invalidPropertyValue", value, name)); //$NON-NLS-1$
        }
    }

}
