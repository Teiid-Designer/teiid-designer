/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.actions;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.vdb.VdbConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.viewsupport.ClosedProjectFilter;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceDialog;
import org.teiid.designer.ui.viewsupport.SingleProjectFilter;

/**
 * @since 8.0
 */
public class EditVdbAction  extends Action implements VdbConstants {
	private static final String PREFIX = I18nUtil.getPropertyPrefix(EditVdbAction.class);
	
	
	Properties designerProperties;
	
    /**
     * @since 5.0
     */
    public EditVdbAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.EDIT_VDB));
        setToolTipText(DqpUiConstants.UTIL.getString(PREFIX + "tooltip")); //$NON-NLS-1$
    }
    
    /**
     * @since 5.0
     */
    public EditVdbAction(Properties properties) {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.EDIT_VDB));
        setToolTipText(DqpUiConstants.UTIL.getString(PREFIX + "tooltip")); //$NON-NLS-1$
        designerProperties = properties;
    }

    @Override
	public void run() {
    	IResource theVdb = DesignerPropertiesUtil.getVDB(designerProperties);
    	IFile selectedVdb = null;
    	
    	if( theVdb != null ) {
    		selectedVdb = (IFile)theVdb;
    	}
    	
    	if( selectedVdb == null ) {
			ModelWorkspaceDialog vdbDialog = getSelectVdbDialog();
	
			// add filters
			vdbDialog.addFilter(new ClosedProjectFilter());
	
			vdbDialog.open();
	
			if (vdbDialog.getReturnCode() == Window.OK) {
				Object[] selections = vdbDialog.getResult();
				// should be single selection
				selectedVdb = (IFile) selections[0];
			}
    	}
		
		if( selectedVdb != null ) {
			try {
				IDE.openEditor(UiUtil.getWorkbenchPage(), selectedVdb, true);
			} catch (PartInitException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
    }

    
    private ModelWorkspaceDialog getSelectVdbDialog() {

		ModelWorkspaceDialog selectVdbDialog = new ModelWorkspaceDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(),
				null, new ModelExplorerLabelProvider(),
				new ModelExplorerContentProvider());
		// add filters
		selectVdbDialog.addFilter(new ClosedProjectFilter());
		selectVdbDialog.addFilter(new SingleProjectFilter(this.designerProperties));

		String title = DqpUiConstants.UTIL.getString(PREFIX + "selectionDialog.title"); //$NON-NLS-1$
		String message = DqpUiConstants.UTIL.getString(PREFIX + "selectionDialog.message"); //$NON-NLS-1$
		selectVdbDialog.setTitle(title);
		selectVdbDialog.setMessage(message);
		selectVdbDialog.setAllowMultiple(false);

		selectVdbDialog.setInput(ModelerCore.getWorkspace().getRoot());

		selectVdbDialog.setValidator(new ISelectionStatusValidator() {
			@Override
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

		return selectVdbDialog;
	}
}
