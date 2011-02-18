/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.util.INewModelObjectHelper;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * @since 4.3
 */
public class TransformationUiNewModelObjectHelper implements INewModelObjectHelper {

    /**
     * @since 4.3
     */
    public TransformationUiNewModelObjectHelper() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     * @since 4.3
     */
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
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#helpCreate(java.lang.Object, Map)
     * @since 4.3
     */
    public boolean helpCreate( Object newObject,
                               Map properties ) {
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
