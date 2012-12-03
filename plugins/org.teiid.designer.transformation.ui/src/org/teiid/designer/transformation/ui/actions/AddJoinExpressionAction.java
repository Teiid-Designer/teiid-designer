/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInto;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.editors.TransformationObjectEditorPage;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlEditorPanel;
import org.teiid.designer.transformation.util.SqlAspectHelper;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelObjectEditorPage;
import org.teiid.designer.ui.editors.MultiPageModelEditor;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;

/**
 * AddJoinExpressionAction This action will create an inner join on the two selected columns. The action is currently contributed
 * by the TransformationObjectEditorPage, so that it only appears when the editor is displayed.
 *
 * @since 8.0
 */
public class AddJoinExpressionAction extends TransformationAction {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public AddJoinExpressionAction( EObject transformationEObject,
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
        ISelection theSelection = getSelection();
        List selectedEObjs = SelectionUtilities.getSelectedEObjects(theSelection);
        Object selection1 = selectedEObjs.get(0);
        Object selection2 = selectedEObjs.get(1);
        if (selection1 != null && selection2 != null && selection1 instanceof EObject && selection2 instanceof EObject) {
            EObject col1 = (EObject)selection1;
            EObject col2 = (EObject)selection2;
            // New clause from selected columns
            String joinClause = buildJoinFromClause(col1, col2);

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
                    ICommand command = sqlEditor.getCommand();
                    if (command instanceof IQuery) {
                        IQuery query = (IQuery)command;
                        ISelect select = query.getSelect();
                        String newQuery = "SELECT " + select.toString() + joinClause; //$NON-NLS-1$
                        sqlEditor.setText(newQuery);
                    }
                }
            }
        }
    }

    /* 
     * this method builds the inner join string, given the column selections
     */
    private String buildJoinFromClause( EObject col1,
                                        EObject col2 ) {
        EObject col1Parent = col1.eContainer();
        EObject col2Parent = col2.eContainer();

        // Object fullnames
        String col1Name = TransformationHelper.getSqlEObjectFullName(col1);
        String col1ParentName = TransformationHelper.getSqlEObjectFullName(col1Parent);
        String col2Name = TransformationHelper.getSqlEObjectFullName(col2);
        String col2ParentName = TransformationHelper.getSqlEObjectFullName(col2Parent);

        StringBuffer sb = new StringBuffer(" FROM "); //$NON-NLS-1$
        sb.append(col1ParentName);
        sb.append(" INNER JOIN "); //$NON-NLS-1$
        sb.append(col2ParentName);
        sb.append(" ON "); //$NON-NLS-1$
        sb.append(col1Name);
        sb.append(" = "); //$NON-NLS-1$
        sb.append(col2Name);

        return sb.toString();
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
        // Two columns must be selected
        // ----------------------------------------------
        if (!selectedEObjs.isEmpty() && selectedEObjs.size() == 2 && !ModelObjectUtilities.isReadOnly(getTransformation())
            && !isDependencyDiagram() && getTransformation() != null && areEObjectsSelected()) {
            //
            // Get the currently active ModelObjectEditorPage, ensure its TransformationObjEditorPage
            // Transformation Editor must be open for this action to enable
            //
            boolean editorOpen = false;
            SqlEditorPanel sqlEditor = null;
            IEditorPart editor = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().getActiveEditor();
            if (editor instanceof MultiPageModelEditor) {
                ModelObjectEditorPage moep = ((MultiPageModelEditor)editor).getActiveObjectEditor();
                if (moep instanceof TransformationObjectEditorPage) {
                    TransformationObjectEditorPage transOEP = (TransformationObjectEditorPage)moep;
                    // TransformationObjectEditorPage
                    // Get the sqlEditor
                    sqlEditor = transOEP.getCurrentSqlEditor();
                    if (sqlEditor != null) editorOpen = true;
                }
            }
            // Check Editor open and is Simple Query
            if (editorOpen && isSimpleQuery(sqlEditor)) {
                // Selected Eobjects
                Object selection1 = selectedEObjs.get(0);
                Object selection2 = selectedEObjs.get(1);
                if (selection1 instanceof EObject && selection2 instanceof EObject) {
                    EObject eObj1 = (EObject)selection1;
                    EObject eObj2 = (EObject)selection2;
                    EObject eObj1Parent = eObj1.eContainer();
                    EObject eObj2Parent = eObj2.eContainer();
                    // Both selections must be non null columns
                    if (eObj1Parent != null && eObj2Parent != null && org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(eObj1)
                        && org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(eObj2) && !SqlAspectHelper.isInputParameter(eObj1)
                        && !SqlAspectHelper.isInputParameter(eObj2)) {

                        // Dont enable if any output columns are selected
                        List transOutputs = ((SqlTransformationMappingRoot)getTransformation()).getOutputs();
                        if (transOutputs.contains(eObj1Parent) || transOutputs.contains(eObj2Parent)) {
                            return false;
                        }

                        if (eObj1Parent.equals(eObj2Parent)) {
                            return false;
                        }
                        enable = true;
                    }
                }
            }
        }
        return enable;
    }

    // This action can currently only work on simple two-source queries. This means that the
    // query must be of the form "SELECT xxx FROM Source1, Source2" without any other clauses.
    // Future enhancement would be to support updating more complex queries.
    private boolean isSimpleQuery( SqlEditorPanel sqlEditor ) {
        boolean isSimpleQuery = false;

        ICommand command = sqlEditor.getCommand();
        if (command != null && command instanceof IQuery) {
            // Get all of the query components
            IQuery query = (IQuery)command;
            ISelect select = query.getSelect();
            IFrom from = query.getFrom();
            ICriteria criteria = query.getCriteria();
            IGroupBy groupBy = query.getGroupBy();
            ICriteria having = query.getHaving();
            IInto into = query.getInto();
            IOption option = query.getOption();
            IOrderBy orderBy = query.getOrderBy();
            // Select and From must be non-null. All others must be null
            if (select != null && from != null && criteria == null && groupBy == null && having == null && into == null
                && option == null && orderBy == null) {
                // All FROM clauses must be UnaryFromClause. And there can only be two.
                List fromClauses = from.getClauses();
                if (fromClauses.size() == 2) {
                    boolean allUnary = true;
                    Iterator fromIter = fromClauses.iterator();
                    while (fromIter.hasNext()) {
                        IFromClause fromClause = (IFromClause)fromIter.next();
                        if (!(fromClause instanceof IUnaryFromClause)) {
                            allUnary = false;
                            break;
                        }
                    }
                    if (allUnary) isSimpleQuery = true;
                }
            }
        }

        return isSimpleQuery;
    }

}
