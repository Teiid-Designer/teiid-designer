package org.teiid.designer.transformation.ui.actions;

import static org.teiid.designer.ui.PluginConstants.Prefs.General.AUTO_OPEN_EDITOR_IF_NEEDED;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;
import org.teiid.designer.transformation.reverseeng.ReverseEngConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class ClearMaterializationAction extends SortableSelectionAction {

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ClearMaterializationAction.class);
	
    public ClearMaterializationAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
        		org.teiid.designer.transformation.ui.PluginConstants.Images.CREATE_MATERIALIZED_VIEWS_ICON));
    }
    
    /**
     * 
     */
    @Override
    public boolean isApplicable( final ISelection selection ) {
        return isValidSelection(selection);
    }
    
    /**
     * Valid selections include only Virtual Relational Tables.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( final ISelection selection ) {
        boolean isValid = true;
        if (SelectionUtilities.isEmptySelection(selection) || 
        		!SelectionUtilities.isAllEObjects(selection) ||
        		SelectionUtilities.getSelectedEObjects(selection).size() > 1) isValid = false;

        if (isValid) {
        	final EObject eObject = SelectionUtilities.getSelectedEObject(selection);
            
            if ( eObject instanceof Table ) {
            	isValid = ((Table)eObject).isMaterialized();
            } else isValid = false;

        } else isValid = false;

        return isValid;
    }
    
    @Override
    public void run() {

        
		boolean requiredStart = ModelerCore.startTxn(false, false, "Clear Materialization", this); //$NON-NLS-1$
		boolean succeeded = false;
		try {
			if( forceEditorOpen() ) {
				Table table = (Table)(SelectionUtilities.getSelectedEObject(getSelection()));
				table.setMaterialized(false);
				table.setMaterializedTable(null);
				
				// Clear extension properties
	            final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
	            ModelObjectExtensionAssistant assistant =  (ModelObjectExtensionAssistant)registry.getModelExtensionAssistant(ReverseEngConstants.RELATIONAL_EXT_ASSISTANT_NS);
	            assistant.removeProperty(table, RelationalModelExtensionConstants.PropertyIds.MATVIEW_AFTER_LOAD_SCRIPT);
	            assistant.removeProperty(table, RelationalModelExtensionConstants.PropertyIds.MATVIEW_BEFORE_LOAD_SCRIPT);
	            assistant.removeProperty(table, RelationalModelExtensionConstants.PropertyIds.MATERIALIZED_STAGE_TABLE);
				succeeded = true;
			}
		} catch (Exception ex) {
			UiConstants.Util.log(IStatus.ERROR, ex,
					UiPlugin.getDefault().getString(I18N_PREFIX, "errorClearingMaterializedProperties")); //$NON-NLS-1$
		} finally {
			// if we started the txn, commit it.
			if (requiredStart) {
				if (succeeded) {
					ModelerCore.commitTxn();
				} else {
					ModelerCore.rollbackTxn();
				}
			}
		}
    }
    
    private boolean forceEditorOpen() throws ModelWorkspaceException {
    	final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
    	ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObject);
        if ( ! ModelEditorManager.isOpen((IFile)mr.getCorrespondingResource()) ) {
            // get preference value for auto-open-editor
            String autoOpen = UiPlugin.getDefault().getPreferenceStore().getString(AUTO_OPEN_EDITOR_IF_NEEDED);

            // if the preference is to auto-open, then set forceOpen so we don't prompt the user
            boolean forceOpen = MessageDialogWithToggle.ALWAYS.equals(autoOpen);

            // If no preference, prompt the user
            if (!forceOpen) {
                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                forceOpen = ModelEditorManager.showDialogShouldOpenEditor(shell);
            }
            
            if(forceOpen) {
                ModelEditorManager.activate((IFile)mr.getCorrespondingResource(), true);
                return true;
            } else {
            	return false;
            }
        }
        
        return true;
    }
}