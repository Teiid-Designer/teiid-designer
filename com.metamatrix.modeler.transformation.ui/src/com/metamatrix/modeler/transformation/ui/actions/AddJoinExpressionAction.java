/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.transformation.ui.actions;

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
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.From;
import com.metamatrix.query.sql.lang.FromClause;
import com.metamatrix.query.sql.lang.GroupBy;
import com.metamatrix.query.sql.lang.Into;
import com.metamatrix.query.sql.lang.Option;
import com.metamatrix.query.sql.lang.OrderBy;
import com.metamatrix.query.sql.lang.Query;
import com.metamatrix.query.sql.lang.Select;
import com.metamatrix.query.sql.lang.UnaryFromClause;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * AddJoinExpressionAction This action will create an inner join on the two selected columns. The action is currently contributed
 * by the TransformationObjectEditorPage, so that it only appears when the editor is displayed.
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
                    Command command = sqlEditor.getCommand();
                    if (command instanceof Query) {
                        Query query = (Query)command;
                        Select select = query.getSelect();
                        String newQuery = select.toString() + joinClause;
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
                    if (eObj1Parent != null && eObj2Parent != null && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(eObj1)
                        && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(eObj2) && !SqlAspectHelper.isInputParameter(eObj1)
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

        Command command = sqlEditor.getCommand();
        if (command != null && command instanceof Query) {
            // Get all of the query components
            Query query = (Query)command;
            Select select = query.getSelect();
            From from = query.getFrom();
            Criteria criteria = query.getCriteria();
            GroupBy groupBy = query.getGroupBy();
            Criteria having = query.getHaving();
            Into into = query.getInto();
            Option option = query.getOption();
            OrderBy orderBy = query.getOrderBy();
            // Select and From must be non-null. All others must be null
            if (select != null && from != null && criteria == null && groupBy == null && having == null && into == null
                && option == null && orderBy == null) {
                // All FROM clauses must be UnaryFromClause. And there can only be two.
                List fromClauses = from.getClauses();
                if (fromClauses.size() == 2) {
                    boolean allUnary = true;
                    Iterator fromIter = fromClauses.iterator();
                    while (fromIter.hasNext()) {
                        FromClause fromClause = (FromClause)fromIter.next();
                        if (!(fromClause instanceof UnaryFromClause)) {
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
