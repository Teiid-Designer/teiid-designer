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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.TransformationObjectEditorPage;
import com.metamatrix.modeler.transformation.ui.util.SqlAliasAccumulatorSource;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.query.internal.ui.tree.AddGroupSelectionValidator;
import com.metamatrix.query.internal.ui.tree.QueryTreeSelectionDialog;
import com.metamatrix.query.sql.lang.SetQuery;
import com.metamatrix.ui.internal.widget.ListMessageDialog;
import com.metamatrix.ui.internal.widget.accumulator.AccumulatorDialog;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource;

/**
 * TransformationSourceManager is a class of static utility methods for easily determining if objects can be added or removed as
 * source to a transformation as well as whether or not the transformation can be cleared.
 */
public class TransformationSourceManager implements UiConstants {
    private static final String ALIAS_ENTRY_DIALOG_TITLE = UiConstants.Util.getString("TransformationSourceManager.aliasEntryDialogTitle"); //$NON-NLS-1$

    /**
     * Method to determine if a source EObject can be added to a transformation. As long as the sourceEObject is valid, it can be
     * added, since aliases are allowed.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObject the source EObject to be added
     * @return 'true' if the source can be added 'false' if not.
     */
    public static boolean canAdd( final EObject transformationEObject,
                                  final EObject sourceEObject,
                                  final Object source ) {
        boolean canAdd = false;

        if (!ModelObjectUtilities.isReadOnly(transformationEObject)
            && transformationEObject instanceof SqlTransformationMappingRoot && sourceEObject != null
            && !TransformationHelper.isMappingClass(sourceEObject)
            && TransformationHelper.isValidSource(transformationEObject, sourceEObject)
            && !TransformationSourceManager.isInvalidStagingTable(sourceEObject, source)) {
            // let's check one more thing. We can't add the Target as a source.
            if (!sourceEObject.equals(TransformationHelper.getTransformationLinkTarget(transformationEObject))) canAdd = true;

        }

        return canAdd;
    }

    /**
     * (jh defect 19361) Method to determine if an EObject is a Procedure with a parameter
     * 
     * @param sourceEObject the source EObject to be added
     * @return 'true' if the source is a Procedure with a parameter.
     */
    public static boolean isProcedureWithParm( final EObject sourceEObject ) {
        boolean bResult = false;

        if (TransformationHelper.isSqlProcedure(sourceEObject)) {
            List lstParms = TransformationHelper.getInAndInoutParameters(sourceEObject);
            if (lstParms != null && lstParms.size() > 0) {
                bResult = true;
            }
        }
        return bResult;
    }

    /**
     * Method to determine if a list of source EObjects can be added to a transformation. As long as any of the sourceEObjects is
     * valid, it can be added, since aliases are allowed.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObjects the list of source EObjects to be added
     * @return 'true' if the source can be added 'false' if not.
     */
    public static boolean canAdd( final EObject transformationEObject,
                                  final List sourceEObjects,
                                  final Object source ) {
        boolean canAdd = false;

        if (!ModelObjectUtilities.isReadOnly(transformationEObject)
            && transformationEObject instanceof SqlTransformationMappingRoot && sourceEObjects != null
            && !sourceEObjects.isEmpty()) {

            // let's check one more thing. We can't add the Target as a source.
            Iterator iter = sourceEObjects.iterator();
            EObject nextSource = null;
            canAdd = true;

            while (iter.hasNext() && canAdd) {
                nextSource = (EObject)iter.next();
                canAdd = canAdd(transformationEObject, nextSource, source);
            }
        }

        return canAdd;
    }

