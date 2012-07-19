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
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.ValidationAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationRuleSet;
import org.teiid.designer.metamodels.transformation.TreeMappingRoot;


/**
 * TreeMappingRootAspect
 *
 * @since 8.0
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
	 * @See org.teiid.designer.core.metamodel.aspect.ValidationAspect#updateContext(org.teiid.designer.core.validation.ValidationContext)
	 */
	@Override
    public void updateContext(final EObject eObject, final ValidationContext context) {
		SqlTransformationAspect transformationAspect = (SqlTransformationAspect) AspectManager.getSqlAspect(eObject);
		EObject transformedObject = (EObject) transformationAspect.getTransformedObject(eObject);
		// update the map
		context.addTargetTransform(transformedObject, eObject);
	}

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
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
