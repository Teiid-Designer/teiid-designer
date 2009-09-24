/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;

import com.metamatrix.vdb.edit.VdbEditingContext;


/** 
 * @since 4.3
 */
public interface IVdbEditorPage extends IEditorPart {
    
    /**
     * Obtains the workspace selection handler. 
     * @return the selection handler or <code>null</code>
     * @since 4.3
     */
    ISelectionListener getSelectionListener();
    
    /**
     * An opportunity for the page to perform work prior to the VDB editor being disposed.
     * @since 4.2
     */
    void preDispose();
    
    /**
     * Sets a display name on the page suitable for the current locale. 
     * @param theName the name
     * @since 4.3
     */
    void setDisplayName(String theName);

    /**
     * Sets the <code>VdbEditingContext</code>.
     * @param theEditingContext the context
     * @since 4.3
     */
    void setVdbEditingContext(VdbEditingContext theEditingContext);
    
    /**
     * Alerts the page of changes to the editor's readonly state.
     * @param theReadOnlyFlag the flag indicating if the editor is in a readonly state
     */
    void updateReadOnlyState(boolean theReadOnlyFlag);
    
}
