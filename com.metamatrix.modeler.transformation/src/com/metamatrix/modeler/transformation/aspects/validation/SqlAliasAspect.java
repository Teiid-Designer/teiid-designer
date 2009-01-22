/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;


/** 
 * @since 4.2
 */
public class SqlAliasAspect extends TransformationAspect {

    /**
     * @param entity
     * @since 4.2
     */
    public SqlAliasAspect(MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        ArgCheck.isInstanceOf(SqlAlias.class, eObject);
        if(!context.shouldIgnore(eObject)) {
            SqlAlias sqlAlias = (SqlAlias) eObject;
	        EObject sqlTransform = sqlAlias.getSqlTransformation();
	        if(sqlTransform != null) {
	            ValidationAspect validAspect = AspectManager.getValidationAspect(sqlTransform);
	            if(validAspect != null) {
	                boolean shouldValidate = validAspect.shouldValidate(sqlTransform, context);
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
