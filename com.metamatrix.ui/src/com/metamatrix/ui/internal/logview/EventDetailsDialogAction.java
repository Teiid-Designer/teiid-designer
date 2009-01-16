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

package com.metamatrix.ui.internal.logview;

import java.util.Comparator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;


/**
 * @since 4.3
 */
public class EventDetailsDialogAction extends SelectionProviderAction{

    /**
     * The shell in which to open the property dialog
     */
    private Shell shell;
    private ISelectionProvider provider;
    private EventDetailsDialog propertyDialog;
    private Comparator comparator;
    /**
     * Creates a new action for opening a property dialog
     * on the elements from the given selection provider
     * @param shell - the shell in which the dialog will open
     * @param provider - the selection provider whose elements
     * the property dialog will describe
     */
    public EventDetailsDialogAction(Shell shell, ISelectionProvider provider){
        super(provider, LogViewMessages.EventDetailsDialog_title);
        Assert.isNotNull(shell);
        this.shell = shell;
        this.provider = provider;
        // setToolTipText
        //WorkbenchHelp.setHelp
    }

    public boolean resetSelection(byte sortType, int sortOrder){
        IAdaptable element = (IAdaptable) getStructuredSelection().getFirstElement();
        if (element == null)
            return false;
        if (propertyDialog != null && propertyDialog.isOpen()){
            propertyDialog.resetSelection(element, sortType, sortOrder);
            return true;
        }
        return false;
    }
    public void resetSelection(){
        IAdaptable element = (IAdaptable) getStructuredSelection().getFirstElement();
        if (element == null)
            return;
        if (propertyDialog != null && propertyDialog.isOpen())
            propertyDialog.resetSelection(element);
    }

    public void resetDialogButtons(){
        if (propertyDialog != null && propertyDialog.isOpen())
            propertyDialog.resetButtons();
    }

    public void setComparator(Comparator comparator){
        this.comparator = comparator;
        if (propertyDialog != null && propertyDialog.isOpen())
            propertyDialog.setComparator(comparator);
    }

    @Override
    public void run(){
        if (propertyDialog != null && propertyDialog.isOpen()){
            resetSelection();
            return;
        }

        //get initial selection
        IAdaptable element = (IAdaptable) getStructuredSelection().getFirstElement();
        if (element == null)
            return;

        propertyDialog = new EventDetailsDialog(shell, element, provider, comparator);
        propertyDialog.create();
        propertyDialog.getShell().setText(LogViewMessages.EventDetailsDialog_title);
        propertyDialog.open();
    }
}
