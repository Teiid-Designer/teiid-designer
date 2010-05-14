/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import junit.framework.TestCase;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Fault;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;

public class FaultImplTest extends TestCase {

    public FaultImplTest( String name ) {
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
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.FaultImpl.FaultImpl(Operation)'
     */
    public void testFaultImpl() {
        Fault fault = new FaultImpl(ModelElementFactory.getTestOperation("testOperation", "testOperationId", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertNotNull(fault);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.FaultImpl.getOperation()'
     */
    public void testGetOperation() {
        Fault theFault = new FaultImpl(
                                       ModelElementFactory.getTestOperation("testOperation", "testOperationId", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Operation oper = theFault.getOperation();
        assertNotNull(oper);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.FaultImpl.setMessage(Message)'
     */
    public void testGetSetMessage() {
        Fault theFault = new FaultImpl(
                                       ModelElementFactory.getTestOperation("testOperation", "testOperationId", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        theFault.setName("fault"); //$NON-NLS-1$
        theFault.setId("faultId"); //$NON-NLS-1$
        Message newMessage = ModelElementFactory.getTestMessage("testMessage", "testMessageId", theFault); //$NON-NLS-1$ //$NON-NLS-2$
        theFault.setMessage(newMessage);
        assertEquals(newMessage, theFault.getMessage());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.FaultImpl.copy()'
     */
    public void testCopy() {
        Fault theFault = new FaultImpl(ModelElementFactory.getTestOperation("testOperation", //$NON-NLS-1$
                                                                            "testOperationId", //$NON-NLS-1$
                                                                            "Request_Response")); //$NON-NLS-1$
        theFault.setId("faultId"); //$NON-NLS-1$
        theFault.setName("fault"); //$NON-NLS-1$
        Message newMessage = ModelElementFactory.getTestMessage("testMessage", "testMessageId", theFault); //$NON-NLS-1$ //$NON-NLS-2$
        theFault.setMessage(newMessage);
        Fault newFault = (Fault)theFault.copy();
        assertEquals(newFault, theFault);
        assertEquals(newFault.getId(), theFault.getId());
        assertEquals(newFault.getName(), theFault.getName());
        assertEquals(newFault.getMessage(), theFault.getMessage());
        assertEquals(newFault.getOperation(), theFault.getOperation());
    }

}
