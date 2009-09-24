/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.product;

import org.eclipse.ui.IWorkbenchWindow;


/**
 * The <code>IVetoableShutdownListener</code> interface provides extensions the ability to veto product shutdown.
 * Listeners will be notified before warning the user of unsaved editors.
 * @since 5.0
 */
public interface IVetoableShutdownListener {
    
    /**
     * Indicates if shutdown should continue or be stopped. 
     * @return <code>true</code> if shutdown should continue; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean continueShutdown();
    
    /**
     * Sets the window. This is the last open window before shutting down. 
     * @param theWindow the last open window
     * @since 5.0
     */
    void setWindow(IWorkbenchWindow theWindow);

}
