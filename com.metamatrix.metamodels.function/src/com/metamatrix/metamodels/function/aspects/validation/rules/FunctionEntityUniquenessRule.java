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

package com.metamatrix.metamodels.function.aspects.validation.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.metamodels.function.FunctionPlugin;
import com.metamatrix.metamodels.function.ScalarFunction;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * ScalarFunctionUniquenessRule
 */
public abstract class FunctionEntityUniquenessRule implements ObjectValidationRule {

    private final String ruleName;

    /**
     * Construct an instance of ScalarFunctionUniquenessRule.
     * 
     */
    public FunctionEntityUniquenessRule() {
        super();
        this.ruleName = this.getClass().getName();
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(final EObject eObject, final ValidationContext context) {

        if(context.hasRunRule(eObject, this.ruleName)) {
            return;
        }

        // Check whether there are any other scalar functions with a similar signature ...
        final List siblings = getSiblingsForUniquenessCheck(eObject);
        if ( siblings.size() < 2 ) {
            return;     // no siblings ...
        }

        // Get the signature of the siblings ...
        final Map siblingsBySignature = new HashMap();
        final Iterator iter = siblings.iterator();
        while (iter.hasNext()) {
            final EObject sibling = (EObject)iter.next();
            if ( sibling instanceof ScalarFunction ) {
                final String signature = computeSignature(sibling);
                List existing = (List) siblingsBySignature.get(signature);
                if ( existing == null ) {
                    existing = new LinkedList();
                    siblingsBySignature.put(signature,existing);
                }
                if ( !existing.contains(sibling) ) {
                    existing.add(sibling);
                }
            }
        }
        
        // Go through all the duplicate signatures and create errors ...
        final Iterator iterator = siblingsBySignature.keySet().iterator();
        while (iterator.hasNext()) {
            final String signature = (String)iterator.next();
            
            final List siblingsWithSameSignature = (List)siblingsBySignature.get(signature);
            final int numSiblingsWithSameSignature = siblingsWithSameSignature.size();
            
            // create validation problem and addit to the results
            if ( numSiblingsWithSameSignature > 1 ) {
                final Iterator siblingIter = siblingsWithSameSignature.iterator();
                while (siblingIter.hasNext()) {
                    final EObject sibling = (EObject)siblingIter.next();
                    ValidationResult result = new ValidationResultImpl(sibling);
                    final String msg = getDuplicateMessage(numSiblingsWithSameSignature,signature);
                    ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                    result.addProblem(problem);
                    // add the result to the context
                    context.addResult(result);
                }
            }
        }

        // set the rule has been run
        context.recordRuleRun(eObject, this.ruleName);
    }
    
    protected abstract String computeSignature(EObject eObject);
    
    protected String getDuplicateMessage( final int numDuplicates, final String signature ) {
        final Object[] params = new Object[]{new Integer(numDuplicates-1),signature};
        final String msg = FunctionPlugin.Util.getString("FunctionEntityUniquenessRule.Same_signature_as_{0}_sibling(s)__{1}",params); //$NON-NLS-1$
        return msg;
    }
    
    /**
    * Obtain those siblings of the supplied object that are to be considered the domain of objects
    * for the uniqueness check.
    * @param eObject the object whose name is to be checked for uniqueness amongst its siblings
    * @return the siblings that should be used to check for uniqueness in the name; never null
    */
   protected List getSiblingsForUniquenessCheck( final EObject eObject ) {
       final EObject parent = eObject.eContainer();
       if ( parent != null ) {
           // eObject is not a parent ...
           return parent.eContents();
       }
       // eObject is one of the roots ...
       final Resource resource = eObject.eResource();
       if ( resource != null ) {
           return resource.getContents();
       }
       return new ArrayList(1);
   }
 
}
