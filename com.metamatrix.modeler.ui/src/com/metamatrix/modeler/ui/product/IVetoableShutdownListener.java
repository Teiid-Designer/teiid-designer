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
