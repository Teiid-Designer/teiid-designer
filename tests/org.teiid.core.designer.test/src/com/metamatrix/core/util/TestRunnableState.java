/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import junit.framework.TestCase;

/**
 */
public class TestRunnableState extends TestCase {

    private RunnableState unstarted;
    private RunnableState starting;
    private RunnableState started;
    private RunnableState stopping;
    private RunnableState stopped;
    private RunnableState failed;

    /**
     * Constructor for TestRunnableState.
     * 
     * @param name
     */
    public TestRunnableState( String name ) {
        super(name);
    }

    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        unstarted = new RunnableState();

        starting = new RunnableState();
        starting.setState(RunnableState.STARTING);

        started = new RunnableState();
        started.setState(RunnableState.STARTING);
        started.setState(RunnableState.STARTED);

        stopping = new RunnableState();
        stopping.setState(RunnableState.STARTING);
        stopping.setState(RunnableState.STARTED);
        stopping.setState(RunnableState.STOPPING);

        stopped = new RunnableState();
        stopped.setState(RunnableState.STARTING);
        stopped.setState(RunnableState.STARTED);
        stopped.setState(RunnableState.STOPPING);
        stopped.setState(RunnableState.STOPPED);

        failed = new RunnableState();
        failed.setState(RunnableState.STARTING);
        failed.setState(RunnableState.STARTED);
        failed.setState(RunnableState.FAILED);
    }

    /**
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        unstarted = null;
        starting = null;
        started = null;
        stopping = null;
        stopped = null;
        failed = null;
    }

    public void helpCheckLiteral( final int state,
                                  final String expectedLiteral,
                                  final boolean stateShouldBeValid ) {
        try {
            final String actualLiteral = RunnableState.getLiteralForState(state);
            // Check whether the supplied state should have thrown exception (because it didn't)
            if (!stateShouldBeValid) {
                fail("The state " + state + " should not be valid but was not caught"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            // At this point, the supplied state should be an expected value, so the literal
            // should match ...
            if (actualLiteral == null) {
                fail("The literal for any state may not be null"); //$NON-NLS-1$
            }
            if (actualLiteral != expectedLiteral || !actualLiteral.equals(expectedLiteral)) {
                fail("The actual literal \"" + actualLiteral + "\" did not match " + //$NON-NLS-1$ //$NON-NLS-2$
                     "expected literal \"" + expectedLiteral + "\" for state=" + state); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (IllegalArgumentException e) {
            if (stateShouldBeValid) {
                throw e;
            }
            // Otherwise, not valid as expected
        }
    }

    public void helpCheckState( final RunnableState rs,
                                final int expectedState ) {
        if (rs.getState() != expectedState) {
            fail("The state was expected to be " + printState(expectedState) + //$NON-NLS-1$
                 "but was " + printState(rs.getState())); //$NON-NLS-1$
        }
    }

    protected String printState( final int state ) {
        return "" + state + " (" + RunnableState.getLiteralForState(state) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void helpCheckStateTransition( final RunnableState rs,
                                          final int newState,
                                          final boolean newStateIsValid ) {
        final int oldState = rs.getState();
        try {
            rs.setState(newState);
            if (!newStateIsValid) {
                fail("Unexpectedly allowed to transition from " + printState(oldState) + //$NON-NLS-1$
                     " to " + printState(newState)); //$NON-NLS-1$
            }

            // Check whether the state matches ...
            helpCheckState(rs, newState);
        } catch (IllegalStateException e) {
            if (newStateIsValid) {
                fail("Unexpectedly failed to transition from " + printState(oldState) + //$NON-NLS-1$
                     " to " + printState(newState)); //$NON-NLS-1$
            }
        }
    }

    public void testValidGetLiteralForState1() {
        helpCheckLiteral(RunnableState.UNSTARTED, RunnableState.UNSTARTED_LITERAL, true);
    }

    public void testValidGetLiteralForState2() {
        helpCheckLiteral(RunnableState.STARTING, RunnableState.STARTING_LITERAL, true);
    }

    public void testValidGetLiteralForState3() {
        helpCheckLiteral(RunnableState.STARTED, RunnableState.STARTED_LITERAL, true);
    }

    public void testValidGetLiteralForState4() {
        helpCheckLiteral(RunnableState.STOPPING, RunnableState.STOPPING_LITERAL, true);
    }

    public void testValidGetLiteralForState5() {
        helpCheckLiteral(RunnableState.STOPPED, RunnableState.STOPPED_LITERAL, true);
    }

    public void testValidGetLiteralForState6() {
        helpCheckLiteral(RunnableState.FAILED, RunnableState.FAILED_LITERAL, true);
    }

    public void testInvalidGetLiteralForState1() {
        helpCheckLiteral(-1, null, false);
    }

    public void testInvalidGetLiteralForState2() {
        helpCheckLiteral(10, null, false);
    }

    public void testInvalidGetLiteralForState3() {
        helpCheckLiteral(61, null, false);
    }

    public void testInvalidGetLiteralForState4() {
        helpCheckLiteral(244, null, false);
    }

    public void testConstructor() {
        final RunnableState uow = new RunnableState();
        // Check the state
        helpCheckState(uow, RunnableState.UNSTARTED);
    }

    // Test from UNSTARTED
    public void testStateTransitionFromUnstartedToUnstarted() {
        helpCheckStateTransition(unstarted, RunnableState.UNSTARTED, true);
    }

    public void testStateTransitionFromUnstartedToStarting() {
        helpCheckStateTransition(unstarted, RunnableState.STARTING, true);
    }

    public void testStateTransitionFromUnstartedToStarted() {
        helpCheckStateTransition(unstarted, RunnableState.STARTED, false);
    }

    public void testStateTransitionFromUnstartedToStopping() {
        helpCheckStateTransition(unstarted, RunnableState.STOPPING, false);
    }

    public void testStateTransitionFromUnstartedToStopped() {
        helpCheckStateTransition(unstarted, RunnableState.STOPPED, false);
    }

    public void testStateTransitionFromUnstartedToFailed() {
        helpCheckStateTransition(unstarted, RunnableState.FAILED, false);
    }

    // Test from STARTING
    public void testStateTransitionFromStartingToUnstarted() {
        helpCheckStateTransition(starting, RunnableState.UNSTARTED, false);
    }

    public void testStateTransitionFromStartingToStarting() {
        helpCheckStateTransition(starting, RunnableState.STARTING, true);
    }

    public void testStateTransitionFromStartingToStarted() {
        helpCheckStateTransition(starting, RunnableState.STARTED, true);
    }

    public void testStateTransitionFromStartingToStopping() {
        helpCheckStateTransition(starting, RunnableState.STOPPING, false);
    }

    public void testStateTransitionFromStartingToStopped() {
        helpCheckStateTransition(starting, RunnableState.STOPPED, false);
    }

    public void testStateTransitionFromStartingToFailed() {
        helpCheckStateTransition(starting, RunnableState.FAILED, true);
    }

    // Test from STARTED
    public void testStateTransitionFromStartedToUnstarted() {
        helpCheckStateTransition(started, RunnableState.UNSTARTED, false);
    }

    public void testStateTransitionFromStartedToStarting() {
        helpCheckStateTransition(started, RunnableState.STARTING, false);
    }

    public void testStateTransitionFromStartedToStarted() {
        helpCheckStateTransition(started, RunnableState.STARTED, true);
    }

    public void testStateTransitionFromStartedToStopping() {
        helpCheckStateTransition(started, RunnableState.STOPPING, true);
    }

    public void testStateTransitionFromStartedToStopped() {
        helpCheckStateTransition(started, RunnableState.STOPPED, false);
    }

    public void testStateTransitionFromStartedToFailed() {
        helpCheckStateTransition(started, RunnableState.FAILED, true);
    }

    // Test from STOPPING
    public void testStateTransitionFromStoppingToUnstarted() {
        helpCheckStateTransition(stopping, RunnableState.UNSTARTED, false);
    }

    public void testStateTransitionFromStoppingToStarting() {
        helpCheckStateTransition(stopping, RunnableState.STARTING, false);
    }

    public void testStateTransitionFromStoppingToStarted() {
        helpCheckStateTransition(stopping, RunnableState.STARTED, false);
    }

    public void testStateTransitionFromStoppingToStopping() {
        helpCheckStateTransition(stopping, RunnableState.STOPPING, true);
    }

    public void testStateTransitionFromStoppingToStopped() {
        helpCheckStateTransition(stopping, RunnableState.STOPPED, true);
    }

    public void testStateTransitionFromStoppingToFailed() {
        helpCheckStateTransition(stopping, RunnableState.FAILED, true);
    }

    // Test from STOPPED
    public void testStateTransitionFromStoppedToUnstarted() {
        helpCheckStateTransition(stopped, RunnableState.UNSTARTED, false);
    }

    public void testStateTransitionFromStoppedToStarting() {
        helpCheckStateTransition(stopped, RunnableState.STARTING, true);
    }

    public void testStateTransitionFromStoppedToStarted() {
        helpCheckStateTransition(stopped, RunnableState.STARTED, false);
    }

    public void testStateTransitionFromStoppedToStopping() {
        helpCheckStateTransition(stopped, RunnableState.STOPPING, false);
    }

    public void testStateTransitionFromStoppedToStopped() {
        helpCheckStateTransition(stopped, RunnableState.STOPPED, true);
    }

    public void testStateTransitionFromStoppedToFailed() {
        helpCheckStateTransition(stopped, RunnableState.FAILED, false);
    }

    // Test from FAILED
    public void testStateTransitionFromFailedToUnstarted() {
        helpCheckStateTransition(failed, RunnableState.UNSTARTED, false);
    }

    public void testStateTransitionFromFailedToStarting() {
        helpCheckStateTransition(failed, RunnableState.STARTING, true);
    }

    public void testStateTransitionFromFailedToStarted() {
        helpCheckStateTransition(failed, RunnableState.STARTED, false);
    }

    public void testStateTransitionFromFailedToStopping() {
        helpCheckStateTransition(failed, RunnableState.STOPPING, false);
    }

    public void testStateTransitionFromFailedToStopped() {
        helpCheckStateTransition(failed, RunnableState.STOPPED, false);
    }

    public void testStateTransitionFromFailedToFailed() {
        helpCheckStateTransition(failed, RunnableState.FAILED, true);
    }

}
