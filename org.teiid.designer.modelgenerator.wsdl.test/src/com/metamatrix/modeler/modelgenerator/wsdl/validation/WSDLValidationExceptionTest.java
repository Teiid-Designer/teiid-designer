/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.validation;

import java.util.ArrayList;
import javax.wsdl.WSDLException;
import junit.framework.TestCase;

public class WSDLValidationExceptionTest extends TestCase {

    public WSDLValidationExceptionTest( String name ) {
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
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidationException.WSDLValidationException()'
     */
    public void testWSDLValidationException() {
        WSDLValidationException ex = new WSDLValidationException();
        assertNotNull(ex);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidationException.WSDLValidationException(ArrayList)'
     */
    public void testWSDLValidationExceptionArrayListAndGetValidationMessages() {
        ArrayList messages = new ArrayList();
        final String message1 = "message1"; //$NON-NLS-1$
        final String message2 = "message2"; //$NON-NLS-1$
        messages.add(message1);
        messages.add(message2);
        WSDLValidationException ex = new WSDLValidationException(messages);
        assertNotNull(ex);
        ArrayList listOut = ex.getValidationMessages();
        assertNotNull(listOut);
        assertEquals(listOut.get(0), message1);
        assertEquals(listOut.get(1), message2);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidationException.WSDLValidationException(WSDLException)'
     */
    public void testWSDLValidationExceptionWSDLException() {
        final String faultCode = "WSDLFault"; //$NON-NLS-1$
        final String faultMsg = "A fault"; //$NON-NLS-1$
        WSDLException wsEx = new WSDLException(faultCode, faultMsg);
        WSDLValidationException ex = new WSDLValidationException(wsEx);
        assertNotNull(ex);
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof WSDLException);
        assertEquals(wsEx.getFaultCode(), ((WSDLException)ex.getCause()).getFaultCode());
        assertEquals(wsEx.getMessage(), ex.getCause().getMessage());
    }

}
