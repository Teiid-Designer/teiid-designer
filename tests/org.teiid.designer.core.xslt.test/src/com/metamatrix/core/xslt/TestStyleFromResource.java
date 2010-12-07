/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.xslt;

import java.io.File;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.teiid.core.TeiidException;
import com.metamatrix.core.util.SmartTestSuite;

/**
 * TestStyleFromResource
 */
public class TestStyleFromResource extends TestCase {

    private static final String NAME_STYLE1 = "StyleWithoutDescription"; //$NON-NLS-1$
    private static final String PATH_STYLE1 = "passthrough.xsl"; //$NON-NLS-1$

    private static final String NAME_STYLE2 = "StyleWithDescription"; //$NON-NLS-1$
    private static final String PATH_STYLE2 = "folder1/passthrough2.xsl"; //$NON-NLS-1$

    private static final String NAME_STYLE3 = "StyleWithBadPath"; //$NON-NLS-1$
    private static final String PATH_STYLE3 = "folder444/passthrough2.xsl"; //$NON-NLS-1$

    private static final String NAME_STYLE4 = "StyleWithUnreadableResource"; //$NON-NLS-1$
    private static final String PATH_STYLE4 = "folder1/unreadableResource.xsl"; //$NON-NLS-1$

    private ClassLoader loader;

    private Style styleWithoutDescription;
    private Style styleWithDescription;
    private Style styleWithBadPath;
    private Style styleWithUnreadableResource;

    /**
     * Constructor for TestStyleFromResource.
     * 
     * @param name
     */
    public TestStyleFromResource(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Create the class loader ...
		final File testDataFolder = new File(SmartTestSuite.getTestDataPath());

        this.styleWithoutDescription = new StyleFromResource(new File(testDataFolder, PATH_STYLE1).toURI().toURL(), NAME_STYLE1);
        this.styleWithDescription = new StyleFromResource(new File(testDataFolder, PATH_STYLE2).toURI().toURL(), NAME_STYLE2,
		                                                  "Description"); //$NON-NLS-1$
        this.styleWithBadPath = new StyleFromResource(new File(testDataFolder, PATH_STYLE3).toURI().toURL(), NAME_STYLE3);
        this.styleWithUnreadableResource = new StyleFromResource(new File(testDataFolder, PATH_STYLE4).toURI().toURL(),
                                                                 NAME_STYLE4);
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
        TestSuite suite = new TestSuite("TestStyleFromResource"); //$NON-NLS-1$
        suite.addTestSuite(TestStyleFromResource.class);
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

    public void helpTestConstructor( final ClassLoader loader,
                                     final String name,
                                     final String path,
                                     final String desc,
                                     final boolean shouldSucceed ) {
        // Try with description ...
        try {
			//            new StyleFromResource(loader,name,path,desc);
            if ( !shouldSucceed ) {
                fail("Failed to catch fault condition"); //$NON-NLS-1$
            }
        } catch (IllegalArgumentException e) {
            if ( shouldSucceed ) {
                throw e;
            }
        }

        // Try withOUT description ...
        try {
			//            new StyleFromResource(loader,name,path);
            if ( !shouldSucceed ) {
                fail("Failed to catch fault condition"); //$NON-NLS-1$
            }
        } catch (IllegalArgumentException e) {
            if ( shouldSucceed ) {
                throw e;
            }
        }
    }

    public void testConstructorWithNullLoader() {
        helpTestConstructor(null,"some","path","desc",true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void testConstructorWithNullName() {
        helpTestConstructor(this.loader,null,"path","desc",true); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstructorWithNullPath() {
        helpTestConstructor(this.loader,"some",null,"desc",true); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstructorWithZeroLengthName() {
        helpTestConstructor(this.loader,"","path","desc",true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void testConstructorWithZeroLengthPath() {
        helpTestConstructor(this.loader,"some","","desc",true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void testGetName1() {
        final String name = this.styleWithoutDescription.getName();
        assertEquals(NAME_STYLE1, name);
    }

    public void testGetName2() {
        final String name = this.styleWithDescription.getName();
        assertEquals(NAME_STYLE2, name);
    }

    public void testGetDescription1() {
        final String description = this.styleWithoutDescription.getDescription();
        assertNotNull(description);
		assertSame(0, description.length());
    }

    public void testGetDescription2() {
        final String description = this.styleWithDescription.getDescription();
        assertNotNull(description);
		assertNotSame(0, description.length());
    }

    public void testStreamSource1() {
        try {
            final StreamSource ssource = this.styleWithoutDescription.getStreamSource();
            assertNotNull(ssource);
        } catch (TeiidException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }

    public void testStreamSource2() {
        try {
            final StreamSource ssource = this.styleWithDescription.getStreamSource();
            assertNotNull(ssource);
        } catch (TeiidException e) {
        	throw new RuntimeException(e);
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }

    public void testStreamSource3() {
        try {
            final StreamSource ssource = this.styleWithBadPath.getStreamSource();
            fail("Should not have been able to get the StreamSource for a resource that doesn't exist"); //$NON-NLS-1$
            assertNotNull(ssource);
        } catch (TeiidException e) {
        	throw new RuntimeException(e);
        } catch (IOException e) {
            // Expected !!!
        }
    }

    public void testStreamSource4() {
        try {
            final StreamSource ssource = this.styleWithUnreadableResource.getStreamSource();
            fail("Should not have been able to get the StreamSource for an unreadable resource"); //$NON-NLS-1$
            assertNotNull(ssource);
        } catch (TeiidException e) {
            // Expected !!!
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }


}
