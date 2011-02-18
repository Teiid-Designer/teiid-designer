/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.internal.transformation.util.SqlAspectHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlEditorPanel;
import com.metamatrix.modeler.transformation.ui.util.TransformationUiResourceHelper;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * AddToSqlSelectAction This action adds the selected SqlColumn fullnames to the SQL SELECT clause. The action is currently
 * contributed by the TransformationObjectEditorPage, so that it only appears when the editor is displayed. The elements are added
 * to the editor at the current cursor location, or at the end of the SELECT clause if the cursor is not within the SELECT clause.
 */
public class AddToSqlSelectAction extends TransformationAction {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public AddToSqlSelectAction( EObject transformationEObject,
                                 Diagram diagram ) {
        super(transformationEObject, diagram);
        // setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.CLEAR_TRANSFORMATION));
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        super.selectionChanged(thePart, theSelection);

        // Enable the action based on the selection
        boolean enable = shouldEnable();
        setEnabled(enable);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        // Build lists of Column fullnames and corresponding Table fullnames,
        // from the selected EObjects
        ISelection theSelection = getSelection();
        List selectedEObjs = SelectionUtilities.getSelectedEObjects(theSelection);
        List elemNames = new ArrayList(selectedEObjs.size());
        List parentNames = new ArrayList(selectedEObjs.size());
        Iterator iter = selectedEObjs.iterator();
        while (iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            // Column fullname
            String elemFullName = TransformationHelper.getSqlEObjectFullName(eObj);
            // Table fullname
            EObject parentEObj = eObj.eContainer();
            String groupFullName = TransformationHelper.getSqlEObjectFullName(parentEObj);
            // Add the element and table names to the lists
            if (elemFullName != null && groupFullName != null) {
                elemNames.add(elemFullName);
                parentNames.add(groupFullName);
            }
        }

        //
        // Get the currently active ModelObjectEditorPage, ensure its TransformationObjEditorPage
        //
        IEditorPart editor = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().getActiveEditor();
        if (editor instanceof MultiPageModelEditor) {
            ModelObjectEditorPage moep = ((MultiPageModelEditor)editor).getActiveObjectEditor();
            if (moep != null && moep instanceof TransformationObjectEditorPage) {
                TransformationObjectEditorPage transOEP = (TransformationObjectEditorPage)moep;
                // TransformationObjectEditorPage
                // Get the sqlEditor
                SqlEditorPanel sqlEditor = transOEP.getCurrentSqlEditor();
                // If editor cursor is within the SELECT, add elements at the cursor, otherwise add to end
                if (sqlEditor.isCurrentCaretWithinSelect()) {
                    sqlEditor.insertElements(elemNames, parentNames, sqlEditor.getCaretOffset(), null);
                } else {
                    sqlEditor.insertElementsAtEndOfSelect(elemNames, parentNames, null);
                }
            }
        }
    }

    /* 
     * method to determine if this action should be enabled, based on the current selection
     * @param theSelection the current selection
     * @return 'true' if the action should be enabled, 'false' if not.
     */
    private boolean shouldEnable() {
        boolean enable = false;
        List selectedEObjs = SelectionUtilities.getSelectedEObjects(getSelection());
        // ----------------------------------------------
        // All of the selected items must be Columns
        // ----------------------------------------------
        if (!selectedEObjs.isEmpty() && !ModelObjectUtilities.isReadOnly(getTransformation()) && !isDependencyDiagram()
            && getTransformation() != null && TransformationUiResourceHelper.isSqlTransformationResource(getTransformation())
            && areEObjectsSelected()) {
            //
            // Get the currently active ModelObjectEditorPage, ensure its TransformationObjEditorPage
            // Transformation Editor must be open for this action to enable
            //
            boolean editorOpen = false;
            IEditorPart editor = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().getActiveEditor();
            if (editor instanceof MultiPageModelEditor) {
                ModelObjectEditorPage moep = ((MultiPageModelEditor)editor).getActiveObjectEditor();
                if (moep != null && moep instanceof TransformationObjectEditorPage) {
                    TransformationObjectEditorPage transOEP = (TransformationObjectEditorPage)moep;
                    // TransformationObjectEditorPage
                    // Get the sqlEditor
                    SqlEditorPanel sqlEditor = transOEP.getCurrentSqlEditor();
                    if (sqlEditor != null) editorOpen = true;
                }
            }
            if (editorOpen) {
                selectedEObjs = SelectionUtilities.getSelectedEObjects(getSelection());
                Iterator iter = selectedEObjs.iterator();
                // If any are not columns, disable and break
                while (iter.hasNext()) {
                    EObject eObj = (EObject)iter.next();
                    if (eObj != null) {
                        if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(eObj) && !SqlAspectHelper.isInputParameter(eObj)) {
                            if (((SqlTransformationMappingRoot)getTransformation()).getOutputs().contains(eObj.eContainer())) return false;
                            enable = true;
                        } else return false;
                    }
                }
            }
        }
        return enable;
    }

}
