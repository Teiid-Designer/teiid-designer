/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.transaction;


/**
 * Interface to Manage EMF Transactions within an EMFContainer
 * @author lphillips
 * @since 3.1
 * 
 */
public interface UnitOfWorkProvider {
    /**
     * Return the current UnitOfWork object for the given EMF Container
     * @return UnitOfWork
     */
    UnitOfWork getCurrent();
    
    /**
     * Remove the txn for the given thread
     * @param thread
     */
    void cleanup(Thread thread);
    
    /**
     * Add the given listener to the list of UndoableEventListeners
     * @param listener
     */
    void addUndoableEditListener(final UndoableListener listener);
    
    /**
     * Remove the given listener from the list of UndoableEventListeners
     * @param listener
     */
    void removeUndoableEditListener(final UndoableListener listener);

}
