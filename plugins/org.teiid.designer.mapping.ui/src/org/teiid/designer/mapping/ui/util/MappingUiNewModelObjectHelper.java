/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.util;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.util.INewModelObjectHelper;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.mapping.ui.diagram.MappingDiagramProvider;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
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
     * @see org.teiid.designer.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     * @since 4.3
     */
    @Override
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
     * @see org.teiid.designer.core.util.INewModelObjectHelper#helpCreate(java.lang.Object, Map)
     * @since 4.3
     */
    @Override
	public boolean helpCreate( Object newObject,
                               Map properties,
                               List<EObject> references) {
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
