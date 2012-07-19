/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.uml;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.relational.RelationalPlugin;


/**
 * KeyAspect
 *
 * @since 8.0
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
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_UniqueConstraint_type"); //$NON-NLS-1$
    }

    @Override
	public int getEndCount(Object obj) {
        return 0;
    }

    @Override
	public String getRoleName(Object assoc, int end) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_UniqueConstraint_type"); //$NON-NLS-1$
    }
    
    @Override
	public EObject getEnd(Object constraint, int end){
        return null;
    }

    @Override
	public EObject getEndTarget(Object constraint, int end){
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    @Override
	public String getName(Object eObject) {
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    @Override
	public String getToolTip(Object eObject) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append(this.getStereotype(eObject));
        return sb.toString();
    }

}