    /**
     * Method to determine if a source EObject can be added as a union source to a transformation.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObject the source EObject to be added as a union
     * @return 'true' if the source can be added as union source, 'false' if not.
     */
    public static boolean canAddUnionSource( final EObject transformationEObject,
                                             final EObject sourceEObject,
                                             final Object source ) {
        boolean canAdd = false;

        if (!ModelObjectUtilities.isReadOnly(transformationEObject)
            && transformationEObject instanceof SqlTransformationMappingRoot && sourceEObject != null
            && TransformationHelper.isValidSource(transformationEObject, sourceEObject)
            && !TransformationSourceManager.isInvalidStagingTable(sourceEObject, source)
            && !TransformationHelper.isMappingClass(sourceEObject)) {
            // Additional Checks
            // Is the source being added the transformation target
            boolean sourceIsTarget = sourceEObject.equals(TransformationHelper.getTransformationLinkTarget(transformationEObject));
            // Is the current transformation an empty sql or initial "SELECT * FROM"
            boolean isInitialOrEmptySql = TransformationHelper.isEmptySelect(transformationEObject);
            // Can add if source is not the target, and not an initial or empty SQL statement
            if (!sourceIsTarget && !isInitialOrEmptySql) {
                canAdd = true;
            }
        }

        return canAdd;
    }

    /**
     * Method to determine if a list of source EObjects can be added to a transformation. As long as any of the sourceEObjects is
     * valid, it can be added, since aliases are allowed.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObjects the list of source EObjects to be added
     * @return 'true' if the source can be added 'false' if not.
     */
    public static boolean canAddUnionSource( final EObject transformationEObject,
                                             final List sourceEObjects,
                                             final Object source ) {
        boolean canAdd = false;

        if (!ModelObjectUtilities.isReadOnly(transformationEObject)
            && transformationEObject instanceof SqlTransformationMappingRoot && sourceEObjects != null
            && !sourceEObjects.isEmpty()) {

            // Additional Check
            // None of the sources can be the transformation target
            Iterator iter = sourceEObjects.iterator();
            EObject linkTarget = TransformationHelper.getTransformationLinkTarget(transformationEObject);
            boolean sourceIsTarget = false;
            boolean invalidST = false;

            // jh defect 20943: added check for MappingClassColumn as source in order to
            // FAIL this test when user is trying to add a link
            boolean invalidSource = false;

            while (iter.hasNext() && !sourceIsTarget && !invalidST && !invalidSource) {
                EObject sourceEObj = (EObject)iter.next();
                invalidST = TransformationSourceManager.isInvalidStagingTable(sourceEObj, source)
                            || TransformationHelper.isMappingClass(sourceEObj)
                            || !TransformationHelper.isValidSource(transformationEObject, sourceEObj);
                sourceIsTarget = sourceEObj.equals(linkTarget);

                if (sourceEObj instanceof MappingClassColumn) {
                    invalidSource = true;
                }
            }

            if (!sourceIsTarget && !invalidST && !invalidSource) {
                canAdd = true;
            }
        }

        return canAdd;
    }

    /**
     * Method to determine if a source EObject can be removed from a transformation. As long the sourceEObject is currently in the
     * transformation, it can be removed.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObject the source EObject to be removed.
     * @return 'true' if the source can be removed 'false' if not.
     */
    public static boolean canRemove( final EObject transformationEObject,
                                     final EObject sourceEObject ) {
        boolean canRemove = false;

        if (!ModelObjectUtilities.isReadOnly(transformationEObject)
            && transformationEObject instanceof SqlTransformationMappingRoot && sourceEObject != null
            && TransformationHelper.isValidSource(transformationEObject, sourceEObject)) {

            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            // Let's get current Input's
            List inputEObjects = mappingRoot.getInputs();

            // Now let's check to see if any exist as current inputs
            if (sourceEObject instanceof SqlAlias) {
                EObject aliasedEObj = ((SqlAlias)sourceEObject).getAliasedObject();
                if (inputEObjects.contains(aliasedEObj)) {
                    canRemove = true;
                }
            } else if (inputEObjects.contains(sourceEObject)) {
                canRemove = true;
            }
        }

        return canRemove;
    }

