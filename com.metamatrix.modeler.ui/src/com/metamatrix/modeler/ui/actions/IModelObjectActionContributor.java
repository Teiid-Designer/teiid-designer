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
