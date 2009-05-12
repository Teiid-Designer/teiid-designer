/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import junit.framework.TestCase;
import com.metamatrix.core.log.Logger;
import com.metamatrix.core.log.NullLogger;
import com.metamatrix.modeler.modelgenerator.wsdl.WSDLReaderTest;

public class ModelBuilderTest extends TestCase {

    private final String badURI = WSDLReaderTest.CIS_WSDL + "fdfdfd"; //$NON-NLS-1$
    private final String notURI = "this is decidedly NOT a URI"; //$NON-NLS-1$
    private final String fileNotFound = "file:///../FileNotFound.wsdl"; //$NON-NLS-1$
    private Logger logger;

    public ModelBuilderTest( String name ) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        logger = new NullLogger();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ModelBuilder.ModelBuilder()'
     */
    public void testModelBuilder() {
        ModelBuilder builder = new ModelBuilder();
        assertNotNull(builder);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ModelBuilder.setWSDL(String)'
     */
    public void testSetWSDLGood() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(WSDLReaderTest.CIS_WSDL);
    }

    public void testSetWSDLBad() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(badURI);
    }

    public void testSetWSDLBadWSDL() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(WSDLReaderTest.badWSDL);
    }

    public void testSetWSDLNotURI() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(notURI);
    }

    public void testSetWSDLFileNotFound() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(fileNotFound);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ModelBuilder.isWSDLParsed()'
     */
    public void testIsWSDLParsedNoWSDL() {
        ModelBuilder builder = new ModelBuilder();
        assertFalse(builder.isWSDLParsed());
    }

    public void testIsWSDLParsedGood() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(WSDLReaderTest.CIS_WSDL);
        assertTrue(builder.isWSDLParsed());
    }

    public void testIsWSDLParsedBad() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(badURI);
        assertFalse(builder.isWSDLParsed());
    }

    public void testIsWSDLParsedFileNotFound() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(fileNotFound);
        assertFalse(builder.isWSDLParsed());
    }

    public void testIsWSDLParsedNotURI() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(notURI);
        assertFalse(builder.isWSDLParsed());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ModelBuilder.getWSDLException()'
     */
    public void testGetWSDLExceptionNoWSDL() {
        ModelBuilder builder = new ModelBuilder();
        assertNull(builder.getWSDLException());
    }

    public void testGetWSDLExceptionGood() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(WSDLReaderTest.CIS_WSDL);
        assertNull(builder.getWSDLException());

    }

    public void testGetWSDLExceptionBad() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(badURI);
        assertNotNull(builder.getWSDLException());
    }

    public void testGetWSDLExceptionFileNotFound() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(fileNotFound);
        assertNotNull(builder.getWSDLException());
    }

    public void testGetWSDLExceptionNotURI() {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(notURI);
        assertNotNull(builder.getWSDLException());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ModelBuilder.getModel()'
     */
    public void testGetModelNoWSDL() throws Exception {
        ModelBuilder builder = new ModelBuilder();
        assertNull(builder.getModel(logger));
    }

    public void testGetModelGood() throws Exception {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(WSDLReaderTest.CIS_WSDL);
        assertNotNull(builder.getModel(logger));
    }

    public void testGetModelBad() throws Exception {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(badURI);
        assertNull(builder.getModel(logger));
    }

    public void testGetModelFileNotFound() throws Exception {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(fileNotFound);
        assertNull(builder.getModel(logger));
    }

    public void testGetModelNotURI() throws Exception {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(notURI);
        assertNull(builder.getModel(logger));
    }
}
