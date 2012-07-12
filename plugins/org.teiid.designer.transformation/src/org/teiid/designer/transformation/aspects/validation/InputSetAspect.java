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
import org.teiid.designer.metamodels.transformation.InputSet;


/** 
 * InputSetAspect
 * @since 4.2
 */
public class InputSetAspect extends TransformationAspect {

    /** 
     * InputSetAspect
     * @param entity
     * @since 4.2
     */
    public InputSetAspect(MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isInstanceOf(InputSet.class, eObject);
        if(!context.shouldIgnore(eObject)) {
            InputSet inputSet = (InputSet) eObject;
	        EObject mappingClass = inputSet.getMappingClass();
	        if(mappingClass != null) {
	            ValidationAspect validAspect = AspectManager.getValidationAspect(mappingClass);
	            if(validAspect != null) {
	                boolean shouldValidate = validAspect.shouldValidate(mappingClass, context);
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
