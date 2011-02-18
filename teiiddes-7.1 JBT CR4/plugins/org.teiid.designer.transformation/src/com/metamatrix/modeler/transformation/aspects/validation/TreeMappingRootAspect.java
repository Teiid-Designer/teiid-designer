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
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * TreeMappingRootAspect
 */
public class TreeMappingRootAspect extends TransformationAspect {

    public TreeMappingRootAspect(MetamodelEntity entity) {
        super(entity);
    }
    
	/**
	 * Get validation rules for TreeMappingRoot
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(DOCUMENT_RULE);
		return super.getValidationRules();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#updateContext(com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	@Override
    public void updateContext(final EObject eObject, final ValidationContext context) {
		SqlTransformationAspect transformationAspect = (SqlTransformationAspect) AspectManager.getSqlAspect(eObject);
		EObject transformedObject = (EObject) transformationAspect.getTransformedObject(eObject);
		// update the map
		context.addTargetTransform(transformedObject, eObject);
	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        if(!context.shouldIgnore(eObject)) {
			// check if all of the outputs to the mapping root are excluded
			// from the xmldocument
	        boolean shouldValidate = false;
	        TreeMappingRoot mappingRoot = (TreeMappingRoot) eObject;
			Collection outputs = mappingRoot.getOutputs();
			for(final Iterator iter = outputs.iterator(); iter.hasNext();) {
			    EObject outputEObject = (EObject) iter.next();
				ValidationAspect outputAspect = AspectManager.getValidationAspect(outputEObject);
				if(outputAspect == null || outputAspect.shouldValidate(outputEObject, context)) {
				    shouldValidate = true;
				    break;
				}
			}
			
			if(!shouldValidate) {
	            context.addObjectToIgnore(eObject, true);
	        }
	        return shouldValidate;
        }
        return false;
    }
}
