/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Factory;

/**
 * 
 */
public class ModelExtensionPropertyDefinitionImplTest {

    private ModelExtensionPropertyDefinitionImpl propDefn;

    @Before
    public void beforeEach() {
        this.propDefn = new ModelExtensionPropertyDefinitionImpl(Factory.createDefaultNamespacePrefixProvider());
    }

    @Test
    public void cloneShouldBeEquals() {
        assertEquals(this.propDefn, this.propDefn.clone());
    }

    @Test
    public void cloneShouldHaveSameHashCode() {
        assertEquals(this.propDefn.hashCode(), this.propDefn.clone().hashCode());
    }

    @Test
    public void cloneShouldNotBeExactlyEquals() {
        assertTrue(this.propDefn != this.propDefn.clone());
    }
}
