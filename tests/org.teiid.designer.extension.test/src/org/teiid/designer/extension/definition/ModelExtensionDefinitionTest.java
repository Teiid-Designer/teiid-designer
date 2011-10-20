/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.Factory;

/**
 * 
 */
public class ModelExtensionDefinitionTest implements Constants {

    private ModelExtensionDefinition definition;

    @Before
    public void beforeEach() {
        this.definition = Factory.createDefinitionWithNoPropertyDefinitions();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullAssistantOnConstruction() {
        new ModelExtensionDefinition(null);
    }

    @Test
    public void shouldSetNamespacePrefixOnConstruction() {
        Assert.assertEquals(DEFAULT_NAMESPACE_PREFIX, this.definition.getNamespacePrefix());
    }

    @Test
    public void shouldSetNamespaceUriOnConstruction() {
        Assert.assertEquals(DEFAULT_NAMESPACE_URI, this.definition.getNamespaceUri());
    }

    @Test
    public void shouldSetMetamodelUriOnConstruction() {
        Assert.assertEquals(DEFAULT_METAMODEL_URI, this.definition.getMetamodelUri());
    }

    @Test
    public void shouldSetVersionOnConstruction() {
        Assert.assertEquals(ModelExtensionDefinitionHeader.DEFAULT_VERSION, this.definition.getVersion());
    }

}
