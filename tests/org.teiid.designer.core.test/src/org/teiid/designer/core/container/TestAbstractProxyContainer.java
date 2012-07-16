/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.container;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.teiid.designer.core.ModelerCoreException;

/**
 * @since 3.1
 */
public class TestAbstractProxyContainer extends TestCase {
	//############################################################################################################################
	//# Main                                                                                                                     #
	//############################################################################################################################

	/**
	 * @since 3.1
	 */
	public static void main(final String[] arguments) {
		TestRunner.run(suite());
	}

	//############################################################################################################################
	//# Static Methods                                                                                                           #
	//############################################################################################################################

	/**
	 * @since 3.1
	 */
	public static Test suite() {
		return new TestSuite(TestAbstractProxyContainer.class);
	}

    //############################################################################################################################
	//# Variables                                                                                                                #
	//############################################################################################################################
    
    private Container ctnr;

	//############################################################################################################################
	//# Constructors                                                                                                             #
	//############################################################################################################################

	/**
	 * @since 3.1
	 */
	public TestAbstractProxyContainer(final String testMethodName) {
		super(testMethodName);
	}

	//############################################################################################################################
	//# Methods                                                                                                                  #
	//############################################################################################################################

    /**
	 * @since 3.1
	 */
    @Override
    public void setUp() {
        this.ctnr = new FakeContainer();
    }

    /**
     * @since 3.1
     */
    public void testGetEditingDomain() {
        try {
            assertNotNull(((ContainerImpl)this.ctnr).getEditingDomain());
            fail("Expected ModelerCoreRuntimeException"); //$NON-NLS-1$
        } catch (RuntimeException expected) {
        }
    }

    /**
     * @since 3.1
     */
    public void testGetEditingDomain2() {
        try {
            this.ctnr.start();
        } catch (ModelerCoreException e) {
            fail(e.getMessage());
        }
        assertNotNull(((ContainerImpl)this.ctnr).getEditingDomain());
    }
    /**
     * @since 3.1
     */
    public void testSetEditingDomain() {
        try {
            ((ContainerImpl)this.ctnr).setEditingDomain(null);
            fail("Expected IllegalArgumentException"); //$NON-NLS-1$
        } catch (final IllegalArgumentException expected) { 
        }
    }
    
    
}
