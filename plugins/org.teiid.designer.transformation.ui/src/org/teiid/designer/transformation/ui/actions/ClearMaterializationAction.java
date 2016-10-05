package org.teiid.designer.transformation.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;

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
			Table table = (Table)(SelectionUtilities.getSelectedEObject(getSelection()));
			table.setMaterialized(false);
			table.setMaterializedTable(null);
			succeeded = true;
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
}