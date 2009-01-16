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

package com.metamatrix.modeler.core.transaction;

import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * @author Lance Phillips
 *
 * @since 3.1
 */
public interface MtkTransaction {
    /**
     * Commit the transaction and create the undoable edit
     * @throws ModelerCoreException
     */
    void commit() throws ModelerCoreException;
    
    /**
     * Rollback the transaction and create the undoable edit
     * @throws ModelerCoreException
     */
    void rollback() throws ModelerCoreException;
    
    /**
     * Begin the transaction... should intialize all the txn resources.
     */
    void begin() throws ModelerCoreException;
    
    /**
     * 
     * @return true if the state == TransactionStateConstants.STARTED, else
     * false
     */
    boolean isStarted();

    /**
     * @return true if the state == FAILED, UNITIALIZED or COMPLETE, else
     * false
     */
    boolean requiresStart();
    
    /**
     * 
     * @return true if the state == TransactionStateConstants.ROLLING_BACK, else
     * false
     */
    boolean isRollingBack();

    /**
     * 
     * @return true if the state == TransactionStateConstants.COMPLETE, else
     * false
     */
    public boolean isComplete();
    
    /**
     * 
     * @return true if the state == TransactionStateConstants.COMMITTING, else
     * false
     */
    boolean isCommitting();
    
    /**
     * 
     * @return true if the state == TransactionStateConstants.FAILED, else false
     */
    boolean isFailed();
    
    /**
     * @return the UoW source attribute
     */
    public Object getSource();

    /**
     * Set the UoW source attribute
     * @param object
     */
    public void setSource(Object object);
}
