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
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;

public class MessageImplTest extends TestCase {

    public MessageImplTest( String name ) {
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
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.MessageImpl(Operation)'
     */
    public void testMessageImplOperation() {
        Message message = new MessageImpl(ModelElementFactory.getTestOperation("oper", "oper", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertNotNull(message);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.MessageImpl(Fault)'
     */
    public void testMessageImplFault() {
        Fault fault = ModelElementFactory.getTestFault("testFault", "testFault"); //$NON-NLS-1$ //$NON-NLS-2$
        Message theMessage = new MessageImpl(fault);
        assertNotNull(theMessage);
        assertEquals(fault, theMessage.getFault());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.setParts(Part[])'
     */
    public void testGetSetParts() {
        Message message = new MessageImpl(
                                          ModelElementFactory.getTestOperation("testOperation", "testOperationId", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Part[] parts = new Part[] {ModelElementFactory.getTestPart("testPart", "testPart")}; //$NON-NLS-1$ //$NON-NLS-2$
        message.setParts(parts);
        Part[] parts2 = message.getParts();
        assertEquals(parts.length, parts2.length);
        for (int i = 0; i < parts.length; i++) {
            assertEquals(parts[i], parts2[i]);
        }
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.getOperation()'
     */
    public void testGetOperation() {
        Operation oper = ModelElementFactory.getTestOperation("testOperation", "testOperationId", "Request_Response"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Message msg = new MessageImpl(oper);
        assertEquals(oper, msg.getOperation());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.isRequest()'
     */
    public void testIsRequest() {
        Message message = new MessageImpl(ModelElementFactory.getTestOperation("testOperation", //$NON-NLS-1$
                                                                               "testOperationId", //$NON-NLS-1$
                                                                               "Request_Response")); //$NON-NLS-1$
        message.setType(Message.REQUEST_TYPE);
        assertTrue(message.isRequest());
        message.setType(Message.RESPONSE_TYPE);
        assertFalse(message.isRequest());
        message.setType(Message.FAULT_TYPE);
        assertFalse(message.isRequest());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.isResponse()'
     */
    public void testIsResponse() {
        Message message = new MessageImpl(ModelElementFactory.getTestOperation("testOperation", //$NON-NLS-1$
                                                                               "testOperationId", //$NON-NLS-1$
                                                                               "Request_Response")); //$NON-NLS-1$
        message.setType(Message.RESPONSE_TYPE);
        assertTrue(message.isResponse());
        message.setType(Message.REQUEST_TYPE);
        assertFalse(message.isResponse());
        message.setType(Message.FAULT_TYPE);
        assertFalse(message.isResponse());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.isFault()'
     */
    public void testIsFault() {
        Message message = new MessageImpl(ModelElementFactory.getTestOperation("testOperation", //$NON-NLS-1$
                                                                               "testOperationId", //$NON-NLS-1$
                                                                               "Request_Response")); //$NON-NLS-1$
        message.setType(Message.FAULT_TYPE);
        assertTrue(message.isFault());
        message.setType(Message.RESPONSE_TYPE);
        assertFalse(message.isFault());
        message.setType(Message.REQUEST_TYPE);
        assertFalse(message.isFault());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.setType(int)'
     */
    public void testGetSetType() {
        Message message = new MessageImpl(ModelElementFactory.getTestOperation("testOperation", //$NON-NLS-1$
                                                                               "testOperationId", //$NON-NLS-1$
                                                                               "Request_Response")); //$NON-NLS-1$
        message.setType(Message.REQUEST_TYPE);
        assertEquals(message.getType(), Message.REQUEST_TYPE);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.copy()'
     */
    public void testCopy() {
        Message message = new MessageImpl(ModelElementFactory.getTestOperation("testOperation", //$NON-NLS-1$
                                                                               "testOperationId", //$NON-NLS-1$
                                                                               "Request_Response")); //$NON-NLS-1$
        message.setType(Message.REQUEST_TYPE);
        message.setId("message"); //$NON-NLS-1$
        message.setName("message"); //$NON-NLS-1$
        message.setParts(new Part[0]);
        Message message2 = (Message)message.copy();
        assertEquals(message, message2);
        assertEquals(message2.getName(), message.getName());
        assertEquals(message2.getId(), message.getId());
        assertEquals(message2.getFault(), message.getFault());
        assertEquals(message2.getOperation(), message.getOperation());
        Part[] part1 = message.getParts();
        Part[] part2 = message.getParts();
        assertEquals(part1.length, part2.length);
        for (int i = 0; i < part1.length; i++) {
            assertEquals(part1[i], part2[i]);
        }
        assertEquals(message2.getType(), message.getType());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.MessageImpl.getFault()'
     */
    public void testGetFault() {
        Fault fault = ModelElementFactory.getTestFault("testFault", "testFaultId"); //$NON-NLS-1$ //$NON-NLS-2$
        Message theMessage = new MessageImpl(fault);
        assertEquals(theMessage.getFault(), fault);
    }

}
