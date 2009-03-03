/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.SmartTestSuite;

/**
 * @since 4.2
 */
public class TestVdbWsdlGenerationOptionsImpl extends TestCase {

    protected static final String DATAPATH = SmartTestSuite.getTestDataPath();
    // protected static final String SCRATCHPATH = UnitTest.getTestScratchPath();

    protected static final String VDB_ARCHIVE_PATH = "/vdbEditContext.jar"; //$NON-NLS-1$

    private VdbWsdlGenerationOptionsImpl options;
    private VdbEditingContextImpl context;

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final IPath patha = new Path(DATAPATH + VDB_ARCHIVE_PATH);
        this.context = new VdbEditingContextImpl(patha);
        this.options = new VdbWsdlGenerationOptionsImpl(context);
    }

    /**
     * Constructor for TestVdbWsdlGenerationOptionsImpl.
     * 
     * @param name
     */
    public TestVdbWsdlGenerationOptionsImpl( String name ) {
        super(name);
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new SmartTestSuite("com.metamatrix.vdb.edit", "TestVdbWsdlGenerationOptionsImpl"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestVdbWsdlGenerationOptionsImpl.class);
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

    public void helpTestValidUri( final String uriString,
                                  final boolean expectedValid ) {
        final boolean valid = this.options.isValidUri(uriString);
        assertEquals(expectedValid, valid);
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testIsValidUri_ShouldBeValid() {
        helpTestValidUri("http://www.metamatrix.com", true); //$NON-NLS-1$
        helpTestValidUri("https://www.acme.com", true); //$NON-NLS-1$
        helpTestValidUri("file:/E:/some/folder/somewhere", true); //$NON-NLS-1$
        helpTestValidUri("www.metamatrix.com", true); //$NON-NLS-1$
    }

    public void testIsValidUri_ShouldBeInvalid() {
        helpTestValidUri("http://www.metamatrix. com", false); //$NON-NLS-1$
        helpTestValidUri("http://www.metamatrix.com ", false); //$NON-NLS-1$
        helpTestValidUri(" http://www.metamatrix.com", false); //$NON-NLS-1$
    }

}
