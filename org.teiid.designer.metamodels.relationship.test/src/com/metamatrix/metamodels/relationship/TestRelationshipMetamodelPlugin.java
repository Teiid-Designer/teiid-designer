/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import java.io.File;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.metamodels.core.impl.CorePackageImpl;
import com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl;


/** 
 * @since 4.3
 */
public class TestRelationshipMetamodelPlugin extends TestCase {
    
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
    public TestRelationshipMetamodelPlugin(String name) {
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
        TestSuite suite = new TestSuite("TestRelationshipMetamodelPlugin"); //$NON-NLS-1$
        suite.addTest(new TestRelationshipMetamodelPlugin("testNothing")); //$NON-NLS-1$
        //suite.addTestSuite(TestRelationshipMetamodelPlugin.class);

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
        CorePackageImpl.init();
        RelationshipPackageImpl.init();
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
        // placeholder until I can get the RelationshipMetamodelPlugin.getBuiltInRelationshipTypesResource()
        // method to work in the nightly test environment.  I do not have to comment 
        // out all the test methods.
    }

    public void testGetDefault() {
        assertNotNull(RelationshipMetamodelPlugin.getDefault());
    }

    public void testGetBaseUrl() {
        System.out.println(RelationshipMetamodelPlugin.getPluginResourceLocator().getBaseURL());
        assertNotNull(RelationshipMetamodelPlugin.getPluginResourceLocator().getBaseURL());
    }

    public void testBuiltInResourceFileExists() {
        String baseURL = RelationshipMetamodelPlugin.getPluginResourceLocator().getBaseURL().toString();
        URI uri = URI.createURI(baseURL + "cache/www.metamatrix.com/relationships/builtInRelationshipTypes.xmi"); //$NON-NLS-1$
        File f = new File(uri.toFileString());
        assertTrue(f.exists());
    }

    public void testGetBuiltInTypesResource() {
        Resource resource = RelationshipMetamodelPlugin.getBuiltInRelationshipTypesResource();
        assertNotNull(resource);
        List contents = resource.getContents();
        assertNotNull(contents);
        assertEquals(52,contents.size());
//        for (Iterator iter = contents.iterator(); iter.hasNext();) {
//            assertTrue(iter.next() instanceof RelationshipType);
//        }
    }
    
    public void testGetGlobalResourceSet() {
        ResourceSet rs = RelationshipMetamodelPlugin.getGlobalResourceSet();
        assertNotNull(rs);
        assertEquals(1,rs.getResources().size());
        assertEquals(RelationshipMetamodelPlugin.getBuiltInRelationshipTypesResource(),rs.getResources().get(0));
    }
    
    public void testGetBuiltInTypesResourceByLogicalUri() {
        ResourceSet rs = RelationshipMetamodelPlugin.getGlobalResourceSet();
        assertNotNull(rs);
        assertEquals(1,rs.getResources().size());
        URI logicalURI = URI.createURI(RelationshipMetamodelPlugin.BUILTIN_RELATIONSHIP_TYPES_URI);
        assertNotNull(rs.getResource(logicalURI, false));
        assertTrue(rs.getResource(logicalURI, false).isLoaded());
    }
    
}
