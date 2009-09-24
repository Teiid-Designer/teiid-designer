/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.transaction;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.resource.EmfResourceSetImpl;

/**
 * @author Lance Phillips
 * @since 3.1
 */
public class TestUnitOfWorkProviderImpl extends TestCase {

    // ###################################################################################
    // # Main
    // ###################################################################################
    /**
     * @since 3.1
     */
    public static void main( final String[] arguments ) {
        TestRunner.run(suite());
    }

    // ###################################################################################
    // # Test Suite
    // ###################################################################################
    /**
     * @since 3.1
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();
        suite.addTestSuite(TestUnitOfWorkProviderImpl.class);
        return suite;
    }

    // ###################################################################################
    // # Constructors
    // ###################################################################################
    /**
     * Constructor for TestUnitOfWorkProviderImpl.
     * 
     * @param name
     */
    public TestUnitOfWorkProviderImpl( String name ) {
        super(name);
    }

    /**
     * Constructor for TestUnitOfWorkProviderImpl.
     * 
     * @param name
     */
    public TestUnitOfWorkProviderImpl() {
        this("TestUnitOfWorkProviderImpl"); //$NON-NLS-1$
    }

    // ###################################################################################
    // # Helper Methods
    // ###################################################################################

    private UnitOfWorkProviderImpl helpCreateTransactionProvider( ResourceSet resources ) {
        return new UnitOfWorkProviderImpl(resources);
    }

    // ###################################################################################
    // # Actual Tests
    // ###################################################################################

    /**
     * Ensure that SimpleEmfUnitOfWorkProvider can't be created with NULL resource set.
     */
    public void testCreationArgs() {
        // Verify that SimpleEmfUnitOfWorkProvider can not be created with NULL resource set
        try {
            helpCreateTransactionProvider(null);
        } catch (AssertionError e) {
            // This is good. PASS
            return;
        }

        fail("Should not be able to create SimpleEmfUnitOfWorkProvider with NULL resource set"); //$NON-NLS-1$
    }

    /**
     * Verify that getCurrent does not return null for new SimpleEmfUnitOfWorkProvider
     */
    public void testGetCurrent() {
        // Verify that SimpleEmfUnitOfWorkProvider can not be created with NULL resource set
        ContainerImpl cntr = new ContainerImpl();
        cntr.start();
        UnitOfWorkProviderImpl stp = helpCreateTransactionProvider(new EmfResourceSetImpl(cntr));
        if (stp.getCurrent() == null) {
            fail("GetCurrent shoud not return null"); //$NON-NLS-1$
        }
        stp.cleanup(Thread.currentThread());

        cntr.shutdown();
    }

}
