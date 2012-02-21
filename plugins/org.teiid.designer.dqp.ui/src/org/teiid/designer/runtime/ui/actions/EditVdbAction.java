/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.teiid.designer.runtime.ui.vdb.VdbConstants;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

public class EditVdbAction  extends Action implements VdbConstants {
	private static final String PREFIX = I18nUtil.getPropertyPrefix(EditVdbAction.class);
	
    /**
     * @since 5.0
     */
    public EditVdbAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.EDIT_VDB));
        setToolTipText(DqpUiConstants.UTIL.getString(PREFIX + "tooltip")); //$NON-NLS-1$
    }

    @Override
	public void run() {
		ModelWorkspaceDialog vdbDialog = getSelectVdbDialog();

		// add filters
		((ModelWorkspaceDialog) vdbDialog).addFilter(new ClosedProjectFilter());

		vdbDialog.open();

		if (vdbDialog.getReturnCode() == Window.OK) {
			Object[] selections = vdbDialog.getResult();
			// should be single selection
			IFile selectedVdb = (IFile) selections[0];
			
			try {
				IDE.openEditor(UiUtil.getWorkbenchPage(), selectedVdb, true);
			} catch (PartInitException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			//ModelEditorManager.getModelEditorForFile(selectedVdb, true);
		}
    }

    
    private ModelWorkspaceDialog getSelectVdbDialog() {

		ModelWorkspaceDialog result = new ModelWorkspaceDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(),
				null, new ModelExplorerLabelProvider(),
				new ModelExplorerContentProvider());

		String title = DqpUiConstants.UTIL.getString(PREFIX + "selectionDialog.title"); //$NON-NLS-1$
		String message = DqpUiConstants.UTIL.getString(PREFIX + "selectionDialog.message"); //$NON-NLS-1$
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ResourcesPlugin.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				if (selection != null
						&& selection.length == 1 ) {
		            if (selection[0] instanceof IFile) {
		                String extension = ((IFile)selection[0]).getFileExtension();
		                if (extension != null && extension.equals(VDB_EXTENSION)) {
		                	return new StatusInfo(DqpUiConstants.PLUGIN_ID);
		                }
		            }
				}
				if( selection == null || selection.length == 0 ) {
					return new StatusInfo(DqpUiConstants.PLUGIN_ID, IStatus.ERROR, DqpUiConstants.UTIL.getString(PREFIX + "selectionDialog.emptySelection")); //$NON-NLS-1$
				}
				String msg = DqpUiConstants.UTIL.getString(PREFIX + "selectionDialog.invalidSelection"); //$NON-NLS-1$
				return new StatusInfo(DqpUiConstants.PLUGIN_ID, IStatus.ERROR, msg);
			}
		});

		return result;
	}
}
