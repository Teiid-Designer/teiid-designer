/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.actions;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.dqp.webservice.war.ui.wizards.WarDeploymentInfoDialog;
import org.teiid.designer.vdb.VdbUtil;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class GenerateWarAction extends Action implements ISelectionListener,
		Comparable, ISelectionAction {
	protected static final String I18N_PREFIX = I18nUtil
			.getPropertyPrefix(GenerateWarAction.class);
	protected static final String VDB_EXTENSION = "vdb"; //$NON-NLS-1$

	protected boolean successfulRefresh = false;

	IFile selectedVDB;
//	ArrayList<ModelResource> wsModelResources = new ArrayList<ModelResource>();
//	Vdb vdb;
	boolean contextIsLocal = false;

	public GenerateWarAction() {
		this.setText(DqpUiConstants.UTIL.getString(I18N_PREFIX + "text")); //$NON-NLS-1$
		this.setToolTipText(DqpUiConstants.UTIL.getString(I18N_PREFIX
				+ "tooltip")); //$NON-NLS-1$
		this.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(
				DqpUiConstants.Images.CREATE_WAR));
		setDisabledImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(
				DqpUiConstants.Images.CREATE_WAR));
		setEnabled(false);
	}

	public int compareTo(Object o) {
		if (o instanceof String) {
			return getText().compareTo((String) o);
		}

		if (o instanceof Action) {
			return getText().compareTo(((Action) o).getText());
		}
		return 0;
	}

	/**
	 * @param selection
	 * @return
	 */
	public boolean isApplicable(ISelection selection) {
		boolean result = false;
		if (!SelectionUtilities.isMultiSelection(selection)) {
			Object obj = SelectionUtilities.getSelectedObject(selection);
			if (obj instanceof IFile) {
				String extension = ((IFile) obj).getFileExtension();
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

		final IWorkbenchWindow window = DqpUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		boolean cont = true;
		if (compiler == null) {
			cont = MessageDialog.openConfirm(window.getShell(),
					DqpUiPlugin.UTIL.getString(I18N_PREFIX + "javaWarningTitle"), //$NON-NLS-1$
					DqpUiPlugin.UTIL.getString(I18N_PREFIX
							+ "invalidJDKMessage")); //$NON-NLS-1$
		}

		if (!cont) {
			notifyResult(false);
			return;
		}

		WarDeploymentInfoDialog dialog = new WarDeploymentInfoDialog(window.getShell(), this.selectedVDB, null);

		int rc = dialog.open();

		// Retrieve the file name for the confirmation dialog
		String warFileName = dialog.getWarFileName();

		final String successMessage = DqpUiPlugin.UTIL.getString(I18N_PREFIX + "warFileCreated", warFileName); //$NON-NLS-1$

		boolean wasSuccessful = (rc == Window.OK);
		if (wasSuccessful) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(window.getShell(),
							DqpUiPlugin.UTIL.getString(I18N_PREFIX + "creationCompleteTitle"),//$NON-NLS-1$ 
							successMessage);
				}
			});
		} else {
			if (rc != Window.CANCEL) {

				MessageDialog.openError(window.getShell(), DqpUiPlugin.UTIL.getString(I18N_PREFIX + "creationFailedTitle"),//$NON-NLS-1$ 
						dialog.getMessage());
			}
		}
		notifyResult(rc == Window.OK);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		boolean enable = false;
		if (!SelectionUtilities.isMultiSelection(selection)) {
			Object obj = SelectionUtilities.getSelectedObject(selection);
			// If a VDB is selected and it contains a web service model then
			// enable
			if (obj instanceof IFile) {
				String extension = ((IFile) obj).getFileExtension();
				if (extension != null && extension.equals(VDB_EXTENSION)) {
					this.selectedVDB = (IFile) obj;
					
					if( VdbUtil.hasModelClass(this.selectedVDB, ModelUtil.MODEL_CLASS_WEB_SERVICE, ModelType.VIRTUAL_LITERAL.getLiteral())) {
						enable = true;
					}
				}
			}
		}
		setEnabled(enable);
	}
}
