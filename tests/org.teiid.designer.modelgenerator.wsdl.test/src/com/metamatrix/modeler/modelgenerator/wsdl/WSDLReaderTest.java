/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

import junit.framework.TestCase;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;

public class WSDLReaderTest extends TestCase {

    public static final String CIS_WSDL = "./src/sources/CountryInfoService.wsdl"; //$NON-NLS-1$
    public static final String badWSDL = "./src/sources/InvalidCountryInfoService.wsdl"; //$NON-NLS-1$

    public WSDLReaderTest( String name ) {
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
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.WSDLReader.WSDLReader()'
     */
    public void testWSDLReader() {
        WSDLReader reader = null;
        reader = new WSDLReader();
        assertNotNull(reader);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.WSDLReader.WSDLReader(String)'
     */
    public void testWSDLReaderString() {
        WSDLReader reader = null;
        reader = new WSDLReader(CIS_WSDL);
        assertNotNull(reader);
        assertEquals(CIS_WSDL, reader.getFileUri());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.WSDLReader.getModel()'
     */
    public void testGetModel() throws ModelGenerationException {
        WSDLReader reader = new WSDLReader(CIS_WSDL);
        Model model = reader.getModel();
        assertNotNull(model);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.WSDLReader.setFileUri(String)'
     */
    public void testGetSetFileUri() {
        WSDLReader reader = new WSDLReader();
        reader.setFileUri(CIS_WSDL);
        assertEquals(CIS_WSDL, reader.getFileUri());
    }

    public void testValidateWSDLNotThereUrl() {
        final String garbageUri = CIS_WSDL + "garbage"; //$NON-NLS-1$
        validateWSDL(garbageUri, false);
    }

    public void testValidateWSDLFileNotFound() {
        final String fileNotFound = "file:///C:/temp/filenotfound"; //$NON-NLS-1$
        validateWSDL(fileNotFound, false);
    }

    public void testValidateWSDLUnknownSource() {
        final String justPlainGarbage = "sdafgsdghrg"; //$NON-NLS-1$
        validateWSDL(justPlainGarbage, false);
    }

    public void testValidateNotAWSDL() {
        final String XMLPage = "http://schemas.xmlsoap.org/soap/encoding/"; //$NON-NLS-1$
        validateWSDL(XMLPage, false);
    }

//    public void testValidateBadWSDL() {
//        validateWSDL(badWSDL, false);
//    }

    private void validateWSDL( String wsdl,
                               boolean shouldSucceed ) {
        WSDLReader reader = new WSDLReader(wsdl);
        MultiStatus status = reader.validateWSDL(new NullProgressMonitor());
        assertNotNull("The Multistatus is Null", status); //$NON-NLS-1$
        
        if (shouldSucceed) {
            assertTrue("This WSDL is valid and should have succeeded: " + wsdl, status.isOK()); //$NON-NLS-1$
        } else {
            assertFalse("This WSDL is invalid and should not have succeeded: " + wsdl, status.isOK()); //$NON-NLS-1$
            IStatus[] messages = status.getChildren();
            assertTrue("messages.length should be greater than 0", messages.length > 0); //$NON-NLS-1$
        }
    }
}
