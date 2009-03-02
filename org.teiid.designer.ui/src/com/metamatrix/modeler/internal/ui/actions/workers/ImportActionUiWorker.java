/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions.workers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.modeler.internal.ui.actions.help.NewProjectAction;
import com.metamatrix.modeler.internal.ui.wizards.ImportWizard;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class ImportActionUiWorker {
	private IStructuredSelection theSelection;
	private IWorkbenchWindow wdw;
	
	public ImportActionUiWorker(IStructuredSelection selection, IWorkbenchWindow window) {
		super();
		this.theSelection = selection;
		this.wdw = window;
	}
	
	public boolean run() {
		if( theSelection != null ) {
		    if ( ! ( SelectionUtilities.getSelectedObject(theSelection) instanceof IResource ) ) {
		        theSelection = getFirstOpenProject();
		    }
		    if( !theSelection.isEmpty() ) {
		        int windowRC = WidgetFactory.createWizardDialog(this.wdw.getShell(), new ImportWizard(this.wdw.getWorkbench(), theSelection)).open();
                return windowRC == Window.OK;
			}
		}

        return false; // == did not run or cancelled
	}

	private IStructuredSelection getFirstOpenProject() {
	    // Check workspace for projects
	    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	    if ( projects == null || projects.length == 0 ) {
            if( ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
                // Force create a project if doesn't exist
                ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(true);
                projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
            } else {
    	        NewProjectAction npAction = new NewProjectAction();
    	        npAction.run();
    	        //  Now we check again. If user has canceled, we'll know it and we need to return EMPTY selection
    	        // (i.e. can't do anything)
    	        projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    	        if ( projects == null || projects.length == 0 ) {
    	            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    	            String title = UiConstants.Util.getString("ImportMetadata.noProjectTitle"); //$NON-NLS-1$
    	            String message = UiConstants.Util.getString("ImportMetadata.noProjectMessage"); //$NON-NLS-1$
    	            MessageDialog.openError(shell, title, message);
    	            return StructuredSelection.EMPTY;
    	        }
            }
	    }
	
	    // Now, we've made it this far and there is at least one project
	    boolean noOpenProject = true;
	    for ( int i=0 ; i<projects.length ; ++i ) {
	        if ( projects[i].isOpen() ) {
	            noOpenProject = false;
	            return new StructuredSelection(projects[i]);
	        }
	        if ( noOpenProject && i == projects.length ) {
	            notifyNoOpenProject();
	            return StructuredSelection.EMPTY;
	        }
	    }
	    
	    return StructuredSelection.EMPTY;
	}
	
	private void notifyNoOpenProject() {
	    String title = UiConstants.Util.getString("ImportMetadata.noProjectTitle"); //$NON-NLS-1$
	    String message = UiConstants.Util.getString("ImportMetadata.projectClosedMessage"); //$NON-NLS-1$
	    MessageDialog.openError(UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(), title, message);
	}
}
