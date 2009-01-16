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

package com.metamatrix.core.event;

/**
 * The <code>IChangeNotifier</code> interface provides a means for registering for change notification.
 */
public interface IChangeNotifier {

    /**
     * Adds the given listener to this notifier. Has no effect if an identical listener is already registered.
     * @param theListener the listener being registered
     */
    void addChangeListener(IChangeListener theListener);
    
    /**
     * Removes the given listener from this notifier. Has no effect if the listener is not registered.
     * @param theListener the listener being unregistered
     */
    void removeChangeListener(IChangeListener theListener);

}
