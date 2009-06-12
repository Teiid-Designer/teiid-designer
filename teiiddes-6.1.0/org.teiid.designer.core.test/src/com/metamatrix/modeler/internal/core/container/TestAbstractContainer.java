/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.EObjectFinder;
import com.metamatrix.modeler.core.container.ResourceFinder;

/**
 * @since 3.1
 */
public class TestAbstractContainer extends TestCase {
    // ############################################################################################################################
    // # Main #
    // ############################################################################################################################

    /**
     * @since 3.1
     */
    public static void main( final String[] arguments ) {
        TestRunner.run(suite());
    }

    // ############################################################################################################################
    // # Static Methods #
    // ############################################################################################################################

    /**
     * Test suite, with one-time setup.
     * 
     * @since 3.1
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite("TestEmfContainer"); //$NON-NLS-1$
        suite.addTestSuite(TestAbstractContainer.class);
        // One-time setup and teardown
        return new TestSetup(suite);
    }

    // ############################################################################################################################
    // # Variables #
    // ############################################################################################################################

    private ContainerImpl unnamedUnstartedContainer, unnamedStartedContainer;
    private ContainerImpl unstartedContainer;
    private ContainerImpl startedContainer;
    private ContainerImpl stoppedContainer;

    // ############################################################################################################################
    // # Constructors #
    // ############################################################################################################################

    /**
     * Constructor for TestEmfContainer.
     * 
     * @since 3.1
     */
    public TestAbstractContainer( final String testMethodName ) {
        super(testMethodName);
    }

    // ############################################################################################################################
    // # Methods #
    // ############################################################################################################################

    /**
     * @since 3.1
     */
    private ContainerImpl createFakeContainer( final String name,
                                               final int desiredState ) throws Exception {
        final ContainerImpl container = new FakeContainer();
        if (name != null) {
            container.setName(name);
        }
        if (desiredState == ContainerImpl.STARTED || desiredState == ContainerImpl.STOPPED) {
            container.start();
        }
        if (desiredState == ContainerImpl.STOPPED) {
            container.shutdown();
        }
        return container;
    }

