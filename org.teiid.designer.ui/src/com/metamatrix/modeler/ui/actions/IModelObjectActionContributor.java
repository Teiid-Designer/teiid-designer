/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;

/**
 * IModelObjectActionContributor
 */
public interface IModelObjectActionContributor {
    
    /**
     * Called to give the <code>IModelObjectActionContributor</code> an opportunity to contribute to the 
     * context menu
     * @param theMenuMgr the context menu
     * @param theSelection the current selection
     */
    void contributeToContextMenu(IMenuManager theMenuMgr, ISelection theSelection);
    
    /**
     * Called to give the <code>IModelObjectActionContributor</code> an opportunity to contribute to the 
     * Edit menu
     * @param theMenuMgr the Edit menu
     * @param theSelection the current selection
     */
    void contributeToEditMenu(IMenuManager theMenuMgr, ISelection theSelection);
    
    /**
     * Called to give the <code>IModelObjectActionContributor</code> an opportunity to contribute to the Modeling actions
     * submenu
     * @param theSelection the current selection
     * @return List of additional actions
     */
    List<IAction> getAdditionalModelingActions(ISelection theSelection);
}
