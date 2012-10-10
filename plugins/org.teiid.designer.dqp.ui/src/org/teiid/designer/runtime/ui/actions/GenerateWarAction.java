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
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.wizards.webservices.WarDeploymentInfoDialog;
import org.teiid.designer.ui.actions.ISelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.vdb.VdbUtil;


/**
 * @since 8.0
 */
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

	@Override
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
	@Override
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
					DqpUiConstants.UTIL.getString(I18N_PREFIX + "javaWarningTitle"), //$NON-NLS-1$
					DqpUiConstants.UTIL.getString(I18N_PREFIX
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

		final String successMessage = DqpUiConstants.UTIL.getString(I18N_PREFIX + "warFileCreated", warFileName); //$NON-NLS-1$

		boolean wasSuccessful = (rc == Window.OK);
		if (wasSuccessful) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openInformation(window.getShell(),
							DqpUiConstants.UTIL.getString(I18N_PREFIX + "creationCompleteTitle"),//$NON-NLS-1$ 
							successMessage);
				}
			});
		} else {
			if (rc != Window.CANCEL) {

				MessageDialog.openError(window.getShell(), DqpUiConstants.UTIL.getString(I18N_PREFIX + "creationFailedTitle"),//$NON-NLS-1$ 
						dialog.getMessage());
			}
		}
		notifyResult(rc == Window.OK);
	}

	@Override
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
