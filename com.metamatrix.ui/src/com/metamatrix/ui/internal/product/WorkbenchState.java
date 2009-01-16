/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