    /**
     * @since 3.1
     */
    public void helpTestGetState( final ContainerImpl instance,
                                  final int expectedState ) {
        final int actualState = instance.getState();
        if (actualState != expectedState) {
            fail("The actual state (" + actualState + ") doesn't match the expected state (" + expectedState + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * @since 3.1
     */
    public void helpTestRegistryEntry( final ContainerImpl instance,
                                       final String name ) {
        // Look up in the registry the object with the specified name ...
        final Object namedObject = ModelerCore.getRegistry().lookup(name);
        if (instance != namedObject) {
            fail("The named object (\"" + namedObject + "\") doesn't match the supplied instance (\"" + instance + "\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * @since 3.1
     */
    public String helpTestSetName( final Container container,
                                   final String newName,
                                   final boolean shouldSucceed ) throws Exception {
        try {
            // Set the name
            container.setName(newName);
            final String actualName = container.getName();
            if (!shouldSucceed) {
                fail("Should have failed setting the name to \"" + newName + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (newName == null) {
                if (actualName != null) {
                    fail("Failed to set the name to null"); //$NON-NLS-1$
                }
            } else if (actualName == null || !newName.equals(actualName)) {
                fail("Unable to set the name to \"" + newName + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }

            return actualName;
        } catch (Exception e) {
            if (shouldSucceed) {
                throw e;
            }
        }
        return null;
    }

    /**
     * @see TestCase#setUp()
     * @since 3.1
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.unnamedUnstartedContainer = createFakeContainer(null, ContainerImpl.UNSTARTED);
        this.unnamedStartedContainer = createFakeContainer(null, ContainerImpl.STARTED);
        this.unstartedContainer = createFakeContainer("UnstartedContainer", ContainerImpl.UNSTARTED); //$NON-NLS-1$
        this.startedContainer = createFakeContainer("StartedContainer", ContainerImpl.STARTED); //$NON-NLS-1$
        this.stoppedContainer = createFakeContainer("StoppedContainer", ContainerImpl.STOPPED); //$NON-NLS-1$
    }

    /**
     * @since 3.1
     */
    private ContainerImpl shutdownContainer( final ContainerImpl container ) throws Exception {
        if (container != null && container.getState() == ContainerImpl.STARTED) {
            container.shutdown();
            return null;
        }
        return container;
    }

    /**
     * @see TestCase#tearDown()
     * @since 3.1
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.unstartedContainer = shutdownContainer(this.unstartedContainer);
        this.startedContainer = shutdownContainer(this.startedContainer);
        this.startedContainer = shutdownContainer(this.startedContainer);
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    /**
     * @since 3.1
     */
    public void testUnstartedState() {
        helpTestGetState(this.unstartedContainer, ContainerImpl.UNSTARTED);
    }

    /**
     * @since 3.1
     */
    public void testStartedState() {
        helpTestGetState(this.startedContainer, ContainerImpl.STARTED);
    }

    /**
     * @since 3.1
     */
    public void testStoppedState() {
        helpTestGetState(this.stoppedContainer, ContainerImpl.STOPPED);
    }

    /**
     * @since 3.1
     */
    public void testStateTransitionToStarted() {
        this.unstartedContainer.start();
        helpTestGetState(this.unstartedContainer, ContainerImpl.STARTED);
    }

    /**
     * @since 3.1
     */
    public void testStateTransitionToStopped() {
        this.startedContainer.shutdown();
        helpTestGetState(this.startedContainer, ContainerImpl.STOPPED);
    }

    /**
     * @since 3.1
     */
    public void testInvalidStateTransitionToStopped() {
        try {
            this.unstartedContainer.shutdown();
            fail("Failed to throw an exception for invalid state transition"); //$NON-NLS-1$
        } catch (final IllegalStateException expected) {
        }
    }

    /**
     * @since 3.1
     */
    public void testUnchangedStateTransitionFromStarted() {
        this.startedContainer.start();
        helpTestGetState(this.startedContainer, ContainerImpl.STARTED);
    }

    /**
     * @since 3.1
     */
    public void testUnchangedStateTransitionFromStopped() {
        this.stoppedContainer.shutdown();
        helpTestGetState(this.stoppedContainer, ContainerImpl.STOPPED);
    }

    /**
     * @since 3.1
     */
    public void testGetName() {
        assertNull(this.unnamedUnstartedContainer.getName());
        assertNotNull(this.unstartedContainer.getName());
    }

    public void testSetNullName() throws Exception {
        // Can only set the name on unstartedContainers ...
        helpTestSetName(this.unstartedContainer, null, true);
        helpTestRegistryEntry(this.unstartedContainer, null);
    }

    public void testSetValidName() throws Exception {
        // Can only set the name on unstartedContainers ...
        final String name = "NewName"; //$NON-NLS-1$
        helpTestSetName(this.unstartedContainer, name, true);
        helpTestRegistryEntry(this.unstartedContainer, name);
    }

    public void testSetValidMultiWordName() throws Exception {
        // Can only set the name on unstartedContainers ...
        final String name = "New Name"; //$NON-NLS-1$
        helpTestSetName(this.unstartedContainer, name, true);
        helpTestRegistryEntry(this.unstartedContainer, name);
    }

    /**
     * @since 3.1
     */
    public void testSetNameWhileUnsetAndRunning() {
        this.unnamedStartedContainer.setName("New Name"); //$NON-NLS-1$
        assertSame("New Name", this.unnamedStartedContainer.getName()); //$NON-NLS-1$
    }

    /**
     * @since 3.1
     */
    public void testSetNameWhileSetAndRunning() {
        try {
            this.startedContainer.setName("New Name"); //$NON-NLS-1$
            fail("Expected IllegalStateException"); //$NON-NLS-1$
        } catch (final IllegalStateException expected) {
        }
    }

    /**
     * @since 3.1
     */
    public void testGetFinder() {
        assertNotNull(this.unstartedContainer.getEObjectFinder());
    }

    /**
     * @since 3.1
     */
    public void testSetFinder() {
        final EObjectFinder finder = new FakeFinder();
        this.unstartedContainer.setEObjectFinder(finder);
        assertSame(finder, this.unstartedContainer.getEObjectFinder());
    }

    /**
     * @since 3.1
     */
    public void testSetFinderWhileUnsetAndRunning() {
        final EObjectFinder finder = new FakeFinder();
        this.unnamedStartedContainer.setEObjectFinder(finder);
        assertSame(finder, this.unnamedStartedContainer.getEObjectFinder());
    }

    /**
     * @since 3.1
     */
    public void testSetFinderWhileSetAndRunning() throws Exception {
        try {
            this.unnamedUnstartedContainer.getEObjectFinder(); // Causes default to be lazily created
            this.unnamedUnstartedContainer.start();
            this.unnamedUnstartedContainer.setEObjectFinder(new FakeFinder());
            fail("Expected IllegalStateException"); //$NON-NLS-1$
        } catch (final IllegalStateException expected) {
        }
    }

    /**
     * @since 4.3
     */
    public void testSetResourceFinder() {
        final ResourceFinder finder = new FakeResourceFinder();
        this.unstartedContainer.setResourceFinder(finder);
        assertSame(finder, this.unstartedContainer.getResourceFinder());
    }

    /**
     * @since 4.3
     */
    public void testSetResourceFinderWhileUnsetAndRunning() {
        final ResourceFinder finder = new FakeResourceFinder();
        this.unnamedStartedContainer.setResourceFinder(finder);
        assertSame(finder, this.unnamedStartedContainer.getResourceFinder());
    }

    /**
     * @since 4.3
     */
    public void testSetResourceFinderWhileSetAndRunning() throws Exception {
        try {
            this.unnamedUnstartedContainer.getResourceFinder(); // Causes default to be lazily created
            this.unnamedUnstartedContainer.start();
            this.unnamedUnstartedContainer.setResourceFinder(new FakeResourceFinder());
            fail("Expected IllegalStateException"); //$NON-NLS-1$
        } catch (final IllegalStateException expected) {
        }
    }

    /**
     * @since 3.1
     */
    public void testGetChangeNotifier() {
        assertNotNull(this.unstartedContainer.getChangeNotifier());
    }

    /**
     * @since 3.1
     */
    public void testSetChangeNotifier() {
        final ChangeNotifier notifier = new ChangeNotifier();
        this.unstartedContainer.setChangeNotifier(notifier);
        assertSame(notifier, this.unstartedContainer.getChangeNotifier());
    }

    /**
     * @since 3.1
     */
    public void testSetEventBrokerWhileUnsetAndRunning() {
        final ChangeNotifier notifier = new ChangeNotifier();

        try {
            this.unnamedStartedContainer.setChangeNotifier(notifier);
        } catch (IllegalStateException theException) {
            fail("IllegalStateException not Expected as the running container previously did not have a change notifier on it."); //$NON-NLS-1$
        }
    }

    /**
     * @since 3.1
     */
    public void testSetEventBrokerWhileSetAndRunning1() throws Exception {
        try {
            this.unnamedStartedContainer.getChangeNotifier(); // Causes default to be lazily created
            this.unnamedStartedContainer.setChangeNotifier(new ChangeNotifier());
            fail("Expected IllegalStateException"); //$NON-NLS-1$
        } catch (final IllegalStateException expected) {
        }
    }

    /**
     * @since 4.3
     */
    public void testSetEventBrokerWhileSetAndRunning2() throws Exception {
        try {
            this.unnamedUnstartedContainer.getChangeNotifier(); // Causes default to be lazily created
            this.unnamedUnstartedContainer.start();
            this.unnamedUnstartedContainer.setChangeNotifier(new ChangeNotifier());
            fail("Expected IllegalStateException"); //$NON-NLS-1$
        } catch (final IllegalStateException expected) {
        }
    }
}
