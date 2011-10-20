/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.registry;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.Factory;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;

/**
 * 
 */
public class ModelExtensionRegistryTest implements Constants {

    private ModelExtensionAssistant assistant;
    private ModelExtensionRegistry registry;

    @Before
    public void beforeEach() throws Exception {
        this.assistant = Factory.createAssistant();
        this.registry = Factory.createRegistry();
    }

    @Test
    public void shouldAddDefinition() throws Exception {
        File defnFile = new File(SALESFORCE_MED);
        ModelExtensionDefinition med = this.registry.addDefinition(new FileInputStream(defnFile), this.assistant);
        Assert.assertNotNull("MED is null", med); //$NON-NLS-1$
        Assert.assertEquals(1, this.registry.getAllDefinitions().size());
    }

}
