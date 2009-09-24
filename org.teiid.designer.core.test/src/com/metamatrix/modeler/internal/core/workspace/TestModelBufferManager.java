/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.modeler.core.util.UnitTestDebugSettings;
import com.metamatrix.modeler.core.workspace.FakeOpenable;
import com.metamatrix.modeler.core.workspace.FakeOpenableModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelBuffer;
import com.metamatrix.modeler.core.workspace.Openable;

public class TestModelBufferManager extends TestCase {

    static UnitTestDebugSettings DEBUG_SETTINGS = new UnitTestDebugSettings();

    private ModelBufferManager mgr;

    /**
     * Constructor for TestModelBufferManager.
     * 
     * @param name
     */
    public TestModelBufferManager( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.mgr = new ModelBufferManager(); // don't use the default so that it is clean for each test
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.mgr = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestModelBufferManager"); //$NON-NLS-1$
        suite.addTestSuite(TestModelBufferManager.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
                DEBUG_SETTINGS.acquire(); // record current settings
                // Make any changes to the settings ...
                // ModelerCore.DEBUG_MODEL_WORKSPACE = true;
            }

            @Override
            public void tearDown() {
                DEBUG_SETTINGS.reset(); // undo the changes made here
            }
        };
    }

    public int getOpenBufferSpaceLimit() {
        return this.mgr.getOpenBufferCache().getSpaceLimit();
    }

    public static List createOpenables( final int num ) {
        final List result = new ArrayList();
        for (int i = 0; i < num; i++) {
            result.add(new FakeOpenable("Some key")); //$NON-NLS-1$
        }
        return result;
    }

    public ModelBuffer helpTestCreateBuffer( final ModelBufferManager bufferMgr,
                                             final Openable openable,
                                             final boolean shouldSucceed ) throws Exception {
        // Should always be able to create a buffer, because not added to the open cache upon creation
        try {
            final ModelBuffer buffer = bufferMgr.createBuffer(openable);
            if (!shouldSucceed) {
                fail("Failed to catch illegal parameter when creating a buffer"); //$NON-NLS-1$
            }
            assertNotNull(buffer);
            return buffer;
        } catch (IllegalArgumentException e) {
            if (shouldSucceed) {
                throw e;
            }
        }
        return null; // should never get here
    }

    public void testGetDefaultBufferManager() {
        final ModelBufferManager defaultMgr = ModelBufferManager.getDefaultBufferManager();
        assertNotNull(defaultMgr);
    }

    public void testModelBufferManager() {
        final ModelBufferManager newMgr = new ModelBufferManager();
        assertNotNull(newMgr);
    }

    public void testGetOpenBufferCache() {
        assertNotNull(this.mgr.getOpenBufferCache());
    }

    public void testCreateBuffer() throws Exception {
        helpTestCreateBuffer(this.mgr, new FakeOpenableModelWorkspaceItem("Some id"), true); //$NON-NLS-1$
    }

    public void testCreateBufferWithWrongOpenable() throws Exception {
        helpTestCreateBuffer(this.mgr, new FakeOpenable("Some id"), false); //$NON-NLS-1$
    }

    public void testGetDefaultBufferFactory() {
        assertSame(this.mgr, this.mgr.getDefaultBufferFactory());
    }

    public void testAddBufferAndRemoveBuffer() throws Exception {
        final int numOpen = this.mgr.getOpenBufferCache().size();
        final FakeOpenable openable = new FakeOpenableModelWorkspaceItem("Some id"); //$NON-NLS-1$
        final ModelBuffer buffer = helpTestCreateBuffer(this.mgr, openable, true);

        this.mgr.addBuffer(buffer);
        final int numOpenAfter = this.mgr.getOpenBufferCache().size();
        assertEquals(numOpenAfter, numOpen + 1);

        this.mgr.removeBuffer(buffer);
        final int numOpenAfterRemove = this.mgr.getOpenBufferCache().size();
        assertEquals(numOpenAfterRemove, numOpen);
    }

    public void testGetOpenBuffer() throws Exception {
        final FakeOpenable openable = new FakeOpenableModelWorkspaceItem("Some id"); //$NON-NLS-1$
        final ModelBuffer buffer = helpTestCreateBuffer(this.mgr, openable, true);
        this.mgr.addBuffer(buffer);

        final FakeOpenable openable2 = new FakeOpenableModelWorkspaceItem("Some other id"); //$NON-NLS-1$
        final ModelBuffer buffer2 = helpTestCreateBuffer(this.mgr, openable2, true);
        this.mgr.addBuffer(buffer2);

        final ModelBuffer foundBuffer = this.mgr.getOpenBuffer(openable);
        assertSame(foundBuffer, buffer);

        final ModelBuffer foundBuffer2 = this.mgr.getOpenBuffer(openable2);
        assertSame(foundBuffer2, buffer2);
    }

    public void testGetOpenBufferWithNullOpenable() {
        try {
            this.mgr.getOpenBuffer(null);
            fail("Should not have succeeded"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // this is what should have happened
        }
    }

    public void testGetOpenBufferWithUnknownOpenable() {
        final FakeOpenable openableNotAdded = new FakeOpenableModelWorkspaceItem("Some id"); //$NON-NLS-1$
        final ModelBuffer foundBuffer = this.mgr.getOpenBuffer(openableNotAdded);
        assertNull(foundBuffer);
    }

    public void testGetOpenBuffers() {
        final Iterator iter = this.mgr.getOpenBuffers();
        while (iter.hasNext()) {
            fail("Should not have any open buffers upon construction"); //$NON-NLS-1$
        }
    }

    public void testCreateMoreBuffersThanSizeLimit() throws Exception {
        final int limit = getOpenBufferSpaceLimit() * 3;
        for (int i = 0; i != limit; ++i) {
            helpTestCreateBuffer(this.mgr, new FakeOpenableModelWorkspaceItem("Some id"), true); //$NON-NLS-1$
        }
    }
}
