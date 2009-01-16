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

package com.metamatrix.ui.actions;

import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;

/**
 * The <code>IModelerActionConstants</code> interface defines the constants used for the
 * action identifiers for global actions and actions groups used in the modeler.
 */
public interface IActionConstants {

    /**
     * Eclipse global actions. Keep consistent with
     * {@link org.eclipse.ui.IWorkbenchActionConstants#GLOBAL_ACTIONS}.
     */
    interface EclipseGlobalActions {
        /** Key for accessing the eclipse global copy action. */
        String BOOKMARK = IDEActionFactory.BOOKMARK.getId();

        /** Key for accessing the eclipse global copy action. */
        String COPY = ActionFactory.COPY.getId();

        /** Key for accessing the eclipse global cut action. */
        String CUT = ActionFactory.CUT.getId();

        /** Key for accessing the eclipse global delete action. */
        String DELETE = ActionFactory.DELETE.getId();

        /** Key for accessing the eclipse global delete action. */
        String FIND = ActionFactory.FIND.getId();

        /** Key for accessing the eclipse global paste action. */
        String PASTE = ActionFactory.PASTE.getId();

        /** Key for accessing the eclipse global paste action. */
        String PRINT = ActionFactory.PRINT.getId();

        /** Key for accessing the eclipse global redo action. */
        String REDO = ActionFactory.REDO.getId();

        /** Key for accessing the eclipse global rename action. */
        String RENAME = ActionFactory.RENAME.getId();

        /** Key for accessing the eclipse global redo action. */
        String SELECT_ALL = ActionFactory.SELECT_ALL.getId();

        /** Key for accessing the eclipse global undo action. */
        String UNDO = ActionFactory.UNDO.getId();

        /** All actions eclipse identifies as being global. */
        String[] ALL_ACTIONS = new String[] {
            UNDO, REDO, CUT, COPY, PASTE, PRINT, DELETE, FIND, SELECT_ALL, BOOKMARK, RENAME
        };
    }
}
