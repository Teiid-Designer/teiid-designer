/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.Util;
import java.util.Collection;
import java.util.Properties;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;

/**
 * The <code>TranslatorTemplate</code> class is an implementation of {@link ITeiidTranslator} that does not communicate with a Teiid
 *
 * @since 8.0
 */
public class TranslatorTemplate implements ITeiidTranslator {

    private final Properties changedProperties;
    
    private ITeiidTranslator translator;

    /**
     * @param translator the translator whose properties are used to create this template (never <code>null</code>)
     */
    public TranslatorTemplate( ITeiidTranslator translator ) {
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$

        this.translator = translator;
        this.changedProperties = new Properties();
    }

    /**
     * @return the properties that have changed values since being constructed
     */
    public Properties getChangedProperties() {
        return this.changedProperties;
    }

    @Override
    public String getPropertyValue( String name ) {
        if (this.changedProperties.containsKey(name)) {
            return this.changedProperties.getProperty(name);
        }

        return translator.getPropertyValue(name);
    }
    
    @Override
    public Properties getDefaultPropertyValues() {
        return translator.getDefaultPropertyValues();
    }

    @Override
    public String isValidPropertyValue(String name, String value) {
        return translator.isValidPropertyValue(name, value);
    }

    @Override
    public Properties getProperties() {
        Properties props = new Properties(translator.getProperties());
        props.putAll(this.changedProperties);
        return props;
    }

    @Override
    public void setProperties( Properties changedProperties ) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPropertyValue( String name,
                                  String value ) throws Exception {
        if (isValidPropertyValue(name, value) == null) {
            this.changedProperties.setProperty(name, value);
        } else {
            throw new Exception(Util.getString("invalidPropertyValue", value, name)); //$NON-NLS-1$
        }
    }

    @Override
    public Collection<String> findInvalidProperties() {
        return translator.findInvalidProperties();
    }

    @Override
    public String getName() {
        return translator.getName();
    }

    @Override
    public String getType() {
        return translator.getType();
    }

    @Override
    public ITeiidServer getTeiidServer() {
        return translator.getTeiidServer();
    }

    @Override
    public TeiidPropertyDefinition getPropertyDefinition(String name) {
        return translator.getPropertyDefinition(name);
    }

    @Override
    public Collection<TeiidPropertyDefinition> getPropertyDefinitions() {
       return translator.getPropertyDefinitions();
    }
}
