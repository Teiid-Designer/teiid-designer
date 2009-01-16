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

package com.metamatrix.modeler.diagram.ui.util;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.util.INewModelObjectHelper;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.pakkage.PackageDiagramUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * @since 4.3
 */
public class DiagramUiNewModelObjectHelper implements INewModelObjectHelper {

    /**
     * @since 4.3
     */
    public DiagramUiNewModelObjectHelper() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     * @since 4.3
     */
    public boolean canHelpCreate( Object newObject ) {

        // First case is a standard virtual table
        // If the createdObject is VirtualTable, set supportsUpdate to false
        if (newObject != null && newObject instanceof EObject) {
            EObject newEObject = (EObject)newObject;
            return isValidPackage(newEObject);
        }
        return false;
    }

    private boolean isValidPackage( EObject eObject ) {
        int objType = RelationalUmlEObjectHelper.getEObjectType(eObject);
        if (objType == RelationalUmlEObjectHelper.UML_PACKAGE) return true;

        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#helpCreate(java.lang.Object, Map)
     * @since 4.3
     */
    public boolean helpCreate( Object newObject,
                               Map properties ) {
        if (newObject != null && newObject instanceof EObject && canHelpCreate(newObject)) {
            EObject newEObject = (EObject)newObject;
            if (isValidPackage(newEObject)) {
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(newEObject);
                if (mr != null) {
                    PackageDiagramUtil.createPackageDiagram(newEObject, mr);
                }
            }
        }
        return true;
    }

}
