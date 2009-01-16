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

package com.metamatrix.modeler.ui.editors;


/** 
 * This interface provides classes like <code>DiagramEditor</code> i.e. (<code>ModelEditorPage</code>)to identify themselves as needing
 * to broadcast when their initialization is complete. In the case of the <code>DiagramEditor</code>, the act of displaying a diagram, 
 * requires an async process that will potentially change the resource (i.e. make it dirty). As a notifier, the <code>ModelEditor</code>
 * (main editor class) can register itself as a listener for the <code>ModelEditor</code>, so it can perform a save when the DiagramEditor 
 * has completed it's display.
 * 
 * see <code>IInitializationCompletionListener</code> for the companion listener class
 * @since 4.3
 */
public interface IInitializationCompleteNotifier {

    /**  
     * 
     * @since 4.3
     */
    void notifyInitializationComplete();
    
    /**
     *  
     * @param listener
     * @since 4.3
     */
    void addListener(IInitializationCompleteListener listener);
    
    /**
     *  
     * @param listener
     * @since 4.3
     */
    void removeListener(IInitializationCompleteListener listener);
    
}
