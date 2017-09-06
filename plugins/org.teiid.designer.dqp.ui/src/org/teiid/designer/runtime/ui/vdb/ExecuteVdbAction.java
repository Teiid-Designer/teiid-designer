/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.vdb;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.util.ErrorHandler;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.XmiVdb;


/**
 * @since 8.0
 */
public class ExecuteVdbAction extends Action {
	@SuppressWarnings("javadoc")
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(ExecuteVdbAction.class);
	
	ExecuteVdbWorker worker;
	
	Properties designerProperties;

	/**
	 * @since 5.0
	 */
	public ExecuteVdbAction() {
		super();
		setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.EXECUTE_VDB));
		setToolTipText(DqpUiConstants.UTIL.getString(THIS_CLASS + "tooltip")); //$NON-NLS-1$
		worker = new ExecuteVdbWorker();
	}
	
	/**
	 * Execute VDB
	 * @param properties the properties
	 * @since 5.0
	 */
	public ExecuteVdbAction(Properties properties) {
		this();
		designerProperties = properties;
	}

	@Override
	public void run() {
        if (!checkForConnectedServer())
            return;

        ExecuteVdbDialog dialog = new ExecuteVdbDialog(worker.getShell(), designerProperties);

        dialog.open();

        if (dialog.getReturnCode() != Window.OK)
            return;

        IFile vdb = dialog.getSelectedVdb();
        if (vdb == null)
            return;

        try {
            if (!isVdbSyncd(vdb) ) {
                Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
                String title = UTIL.getString("VdbNotSyncdDialog.title"); //$NON-NLS-1$
                String msg = UTIL.getString("VdbNotSyncdDialog.msg"); //$NON-NLS-1$
                if (!MessageDialog.openQuestion(shell, title, msg))
                    return;
            }
            if( !VdbErrorChecker.hasErrors(vdb, false)) {
            	return;
            }
            worker.run(vdb);
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
        if(teiidServer == null || !teiidServer.isConnected()) {
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

    private static ITeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }
	
}
