/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.diagram.ui.actions.DiagramGlobalActionsMap;
import org.teiid.designer.diagram.ui.actions.RefreshAction;
import org.teiid.designer.diagram.ui.actions.SaveDiagramAction;
import org.teiid.designer.diagram.ui.editor.DiagramActionAdapter;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.editor.DiagramToolBarManager;
import org.teiid.designer.diagram.ui.pakkage.actions.RenameAction;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.transformation.TransformationMappingRoot;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.actions.CopyFullNameAction;
import org.teiid.designer.ui.actions.CopyNameAction;
import org.teiid.designer.ui.actions.EditAction;
import org.teiid.designer.ui.actions.IModelObjectActionContributor;
import org.teiid.designer.ui.actions.IModelerActionConstants;
import org.teiid.designer.ui.actions.IModelerActionConstants.ModelerGlobalActions;
import org.teiid.designer.ui.actions.ModelerActionBarIdManager;
import org.teiid.designer.ui.actions.ModelerActionService;
import org.teiid.designer.ui.common.actions.AbstractAction;
import org.teiid.designer.ui.common.actions.GlobalActionsMap;
import org.teiid.designer.ui.common.actions.IActionConstants.EclipseGlobalActions;
import org.teiid.designer.ui.editors.ModelEditorPage;


/**
 * TransformationActionAdapter
 *
 * @since 8.0
 */
