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

package com.metamatrix.modeler.diagram.ui.editor;
  
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;

import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.GlobalActionsMap;

/**
 * DiagramActionAdapter
 */
public interface IDiagramActionAdapter {
    
    void pageActivated();
    
    void pageDeactivated();

    void setDiagramEditor( ModelEditorPage editor);
    
    void contributeToMenuManager(IMenuManager theMenuMgr, ISelection selection);
    
    boolean shouldOverrideMenu(ISelection selection);
    
    void contributeExportedActions(IMenuManager theMenuMgr);
    
    GlobalActionsMap getGlobalActions();
    
    void contributeToDiagramToolBar();
    
    void enableDiagramToolbarActions();
    
    void disposeOfActions();
    
    void handleNotification(Notification theNotification);
        
}
