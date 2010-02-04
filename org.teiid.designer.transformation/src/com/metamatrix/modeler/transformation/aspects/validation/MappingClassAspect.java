/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.core.validation.rules.StringLengthRule;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * MappingClassAspect
 */
public class MappingClassAspect extends TransformationAspect {

	public static final ValidationRule NAME_RULE = new StringNameRule(TransformationPackage.MAPPING_CLASS__NAME);
	public static final ValidationRule LENGTH_RULE = new StringLengthRule(TransformationPackage.MAPPING_CLASS__NAME);

	public MappingClassAspect(final MetamodelEntity entity) {
		super(entity);
	}

	/**
	 * Get validation rules for MappingClassAspect
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NAME_RULE);
		addRule(LENGTH_RULE);
		return super.getValidationRules();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#updateContext(com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	@Override
    public void updateContext(final EObject eObject, final ValidationContext context) {
		Map transformMap = context.getTargetTransformMap();
		if(transformMap != null) {
			if(transformMap.containsKey(eObject)) {
				return;
			}
		}
		context.addTargetTransform(eObject, null);
	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(EObject eObject, final ValidationContext context) {
        ArgCheck.isInstanceOf(MappingClass.class, eObject);
        if(!context.shouldIgnore(eObject)) {
	        MappingClass mappingClass = (MappingClass) eObject;
	        Resource resource = mappingClass.eResource();
	        ModelContents contents = null;
	        if(resource instanceof EmfResource) {
	            EmfResource emfResource = (EmfResource) resource;
	            contents = emfResource.getModelContents();
	            if(contents != null) {
			        Collection mappingRoots = contents.getTransformationsForInput(mappingClass);
			        if(!mappingRoots.isEmpty()) {
				        boolean shouldValidate = false;
				        for(final Iterator iter = mappingRoots.iterator(); iter.hasNext();) {
				            EObject mappingRoot = (EObject) iter.next();
				            ValidationAspect validAspect = AspectManager.getValidationAspect(mappingRoot);
				            if(validAspect == null || validAspect.shouldValidate(mappingRoot, context)) {
				                shouldValidate = true;
				                break;
				            }     
				        }
				        if(!shouldValidate) {
				            context.addObjectToIgnore(eObject, true);
				        }
				        return shouldValidate;
			        }
	            }
	        }
	        return true;
        }
        return false;
    }
}
