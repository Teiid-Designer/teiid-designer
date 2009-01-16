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

package com.metamatrix.metamodels.relational.aspects.validation.rules;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * ProcedureParametersRule
 */
public class ProcedureParametersRule implements ObjectValidationRule {

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(Procedure.class, eObject);

        Procedure procedure = (Procedure) eObject;

        ValidationResult result = new ValidationResultImpl(eObject);

        // get the procedure parameters, check the direction on the
        // parameter
        // 1) Warn if the parameter direction is not set.
        // 2) Warn if the parameter direction is 'UNKNOWN".
        // 3) Error if more than one paramter is of 'RETURN' direction. 
        
        Iterator paramIter = procedure.getParameters().iterator();
        boolean foundReturnParam = false;
        while(paramIter.hasNext()) {
            ProcedureParameter param = (ProcedureParameter) paramIter.next();
            DirectionKind direction = param.getDirection();
            if(direction == null) {
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, RelationalPlugin.Util.getString("ProcedureParametersRule.Parameter_{0}_does_not_have_a_direction._1", new Object[]{param.getName()})); //$NON-NLS-1$
                result.addProblem(problem);
            }

            // validate the direction of the parameters in the procedure 
            int directionKind = direction.getValue();
            if(directionKind == DirectionKind.UNKNOWN) {
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, RelationalPlugin.Util.getString("ProcedureParametersRule.Parameter_{0}_has_an_UNKNOWN_direction._1", new Object[]{param.getName()})); //$NON-NLS-1$
                result.addProblem(problem);
            } else if(directionKind == DirectionKind.RETURN) {
                if(!foundReturnParam) {
                    foundReturnParam = true;
                    continue;
                }
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, RelationalPlugin.Util.getString("ProcedureParametersRule.Procedure_{0}_has_more_than_one_parameter_with_RETURN_direction._1",  new Object[]{procedure.getName()})); //$NON-NLS-1$
                result.addProblem(problem);
                context.addResult(result);
                return;                
            }
        }
		// add the result to the context
		context.addResult(result);
    }

}
