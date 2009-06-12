/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
