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

package com.metamatrix.modeler.mapping.ui.util;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.ArgCheck;
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
        ArgCheck.isNotNull(newObject);

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
        ArgCheck.isNotNull(newObject);

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
