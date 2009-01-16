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

package com.metamatrix.modeler.mapping.ui.diagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.impl.MappingClassImpl;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlAttributeEditPart;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.EditableEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.actions.MappingObjectEditHelper;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.modeler.mapping.ui.part.MappingExtentEditPart;
import com.metamatrix.modeler.mapping.ui.part.MappingLinkEditPart;
import com.metamatrix.modeler.transformation.ui.part.TransformationEditPart;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

/**
 * MappingDiagramSelectionHandler
 */
public class MappingDiagramSelectionHandler extends DiagramSelectionHandler {
    private MappingObjectEditHelper editHelper;
    private MappingDiagramProvider diagramProvider;

    /**
     * Construct an instance of MappingDiagramSelectionHandler.
     * 
     * @param viewer
     */
    public MappingDiagramSelectionHandler( DiagramViewer viewer ) {
        super(viewer);
        editHelper = new MappingObjectEditHelper();
        diagramProvider = new MappingDiagramProvider();
    }

    @Override
    public void select( EObject selectedObject ) {
        // System.out.println("[MappingDiagramSelectionHandler.select 1] TOP, selectedObject is: " + selectedObject );
        // Thread.dumpStack();

        super.deselectAll();
        super.select(selectedObject);

        // // jh defect 20457, etc: selection problems in Detailed diagram: This hilite seems redundant:
        // hiliteDependencies(selectedObject);
        // fix for 20078:
        hiliteDependenciesForTreeSelection(selectedObject);

        // fix for 20235:
        // MappingDiagramController controller = (MappingDiagramController)getViewer().getEditor().getDiagramController();
        // controller.handleReveal();

        // System.out.println("[MappingDiagramSelectionHandler.select 1] BOT " );
    }

    @Override
    public void select( List selectedEObjects ) {
        // System.out.println("[MappingDiagramSelectionHandler.select 2] TOP, selectedObject is: " + selectedEObjects );
        // Thread.dumpStack();
        clearDependencyHilites();
        super.select(selectedEObjects);
        // System.out.println("[MappingDiagramSelectionHandler.select 2] BOT " );
    }

    @Override
    public List getSelectedEObjects() {
        List selectedEObjects = new ArrayList();
        Iterator iter = getViewer().getSelectedEditParts().iterator();
        Object obj = null;
        EObject nextEObject = null;

        while (iter.hasNext()) {
            obj = iter.next();

            if (obj instanceof DiagramEditPart) {
                DiagramEditPart dep = (DiagramEditPart)obj;
                nextEObject = null;
                nextEObject = dep.getModelObject();

                if (nextEObject != null && !selectedEObjects.contains(nextEObject)) {
                    selectedEObjects.add(nextEObject);
                }
            }

        }
        return selectedEObjects;
    }