    /**
     * Method to determine if source EObjects can be removed from a transformation. As long any of the sourceEObjects is currently
     * in the transformation, remove is enabled.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObjects the source EObjects to be removed.
     * @return 'true' if the source can be removed 'false' if not.
     */
    public static boolean canRemove( final EObject transformationEObject,
                                     final List sourceEObjects ) {
        boolean canRemove = false;

        if (!ModelObjectUtilities.isReadOnly(transformationEObject)
            && transformationEObject instanceof SqlTransformationMappingRoot && sourceEObjects != null
            && !sourceEObjects.isEmpty()) {

            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            // Let's get current Input's
            List inputEObjects = mappingRoot.getInputs();
            // Now let's check to see if any exist as current inputs

            Iterator iter = sourceEObjects.iterator();
            EObject sourceEObject = null;
            while (iter.hasNext()) {
                sourceEObject = (EObject)iter.next();
                // Now let's check to see if any exist as current inputs
                if (sourceEObject instanceof SqlAlias) {
                    EObject aliasedEObj = ((SqlAlias)sourceEObject).getAliasedObject();
                    if (inputEObjects.contains(aliasedEObj)) {
                        canRemove = true;
                        break;
                    }
                } else if (inputEObjects.contains(sourceEObject)) {
                    canRemove = true;
                    break;
                }
            }
        }

        return canRemove;
    }

    /**
     * Method to determine if the transformation can be cleared. If the transformation has Inputs, clear is enabled.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @return 'true' if the transformation can be cleared, 'false' if not.
     */
    public static boolean canClear( final EObject transformationEObject ) {
        boolean canClear = false;
        // Let's check Model Resource first
        if (transformationEObject != null && transformationEObject.eContainer() != null
            && transformationEObject.eResource() != null) {
            if (!ModelObjectUtilities.isReadOnly(transformationEObject)
                && transformationEObject instanceof SqlTransformationMappingRoot) {
                SqlTransformationMappingRoot stmr = (SqlTransformationMappingRoot)transformationEObject;
                if (stmr.getTarget() != null) {
                    canClear = true;
                }
            }
        }
        return canClear;
    }

