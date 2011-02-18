/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestFakeMappableObject
 */
public class TestFakeMappableObject extends TestCase {

    /**
     * Constructor for TestFakeMappableObject.
     * @param name
     */
    public TestFakeMappableObject(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
        TestSuite suite = new TestSuite("TestFakeMappableObject"); //$NON-NLS-1$
        suite.addTestSuite(TestFakeMappableObject.class);
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
    //                      H E L P E R   M E T H O D S
    // =========================================================================

    public void helpTestCreateFakeMappableTree( final String namePrefix, final int startingType, 
                                                final int endingType, final int numTypeInc, final int depth ) {
        final List roots = new LinkedList();
        FakeMappableObject.createFakeMappableTree(roots,namePrefix,startingType,endingType,numTypeInc,depth);
        System.out.println("Tree with namePrefix="+namePrefix+"; startingType="+startingType+"; endingType="+endingType+"; numTypeInc="+numTypeInc+"; depth="+depth);   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$
        final Iterator iter = roots.iterator();
        while (iter.hasNext()) {
            final FakeMappableObject obj = (FakeMappableObject)iter.next();
            obj.print(System.out," "); //$NON-NLS-1$
        }
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

//    public void testDataPathSupplied() {
//        UnitTestUtil.assertTestDataPathSet();
//    }

    public void testCreateFakeMappableTree1() {
        final String namePrefix = "Object"; //$NON-NLS-1$
        final int startingType = 0;
        final int endingType = 3;
        final int numTypeInc = 0;
        final int depth = 1;
        helpTestCreateFakeMappableTree(namePrefix,startingType,endingType,numTypeInc,depth);
    }

    public void testCreateFakeMappableTree2() {
        final String namePrefix = "Object"; //$NON-NLS-1$
        final int startingType = 1;
        final int endingType = 4;
        final int numTypeInc = 0;
        final int depth = 3;
        helpTestCreateFakeMappableTree(namePrefix,startingType,endingType,numTypeInc,depth);
    }

    public void testCreateFakeMappableTree3() {
        final String namePrefix = "Object"; //$NON-NLS-1$
        final int startingType = 1;
        final int endingType = 4;
        final int numTypeInc = 3;
        final int depth = 3;
        helpTestCreateFakeMappableTree(namePrefix,startingType,endingType,numTypeInc,depth);
    }

}
