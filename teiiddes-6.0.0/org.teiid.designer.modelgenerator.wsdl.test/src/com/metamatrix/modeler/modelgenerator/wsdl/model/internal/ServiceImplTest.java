/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import junit.framework.TestCase;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;

public class ServiceImplTest extends TestCase {

    public ServiceImplTest( String name ) {
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
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ServiceImpl.getPorts()'
     */
    public void testGetSetPorts() {
        Service service = ModelElementFactory.getTestService("service", "service"); //$NON-NLS-1$ //$NON-NLS-2$
        Port[] ports = new Port[] {ModelElementFactory.getTestPort("port", "portId", service)}; //$NON-NLS-1$ //$NON-NLS-2$
        service.setPorts(ports);
        Port[] newPorts = service.getPorts();
        assertEquals(newPorts.length, ports.length);
        for (int i = 0; i < ports.length; i++) {
            assertEquals(ports[i], newPorts[i]);
        }
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ServiceImpl.copy()'
     */
    public void testCopy() {
        Service service = ModelElementFactory.getTestService("service", "service"); //$NON-NLS-1$ //$NON-NLS-2$
        Service newSvc = (Service)service.copy();
        assertEquals(service, newSvc);
        assertEquals(newSvc.getName(), service.getName());
        assertEquals(newSvc.getId(), service.getName());
        Port[] ports = service.getPorts();
        Port[] newPorts = newSvc.getPorts();
        assertEquals(newPorts.length, ports.length);
        for (int i = 0; i < ports.length; i++) {
            assertEquals(ports[i], newPorts[i]);
        }
    }
}