    // This method overides the base class method so we can hilite the extents for selecting a Mapping Class
    @Override
    public void hiliteDependencies( Object selectedObject ) {
        // System.out.println("\n[MappingDiagramSelectionHandler.hiliteDependencies] TOP; selectedObject is: " + selectedObject );
        // Thread.dumpStack();

        clearDependencyHilites();
        EObject selectedEObject = null;

        EditPart selectedPart = null;

        if (selectedObject != null && selectedObject instanceof EObject) {

            selectedEObject = (EObject)selectedObject;
            selectedPart = findEditPart(selectedEObject, false);

            if (selectedPart != null && selectedPart instanceof DiagramEditPart) {
                // System.out.println("[MappingDiagramSelectionHandler.hiliteDependencies] selectedPart is a DiagramEditPart");

                // Assume that we want to hilite all "connected" objects here in coarse mapping
                if (selectedPart instanceof UmlClassifierEditPart) {
                    // get all "source" connections
                    List sConnectionEditParts = ((DiagramEditPart)selectedPart).getSourceConnections();
                    Iterator sIter = sConnectionEditParts.iterator();
                    MappingLinkEditPart nextMLEP = null;
                    Object nextObj = null;
                    while (sIter.hasNext()) {
                        nextObj = sIter.next();
                        if (nextObj instanceof MappingLinkEditPart) {
                            nextMLEP = (MappingLinkEditPart)nextObj;
                            ((DiagramEditPart)nextMLEP.getTarget()).hiliteBackground(UiConstants.Colors.DEPENDENCY);
                        }
                    }

                } else if (selectedPart instanceof MappingExtentEditPart) {
                    // System.out.println("[MappingDiagramSelectionHandler.hiliteDependencies] selectedPart is a MappingExtentEditPart");
                    if (getDiagramType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) {
                        // get all "source" connections
                        List tConnectionEditParts = ((DiagramEditPart)selectedPart).getTargetConnections();
                        Iterator tIter = tConnectionEditParts.iterator();
                        MappingLinkEditPart nextMLEP = null;
                        Object nextObj = null;

                        int iEditPartsProcessed = 0;
                        while (tIter.hasNext()) {
                            nextObj = tIter.next();
                            if (nextObj instanceof MappingLinkEditPart) {
                                nextMLEP = (MappingLinkEditPart)nextObj;
                                ((DiagramEditPart)nextMLEP.getSource()).hiliteBackground(UiConstants.Colors.DEPENDENCY);

                                ++iEditPartsProcessed;
                                if (iEditPartsProcessed < 2) {
                                    getViewer().reveal(nextMLEP.getSource());
                                }
                            }
                        }
                    } else {
                        // System.out.println("[MappingDiagramSelectionHandler.hiliteDependencies] this is a Detailed Diagram");
                        // We have a detailed diagram, so the extent selection also means column selection
                        EObject selectedColumn = ((MappingDiagramController)getViewer().getEditor().getDiagramController()).getMappingAdapter().getMappingClassColumn((EObject)selectedObject);
                        if (selectedColumn != null) {
                            selectedPart = findEditPart(selectedColumn, false);
                            if (selectedPart != null) {
                                // selectedEObject = selectedColumn;
                                ((DiagramEditPart)selectedPart).hiliteBackground(UiConstants.Colors.DEPENDENCY);
                                // reveal Classifier Parent
                                DiagramEditPart dep = DiagramUiUtilities.getClassifierParent((DiagramEditPart)selectedPart);
                                if (dep != null) getViewer().reveal(dep);
                            }
                        }
                    }
                } else if (selectedPart instanceof UmlAttributeEditPart
                           && getDiagramType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) {
                    // System.out.println("[MappingDiagramSelectionHandler.hiliteDependencies] selectedPart is a UmlAttributeEditPart");

                    super.hiliteDependencies(selectedObject);
                    // Now we add the attribute dependency from the Transformation Helper..
                    // have to pass in the mapping root
                    TransformationMappingRoot tmr = null;
                    Diagram detailedDiagram = getViewer().getEditor().getDiagram();
                    if (detailedDiagram != null) {
                        EObject virtualGroup = detailedDiagram.getTarget();

                        tmr = (TransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(virtualGroup);
                        List allDependencies = new ArrayList();

                        // Only add source dependencies when the selectedObject is virtual, else there will be
                        // no mapping's defining the selected physical attribute!!
                        if (ModelObjectUtilities.isVirtual(selectedEObject)) allDependencies.addAll(TransformationHelper.getSourceAttributesForTargetAttr(selectedObject,
                                                                                                                                                          tmr));

                        // Always look for target attributes because the selected attribute may in a virtual
                        // group that is just one step in a complex tranformation hiearchy.
                        allDependencies.addAll(TransformationHelper.getTargetAttributesForSourceAttr(selectedObject, tmr));

                        Iterator iter = allDependencies.iterator();
                        EditPart nextEP = null;
                        EObject nextEObject = null;
                        while (iter.hasNext()) {
                            nextEObject = (EObject)iter.next();
                            nextEP = findEditPart(nextEObject, false);
                            if (nextEP != null && nextEP instanceof DiagramEditPart) {
                                ((DiagramEditPart)nextEP).hiliteBackground(UiConstants.Colors.DEPENDENCY);
                                if (UiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.diagram.ui.DebugConstants.DIAGRAM_SELECTION)) {
                                    String debugMessage = "hiliteDependencies():  Dependent object = " + nextEObject; //$NON-NLS-1$
                                    UiConstants.Util.print(this.getClass(), debugMessage);
                                }
                            }
                        }
                    }

                } else {
                    super.hiliteDependencies(selectedObject);
                }
            } else {
                // Here we need to check for "attribute" or child type edit parts... (i.e. XmlSequence, etc...
                EObject selectedColumn = ((MappingDiagramController)getViewer().getEditor().getDiagramController()).getMappingAdapter().getMappingClassColumn((EObject)selectedObject);
                if (selectedColumn != null) {
                    selectedPart = findEditPart(selectedColumn, false);
                    if (selectedPart != null) {
                        // selectedEObject = selectedColumn;
                        ((DiagramEditPart)selectedPart).hiliteBackground(UiConstants.Colors.DEPENDENCY);
                        // reveal Classifier Parent
                        DiagramEditPart dep = DiagramUiUtilities.getClassifierParent((DiagramEditPart)selectedPart);
                        if (dep != null) getViewer().reveal(dep);
                    }
                }
            }
        }
        // System.out.println("[MappingDiagramSelectionHandler.hiliteDependencies] BOT");

    }

