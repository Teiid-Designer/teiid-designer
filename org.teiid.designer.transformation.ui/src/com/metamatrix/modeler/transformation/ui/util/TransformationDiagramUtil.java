/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.util.DiagramProxy;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;

/**
 * TransformationDiagramUtil
 */
public class TransformationDiagramUtil {

    /**
     * Construct an instance of TransformationDiagramUtil.
     */
    //    private static final String PRODUCT_VERSION = "3.0"; //$NON-NLS-1$
    private static final String NAMESPACE_URI_PREFIX = "http://www.metamatrix.com/metamodels/"; //$NON-NLS-1$
    private static final String RELATIONAL_URI = NAMESPACE_URI_PREFIX + "Relational"; //$NON-NLS-1$
    private static final String DATA_ACCESS_URI = NAMESPACE_URI_PREFIX + "DataAccess"; //$NON-NLS-1$
    private static final String WEB_SERVICE_URI = NAMESPACE_URI_PREFIX + "WebService"; //$NON-NLS-1$
    private static final String XML_SERVICE_URI = NAMESPACE_URI_PREFIX + "XmlService"; //$NON-NLS-1$

    public static Diagram createTransformationDiagram( final EObject target,
                                                       final ModelResource modelResource,
                                                       final boolean persistance ) {
        Diagram result = null;

        boolean persist = persistance;
        if (ModelUtil.isIResourceReadOnly(modelResource.getResource())) persist = false;

        if (!diagramExistsForProcedure(modelResource, target)) {
            if (isVirtualProcedureOrTable(target) || isStandardVirtualSqlTable(target)) {
                boolean requiresStart = ModelerCore.startTxn(false, true, "Create Transformation Diagram", target); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    result = modelResource.getModelDiagrams().createNewDiagram(target, persist);
                    result.setType(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID);
                    succeeded = true;
                } catch (ModelWorkspaceException e) {
                    String message = UiConstants.Util.getString("TransformationDiagramUtil.createTransformationDiagramError", modelResource.toString()); //$NON-NLS-1$
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
            }
        }
        return result;
    }

    private static Diagram createProxiedTransformationDiagram( final EObject target,
                                                               final ModelResource modelResource ) {
        return new DiagramProxy(target, PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID, modelResource);
    }

    public static List getTransformationDiagrams( final ModelResource modelResource,
                                                  final EObject eObject,
                                                  final boolean createIfNone,
                                                  final boolean persistance ) {
        List transformationDiagrams = new ArrayList();
        try {
            List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(eObject));
            Iterator iter = diagramList.iterator();
            Diagram nextDiagram = null;
            while (iter.hasNext()) {
                nextDiagram = (Diagram)iter.next();
                if (nextDiagram.getType() != null && nextDiagram.getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID)) transformationDiagrams.add(nextDiagram);
            }
        } catch (ModelWorkspaceException e) {
            String message = UiConstants.Util.getString("TransformationDiagramUtil.getTransformationDiagramsError", modelResource.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        }

        if (transformationDiagrams.isEmpty()) {
            // create one here.
            Diagram newTransformationDiagram = null;
            if (createIfNone) {
                newTransformationDiagram = createTransformationDiagram(eObject, modelResource, persistance);
            } else {
                newTransformationDiagram = createProxiedTransformationDiagram(eObject, modelResource);
            }
            if (newTransformationDiagram != null) transformationDiagrams.add(newTransformationDiagram);
        }

        return transformationDiagrams;
    }

    public static Diagram getTransformationDiagram( final ModelResource modelResource,
                                                    final EObject eObject,
                                                    final boolean createIfNone,
                                                    final boolean persistance ) {
        Diagram tDiagram = null;

        try {
            List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(eObject));
            Iterator iter = diagramList.iterator();
            Diagram nextDiagram = null;
            while (iter.hasNext() && tDiagram == null) {
                nextDiagram = (Diagram)iter.next();
                if (nextDiagram.getType() != null && nextDiagram.getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID)) tDiagram = nextDiagram;
            }
        } catch (ModelWorkspaceException e) {
            String message = UiConstants.Util.getString("TransformationDiagramUtil.getTransformationDiagramsError", modelResource.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        }

        if (tDiagram == null && createIfNone) {
            // create one here.
            tDiagram = createTransformationDiagram(eObject, modelResource, persistance);
        } else if (tDiagram == null) {
            tDiagram = createProxiedTransformationDiagram(eObject, modelResource);
        }

        return tDiagram;
    }

    public static Diagram getTransformationDiagram( final ModelResource modelResource,
                                                    final EObject transformationObject ) {
        Diagram tDiagram = null;

        EObject mappingClassEObject = null;
        if (TransformationHelper.isSqlTransformation(transformationObject)) {

        } else if (TransformationHelper.isSqlTransformationMappingRoot(transformationObject)
                   || TransformationHelper.isXQueryTransformationMappingRoot(transformationObject)) {
            mappingClassEObject = TransformationHelper.getTransformationLinkTarget(transformationObject);
        } else if (TransformationHelper.isTransformationMapping(transformationObject)) {
            EObject tRoot = transformationObject.eContainer();
            if (tRoot != null) mappingClassEObject = TransformationHelper.getTransformationLinkTarget(tRoot);
        }

        if (mappingClassEObject != null) {
            // create one here.
            tDiagram = getTransformationDiagram(modelResource, mappingClassEObject, true, true);
        }

        return tDiagram;
    }

    public static boolean isStandardVirtualSqlTable( final Object input ) {
        boolean result = false;
        if (input instanceof EObject
            && (TransformationHelper.isVirtualSqlTable(input) || TransformationHelper.isSqlVirtualProcedure(input))) {
            EObject eObject = (EObject)input;
            final String mmUri = eObject.eClass().getEPackage().getNsURI();
            if (mmUri != null
                && (mmUri.equals(RELATIONAL_URI) || mmUri.equals(DATA_ACCESS_URI) || mmUri.equals(WEB_SERVICE_URI) || mmUri.equals(XML_SERVICE_URI))) {
                result = true;
            }
        }

        return result;
    }

    public static boolean isVirtualProcedureOrTable( final EObject eObject ) {
        boolean result = ((com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(eObject) || com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(eObject)) && ModelObjectUtilities.isVirtual(eObject));
        return result;
    }

    public static boolean isVirtualProcedure( final EObject eObject ) {
        return com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(eObject) && ModelObjectUtilities.isVirtual(eObject);
    }

    public static boolean diagramExistsForProcedure( final ModelResource modelResource,
                                                     final EObject eObject ) {
        boolean exists = false;
        if (isVirtualProcedure(eObject)) {
            try {
                List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(eObject));
                if (!diagramList.isEmpty()) exists = true;
            } catch (ModelWorkspaceException e) {
                UiConstants.Util.log(IStatus.ERROR, e, null);
            }
        }

        return exists;
    }

    public static boolean isTreeLayout() {
        return UiPlugin.getDefault().getPreferenceStore().getBoolean(com.metamatrix.query.ui.UiConstants.Prefs.TREE_DIAGRAM_LAYOUT);
    }

}
