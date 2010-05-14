/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.gen;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.metamodels.webservice.WebServiceTestUtil;

/**
 * @since 4.2
 */
public class TestBasicWsdlGenerator extends TestCase {

    public static final String PATH_TO_XSD1 = "BookRequests.xsd"; //$NON-NLS-1$
    public static final String PATH_TO_XSD2 = "BookDatatypes.xsd"; //$NON-NLS-1$

    private Resource webService1;
    private Resource webService2;
    private XSDResourceImpl xsd1;
    private XSDResourceImpl xsd2;
    private XSDSchema schema1;
    private XSDSchema schema2;
    private IPath schema1LocationPath;
    private IPath schema2LocationPath;

    private BasicWsdlGenerator generator;

    /**
     * Constructor for TestBasicWsdlGenerator.
     * 
     * @param name
     */
    public TestBasicWsdlGenerator( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.generator = new BasicWsdlGenerator();

        final URI uri1 = URI.createURI("/someProject/webservice1"); //$NON-NLS-1$
        this.webService1 = WebServiceTestUtil.createMinimalWebServiceModel(uri1);

        final URI uri2 = URI.createURI("/someProject/webservice2"); //$NON-NLS-1$
        this.webService2 = WebServiceTestUtil.createMinimalWebServiceModel(uri2);

        final XSDResourceFactoryImpl xsdFactory = new XSDResourceFactoryImpl();
        final File xsdFile = SmartTestSuite.getTestDataFile(PATH_TO_XSD1);
        if (xsdFile.exists() == false) {
            System.out.println("Missing File " + xsdFile.getCanonicalPath()); //$NON-NLS-1$
        }
        final URI xsdUri = URI.createFileURI(xsdFile.getCanonicalPath());
        this.xsd1 = (XSDResourceImpl)xsdFactory.createResource(xsdUri);
        Map options = (this.xsd1.getResourceSet() != null ? this.xsd1.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
        this.xsd1.load(options);
        this.schema1 = this.xsd1.getSchema();

        final File xsdFile2 = SmartTestSuite.getTestDataFile(PATH_TO_XSD2);
        if (xsdFile2.exists() == false) {
            System.out.println("Missing File " + xsdFile2.getCanonicalPath()); //$NON-NLS-1$
        }
        final URI xsdUri2 = URI.createFileURI(xsdFile2.getCanonicalPath());
        this.xsd2 = (XSDResourceImpl)xsdFactory.createResource(xsdUri2);
        options = (this.xsd2.getResourceSet() != null ? this.xsd2.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
        this.xsd2.load(options);
        this.schema2 = this.xsd2.getSchema();

        this.schema1LocationPath = new Path("/someProject/" + PATH_TO_XSD1); //$NON-NLS-1$
        this.schema1LocationPath = new Path("/someProject/" + PATH_TO_XSD2); //$NON-NLS-1$
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new SmartTestSuite("org.teiid.designer.webservice", "TestBasicWsdlGenerator"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestBasicWsdlGenerator.class);
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

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testSetup() {
        assertNotNull(this.webService1);
        assertNotNull(this.webService2);
        assertNotNull(this.xsd1);
        assertNotNull(this.xsd2);
        assertNotNull(this.schema1);
        assertNotNull(this.schema2);
    }

    public void testBasicWsdlGenerator() {
        assertNotNull(new BasicWsdlGenerator());
    }

    public void testBasicWsdlGeneratorWithNullUri() {
        try {
            new BasicWsdlGenerator(null);
            fail("Should have caught null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException err) {
            // expected
        }
    }

    public void testAddWebServiceModel() {
        assertEquals(0, this.generator.getWebServiceModels().size());
        this.generator.addWebServiceModel(webService1);
        assertEquals(1, this.generator.getWebServiceModels().size());

        this.generator.addWebServiceModel(webService1);
        assertEquals(1, this.generator.getWebServiceModels().size());

        this.generator.addWebServiceModel(webService2);
        assertEquals(2, this.generator.getWebServiceModels().size());
    }

    public void testAddXsdModel() {
        assertEquals(0, this.generator.getXSDSchemas().size());
        this.generator.addXsdModel(schema1, this.schema1LocationPath);
        assertEquals(1, this.generator.getXSDSchemas().size());
        assertEquals(this.schema1LocationPath, this.generator.getLocationPathForXsdModel(schema1));

        this.generator.addXsdModel(schema1, this.schema1LocationPath);
        assertEquals(1, this.generator.getXSDSchemas().size());
        assertEquals(this.schema1LocationPath, this.generator.getLocationPathForXsdModel(schema1));

        this.generator.addXsdModel(schema2, this.schema2LocationPath);
        assertEquals(2, this.generator.getXSDSchemas().size());
        assertEquals(this.schema2LocationPath, this.generator.getLocationPathForXsdModel(schema2));
    }

    public void testGetWebServiceModels() {
        assertNotNull(this.generator.getWebServiceModels());
    }

    public void testGetXSDSchemas() {
        assertNotNull(this.generator.getXSDSchemas());
    }

    public void testGenerate() {
    }

    public void testWrite() {
    }

    public void testCloseWithEmptyGenerator() {
        this.generator.close();
    }

    public void testClose() {
        this.generator.addWebServiceModel(webService1);
        this.generator.addWebServiceModel(webService2);
        assertEquals(2, this.generator.getWebServiceModels().size());

        this.generator.addXsdModel(schema1, this.schema1LocationPath);
        this.generator.addXsdModel(schema2, this.schema2LocationPath);
        assertEquals(2, this.generator.getXSDSchemas().size());

        this.generator.close();

        assertEquals(0, this.generator.getWebServiceModels().size());
        assertEquals(0, this.generator.getXSDSchemas().size());
    }

}
