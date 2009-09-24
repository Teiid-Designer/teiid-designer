/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.relationship.NavigationContextBuilder;
import com.metamatrix.modeler.relationship.NavigationHistory;

/**
 * TestNavigationHistoryImpl
 */
public class TestNavigationHistoryImpl extends TestCase {

    private static final String URI_STR1 = "platform://project/folder A/model A#uuid1"; //$NON-NLS-1$
    private static final String URI_STR2 = "platform://project/folder A/model A#uuid2"; //$NON-NLS-1$
    private static final String URI_STR3 = "platform://project/folder A/model A#uuid3"; //$NON-NLS-1$
    private static final String URI_STR4 = "platform://project/folder A/model A#uuid4"; //$NON-NLS-1$
    private static final String URI_STR5 = "platform://project/folder A/model A#uuid5"; //$NON-NLS-1$

    private static final URI URI1 = URI.createURI(URI_STR1);
    private static final URI URI2 = URI.createURI(URI_STR2);
    private static final URI URI3 = URI.createURI(URI_STR3);
    private static final URI URI4 = URI.createURI(URI_STR4);
    private static final URI URI5 = URI.createURI(URI_STR5);

    private FakeNavigationResolver resolver;
    private NavigationContextBuilder builder;
    private NavigationContextCache cache;
    private NavigationHistory emptyHistory;
    private NavigationHistory historyNoForward;
    private NavigationHistory historyNoBack;
    private NavigationHistory history; // with forward and back