    // jh 2/13/2006: Fix for Defect 20078: Added this alternate 'hilite' method so we can handle a selection
    // of an XML Element in the tree
    // To handle XML Element and XML Attribute
    public void hiliteDependenciesForTreeSelection( Object selectedObject ) {
        // System.out.println("\n[MappingDiagramSelectionHandler.hiliteDependenciesForTreeSelection] TOP; selectedObject is: " +
        // selectedObject );

        clearDependencyHilites();

        EditPart selectedPart = null;

        if (selectedObject != null && selectedObject instanceof EObject) {

            if (selectedObject instanceof XmlElement || selectedObject instanceof XmlAttribute) {
                // System.out.println("[MappingDiagramSelectionHandler.hiliteDependencies] selectedObject is a XmlElement");

                // Here we need to check for "attribute" or child type edit parts... (i.e. XmlSequence, etc...
                EObject selectedColumn = ((MappingDiagramController)getViewer().getEditor().getDiagramController()).getMappingAdapter().getMappingClassColumn((EObject)selectedObject);
                if (selectedColumn != null) {
                    selectedPart = findEditPart(selectedColumn, false);
                    if (selectedPart != null) {
                        // selectedEObject = selectedColumn;
                        ((DiagramEditPart)selectedPart).hiliteBackground(UiConstants.Colors.DEPENDENCY);
                        // reveal Classifier Parent
                        DiagramEditPart dep = DiagramUiUtilities.getClassifierParent((DiagramEditPart)selectedPart);
                        if (dep != null) {
                            getViewer().reveal(dep);
                        }
                        return;
                    }
                }
            }
        }

        // System.out.println("[MappingDiagramSelectionHandler.hiliteDependenciesForTreeSelection] BOT");

    }

