/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.util.JndiUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.dialogs.CreateVdbDataSourceDialog;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

/**
 *
 */
public class CreateVdbDataSourceAction  extends SortableSelectionAction implements DqpUiConstants {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(CreateVdbDataSourceAction.class);
    private static final String label = DqpUiConstants.UTIL.getString("label"); //$NON-NLS-1$

    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object value ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value);
    }
    
    private static String getString( final String id, final Object value, final Object value2) {
    	return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value, value2);
	}

    private ITeiidServer cachedServer;
    
    private String cachedVdbName;

    /**
     * @since 5.0
     */
    public CreateVdbDataSourceAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
    }
    
    /**
     * @since 5.0
     */
    public CreateVdbDataSourceAction(String vdbName) {
    	super(label, SWT.DEFAULT);
    	this.cachedVdbName = vdbName;
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
    }

    public void setTeiidServer( ITeiidServer teiidServer ) {
        this.cachedServer = teiidServer;
    }

    /**
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return vdbSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();

        IFile selectedVdb = null;
        if (!getSelection().isEmpty()) {
        	selectedVdb = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
        }
        try {
        	// Check Server status. If none defined, query to create or cancel.
        	
            ITeiidServer teiidServer = cachedServer;
            if (teiidServer == null) {
            	teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
            	if( teiidServer == null ) {
	            	if( RuntimeAssistant.ensureServerConnection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	            			getString("noServer.message"), true) ) { //$NON-NLS-1$
	            		teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
	            		teiidServer.connect();	
	            	} else {
	            		// User has cancelled this action or decided not to create a new server
	            		return;
	            	}
            	} else {
            		if( RuntimeAssistant.ensureServerConnection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	            			getString("noServer.message"), false) ) { //$NON-NLS-1$
	            		teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
	            		teiidServer.connect();	
	            	} else {
	            		// User has cancelled this action or decided not to create a new server
	            		return;
	            	}
            	}
            }
            
            // A) get the selected model and extract a "ConnectionProfileInfo" from it using the ConnectionProfileInfoHandler

            // B) Use ConnectionProfileHandler.getConnectionProfile(connectionProfileInfo) to query the user to
            // select a ConnectionProfile (or create new one)

            // C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
            // via the ConnectionProfileInfoHandler
            
            String vdbName = cachedVdbName;
            if( vdbName == null ) {
            	vdbName = selectedVdb.getLocation().removeFileExtension().lastSegment();
            }
            
            // Do a check to see if vdbName exists on server, then warn user if it does NOT
            ITeiidVdb deployedVDB = teiidServer.getVdb(vdbName);
            if( deployedVDB == null ) {
            	boolean result = MessageDialog.openQuestion(iww.getShell(), getString("noDeployedVDB.title"),  //$NON-NLS-1$
            			getString("noDeployedVDB.message", vdbName)); //$NON-NLS-1$
            	if (! result ) return;
            }
            
            doCreateDataSource(vdbName, teiidServer, false);

        } catch (Exception e) {
            if (selectedVdb != null) {
                MessageDialog.openError(getShell(),
                                        getString("errorCreatingDataSourceForVDB", selectedVdb.getName()), e.getMessage()); //$NON-NLS-1$
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("errorCreatingDataSourceForVDB",selectedVdb.getName())); //$NON-NLS-1$
            } else {
                MessageDialog.openError(getShell(), getString("errorCreatingDataSource"), e.getMessage()); //$NON-NLS-1$
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("errorCreatingDataSource")); //$NON-NLS-1$

            }
        }
    }
    
    /**
     * Creates Data Source for a VDB
     * @param vdbName the VDB name
     * @param teiidServer the TeiidServer
     * @param withDeployment 'true' if dialog is shown during deployment, 'false' if not
     * @throws Exception the exception
     */
    public static void doCreateDataSource(String vdbName, ITeiidServer teiidServer, boolean withDeployment) throws Exception {
    	final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();
    	
        VdbDataSourceInfo info = new VdbDataSourceInfo(vdbName, vdbName, vdbName, teiidServer);
        
        info.setPassword(teiidServer.getTeiidJdbcInfo().getPassword());
        info.setUsername(teiidServer.getTeiidJdbcInfo().getUsername());
        
        final CreateVdbDataSourceDialog dialog = new CreateVdbDataSourceDialog(iww.getShell(), info, teiidServer, withDeployment);

        final int rc = dialog.open();
        if (rc != Window.OK)
            return;

        String jndiName = JndiUtil.addJavaPrefix(info.getJndiName());
        
        // if datasource already exists, user has 'OKd' the dialog and elected to replace it
        ITeiidDataSource vdbDS = teiidServer.getDataSource(jndiName);
        if( vdbDS != null ) {
        	teiidServer.deleteDataSource(jndiName);
        }


        // creates the data source
        teiidServer.getOrCreateDataSource(info.getDisplayName(),
        								  jndiName,
                                          "teiid", //$NON-NLS-1$
                                          info.getProperties());
    }

    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return vdbSelected(selection);
    }

    private boolean vdbSelected( ISelection theSelection ) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelUtilities.isVdbFile((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}

