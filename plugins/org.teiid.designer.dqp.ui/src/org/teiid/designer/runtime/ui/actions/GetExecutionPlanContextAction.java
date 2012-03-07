/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.teiid.designer.runtime.ui.preview.PreviewDataWorker;
import org.teiid.designer.runtime.ui.preview.PreviewTableDataContextAction;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * Get SQL Execution Plan Action
 */
public class GetExecutionPlanContextAction extends SortableSelectionAction {
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(GetExecutionPlanContextAction.class);
	
    static String getString( String key ) {
        return DqpUiConstants.UTIL.getString(THIS_CLASS + key);
    }

    static String getString( final String key, final Object param ) {
        return DqpUiConstants.UTIL.getString(THIS_CLASS + key, param);
    }
    
    static String getString( final String key, final Object param, final Object param2 ) {
        return DqpUiConstants.UTIL.getString(THIS_CLASS + key, param, param2);
    }
    
    PreviewDataWorker worker;
    
    /**
     * @since 5.0
     */
    public GetExecutionPlanContextAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.PREVIEW_DATA_ICON));
        setWiredForSelection(true);
        setToolTipText(getString("tooltip")); //$NON-NLS-1$
        worker = new PreviewDataWorker();
    }

    /**
     * This method was created to allow the transformation.ui plugin, and TransformationObjectEditorPage to get it's own instance
     * of this action so it can allow preview of the specific edited virtual table or procedure. This allows the original action
     * to remain workspace selection driven. Override abstract method
     * 
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#getClone()
     * @since 5.0
     */
    @Override
    public SortableSelectionAction getClone() {
        return new PreviewTableDataContextAction();
    }

    /**
     *
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return isValidSelection(selection);
    }

    /**
     * Valid selections include Relational Tables, Procedures or Relational Models. The roots instance variable will populated
     * with all Tables and Procedures contained within the current selection.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( ISelection selection ) {
        setToolTipText(getString("tooltip")); //$NON-NLS-1$
        setText(getString("tooltip")); //$NON-NLS-1$

        // must have one and only one EObject selected
        EObject eObj = SelectionUtilities.getSelectedEObject(selection);
        if (eObj == null) return false;
        
        return worker.isPreviewableEObject(eObj);

    }


    @Override
    public void run() {

        EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
        if (eObj != null) {
            if (worker.isPreviewPossible()) {
                worker.run(eObj, true);
            }
        }
    }

    /**
     * Show the specified results in the results view.
     * 
     * @param theResults the results being displayed
     * @since 5.5.3
     */
    @SuppressWarnings( "unused" )
    private void showResults( final IResults theResults ) {
        // REPLACE
    }
    
}
