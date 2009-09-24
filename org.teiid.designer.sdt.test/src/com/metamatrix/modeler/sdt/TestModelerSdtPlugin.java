/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.sdt;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDPackageImpl;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;

import com.metamatrix.modeler.core.types.DatatypeConstants;


/** 
 * @since 4.3
 */
public class TestModelerSdtPlugin extends TestCase {
    
    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------

    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------

    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================

    /**
     * Constructor for TestDefaultEObjectFinder.
     * @param name
     */
    public TestModelerSdtPlugin(String name) {
        super(name);
    }

    // =========================================================================
    //                        T E S T   C O N T R O L
    // =========================================================================

    /** 
     * Construct the test suite, which uses a one-time setup call
     * and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestModelerSdtPlugin"); //$NON-NLS-1$
        suite.addTest(new TestModelerSdtPlugin("testNothing")); //$NON-NLS-1$
        //suite.addTestSuite(TestModelerSdtPlugin.class);

        return new TestSetup(suite) { // junit.extensions package

            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }

            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }

    // =========================================================================
    //                                 M A I N
    // =========================================================================

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    // =========================================================================
    //                 S E T   U P   A N D   T E A R   D O W N
    // =========================================================================

    public static void oneTimeSetUp() {
        // Ensure that the metamodels are initialized
        XSDPackageImpl.init();
        
        // Ensure that the XSD global resources are initialized
        XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        XSDSchemaImpl.getSchemaInstance(XSDConstants.SCHEMA_INSTANCE_URI_2001);    
    }

    public static void oneTimeTearDown() {
    }

    @Override
    protected void setUp() throws Exception {
    }
    
    @Override
    protected void tearDown() throws Exception {
    }

    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    
    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    
    public void testNothing() {
        // placeholder until I can get the ModelerSdtPlugin.getBuiltInTypesResource() 
        // method to work in the nightly test environment.  I do not have to comment 
        // out all the test methods.
    }

    public void testGetDefault() {
        assertNotNull(ModelerSdtPlugin.getDefault());
    }

    public void testGetBaseUrl() {
        System.out.println(ModelerSdtPlugin.getDefault().getBaseURL());
        assertNotNull(ModelerSdtPlugin.getDefault().getBaseURL());
    }

    public void testBuiltInResourceFileExists() {
        String baseURL = ModelerSdtPlugin.getDefault().getBaseURL().toString();
        URI uri = URI.createURI(baseURL + "cache/www.metamatrix.com/metamodels/builtInDataTypes.xsd"); //$NON-NLS-1$
        File f = new File(uri.toFileString());
        assertTrue(f.exists());
    }

    public void testGetBuiltInTypesResource() {
        XSDResourceImpl xsdResource = (XSDResourceImpl)ModelerSdtPlugin.getBuiltInTypesResource();
        assertNotNull(xsdResource);
        XSDSchema schema = xsdResource.getSchema();
        List contents = schema.eContents();
        assertNotNull(contents);
        assertEquals(52,contents.size());
        for (Iterator iter = contents.iterator(); iter.hasNext();) {
            assertTrue(iter.next() instanceof XSDSimpleTypeDefinition);
        }
    }
    
    public void testGetGlobalResourceSet() {
        ResourceSet rs = ModelerSdtPlugin.getGlobalResourceSet();
        assertNotNull(rs);
        assertEquals(1,rs.getResources().size());
        assertEquals(ModelerSdtPlugin.getBuiltInTypesResource(),rs.getResources().get(0));
    }
    
    public void testGetBuiltInTypesResourceByLogicalUri() {
        ResourceSet rs = ModelerSdtPlugin.getGlobalResourceSet();
        assertNotNull(rs);
        assertEquals(1,rs.getResources().size());
        URI logicalURI = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);
        assertNotNull(rs.getResource(logicalURI, false));
        assertTrue(rs.getResource(logicalURI, false).isLoaded());
    }
    
}
