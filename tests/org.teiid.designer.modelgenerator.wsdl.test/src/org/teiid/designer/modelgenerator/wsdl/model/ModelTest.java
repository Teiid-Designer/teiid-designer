/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import junit.framework.TestCase;

import org.teiid.designer.modelgenerator.wsdl.WSDLReader;
import org.teiid.designer.modelgenerator.wsdl.WSDLReaderTest;

/**
 * 
 */
public class ModelTest extends TestCase {

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.WSDLReader.getModel()'
     */
    public void testGetModel() throws ModelGenerationException {
        WSDLReader reader = new WSDLReader(WSDLReaderTest.CIS_WSDL);
        Model model = reader.getModel();
        assertNotNull(model);
        assertEquals("Incorrect namespace count", 4, model.getNamespaces().size()); //$NON-NLS-1$
        assertEquals("Incorrect Service count", 1, model.getServices().length); //$NON-NLS-1$
        Service service = model.getServices()[0];
        assertEquals("Incorrect Service name", "CountryInfoService", service.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("These should be the same service", service, model.getService(service.getName())); //$NON-NLS-1$
        assertEquals("Incorrect Port Count", 1, model.getServices()[0].getPorts().length); //$NON-NLS-1$
        Port port = model.getServices()[0].getPorts()[0];
        assertEquals("Incorrect Port Name", "CountryInfoServiceSoap", port.getName()); //$NON-NLS-1$//$NON-NLS-2$
        assertEquals("These should be the same port", port, model.getPort(port.getName())); //$NON-NLS-1$
        assertEquals("These should be the same operations", //$NON-NLS-1$
                     model.getOperation("ListOfContinentsByName"), //$NON-NLS-1$
                     port.getBinding().getOperations()[0]);
    }
}
