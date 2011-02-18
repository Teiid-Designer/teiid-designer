/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import junit.framework.TestCase;
import com.metamatrix.modeler.schema.tools.mocks.MockSchemaObjectKey;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObjectKey;

public class RootElementImplTest extends TestCase {

    private RootElementImpl rootElem;
    private SchemaObjectKey mock;

    private RootElementImpl nullRootElem;
    private static String namespace = "namespace"; //$NON-NLS-1$
    private static String name = "name"; //$NON-NLS-1$
    private static String nullNamespace = null;
    private static String nullName = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mock = new MockSchemaObjectKey();
        rootElem = new RootElementImpl(mock, name, namespace, false);

        new MockSchemaObjectKey();
        nullRootElem = new RootElementImpl(mock, nullName, nullNamespace, true);
    }

    /*
     * Test method for 'com.metamatrix.modeler.schema.tools.model.schema.impl.RootElementImpl.toString()'
     */
    public void testToString() {
        String result = name + " (" + namespace + ')'; //$NON-NLS-1$
        assertTrue("These strings should be the same", result.equals(rootElem.toString())); //$NON-NLS-1$
    }

    /*
     * Test method for 'com.metamatrix.modeler.schema.tools.model.schema.impl.RootElementImpl.isUseAsRoot()'
     */
    public void testIsUseAsRoot() {

        assertFalse("This RootElement should not be a root", rootElem.isUseAsRoot()); //$NON-NLS-1$
        assertTrue("This RootElement should be a root", nullRootElem.isUseAsRoot()); //$NON-NLS-1$

    }

    /*
     * Test method for 'com.metamatrix.modeler.schema.tools.model.schema.impl.RootElementImpl.getKey()'
     */
    public void testGetKey() {
        assertEquals("These objects should be equal", mock, rootElem.getKey()); //$NON-NLS-1$
    }

    /*
     * Test method for 'com.metamatrix.modeler.schema.tools.model.schema.impl.RootElementImpl.getName()'
     */
    public void testGetName() {
        assertEquals("These names should match", name, rootElem.getName()); //$NON-NLS-1$
    }

    public void testGetNullName() {
        assertEquals("These names should match", "", nullRootElem.getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * Test method for 'com.metamatrix.modeler.schema.tools.model.schema.impl.RootElementImpl.getNamespace()'
     */
    public void testGetNamespace() {
        assertEquals("These names should match", namespace, rootElem.getNamespace()); //$NON-NLS-1$
    }

    public void testGetNullNamespace() {
        assertEquals("These names should match", "", nullRootElem.getNamespace()); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
