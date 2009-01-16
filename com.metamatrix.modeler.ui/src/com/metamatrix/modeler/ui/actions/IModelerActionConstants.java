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

package com.metamatrix.modeler.ui.actions;

import org.eclipse.ui.IWorkbenchActionConstants;

import com.metamatrix.modeler.internal.ui.actions.CloneAction;
import com.metamatrix.modeler.internal.ui.actions.CopyFullNameAction;
import com.metamatrix.modeler.internal.ui.actions.CopyNameAction;
import com.metamatrix.modeler.internal.ui.actions.EditAction;
import com.metamatrix.modeler.internal.ui.actions.EditTableEditorPreferencesAction;
import com.metamatrix.modeler.internal.ui.actions.InsertRowsAction;
import com.metamatrix.modeler.internal.ui.actions.OpenAction;
import com.metamatrix.modeler.internal.ui.actions.PasteSpecialAction;
import com.metamatrix.modeler.internal.ui.actions.RefreshTableAction;
import com.metamatrix.modeler.internal.ui.actions.SetDatatypeAction;
import com.metamatrix.modeler.internal.ui.actions.TableClipboardPasteAction;
import com.metamatrix.modeler.internal.ui.actions.TablePrintAction;
import com.metamatrix.ui.actions.IActionConstants;

/**
 * The <code>IModelerActionConstants</code> interface defines the constants used for the
 * action identifiers for global actions and actions groups used in the modeler.
 */
public interface IModelerActionConstants extends IActionConstants {

    interface ModelerGlobalActions {

        /** Key for accessing the global Paste Special action. */
        String PASTE_SPECIAL = PasteSpecialAction.class.getName();
        
        /** Key for accessing the global Paste Special action. */
        String SET_DATATYPE = SetDatatypeAction.class.getName();

        /** Key for accessing the global copy action. */
        String CLONE = CloneAction.class.getName();
        
        /** Key for accessing the global copy full name action. */
        String COPY_FULL_NAME = CopyFullNameAction.class.getName();
        
        /** Key for accessing the global copy name action. */
        String COPY_NAME = CopyNameAction.class.getName();
        
        /** Key for accessing the global edit action. */
        String EDIT = EditAction.class.getName();
        
        /** Key for accessing the global edit action. */
        String OPEN = OpenAction.class.getName();
        
        /** All Modeler unique global actions. */
        String[] ALL_ACTIONS = new String[] {
                                             PASTE_SPECIAL,
                                             CLONE,
                                             EDIT,
                                             OPEN,
                                             SET_DATATYPE,
                                             COPY_FULL_NAME,
                                             COPY_NAME,
                                             };
    }
    
    interface ContextMenu {
        /** The suffix to add to a part's class name to get their associated context menu identifier. */
        String MENU_ID_SUFFIX = ".contextMenu"; //$NON-NLS-1$
        
        /** Name of group for start of new child, new sibling menu items. */
        String INSERT_START = "insertStart"; //$NON-NLS-1$

        /** Name of group for end of new child, new sibling menu items. */
        String INSERT_END = "insertEnd"; //$NON-NLS-1$

        /** Name of group for start of undo, redo menu items. */
        String UNDO_START = "undoStart"; //$NON-NLS-1$

        /** Name of group for end of undo, redo menu items. */
        String UNDO_END = "undoEnd"; //$NON-NLS-1$
        
        /** Name of group for start of cut, copy, paste menu items. */
        String CUT_START = "cutStart"; //$NON-NLS-1$

        /** Name of group for end of cut, copy, paste menu items. */
        String CUT_END = "cutEnd"; //$NON-NLS-1$
        
        /** Name of group for start of delete, rename menu items. */
        String DELETE_START = "deleteStart"; //$NON-NLS-1$

        /** Name of group for end of delete, rename menu items. */
        String DELETE_END = "deleteEnd"; //$NON-NLS-1$
        
        /** Name of group for start of open, edit menu items. */
        String OPEN_START = "openStart"; //$NON-NLS-1$

        /** Name of group for end of open, edit menu items. */
        String OPEN_END = "openEnd"; //$NON-NLS-1$
        
        /** Name of the group that contains model related actions. */
        String MODEL_START = "modelStart"; //$NON-NLS-1$
        
        /** Name of the separator that ends the group of model related actions. */
        String MODEL_END = "modelEnd"; //$NON-NLS-1$
        
        /** Name of last separator in the context menu. */
        String ADDITIONS = IWorkbenchActionConstants.MB_ADDITIONS;
    }
    
    interface StatusBar {
        String SUFFIX = ".statusBarField"; //$NON-NLS-1$
        
        /** StatusBar field showing the Read Only/Writable state. */
        String MODEL_EDITOR_FILE_STATE = "modelEditorFileState" + SUFFIX; //$NON-NLS-1$
    }
    
    interface TableEditorActions {
        String CLIPBOARD_PASTE = TableClipboardPasteAction.class.getName();
        String INSERT_ROWS     = InsertRowsAction.class.getName();
        String PRINT           = TablePrintAction.class.getName();
        String EDIT_COLUMNS    = EditTableEditorPreferencesAction.class.getName();
        String REFRESH_TABLE   = RefreshTableAction.class.getName();
    }
    
}
