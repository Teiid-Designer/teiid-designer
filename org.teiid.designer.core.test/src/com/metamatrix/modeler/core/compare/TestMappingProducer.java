/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.impl.MappingFactoryImpl;

/**
 * TestCompositeMappingAdapter
 */
public class TestMappingProducer extends TestCase {

    private MappingProducer adapter;
    private List inputs;
    private List outputsMatchingInputs;
    private List outputsWithExtras;

    // private MappingFactory factory;
    // private IProgressMonitor monitor = new NullProgressMonitor();

    /**
     * Constructor for TestCompositeMappingAdapter.
     * 
     * @param name
     */
    public TestMappingProducer( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // this.factory =
        new MappingFactoryImpl();

        // Create the composite adapter ...
        this.adapter = new MappingProducer();

        // Add the adapters ...
        this.adapter.getEObjectMatcherCache().getEObjectMatcherFactories().add(new FakeEObjectMatcherFactory());

        // Create the inputs and outputs ...
        this.inputs = new ArrayList();
        this.outputsMatchingInputs = new ArrayList();
        this.outputsWithExtras = new ArrayList();
        final String namePrefix = "Object"; //$NON-NLS-1$
        FakeMappableObject.createFakeMappableTree(inputs, namePrefix, 1, 4, 0, 3);
        FakeMappableObject.createFakeMappableTree(outputsMatchingInputs, namePrefix, 1, 4, 0, 3);
        FakeMappableObject.createFakeMappableTree(outputsWithExtras, namePrefix, 0, 5, 1, 3);
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
        TestSuite suite = new TestSuite("TestCompositeMappingAdapter"); //$NON-NLS-1$
        suite.addTestSuite(TestMappingProducer.class);
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

    public void helpPrintMapping( final Mapping mapping,
                                  final String prefix ) {
        final PrintStream stream = System.out;
        stream.println(prefix + mapping.toString());
        final Iterator iter = mapping.getNested().iterator();
        while (iter.hasNext()) {
            final Mapping nested = (Mapping)iter.next();
            helpPrintMapping(nested, "  " + prefix); //$NON-NLS-1$
        }
    }

    /*
     * Test for void CompositeMappingAdapter()
     */
    public void testMappingProducer() {
        final MappingProducer obj = new MappingProducer();
        assertNotNull(obj);
    }

}
