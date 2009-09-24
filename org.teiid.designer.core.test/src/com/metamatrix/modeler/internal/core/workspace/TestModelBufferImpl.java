/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.modeler.core.workspace.Openable;

/**
 * TestModelBufferImpl
 */
public class TestModelBufferImpl extends TestCase {

    /**
     * Constructor for TestModelBufferImpl.
     * @param name
     */
    public TestModelBufferImpl(String name) {
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
        TestSuite suite = new TestSuite("TestModelBufferImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestModelBufferImpl.class);
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

    public ModelBufferImpl helpCreateModelBufferImpl() {
        final IFile file = null;
        final Openable owner = null;
        final ResourceSet resourceSet = null;
        final boolean readOnly = true;
        final ModelBufferImpl buffer = new ModelBufferImpl(file,owner,resourceSet,readOnly);
        if ( buffer.getUnderlyingResource() != file ) {
            fail("Underlying resource didn't match constructor argument"); //$NON-NLS-1$
        }
        if ( buffer.getOwner() != owner ) {
            fail("Owner didn't match constructor argument"); //$NON-NLS-1$
        }
        if ( buffer.getEmfResourceSet() != resourceSet ) {
            fail("ResourceSet didn't match constructor argument"); //$NON-NLS-1$
        }
        if ( buffer.getEmfResource() != null ) {
            fail("Resource should be null"); //$NON-NLS-1$
        }
        if ( !buffer.isClosed() ) {
            fail("Should be closed"); //$NON-NLS-1$
        }
        if ( buffer.isReadOnly() != readOnly) {
            fail("Should " + (readOnly ? "not " : "") + "be readonly"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        if ( buffer.hasUnsavedChanges() ) {
            fail("Should not have unsaved changes"); //$NON-NLS-1$
        }
        return buffer;
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testConstructor() {
        helpCreateModelBufferImpl();
    }

    public void testOpenAndCloseCycle() {
        final ModelBufferImpl buffer = helpCreateModelBufferImpl();
        for ( int i=0; i!=3; ++i ) {
            buffer.close();
            if ( !buffer.isClosed() ) {
                fail("Should be closed"); //$NON-NLS-1$
            }
            try {
                buffer.open(null);
                fail("Should be not able to open resource with null emfResource"); //$NON-NLS-1$
            } catch (RuntimeException e) {
                //this is good continue;
            }
        }

    }

}
