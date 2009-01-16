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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;

import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * Interface for the UnitOfWork Object for an Emf Container
 * @author lphillips
 * @since 3.1
 * 
 */
public interface UnitOfWork extends MtkTransaction{
    /**
     * Call back method for the EmfAdapter to notify the transaction of an event notification
     * @param notification
     */
    void processNotification(Notification notification) throws ModelerCoreException;
    
//    /**
//     * Process the given invocation through this UnitOfWork..
//     * If the invocation involves a write command, execute it through the edit
//     * domain, else, execute it directly against the delegate.
//     * @param invocation
//     * @return the Object result of processing the invocation
//     */
//    Object process(Invocation invocation) throws ModelerCoreException;

    /**
     * @return the id for this UnitOfWork
     */
    Object getId();

    /**
     * Setter for description attribute.  Used when creating the undoable.
     * @param description
     */
    public void setDescription(String description);
            
    /**
     * Pass through to the editing domain /  command stack to execute the
     * command
     * @return true if the command was executed, or false if it could not be executed
     * @see com.metamatrix.mtk.emf.container.container.transaction.api.UnitOfWork#executeCommand(Command)
     */
    boolean executeCommand(Command command) throws ModelerCoreException;  
    
    /**
     * Setter for the significant attribute that will passed on to the undoable upon commit
     * @param isSignificant
     * @throws ModelerCoreException if the UoW is not in a started state
     */
    void setSignificant(boolean isSignificant) throws ModelerCoreException; 
    
    /**
     * @return the isUndoable flag
     */
    boolean isUndoable();

    /**
     * Set the isUndoable flag
     * @param b
     */
    public void setUndoable(boolean b); 
}
