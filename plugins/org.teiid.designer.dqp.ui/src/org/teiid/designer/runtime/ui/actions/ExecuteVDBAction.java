/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.actions;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.vdb.ExecuteVdbDialog;
import org.teiid.designer.runtime.ui.vdb.ExecuteVdbWorker;
import org.teiid.designer.runtime.ui.vdb.VdbConstants;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.util.ErrorHandler;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.XmiVdb;


/**
 * 
 *
 * @since 8.0
 */
public class ExecuteVDBAction extends SortableSelectionAction implements VdbConstants {
    protected static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExecuteVDBAction.class);

    protected boolean successfulRefresh = false;

    IFile selectedVDB;
    
	static ExecuteVdbWorker worker;

	/**
	 * Execute VDB constructor
	 */
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
     * @param selection the selection
     * @return 'true' if applicable selection, 'false' if not
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                	ITeiidServer teiidServer = getServerManager().getDefaultServer();
                    if (teiidServer != null) {
                        return true;
                    }
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
        if (!checkForConnectedServer())
            return;

        IFile vdb = selectedVDB;

        try {
            if (!isVdbSyncd(vdb)) {
                Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
                String title = UTIL.getString("VdbNotSyncdDialog.title"); //$NON-NLS-1$
                String msg = UTIL.getString("VdbNotSyncdDialog.msg"); //$NON-NLS-1$
                if (!MessageDialog.openQuestion(shell, title, msg))
                    return;
            }

            if (vdb == null) {
                ExecuteVdbDialog dialog = new ExecuteVdbDialog(worker.getShell(), null);

                dialog.open();

                if (dialog.getReturnCode() == Window.OK) {
                    vdb = dialog.getSelectedVdb();
                }
            }

            if (vdb != null) {
                worker.run(vdb);
            }
        } catch (Exception ex) {
            ErrorHandler.toExceptionDialog(ex);
        }
    }
    
    /*
     * Check that the default teiid instance is connected.  Show dialog if it is not.
     * @return 'true' if default teiid instance is connected, 'false' if not.
     */
    private boolean checkForConnectedServer() {
        ITeiidServer teiidServer = getServerManager().getDefaultServer();
        if(teiidServer==null || !teiidServer.isConnected()) {
    		Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
    		String title = UTIL.getString("ActionRequiresServer.title"); //$NON-NLS-1$
    		String msg = UTIL.getString("ActionRequiresServer.msg"); //$NON-NLS-1$
        	MessageDialog.openInformation(shell,title,msg);
        	return false;
        }
        return true;
    }
    
    private boolean isVdbSyncd(IFile file) throws Exception {
    	Vdb vdb = new XmiVdb(file);
    	return vdb.isSynchronized();
    }
    
    /**
     * Execute the VDB
     * @param teiidServer the TeiidServer instance
     * @param vdbName the VDB
     * @throws Exception exception
     */
    public static void executeVdb( ITeiidServer teiidServer, String vdbName ) throws Exception {
    	if( worker == null ) {
    		worker = new ExecuteVdbWorker();
    	}
    	worker.processForDTP(teiidServer, vdbName);
	}
    
    private static ITeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
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
