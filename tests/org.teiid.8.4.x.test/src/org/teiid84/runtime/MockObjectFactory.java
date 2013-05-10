/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid84.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid84.runtime.TeiidTranslator;

/**
 *
 */
public class MockObjectFactory {

    public static final String CONNECTOR_TYPE = "rar-name"; //$NON-NLS-1$
    
    /**
     * Creates a mock <code>ConnectionFactory</code>. The name and the properties can be obtained. The connector type property
     * value can be obtained.
     * 
     * @param name the name of the translator
     * @param translatorTypeName the name of the translator type of this translator
     * @return the translator
     */
    public static Translator createTranslator( final String name,
                                               final String translatorTypeName ) {
        final Translator connectionFactory = mock(Translator.class);
        final Properties props = new Properties();
        props.setProperty(CONNECTOR_TYPE, translatorTypeName);

        when(connectionFactory.getName()).thenReturn(name);
        when(connectionFactory.getProperties()).thenReturn(props);
        when(connectionFactory.getPropertyValue(CONNECTOR_TYPE)).thenReturn(translatorTypeName);

        return connectionFactory;
    }

    /**
     * Creates a <code>TeiidTranslator</code> using a mock <code>ConnectionFactory</code> and mock <code>TranslatorType</code>.
     * The names can be obtained from the translator and translator type. The translator type name can be obtained from the
     * translator properties.
     * 
     * @param name the name of the translator
     * @param translatorTypeName the name of the translator type
     * @return the translator
     * @since 7.0
     */
    public static ITeiidTranslator createTeiidTranslator( final String name,
                                                         final String translatorTypeName ) {
        final Translator translator = createTranslator(name, translatorTypeName);

        return new TeiidTranslator(translator, new ArrayList<PropertyDefinition>(), createTeiidServer());
    }

    /**
     * Creates a <code>TeiidTranslator</code> using a mock <code>ConnectionFactory</code> and mock <code>TranslatorType</code>.
     * The names can be obtained from the translator and translator type. The translator type name can be obtained from the
     * translator properties.
     * 
     * @param name the name of the translator
     * @param translatorTypeName the name of the translator type
     * @return the translator
     * @since 7.0
     */
    public static ITeiidTranslator createTeiidTranslator( final String name,
                                                         final String translatorTypeName,
                                                         final Collection<PropertyDefinition> propertyDefs ) {
        final Translator translator = createTranslator(name, translatorTypeName);

        return new TeiidTranslator(translator, propertyDefs, createTeiidServer());
    }

    private static ITeiidServer createTeiidServer() {
        final EventManager eventManager = mock(EventManager.class);
        final ITeiidServer teiidServer = mock(ITeiidServer.class);
        
        return teiidServer;
    }

    /**
     * Prevent construction.
     */
    private MockObjectFactory() {
        // nothing to do
    }
}
