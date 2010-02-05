/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.editor.MappingAdapterFilter;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramBehavior;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.modeler.mapping.ui.figure.MappingExtentFigure;
import com.metamatrix.modeler.mapping.ui.model.MappingExtentNode;
import com.metamatrix.modeler.mapping.ui.part.MappingDiagramEditPart;
import com.metamatrix.modeler.mapping.ui.part.MappingExtentEditPart;
import com.metamatrix.modeler.xsd.util.ModelerXsdUtils;

/**
 * MappingDiagramUtil
 */
public class MappingDiagramUtil {

    /**
     * given a document tree root, find the coarse mapping diagram. One is created if none exists yet
     * 
     * @param eObject
     * @return
     */
    public static Diagram getCoarseMappingDiagram( EObject documentTreeRootEObject ) {
        Iterator iter = null;

        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(documentTreeRootEObject);
        if (modelResource != null) {
            try {
                iter = modelResource.getModelDiagrams().getDiagrams(documentTreeRootEObject).iterator();
            } catch (ModelWorkspaceException e) {
                String message = UiConstants.Util.getString("getCoarseMappingDiagram.getDiagramsError", modelResource.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
            if (iter != null) {
                Diagram nextDiagram = null;
                while (iter.hasNext()) {
                    nextDiagram = (Diagram)iter.next();
                    if (nextDiagram.getType() != null && nextDiagram.getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) return nextDiagram;
                }
            }
            // Couldn't find one so create one
            boolean requiresStart = false;
            boolean succeeded = false;
            boolean persist = false;

            try {
                requiresStart = ModelerCore.startTxn(false, true, "Create Mapping Diagram", documentTreeRootEObject); //$NON-NLS-1$

                Diagram depDiagram = modelResource.getModelDiagrams().createNewDiagram(documentTreeRootEObject, persist); // Do
                                                                                                                          // Not
                                                                                                                          // persist
                                                                                                                          // this
                                                                                                                          // diagram.
                depDiagram.setType(PluginConstants.MAPPING_DIAGRAM_TYPE_ID);
                succeeded = true;
                return depDiagram;
            } catch (ModelWorkspaceException mwe) {
                String message = UiConstants.Util.getString("getCoarseMappingDiagram.createMappingDiagramError", modelResource.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, mwe, message);
            } finally {
                if (requiresStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        return null;
    }

    public static Diagram getCoarseMappingDiagram( Diagram detailedMappingDiagram ) {
        // Now, let's get the coarse mapping diagram based on selected object;
        // Need to get the document!!
        EObject documentTreeRoot = null;
        if (detailedMappingDiagram != null) {
            MappingClass mappingClass = (MappingClass)detailedMappingDiagram.getTarget();
            documentTreeRoot = mappingClass.getMappingClassSet().getTarget();
        }

        if (documentTreeRoot != null) return getCoarseMappingDiagram(documentTreeRoot);
        return null;
    }

    public static boolean isMappingSqlTable( final Object input ) {
        boolean result = false;
        if (input instanceof EObject && TransformationHelper.isVirtualSqlTable(input) && input instanceof MappingClass) {
            return true;
        }

        return result;
    }

    public static boolean isMappingClassColumn( final Object input ) {
        boolean result = false;
        if (input instanceof EObject) {
            Object container = ((EObject)input).eContainer();
            if (MappingDiagramUtil.isMappingSqlTable(container)) {
                result = true;
            }
        }

        return result;
    }

    public static boolean isInputSet( final Object input ) {
        boolean result = false;
        if (input instanceof EObject && TransformationHelper.isVirtualSqlTable(input) && input instanceof InputSet) {
            return true;
        }

        return result;
    }

    public static boolean isMappingClass( final Object input ) {
        boolean result = false;
        if (input instanceof EObject && input instanceof MappingClass && !(input instanceof StagingTable)) {
            return true;
        }

        return result;
    }

    public static boolean isStagingTable( final Object input ) {
        boolean result = false;
        if (input instanceof EObject && input instanceof StagingTable) {
            return true;
        }

        return result;
    }

    public static boolean isInputSetParameter( final Object input ) {
        boolean result = false;
        if (input instanceof EObject) {
            Object container = ((EObject)input).eContainer();
            if (MappingDiagramUtil.isInputSet(container)) {
                result = true;
            }
        }

        return result;
    }

    public static boolean isMappingDocument( final Object input ) {
        boolean result = false;
        if (input instanceof EObject && ModelMapperFactory.isTreeRoot((EObject)input)) {
            return true;
        }

        return result;
    }

    public static EObject getTreeRoot( final EObject eObject ) {
        EObject treeRootEObject = null;

        if (ModelMapperFactory.isTreeRoot(eObject)) {
            treeRootEObject = eObject;
        } else {
            MappingClassSet mappingClassSet = null;
            if (eObject instanceof MappingClass) {
                mappingClassSet = ((MappingClass)eObject).getMappingClassSet();
            } else if (eObject instanceof MappingClassColumn) {
                EObject mappingClass = eObject.eContainer();
                mappingClassSet = ((MappingClass)mappingClass).getMappingClassSet();
            }

            if (mappingClassSet != null) {
                treeRootEObject = mappingClassSet.getTarget();
            }
        }

        return treeRootEObject;
    }

    public static boolean hasMappingDocument( final Object input ) {
        boolean result = false;

        if (input instanceof EObject) {
            EObject eObject = (EObject)input;

            EObject parentPackage = getMappingDocument(eObject);
            if (parentPackage != null) {
                result = true;
            }
        }

        return result;
    }

    public static EObject getMappingDocument( final Object input ) {
        EObject documentEObject = null;

        if (input instanceof EObject) {
            EObject eObject = (EObject)input;

            Object parent = eObject.eContainer();

            if (parent != null && parent instanceof EObject) {
                if (isMappingDocument(parent)) documentEObject = (EObject)parent;
                else documentEObject = getMappingDocument(parent);
            }
        }

        return documentEObject;
    }

    public static void layoutDiagram( final DiagramModelNode diagramNode ) {
        // Get current editor and check for matching diagram node.
        DiagramEditor editor = DiagramEditorUtil.getVisibleDiagramEditor();
        if (diagramNode != null && editor != null && editor.getCurrentModel() != null
            && editor.getCurrentModel().getModelObject().equals(diagramNode.getModelObject())) {
            // Let's find the edit part for the diagram....
            EditPart contents = editor.getDiagramViewer().getContents();
            if (contents instanceof MappingDiagramEditPart) {
                ((MappingDiagramEditPart)contents).layout();
            }
        }
    }

    public static void layoutDiagram( final DiagramModelNode diagramNode,
                                      boolean layoutChildren ) {
        // Get current editor and check for matching diagram node.
        DiagramEditor editor = DiagramEditorUtil.getVisibleDiagramEditor();
        if (diagramNode != null && editor != null && editor.getCurrentModel() != null
            && editor.getCurrentModel().getModelObject().equals(diagramNode.getModelObject())) {
            // Let's find the edit part for the diagram....
            EditPart contents = editor.getDiagramViewer().getContents();
            if (contents instanceof MappingDiagramEditPart) {
                ((MappingDiagramEditPart)contents).layout(layoutChildren);
            }
        }
    }

    public static void hiliteUnconnectedExtents( final DiagramModelNode diagramNode ) {
        // Get current editor and check for matching diagram node.
        DiagramEditor editor = DiagramEditorUtil.getVisibleDiagramEditor();
        if (diagramNode != null && diagramNode.getModelObject() != null && editor != null && editor.getCurrentModel() != null
            && editor.getCurrentModel().getModelObject() != null
            && editor.getCurrentModel().getModelObject().equals(diagramNode.getModelObject())) {
            // Let's find the edit part for the diagram....
            EditPart contents = editor.getDiagramViewer().getContents();
            if (contents instanceof MappingDiagramEditPart) {
                List editParts = contents.getChildren();
                Iterator iter = editParts.iterator();

                EditPart nextEP = null;
                while (iter.hasNext()) {
                    nextEP = (EditPart)iter.next();
                    if (nextEP instanceof MappingExtentEditPart) {
                        hiliteExtent((MappingExtentEditPart)nextEP);
                    }
                }
            }
        }
    }

    public static void hiliteExtent( MappingExtentEditPart meep ) {
        MappingExtentNode nextNode = (MappingExtentNode)meep.getModel();
        if (nextNode != null
        // jhTODO jh test: drop the 'detailed diagram only' so we can do this in Coarse as well:
        /*&& nextNode.getDiagram().getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID) */) {
            if (!(nextNode.getModelObject() instanceof StagingTable)) {
                if (!DiagramUiUtilities.isNodeConnected(nextNode)) {
                    if (nextNode.getExtent().isMappingRequired()) {
                        ((MappingExtentFigure)meep.getDiagramFigure()).setDefaultBkgdColor(UiConstants.Colors.REQUIRES_MAPPING);
                        meep.getDiagramFigure().hiliteBackground(UiConstants.Colors.REQUIRES_MAPPING);
                    } else {
                        ((MappingExtentFigure)meep.getDiagramFigure()).setDefaultBkgdColor(UiConstants.Colors.UNMAPPED);
                        meep.getDiagramFigure().hiliteBackground(UiConstants.Colors.UNMAPPED);
                    }
                }
            }
        }

    }

    public static DiagramEditPart getInputSetEditPart( DiagramEditPart detailedDiagramEditPart ) {
        DiagramEditPart inputSetEP = null;
        Iterator iter = detailedDiagramEditPart.getChildren().iterator();
        Object nextObj = null;
        DiagramEditPart nextDEP = null;
        while (iter.hasNext() && inputSetEP == null) {
            nextObj = iter.next();
            if (nextObj instanceof DiagramEditPart) {
                nextDEP = (DiagramEditPart)nextObj;
                if (nextDEP.getModelObject() != null && TransformationHelper.isSqlInputSet(nextDEP.getModelObject())) inputSetEP = nextDEP;
            }
        }

        return inputSetEP;
    }

    public static DiagramEditPart getTransformationEditPart( DiagramEditPart detailedDiagramEditPart ) {
        DiagramEditPart transformationEP = null;
        Iterator iter = detailedDiagramEditPart.getChildren().iterator();
        Object nextObj = null;
        DiagramEditPart nextDEP = null;
        while (iter.hasNext() && transformationEP == null) {
            nextObj = iter.next();
            if (nextObj instanceof DiagramEditPart) {
                nextDEP = (DiagramEditPart)nextObj;
                if (nextDEP.getModelObject() != null
                    && TransformationHelper.isTransformationMappingRoot(nextDEP.getModelObject())) transformationEP = nextDEP;
            }
        }

        return transformationEP;
    }

    /**
     * @param obj
     * @return
     */
    public static boolean isTreeMappingRoot( Object obj ) {
        return obj instanceof TreeMappingRoot;
    }

    /**
     * This utility method will: 1) Locate visible Diagram Editor 2) Obtain the MappingDocumentController 3) Get the filter from
     * the controller and use to get visible ordered mapping classes
     * 
     * @return
     * @since 4.3
     */
    public static List getOrderedCoarseMappingClasses() {
        List onlyMappingClasses = new ArrayList();

        DiagramEditor editor = DiagramEditorUtil.getVisibleDiagramEditor();
        if (editor != null) {
            MappingDiagramController controller = (MappingDiagramController)editor.getDiagramController();
            if (controller != null) {
                MappingAdapterFilter filter = controller.getMappingFilter();
                // Filter will return mapping classes and staging tables
                List visibleMappingClasses = filter.getMappedClassifiers();

                MappingClass nextMC = null;
                Iterator iter = visibleMappingClasses.iterator();
                while (iter.hasNext()) {
                    nextMC = (MappingClass)iter.next();
                    // Add only if it's not a Staging Table
                    if (!(nextMC instanceof StagingTable)) {
                        onlyMappingClasses.add(nextMC);
                    }
                }
            }
        }
        if (onlyMappingClasses.isEmpty()) return Collections.EMPTY_LIST;

        return onlyMappingClasses;
    }

    /**
     * Obtains all the <code>DiagramModelNode</code>s that are associated with enumerated types.
     * 
     * @return the diagram model nodes (never <code>null</code>)
     * @since 5.0.2
     */
    public static DiagramModelNode[] getEnumeratedTypeNodes() {
        DiagramModelNode[] result = null;

        // Create temporary list
        List tempList = new ArrayList();

        // Get visible editor
        DiagramEditor editor = DiagramEditorUtil.getVisibleDiagramEditor();

        if (editor != null) {
            // Get current diagram model
            DiagramModelNode theMSDiagramNode = editor.getCurrentModel();
            if (theMSDiagramNode != null) {
                // Get diagram children. These will contain the Enumeration classifiers (if any exist)
                Collection children = theMSDiagramNode.getChildren();
                Object nextObj = null;
                for (Iterator iter = children.iterator(); iter.hasNext();) {
                    nextObj = iter.next();
                    // Check for classifier node AND enumerated type, then add to temp list
                    if (nextObj instanceof UmlClassifierNode) {
                        if (ModelerXsdUtils.isEnumeratedType(((DiagramModelNode)nextObj).getModelObject())) {
                            tempList.add(nextObj);
                        }
                    }
                }
            }
        }

        // load the array result if not empty
        if (!tempList.isEmpty()) {
            tempList.toArray(result = new DiagramModelNode[tempList.size()]);
        }

        // create empty array if result is still NULL
        if (result == null) {
            result = new DiagramModelNode[0];
        }

        return result;
    }

    public static MappingDiagramBehavior getCurrentMappingDiagramBehavior() {
        DiagramEditor editor = DiagramEditorUtil.getVisibleDiagramEditor();
        if (editor != null) {
            MappingDiagramController controller = (MappingDiagramController)editor.getDiagramController();
            if (controller != null) {
                return controller.getMappingDiagramBehavior();
            }
        }

        // jh fix for Defect 21171
        // as default, return a valid default-state behavior object, if we cannot find a current DiagramEditor
        return new MappingDiagramBehavior();
    }

    public static boolean isDetailedDiagram( Diagram diagram ) {
        if (diagram != null && diagram.getType() != null
            && diagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) {
            return true;
        }
        return false;
    }
}
