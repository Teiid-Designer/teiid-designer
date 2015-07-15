/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.transaction.SourcedNotification;
import org.teiid.designer.diagram.ui.actions.DiagramGlobalActionsMap;
import org.teiid.designer.diagram.ui.actions.RefreshAction;
import org.teiid.designer.diagram.ui.editor.DiagramActionAdapter;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.editor.DiagramToolBarManager;
import org.teiid.designer.diagram.ui.pakkage.actions.RenameAction;
import org.teiid.designer.mapping.factory.ITreeToRelationalMapper;
import org.teiid.designer.mapping.factory.MappingClassFactory;
import org.teiid.designer.mapping.factory.ModelMapperFactory;
import org.teiid.designer.mapping.factory.TreeMappingAdapter;
import org.teiid.designer.mapping.ui.PluginConstants;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.editor.MappingDiagramController;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.metamodels.transformation.MappingClassSet;
import org.teiid.designer.metamodels.transformation.TransformationContainer;
import org.teiid.designer.metamodels.transformation.TransformationFactory;
import org.teiid.designer.metamodels.transformation.TransformationPlugin;
import org.teiid.designer.transformation.ui.actions.AddJoinExpressionAction;
import org.teiid.designer.transformation.ui.actions.AddToSqlSelectAction;
import org.teiid.designer.transformation.ui.actions.AddTransformationSourceAction;
import org.teiid.designer.transformation.ui.actions.AddUnionSourceAction;
import org.teiid.designer.transformation.ui.actions.ClearTransformationAction;
import org.teiid.designer.transformation.ui.actions.ReconcileTransformationAction;
import org.teiid.designer.transformation.ui.actions.RemoveTransformationSourceAction;
import org.teiid.designer.transformation.ui.actions.TransformationAction;
import org.teiid.designer.transformation.ui.actions.TransformationSelectionHelper;
import org.teiid.designer.transformation.ui.actions.TransformationSourceManager;
import org.teiid.designer.ui.actions.CopyFullNameAction;
import org.teiid.designer.ui.actions.CopyNameAction;
import org.teiid.designer.ui.actions.IModelObjectActionContributor;
import org.teiid.designer.ui.actions.IModelerActionConstants;
import org.teiid.designer.ui.actions.IModelerActionConstants.ModelerGlobalActions;
import org.teiid.designer.ui.actions.ModelerActionBarIdManager;
import org.teiid.designer.ui.actions.ModelerGlobalActionsMap;
import org.teiid.designer.ui.common.actions.AbstractAction;
import org.teiid.designer.ui.common.actions.GlobalActionsMap;
import org.teiid.designer.ui.common.actions.IActionConstants.EclipseGlobalActions;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * MappingDiagramActionAdapter
 *
 * @since 8.0
 */
public class MappingDiagramActionAdapter extends DiagramActionAdapter implements UiConstants, IMappingDiagramActionConstants {

    private static final String D_MARKER = ContextMenu.MAPPING_DIAGRAM_START;
    private static final String T_MARKER = ContextMenu.TRANS_START;
    private static final String M_MARKER = ContextMenu.MAPPING_START;
    static final String PARAM_BASE_NAME = UiConstants.Util.getString("MappingDiagramActionAdapter.inputParamBaseName.text"); //$NON-NLS-1$
    static final String NEW_INPUT_PARAMETER_TITLE = UiConstants.Util.getString("MappingDiagramActionAdapter.inputParameterTitle.text"); //$NON-NLS-1$

    private List<Object> modelObjectContributors;

    private AbstractAction showDetailedMappingAction;
    private MappingAction newMappingClassAction;
    private MappingAction newStagingTableAction;
    private MappingAction newMappingLinkAction;
    private MappingAction deleteMappingLinksAction;
    private MappingAction mergeMappingClassesAction;
    private MappingAction splitMappingClassAction;
    private MappingAction toggleDisplayAllMappingClassesAction;
    private MappingAction toggleFoldAllMappingClassesAction;
    // Defect 20604: removing this 'sync tree and diagram' action for now;
    // retargeted to 5.0 SP1.
    // private MappingAction toggleSyncTreeAndDiagramExpandsAction;
    private MappingAction togglePopulateDiagramFromTreeSelectionAction;

    private MappingAction expandAllEnumsAction;
    private MappingAction collapseAllEnumsAction;

    private GenerateMappingClassesAction generateMappingClassesAction;
    private MappingAction lockAction;

    private RefreshAction refreshDiagramAction;

    private AbstractAction saveDiagramAction;
    private ShowParentDiagramAction upDiagramAction;

    private AddTransformationSourceAction addSourcesAction;
    private AddUnionSourceAction addUnionSourcesAction;
    private TransformationAction removeSourcesAction;
    private TransformationAction clearSourcesAction;
    private TransformationAction reconcileAction;
    private TransformationAction addToSqlSelectAction;
    private TransformationAction addJoinExpressionAction;

    // overrides of global actions
    private MappingAction deleteAction;
    private MappingAction cutAction;
    private MappingAction copyAction;
    private MappingAction pasteAction;
    private MappingAction cloneAction;
    private RenameAction renameAction;
    private AddInputSetParameterAction addInputSetParameterAction;
    private EditInputSetAction editInputSetAction;

    // actions map is needed since we want to override the default print action
    private ModelerGlobalActionsMap actionsMap;

    /**
     * @since 4.0
     */
    public MappingDiagramActionAdapter( final ModelEditorPage page ) {
        super(page);
    }

    private void addDiagramActions( final IMenuManager theMenuMgr ) {
        if (isDetailedMapping()) {
            theMenuMgr.add(new Separator());
            theMenuMgr.add(new GroupMarker(D_MARKER));
            theMenuMgr.appendToGroup(D_MARKER, this.refreshDiagramAction);
            theMenuMgr.appendToGroup(D_MARKER, this.upDiagramAction);
            theMenuMgr.appendToGroup(D_MARKER, this.saveDiagramAction);
        } else {
            theMenuMgr.add(new Separator());
            theMenuMgr.add(new GroupMarker(D_MARKER));
            theMenuMgr.appendToGroup(D_MARKER, this.refreshDiagramAction);
            theMenuMgr.appendToGroup(D_MARKER, this.saveDiagramAction);
        }
    }

    private void addExternalExportedActions( final IMenuManager theMenuMgr,
                                             final ISelection selection ) {
        final List<Object> contributors = getModelObjectActionContributors();

        for (int size = contributors.size(), i = 0; i < size; i++) {
            final IModelObjectActionContributor contributor = (IModelObjectActionContributor)contributors.get(i);
            contributor.contributeToContextMenu(theMenuMgr, selection);
        }
    }

