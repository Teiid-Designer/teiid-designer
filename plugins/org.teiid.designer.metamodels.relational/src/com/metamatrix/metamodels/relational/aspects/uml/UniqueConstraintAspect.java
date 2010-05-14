/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.uml;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * KeyAspect
 */
public class UniqueConstraintAspect extends UniqueKeyAspect {
    /**
     * Construct an instance of UniqueConstraintAspect.
     * @param entity
     */
    public UniqueConstraintAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }

    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_UniqueConstraint_type"); //$NON-NLS-1$
    }

    public int getEndCount(Object obj) {
        return 0;
    }

    public String getRoleName(Object assoc, int end) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_UniqueConstraint_type"); //$NON-NLS-1$
    }
    
    public EObject getEnd(Object constraint, int end){
        return null;
    }

    public EObject getEndTarget(Object constraint, int end){
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    public String getName(Object eObject) {
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    public String getToolTip(Object eObject) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append(this.getStereotype(eObject));
        return sb.toString();
    }

}
