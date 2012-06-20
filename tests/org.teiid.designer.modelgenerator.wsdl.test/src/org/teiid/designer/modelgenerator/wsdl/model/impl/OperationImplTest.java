/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model.impl;

import junit.framework.TestCase;

import org.teiid.designer.modelgenerator.wsdl.model.Binding;
import org.teiid.designer.modelgenerator.wsdl.model.Fault;
import org.teiid.designer.modelgenerator.wsdl.model.Message;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;

public class OperationImplTest extends TestCase {

    public OperationImplTest( String name ) {
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
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.OperationImpl.OperationImpl(Binding)'
     */
    public void testOperationImpl() {
        Operation oper = new OperationImpl(
                                           ModelElementFactory.getTestBinding("testBinding", "testBindingId", "http://test/test.wsdl", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertNotNull(oper);
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.OperationImpl.getBinding()'
     */
    public void testGetBinding() {
        Binding binding = ModelElementFactory.getTestBinding("testBinding", "testBindingId", "http://test/test.wsdl", "Request_Response"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Operation oper = new OperationImpl(binding);
        assertEquals(binding, oper.getBinding());
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.OperationImpl.copy()'
     */
    public void testCopy() {
        Operation oper = ModelElementFactory.getTestOperation("testOperation", "testOperation", "Request_Response"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Operation oper2 = (Operation)oper.copy();
        assertEquals(oper, oper2);
        assertEquals(oper.getName(), oper2.getName());
        assertEquals(oper.getId(), oper2.getId());
        assertEquals(oper.getStyle(), oper2.getStyle());
        assertEquals(oper.getBinding(), oper2.getBinding());
        assertEquals(oper.getInputMessage(), oper2.getInputMessage());
        assertEquals(oper.getOutputMessage(), oper2.getOutputMessage());
        Fault[] fault1 = oper.getFaults();
        Fault[] fault2 = oper2.getFaults();
        assertEquals(fault1.length, fault2.length);
        for (int i = 0; i < fault1.length; i++) {
            assertEquals(fault1[i], fault2[i]);
        }
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.OperationImpl.getInputMessage()'
     */
    public void testSetGetInputMessage() {
        Operation oper = new OperationImpl(
                                           ModelElementFactory.getTestBinding("testBinding", "testBindingId", "http://test/test.wsdl", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Message msg = ModelElementFactory.getTestMessage("testMessage", "testMessageId", Message.REQUEST_TYPE, oper); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setInputMessage(msg);
        assertEquals(msg, oper.getInputMessage());
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.OperationImpl.getOutputMessage()'
     */
    public void testSetGetOutputMessage() {
        Operation oper = new OperationImpl(
                                           ModelElementFactory.getTestBinding("testBinding", "testBindingId", "http://test/test.wsdl", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Message msg = ModelElementFactory.getTestMessage("testMessage", "testMessageId", Message.RESPONSE_TYPE, oper); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setOutputMessage(msg);
        assertEquals(msg, oper.getOutputMessage());
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.OperationImpl.getStyle()'
     */
    public void testSetGetStyle() {
        Operation oper = new OperationImpl(
                                           ModelElementFactory.getTestBinding("testBinding", "testBindingId", "http://test/test.wsdl", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String style = "Request_Response"; //$NON-NLS-1$
        oper.setStyle(style);
        assertEquals(style, oper.getStyle());
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.internal.OperationImpl.getFaults()'
     */
    public void testSetGetFaults() {
        Operation oper = new OperationImpl(
                                           ModelElementFactory.getTestBinding("testBinding", "testBindingId", "http://test/test.wsdl", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Fault[] faults = new Fault[] {ModelElementFactory.getTestFault("testFault", "testFaultId", oper)}; //$NON-NLS-1$ //$NON-NLS-2$
        oper.setFaults(faults);
        assertSame(faults, oper.getFaults());
    }
}
