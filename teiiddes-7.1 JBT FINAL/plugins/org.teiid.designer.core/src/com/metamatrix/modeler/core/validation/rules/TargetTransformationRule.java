/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.validation.ResourceValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * TargetTransformationRule
 */
public class TargetTransformationRule implements ResourceValidationRule {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.validation.ResourceValidationRule#validate(org.eclipse.emf.ecore.resource.Resource, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate( final Resource resource,
                          final ValidationContext context ) {
        CoreArgCheck.isNotNull(resource);
        CoreArgCheck.isNotNull(context);
        // run this rule only for XMI resources
        final URI uri = resource.getURI();
        if (uri != null && !uri.lastSegment().endsWith(ModelUtil.EXTENSION_XMI)) {
            return;
        }

        Map targetTransformMap = context.getTargetTransformMap();
        // there are no mapping, meaning no virtual tables in the resource
        if (targetTransformMap == null || targetTransformMap.isEmpty()) {
            return;
        }

        Iterator targetIter = targetTransformMap.keySet().iterator();
        while (targetIter.hasNext()) {
            EObject target = (EObject)targetIter.next();
            // check if this eObject is on the same resource as we are validating
            Resource targetResource = target.eResource();
            if (targetResource != resource) {
                continue;
            }
            if (targetTransformMap.get(target) == null) {
                SqlAspect targetAspect = AspectManager.getSqlAspect(target);
                CoreArgCheck.isNotNull(targetAspect);
                if (targetAspect instanceof SqlTableAspect) {
                    SqlTableAspect tableAspect = (SqlTableAspect)targetAspect;

                    // MyDefect : 17200 Added this logic to make sure document is not xml document,
                    // a document could be a table
                    // but should be able to create an emty document for soap.
                    boolean isXmlDoc = (tableAspect.getTableType(target) == MetadataConstants.TABLE_TYPES.DOCUMENT_TYPE);

                    if (tableAspect.isVirtual(target) && !isXmlDoc) {
                        final int status = getPreferenceStatus(context);

                        if (status == IStatus.OK) {
                            return;
                        }

                        final ValidationResult result = new ValidationResultImpl(target);
                        // create validation problem and addit to the result
                        final String msg = ModelerCore.Util.getString("TargetTransformationRule.The_virtual_table_{0}_does_not_have_any_associated_transformation._1", tableAspect.getName(target)); //$NON-NLS-1$
                        final ValidationProblem problem = new ValidationProblemImpl(0, status, msg);
                        result.addProblem(problem);
                        context.addResult(result);
                    }
                } else {
                    SqlProcedureAspect procAspect = (SqlProcedureAspect)targetAspect;
                    if (procAspect.isVirtual(target)) {
                        final int status = getPreferenceStatus(context);

                        if (status == IStatus.OK) {
                            return;
                        }

                        final ValidationResult result = new ValidationResultImpl(target);
                        // create validation problem and addit to the result
                        final String msg = ModelerCore.Util.getString("TargetTransformationRule.The_virtual_procedure_{0}_does_not_have_any_associated_transformation._1", procAspect.getName(target));//$NON-NLS-1$
                        final ValidationProblem problem = new ValidationProblemImpl(0, status, msg);
                        result.addProblem(problem);
                        context.addResult(result);
                    }
                }
            }
        }
    }

    protected int getPreferenceStatus( ValidationContext context ) {
        return context.getPreferenceStatus(ValidationPreferences.RELATIONAL_EMPTY_TRANSFORMATIONS, IStatus.ERROR);
    }
}
