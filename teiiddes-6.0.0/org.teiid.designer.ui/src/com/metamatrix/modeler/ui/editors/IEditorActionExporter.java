/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.editors;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;

/**
 * The <code>IEditorActionExporter</code> describes the way <code>IEditorPart</code>s can contribute
 * to model views' context menus. This interface is used by the <code>ModelerActionService</code> when
 * creating context menus. When a model view context menu is being created, if the current editor
 * implements this interface, it will be asked to contribute to the context menu.
 */
public interface IEditorActionExporter {

    /**
     * Offers the editor a chance to contribute actions which will be made available to context menus
     * from other model views.
     * @param theMenuMgr the context menu being contributed to
     */
    public void contributeExportedActions(IMenuManager theMenuMgr);
    
    /**
     *  Offers the editor a chance to contribute actions which will be made available to menus from other views
     * @param selection
     * @return List of exported actions
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions(ISelection selection);
}
