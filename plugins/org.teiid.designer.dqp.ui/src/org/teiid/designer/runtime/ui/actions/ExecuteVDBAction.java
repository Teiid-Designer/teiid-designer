/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ui.vdb.ExecuteVdbDialog;
import org.teiid.designer.runtime.ui.vdb.ExecuteVdbWorker;
import org.teiid.designer.runtime.ui.vdb.VdbConstants;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * 
 */
public class ExecuteVDBAction extends SortableSelectionAction implements VdbConstants {
    protected static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExecuteVDBAction.class);

    protected boolean successfulRefresh = false;

    IFile selectedVDB;
    
	static ExecuteVdbWorker worker;

    public ExecuteVDBAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.EXECUTE_VDB));
        worker = new ExecuteVdbWorker();
    }

    @Override
    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    /**
     * @param selection
     * @return
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
//                    Server server = DqpPlugin.getInstance().getServerManager().getDefaultServer();
//                    if (server != null) {
                        return true;
//                    }
                }
            }
        }
        return result;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {
    	IFile vdb = selectedVDB;
    	
    	if (vdb == null) {
    		ExecuteVdbDialog dialog = new ExecuteVdbDialog(worker.getShell());

    		dialog.open();

    		if (dialog.getReturnCode() == Window.OK) {
    			vdb = dialog.getSelectedVdb();
    		}
    	}
    	
    	if( vdb != null ) {
    		worker.run(vdb);
    	}
    }
    
    public static void executeVdb( Server server, String vdbName ) throws CoreException {
    	if( worker == null ) {
    		worker = new ExecuteVdbWorker();
    	}
    	worker.processForDTP(server, vdbName);
	}
    


    @Override
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        boolean enable = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals(VDB_EXTENSION)) {
                    this.selectedVDB = (IFile)obj;
                    enable = true;
                }
            }
        }
        if( !enable ) {
        	this.selectedVDB = null;
        }
        setEnabled(enable);
    }


}
