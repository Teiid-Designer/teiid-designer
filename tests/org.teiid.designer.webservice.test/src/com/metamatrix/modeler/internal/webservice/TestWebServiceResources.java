/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.io.File;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.SmartTestDesignerSuite;

/**
 * @since 4.2
 */
public class TestWebServiceResources extends TestCase {

    public static final URI WSDL_BABEL_FISH = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/wsdl/BabelFishService.wsdl").getAbsolutePath()); //$NON-NLS-1$
    public static final URI WSDL_DAY_OF_WEEK = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/wsdl/DayOfWeek.wsdl").getAbsolutePath()); //$NON-NLS-1$
    public static final URI WSDL_HELLOSERVICE = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/wsdl/HelloService.wsdl").getAbsolutePath()); //$NON-NLS-1$
    public static final URI WSDL_HELLOSERVICE_SIMPLE = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/wsdl/HelloServiceSimple.wsdl").getAbsolutePath()); //$NON-NLS-1$
    public static final URI WSDL_HELLOSERVICE_WITHOUT_SOAP = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/wsdl/HelloServiceWithoutSoap.wsdl").getAbsolutePath()); //$NON-NLS-1$
    public static final URI WSDL_MSDOTNET_GENERATED = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/wsdl/MsDotNetGenerated.wsdl").getAbsolutePath()); //$NON-NLS-1$
    public static final URI WSDL_WEATHER_SUMMARY = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/wsdl/WeatherSummary.wsdl").getAbsolutePath()); //$NON-NLS-1$

    public static final URI AUTHORS_AND_PUBLISHERS = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/xsd/AuthorsAndPublishers.xsd").getAbsolutePath()); //$NON-NLS-1$
    public static final URI BOOK_DATATYPES = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/xsd/BookDatatypes.xsd").getAbsolutePath()); //$NON-NLS-1$
    public static final URI BOOK_REQUESTS = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/xsd/BookRequests.xsd").getAbsolutePath()); //$NON-NLS-1$
    public static final URI BOOKS = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/xsd/Books.xsd").getAbsolutePath()); //$NON-NLS-1$
    public static final URI AUTHORS_AND_PUBLISHERS_WEB_SERVICES = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/xsd/AuthorsAndPublishers_WsdlDefaultNS.wsdl").getAbsolutePath()); //$NON-NLS-1$
    public static final URI BOOKS_WEB_SERVICES = URI.createFileURI(SmartTestDesignerSuite.getTestDataFile("/xsd/BooksWebServiceWithDefaultNS.wsdl").getAbsolutePath()); //$NON-NLS-1$

    private WebServiceResources resources;

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        resources = new WebServiceResources();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        resources = null;
    }

    /**
     * Constructor for TestWebServiceResources.
     * 
     * @param name
     */
    public TestWebServiceResources( String name ) {
        super(name);
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new SmartTestDesignerSuite("org.teiid.designer.webservice", "TestWebServiceResources"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestWebServiceResources.class);
        // One-time setup and teardown
        return new TestSetup(suite) {

            @Override
            public void setUp() {
            }

            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    public void helpTestUrl( final URI uri ) {
        assertNotNull(uri);
        // Convert to a file ...
        final File f = new File(uri.toFileString());
        assertEquals("The file " + f.getAbsolutePath() + " does not exist", true, f.exists()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void helpTestAddingResource( final URI uri,
                                        final int numRoots ) {
        final Resource r = this.resources.add(uri);
        assertNotNull(r);
        assertEquals(numRoots, r.getContents().size());
        assertEquals(uri.toString(), r.getURI().toString());
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testUrls() {
        helpTestUrl(WSDL_BABEL_FISH);
        helpTestUrl(WSDL_DAY_OF_WEEK);
        helpTestUrl(WSDL_HELLOSERVICE);
        helpTestUrl(WSDL_HELLOSERVICE_SIMPLE);
        helpTestUrl(WSDL_HELLOSERVICE_WITHOUT_SOAP);
        helpTestUrl(WSDL_MSDOTNET_GENERATED);
        helpTestUrl(WSDL_WEATHER_SUMMARY);

        helpTestUrl(AUTHORS_AND_PUBLISHERS);
        helpTestUrl(BOOK_DATATYPES);
        helpTestUrl(BOOK_REQUESTS);
        helpTestUrl(BOOKS);
        helpTestUrl(BOOKS_WEB_SERVICES);
        helpTestUrl(AUTHORS_AND_PUBLISHERS_WEB_SERVICES);
    }

    public void testConstruction() {
        assertNotNull(this.resources);
    }

    public void testAddingWsdlResources() {
        helpTestAddingResource(WSDL_BABEL_FISH, 1);
        helpTestAddingResource(WSDL_DAY_OF_WEEK, 1);
        helpTestAddingResource(WSDL_HELLOSERVICE, 1);
        helpTestAddingResource(WSDL_HELLOSERVICE_SIMPLE, 1);
        helpTestAddingResource(WSDL_HELLOSERVICE_WITHOUT_SOAP, 1);
        helpTestAddingResource(WSDL_MSDOTNET_GENERATED, 1);
        helpTestAddingResource(WSDL_WEATHER_SUMMARY, 1);
        helpTestAddingResource(AUTHORS_AND_PUBLISHERS_WEB_SERVICES, 1);
    }

    public void testAddingXsdResources() {
        helpTestAddingResource(AUTHORS_AND_PUBLISHERS, 1);
        helpTestAddingResource(BOOK_DATATYPES, 1);
        helpTestAddingResource(BOOK_REQUESTS, 1);
        helpTestAddingResource(BOOKS, 1);
    }

    public void testAddingWsdlAndXsdResources() {
        helpTestAddingResource(WSDL_BABEL_FISH, 1);
        helpTestAddingResource(WSDL_DAY_OF_WEEK, 1);
        helpTestAddingResource(WSDL_HELLOSERVICE, 1);
        helpTestAddingResource(WSDL_HELLOSERVICE_SIMPLE, 1);
        helpTestAddingResource(WSDL_HELLOSERVICE_WITHOUT_SOAP, 1);
        helpTestAddingResource(WSDL_MSDOTNET_GENERATED, 1);
        helpTestAddingResource(WSDL_WEATHER_SUMMARY, 1);

        helpTestAddingResource(AUTHORS_AND_PUBLISHERS, 1);
        helpTestAddingResource(BOOK_DATATYPES, 1);
        helpTestAddingResource(BOOK_REQUESTS, 1);
        helpTestAddingResource(BOOKS, 1);
        helpTestAddingResource(AUTHORS_AND_PUBLISHERS_WEB_SERVICES, 1);
    }

}
