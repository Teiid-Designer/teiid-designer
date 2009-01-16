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

package com.metamatrix.modeler.internal.ui.refactor;

/**
 * RefactorUndoListener is an interface for events that occur in the RefactorUndoManager.
 */
public interface RefactorUndoListener {

    /**
     * notify listeners that the state of the RefactorUndoManager has changed.  Listeners
     * should respond by checking with the RefactorUndoManager to refresh their state. 
     * This method is called when the RefactorUndoManager performs an undo, or redo, or
     * clears, or receives a new command.
     */
    public void stateChanged();

}