    @Override
    public boolean handleDoubleClick( final EObject selectedObject ) {
        boolean handledHere = false;

        // If current diagram is of type MAPPING_COARSE, then if object is Classifier, then we
        // check for Mapping Class, then open up the DETAILED View.

        MappingDiagramController controller = (MappingDiagramController)getViewer().getEditor().getDiagramController();

        if (controller != null && controller.getMappingType() == PluginConstants.COARSE_MAPPING) {
            final EditPart selectedEP = findEditPart(selectedObject, false);
            // Need to see if the selected object is of UmlClassifierAspect and isVirtual
            if (selectedEP != null && ModelObjectUtilities.isVirtual(selectedObject)) {
                MetamodelAspect aspect = DiagramUiPlugin.getDiagramAspectManager().getUmlAspect(selectedObject);
                if (aspect instanceof UmlClassifier) {
                    boolean navigateToDetailedDiagram = true;
                    if (selectedEP instanceof UmlClassifierEditPart && ((UmlClassifierEditPart)selectedEP).doubleClickedName()) {
                        navigateToDetailedDiagram = false;
                    }
                    if (!navigateToDetailedDiagram) {
                        ((EditableEditPart)selectedEP).edit();
                        handledHere = true;
                    } else {
                        final Diagram detailedDiagram = getDetailedMappingDiagram(selectedObject);
                        if (detailedDiagram != null) {
                            if (getViewer().getEditor().canOpenContext(detailedDiagram)) {
                                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                                    public void run() {
                                        if (selectedEP instanceof EditableEditPart) {

                                            // ==> do Edit
                                            ((EditableEditPart)selectedEP).edit();

                                            // ==> do Expand
                                            // jh 2/28/2006: Fix for Defect 21044: force an expand so we'll see treenodes and
                                            // normal
                                            // extents in the detailed diagram
                                            if (selectedEP instanceof UmlClassifierEditPart) {
                                                MappingDiagramController controller = (MappingDiagramController)getViewer().getEditor().getDiagramController();
                                                UmlClassifierEditPart ucep = (UmlClassifierEditPart)selectedEP;

                                                MappingClassImpl mci = (MappingClassImpl)ucep.getModelObject();
                                                EList elst = mci.getColumns();

                                                // we need a list of MappingClassColumns for this...
                                                controller.notifyElementsRevealed(ucep, elst);
                                            }

                                            // ==> do Refresh
                                            // jh 2/13/2006: Fix for Defect 20182: force a refresh to make sure extents line up
                                            // properly in Detailed Diagram.
                                            DiagramEditor editor = DiagramEditorUtil.getVisibleDiagramEditor();
                                            if (editor != null && editor.getCurrentModel() != null) {
                                                editor.doRefreshDiagram();
                                                editor.getDiagramViewer().setFocus(selectedEP);
                                            }

                                        } else {
                                            getViewer().getEditor().openContext(detailedDiagram);
                                        }
                                        select(selectedObject);
                                    }
                                });
                            }
                            handledHere = true;
                        }
                    }
                }
            }
        }
        /*
         * jh Defect 18919: New case: d-click on another MC root node or other mappable column in the  
         *    doc tree while already in a DETAILED DIAGRAM willl reload the DETAILED DIAGRAM editor with the
         *    new MC.
         */
        else if (controller != null && controller.getMappingType() == PluginConstants.DETAILED_MAPPING) {
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(controller.getDocumentEObject());
            EditPart selectedEP = findEditPart(selectedObject, false);
            if (selectedEP != null && selectedEP instanceof EditableEditPart && editHelper.canEdit(selectedObject, modelResource)) {
                // Check to see if we want to direct edit
                // System.out.println("[MappingDiagramSelectionHandler.handleDoubleClick] About to call 'NOT coarse!' edit()");
                ((EditableEditPart)selectedEP).edit();
                handledHere = true;
            } else if (ModelObjectUtilities.isVirtual(selectedObject)
                       && ModelUtilities.areModelResourcesSame(selectedObject, controller.getCurrentDiagram())) {

                final Diagram detailedDiagram = getDetailedMappingDiagram(selectedObject);
                if (detailedDiagram != null) {

                    // proceed only if this diagram is NOT already the current diagram
                    if (detailedDiagram != getViewer().getEditor().getDiagram()) {

                        if (getViewer().getEditor().canOpenContext(detailedDiagram)) {
                            UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                                public void run() {
                                    getViewer().getEditor().openContext(detailedDiagram);
                                    select(selectedObject);
                                }
                            });
                        }
                    }
                }
                handledHere = true;
            } else {
                // The d-clicked object is NOT in the Mapping's Resource, so we assume it's in a source resource and call open()
                // to show it's object in a package diagram
                ModelEditorManager.open(selectedObject, true);

                handledHere = true;
            }

        } else if (controller != null) {
            EditPart selectedEP = findEditPart(selectedObject, false);
            if (selectedEP != null && selectedEP instanceof TransformationEditPart && selectedEP instanceof EditableEditPart) {
                // Check to see if we want to direct edit
                ((EditableEditPart)selectedEP).edit();
                handledHere = true;
            } else if (ModelObjectUtilities.isVirtual(selectedObject)) {
                if (selectedEP != null) {
                    if (selectedEP instanceof UmlClassifierEditPart || selectedObject instanceof InputSet) {
                        if (ModelEditorManager.canEdit(selectedObject)) {
                            UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                                public void run() {
                                    ModelEditorManager.edit(selectedObject);
                                }
                            });
                            handledHere = true;
                        }
                    }
                }
            }
        }

        return handledHere;
    }

    private Diagram getDetailedMappingDiagram( EObject targetEObject ) {
        return diagramProvider.getDetailedMappingDiagram(targetEObject);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler#shouldReveal(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean shouldReveal( EObject eObject ) {
        if (TransformationHelper.isSqlTable(eObject)) return true;

        return false;
    }

}
