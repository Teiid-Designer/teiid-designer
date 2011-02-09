/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.adminapi.VDB;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.vdb.Vdb;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class DeployVdbAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    protected static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(DeployVdbAction.class);
    protected static final String VDB_EXTENSION = "vdb"; //$NON-NLS-1$

    protected boolean successfulRefresh = false;
    private static VDB deployedVDB = null;
    private static String vdbName = null;
    
    Collection<IFile> selectedVDBs;
    Vdb vdb;
    boolean contextIsLocal = false;

    public DeployVdbAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.DEPLOY_VDB));
    }

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
    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        
        List objs = SelectionUtilities.getSelectedObjects(selection);
        Iterator iter = objs.iterator();
        
        while( iter.hasNext() ) {
        	Object next = iter.next();
            if (next instanceof IFile) {
                String extension = ((IFile)next).getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                    result = true;
                }
            }
            if (!result ) {
            	break;
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {
        Server server = DqpPlugin.getInstance().getServerManager().getDefaultServer();

        for( IFile nextVDB : this.selectedVDBs ) {
	        boolean doDeploy = VdbRequiresSaveChecker.insureOpenVdbSaved(nextVDB);
	        if( doDeploy ) {
	        	deployVdb(server, nextVDB);
	        }
        }
    }

    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        boolean enable = false;
        List objs = SelectionUtilities.getSelectedObjects(selection);
        Iterator iter = objs.iterator();
        
        this.selectedVDBs = new ArrayList<IFile>();
        
        while( iter.hasNext() ) {
        	Object next = iter.next();
            if (next instanceof IFile) {
                String extension = ((IFile)next).getFileExtension();
                if (extension != null && extension.equals(VDB_EXTENSION)) {
                    this.selectedVDBs.add((IFile)next);
                    enable = true;
                }
            }
            if( !enable ) {
            	break;
            }
    	}
        if( !enable ) {
        	this.selectedVDBs.clear();
        }
        setEnabled(enable);
    }

    public static VDB deployVdb( final Server server,
                                 final Object vdbOrVdbFile ) {

        if (server == null) {
        	MessageDialog.openWarning(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                    DqpUiConstants.UTIL.getString("DeployVdbAction.noTeiidInstance.title"), //$NON-NLS-1$
                    DqpUiConstants.UTIL.getString("DeployVdbAction.noTeiidInstance.message")); //$NON-NLS-1$
        	return null;
        } else if( !server.isConnected() ) {
        	MessageDialog.openWarning(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                    DqpUiConstants.UTIL.getString("DeployVdbAction.teiidNotConnected.title"), //$NON-NLS-1$
                    DqpUiConstants.UTIL.getString("DeployVdbAction.teiidNotConnected.message", server)); //$NON-NLS-1$
        	return null;
    	}

        BusyIndicator.showWhile(null, new Runnable() {
			
			@Override
			public void run() {
				
		        try {
					if (vdbOrVdbFile instanceof IFile) {
					    deployedVDB = server.getAdmin().deployVdb((IFile)vdbOrVdbFile);
					    vdbName = ((IFile)vdbOrVdbFile).getName();
					} else {
					    deployedVDB = server.getAdmin().deployVdb((Vdb)vdbOrVdbFile);
					    vdbName = ((Vdb)vdbOrVdbFile).getName().toString();
					}

					if (deployedVDB == null) {
					    MessageDialog.openError(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
					                            DqpUiConstants.UTIL.getString("DeployVdbAction.vdbNotDeployedTitle"), //$NON-NLS-1$
					                            DqpUiConstants.UTIL.getString("DeployVdbAction.vdbNotDeployedMessage", vdbName)); //$NON-NLS-1$
					} else if (deployedVDB.getStatus().equals(VDB.Status.INACTIVE)) {
					    StringBuilder message = new StringBuilder(
					                                              DqpUiConstants.UTIL.getString("DeployVdbAction.vdbNotActiveMessage", deployedVDB.getName())); //$NON-NLS-1$
					    for (String error : deployedVDB.getValidityErrors()) {
					        message.append("\n\nERROR:\t").append(error); //$NON-NLS-1$
					    }
					    MessageDialog.openWarning(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
					                              DqpUiConstants.UTIL.getString("DeployVdbAction.vdbNotActiveTitle"), //$NON-NLS-1$
					                              message.toString());
					}
				} catch (Exception e) {
					DqpUiConstants.UTIL.log(IStatus.ERROR,
                            e,
                            DqpUiConstants.UTIL.getString("DeployVdbAction.problemDeployingVdbToServer", //$NON-NLS-1$
                            							  vdbName,
                                                          server));
				}
			}
		});


        return deployedVDB;
    }
 
}
