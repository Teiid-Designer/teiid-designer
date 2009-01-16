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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This class provides an action class that can be alphabetically sorted. The initial intention is to provide a contribution
 * mechanism for plugins to add actions to either Resource (i.e. Model) or EObject (model object) selections.  The isApplicable() 
 * method will be called for each selection to determine if the action should be inluded in a context menu, for instance.
 *  
 * @since 5.0
 */

public abstract class SortableSelectionAction extends Action 
	implements ISelectionListener, Comparable,  ISelectionAction, ISelectionChangedListener {
    
    // Value can be set, so when action is loaded and instantiated by the extension manager, it will be wired as a listener for
    // workspace selection.
    private boolean wireForSelection = false;

	private ISelection selection;
	
    /**
     *  
     * @since 5.0
     */
	public SortableSelectionAction() {
		super();
	}
    
    /**
     *  
     * @param text
     * @param style
     * @since 5.0
     */
    public SortableSelectionAction(String text, int style) {
        super(text, style);
    }
	
    /**
     *  
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		boolean enable = isValidSelection(selection);
		if( enable ) {
			setSelection(selection);
		}
		setEnabled(enable);
	}

    /**
     *  
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @since 5.0
     */
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection sel = event.getSelection();
        
        selectionChanged(null, sel);
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
	public boolean isApplicable(ISelection selection) {
		return true;
	}
	
    /**
     *  
     * @param o
     * @return
     * @since 5.0
     */
	public int compareTo(Object o) {
		if( o instanceof String) {
			return getText().compareTo((String)o);
		}
		
		if( o instanceof Action ) {
			return getText().compareTo( ((Action)o).getText() );
		}
		return 0;
	}
	
	public void setSelection(ISelection selection) { 
		this.selection = selection;
	}
	
	public ISelection getSelection(){
		return this.selection;
	}
	
    /**
     *  
     * @param selection
     * @return
     * @since 5.0
     */
	protected boolean isValidSelection(ISelection selection) {
		if( selection.isEmpty())
			return false;
			
		return true;
	}
	
	public String getSingleMenuText() {
		return getText();
	}
    
    /**
     *  
     * @param value
     * @since 5.0
     */
    protected void setWiredForSelection(boolean value) {
        this.wireForSelection = value;
    }
    
    /**
     *  
     * @return true if should wire for workspace selection
     * @since 5.0
     */
    public boolean doWireForSelection() {
        return this.wireForSelection;
    }
    
    public SortableSelectionAction getClone() {
        return null;
    }
}
