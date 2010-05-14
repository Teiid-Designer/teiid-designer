/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
