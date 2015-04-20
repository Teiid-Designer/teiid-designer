/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.util;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.util.INewModelObjectHelper;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class TransformationUiNewModelObjectHelper implements INewModelObjectHelper {

    /**
     * @since 4.3
     */
    public TransformationUiNewModelObjectHelper() {
        super();
    }

    /**
     * @see org.teiid.designer.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     * @since 4.3
     */
    @Override
	public boolean canHelpCreate( Object newObject ) {
        CoreArgCheck.isNotNull(newObject);

        // First case is a standard virtual table
        // If the createdObject is VirtualTable, set supportsUpdate to false
        if (newObject != null && newObject instanceof EObject) {
            EObject newEObject = (EObject)newObject;
            return isVirtualTable(newEObject);
        }
        return false;
    }

    private boolean isVirtualTable( EObject newEObject ) {
        if (TransformationHelper.isVirtual(newEObject)) {
            // If the createdObject is VirtualTable, set supportsUpdate to false & create T-Root
            if (TransformationHelper.isSqlTable(newEObject) && !TransformationHelper.isXmlDocument(newEObject)
                && !TransformationHelper.isMappingClass(newEObject)) {
                return true;
            } else if (TransformationHelper.isSqlProcedure(newEObject)) {
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
                               List<EObject> references ) {
        CoreArgCheck.isNotNull(newObject);

        if (newObject instanceof EObject) {
            EObject newTarget = (EObject)newObject;
            if (isVirtualTable(newTarget)) {
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(newTarget);
                if (mr != null && !diagramExists(newTarget, mr)) {
                    TransformationDiagramUtil.createTransformationDiagram(newTarget, mr, true);
                }
            }
        }
        return true;
    }

    private boolean diagramExists( EObject target,
                                   ModelResource modelResource ) {
        return TransformationDiagramUtil.getTransformationDiagram(modelResource, target, false, false) != null;
    }

}
