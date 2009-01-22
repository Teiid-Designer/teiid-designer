/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import com.metamatrix.core.CorePlugin;

/**
 * This class records the state for a component that can be started and stopped.
 * There are actually six states:
 * <ul>
 *   <li>{@link #UNSTARTED} - the initial state before any other transitions</li> 
 *   <li>{@link #STARTING}  - the state when the component is in the process of starting</li> 
 *   <li>{@link #STARTED}   - the state when the component has successfully started</li> 
 *   <li>{@link #STOPPING}  - the state when the component is in the process of stopping</li> 
 *   <li>{@link #STOPPED}   - the state when the component has successfully stopped</li> 
 *   <li>{@link #FAILED}    - the state when an error condition has occurred and the component resides
 *        in a failed and unusable state</li> 
 * </ul>
 * <p>
 * The following are the only allowable and legal state transitions:
 * <table>
 *   <tr><td>From State</td><td>Can transition to</td></tr>
 *   <tr><td>{@link #UNSTARTED}</td><td>{@link #STARTED}</td></tr>
 *   <tr><td>{@link #STARTING}</td><td>{@link #STARTED},{@link #FAILED}</td></tr>
 *   <tr><td>{@link #STARTED}</td><td>{@link #STOPPING},{@link #FAILED}</td></tr>
 *   <tr><td>{@link #STOPPING}</td><td>{@link #STOPPED},{@link #FAILED}</td></tr>
 *   <tr><td>{@link #STOPPED}</td><td>{@link #FAILED},{@link #STARTED}</td></tr>
 *   <tr><td>{@link #FAILED}</td><td>{@link #STARTED}</td></tr>
 * </table>
 * Of course, this class does <i>not</i> consider it an error to attempt to transition to the current state
 * (since the net result is no change in state).
 * </p>
 */
public class RunnableState {
    
    /** The initial state before any other transitions */
    public static final int UNSTARTED   = 0;
    /** The state when the component is in the process of starting */
    public static final int STARTING    = 1;
    /** The state when the component has successfully started */
    public static final int STARTED     = 2;
    /** The state when the component is in the process of stopping */
    public static final int STOPPING    = 3;
    /** The state when the component has successfully stopped */
    public static final int STOPPED     = 4;
    /** The state when an error condition has occurred */
    public static final int FAILED      = 5;
    
    protected static final String UNSTARTED_LITERAL   = "UNSTARTED"; //$NON-NLS-1$
    protected static final String STARTING_LITERAL    = "STARTING"; //$NON-NLS-1$
    protected static final String STARTED_LITERAL     = "STARTED"; //$NON-NLS-1$
    protected static final String STOPPING_LITERAL    = "STOPPING"; //$NON-NLS-1$
    protected static final String STOPPED_LITERAL     = "STOPPED"; //$NON-NLS-1$
    protected static final String FAILED_LITERAL      = "FAILED"; //$NON-NLS-1$
    
    private int state;
    
    protected static String getLiteralForState( int value ) {
        switch(value) {
            case UNSTARTED:
                return UNSTARTED_LITERAL;   
            case STARTING:
                return STARTING_LITERAL;   
            case STARTED:
                return STARTED_LITERAL;   
            case STOPPING:
                return STOPPING_LITERAL;   
            case STOPPED:
                return STOPPED_LITERAL;   
            case FAILED:
                return FAILED_LITERAL;
        }
        throw new IllegalArgumentException(CorePlugin.Util.getString("RunnableState.Unknown_state_value___7") + value ); //$NON-NLS-1$
    }


    /**
     * Constructor for RunnableState.
     */
    public RunnableState() {
        super();
        this.state = UNSTARTED;
    }

    /**
     * Returns the current state.
     * @return int the state
     */
    public int getState() {
        return state;
    }
    
    /**
     * Transition to a new state.
     * @param newState the new state; must be one of {@link #UNSTARTED}, {@link #STARTING},
     * {@link #STARTED}, {@link #STOPPING}, {@link #STOPPED}, or {@link #FAILED}.
     * @throws IllegalStateException if this unit of work cannot be transitioned legally from
     * its current state to the new state
     * @throws IllegalArgumentException if the supplied value is not a known state.
     */
    public void setState( int newState ) throws IllegalStateException {
        switch ( newState ) {
            case UNSTARTED:
                if ( this.state == UNSTARTED ) {
                    break;  // no change!
                }
                throw new IllegalStateException(CorePlugin.Util.getString("RunnableState.Unable_to_change_the_state_to__8") + UNSTARTED_LITERAL); //$NON-NLS-1$
            case STARTING:
                if ( this.state == STARTING ) {
                    break;  // no change!
                }
                if ( this.state == STARTED || this.state == STOPPING ) {
                    throw new IllegalStateException(CorePlugin.Util.getString("RunnableState.Unable_to_change_the_state_from__9") + getLiteralForState(this.state) + CorePlugin.Util.getString("RunnableState._to__10") + STARTING_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
                }
                this.state = newState;
                break;
            case STARTED:
                if ( this.state == STARTED ) {
                    break;  // no change!
                }
                if ( this.state == STARTING ) {
                    this.state = newState;
                    break;
                }
                throw new IllegalStateException(CorePlugin.Util.getString("RunnableState.Unable_to_change_the_state_from__11") + getLiteralForState(this.state) + CorePlugin.Util.getString("RunnableState._to__12") + STARTED_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
            case STOPPING:
                if ( this.state == STOPPING ) {
                    break;  // no change!
                }
                if ( this.state == STARTED ) {
                    this.state = newState;
                    break;
                }
                throw new IllegalStateException(CorePlugin.Util.getString("RunnableState.Unable_to_change_the_state_from__13") + getLiteralForState(this.state) + CorePlugin.Util.getString("RunnableState._to__14") + STOPPING_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
            case STOPPED:
                if ( this.state == STOPPED ) {
                    break;  // no change!
                }
                if ( this.state == STOPPING ) {
                    this.state = newState;
                    break;
                }
                throw new IllegalStateException(CorePlugin.Util.getString("RunnableState.Unable_to_change_the_state_from__15") + getLiteralForState(this.state) + CorePlugin.Util.getString("RunnableState._to__16") + STOPPED_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
            case FAILED:
                if ( this.state == FAILED ) {
                    break;  // no change!
                }
                if ( this.state == STARTING || this.state == STARTED || this.state == STOPPING ) {
                    this.state = newState;
                    break;
                }
                throw new IllegalStateException(CorePlugin.Util.getString("RunnableState.Unable_to_change_the_state_from__17") + getLiteralForState(this.state) + CorePlugin.Util.getString("RunnableState._to__18") + FAILED_LITERAL); //$NON-NLS-1$ //$NON-NLS-2$
            default:
                throw new IllegalArgumentException(CorePlugin.Util.getString("RunnableState.Unknown_state_value___19") + newState ); //$NON-NLS-1$
        }
    }
    
    /**
     * Return whether the current state matches the supplied argument.
     * @param state the state that is to be compared against the current state; must
     * be one of 
     * @return true if the state is the same as passed in, or false otherwise
     */
    public boolean isState( final int state ) {
        return this.state == state;
    }
    
    public boolean isInTransition() {
        return this.state == STARTING || this.state == STOPPING;
    }
    
    public boolean isRunning() {
        return this.state == STARTED;
    }

}
