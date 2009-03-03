/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.product;


/** 
 * @since 4.3
 */
public class WorkbenchState {
    //============================================================================================================================
    // Constants
    
    public static final int UNKNOWN = -1;
    public static final int STANDARD = 0;
    public static final int STARTING_UP = 1;
    public static final int SHUTTING_DOWN = 2;
    public static final int SHUT_DOWN = 3;
    
    //===================
    // FIELDS
    // ==================
    
    private int currentState = UNKNOWN;
    
    /** 
     * 
     * @since 4.3
     */
    public WorkbenchState() {
        super();
    }

    
    /** 
     * @return Returns the currentState.
     * @since 4.3
     */
    public int getCurrentState() {
        return this.currentState;
    }

    
    /** 
     * @param theCurrentState The currentState to set.
     * @since 4.3
     */
    public void setCurrentState(int theCurrentState) {
        this.currentState = theCurrentState;
    }
    
    public boolean isStandard() {
        return currentState == STANDARD;
    }
    
    public boolean isStartingUp() {
        return currentState == STARTING_UP;
    }
    
    public boolean isShuttingDown() {
        return currentState == SHUTTING_DOWN;
    }
    
    public boolean isShutDown() {
        return currentState == SHUT_DOWN;
    }
    
    public boolean isStateKnown() {
        return currentState != UNKNOWN;
    }
}
