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

package com.metamatrix.modeler.internal.dqp.ui.views;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;

/**
 * StatusBarUpdater
 */
public class StatusBarUpdater implements DqpUiConstants,
                                         ISelectionChangedListener {

    private IStatusLineManager statusLineManager;

    /**
     * Construct an instance of StatusBarUpdater.
     * 
     */
    public StatusBarUpdater(IStatusLineManager statusLineManager) {
        this.statusLineManager= statusLineManager;
    }
        
    /*
     * @see ISelectionChangedListener#selectionChanged
     */
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        String statusBarMessage= formatMessage(selection);
        statusLineManager.setMessage(statusBarMessage);
    }
    
    
    protected String formatMessage(ISelection sel) {
        if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
            IStructuredSelection selection= (IStructuredSelection) sel;
            
            int nElements= selection.size();
            if (nElements > 1) {
                return UTIL.getString("StatusBarUpdater.num_elements_selected", String.valueOf(nElements)); //$NON-NLS-1$
            } 
            Object elem= selection.getFirstElement();
            if (elem instanceof ConnectorBinding) {
                return ((ConnectorBinding) elem).getFullName();
            } else if (elem instanceof ComponentType) {
                return ((ComponentType) elem).getFullName();
            }
            
            return elem.getClass().getName();
        }
        return "";  //$NON-NLS-1$
    }

}
