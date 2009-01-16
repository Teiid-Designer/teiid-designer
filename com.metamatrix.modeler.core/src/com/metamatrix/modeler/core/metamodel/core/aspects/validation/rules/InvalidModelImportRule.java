/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/**
 * @since 4.3
 */
public class InvalidModelImportRule implements ObjectValidationRule {

    /**
     *
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.3
     */
    public void validate(EObject eObject, ValidationContext context) {

        ArgCheck.isInstanceOf(ModelImport.class, eObject);
        final ModelImport modelImport = (ModelImport) eObject;
        final String location = modelImport.getModelLocation();
        final Resource resource = eObject.eResource();
        Assertion.isNotNull(resource);

        if (location == null) {
            ValidationResult result = new ValidationResultImpl(eObject, resource);
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,ModelerCore.Util.getString("InvalidModelImportRule.0"), //$NON-NLS-1$
                                                                   getLocationPath(modelImport), getURIString(modelImport));
            result.addProblem(problem);
            context.addResult(result);
            return;
        }

        // Defect 17511 - The UUID in the import should not the same as the containing resource.
        String resourceUUID = ModelerCore.getObjectIdString(modelImport.getModel());
        String importResourceUUID = modelImport.getUuid();
        if( resourceUUID != null && resourceUUID.equals( importResourceUUID ) ) {
            ValidationResult result = new ValidationResultImpl(eObject, modelImport);
            Object[] params = new Object[]{location};
            String msg = ModelerCore.Util.getString("InvalidModelImportRule.The_model_import_is_invalid", params); //$NON-NLS-1$
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, msg,
                                                                   getLocationPath(modelImport), getURIString(modelImport));
            result.addProblem(problem);
            context.addResult(result);
        }

//        // Check to see if the import is to the MetaMatrix built-in datatypes
//        // resource or to one of the Emf XMLSchema resources
//        if (WorkspaceResourceFinderUtil.isGlobalResource(location)) {
//            return;
//        }
//
//        //MyDefect : Fixing 17511
//        //UUID in the import should not the same as the containing resource.
//        String modelObjectId = modelImport.getModel().getObjectId().toString();
//        String modelUuid = modelImport.getUuid();
//        if( modelObjectId.equals( modelUuid ) ) {
//            ValidationResult result = new ValidationResultImpl(eObject, modelImport);
//            Object[] params = new Object[]{location};
//            String msg = ModelerCore.Util.getString("InvalidModelImportRule.The_model_import_is_invalid", params); //$NON-NLS-1$
//            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, msg);
//            result.addProblem(problem);
//            context.addResult(result);
//        }

    }

    private static String getURIString(EObject eoj) {
        return ModelerCore.getModelEditor().getUri(eoj).toString();
    }

    private static String getLocationPath(EObject eoj) {
        return ModelerCore.getModelEditor().getModelRelativePath(eoj).toString();
    }

}
