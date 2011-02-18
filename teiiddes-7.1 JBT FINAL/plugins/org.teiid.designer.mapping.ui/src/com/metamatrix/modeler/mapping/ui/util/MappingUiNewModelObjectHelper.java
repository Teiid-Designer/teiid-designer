/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.util;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.util.INewModelObjectHelper;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramProvider;

/**
 * @since 5.0
 */
public class MappingUiNewModelObjectHelper implements INewModelObjectHelper {
    private static MappingDiagramProvider mappingDiagramProvider;

    /**
     * @since 5.0
     */
    public MappingUiNewModelObjectHelper() {
        super();
        mappingDiagramProvider = new MappingDiagramProvider();
    }

    /**
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     * @since 4.3
     */
    public boolean canHelpCreate( Object newObject ) {
        CoreArgCheck.isNotNull(newObject);

        // First case is a standard mapping class table
        if (newObject != null && newObject instanceof EObject) {
            EObject newEObject = (EObject)newObject;
            return isMappingClass(newEObject);
        }
        return false;
    }

    private boolean isMappingClass( EObject newEObject ) {
        if (TransformationHelper.isVirtual(newEObject)) {
            // If the createdObject is mapping class
            if (TransformationHelper.isSqlTable(newEObject) && !TransformationHelper.isXmlDocument(newEObject)
                && TransformationHelper.isMappingClass(newEObject)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#helpCreate(java.lang.Object, Map)
     * @since 4.3
     */
    public boolean helpCreate( Object newObject,
                               Map properties ) {
        CoreArgCheck.isNotNull(newObject);

        if (newObject instanceof EObject) {
            EObject newTarget = (EObject)newObject;
            if (isMappingClass(newTarget)) {
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(newTarget);
                if (mr != null && !diagramExists(newTarget, mr)) {
                    mappingDiagramProvider.getDetailedMappingDiagram(newTarget);
                }
            }
        }
        return true;
    }

    private boolean diagramExists( EObject target,
                                   ModelResource modelResource ) {
        return mappingDiagramProvider.hasDetailedMappingDiagram(modelResource, target);
    }
}
