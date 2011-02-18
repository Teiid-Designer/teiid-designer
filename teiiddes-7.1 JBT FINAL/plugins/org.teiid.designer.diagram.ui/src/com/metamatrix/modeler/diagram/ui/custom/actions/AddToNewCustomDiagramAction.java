/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.diagram.ui.actions.DiagramAction;
import com.metamatrix.modeler.diagram.ui.connection.UmlRelationshipHelper;
import com.metamatrix.modeler.diagram.ui.custom.CustomDiagramContentHelper;
import com.metamatrix.modeler.diagram.ui.custom.CustomDiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class AddToNewCustomDiagramAction extends DiagramAction implements DiagramUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(AddToNewCustomDiagramAction.class);

    private static final String TARGET_SELECTION_DIALOG_TITLE = getString("containerSelectionDialogTitle"); //$NON-NLS-1$
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private static final String TARGET_SELECTION_DIALOG_MESSAGE = getString("containerSelectionDialogMessage"); //$NON-NLS-1$
    static final String INVALID_SELECTION_MESSAGE = getString("invalidSelectionMessage"); //$NON-NLS-1$
    static final String READ_ONLY_MODEL_SELECTION_MESSAGE = getString("readOnlyModelSelectionMessage"); //$NON-NLS-1$
    static final String READ_ONLY_OBJECT_SELECTION_MESSAGE = getString("readOnlyObjectSelectionMessage"); //$NON-NLS-1$
    private static final String LEVELS_SELECTION_DIALOG_TITLE = getString("levelsDialogTitle"); //$NON-NLS-1$

    private static final boolean PERSIST_CUSTOM_DIAGRAMS = true;
    private static final String THIS_CLASS = "AddToNewCustomDiagramAction"; //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * Construct an instance of NewCustomDiagramAction.
     */
    public AddToNewCustomDiagramAction() {
        super();
    }

    /**
     * Construct an instance of NewCustomDiagramAction.
     * 
     * @param theStyle
     */
    public AddToNewCustomDiagramAction( int theStyle ) {
        super(theStyle);
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged( final IWorkbenchPart part,
                                  final ISelection selection ) {
        super.selectionChanged(part, selection);
        determineEnablement();
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {

        // once that is returned, then we can go ahead and create custom diagram and
        // add objects to diagram.
        List selectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());

        if (selectedEObjects != null && !selectedEObjects.isEmpty()) {
            // Ask user to select model or package to create custom diagram in (and model)

            Object selectedContainer = getTargetForDiagram();
            Diagram newDiagram = null;
            boolean diagramWasConstructed = false;

            if (selectedContainer != null) {
                verifyResouceOpen(selectedContainer);

                DiagramEditor dEditor = null;

                boolean requiredStart = false;
                boolean succeeded = false;
                try {
                    // -------------------------------------------------
                    // Let's wrap this in a transaction!!!
                    // -------------------------------------------------

                    requiredStart = ModelerCore.startTxn(true, false, "Add To New Custom Diagram", this); //$NON-NLS-1$$

                    newDiagram = createDiagram(selectedContainer);
                    if (newDiagram != null) {
                        diagramWasConstructed = true;

                        ModelEditorManager.open(newDiagram, true);

                        // Get Visible Diagram Editor (should be loaded with an empty diagram.
                        dEditor = DiagramEditorUtil.getVisibleDiagramEditor();
                        // Need to get the current diagram
                        DiagramModelNode diagramNode = dEditor.getCurrentModel();
                        // Need to get ahold of the CustomDiagramModelFactory
                        CustomDiagramModelFactory modelFactory = (CustomDiagramModelFactory)dEditor.getModelFactory();
                        // And call add(SelectionUtilities.getSelectedEObjects(getSelection())

                        if (diagramNode != null && modelFactory != null) {
                            DiagramEditorUtil.setDiagramUnderConstruction(newDiagram);
                            // Now we can add to diagram
                            List applicableDiagramObjects = CustomDiagramContentHelper.getApplicableDiagramEObjects(selectedEObjects);
                            modelFactory.add(applicableDiagramObjects, diagramNode);
                            int levels = getAssociationLevels();

                            addAssociatedObjects(dEditor, modelFactory, diagramNode, selectedEObjects, levels);

                            dEditor.autoLayout();
                        }
                    }
                    succeeded = true;
                } catch (ModelWorkspaceException e) {
                    String message = Util.getString("AddToNewCustomDiagramAction.createCustomDiagramError", selectedContainer.toString()); //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
                } catch (Exception ex) {
                    DiagramUiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".doRun()"); //$NON-NLS-1$  //$NON-NLS-2$
                } finally {
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                    if (diagramWasConstructed) {
                        DiagramEditorUtil.setDiagramConstructionComplete(newDiagram, true);
                        // For some reason, this still isn't sufficient to complete the layout of Classifiers within the custom
                        // diagram. Easiest to just call open context again here to insure it's done properly.
                        if (dEditor != null) {
                            dEditor.openContext(newDiagram, true);
                        }
                    }
                }
            }

        }

        determineEnablement();
    }

    /**
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;

        if (!SelectionUtilities.getSelectedEObjects(getSelection()).isEmpty()) {
            // Check that no eObjects are "Diagrams" themselves

            List allEObjs = SelectionUtilities.getSelectedEObjects(getSelection());
            enable = true;
            Iterator iter = allEObjs.iterator();
            while (iter.hasNext() && enable) {
                if (iter.next() instanceof Diagram) {
                    enable = false;
                }
            }
        }

        setEnabled(enable);
    }

    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling the run at the last
     * minute. This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

    private void verifyResouceOpen( Object targetContainer ) {
        if (requiresEditorForRun()) {
            final Object selectedObject = SelectionUtilities.getSelectedObject(getSelection());
            ModelResource modelResource = null;

            if ((targetContainer instanceof IResource) && ModelUtilities.isModelFile((IResource)targetContainer)) {

                try {
                    modelResource = ModelUtil.getModelResource((IFile)targetContainer, false);
                } catch (ModelWorkspaceException e) {
                    String message = Util.getString("AddToNewCustomDiagramAction.createCustomDiagramError", selectedObject.toString()); //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
                }
            } else {
                modelResource = ModelUtilities.getModelResourceForModelObject((EObject)targetContainer);
            }

            if (modelResource != null) {
                ModelEditorManager.activate(modelResource, true);
            }
        }
    }

    private Diagram createDiagram( Object targetContainer ) throws ModelWorkspaceException {
        Diagram result = null;
        if ((targetContainer instanceof IResource) && ModelUtilities.isModelFile((IResource)targetContainer)) {
            ModelResource modelResource = ModelUtil.getModelResource((IFile)targetContainer, false);

            if (modelResource != null) {
                result = modelResource.getModelDiagrams().createNewDiagram(null, PERSIST_CUSTOM_DIAGRAMS);
                result.setType(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID);
            }
        } else {
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject((EObject)targetContainer);
            if (modelResource != null) {
                result = modelResource.getModelDiagrams().createNewDiagram((EObject)targetContainer, PERSIST_CUSTOM_DIAGRAMS);
                result.setType(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID);
            }
        }

        return result;
    }

    public int getAssociationLevels() {
        int levels = 0;
        Shell shell = DiagramUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

        AssociatedLevelsDialog dialog = new AssociatedLevelsDialog(shell, LEVELS_SELECTION_DIALOG_TITLE);
        if (dialog.open() == Window.OK) {
            levels = dialog.getLevels();
        }
        return levels;
    }

    private Object getTargetForDiagram() {

        final ViewerFilter filter = new ModelWorkspaceViewerFilter(true) {

            @Override
            public boolean select( final Viewer viewer,
                                   final Object parent,
                                   final Object element ) {
                // first make sure workspace filter would select element
                boolean doSelect = super.select(viewer, parent, element);

                // now make sure element can be handled by UML
                if (doSelect && (element instanceof EObject)) {
                    if (!DiagramUiUtilities.isStandardUmlPackage(element)) {
                        return false;
                    }
                }

                // only allow containers and resources
                return doSelect && ((element instanceof IContainer) || (element instanceof IResource));
            }
        };

        final ISelectionStatusValidator validator = new ISelectionStatusValidator() {

            public IStatus validate( final Object[] selection ) {
                if (selection.length == 1) {
                    if (selection[0] instanceof IResource) {
                        if (ModelUtilities.isModelFile((IResource)selection[0]) && !ModelUtil.isXsdFile((IResource)selection[0])) {
                            ModelResource mr = null;
                            try {
                                mr = ModelUtil.getModelResource((IFile)selection[0], false);
                            } catch (ModelWorkspaceException err) {
                                String message = Util.getString("AddToNewCustomDiagramAction.findModelResourceError", selection[0].toString()); //$NON-NLS-1$
                                Util.log(IStatus.ERROR, err, message);
                            }
                            if (mr != null && mr.isReadOnly()) return new Status(IStatus.ERROR, PLUGIN_ID, 0,
                                                                                 READ_ONLY_MODEL_SELECTION_MESSAGE, null);

                            return new Status(IStatus.OK, PLUGIN_ID, 0, EMPTY_STRING, null);
                        }
                    } else if (selection[0] instanceof EObject) {
                        if (DiagramUiUtilities.isStandardUmlPackage(selection[0])) {
                            ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)selection[0]);
                            if (mr != null && mr.isReadOnly()) return new Status(IStatus.ERROR, PLUGIN_ID, 0,
                                                                                 READ_ONLY_OBJECT_SELECTION_MESSAGE, null);

                            return new Status(IStatus.OK, PLUGIN_ID, 0, EMPTY_STRING, null);
                        }
                    }
                }

                return new Status(IStatus.ERROR, PLUGIN_ID, 0, INVALID_SELECTION_MESSAGE, null);
            }
        };

        final Object[] targets = WidgetUtil.showWorkspaceObjectSelectionDialog(TARGET_SELECTION_DIALOG_TITLE,
                                                                               TARGET_SELECTION_DIALOG_MESSAGE,
                                                                               true,
                                                                               null,
                                                                               filter,
                                                                               validator,
                                                                               new ModelExplorerLabelProvider(),
                                                                               new ModelExplorerContentProvider());
        if (targets.length > 0) {
            // Need to check if the

            return targets[0];

        }

        return null;
    }

    private void addAssociatedObjects( DiagramEditor editor,
                                       CustomDiagramModelFactory modelFactory,
                                       DiagramModelNode diagramNode,
                                       List startingList,
                                       int levels ) {
        List currentContents = new ArrayList(startingList);
        for (int i = 0; i < levels; i++) {
            List newObjects = getAssociatedObjectsNotInDiagram(editor, currentContents);
            if (newObjects.isEmpty()) break;
            modelFactory.add(newObjects, diagramNode);
            currentContents = DiagramEditorUtil.getCurrentDiagramNodeEObjects();
        }
    }

    private List getAssociatedObjectsNotInDiagram( DiagramEditor editor,
                                                   List objectList ) {
        List newObjects = new ArrayList();
        if (editor != null && editor.getCurrentModel() != null) {
            DiagramModelNode diagramNode = editor.getCurrentModel();
            List allAssociatedToAdd = UmlRelationshipHelper.getRelatedObjects(objectList);
            Iterator iter = allAssociatedToAdd.iterator();
            EObject nextEObj = null;
            while (iter.hasNext()) {
                nextEObj = (EObject)iter.next();
                if (editor != null && editor.getCurrentModel() != null) {
                    if (!(DiagramUiUtilities.diagramContainsEObject(nextEObj, diagramNode))) newObjects.add(nextEObj);
                }
            }
        }

        if (newObjects.isEmpty()) return Collections.EMPTY_LIST;

        return newObjects;
    }
}
