package org.teiid.designer.runtime.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.runtime.ui.DqpUiConstants.Images;
import org.teiid.designer.runtime.ui.wizards.vdbs.GenerateArchiveVdbWizard;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

public class DeployJarAction  extends SortableSelectionAction implements DqpUiConstants {
    private static final String label = "Deploy Jar"; //$NON-NLS-1$
    /**
     * @since 5.0
     */
    public DeployJarAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.CREATE_WAR));
    }

    /**
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return jarFileSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
        
        Object obj = SelectionUtilities.getSelectedObject(getSelection());
    	IFile theFile = (IFile)obj;
    	
    	// Check server to see if it's running
    	// if it's not warn the user
    	
    	// If it's running, then get the server and call:
    	
    	// server.deployDriver(jarOrRarFile);

    }
    


    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return jarFileSelected(selection);
    }

    private boolean jarFileSelected( ISelection theSelection ) {
        boolean result = false;
        List<Object> allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator<Object> iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                	IFile theFile = (IFile)nextObj;
                	 
                    result = theFile.getFileExtension().equalsIgnoreCase("JAR");
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    private Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }
}
