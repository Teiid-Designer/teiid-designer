/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import junit.framework.TestCase;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;

public class PartImplTest extends TestCase {

    public PartImplTest( String name ) {
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
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PartImpl.PartImpl(Message)'
     */
    public void testPartImpl() {
        Part part = new PartImpl(ModelElementFactory.getTestMessage("testMessage", "testMessageId", Message.REQUEST_TYPE)); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(part);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PartImpl.getElementName()'
     */
    public void testGetSetElementName() {
        Part part = new PartImpl(ModelElementFactory.getTestMessage("testMessage", "testMessageId", Message.REQUEST_TYPE)); //$NON-NLS-1$ //$NON-NLS-2$
        String eName = "elem"; //$NON-NLS-1$
        part.setElementName(eName);
        assertEquals(eName, part.getElementName());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PartImpl.getElementNamespace()'
     */
    public void testGetSetElementNamespace() {
        Part part = new PartImpl(ModelElementFactory.getTestMessage("testMessage", "testMessageId", Message.REQUEST_TYPE)); //$NON-NLS-1$ //$NON-NLS-2$
        String eName = "http://www.metamatrix.com/elem"; //$NON-NLS-1$
        part.setElementNamespace(eName);
        assertEquals(eName, part.getElementNamespace());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PartImpl.getTypeName()'
     */
    public void testGetSetTypeName() {
        Part part = new PartImpl(ModelElementFactory.getTestMessage("testMessage", "testMessageId", Message.REQUEST_TYPE)); //$NON-NLS-1$ //$NON-NLS-2$
        String eName = "string"; //$NON-NLS-1$
        part.setTypeName(eName);
        assertEquals(eName, part.getTypeName());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PartImpl.getTypeNamespace()'
     */
    public void testGetSetTypeNamespace() {
        Part part = new PartImpl(ModelElementFactory.getTestMessage("testMessage", "testMessageId", Message.REQUEST_TYPE)); //$NON-NLS-1$ //$NON-NLS-2$
        String eName = "http://www.metamatrix.com/elem"; //$NON-NLS-1$
        part.setTypeNamespace(eName);
        assertEquals(eName, part.getTypeNamespace());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PartImpl.getMessage()'
     */
    public void testGetMessage() {
        Message message = ModelElementFactory.getTestMessage("testMessage", "testMessageId", Message.REQUEST_TYPE); //$NON-NLS-1$ //$NON-NLS-2$
        Part part = new PartImpl(message);
        assertEquals(message, part.getMessage());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.PartImpl.copy()'
     */
    public void testCopy() {
        Part part = ModelElementFactory.getTestPart("testMessage", "testMessageId"); //$NON-NLS-1$ //$NON-NLS-2$
        Part part2 = (Part)part.copy();
        assertEquals(part, part2);
        assertEquals(part.getName(), part2.getName());
        assertEquals(part.getId(), part2.getId());
        assertEquals(part.getElementName(), part2.getElementName());
        assertEquals(part.getElementNamespace(), part2.getElementNamespace());
        assertEquals(part.getTypeName(), part2.getTypeName());
        assertEquals(part.getTypeNamespace(), part2.getTypeNamespace());
        assertEquals(part.getMessage(), part2.getMessage());
    }

}