    /**
     * Method to add a source to the transformation. The source may be an EObject or SqlAlias.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObject the source EObject (may be SqlAlias) to be added.
     */
    public static void addSource( final Object mappingRoot,
                                  final EObject sourceEObject ) {

        SqlTransformationMappingRoot transMappingRoot = null;
        if (TransformationHelper.isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (transMappingRoot != null && sourceEObject != null) {
            // If existing command is a UNION, prompt for user input.
            if (TransformationHelper.isUnionCommand(transMappingRoot)) {
                SetQuery unionQuery = (SetQuery)SqlMappingRootCache.getSelectCommand(transMappingRoot);

                Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

                // Dialog - whether to add source as Union Source or add join source to segment
                QueryTreeSelectionDialog addToUnionDialog = new QueryTreeSelectionDialog(shell, unionQuery);
                addToUnionDialog.setValidator(new AddGroupSelectionValidator());
                addToUnionDialog.setTitle(AddGroupSelectionValidator.DIALOG_TITLE);
                addToUnionDialog.setMessage(AddGroupSelectionValidator.DIALOG_MESSAGE);
                // get editor selection (if there is one)
                int selectedUnionSegment = getCurrentSqlEditorUnionSegment();
                if (selectedUnionSegment != -1) {
                    addToUnionDialog.setInitialUnionSelection(selectedUnionSegment);
                } else {
                    addToUnionDialog.setInitialUnionSelection(0);
                }
                addToUnionDialog.open();

                // If OK pressed, modify the union query
                if (addToUnionDialog.getReturnCode() == Window.OK) {
                    // Add the source group to the desired UNION segment
                    if (addToUnionDialog.unionQueryFromClauseSelected()) {
                        int segmentIndex = addToUnionDialog.getUnionQuerySegmentIndexForFromClause();
                        if (segmentIndex != -1) {
                            addSourceToUnionSegment(mappingRoot, sourceEObject, segmentIndex);
                        }
                    } else // Add the source group as new UNION query
                    {
                        // Get UnionAll selection
                        boolean unionAll = addToUnionDialog.isUnionAllSelected();
                        // Add the source
                        addUnionSource(transMappingRoot, sourceEObject, unionAll);

                    }
                }
                // Existing command not a union, add the source
            } else {
                // start txn
                boolean requiredStart = ModelerCore.startTxn(false, false, "Add Source", null); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // ----------------------------------------------------------------------
                    // Get the name of the SqlAlias to be added
                    // ----------------------------------------------------------------------
                    String aliasName = null;
                    // supplied source is SqlAlias
                    if (sourceEObject instanceof SqlAlias) {
                        aliasName = ((SqlAlias)sourceEObject).getAlias();
                        // supplied source is EObject source, try to use the name of the Source
                    } else {
                        aliasName = getSqlEObjectName(sourceEObject);
                    }

                    // ----------------------------------------------------------------------
                    // If mapping doesn't already contain this alias, go ahead and add
                    // ----------------------------------------------------------------------
                    if (!TransformationHelper.containsSqlAliasName(transMappingRoot, aliasName, sourceEObject)) {
                        // supplied source is SqlAlias
                        if (sourceEObject instanceof SqlAlias) {
                            TransformationHelper.addSqlAlias(transMappingRoot, (SqlAlias)sourceEObject, true, null);
                            // supplied source is EObject source, try to use the name of the Source
                        } else {
                            TransformationHelper.addSqlAlias(transMappingRoot,
                                                             sourceEObject,
                                                             getSqlEObjectName(sourceEObject),
                                                             true,
                                                             null);
                        }
                        // ----------------------------------------------------
                        // If alias already exists, prompt for new alias name
                        // ----------------------------------------------------
                    } else {
                        // Prompt for alias to use
                        String sourceTableName = ModelerCore.getModelEditor().getName(sourceEObject);
                        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
                        AliasEntryDialog reconcilerDialog = new AliasEntryDialog(shell, ALIAS_ENTRY_DIALOG_TITLE,
                                                                                 sourceTableName, transMappingRoot, sourceEObject);
                        reconcilerDialog.open();
                        String newAliasName = reconcilerDialog.getAliasName();
                        if (newAliasName != null) {
                            EObject eObj = null;
                            if (sourceEObject instanceof SqlAlias) {
                                eObj = ((SqlAlias)sourceEObject).getAliasedObject();
                                // supplied source is EObject source, try to use the name of the Source
                            } else {
                                eObj = sourceEObject;
                            }
                            TransformationHelper.addSqlAlias(transMappingRoot, eObj, newAliasName, true, null);
                        }
                    }
                    succeeded = true;
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
    }

    /**
     * Method to add a List of sources to the transformation. The sources may be EObjects or SqlAliases.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObjects the List of source EObjects (may be SqlAliases) to be added.
     */
    public static void addSources( final Object mappingRoot,
                                   final List sourceEObjects ) {

        SqlTransformationMappingRoot transMappingRoot = null;
        if (TransformationHelper.isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (transMappingRoot != null && sourceEObjects != null && !sourceEObjects.isEmpty()) {
            if (TransformationHelper.isUnionCommand(transMappingRoot)) {
                SetQuery unionQuery = (SetQuery)SqlMappingRootCache.getSelectCommand(transMappingRoot);

                Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

                // Dialog - whether to add source as Union Source or add join source to segment
                QueryTreeSelectionDialog addToUnionDialog = new QueryTreeSelectionDialog(shell, unionQuery);
                addToUnionDialog.setValidator(new AddGroupSelectionValidator());
                addToUnionDialog.setTitle(AddGroupSelectionValidator.DIALOG_TITLE);
                addToUnionDialog.setMessage(AddGroupSelectionValidator.DIALOG_MESSAGE);
                // get editor selection (if there is one)
                int selectedUnionSegment = getCurrentSqlEditorUnionSegment();
                if (selectedUnionSegment != -1) {
                    addToUnionDialog.setInitialUnionSelection(selectedUnionSegment);
                } else {
                    addToUnionDialog.setInitialUnionSelection(0);
                }
                addToUnionDialog.open();

                // If OK pressed, modify the union query
                if (addToUnionDialog.getReturnCode() == Window.OK) {
                    // FROM clause selected - Add the source groups to the desired UNION segment
                    if (addToUnionDialog.unionQueryFromClauseSelected()) {
                        // Get the UNION segment for the selected from clause
                        int segmentIndex = addToUnionDialog.getUnionQuerySegmentIndexForFromClause();
                        // Add sources to the UNION segment
                        if (segmentIndex != -1) {
                            addSourcesToUnionSegment(mappingRoot, sourceEObjects, segmentIndex);
                        }
                    } else { // Add the source groups as new UNION query
                        // Get UnionAll selection
                        boolean unionAll = addToUnionDialog.isUnionAllSelected();
                        // add the sources
                        addUnionSources(transMappingRoot, sourceEObjects, unionAll);
                    }
                }
                // Existing command not a union, add the sources
            } else {
                // start txn
                boolean requiredStart = ModelerCore.startTxn(false, false, "Add Sources", null); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Add each source
                    Iterator iter = sourceEObjects.iterator();
                    while (iter.hasNext()) {
                        addSource(mappingRoot, (EObject)iter.next());
                    }
                    succeeded = true;
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
    }

    /**
     * Method to add a source to the transformation. The source may be an EObject or SqlAlias.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObject the source EObject (may be SqlAlias) to be added.
     */
    public static void addUnionSource( final Object mappingRoot,
                                       final EObject sourceEObject,
                                       boolean useAll ) {

        SqlTransformationMappingRoot transMappingRoot = null;
        if (TransformationHelper.isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (transMappingRoot != null && sourceEObject != null) {
            // start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Add Union Source", null); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                List grpList = new ArrayList(1);
                grpList.add(sourceEObject);
                Object txnSource = ModelerCore.getCurrentUoW().getSource();
                // Add SQLAlias for the added source to the transformation
                TransformationHelper.addSqlAlias(transMappingRoot, sourceEObject, getSqlEObjectName(sourceEObject), true, null);
                // Update the SQL query
                TransformationSqlHelper.updateUnionSelectOnGroupsAdded(transMappingRoot, grpList, useAll, txnSource);
                succeeded = true;
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

    /**
     * Method to add a List of sources to the transformation. The sources may be EObjects or SqlAliases.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObjects the List of source EObjects (may be SqlAliases) to be added.
     */
    public static void addUnionSources( final Object mappingRoot,
                                        final List sourceEObjects,
                                        boolean useAll ) {

        SqlTransformationMappingRoot transMappingRoot = null;
        if (TransformationHelper.isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (transMappingRoot != null && sourceEObjects != null && !sourceEObjects.isEmpty()) {
            // start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Add Union Sources", null); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                Object txnSource = ModelerCore.getCurrentUoW().getSource();
                // Add SQLAliases for the added sources to the transformation
                Iterator iter = sourceEObjects.iterator();
                while (iter.hasNext()) {
                    EObject sourceEObject = (EObject)iter.next();
                    TransformationHelper.addSqlAlias(transMappingRoot,
                                                     sourceEObject,
                                                     getSqlEObjectName(sourceEObject),
                                                     true,
                                                     null);
                }
                // Update the SQL query
                TransformationSqlHelper.updateUnionSelectOnGroupsAdded(transMappingRoot, sourceEObjects, useAll, txnSource);
                succeeded = true;
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

    /**
     * Method to add a source to the desired union segment.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObject the source EObject.
     * @param nSegmentIndex the index of the segment.
     */
    public static void addSourceToUnionSegment( final Object mappingRoot,
                                                final EObject sourceEObject,
                                                int nSegmentIndex ) {

        SqlTransformationMappingRoot transMappingRoot = null;
        if (TransformationHelper.isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (transMappingRoot != null && sourceEObject != null) {
            // start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Add Union Source", null); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                List grpList = new ArrayList(1);
                grpList.add(sourceEObject);
                Object txnSource = ModelerCore.getCurrentUoW().getSource();
                TransformationSqlHelper.updateUnionSelectAddGroupsToSegment(transMappingRoot, grpList, nSegmentIndex, txnSource);
                succeeded = true;
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

    /**
     * Method to add a List of sources to the desired union segment.
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObjects the source EObjects.
     * @param nSegmentIndex the index of the segment.
     */
    public static void addSourcesToUnionSegment( final Object mappingRoot,
                                                 final List sourceEObjects,
                                                 int nSegmentIndex ) {

        SqlTransformationMappingRoot transMappingRoot = null;
        if (TransformationHelper.isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (transMappingRoot != null && sourceEObjects != null && !sourceEObjects.isEmpty()) {
            // start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Add Union Sources", null); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                Object txnSource = ModelerCore.getCurrentUoW().getSource();
                TransformationSqlHelper.updateUnionSelectAddGroupsToSegment(transMappingRoot,
                                                                            sourceEObjects,
                                                                            nSegmentIndex,
                                                                            txnSource);
                succeeded = true;
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

    /**
     * Method to remove a source from the transformation. The source may be an EObject or SqlAlias. If the source being removed is
     * a SqlAlias, only the alias is removed (unless
     * 
     * @param transformationEObject the transformation MappingRoot
     * @param sourceEObject the source EObject (may be SqlAlias) to be removed.
     */
    public static void removeSource( final EObject transformationEObject,
                                     final EObject sourceEObject ) {
        if (transformationEObject instanceof SqlTransformationMappingRoot && sourceEObject != null) {

            // User may select one object, but it could mean one or more aliases of same source.
            List sqlAliases = getSqlAliasesToRemove(transformationEObject, sourceEObject);

            TransformationHelper.removeSourceAliases(transformationEObject, sourceEObject, sqlAliases, false, null);
        }
    }

    public static void removeSources( final EObject transformationEObject,
                                      final List sourceEObjects ) {
        if (transformationEObject instanceof SqlTransformationMappingRoot && sourceEObjects != null && !sourceEObjects.isEmpty()) {

            // SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            // Now let's check to see if any exist as current inputs

            // start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Remove Sources", null); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                Iterator iter = sourceEObjects.iterator();
                EObject sourceEObject = null;
                while (iter.hasNext()) {
                    sourceEObject = (EObject)iter.next();
                    // supplied source is SqlAlias
                    if (sourceEObject instanceof SqlAlias) {
                        // we need to call the "remove" method for each sourceEObject
                        removeSource(transformationEObject, ((SqlAlias)sourceEObject).getAliasedObject());
                        // supplied source is EObject source, try to use the name of the Source
                    } else {
                        removeSource(transformationEObject, sourceEObject);
                    }
                }
                succeeded = true;
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

    /**
     * Method to clear the transformation.
     * 
     * @param transformationEObject the transformation MappingRoot
     */
    public static void clear( final EObject transformationEObject,
                              final boolean removeAttributes ) {
        if (transformationEObject instanceof SqlTransformationMappingRoot) {
            // start txn
            boolean requiredStart = ModelerCore.startTxn(false, false, "Clear Transformation", null); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                TransformationHelper.clearTransformation(transformationEObject, true, null, removeAttributes);
                succeeded = true;
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

    /**
     * Get the transformation mapping root for the supplied object.
     * 
     * @param object the supplied Object
     * @return the transformation for the supplied object
     */
    public static Object getTransformationForObject( final Object object ) {
        Object result = null;

        IFile file = null;
        ModelResource mdlRsrc = null;

        if (object instanceof EObject) {
            // get the model file corresponding to the target
            mdlRsrc = ModelUtilities.getModelResourceForModelObject((EObject)object);
            if (mdlRsrc != null) {
                file = (IFile)mdlRsrc.getResource();
            }
        }

        IWorkbenchPage page = DiagramUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (page != null) {
            // look through the open editors and see if there is one available for this model file.
            IEditorReference[] editors = page.getEditorReferences();
            for (int i = 0; i < editors.length; ++i) {
                IEditorPart editor = editors[i].getEditor(false);
                if (editor != null) {
                    IEditorInput input = editor.getEditorInput();
                    if (input instanceof IFileEditorInput) {
                        if (file.equals(((IFileEditorInput)input).getFile())) {
                            // found it;
                            if (editor instanceof ModelEditor) {
                                result = editor;
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (result != null) {
            // TransformationSourceAdapter adapter = null;
            // ((ModelEditor)result).getAdapter(TransformationSourceAdapter.class);
        }

        return result;
    }

    /**
     * Get the transformation mapping root for the supplied diagram.
     * 
     * @param object the supplied Diagram
     * @return the transformation for the supplied diagram
     */
    public static EObject getTransformationFromDiagram( final Diagram diagram ) {
        EObject transformationEObject = null;

        // get the model resource from the diagram
        ModelResource mdlRsrc = ModelUtilities.getModelResourceForModelObject(diagram);
        if (mdlRsrc != null) {
            EObject targetEObject = diagram.getTarget();
            if (TransformationHelper.isValidSqlTransformationTarget(targetEObject)) {
                transformationEObject = TransformationHelper.getTransformationMappingRoot(targetEObject);
            }
        }

        return transformationEObject;
    }

    /**
     * Get the source EObjects for the supplied transformation mapping root.
     * 
     * @param object the supplied transformation mapping root.
     * @return the List of Input EObject sources
     */
    public static List getSourceEObjects( final EObject transformationEObject ) {
        if (transformationEObject instanceof SqlTransformationMappingRoot) {
            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            // Let's get current Input's
            List inputEObjects = mappingRoot.getInputs();
            // Now let's check to see if any exist as current inputs

            if (inputEObjects != null) return inputEObjects;
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * Get the name the supplied object sql eobject.
     * 
     * @param object the supplied Object
     * @return the name
     */
    public static String getSqlEObjectName( EObject eObject ) {
        String returnString = "NULL"; //$NON-NLS-1$
        SqlAspect aspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(eObject);
        if (aspect != null) {
            returnString = aspect.getName(eObject);
        }
        return returnString;
    }

    // /**
    // * Method to determin if the supplied EObject is a valid source. Must be a Sql Table or Procedure
    // * @param eObject the supplied EObject
    // * @return true if the supplied EObject is a valid source, false if not.
    // */
    // private static boolean isValidSource(EObject tRoot, EObject eObject) {
    // boolean isValid=false;
    // if(tRoot instanceof SqlTransformationMappingRoot &&
    // (TransformationHelper.isSqlTable(eObject)&&!TransformationHelper.isSqlInputSet(eObject))
    // || TransformationHelper.isSqlProcedure(eObject) ) {
    // // We need to check to make sure that the eObject is not the target
    // EObject target = ((SqlTransformationMappingRoot)tRoot).getTarget();
    // if( target != null && !target.equals(eObject) )
    // isValid=true;
    // }
    // return isValid;
    // }

    // This Method provides the ability to look at a group of sourceEObjects and see if any of them have
    // mulitiple aliases. If not, then the list is just passed through.
    // If so, then we need to ask the user for each source, which aliased objects to remove....
    public static List getSqlAliasesToRemove( final EObject transformationEObject,
                                              final EObject sourceEObject ) {
        // Create a temporary list here...
        List sqlAliases = new ArrayList();
        if (sourceEObject instanceof SqlAlias) sqlAliases.addAll(TransformationHelper.getSqlAliasesForSource(transformationEObject,
                                                                                                             ((SqlAlias)sourceEObject).getAliasedObject()));
        else sqlAliases.addAll(TransformationHelper.getSqlAliasesForSource(transformationEObject, sourceEObject));
        if (!sqlAliases.isEmpty()) {
            if (sqlAliases.size() == 1) {
                return sqlAliases;
            }
            // Prompt the User - which Alias(es) to remove
            return getUserSelectedSqlAliases(sqlAliases);
        }
        return Collections.EMPTY_LIST;
    }

    private static List getUserSelectedSqlAliases( final List sqlAliases ) {
        // Initialize removal list to all
        List initialSelectionList = new ArrayList(sqlAliases);
        // Result list
        List resultList = Collections.EMPTY_LIST;

        // Construct the Accumulator Dialog
        IAccumulatorSource accumulatorSource = new SqlAliasAccumulatorSource(getCurrentDiagramLabelProvider(), new ArrayList());
        String keepLabel = "Keep SQL Alias List"; //EMFEditUIPlugin.INSTANCE.getString("_UI_Choices_label");  //$NON-NLS-1$
        String removeLabel = "Remove SQL Alias List"; //EMFEditUIPlugin.INSTANCE.getString("_UI_Feature_label");  //$NON-NLS-1$
        AccumulatorDialog accumulatorDialog = new AccumulatorDialog(
                                                                    UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                                                    accumulatorSource,
                                                                    "Remove Selected Aliased Sources from SQL", initialSelectionList, getCurrentDiagramLabelProvider(), //$NON-NLS-1$
                                                                    keepLabel, removeLabel);

        // Display the Dialog
        accumulatorDialog.open();

        // Get the Dialog selection list (unless dialog was cancelled)
        if (!accumulatorDialog.wasCancelled()) {
            resultList = new ArrayList(accumulatorDialog.getSelectedItems());
        }

        return resultList;
    }

    private static ILabelProvider getCurrentDiagramLabelProvider() {
        return DiagramUiPlugin.getDiagramNotationManager().getLabelProvider();
    }

    /**
     * If the SqlEditor is Open, get the currently selected Union segment. If the editor is not open, or there is no segment
     * selection, returns -1
     * 
     * @return the currently selected segment, -1 if none selected
     */
    private static int getCurrentSqlEditorUnionSegment() {
        int selectedUnionSegment = -1;
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
                selectedUnionSegment = transOEP.getCurrentSqlEditor().getCurrentUnionCommandSegmentIndex();
            }
        }
        return selectedUnionSegment;
    }

    private static boolean isInvalidStagingTable( final EObject eObject,
                                                  final Object source ) {
        if (eObject != null && TransformationHelper.isStagingTable(eObject) && !(source instanceof ModelEditor)) {
            return true;
        }

        return false;
    }

    public static void warnUserAboutInvalidSources( List invalidSourceList ) {
        String title = UiConstants.Util.getString("TransformationSourceManager.invalidSourceListTitle"); //$NON-NLS-1$
        String msg = UiConstants.Util.getString("TransformationSourceManager.invalidSourceListMessage"); //$NON-NLS-1$
        List copyOfList = new ArrayList(invalidSourceList.size());
        for (Iterator iter = invalidSourceList.iterator(); iter.hasNext();) {
            Object nextObj = iter.next();
            String path = ModelObjectUtilities.getTrimmedFullPath((EObject)nextObj);
            String name = ModelerCore.getModelEditor().getName((EObject)nextObj);
            String row = path + "/" + name; //$NON-NLS-1$
            copyOfList.add(row);
        }

        ListMessageDialog.openWarning(UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                      title,
                                      null,
                                      msg,
                                      copyOfList,
                                      null);
    }

}