    /**
     * Constructor for TestNavigationHistoryImpl.
     * 
     * @param name
     */
    public TestNavigationHistoryImpl( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.resolver = new FakeNavigationResolver();
        this.builder = new FakeNavigationContextBuilder();
        this.cache = new NavigationContextCache(builder);
        this.cache.setResolver(this.resolver);

        this.emptyHistory = new NavigationHistoryImpl(cache);
        this.historyNoForward = new NavigationHistoryImpl(cache);
        this.historyNoBack = new NavigationHistoryImpl(cache);
        this.history = new NavigationHistoryImpl(cache);

        // Initialize the histories ...
        this.historyNoForward.navigateTo(URI1);
        this.historyNoForward.navigateTo(URI2);
        this.historyNoForward.navigateTo(URI3);
        this.historyNoForward.navigateTo(URI4);
        this.historyNoForward.navigateTo(URI5);

        this.historyNoBack.navigateTo(URI1);
        this.historyNoBack.navigateTo(URI2);
        this.historyNoBack.navigateTo(URI3);
        this.historyNoBack.navigateTo(URI4);
        this.historyNoBack.navigateTo(URI5);
        this.historyNoBack.getPrevious(); // current is URI_STR4
        this.historyNoBack.getPrevious(); // current is URI_STR3
        this.historyNoBack.getPrevious(); // current is URI_STR2
        this.historyNoBack.getPrevious(); // current is URI_STR1

        this.history.navigateTo(URI1);
        this.history.navigateTo(URI2);
        this.history.navigateTo(URI3);
        this.history.navigateTo(URI4);
        this.history.navigateTo(URI5);
        this.history.getPrevious(); // current is URI_STR4
        this.history.getPrevious(); // current is URI_STR3
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
        TestSuite suite = new TestSuite("TestNavigationHistoryImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestNavigationHistoryImpl.class);
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

    public EObject helpCreateEObject( final String URI_STR ) {
        return resolver.resolve(URI_STR);
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    // public void testDataPathSupplied() {
    // UnitTestUtil.assertTestDataPathSet();
    // }

    public void testSetup() {
        assertNotNull(this.emptyHistory);
        assertNotNull(this.historyNoForward);
        assertNotNull(this.historyNoBack);
        assertNotNull(this.history);
    }

    public void testNavigationHistoryImplWithNull() {
        try {
            new NavigationHistoryImpl(null);
            fail("Uncaught null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testNavigationHistoryImpl() {
        new NavigationHistoryImpl(cache);
    }

    public void testGetCurrent() {
        assertNull(this.emptyHistory.getCurrent());
        assertEquals(URI_STR5, this.historyNoForward.getCurrent().getInfo().getFocusNodeUri());
        assertEquals(URI_STR1, this.historyNoBack.getCurrent().getInfo().getFocusNodeUri());
        assertEquals(URI_STR3, this.history.getCurrent().getInfo().getFocusNodeUri());
    }

    public void testGetPrevious() throws Exception {
        this.emptyHistory.getPrevious(); // does nothing
        this.historyNoForward.getPrevious(); // moves to URI_STR4
        this.historyNoBack.getPrevious(); // does nothing
        this.history.getPrevious(); // moves to URI_STR2
        assertNull(this.emptyHistory.getCurrent());
        assertEquals(URI_STR4, this.historyNoForward.getCurrent().getInfo().getFocusNodeUri());
        assertEquals(URI_STR1, this.historyNoBack.getCurrent().getInfo().getFocusNodeUri());
        assertEquals(URI_STR2, this.history.getCurrent().getInfo().getFocusNodeUri());
    }

    public void testGetNext() throws Exception {
        this.emptyHistory.getNext(); // does nothing
        this.historyNoForward.getNext(); // does nothing
        this.historyNoBack.getNext(); // moves to URI_STR2
        this.history.getNext(); // moves to URI_STR4
        assertNull(this.emptyHistory.getCurrent());
        assertEquals(URI_STR5, this.historyNoForward.getCurrent().getInfo().getFocusNodeUri());
        assertEquals(URI_STR2, this.historyNoBack.getCurrent().getInfo().getFocusNodeUri());
        assertEquals(URI_STR4, this.history.getCurrent().getInfo().getFocusNodeUri());
    }

    public void testHasPrevious() {
        assertEquals(false, this.emptyHistory.hasPrevious());
        assertEquals(true, this.historyNoForward.hasPrevious());
        assertEquals(false, this.historyNoBack.hasPrevious());
        assertEquals(true, this.history.hasPrevious());
    }

    public void testHasNext() {
        assertEquals(false, this.emptyHistory.hasNext());
        assertEquals(false, this.historyNoForward.hasNext());
        assertEquals(true, this.historyNoBack.hasNext());
        assertEquals(true, this.history.hasNext());
    }

    public void testClearHistory() {
        this.emptyHistory.clearHistory();
        this.historyNoForward.clearHistory();
        this.historyNoBack.clearHistory();
        this.history.clearHistory();

        assertEquals(0, this.emptyHistory.getBackInfos().size());
        assertEquals(0, this.emptyHistory.getForwardInfos().size());
        assertEquals(0, this.historyNoForward.getBackInfos().size());
        assertEquals(0, this.historyNoForward.getForwardInfos().size());
        assertEquals(0, this.historyNoBack.getBackInfos().size());
        assertEquals(0, this.historyNoBack.getForwardInfos().size());
        assertEquals(0, this.history.getBackInfos().size());
        assertEquals(0, this.history.getForwardInfos().size());

        // Verify that the 'current' context is unaffected
        assertNull(this.emptyHistory.getCurrent());
        assertEquals(URI_STR5, this.historyNoForward.getCurrent().getInfo().getFocusNodeUri());
        assertEquals(URI_STR1, this.historyNoBack.getCurrent().getInfo().getFocusNodeUri());
        assertEquals(URI_STR3, this.history.getCurrent().getInfo().getFocusNodeUri());
    }

    // public void testGetBackInfos() {
    // // tested in testClearHistory
    // }
    //
    // public void testGetForwardInfos() {
    // // tested in testClearHistory
    // }
    //
    public void testSelectFromHistory() {
    }

    public void testNavigateToSomethingInHistory() throws Exception {
        this.history.navigateTo(URI5);
        assertEquals(3, this.history.getBackInfos().size());
        assertEquals(0, this.history.getForwardInfos().size());
        assertEquals(URI_STR5, this.history.getCurrent().getInfo().getFocusNodeUri());

        this.history.navigateTo(URI2);
        assertEquals(4, this.history.getBackInfos().size());
        assertEquals(0, this.history.getForwardInfos().size());
        assertEquals(URI_STR2, this.history.getCurrent().getInfo().getFocusNodeUri());
    }

}
