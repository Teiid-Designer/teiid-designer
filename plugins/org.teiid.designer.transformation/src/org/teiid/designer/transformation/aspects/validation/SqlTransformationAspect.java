/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.ValidationAspect;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.metamodels.transformation.SqlTransformation;



/** 
 * @since 4.2
 */
public class SqlTransformationAspect extends TransformationAspect {

    /** 
     * @param entity
     * @since 4.2
     */
    public SqlTransformationAspect(MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isInstanceOf(SqlTransformation.class, eObject);
        if(!context.shouldIgnore(eObject)) {
            SqlTransformation sqlTransformation = (SqlTransformation) eObject;
	        EObject mapping = sqlTransformation.getMapper();
	        if(mapping != null) {
	            ValidationAspect validAspect = AspectManager.getValidationAspect(mapping);
	            if(validAspect != null) {
	                boolean shouldValidate = validAspect.shouldValidate(mapping, context);
	                if(!shouldValidate) {
	    	            context.addObjectToIgnore(eObject, true);
	                }
	                return shouldValidate;
	            }
	        }
	        return true;
        }
        return false;
    }

}
