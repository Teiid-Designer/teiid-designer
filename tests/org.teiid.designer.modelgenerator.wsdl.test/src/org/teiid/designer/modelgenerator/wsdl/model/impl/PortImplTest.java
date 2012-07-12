/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model.impl;

import org.teiid.designer.modelgenerator.wsdl.model.Binding;
import org.teiid.designer.modelgenerator.wsdl.model.Port;
import org.teiid.designer.modelgenerator.wsdl.model.Service;
import org.teiid.designer.modelgenerator.wsdl.model.impl.PortImpl;

import junit.framework.TestCase;

public class PortImplTest extends TestCase {

    public PortImplTest( String name ) {
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
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.PortImpl.PortImpl(Service)'
     */
    public void testPortImpl() {
        Service svc = ModelElementFactory.getTestService("service", "serviceId"); //$NON-NLS-1$ //$NON-NLS-2$
        Port port = new PortImpl(svc);
        assertNotNull(port);
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.PortImpl.getBinding()'
     */
    public void testGetSetBinding() {
        Port thePort = ModelElementFactory.getTestPort("testPort", "testPort"); //$NON-NLS-1$ //$NON-NLS-2$
        Binding bind = ModelElementFactory.getTestBinding("binding", "binding", "http://test/test.wsdl", "Request_Response", thePort); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        thePort.setBinding(bind);
        assertEquals(thePort.getBinding(), bind);
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.PortImpl.getService()'
     */
    public void testGetService() {
        Service svc = ModelElementFactory.getTestService("service", "serviceId"); //$NON-NLS-1$ //$NON-NLS-2$
        Port port = new PortImpl(svc);
        assertEquals(svc, port.getService());
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.PortImpl.copy()'
     */
    public void testCopy() {
        Port port = ModelElementFactory.getTestPort("port", "portId"); //$NON-NLS-1$ //$NON-NLS-2$
        Port pt = (Port)port.copy();
        assertEquals(port, pt);
        assertEquals(port.getId(), pt.getId());
        assertEquals(port.getName(), pt.getName());
        assertEquals(port.getBinding(), pt.getBinding());
        assertEquals(port.getService(), pt.getService());
    }
}
