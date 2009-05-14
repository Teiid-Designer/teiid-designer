/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import junit.framework.TestCase;

public class WSDLElementImplTest extends TestCase {

    public WSDLElementImplTest( String name ) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.WSDLElementImpl.hashCode()'
     */
    public void testHashCode() {
        WSDLElementImpl impl = (WSDLElementImpl)ModelElementFactory.getTestService("service", "serv"); //$NON-NLS-1$ //$NON-NLS-2$
        WSDLElementImpl impl2 = (WSDLElementImpl)ModelElementFactory.getTestService("service", "serv"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(impl.hashCode(), impl2.hashCode());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.WSDLElementImpl.getName()'
     */
    public void testGetSetName() {
        WSDLElementImpl impl = new ServiceImpl();
        String name = "service"; //$NON-NLS-1$
        impl.setName(name);
        String out = impl.getName();
        assertEquals(name, out);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.WSDLElementImpl.getId()'
     */
    public void testGetSetId() {
        WSDLElementImpl impl = new ServiceImpl();
        String id = "service"; //$NON-NLS-1$
        impl.setId(id);
        String out = impl.getId();
        assertEquals(id, out);
    }
}
