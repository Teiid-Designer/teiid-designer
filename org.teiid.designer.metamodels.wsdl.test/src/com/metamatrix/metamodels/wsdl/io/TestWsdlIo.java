/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Types;
import com.metamatrix.metamodels.wsdl.WsdlFactory;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;

/**
 * @since 4.2
 */
public class TestWsdlIo extends TestCase {

    private ResourceSet resourceSet;
    private Map options;
    private WsdlFactory factory;
    private XSDFactory xsdFactory;

    /**
     * Constructor for TestWsdlIo.
     * 
     * @param name
     */
    public TestWsdlIo( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create a resource set that can delegate to the XSDSchema's global resource set ...
        this.resourceSet = new DelegatingResourceSet();
        final ResourceSet xsdGlobalResourceSet = XSDSchemaImpl.getGlobalResourceSet();
        ((DelegatingResourceSet)this.resourceSet).addDelegateResourceSet(xsdGlobalResourceSet);

        // Register the resource factory for each of the 4 WSDL metamodels ...
        final Resource.Factory.Registry registry = this.resourceSet.getResourceFactoryRegistry();
        registry.getExtensionToFactoryMap().put("wsdl", new WsdlResourceFactoryImpl()); //$NON-NLS-1$

        registry.getProtocolToFactoryMap().put(WsdlPackage.eNS_URI, new WsdlResourceFactoryImpl());
        // registry.getProtocolToFactoryMap().put(SoapPackage.eNS_URI, new SoapResourceFactoryImpl());
        // registry.getProtocolToFactoryMap().put(MimePackage.eNS_URI, new MimeResourceFactoryImpl());
        // registry.getProtocolToFactoryMap().put(HttpPackage.eNS_URI, new HttpResourceFactoryImpl());

        this.options = new HashMap();

        this.factory = WsdlFactory.eINSTANCE;
        this.xsdFactory = XSDFactory.eINSTANCE;
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
        TestSuite suite = new TestSuite("TestWsdlIo"); //$NON-NLS-1$
        suite.addTestSuite(TestWsdlIo.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
                try {
                    // Register the 4 WSDL metamodels ...
                    EPackage.Registry.INSTANCE.put(WsdlPackage.eNS_URI, WsdlPackage.eINSTANCE);
                    EPackage.Registry.INSTANCE.put(MimePackage.eNS_URI, MimePackage.eINSTANCE);
                    EPackage.Registry.INSTANCE.put(HttpPackage.eNS_URI, HttpPackage.eINSTANCE);
                    EPackage.Registry.INSTANCE.put(SoapPackage.eNS_URI, SoapPackage.eINSTANCE);

                    // Register the XSD metamodel ...
                    EPackage.Registry.INSTANCE.put(XSDPackage.eNS_URI, XSDPackage.eINSTANCE);

                    // Load the XSD Schema of Schemas ...
                    final ResourceSet xsdGlobalResourceSet = XSDSchemaImpl.getGlobalResourceSet();
                    final XSDSchema schema1 = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
                    final XSDSchema schema2 = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10);
                    final XSDSchema schema3 = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999);
                    final XSDSchema schema4 = XSDSchemaImpl.getSchemaInstance(XSDConstants.SCHEMA_INSTANCE_URI_2001);
                    final XSDSchema magicSchema1 = XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
                    final XSDSchema magicSchema2 = XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10);
                    final XSDSchema magicSchema3 = XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999);

                    assertNotNull(schema1);
                    assertNotNull(schema2);
                    assertNotNull(schema3);
                    assertNotNull(schema4);
                    assertNotNull(magicSchema1);
                    assertNotNull(magicSchema2);
                    assertNotNull(magicSchema3);

                    final Map uriMap = xsdGlobalResourceSet.getURIConverter().getURIMap();
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001), schema1.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10), schema2.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999), schema3.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_INSTANCE_URI_2001), schema4.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001), magicSchema1.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10), magicSchema2.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999), magicSchema3.eResource().getURI());
                } catch (Throwable err) {
                    err.printStackTrace();
                }

            }

            @Override
            public void tearDown() {
            }
        };
    }

    public Resource helpReadAndWrite( final String pathInTestData ) throws Exception {

        final Resource input = helpRead(pathInTestData);

        // Create a new resource ...
        final Resource output = helpCreateNewResource(pathInTestData);

        // Move the contents from the input to the output ...
        final List outputRoots = output.getContents();
        final List inputRoots = new ArrayList(input.getContents()); // make a copy so no concurrent mod!
        final Iterator iter = inputRoots.iterator();
        while (iter.hasNext()) {
            final EObject root = (EObject)iter.next();
            outputRoots.add(root);
        }

        // Save the resource ...
        output.save(new HashMap());
        output.save(System.out, new HashMap());

        return output;
    }

    public Resource helpRead( final String pathInTestData ) throws Exception {
        final File wsdlFile = SmartTestSuite.getTestDataFile(pathInTestData);
        final String wsdlPath = wsdlFile.getAbsolutePath();
        assertNotNull("File not found at " + wsdlPath, wsdlFile); //$NON-NLS-1$
        assertEquals("File doesn't exist at " + wsdlPath, true, wsdlFile.exists()); //$NON-NLS-1$
        assertEquals("File can't be read at " + wsdlPath, true, wsdlFile.canRead()); //$NON-NLS-1$

        System.out.println("Reading file " + wsdlPath); //$NON-NLS-1$

        final URI wsdlUri = URI.createFileURI(wsdlPath);
        assertNotNull("URI not created", wsdlUri); //$NON-NLS-1$

        final Resource resource = this.resourceSet.getResource(wsdlUri, true);
        assertNotNull("Resource not loaded", resource); //$NON-NLS-1$

        // Show the errors ...
        final List errors = resource.getErrors();
        if (errors != null && errors.size() != 0) {
            System.out.println("Found " + errors.size() + " errors"); //$NON-NLS-1$ //$NON-NLS-2$
            final Iterator iter = errors.iterator();
            while (iter.hasNext()) {
                Object error = iter.next();
                System.out.println(error);
            }
        }

        // Show the warnings ...
        final List warnings = resource.getWarnings();
        if (warnings != null && warnings.size() != 0) {
            System.out.println("Found " + warnings.size() + " warnings"); //$NON-NLS-1$ //$NON-NLS-2$
            final Iterator iter = warnings.iterator();
            while (iter.hasNext()) {
                Object warning = iter.next();
                System.out.println(warning);
            }
        }

        resource.load(this.options);

        final List roots = resource.getContents();
        for (final Iterator iter = roots.iterator(); iter.hasNext();) {
            EObject root = (EObject)iter.next();
            System.out.println(root);
        }

        // Count the number of objects ...
        int counter = 0;
        for (final Iterator iter = resource.getAllContents(); iter.hasNext();) {
            EObject obj = (EObject)iter.next();
            assertNotNull(obj);
            assertSame(obj.eResource(), resource);
            ++counter;
        }

        System.out.println("# of root-level objects: " + resource.getContents().size()); //$NON-NLS-1$
        System.out.println("Total # of objects: " + counter); //$NON-NLS-1$
        return resource;
    }

    public Resource helpCreateNewResource( final String pathInTestData ) {
        final String testdataPath = SmartTestSuite.getTestScratchPath();
        final String path = (testdataPath.endsWith("/") ? testdataPath : testdataPath + '/') + pathInTestData; //$NON-NLS-1$

        final URI resourceUri = URI.createFileURI(path);
        assertNotNull("URI not created", resourceUri); //$NON-NLS-1$

        final Resource resource = this.resourceSet.createResource(resourceUri);
        assertNotNull("Resource not created", resource); //$NON-NLS-1$
        return resource;
    }

    public void testRead_HelloServiceSimple() throws Exception {
        helpReadAndWrite("HelloServiceSimple.wsdl"); //$NON-NLS-1$

    }

    public void testRead_HelloServiceWithoutSoap() throws Exception {
        helpReadAndWrite("HelloServiceWithoutSoap.wsdl"); //$NON-NLS-1$

    }

    public void testRead_HelloService() throws Exception {
        helpReadAndWrite("HelloService.wsdl"); //$NON-NLS-1$

    }

    public void testRead_BabelFishService() throws Exception {
        helpReadAndWrite("BabelFishService.wsdl"); //$NON-NLS-1$

    }

    /**
     * Test loading a wsdl with wsdl prefixes. (Defect 21379 ) Assure that the "types" node and it's childeren (schemas) are
     * found.
     */
    public void testRead_WSDLPrefixed() throws Exception {
        WsdlResourceImpl resource = (WsdlResourceImpl)helpReadAndWrite("wsdlprefixed.wsdl"); //$NON-NLS-1$
        EList list = resource.getContents();
        assertNotNull(list);
        Object defImpl = list.get(0);
        assertNotNull(defImpl);
        assertTrue(defImpl instanceof DefinitionsImpl);
        Types types = ((DefinitionsImpl)defImpl).getTypes();
        assertNotNull(types);
        EList schemaList = types.getSchemas();
        assertNotNull(schemaList);
    }

    /**
     * Test loading a wsdl with wsdl prefixes. (Defect 21379 ) Assure that the "types" node and it's childeren (schemas) are
     * found.
     */
    public void testRead_NoWSDLPrefix() throws Exception {
        WsdlResourceImpl resource = (WsdlResourceImpl)helpReadAndWrite("no_wsdlprefix.wsdl"); //$NON-NLS-1$
        EList list = resource.getContents();
        assertNotNull(list);
        Object defImpl = list.get(0);
        assertNotNull(defImpl);
        assertTrue(defImpl instanceof DefinitionsImpl);
        Types types = ((DefinitionsImpl)defImpl).getTypes();
        assertNotNull(types);
        EList schemaList = types.getSchemas();
        assertNotNull(schemaList);
    }

    public void testRead_DayOfWeek() throws Exception {
        helpReadAndWrite("DayOfWeek.wsdl"); //$NON-NLS-1$

    }

    public void testRead_WeatherSummary() throws Exception {
        helpReadAndWrite("WeatherSummary.wsdl"); //$NON-NLS-1$

    }

    public void testRead_MsDotNetGenerated() throws Exception {
        helpReadAndWrite("MsDotNetGenerated.wsdl"); //$NON-NLS-1$

    }

    public void testRead_SalesforceEnterprise() throws Exception {
        helpReadAndWrite("Salesforce Enterprise.wsdl"); //$NON-NLS-1$

    }

    public void testWrite_SpacesInLocationUrls() throws Exception {
        // Create a new resource ...
        final Resource output = helpCreateNewResource("WsdlWithSpacesInUrls.wsdl"); //$NON-NLS-1$

        final Definitions defns = this.factory.createDefinitions();
        defns.setTargetNamespace("http://something.acme.com"); //$NON-NLS-1$
        defns.setName("MyWSDL"); //$NON-NLS-1$
        output.getContents().add(defns);

        final Types types = this.factory.createTypes();
        types.setDefinitions(defns);

        final XSDSchema schema = this.xsdFactory.createXSDSchema();
        schema.setTargetNamespace(types.getDefinitions().getTargetNamespace());

        Map qNamePrefixToNamespaceMap = schema.getQNamePrefixToNamespaceMap();
        qNamePrefixToNamespaceMap.put(schema.getSchemaForSchemaQNamePrefix(), XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        qNamePrefixToNamespaceMap.put(WsdlPackage.eNS_PREFIX, WsdlPackage.eNS_URI);
        types.getSchemas().add(schema);

        final XSDImport xsdImport = this.xsdFactory.createXSDImport();
        xsdImport.setNamespace("http://importednamespace.acme.com"); //$NON-NLS-1$
        xsdImport.setSchemaLocation("http://www.metamatrix.com/vdb/folder with space/something with % percent and space"); //$NON-NLS-1$
        schema.getContents().add(xsdImport); // always add to contents

        // Save the resource ...
        // output.save(new HashMap());
        output.save(System.out, new HashMap());
    }
}
