/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.diagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.util.DiagramProxy;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;

/**
 * MappingDiagramProvider
 */
public class MappingDiagramProvider implements IPackageDiagramProvider {

    /**
     * Construct an instance of MappingDiagramProvider.
     */
    public MappingDiagramProvider() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider#getPackageDiagram(com.metamatrix.modeler.core.workspace.ModelResource, org.eclipse.emf.ecore.EObject)
     */
    public Diagram getPackageDiagram( ModelResource modelResource,
                                      EObject eObject,
                                      boolean forceCreate ) {
        Diagram mappingDiagram = null;

        if (modelResource != null && eObject != null && ModelMapperFactory.isTreeRoot(eObject)) mappingDiagram = getMappingDiagram(modelResource,
                                                                                                                                   eObject,
                                                                                                                                   forceCreate);

        return mappingDiagram;
    }

    private Diagram createMappingDiagram( EObject target,
                                          ModelResource modelResource ) {
        Diagram result = null;
        boolean requiresStart = false;
        boolean succeeded = false;

        boolean persist = false;

        try {
            requiresStart = ModelerCore.startTxn(false, true, "Create Mapping Diagram", this); //$NON-NLS-1$

            result = modelResource.getModelDiagrams().createNewDiagram(target, persist);
            result.setType(PluginConstants.MAPPING_DIAGRAM_TYPE_ID);
            succeeded = true;
        } catch (ModelWorkspaceException e) {
            String message = UiConstants.Util.getString("MappingDiagramProvider.createMappingDiagramError", modelResource.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        } finally {
            if (requiresStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        return result;
    }

    private Diagram getMappingDiagram( ModelResource modelResource,
                                       EObject eObject,
                                       boolean forceCreate ) {
        Diagram mappingDiagram = null;

        try {
            List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(eObject));
            Iterator iter = diagramList.iterator();
            Diagram nextDiagram = null;
            while (iter.hasNext()) {
                nextDiagram = (Diagram)iter.next();
                if (nextDiagram != null && nextDiagram.getType() != null
                    && nextDiagram.getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID)) mappingDiagram = nextDiagram;
            }
        } catch (ModelWorkspaceException e) {
            String message = UiConstants.Util.getString("MappingDiagramContentProvider.getTransformationDiagramError", modelResource.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        }

        if (mappingDiagram == null) {
            // create one here.
            if (forceCreate) {
                mappingDiagram = createMappingDiagram(eObject, modelResource);
            } else {
                mappingDiagram = new DiagramProxy(eObject, PluginConstants.MAPPING_DIAGRAM_TYPE_ID, modelResource);
            }
        }

        return mappingDiagram;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider#getPackageDiagram(java.lang.Object)
     */
    public Diagram getPackageDiagram( Object targetObject,
                                      boolean forceCreate ) {
        Diagram mappingDiagram = null;
        EObject treeRootEObject = null;
        ModelResource modelResource = null;

        if (targetObject instanceof EObject) {
            EObject eObject = (EObject)targetObject;
            modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
            treeRootEObject = MappingDiagramUtil.getTreeRoot(eObject);
        }

        if (treeRootEObject != null && modelResource != null) mappingDiagram = getMappingDiagram(modelResource,
                                                                                                 treeRootEObject,
                                                                                                 forceCreate);

        return mappingDiagram;
    }

    public Diagram getDetailedMappingDiagram( EObject targetEObject ) {
        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(targetEObject);
        Iterator iter = null;
        EObject eObject = targetEObject;
        if (TransformationHelper.isTransformationMappingRoot(eObject)) {
            // let's get it's target
            EObject tableTarget = TransformationHelper.getTransformationTarget(eObject);
            if (tableTarget != null) {
                eObject = tableTarget;
            }
        } else {
            // Make sure we get the target table (i.e. mapping class)
            // Fix for Defect 22775 - the MappingDiagramSelectionHandler is basically calling this method with a
            // MappingClassColumn, so we need to

            EObject tableTarget = DiagramUiUtilities.getParentClassifier(targetEObject);
            if (tableTarget != null) {
                eObject = tableTarget;
            }
        }

        try {
            iter = modelResource.getModelDiagrams().getDiagrams(eObject).iterator();
        } catch (ModelWorkspaceException e) {
            String message = UiConstants.Util.getString("MappingDiagramProvider.getDiagramsError", modelResource.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        }
        if (iter != null) {
            Diagram nextDiagram = null;
            while (iter.hasNext()) {
                nextDiagram = (Diagram)iter.next();
                if (nextDiagram.getType() != null
                    && nextDiagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) return nextDiagram;
            }
        }
        // Couldn't find one so create one
        boolean requiresStart = false;
        boolean succeeded = false;
        try {
            requiresStart = ModelerCore.startTxn(false, true, "Create Mapping Transformation Diagram", this); //$NON-NLS-1$

            Diagram depDiagram = modelResource.getModelDiagrams().createNewDiagram(eObject, false); // Do Not persist this
            // diagram.
            depDiagram.setType(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID);
            succeeded = true;
            return depDiagram;
        } catch (ModelWorkspaceException mwe) {
            String message = UiConstants.Util.getString("MappingDiagramProvider.createMappingDiagramError", modelResource.toString()); //$NON-NLS-1$
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

        return null;
    }

    public boolean hasDetailedMappingDiagram( final ModelResource modelResource,
                                              final EObject eObject ) {
        try {
            List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(eObject));
            Iterator iter = diagramList.iterator();
            Diagram nextDiagram = null;
            while (iter.hasNext()) {
                nextDiagram = (Diagram)iter.next();
                if (nextDiagram.getType() != null
                    && nextDiagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) return true;
            }
        } catch (ModelWorkspaceException e) {
            String message = UiConstants.Util.getString("TransformationDiagramUtil.getTransformationDiagramsError", modelResource.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        }

        return false;
    }
}
