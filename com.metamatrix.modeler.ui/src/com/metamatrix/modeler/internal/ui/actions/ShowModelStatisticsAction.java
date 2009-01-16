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

package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class ShowModelStatisticsAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
	 private IFile selectedModel;
	 
	public ShowModelStatisticsAction() {
		super();
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor( PluginConstants.Images.MODEL_STATISTICS_ICON));
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        boolean enable = false;
        if ( ! SelectionUtilities.isMultiSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IFile && ModelUtilities.isModelFile((IFile) obj) ) {
                this.selectedModel = (IFile) obj;
                enable = true;
            }
        }
        setEnabled(enable);
	}

	@Override
    public void run() {
		if( selectedModel != null ) {
	        ModelStatisticsReporter reporter = new ModelStatisticsReporter(selectedModel);
	        reporter.show();
		}
	}

    
	public int compareTo(Object o) {
		if( o instanceof String) {
			return getText().compareTo((String)o);
		}
		
		if( o instanceof Action ) {
			return getText().compareTo( ((Action)o).getText() );
		}
		return 0;
	}
	
	
	public boolean isApplicable(ISelection selection) {
        boolean result = false;
        if ( ! SelectionUtilities.isMultiSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IFile && ModelUtilities.isModelFile((IFile) obj) ) {
                result = true;
            }
        }
        
        return result;
	}
}
