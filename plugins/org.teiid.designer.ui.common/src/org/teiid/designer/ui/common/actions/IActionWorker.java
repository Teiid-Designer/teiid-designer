/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.actions;


/** 
 * @since 8.0
 */
public interface IActionWorker {
    
    boolean selectionChanged(Object selection);
    
    boolean setEnabledState();
    
    boolean execute();
    
    Object getSelection();
    
    boolean getEnableAfterExecute();
    
    WorkerProblem getWorkerProblem();

}
