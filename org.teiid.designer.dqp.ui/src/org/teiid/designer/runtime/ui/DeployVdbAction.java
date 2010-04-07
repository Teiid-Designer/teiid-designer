/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
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

    IFile selectedVDB;
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
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
                    result = true;
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
        Server server = DqpPlugin.getInstance().getServerManager().getDefaultServer();

        if (server != null) {
            try {
                // server.getAdmin().deployVdb(selectedVDB);
                MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "VDB Deployed", "VDB: "
                                                                                                     + selectedVDB.getName()
                                                                                                     + " is Deployed on server: "
                                                                                                     + server.getUrl());
            } catch (Exception e) {
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, DqpPlugin.Util.getString("DeployVdbAction.problemDeployingVdbToServer", //$NON-NLS-1$
                                                                                   selectedVDB.getName(),
                                                                                   server.getUrl()));
            }
        }

    }

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
        setEnabled(enable);
    }
}
