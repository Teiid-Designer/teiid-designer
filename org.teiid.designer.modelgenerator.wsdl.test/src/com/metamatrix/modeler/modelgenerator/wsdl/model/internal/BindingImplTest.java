/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import junit.framework.TestCase;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Binding;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;

public class BindingImplTest extends TestCase {

    public BindingImplTest( String name ) {
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
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.BindingImpl.BindingImpl(Port)'
     */
    public void testBindingImpl() {
        Binding newBinding = new BindingImpl(ModelElementFactory.getTestPort("testPort", "testPortId")); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(newBinding);

    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.BindingImpl.getOperations()'
     */
    public void testGetSetOperations() {
        Binding binding = new BindingImpl(ModelElementFactory.getTestPort("testPort", "testPortId")); //$NON-NLS-1$ //$NON-NLS-2$
        Operation[] opers = new Operation[] {ModelElementFactory.getTestOperation("testOperation", "testOperationId", "Request_Response", binding)}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        binding.setOperations(opers);
        assertEquals(binding.getOperations().length, opers.length);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.BindingImpl.getPort()'
     */
    public void testGetPort() {
        Port parent = ModelElementFactory.getTestPort("testPort", "testPortId"); //$NON-NLS-1$ //$NON-NLS-2$
        Binding binding = new BindingImpl(parent);
        Port port = binding.getPort();
        assertEquals(port, parent);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.BindingImpl.copy()'
     */
    public void testCopy() {
        Binding binding = ModelElementFactory.getTestBinding("testBinding", //$NON-NLS-1$
                                                             "testBinding", //$NON-NLS-1$
                                                             "http://test/test.wsdl", //$NON-NLS-1$
                                                             "Request_Response"); //$NON-NLS-1$
        Binding binding2 = (Binding)binding.copy();
        assertEquals(binding, binding2);
        assertEquals(binding2.getId(), binding.getId());
        assertEquals(binding2.getName(), binding.getName());
        Operation[] oper1 = binding.getOperations();
        Operation[] oper2 = binding2.getOperations();
        assertEquals(oper1.length, oper2.length);
        for (int i = 0; i < oper1.length; i++) {
            assertEquals(oper1[i], oper2[i]);
        }
        assertEquals(binding2.getStyle(), binding.getStyle());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.BindingImpl.setTransportURI(String)'
     */
    public void testGetSetTransportURI() {
        Binding binding = new BindingImpl(ModelElementFactory.getTestPort("testPort", "testPortId")); //$NON-NLS-1$ //$NON-NLS-2$
        final String uri = "http://foo/bar.wsdl"; //$NON-NLS-1$
        binding.setTransportURI(uri);
        assertEquals(uri, binding.getTransportURI());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.BindingImpl.setStyle(String)'
     */
    public void testGetSetStyle() {
        Binding binding = new BindingImpl(ModelElementFactory.getTestPort("testPort", "testPortId")); //$NON-NLS-1$ //$NON-NLS-2$
        final String style = "Request_Response"; //$NON-NLS-1$
        binding.setStyle(style);
        assertEquals(binding.getStyle(), style);
    }
}
