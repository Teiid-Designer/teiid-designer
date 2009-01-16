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

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.IClassifierContentAdapter;
import com.metamatrix.modeler.mapping.ui.PluginConstants;

/**
 * MappingClassContentAdapter
 */
public class MappingClassContentAdapter implements IClassifierContentAdapter {
    
    /**
     * Construct an instance of MappingClassContentAdapter.
     * 
     */
    public MappingClassContentAdapter() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.notation.uml.model.IClassifierContentAdapter#showInnerClasses(org.eclipse.emf.ecore.EObject)
     */
    public boolean showInnerClasses(EObject classifierEObject, Diagram diagram) {
        boolean showInnerClasses = false;
        // Get the current diagram, and get it's type.
        if(    diagram != null && diagram.getType() != null ) {
            if( diagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID) ||
                 diagram.getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID) ) {
                if( isMappingClass(classifierEObject) ) {
                    showInnerClasses = false;
                }else if(com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(classifierEObject) ) {
                    showInnerClasses = true;
                }
            }
        }

            
        return showInnerClasses;
    }
    
    private boolean isMappingClass(EObject eObject) {
        if( eObject instanceof MappingClass && !(eObject instanceof InputSet) )
            return true;
            
        return false;
    }

}