    private void contributeCoarseActions( final IMenuManager theMenuMgr,
                                          final ISelection selection ) {
        // Coarse mapping diagram Only extents, mapping classes, staging tables, and links can be
        // selected here.

        final MappingSelectionHelper selectionHelper = new MappingSelectionHelper(selection);

        if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_SINGLE) switch (selectionHelper.getType()) {
            case TransformationSelectionHelper.TYPE_TARGET_TABLE:
            case MappingSelectionHelper.TYPE_MAPPING_CLASS:
            case MappingSelectionHelper.TYPE_STAGING_TABLE:
            case MappingSelectionHelper.TYPE_STAGING_TABLE_CHILD:
            case MappingSelectionHelper.TYPE_MAPPING_CLASS_CHILD:
            case TransformationSelectionHelper.TYPE_TARGET_CHILD: {
                resetTransformationActions();

                // only show if not in a message structure diagram
                if (!ModelIdentifier.isLogicalModel(((DiagramEditor)getEditorPage()).getCurrentModelResource())) {
                    theMenuMgr.add(new Separator());
                    theMenuMgr.add(new GroupMarker(M_MARKER));
                    theMenuMgr.appendToGroup(M_MARKER, this.showDetailedMappingAction);
                    theMenuMgr.appendToGroup(M_MARKER, this.generateMappingClassesAction);
                    theMenuMgr.appendToGroup(M_MARKER, this.splitMappingClassAction);
                    theMenuMgr.appendToGroup(M_MARKER, this.lockAction);
                }

                addDiagramActions(theMenuMgr);
                // addExtendedActions(theMenuMgr);
            }
                break;

            case TransformationSelectionHelper.TYPE_INPUT_SET:
            case TransformationSelectionHelper.TYPE_SOURCE_TABLE:
            case TransformationSelectionHelper.TYPE_SOURCE_CHILD:
            case TransformationSelectionHelper.TYPE_RESULT_SET:
            case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT:
            case TransformationSelectionHelper.TYPE_UNKNOWN: {

            }
                break;

            case TransformationSelectionHelper.TYPE_DIAGRAM: {
                resetTransformationActions();
                // only show if not in a message structure diagram
                if (ModelIdentifier.isLogicalModel(((DiagramEditor)getEditorPage()).getCurrentModelResource())) {
                    theMenuMgr.add(this.expandAllEnumsAction);
                    theMenuMgr.add(this.collapseAllEnumsAction);
                }
                addDiagramActions(theMenuMgr);
            }
                break;

            case MappingSelectionHelper.TYPE_ENUM_TYPE:
            case MappingSelectionHelper.TYPE_ENUM_VALUE: {
                theMenuMgr.add(this.expandAllEnumsAction);
                theMenuMgr.add(this.collapseAllEnumsAction);
                theMenuMgr.add(new Separator());
                final IAction openAction = getAction(ModelerGlobalActions.OPEN);
                if (openAction != null) theMenuMgr.add(openAction);

                break;
            }

            default:
                break;
        }
        else if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_MULTIPLE_SAME) switch (selectionHelper.getType()) {
            case MappingSelectionHelper.TYPE_STAGING_TABLE:
            case MappingSelectionHelper.TYPE_STAGING_TABLE_CHILD:
            case MappingSelectionHelper.TYPE_MAPPING_CLASS:
            case MappingSelectionHelper.TYPE_MAPPING_CLASS_CHILD:
            case TransformationSelectionHelper.TYPE_TARGET_CHILD:
            case TransformationSelectionHelper.TYPE_TARGET_TABLE: {
                resetTransformationActions();
                theMenuMgr.add(new Separator());
                theMenuMgr.add(new GroupMarker(M_MARKER));
                theMenuMgr.appendToGroup(M_MARKER, this.showDetailedMappingAction);
                theMenuMgr.appendToGroup(M_MARKER, this.generateMappingClassesAction);
                theMenuMgr.appendToGroup(M_MARKER, this.splitMappingClassAction);
                theMenuMgr.appendToGroup(M_MARKER, this.mergeMappingClassesAction);
                theMenuMgr.appendToGroup(M_MARKER, this.toggleDisplayAllMappingClassesAction);
                theMenuMgr.appendToGroup(M_MARKER, this.toggleFoldAllMappingClassesAction);
                // Defect 20604: removing this 'sync tree and diagram' action for now;
                // retargeted to 5.0 SP1.
                // theMenuMgr.appendToGroup(M_MARKER, this.toggleSyncTreeAndDiagramExpandsAction);
                theMenuMgr.appendToGroup(M_MARKER, this.togglePopulateDiagramFromTreeSelectionAction);

                addDiagramActions(theMenuMgr);
                // Defect 14333 fix. removing next line... was causing duplicate extended actions.
                // addExtendedActions(theMenuMgr);
            }
                break;

            case TransformationSelectionHelper.TYPE_INPUT_SET:
            case TransformationSelectionHelper.TYPE_SOURCE_TABLE:
            case TransformationSelectionHelper.TYPE_SOURCE_CHILD:
            case TransformationSelectionHelper.TYPE_RESULT_SET:
            case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT:
            case TransformationSelectionHelper.TYPE_UNKNOWN: {

            }
                break;

            default:
                break;
        }
        else if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_MULTIPLE_MIXED) {
            resetTransformationActions();
            addDiagramActions(theMenuMgr);
        }
        theMenuMgr.add(new Separator());
    }

    private void contributeDetailedActions( final IMenuManager theMenuMgr,
                                            final ISelection selection ) {
        if (isDetailedMapping()) {
            final Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
            final EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

            final MappingSelectionHelper selectionHelper = new MappingSelectionHelper(transformationEObject, selection);

            if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_SINGLE) switch (selectionHelper.getType()) {
                case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT: {
                    resetTransformationActions();
                    final IAction editAction = getAction(ModelerGlobalActions.EDIT);
                    if (editAction != null) theMenuMgr.add(editAction);
                    theMenuMgr.add(new Separator());
                    theMenuMgr.add(this.clearSourcesAction);
                    theMenuMgr.add(new Separator());
                    addDiagramActions(theMenuMgr);
                }
                    break;

                case TransformationSelectionHelper.TYPE_INPUT_SET: {
                    resetTransformationActions();
                    final IAction newChildAction = getAction(ModelerActionBarIdManager.getInsertChildMenuId());
                    if (newChildAction != null) theMenuMgr.add(newChildAction);
                    else {
                        // we need to make sure we can add input set columns
                        // Set the InputSet
                        addInputSetParameterAction.setInputSet(selectionHelper.getSingleEObject());
                        theMenuMgr.add(addInputSetParameterAction);
                    }
//                    final IAction editAction = getAction(ModelerGlobalActions.EDIT);
//                    if (editAction != null) theMenuMgr.add(editAction);
                    this.editInputSetAction.setDiagramEditor((DiagramEditor)getEditorPage());
                    theMenuMgr.add(editInputSetAction);
                    theMenuMgr.add(new Separator());
                    theMenuMgr.add(new GroupMarker(T_MARKER));
                    theMenuMgr.appendToGroup(T_MARKER, this.clearSourcesAction);
                    theMenuMgr.add(new Separator());
                    addDiagramActions(theMenuMgr);
                }
                    break;

                case TransformationSelectionHelper.TYPE_RESULT_SET: {
                    resetTransformationActions();
                    addDiagramActions(theMenuMgr);
                }
                    break;

                case MappingSelectionHelper.TYPE_STAGING_TABLE_CHILD:
                case TransformationSelectionHelper.TYPE_SOURCE_CHILD: {
                    resetTransformationActions();
                    final IAction existingAction = getAction(ModelerGlobalActions.OPEN);
                    if (existingAction != null) theMenuMgr.add(existingAction);
                    theMenuMgr.add(new Separator());
                    theMenuMgr.add(new GroupMarker(T_MARKER));
                    theMenuMgr.appendToGroup(T_MARKER, this.removeSourcesAction);
                    theMenuMgr.appendToGroup(T_MARKER, this.clearSourcesAction);
                    theMenuMgr.appendToGroup(T_MARKER, this.addToSqlSelectAction);
                    theMenuMgr.add(new Separator(ContextMenu.TRANS_END));
                    // Defect 24443
                    final MenuManager copyMenu = getCopyNameSubMenu(selection);
                    if (copyMenu != null) {
                        theMenuMgr.add(copyMenu);
                        theMenuMgr.add(new Separator());
                    }
                }
                    break;
                case MappingSelectionHelper.TYPE_STAGING_TABLE:
                case TransformationSelectionHelper.TYPE_SOURCE_TABLE: {
                    resetTransformationActions();
                    final IAction existingAction = getAction(ModelerGlobalActions.OPEN);
                    if (existingAction != null) theMenuMgr.add(existingAction);
                    theMenuMgr.add(new Separator());
                    theMenuMgr.add(new GroupMarker(T_MARKER));
                    theMenuMgr.appendToGroup(T_MARKER, this.addSourcesAction);
                    theMenuMgr.appendToGroup(T_MARKER, this.removeSourcesAction);
                    theMenuMgr.appendToGroup(T_MARKER, this.clearSourcesAction);
                    if (selectionHelper.isVirtual()) addExternalExportedActions(theMenuMgr, selection);
                    theMenuMgr.add(new Separator(ContextMenu.TRANS_END));
                    // Defect 24443
                    final MenuManager copyMenu = getCopyNameSubMenu(selection);
                    if (copyMenu != null) {
                        theMenuMgr.add(copyMenu);
                        theMenuMgr.add(new Separator());
                    }
                }
                    break;

                case TransformationSelectionHelper.TYPE_TARGET_CHILD:
                case MappingSelectionHelper.TYPE_MAPPING_CLASS_CHILD:
                case TransformationSelectionHelper.TYPE_TARGET_TABLE:
                case MappingSelectionHelper.TYPE_MAPPING_CLASS: {
                    resetTransformationActions();
                    theMenuMgr.add(new GroupMarker(M_MARKER));
                    theMenuMgr.appendToGroup(M_MARKER, this.lockAction);
                    addDiagramActions(theMenuMgr);
                }
                    break;

                case MappingSelectionHelper.TYPE_EXTENT: {
                    resetTransformationActions();
                    theMenuMgr.add(new GroupMarker(M_MARKER));
                    theMenuMgr.appendToGroup(M_MARKER, this.deleteMappingLinksAction);
                    addDiagramActions(theMenuMgr);
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
            else if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_MULTIPLE_SAME) switch (selectionHelper.getType()) {
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

                case MappingSelectionHelper.TYPE_STAGING_TABLE_CHILD:
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

                case MappingSelectionHelper.TYPE_STAGING_TABLE:
                case TransformationSelectionHelper.TYPE_SOURCE_TABLE: {
                    resetTransformationActions();
                    final IAction existingAction = getAction(ModelerGlobalActions.OPEN);
                    if (existingAction != null) theMenuMgr.add(existingAction);
                    theMenuMgr.add(new Separator());
                    theMenuMgr.add(new GroupMarker(T_MARKER));
                    theMenuMgr.appendToGroup(T_MARKER, this.removeSourcesAction);
                    theMenuMgr.appendToGroup(T_MARKER, this.clearSourcesAction);

                    if (selectionHelper.isVirtual()) addExternalExportedActions(theMenuMgr, selection);
                    theMenuMgr.add(new Separator());
                }
                    break;

                case MappingSelectionHelper.TYPE_EXTENT: {
                    resetTransformationActions();
                    theMenuMgr.add(new GroupMarker(M_MARKER));
                    theMenuMgr.appendToGroup(M_MARKER, this.deleteMappingLinksAction);
                    addDiagramActions(theMenuMgr);
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
            else if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_MULTIPLE_MIXED) {
                resetTransformationActions();
                if (selectionHelper.someExtentSelected()) {
                    theMenuMgr.add(new Separator());
                    theMenuMgr.add(new GroupMarker(M_MARKER));
                    theMenuMgr.appendToGroup(M_MARKER, this.newMappingLinkAction);
                }
                addDiagramActions(theMenuMgr);
            }

        }
        theMenuMgr.add(new Separator());
    }

    /**
     * @see org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void contributeExportedActions( final IMenuManager theMenuMgr ) {
        if (isDetailedMapping()) {
            resetTransformationActions();

            // check to see if menu is the TempMenuID menu or just a context menu
            if ((theMenuMgr.getId() != null) && theMenuMgr.getId().equals("TempMenuID")) {
                setEditMenu(theMenuMgr); // need this in dispose()

                theMenuMgr.add(this.addSourcesAction);
                theMenuMgr.add(this.addUnionSourcesAction);
            } else {
                if (this.addSourcesAction.isEnabled()) theMenuMgr.add(this.addSourcesAction);
                if (this.addUnionSourcesAction.isEnabled()) theMenuMgr.add(this.addUnionSourcesAction);
            }
        }
    }

    /**
     * @see org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#contributeToDiagramToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    @Override
    public void contributeToDiagramToolBar() {
        final boolean logicalModel = ModelIdentifier.isLogicalModel(((DiagramEditor)getEditorPage()).getCurrentModelResource());
        final DiagramToolBarManager tbm = (DiagramToolBarManager)((DiagramEditor)getEditorPage()).getToolBarManager();

        tbm.removeAll();

        tbm.add(this.refreshDiagramAction);
        tbm.add(this.upDiagramAction);

        if (!isDetailedMapping() && !logicalModel) tbm.add(this.showDetailedMappingAction);

        this.newMappingLinkAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.deleteMappingLinksAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.mergeMappingClassesAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.splitMappingClassAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.toggleDisplayAllMappingClassesAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.toggleFoldAllMappingClassesAction.setDiagramEditor((DiagramEditor)getEditorPage());
        // Defect 20604: removing this 'sync tree and diagram' action for now;
        // retargeted to 5.0 SP1.
        // this.toggleSyncTreeAndDiagramExpandsAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.togglePopulateDiagramFromTreeSelectionAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.refreshDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());
        this.upDiagramAction.setDiagramEditor((DiagramEditor)getEditorPage());

        if (isDetailedMapping()) {
            tbm.add(new Separator());
            tbm.add(this.newMappingLinkAction);
            tbm.add(this.deleteMappingLinksAction);

            resetTransformationActions();

            tbm.add(new Separator());
            tbm.add(this.addSourcesAction);
            tbm.add(this.addUnionSourcesAction);
            tbm.add(this.removeSourcesAction);
            tbm.add(this.clearSourcesAction);
            tbm.add(this.reconcileAction);

            this.addSourcesAction.setToolBarManager(tbm);
            final ActionContributionItem addSourcesItem = new ActionContributionItem(this.addSourcesAction);
            this.addSourcesAction.setItem(addSourcesItem);

            this.addUnionSourcesAction.setToolBarManager(tbm);
            final ActionContributionItem addUnionSourcesItem = new ActionContributionItem(this.addUnionSourcesAction);
            this.addUnionSourcesAction.setItem(addUnionSourcesItem);

            tbm.add(new Separator());
            tbm.add(this.saveDiagramAction);
        } else {
            tbm.add(new Separator());

            if (!logicalModel) {
                tbm.add(this.generateMappingClassesAction);
                tbm.add(this.newMappingClassAction);
                tbm.add(this.newStagingTableAction);
                tbm.add(this.mergeMappingClassesAction);
                tbm.add(this.splitMappingClassAction);
                tbm.add(new Separator());
            }

            tbm.add(this.toggleDisplayAllMappingClassesAction);
            tbm.add(this.toggleFoldAllMappingClassesAction);
            // Defect 20604: removing this 'sync tree and diagram' action for now;
            // retargeted to 5.0 SP1.
            // tbm.add(this.toggleSyncTreeAndDiagramExpandsAction);
            tbm.add(this.togglePopulateDiagramFromTreeSelectionAction);
        }

        wireMappingClassActions();

        tbm.update(true);

        // Need to keep the global actions map in synch
        resetGlobalActionsMap();

    }

    @Override
    public void contributeToMenuManager( final IMenuManager theMenuMgr,
                                         final ISelection selection ) {

        removeAllActions(theMenuMgr);

        if (isDetailedMapping()) contributeDetailedActions(theMenuMgr, selection);
        else {
            processDefaultCoarseMenu(theMenuMgr, selection);
            contributeCoarseActions(theMenuMgr, selection);
        }
    }

    /**
     * @see org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
     */
    @Override
    public void createContextMenu() {
        createContextMenu(ContextMenu.DIAGRAM_EDITOR_PAGE, getEditorPage().getControl());
    }

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        unwireMappingClassActions();

        super.dispose();

        this.reconcileAction.dispose();
        this.clearSourcesAction.dispose();
    }

    /**
     * @see org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#enableDiagramToolbarActions()
     */
    @Override
    public void enableDiagramToolbarActions() {
        if (this.upDiagramAction != null) this.upDiagramAction.determineEnablement();
    }

    private IAction getAction( final String theActionId ) {
        IAction action = null;
        try {
            action = getActionService().getAction(theActionId);
        } catch (final CoreException err) {
        }

        return action;
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

    /**
     * @see org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor#getGlobalActions()
     */
    @Override
    public GlobalActionsMap getGlobalActions() {
        if (actionsMap == null) resetGlobalActionsMap();

        return actionsMap;
    }

    /**
     * Gets a list of the extension for the ModelObjectActionContributor extension point. This was copied from
     * ModelerActionService and changed to only process the trasformation.ui contributor to find the showDependencyAction.
     * 
     * @return the list of <code>IModelObjectActionContributor</code> implementations
     */
    private List<Object> getModelObjectActionContributors() {
        if (modelObjectContributors == null) {
            final String ID = org.teiid.designer.ui.UiConstants.ExtensionPoints.ModelObjectActionContributor.ID;
            final String CLASSNAME = org.teiid.designer.ui.UiConstants.ExtensionPoints.ModelObjectActionContributor.CLASSNAME;
            // get the ModelObjectActionContributor extension point from the plugin class
            final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(org.teiid.designer.ui.UiConstants.PLUGIN_ID,
                                                                                                     ID);

            // get the all extensions to the ModelObjectActionContributor extension point
            final IExtension[] extensions = extensionPoint.getExtensions();

            if (extensions.length > 0) {
                modelObjectContributors = new ArrayList<Object>(extensions.length);

                // for each extension get their contributor
                for (final IExtension extension2 : extensions) {
                    final IConfigurationElement[] elements = extension2.getConfigurationElements();
                    Object extension = null;
                    final String uniqueID = extension2.getSimpleIdentifier();
                    if (uniqueID.startsWith("transformationDiagramModelObjectActionContributor")) for (final IConfigurationElement element : elements) //$NON-NLS-1$
                        try {
                            extension = element.createExecutableExtension(CLASSNAME);

                            if (extension instanceof IModelObjectActionContributor) modelObjectContributors.add(extension);
                            else org.teiid.designer.ui.UiConstants.Util.log(IStatus.ERROR,
                                                                                org.teiid.designer.ui.UiConstants.Util.getString("ModelerActionService.wrongContributorClass", //$NON-NLS-1$
                                                                                                                                     new Object[] {extension.getClass().getName()}));
                        } catch (final Exception theException) {
                            org.teiid.designer.ui.UiConstants.Util.log(IStatus.ERROR,
                                                                           theException,
                                                                           org.teiid.designer.ui.UiConstants.Util.getString("ModelerActionService.contributorProblem", //$NON-NLS-1$
                                                                                                                                new Object[] {element.getAttribute(CLASSNAME)}));
                        }
                }
            } else modelObjectContributors = Collections.emptyList();
        }

        return modelObjectContributors;
    }

    private TreeMappingAdapter getTreeMappingAdapter() {
        final MappingDiagramController controller = (MappingDiagramController)((DiagramEditor)getEditorPage()).getDiagramController();
        if (controller != null) return controller.getMappingAdapter();
        return null;
    }

    /**
     * @see org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#handleNotification(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public void handleNotification( final Notification theNotification ) {
        // if the target or notifier is a MappingClassSet, then we should tell the GenerateMappingClassesAction to
        // reassess it's enablement
        final Object changedObject = ModelerCore.getModelEditor().getChangedObject(theNotification);
        if (changedObject instanceof MappingClassSet) this.generateMappingClassesAction.determineEnablement();

        // Need to look for Deletes where the target is a MappingClassSet,
        if (shouldUpdateMappingClassFactory(theNotification)) wireMappingClassActions();

    }

    /**
     * @since 4.0
     */
    @Override
    protected void initActions() {
        super.initActions();

        final Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
        EObject transformationEObject = null;

        if (isDetailedMapping()) transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

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

        // ----- AddJoinExpressionAction -----//
        this.addJoinExpressionAction = (TransformationAction)getRegisteredAction(AddJoinExpressionAction.class.getName());
        if (this.addJoinExpressionAction == null) {
            this.addJoinExpressionAction = new AddJoinExpressionAction(transformationEObject, currentDiagram);
            registerAction(this.addJoinExpressionAction);
        }

        // ** MAPPING ACTIONS **************************************************
        // ----- ShowDetailedMappingDiagramAction -----//
        this.showDetailedMappingAction = (AbstractAction)getRegisteredAction(ShowDetailedMappingDiagramAction.class.getName());
        if (this.showDetailedMappingAction == null) {
            this.showDetailedMappingAction = new ShowDetailedMappingDiagramAction();
            registerAction(this.showDetailedMappingAction);
        }

        // ----- NewMappingClassAction -----//
        this.newMappingClassAction = (MappingAction)getRegisteredAction(NewMappingClassAction.class.getName());
        if (this.newMappingClassAction == null) {
            this.newMappingClassAction = new NewMappingClassAction();
            registerAction(this.newMappingClassAction);
        }

        // ----- NewStagingTableAction -----//
        this.newStagingTableAction = (MappingAction)getRegisteredAction(NewStagingTableAction.class.getName());
        if (this.newStagingTableAction == null) {
            this.newStagingTableAction = new NewStagingTableAction();
            registerAction(this.newStagingTableAction);
        }

        // ----- NewMappingLinkAction -----//
        this.newMappingLinkAction = (MappingAction)getRegisteredAction(NewMappingLinkAction.class.getName());
        if (this.newMappingLinkAction == null) {
            this.newMappingLinkAction = new NewMappingLinkAction();
            registerAction(this.newMappingLinkAction);
        }

        // ----- DeleteMappingLinksAction -----//
        this.deleteMappingLinksAction = (MappingAction)getRegisteredAction(DeleteMappingLinksAction.class.getName());
        if (this.deleteMappingLinksAction == null) {
            this.deleteMappingLinksAction = new DeleteMappingLinksAction();
            registerAction(this.deleteMappingLinksAction);
        }

        // ----- MergeMappingClassesAction -----//
        this.mergeMappingClassesAction = (MappingAction)getRegisteredAction(MergeMappingClassesAction.class.getName());
        if (this.mergeMappingClassesAction == null) {
            this.mergeMappingClassesAction = new MergeMappingClassesAction();
            registerAction(this.mergeMappingClassesAction);
        }

        // ----- SplitMappingClassAction -----//
        this.splitMappingClassAction = (MappingAction)getRegisteredAction(SplitMappingClassAction.class.getName());
        if (this.splitMappingClassAction == null) {
            this.splitMappingClassAction = new SplitMappingClassAction();
            registerAction(this.splitMappingClassAction);
        }

        // ----- ToggleDisplayAllMappingClassesAction -----//
        this.toggleDisplayAllMappingClassesAction = (MappingAction)getRegisteredAction(ToggleDisplayAllMappingClassesAction.class.getName());
        if (this.toggleDisplayAllMappingClassesAction == null) {
            this.toggleDisplayAllMappingClassesAction = new ToggleDisplayAllMappingClassesAction();
            registerAction(this.toggleDisplayAllMappingClassesAction);
        }

        // ----- ToggleFoldAllMappingClassesAction -----//
        this.toggleFoldAllMappingClassesAction = (MappingAction)getRegisteredAction(ToggleFoldAllMappingClassesAction.class.getName());
        if (this.toggleFoldAllMappingClassesAction == null) {
            this.toggleFoldAllMappingClassesAction = new ToggleFoldAllMappingClassesAction();
            registerAction(this.toggleFoldAllMappingClassesAction);
        }

        // ----- TogglePopulateDiagramFromTreeSelectionAction -----//
        this.togglePopulateDiagramFromTreeSelectionAction = (MappingAction)getRegisteredAction(TogglePopulateDiagramFromTreeSelectionAction.class.getName());
        if (this.togglePopulateDiagramFromTreeSelectionAction == null) {
            this.togglePopulateDiagramFromTreeSelectionAction = new TogglePopulateDiagramFromTreeSelectionAction();
            registerAction(this.togglePopulateDiagramFromTreeSelectionAction);
        }

        // EXPAND & COLLAPSE ENUMs in Message Structure models...
        // ----- ExpandAllEnumsAction -----//
        this.expandAllEnumsAction = (MappingAction)getRegisteredAction(ExpandAllEnumsAction.class.getName());
        if (this.expandAllEnumsAction == null) {
            this.expandAllEnumsAction = new ExpandAllEnumsAction();
            registerAction(this.expandAllEnumsAction);
        }

        // ----- CollapseAllEnumsAction -----//
        this.collapseAllEnumsAction = (MappingAction)getRegisteredAction(CollapseAllEnumsAction.class.getName());
        if (this.collapseAllEnumsAction == null) {
            this.collapseAllEnumsAction = new CollapseAllEnumsAction();
            registerAction(this.collapseAllEnumsAction);
        }

        // ----- GenerateMappingClassesAction -----//
        this.generateMappingClassesAction = (GenerateMappingClassesAction)getRegisteredAction(GenerateMappingClassesAction.class.getName());
        if (this.generateMappingClassesAction == null) {
            this.generateMappingClassesAction = new GenerateMappingClassesAction();
            registerAction(this.generateMappingClassesAction);
        }

        // ----- LockAction -----//
        this.lockAction = (MappingAction)getRegisteredAction(LockAction.class.getName());
        if (this.lockAction == null) {
            this.lockAction = new LockAction();
            registerAction(this.lockAction);
        }

        // ** GLOBAL EDIT OVERRIDE ACTIONS **************************************************
        // ----- DeleteAction -----//
        this.deleteAction = (MappingAction)getRegisteredAction(DeleteAction.class.getName());
        if (this.deleteAction == null) {
            this.deleteAction = new DeleteAction(transformationEObject);
            registerAction(this.deleteAction);
        }
        this.deleteAction.setDetailed(isDetailedMapping());

        // ----- CutAction -----//
        this.cutAction = (MappingAction)getRegisteredAction(CutAction.class.getName());
        if (this.cutAction == null) {
            this.cutAction = new CutAction(transformationEObject);
            registerAction(this.cutAction);
        }

        // ----- CopyAction -----//
        this.copyAction = (MappingAction)getRegisteredAction(CopyAction.class.getName());
        if (this.copyAction == null) {
            this.copyAction = new CopyAction(transformationEObject);
            registerAction(this.copyAction);
        }

        // ----- PasteAction -----//
        this.pasteAction = (MappingAction)getRegisteredAction(PasteAction.class.getName());
        if (this.pasteAction == null) {
            this.pasteAction = new PasteAction(transformationEObject);
            registerAction(this.pasteAction);
        }

        // ----- CloneAction -----//
        this.cloneAction = (MappingAction)getRegisteredAction(CloneAction.class.getName());
        if (this.cloneAction == null) {
            this.cloneAction = new CloneAction(transformationEObject);
            registerAction(this.cloneAction);
        }

        // ----- RenameAction -----//
        this.renameAction = (RenameAction)getRegisteredAction(RenameAction.class.getName());
        if (this.renameAction == null) {
            this.renameAction = new RenameAction();
            registerAction(this.renameAction);
        }

        // ** DIAGRAM RELATED ACTIONS **************************************************
        // ----- RefreshAction -----//
        this.refreshDiagramAction = (RefreshAction)getRegisteredAction(RefreshAction.class.getName());
        if (this.refreshDiagramAction == null) {
            this.refreshDiagramAction = new RefreshAction();
            registerAction(this.refreshDiagramAction);
        }

        // ----- ShowParentDiagramAction -----//
        this.upDiagramAction = (ShowParentDiagramAction)getRegisteredAction(ShowParentDiagramAction.class.getName());
        if (this.upDiagramAction == null) {
            this.upDiagramAction = new ShowParentDiagramAction();
            registerAction(this.upDiagramAction);
        }

        // ----- SaveMappingDiagramAction -----//
        this.saveDiagramAction = (AbstractAction)getRegisteredAction(SaveMappingDiagramAction.class.getName());
        if (this.saveDiagramAction == null) {
            this.saveDiagramAction = new SaveMappingDiagramAction((DiagramEditor)this.getEditorPage());
            registerAction(this.saveDiagramAction);
        }

        this.addInputSetParameterAction = new AddInputSetParameterAction();
        this.editInputSetAction = new EditInputSetAction();
    }

    private boolean isDetailedMapping() {
        final MappingDiagramController controller = (MappingDiagramController)((DiagramEditor)getEditorPage()).getDiagramController();
        if (controller != null) return controller.getMappingType() == PluginConstants.DETAILED_MAPPING;

        return false;
    }

    private boolean objectsResourceSameAsDiagram( final EObject eObject ) {
        final Diagram diagram = ((DiagramEditor)getEditorPage()).getDiagram();
        if (diagram != null) return ModelUtilities.areModelResourcesSame(eObject, diagram);

        return false;
    }

    /**
     * Processes the default context menu.
     * 
     * @param theMenuMgr the context menu
     * @param theSelection the selection the menu pertains to
     * @since 5.0.2
     */
    private void processDefaultCoarseMenu( final IMenuManager theMenuMgr,
                                           final ISelection theSelection ) {
        if (theSelection != null && !theSelection.isEmpty()
            && ModelIdentifier.isLogicalModel(((DiagramEditor)getEditorPage()).getCurrentModelResource())
            && (theSelection instanceof IStructuredSelection)) {

            IContributionManager menuMgr = null;

            if (((IStructuredSelection)theSelection).getFirstElement() instanceof MappingClass) {
                final IContributionItem newChildMenu = theMenuMgr.find(ModelerActionBarIdManager.getInsertChildMenuId());

                if ((newChildMenu != null) && (newChildMenu instanceof IContributionManager)) menuMgr = (IContributionManager)newChildMenu;
            } else if (((IStructuredSelection)theSelection).getFirstElement() instanceof MappingClassColumn) {
                final IContributionItem newSiblingMenu = theMenuMgr.find(ModelerActionBarIdManager.getInsertSiblingMenuId());

                if ((newSiblingMenu != null) && (newSiblingMenu instanceof IContributionManager)) menuMgr = (IContributionManager)newSiblingMenu;
            }

            if (menuMgr != null) {
                final String DEFAULT_LABEL = TransformationPlugin.Util.getString("_UI_MappingClassColumn_type"); //$NON-NLS-1$
                final String NEW_LABEL = UiConstants.Util.getString("MappingDiagramActionAdapter.logicalNewMappingClassColumn"); //$NON-NLS-1$
                final IContributionItem[] items = menuMgr.getItems();

                if ((items != null) && (items.length != 0)) for (int i = 0; i < items.length; ++i)
                    if (items[i] instanceof ActionContributionItem) {
                        final IAction action = ((ActionContributionItem)items[i]).getAction();

                        if (DEFAULT_LABEL.equals(action.getText())) {
                            action.setText(NEW_LABEL);
                            break;
                        }
                    }
            }
        }
    }

    private void removeAllActions( final IMenuManager theMenuMgr ) {
        // clean up old menu items first.
        removeMappingActions(theMenuMgr);
        removeTransformationActions(theMenuMgr);
        removeDiagramActions(theMenuMgr);
    }

    private void removeDiagramActions( final IMenuManager theMenuMgr ) {
        if (theMenuMgr.find(D_MARKER) != null) theMenuMgr.remove(D_MARKER);
        if (theMenuMgr.find(this.refreshDiagramAction.getId()) != null) theMenuMgr.remove(this.refreshDiagramAction.getId());
        if (theMenuMgr.find(this.upDiagramAction.getId()) != null) theMenuMgr.remove(this.upDiagramAction.getId());
        if (theMenuMgr.find(this.saveDiagramAction.getId()) != null) theMenuMgr.remove(this.saveDiagramAction.getId());
    }

    private void removeMappingActions( final IMenuManager theMenuMgr ) {
        if (theMenuMgr.find(M_MARKER) != null) theMenuMgr.remove(M_MARKER);
        if (theMenuMgr.find(this.showDetailedMappingAction.getId()) != null) theMenuMgr.remove(this.showDetailedMappingAction.getId());
        if (theMenuMgr.find(this.generateMappingClassesAction.getId()) != null) theMenuMgr.remove(this.generateMappingClassesAction.getId());
        if (theMenuMgr.find(this.newMappingClassAction.getId()) != null) theMenuMgr.remove(this.newMappingClassAction.getId());
        if (theMenuMgr.find(this.splitMappingClassAction.getId()) != null) theMenuMgr.remove(this.splitMappingClassAction.getId());

        if (theMenuMgr.find(this.toggleDisplayAllMappingClassesAction.getId()) != null) theMenuMgr.remove(this.toggleDisplayAllMappingClassesAction.getId());
        if (theMenuMgr.find(this.toggleFoldAllMappingClassesAction.getId()) != null) theMenuMgr.remove(this.toggleFoldAllMappingClassesAction.getId());

        // Defect 20604: removing this 'sync tree and diagram' action for now;
        // retargeted to 5.0 SP1.
        // if ( theMenuMgr.find( this.toggleSyncTreeAndDiagramExpandsAction.getId() ) != null )
        // theMenuMgr.remove( this.toggleSyncTreeAndDiagramExpandsAction.getId() );
        if (theMenuMgr.find(this.togglePopulateDiagramFromTreeSelectionAction.getId()) != null) theMenuMgr.remove(this.togglePopulateDiagramFromTreeSelectionAction.getId());
        if (theMenuMgr.find(this.expandAllEnumsAction.getId()) != null) theMenuMgr.remove(this.expandAllEnumsAction.getId());
        if (theMenuMgr.find(this.collapseAllEnumsAction.getId()) != null) theMenuMgr.remove(this.collapseAllEnumsAction.getId());

        if (theMenuMgr.find(this.mergeMappingClassesAction.getId()) != null) theMenuMgr.remove(this.mergeMappingClassesAction.getId());
        if (theMenuMgr.find(this.newStagingTableAction.getId()) != null) theMenuMgr.remove(this.newStagingTableAction.getId());
        if (theMenuMgr.find(this.newMappingLinkAction.getId()) != null) theMenuMgr.remove(this.newMappingLinkAction.getId());
        if (theMenuMgr.find(this.deleteMappingLinksAction.getId()) != null) theMenuMgr.remove(this.deleteMappingLinksAction.getId());
        if (theMenuMgr.find(this.lockAction.getId()) != null) theMenuMgr.remove(this.lockAction.getId());
    }

    private void removeTransformationActions( final IMenuManager theMenuMgr ) {
        if (theMenuMgr.find(T_MARKER) != null) theMenuMgr.remove(T_MARKER);
        if (theMenuMgr.find(this.addSourcesAction.getId()) != null) theMenuMgr.remove(this.addSourcesAction.getId());
        if (theMenuMgr.find(this.addUnionSourcesAction.getId()) != null) theMenuMgr.remove(this.addUnionSourcesAction.getId());
        if (theMenuMgr.find(this.removeSourcesAction.getId()) != null) theMenuMgr.remove(this.removeSourcesAction.getId());
        if (theMenuMgr.find(this.clearSourcesAction.getId()) != null) theMenuMgr.remove(this.clearSourcesAction.getId());
    }

    private void resetGlobalActionsMap() {
        actionsMap = new DiagramGlobalActionsMap();
        actionsMap.reset();

        actionsMap.put(EclipseGlobalActions.DELETE, this.deleteAction);
        actionsMap.put(EclipseGlobalActions.CUT, this.cutAction);
        actionsMap.put(EclipseGlobalActions.COPY, this.copyAction);
        actionsMap.put(EclipseGlobalActions.PASTE, this.pasteAction);
        actionsMap.put(IModelerActionConstants.ModelerGlobalActions.CLONE, this.cloneAction);
        this.renameAction.setDiagramEditor((DiagramEditor)getEditorPage());
        actionsMap.put(EclipseGlobalActions.RENAME, this.renameAction);
    }

    /**
     * private method used to update all transformation actions so they maintain the reference to the transformation displayed in
     * the transformation diagram.
     */
    private void resetTransformationActions() {
        final Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
        EObject transformationEObject = null;

        if (isDetailedMapping()) transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

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
        this.lockAction.setTransformation(transformationEObject);

        this.addSourcesAction.setDiagram(currentDiagram);
        this.addUnionSourcesAction.setDiagram(currentDiagram);
        this.removeSourcesAction.setDiagram(currentDiagram);
        this.clearSourcesAction.setDiagram(currentDiagram);
        this.reconcileAction.setDiagram(currentDiagram);
        this.addToSqlSelectAction.setDiagram(currentDiagram);
        this.addJoinExpressionAction.setDiagram(currentDiagram);
    }

    /**
     * UiConstants.PLUGIN_ID,
     * 
     * @see org.teiid.designer.diagram.ui.editor.IDiagramActionAdapter#shouldOverrideMenu()
     * @since 4.2
     */
    @Override
    public boolean shouldOverrideMenu( final ISelection selection ) {
        boolean value = false;
        if (isDetailedMapping()) {
            final Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
            final EObject transformationEObject = TransformationSourceManager.getTransformationFromDiagram(currentDiagram);

            final MappingSelectionHelper selectionHelper = new MappingSelectionHelper(transformationEObject, selection);
            if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_SINGLE) switch (selectionHelper.getType()) {
                case TransformationSelectionHelper.TYPE_DIAGRAM:
                case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT:
                case TransformationSelectionHelper.TYPE_SOURCE_CHILD:
                case TransformationSelectionHelper.TYPE_SOURCE_TABLE:
                case MappingSelectionHelper.TYPE_STAGING_TABLE:
                case MappingSelectionHelper.TYPE_STAGING_TABLE_CHILD:
                case TransformationSelectionHelper.TYPE_INPUT_SET:
                case TransformationSelectionHelper.TYPE_RESULT_SET:
                case MappingSelectionHelper.TYPE_EXTENT:
                case MappingSelectionHelper.TYPE_COARSE_EXTENT: {
                    value = true;
                }
                    break;

                case TransformationSelectionHelper.TYPE_TARGET_CHILD:
                case TransformationSelectionHelper.TYPE_TARGET_TABLE:
                case TransformationSelectionHelper.TYPE_UNKNOWN:

                default:
                    break;
            }
            else if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_MULTIPLE_SAME) switch (selectionHelper.getType()) {

                case MappingSelectionHelper.TYPE_EXTENT:
                case MappingSelectionHelper.TYPE_COARSE_EXTENT: {
                    value = true;
                }
                    break;
                case MappingSelectionHelper.TYPE_STAGING_TABLE:
                case MappingSelectionHelper.TYPE_STAGING_TABLE_CHILD:
                case TransformationSelectionHelper.TYPE_SOURCE_CHILD:
                case TransformationSelectionHelper.TYPE_SOURCE_TABLE: {
                    value = true;
                }
                    break;

                // All these either aren't possible, or shouldn't be overridden
                case TransformationSelectionHelper.TYPE_INPUT_SET:
                case TransformationSelectionHelper.TYPE_RESULT_SET:
                case TransformationSelectionHelper.TYPE_SQL_TRANSFORMATION_ROOT:
                case TransformationSelectionHelper.TYPE_TARGET_CHILD:
                case TransformationSelectionHelper.TYPE_TARGET_TABLE:
                case TransformationSelectionHelper.TYPE_UNKNOWN:

                default:
                    break;
            }
        } else {
            final MappingSelectionHelper selectionHelper = new MappingSelectionHelper(selection);
            if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_SINGLE) switch (selectionHelper.getType()) {
                case TransformationSelectionHelper.TYPE_DIAGRAM:
                case MappingSelectionHelper.TYPE_EXTENT:
                case MappingSelectionHelper.TYPE_COARSE_EXTENT:
                case MappingSelectionHelper.TYPE_ENUM_TYPE:
                case MappingSelectionHelper.TYPE_ENUM_VALUE: {
                    value = true;
                }
                    break;

                default:
                    break;
            }
            else if (selectionHelper.getCountType() == TransformationSelectionHelper.COUNT_MULTIPLE_SAME) switch (selectionHelper.getType()) {
                case MappingSelectionHelper.TYPE_EXTENT:
                case MappingSelectionHelper.TYPE_COARSE_EXTENT:
                case MappingSelectionHelper.TYPE_ENUM_TYPE:
                case MappingSelectionHelper.TYPE_ENUM_VALUE: {
                    value = true;
                }
                    break;

                default:
                    break;
            }
        }

        return value;
    }

    public boolean shouldUpdateMappingClassFactory( final Notification theNotification ) {
        boolean update = false;

        if (theNotification instanceof SourcedNotification) {
            final Collection notifications = ((SourcedNotification)theNotification).getNotifications();
            final Iterator iter = notifications.iterator();
            Notification nextNot = null;
            while (iter.hasNext() && !update) {
                nextNot = (Notification)iter.next();
                final Object changedObject = ModelerCore.getModelEditor().getChangedObject(nextNot);
                if (changedObject instanceof EObject && objectsResourceSameAsDiagram((EObject)changedObject)) if (changedObject instanceof MappingClassSet
                                                                                                                  || changedObject instanceof TransformationContainer) update = true;
            }
        } else {
            final Object changedObject = ModelerCore.getModelEditor().getChangedObject(theNotification);
            if (changedObject instanceof EObject && objectsResourceSameAsDiagram((EObject)changedObject)) if (changedObject instanceof MappingClassSet
                                                                                                              || changedObject instanceof TransformationContainer) update = true;
        }

        return update;
    }

    private void unwireMappingClassActions() {
        this.generateMappingClassesAction.setMappingClassFactory(null);
        this.newMappingClassAction.setMappingClassFactory(null);
        this.newStagingTableAction.setMappingClassFactory(null);
        this.deleteMappingLinksAction.setMappingClassFactory(null);
        this.mergeMappingClassesAction.setMappingClassFactory(null);
        this.splitMappingClassAction.setMappingClassFactory(null);
        this.toggleDisplayAllMappingClassesAction.setMappingClassFactory(null);
        this.toggleFoldAllMappingClassesAction.setMappingClassFactory(null);
        // Defect 20604: removing this 'sync tree and diagram' action for now;
        // retargeted to 5.0 SP1.
        // this.toggleSyncTreeAndDiagramExpandsAction.setMappingClassFactory(null);
        this.togglePopulateDiagramFromTreeSelectionAction.setMappingClassFactory(null);
        this.deleteAction.setMappingClassFactory(null);
        this.lockAction.setMappingClassFactory(null);
    }

    private void wireMappingClassActions() {
        // Create MappingClassFactory

        final boolean isDetailed = isDetailedMapping();

        EObject targetEO = null;
        final Diagram currentDiagram = ((DiagramEditor)getEditorPage()).getDiagram();
        if (!isDetailed) // diagram's target should be the document tree root
        targetEO = currentDiagram.getTarget();
        else {
            // detailed mapping, target is mapping class
            final MappingClass mappingClass = (MappingClass)currentDiagram.getTarget();
            targetEO = mappingClass.getMappingClassSet().getTarget();
        }

        if (targetEO != null && ModelMapperFactory.isTreeRoot(targetEO)) {
            final ITreeToRelationalMapper ittrm = ModelMapperFactory.createModelMapper(targetEO);
            final MappingClassFactory mcf = new MappingClassFactory(ittrm, getTreeMappingAdapter());

            this.generateMappingClassesAction.setMappingClassFactory(mcf);
            this.newMappingClassAction.setMappingClassFactory(mcf);
            this.newStagingTableAction.setMappingClassFactory(mcf);
            this.mergeMappingClassesAction.setMappingClassFactory(mcf);
            this.splitMappingClassAction.setMappingClassFactory(mcf);
            this.toggleDisplayAllMappingClassesAction.setMappingClassFactory(mcf);
            this.toggleFoldAllMappingClassesAction.setMappingClassFactory(mcf);
            // Defect 20604: removing this 'sync tree and diagram' action for now;
            // retargeted to 5.0 SP1.
            // this.toggleSyncTreeAndDiagramExpandsAction.setMappingClassFactory(mcf);
            this.togglePopulateDiagramFromTreeSelectionAction.setMappingClassFactory(mcf);
            this.deleteMappingLinksAction.setMappingClassFactory(mcf);
            this.newMappingLinkAction.setMappingClassFactory(mcf);
            this.deleteAction.setMappingClassFactory(mcf);
            this.lockAction.setMappingClassFactory(mcf);
        }

        this.generateMappingClassesAction.setDetailed(isDetailed);
        this.newMappingClassAction.setDetailed(isDetailed);
        this.newStagingTableAction.setDetailed(isDetailed);
        this.mergeMappingClassesAction.setDetailed(isDetailed);
        this.splitMappingClassAction.setDetailed(isDetailed);
        this.deleteMappingLinksAction.setDetailed(isDetailed);
        this.newMappingLinkAction.setDetailed(isDetailed);
        this.deleteAction.setDetailed(isDetailed);
        this.lockAction.setDetailed(isDetailed);
    }

    public class AddInputSetParameterAction extends Action {
        private EObject inputSet;

        /**
         * @param thePlugin
         * @since 4.3
         */
        public AddInputSetParameterAction() {
            super();
            setText(NEW_INPUT_PARAMETER_TITLE);
        }

        /**
         * @return Returns the inputSet.
         * @since 4.3
         */
        public EObject getInputSet() {
            return this.inputSet;
        }

        /**
         * @see org.teiid.designer.ui.common.actions.AbstractAction#doRun()
         * @since 4.3
         */

        @Override
        public void run() {
            if (getInputSet() != null) {
                // create a new InputParameter in the InputSet
                final InputSet inputSet = (InputSet)getInputSet();

                final boolean started = ModelerCore.startTxn(true, true, NEW_INPUT_PARAMETER_TITLE, this);
                boolean succeeded = false;
                try {
                    int nameIndex = inputSet.getInputParameters().size();

                    final InputParameter param = TransformationFactory.eINSTANCE.createInputParameter();
                    param.setInputSet(inputSet);

                    // Set the name...
                    String name = PARAM_BASE_NAME + (++nameIndex);
                    // check for name clash with an existing parameter
                    boolean tryAgain = true;
                    while (tryAgain) {
                        tryAgain = false;
                        for (final Iterator iter = inputSet.getInputParameters().iterator(); iter.hasNext();)
                            if (name.equals(((InputParameter)iter.next()).getName())) {
                                name = PARAM_BASE_NAME + (++nameIndex);
                                tryAgain = true;
                            }
                    }
                    param.setName(name);

                    succeeded = true;
                } finally {
                    if (started) if (succeeded) ModelerCore.commitTxn();
                    else ModelerCore.rollbackTxn();
                }
            }
        }

        /**
         * @param inputSet The inputSet to set.
         * @since 4.3
         */
        public void setInputSet( final EObject inputSet ) {
            this.inputSet = inputSet;
        }

    }
}
