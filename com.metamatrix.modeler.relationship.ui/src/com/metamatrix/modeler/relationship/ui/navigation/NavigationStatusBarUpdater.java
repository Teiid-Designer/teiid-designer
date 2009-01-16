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

package com.metamatrix.modeler.relationship.ui.navigation;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.metamatrix.modeler.relationship.NavigationLink;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;

/**
 * NavigationStatusBarUpdater
 */
public class NavigationStatusBarUpdater implements ISelectionChangedListener {

    private IStatusLineManager statusLineManager;

    /**
     * Construct an instance of StatusBarUpdater.
     * 
     */
    public NavigationStatusBarUpdater(IStatusLineManager statusLineManager) {
        this.statusLineManager = statusLineManager;
    }

    /*
     * @see ISelectionChangedListener#selectionChanged
     */
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        String statusBarMessage = formatMessage(selection);
        statusLineManager.setMessage(statusBarMessage);
    }

    protected String formatMessage(ISelection sel) {
        if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
            IStructuredSelection selection = (IStructuredSelection)sel;

            int nElements = selection.size();
            if (nElements > 1) {
                return UiConstants.Util.getString("NavigationStatusBarUpdater.num_elements_selected", String.valueOf(nElements)); //$NON-NLS-1$
            }
            Object elem = selection.getFirstElement();
            if (elem instanceof NavigationNode) {
                return format((NavigationNode)elem);
            } else if (elem instanceof NavigationLink) {
                return format((NavigationLink)elem);
            } else {
                return elem.getClass().getName();
            }
        }
        return ""; //$NON-NLS-1$
    }

    public String format(NavigationNode node) {
        String result = node.getMetaclass().getName();
        result += ": "; //$NON-NLS-1$
        result += node.getLabel();
        result += " - "; //$NON-NLS-1$
        result += node.getPathInModel();
        return result;
    }
    
    public String format(NavigationLink link) {
        String result = link.getType();
        result += ": "; //$NON-NLS-1$
        result += link.getLabel();
        return result;
    }

}
