/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
