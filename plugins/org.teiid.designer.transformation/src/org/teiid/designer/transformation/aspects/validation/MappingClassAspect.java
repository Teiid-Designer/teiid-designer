/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.ValidationAspect;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.core.validation.ValidationRuleSet;
import org.teiid.designer.core.validation.rules.StringLengthRule;
import org.teiid.designer.core.validation.rules.StringNameRule;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.TransformationPackage;


/**
 * MappingClassAspect
 *
 * @since 8.0
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
	 * @See org.teiid.designer.core.metamodel.aspect.ValidationAspect#updateContext(org.teiid.designer.core.validation.ValidationContext)
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
     * @see org.teiid.designer.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(EObject eObject, final ValidationContext context) {
        CoreArgCheck.isInstanceOf(MappingClass.class, eObject);
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