public class TransformationActionAdapter extends DiagramActionAdapter
    implements UiConstants, ITransformationDiagramActionConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String D_MARKER = ContextMenu.TRANS_DIAGRAM_START;
    private static final String T_MARKER = ContextMenu.TRANS_START;

    // ============================================================================================================================
    // Variables

    private List<Object> modelObjectContributors;

    // our specific actions
    private AddTransformationSourceAction addSourcesAction;
    private AddUnionSourceAction addUnionSourcesAction;
    private TransformationAction removeSourcesAction;
    private TransformationAction clearSourcesAction;
    private TransformationAction reconcileAction;
    private TransformationAction addToSqlSelectAction;
    private TransformationAction addJoinExpressionAction;
    private AbstractAction lockAction;
    private RefreshAction refreshDiagramAction;
    private ShowParentDiagramAction upPackageDiagramAction;
    private ShowDependencyTargetDiagramAction showDependencyTargetDiagramAction;
    private AbstractAction saveDiagramAction;

    // overrides of global actions
    private TransformationAction deleteAction;
    private TransformationAction cutAction;
    private TransformationAction copyAction;
    private TransformationAction pasteAction;
    private TransformationAction cloneAction;
    private AbstractAction renameAction;

    // actions map is needed since we want to override the default print action
    private DiagramGlobalActionsMap actionsMap;

    // ============================================================================================================================
    // Constructors

    /**
     * @since 4.0
     */
    public TransformationActionAdapter( final ModelEditorPage page ) {
        super(page);
    }

    // ============================================================================================================================
    // Methods

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor#getGlobalActions()
     */
    @Override
    public GlobalActionsMap getGlobalActions() {

        if (actionsMap == null) {
            // construct a global actions map that contains this adapter's tweaks
            actionsMap = new DiagramGlobalActionsMap();
            actionsMap.reset();

            actionsMap.put(EclipseGlobalActions.DELETE, this.deleteAction);
            actionsMap.put(EclipseGlobalActions.CUT, this.cutAction);
            actionsMap.put(EclipseGlobalActions.COPY, this.copyAction);
            actionsMap.put(EclipseGlobalActions.PASTE, this.pasteAction);
            actionsMap.put(IModelerActionConstants.ModelerGlobalActions.CLONE, this.cloneAction);
            ((RenameAction)this.renameAction).setDiagramEditor((DiagramEditor)getEditorPage());
            actionsMap.put(EclipseGlobalActions.RENAME, this.renameAction);

        }

        return actionsMap;
    }

    /**
     * @since 4.0
     */
    @Override
    protected void initActions() {
        super.initActions();

        Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
        EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

        // ** TRANSFORMATION ACTIONS **************************************************
        // ----- AddTransformationSourceAction -----//
        this.addSourcesAction = (AddTransformationSourceAction)getRegisteredAction(AddTransformationSourceAction.class.getName());
        if (this.addSourcesAction == null) {
            this.addSourcesAction = new AddTransformationSourceAction(transformationEObject, currentDiagram);
            registerAction(this.addSourcesAction);
        }

        // ----- AddUnionSourceAction -----//
        this.addUnionSourcesAction = (AddUnionSourceAction)getRegisteredAction(AddUnionSourceAction.class.getName());
        if (this.addUnionSourcesAction == null) {
            this.addUnionSourcesAction = new AddUnionSourceAction(transformationEObject, currentDiagram);
            registerAction(this.addUnionSourcesAction);
        }

        // ----- RemoveTransformationSourceAction -----//
        this.removeSourcesAction = (TransformationAction)getRegisteredAction(RemoveTransformationSourceAction.class.getName());
        if (this.removeSourcesAction == null) {
            this.removeSourcesAction = new RemoveTransformationSourceAction(transformationEObject, currentDiagram);
            registerAction(this.removeSourcesAction);
        }

        // ----- ClearTransformationAction -----//
        this.clearSourcesAction = (TransformationAction)getRegisteredAction(ClearTransformationAction.class.getName());
        if (this.clearSourcesAction == null) {
            this.clearSourcesAction = new ClearTransformationAction(transformationEObject, currentDiagram);
            registerAction(this.clearSourcesAction);
        }

        // ----- ReconcileTransformationAction -----//
        this.reconcileAction = (TransformationAction)getRegisteredAction(ReconcileTransformationAction.class.getName());
        if (this.reconcileAction == null) {
            this.reconcileAction = new ReconcileTransformationAction(transformationEObject, currentDiagram);
            registerAction(this.reconcileAction);
        }

        // ----- AddToSqlSelectAction -----//
        this.addToSqlSelectAction = (TransformationAction)getRegisteredAction(AddToSqlSelectAction.class.getName());
        if (this.addToSqlSelectAction == null) {
            this.addToSqlSelectAction = new AddToSqlSelectAction(transformationEObject, currentDiagram);
            registerAction(this.addToSqlSelectAction);
        }

        // ----- AddSqlJoinAction -----//
        this.addJoinExpressionAction = (TransformationAction)getRegisteredAction(AddJoinExpressionAction.class.getName());
        if (this.addJoinExpressionAction == null) {
            this.addJoinExpressionAction = new AddJoinExpressionAction(transformationEObject, currentDiagram);
            registerAction(this.addJoinExpressionAction);
        }

        // ----- LockAction -----//
        this.lockAction = (AbstractAction)getRegisteredAction(LockAction.class.getName());
        if (this.lockAction == null) {
            this.lockAction = new LockAction(transformationEObject, currentDiagram);
            registerAction(this.lockAction);
        }

        // ** GLOBAL EDIT OVERRIDE ACTIONS **************************************************
        // ----- DeleteAction -----//
        this.deleteAction = (TransformationAction)getRegisteredAction(DeleteAction.class.getName());
        if (this.deleteAction == null) {
            this.deleteAction = new DeleteAction(transformationEObject, currentDiagram);
            registerAction(this.deleteAction);
        }

        // ----- CutAction -----//
        this.cutAction = (TransformationAction)getRegisteredAction(CutAction.class.getName());
        if (this.cutAction == null) {
            this.cutAction = new CutAction(transformationEObject, currentDiagram);
            registerAction(this.cutAction);
        }

        // ----- CopyAction -----//
        this.copyAction = (TransformationAction)getRegisteredAction(CopyAction.class.getName());
        if (this.copyAction == null) {
            this.copyAction = new CopyAction(transformationEObject, currentDiagram);
            registerAction(this.copyAction);
        }

        // ----- PasteAction -----//
        this.pasteAction = (TransformationAction)getRegisteredAction(PasteAction.class.getName());
        if (this.pasteAction == null) {
            this.pasteAction = new PasteAction(transformationEObject, currentDiagram);
            registerAction(this.pasteAction);
        }

        // ----- CloneAction -----//
        this.cloneAction = (TransformationAction)getRegisteredAction(CloneAction.class.getName());
        if (this.cloneAction == null) {
            this.cloneAction = new CloneAction(transformationEObject, currentDiagram);
            registerAction(this.cloneAction);
        }

        // ----- RenameAction -----//
        this.renameAction = (AbstractAction)getRegisteredAction(RenameAction.class.getName());
        if (this.renameAction == null) {
            this.renameAction = new RenameAction();
            registerAction(this.renameAction);
        }

        // ** DIAGRAM-RELATED ACTIONS **************************************************
        // ----- RefreshAction -----//
        this.refreshDiagramAction = (RefreshAction)getRegisteredAction(RefreshAction.class.getName());
        if (this.refreshDiagramAction == null) {
            this.refreshDiagramAction = new RefreshAction();
            registerAction(this.refreshDiagramAction);
        }

        // ----- ShowParentDiagramAction -----//
        this.upPackageDiagramAction = (ShowParentDiagramAction)getRegisteredAction(ShowParentDiagramAction.class.getName());
        if (this.upPackageDiagramAction == null) {
            this.upPackageDiagramAction = new ShowParentDiagramAction();
            registerAction(this.upPackageDiagramAction);
        }
        // ----- ShowParentDiagramAction -----//
        this.showDependencyTargetDiagramAction = (ShowDependencyTargetDiagramAction)getRegisteredAction(ShowDependencyTargetDiagramAction.class.getName());
        if (this.showDependencyTargetDiagramAction == null) {
            this.showDependencyTargetDiagramAction = new ShowDependencyTargetDiagramAction();
            registerAction(this.showDependencyTargetDiagramAction);
        }

        // ----- SaveDiagramAction -----//
        this.saveDiagramAction = (AbstractAction)getRegisteredAction(SaveDiagramAction.class.getName());
        if (this.saveDiagramAction == null) {
            this.saveDiagramAction = new SaveDiagramAction((DiagramEditor)this.getEditorPage());
            registerAction(this.saveDiagramAction);
        }

    }

    /**
     * private method used to update all transformation actions so they maintain the reference to the transformation displayed in
     * the transformation diagram.
     */
    private void resetTransformationActions() {
        Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
        EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

        this.addSourcesAction.setTransformation(transformationEObject);
        this.addUnionSourcesAction.setTransformation(transformationEObject);
        this.removeSourcesAction.setTransformation(transformationEObject);
        this.clearSourcesAction.setTransformation(transformationEObject);
        this.reconcileAction.setTransformation(transformationEObject);
        this.addToSqlSelectAction.setTransformation(transformationEObject);
        this.addJoinExpressionAction.setTransformation(transformationEObject);
        this.deleteAction.setTransformation(transformationEObject);
        this.cutAction.setTransformation(transformationEObject);
        this.copyAction.setTransformation(transformationEObject);
        this.pasteAction.setTransformation(transformationEObject);
        this.cloneAction.setTransformation(transformationEObject);
        ((TransformationAction)this.lockAction).setTransformation(transformationEObject);

        this.addSourcesAction.setDiagram(currentDiagram);
        this.addUnionSourcesAction.setDiagram(currentDiagram);
        this.removeSourcesAction.setDiagram(currentDiagram);
        this.clearSourcesAction.setDiagram(currentDiagram);
        this.reconcileAction.setDiagram(currentDiagram);
        this.addToSqlSelectAction.setDiagram(currentDiagram);
        this.addJoinExpressionAction.setDiagram(currentDiagram);
        this.deleteAction.setDiagram(currentDiagram);
        this.cutAction.setDiagram(currentDiagram);
        this.copyAction.setDiagram(currentDiagram);
        this.pasteAction.setDiagram(currentDiagram);
        this.cloneAction.setDiagram(currentDiagram);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void contributeExportedActions( IMenuManager theMenuMgr ) {
        resetTransformationActions();

        // check to see if this is a dependency diagram
        if (!isDependencyDiagram()) {
            // check to see if menu is the TempMenuID menu or just a context menu
            if ((theMenuMgr.getId() != null) && theMenuMgr.getId().equals("TempMenuID")) {
                setEditMenu(theMenuMgr); // need this in dispose()
                theMenuMgr.add(this.addSourcesAction);
                theMenuMgr.add(this.addUnionSourcesAction);
            } else {
                if (this.addSourcesAction.isEnabled()) {
                	theMenuMgr.add(this.addSourcesAction);
                }
                if (this.addUnionSourcesAction.isEnabled()) {
                	theMenuMgr.add(this.addUnionSourcesAction);
                }
            }
        }

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
     */
    @Override
    public void createContextMenu() {
        createContextMenu(ContextMenu.DIAGRAM_EDITOR_PAGE, getEditorPage().getControl());
    }

    @Override
    public void contributeToMenuManager( IMenuManager theMenuMgr,
                                         ISelection selection ) {
        removeDiagramActions(theMenuMgr);

        if (!isDependencyDiagram()) {
            Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
            EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

            TransformationSelectionHelper selectionHelper = new TransformationSelectionHelper(transformationEObject, selection);
            if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_SINGLE) {
                switch (selectionHelper.getType()) {
                    case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT: {
                        resetTransformationActions();
                        IAction editAction = getAction(ModelerGlobalActions.EDIT);
                        if (editAction != null) {
                            TransformationMappingRoot root = (TransformationMappingRoot)selectionHelper.getSingleEObject();
                            if (TransformationHelper.isOperation(root.getTarget())) {
                                ((EditAction)editAction).setEnabledOnce();
                            }
                            theMenuMgr.add(editAction);
                        }
                        theMenuMgr.add(new Separator());
                        theMenuMgr.add(this.clearSourcesAction);
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_XQUERY_TRANSFORMATION_ROOT: {
                        resetTransformationActions();
                        IAction editAction = getAction(ModelerGlobalActions.EDIT);
                        if (editAction != null) {
                            TransformationMappingRoot root = (TransformationMappingRoot)selectionHelper.getSingleEObject();
                            if (TransformationHelper.isOperation(root.getTarget())) {
                                ((EditAction)editAction).setEnabledOnce();
                            }
                            theMenuMgr.add(editAction);
                        }
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT_EXTRA: {
                        resetTransformationActions();
                        IAction editAction = getAction(ModelerGlobalActions.EDIT);
                        if (editAction != null) {
                            TransformationMappingRoot root = (TransformationMappingRoot)selectionHelper.getSingleEObject();
                            if (TransformationHelper.isOperation(root.getTarget())) {
                                ((EditAction)editAction).setEnabledOnce();
                            }
                            theMenuMgr.add(editAction);
                        }
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_INPUT_SET: {
                        resetTransformationActions();
                        IAction newChildAction = getAction(ModelerActionBarIdManager.getInsertChildMenuId());
                        if (newChildAction != null) theMenuMgr.add(newChildAction);
                        IAction editAction = getAction(ModelerGlobalActions.EDIT);
                        if (editAction != null) theMenuMgr.add(editAction);
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_SOURCE_RESULT_SET:
                    case TransformationSelectionHelper.TYPE_RESULT_SET: {
                        resetTransformationActions();
                        addDiagramActions(theMenuMgr);
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_SOURCE_CHILD: {
                        resetTransformationActions();
                        IAction existingAction = getAction(ModelerGlobalActions.OPEN);
                        if (existingAction != null) theMenuMgr.add(existingAction);
                        theMenuMgr.add(new Separator());
                        theMenuMgr.add(new GroupMarker(T_MARKER));
                        theMenuMgr.appendToGroup(T_MARKER, this.removeSourcesAction);
                        theMenuMgr.appendToGroup(T_MARKER, this.clearSourcesAction);
                        theMenuMgr.appendToGroup(T_MARKER, this.addToSqlSelectAction);
                        theMenuMgr.appendToGroup(T_MARKER, this.addJoinExpressionAction);
                        theMenuMgr.add(new Separator(ContextMenu.TRANS_END));
                        // Defect 24443
                        MenuManager copyMenu = getCopyNameSubMenu(selection);
                        if (copyMenu != null) {
                            theMenuMgr.add(new Separator());
                            theMenuMgr.add(copyMenu);
                            theMenuMgr.add(new Separator());
                        }
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_SOURCE_TABLE: {
                        resetTransformationActions();
                        MenuManager menuMgr = getModelingActionMenu(selection);
                        if (menuMgr.getItems().length > 0) {
                            theMenuMgr.add(menuMgr);
                            theMenuMgr.add(new Separator());
                        }
                        IAction existingAction = getAction(ModelerGlobalActions.OPEN);
                        if (existingAction != null) theMenuMgr.add(existingAction);
                        if (selectionHelper.isVirtual()) {
                            IAction editAction = getAction(ModelerGlobalActions.EDIT);
                            if (editAction != null) theMenuMgr.add(editAction);
                        }
                        theMenuMgr.add(new Separator());
                        theMenuMgr.add(new GroupMarker(T_MARKER));
                        theMenuMgr.appendToGroup(T_MARKER, this.removeSourcesAction);
                        theMenuMgr.appendToGroup(T_MARKER, this.clearSourcesAction);
                        if (selectionHelper.isVirtual()) {
                            addExternalExportedActions(theMenuMgr, selection);
                        }
                        theMenuMgr.add(new Separator(ContextMenu.TRANS_END));
                        // Defect 24443
                        MenuManager copyMenu = getCopyNameSubMenu(selection);
                        if (copyMenu != null) {
                            theMenuMgr.add(new Separator());
                            theMenuMgr.add(copyMenu);
                            theMenuMgr.add(new Separator());
                        }
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_TARGET_CHILD:
                    case TransformationSelectionHelper.TYPE_TARGET_TABLE: {
                        resetTransformationActions();
                        theMenuMgr.add(new GroupMarker(T_MARKER));
                        theMenuMgr.appendToGroup(T_MARKER, this.lockAction);
                        addDiagramActions(theMenuMgr);
                        
                        // Remove Open
                        IAction openAction = getAction(ModelerGlobalActions.OPEN);
                        if (openAction != null && theMenuMgr.find(openAction.getId()) != null) {
                        	theMenuMgr.remove(openAction.getId());
                        }
                        
                     // Remove Edit action
                        IAction editAction = getAction(ModelerGlobalActions.EDIT);
                        if (editAction != null && theMenuMgr.find(editAction.getId()) != null) {
                        	theMenuMgr.remove(editAction.getId());
                        }
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_UNKNOWN: {

                    }
                        break;

                    case TransformationSelectionHelper.TYPE_DIAGRAM: {
                        resetTransformationActions();
                        addDiagramActions(theMenuMgr);
                    }
                        break;

                    default:
                        break;
                }
            } else if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_MULTIPLE_SAME) {
                switch (selectionHelper.getType()) {
                    case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT: {
                        // Shouldn't ever have this if not dependency. Could have it later
                        // if we allow multiple transformations feeding same Virtual group
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_INPUT_SET: {
                        // Shouldn't ever have multiple input sets
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_RESULT_SET: {
                        // Shouldn't ever have multiple result sets?
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_SOURCE_CHILD: {
                        resetTransformationActions();
                        theMenuMgr.add(new GroupMarker(T_MARKER));
                        theMenuMgr.appendToGroup(T_MARKER, this.removeSourcesAction);
                        theMenuMgr.appendToGroup(T_MARKER, this.clearSourcesAction);
                        theMenuMgr.appendToGroup(T_MARKER, this.addToSqlSelectAction);
                        theMenuMgr.appendToGroup(T_MARKER, this.addJoinExpressionAction);

                        theMenuMgr.add(new Separator());
                    }
                        break;
                    case TransformationSelectionHelper.TYPE_SOURCE_TABLE: {
                        resetTransformationActions();
                        IAction existingAction = getAction(ModelerGlobalActions.OPEN);
                        if (existingAction != null) theMenuMgr.add(existingAction);
                        theMenuMgr.add(new Separator());
                        theMenuMgr.add(new GroupMarker(T_MARKER));
                        theMenuMgr.appendToGroup(T_MARKER, this.removeSourcesAction);
                        theMenuMgr.appendToGroup(T_MARKER, this.clearSourcesAction);

                        if (selectionHelper.isVirtual()) {
                            addExternalExportedActions(theMenuMgr, selection);
                        }
                        theMenuMgr.add(new Separator());
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_TARGET_CHILD:
                    case TransformationSelectionHelper.TYPE_TARGET_TABLE: {
                        resetTransformationActions();
                        addDiagramActions(theMenuMgr);
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_UNKNOWN: {

                    }
                        break;

                    default:
                        break;
                }
            }
        } else {
            Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
            EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

            TransformationSelectionHelper selectionHelper = new TransformationSelectionHelper(transformationEObject, selection);
            if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_SINGLE) {
                switch (selectionHelper.getType()) {
                    // defect 16564 - these cases copied from above to add more menu items
                    case TransformationSelectionHelper.TYPE_SOURCE_CHILD: {
                        resetTransformationActions();
                        IAction existingAction = getAction(ModelerGlobalActions.OPEN);
                        if (existingAction != null) theMenuMgr.add(existingAction);
                        theMenuMgr.add(new Separator(ContextMenu.TRANS_END));
                    }
                        break;
                    case TransformationSelectionHelper.TYPE_SOURCE_TABLE: {
                        resetTransformationActions();
                        IAction existingAction = getAction(ModelerGlobalActions.OPEN);
                        if (existingAction != null) theMenuMgr.add(existingAction);
                        if (selectionHelper.isVirtual()) {
                            IAction editAction = getAction(ModelerGlobalActions.EDIT);
                            if (editAction != null) theMenuMgr.add(editAction);
                            addExternalExportedActions(theMenuMgr, selection);
                        }
                        theMenuMgr.add(new Separator(ContextMenu.TRANS_END));
                    }
                        break;
                    case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT: {
                        IAction editAction = getAction(ModelerGlobalActions.EDIT);
                        if (editAction != null) theMenuMgr.add(editAction);
                        theMenuMgr.add(new Separator());
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT_EXTRA: {
                        IAction editAction = getAction(ModelerGlobalActions.EDIT);
                        if (editAction != null) theMenuMgr.add(editAction);
                    }
                        break;

                    case TransformationSelectionHelper.TYPE_INPUT_SET:
                    case TransformationSelectionHelper.TYPE_SOURCE_RESULT_SET:
                    case TransformationSelectionHelper.TYPE_RESULT_SET:
                    case TransformationSelectionHelper.TYPE_TARGET_CHILD:
                    case TransformationSelectionHelper.TYPE_TARGET_TABLE:
                    case TransformationSelectionHelper.TYPE_UNKNOWN:
                    case TransformationSelectionHelper.TYPE_DIAGRAM:

                    default:
                        break;
                }
            }

            addDiagramActions(theMenuMgr);
        }

    }

    private void addExternalExportedActions( IMenuManager theMenuMgr,
                                             ISelection selection ) {
        List<Object> contributors = getModelObjectActionContributors();

        for (int size = contributors.size(), i = 0; i < size; i++) {
            IModelObjectActionContributor contributor = (IModelObjectActionContributor)contributors.get(i);
            contributor.contributeToContextMenu(theMenuMgr, selection);
        }
    }

    private void removeDiagramActions( IMenuManager theMenuMgr ) {
        if (theMenuMgr.find(T_MARKER) != null) theMenuMgr.remove(T_MARKER);

        if (theMenuMgr.find(this.removeSourcesAction.getId()) != null) theMenuMgr.remove(this.removeSourcesAction.getId());
        if (theMenuMgr.find(this.clearSourcesAction.getId()) != null) theMenuMgr.remove(this.clearSourcesAction.getId());
        if (theMenuMgr.find(this.lockAction.getId()) != null) theMenuMgr.remove(this.lockAction.getId());
        if (theMenuMgr.find(this.refreshDiagramAction.getId()) != null) theMenuMgr.remove(this.refreshDiagramAction.getId());
        if (theMenuMgr.find(this.upPackageDiagramAction.getId()) != null) theMenuMgr.remove(this.upPackageDiagramAction.getId());
        if (theMenuMgr.find(this.showDependencyTargetDiagramAction.getId()) != null) theMenuMgr.remove(this.showDependencyTargetDiagramAction.getId());
        if (theMenuMgr.find(this.saveDiagramAction.getId()) != null) theMenuMgr.remove(this.saveDiagramAction.getId());

        if (theMenuMgr.find(ContextMenu.TRANS_END) != null) theMenuMgr.remove(ContextMenu.TRANS_END);
    }

    private void addDiagramActions( IMenuManager theMenuMgr ) {
        if (!isDependencyDiagram()) {
            theMenuMgr.add(new Separator());
            theMenuMgr.add(new GroupMarker(D_MARKER));
            theMenuMgr.appendToGroup(D_MARKER, this.removeSourcesAction);
            theMenuMgr.appendToGroup(D_MARKER, this.clearSourcesAction);
            theMenuMgr.add(new Separator());
            theMenuMgr.appendToGroup(D_MARKER, this.refreshDiagramAction);
            theMenuMgr.appendToGroup(D_MARKER, this.upPackageDiagramAction);
            theMenuMgr.appendToGroup(D_MARKER, this.saveDiagramAction);
        } else {
            theMenuMgr.add(new Separator());
            theMenuMgr.add(new GroupMarker(D_MARKER));
            theMenuMgr.appendToGroup(D_MARKER, this.refreshDiagramAction);
            theMenuMgr.appendToGroup(D_MARKER, this.showDependencyTargetDiagramAction);
            theMenuMgr.appendToGroup(D_MARKER, this.saveDiagramAction);
        }
    }

    // ============================================================================================================================
    // EditorActionBarContributor Methods

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        super.dispose();

        this.reconcileAction.dispose();
        this.clearSourcesAction.dispose();

        setEditorPage(null);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#contributeToDiagramToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    @Override
    public void contributeToDiagramToolBar() {
        // NOTE: this method gets called each time the transformation diagram is displayed
        DiagramToolBarManager tbm = (DiagramToolBarManager)((DiagramEditor)getEditorPage()).getToolBarManager();

        tbm.removeAll();

        tbm.add(this.refreshDiagramAction);
        if (isDependencyDiagram()) {
        	tbm.add(this.showDependencyTargetDiagramAction);
        } else {
        	tbm.add(this.upPackageDiagramAction);
        }
        tbm.add(new Separator());

        resetTransformationActions();

        ActionContributionItem addSourcesItem = null;
        ActionContributionItem addUnionSourcesItem = null;

        if (!isDependencyDiagram()) {
            addSourcesItem = new ActionContributionItem(this.addSourcesAction);
            tbm.add(addSourcesItem);

            addUnionSourcesItem = new ActionContributionItem(this.addUnionSourcesAction);
            tbm.add(this.addUnionSourcesAction);

            tbm.add(this.removeSourcesAction);
            tbm.add(this.clearSourcesAction);
            tbm.add(this.reconcileAction);
        }
        tbm.add(new Separator());
        tbm.add(this.saveDiagramAction);

        this.refreshDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.upPackageDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.showDependencyTargetDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());

        if (!isDependencyDiagram()) {
            // Special wiring is needed to help this action determine if it's diagram toolbar button
            // was pressed when the diagram was not in "Focus". This was causing additional selection events
            // to be handled before the action's "doRun()" method which messed up the intended selected item(s)
            // say.. from the tree, or another diagram.
            this.addSourcesAction.setToolBarManager(tbm);
            this.addSourcesAction.setItem(addSourcesItem);
            this.addUnionSourcesAction.setToolBarManager(tbm);
            this.addUnionSourcesAction.setItem(addUnionSourcesItem);
        }

        tbm.update(true);
    }

    private boolean isDependencyDiagram() {
        DiagramEditor editor = ((DiagramEditor)getEditorPage());
        if (editor.getDiagram() != null && editor.getDiagram().getType() != null
            && editor.getDiagram().getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID)) return true;

        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#handleNotification(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public void handleNotification( Notification theNotification ) {
        // Currently doesn't need to do anything.
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#enableDiagramToolbarActions()
     */
    @Override
    public void enableDiagramToolbarActions() {
        if (this.upPackageDiagramAction != null) this.upPackageDiagramAction.determineEnablement();
        if (this.showDependencyTargetDiagramAction != null ) this.showDependencyTargetDiagramAction.determineEnablement();
    }

    /**
     * @see org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#shouldOverrideMenu()
     * @since 4.2
     */
    @Override
    public boolean shouldOverrideMenu( ISelection selection ) {
        boolean value = false;
        Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
        EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

        TransformationSelectionHelper selectionHelper = new TransformationSelectionHelper(transformationEObject, selection);
        if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_SINGLE) {
            switch (selectionHelper.getType()) {
                case TransformationSelectionHelper.TYPE_DIAGRAM:
                case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT:
                case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT_EXTRA:
                case TransformationSelectionHelper.TYPE_SOURCE_CHILD:
                case TransformationSelectionHelper.TYPE_SOURCE_TABLE:
                case TransformationSelectionHelper.TYPE_INPUT_SET:
                case TransformationSelectionHelper.TYPE_SOURCE_RESULT_SET:
                case TransformationSelectionHelper.TYPE_XQUERY_TRANSFORMATION_ROOT: {
                    value = true;
                }
                    break;

                case TransformationSelectionHelper.TYPE_RESULT_SET:
                case TransformationSelectionHelper.TYPE_TARGET_CHILD:
                case TransformationSelectionHelper.TYPE_TARGET_TABLE:
                case TransformationSelectionHelper.TYPE_UNKNOWN:

                default:
                    break;
            }
        } else if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_MULTIPLE_SAME) {
            switch (selectionHelper.getType()) {

                case TransformationSelectionHelper.TYPE_SOURCE_CHILD:
                case TransformationSelectionHelper.TYPE_SOURCE_TABLE: {
                    value = true;
                }
                    break;

                // All these either aren't possible, or shouldn't be overridden
                case TransformationSelectionHelper.TYPE_INPUT_SET:
                case TransformationSelectionHelper.TYPE_RESULT_SET:
                case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT:
                case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT_EXTRA:
                case TransformationSelectionHelper.TYPE_TARGET_CHILD:
                case TransformationSelectionHelper.TYPE_TARGET_TABLE:
                case TransformationSelectionHelper.TYPE_UNKNOWN:

                default:
                    break;
            }
        }

        return value;
    }

    /**
     * Gets the Copy Name menu. For Defect 24443
     * 
     * @return the Copy Name submenu
     */
    private MenuManager getCopyNameSubMenu(Object selection) {

        MenuManager menu = new MenuManager(
                                           org.teiid.designer.ui.UiConstants.Util.getString("ModelerActionService.copyNameSubMenu.title")); //$NON-NLS-1$
        
        CopyFullNameAction action1 = new CopyFullNameAction();
        action1.getActionWorker().selectionChanged(selection);
        menu.add(action1);
        
        CopyNameAction action2 = new CopyNameAction();
        action1.getActionWorker().selectionChanged(selection);
        menu.add(action2);
        
        return menu;
    }

    private IAction getAction( String theActionId ) {
        IAction action = null;
        try {
            action = getActionService().getAction(theActionId);
        } catch (CoreException err) {
        }

        return action;
    }

    private MenuManager getModelingActionMenu( ISelection theSelection ) {
        IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        ModelerActionService service = (ModelerActionService)org.teiid.designer.ui.UiPlugin.getDefault().getActionService(window.getActivePage());
        return service.getModelingActionMenu(theSelection);
    }

    /**
     * Gets a list of the extension for the ModelObjectActionContributor extension point. This was copied from
     * ModelerActionService and changed to only process the trasformation.ui contributor to find the showDependencyAction.
     * 
     * @return the list of <code>IModelObjectActionContributor</code> implementations
     */
    private List<Object> getModelObjectActionContributors() {
        if (modelObjectContributors == null) {
            String ID = org.teiid.designer.ui.UiConstants.ExtensionPoints.ModelObjectActionContributor.ID;
            String CLASSNAME = org.teiid.designer.ui.UiConstants.ExtensionPoints.ModelObjectActionContributor.CLASSNAME;
            // get the ModelObjectActionContributor extension point from the plugin class
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(org.teiid.designer.ui.UiConstants.PLUGIN_ID,
                                                                                               ID);

            // get the all extensions to the ModelObjectActionContributor extension point
            IExtension[] extensions = extensionPoint.getExtensions();

            if (extensions.length > 0) {
                modelObjectContributors = new ArrayList<Object>(extensions.length);

                // for each extension get their contributor
                for (int i = 0; i < extensions.length; i++) {
                    IConfigurationElement[] elements = extensions[i].getConfigurationElements();
                    Object extension = null;
                    String uniqueID = extensions[i].getSimpleIdentifier();
                    if (uniqueID.startsWith("transformationDiagramModelObjectActionContributor")) { //$NON-NLS-1$
                        for (int j = 0; j < elements.length; j++) {
                            try {
                                extension = elements[j].createExecutableExtension(CLASSNAME);

                                if (extension instanceof IModelObjectActionContributor) {
                                    modelObjectContributors.add(extension);
                                } else {
                                    org.teiid.designer.ui.UiConstants.Util.log(IStatus.ERROR,
                                                                                   org.teiid.designer.ui.UiConstants.Util.getString("ModelerActionService.wrongContributorClass", //$NON-NLS-1$
                                                                                                                                        new Object[] {extension.getClass().getName()}));
                                }
                            } catch (Exception theException) {
                                org.teiid.designer.ui.UiConstants.Util.log(IStatus.ERROR,
                                                                               theException,
                                                                               org.teiid.designer.ui.UiConstants.Util.getString("ModelerActionService.contributorProblem", //$NON-NLS-1$
                                                                                                                                    new Object[] {elements[j].getAttribute(CLASSNAME)}));
                            }
                        }
                    }
                }
            } else {
                modelObjectContributors = Collections.emptyList();
            }
        }

        return modelObjectContributors;
    }
}
