/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.registry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.Factory;
import org.teiid.designer.extension.MedRegistryListener;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;

/**
 * 
 */
public class ModelExtensionRegistryTest implements Constants {

    private ModelExtensionAssistant assistant;
    private ModelExtensionRegistry registry;

    private void loadBuiltInMeds() throws Exception {
        for (String medFileName : BUILT_IN_MEDS) {
            File defnFile = new File(medFileName);
            this.registry.addDefinition(new FileInputStream(defnFile), this.assistant);
       }
    }

    @Before
    public void beforeEach() throws Exception {
        this.assistant = Factory.createAssistant();
        this.registry = Factory.createRegistry();
    }

    @Test
    public void shouldAddDefinition() throws Exception {
        File defnFile = new File(SALESFORCE_MED_FILE_NAME);
        ModelExtensionDefinition med = this.registry.addDefinition(new FileInputStream(defnFile), this.assistant);
        assertNotNull("MED is null", med); //$NON-NLS-1$
        assertEquals(1, this.registry.getAllDefinitions().size());
    }

    @Test(expected = Exception.class)
    public void shouldNotAddDefinitionIfThereAreParserErrors() throws Exception {
        File defnFile = new File(EMPTY_MED_FILE_NAME);
        this.registry.addDefinition(new FileInputStream(defnFile), this.assistant);
    }

    @Test
    public void shouldReceiveEventWhenAddingMed() throws Exception {
        MedRegistryListener l = Factory.createRegistryListener();
        this.registry.addListener(l);
        File defnFile = new File(SALESFORCE_MED_FILE_NAME);
        ModelExtensionDefinition med = this.registry.addDefinition(new FileInputStream(defnFile), this.assistant);
        assertEquals(1, l.getCount());
        assertEquals(med, l.getEvent().getDefinition());
        assertTrue(l.getEvent().isAdd());
    }

    @Test
    public void shouldReceiveEventWhenRemovingMed() throws Exception {
        MedRegistryListener l = Factory.createRegistryListener();
        File defnFile = new File(SALESFORCE_MED_FILE_NAME);
        this.registry.addDefinition(new FileInputStream(defnFile), this.assistant);
        this.registry.addListener(l);
        this.registry.removeDefinition(SALESFORCE_MED_PREFIX);
        assertEquals(1, l.getCount());
        assertTrue(l.getEvent().isRemove());
    }

    @Test
    public void shouldNotReceiveEventWhenListenerIsUnregistered() throws Exception {
        MedRegistryListener l = Factory.createRegistryListener();
        this.registry.addListener(l);
        this.registry.removeListener(l);
        File defnFile = new File(SALESFORCE_MED_FILE_NAME);
        this.registry.addDefinition(new FileInputStream(defnFile), this.assistant);
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldProvideRegisteredNamespacePrefixes() throws Exception {
        loadBuiltInMeds();
        Set<String> namespacePrefixes = this.registry.getAllNamespacePrefixes();
        assertEquals(BUILT_IN_MEDS_NAMESPACE_PREFIXES.length, namespacePrefixes.size());

        for (String prefix : BUILT_IN_MEDS_NAMESPACE_PREFIXES) {
            assertTrue(namespacePrefixes.contains(prefix));
        }
    }

    @Test
    public void shouldProvideRegisteredNamespaceUris() throws Exception {
        loadBuiltInMeds();
        Set<String> namespaceUris = this.registry.getAllNamespaceUris();
        assertEquals(BUILT_IN_MEDS_NAMESPACE_URIS.length, namespaceUris.size());

        for (String uri : BUILT_IN_MEDS_NAMESPACE_URIS) {
            assertTrue(namespaceUris.contains(uri));
        }
    }

}
