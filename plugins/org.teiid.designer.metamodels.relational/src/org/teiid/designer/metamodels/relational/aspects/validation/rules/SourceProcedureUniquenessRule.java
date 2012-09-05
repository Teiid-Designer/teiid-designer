/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.util.PushdownFunctionUtil;

/**
 * ScalarFunctionUniquenessRule
 */
public class SourceProcedureUniquenessRule implements ObjectValidationRule {
    private final String ruleName;

    /**
     * Construct an instance of ScalarFunctionUniquenessRule.
     * 
     */
    public SourceProcedureUniquenessRule() {
        super();
        this.ruleName = this.getClass().getName();
    }

    /**
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      org.teiid.designer.core.validation.ValidationContext)
     */
    public void validate( final EObject eObject,
                          final ValidationContext context ) {

        if (context.hasRunRule(eObject, this.ruleName)) {
            return;
        }

        // Check whether there are any other scalar functions with a similar signature ...
        final List siblings = getSiblingsForUniquenessCheck(eObject);
        if (siblings.size() < 2) {
            return; // no siblings ...
        }

        // Get the signature of the siblings ...
        final Map siblingsBySignature = new HashMap();
        final Iterator iter = siblings.iterator();
        while (iter.hasNext()) {
            final EObject sibling = (EObject)iter.next();
            if (sibling instanceof Procedure && ((Procedure)sibling).isFunction()) {
                final String signature = computeSignature(sibling);
                List existing = (List)siblingsBySignature.get(signature);
                if (existing == null) {
                    existing = new LinkedList();
                    siblingsBySignature.put(signature, existing);
                }
                if (!existing.contains(sibling)) {
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
            if (numSiblingsWithSameSignature > 1) {
                final Iterator siblingIter = siblingsWithSameSignature.iterator();
                while (siblingIter.hasNext()) {
                    final EObject sibling = (EObject)siblingIter.next();
                    ValidationResult result = new ValidationResultImpl(sibling);
                    final String msg = getDuplicateMessage(numSiblingsWithSameSignature, signature);
                    ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                    result.addProblem(problem);
                    // add the result to the context
                    context.addResult(result);
                }
            }
        }

        // set the rule has been run
        context.recordRuleRun(eObject, this.ruleName);
    }

    protected String computeSignature(EObject eObject) {
        return PushdownFunctionUtil.getSignature((Procedure)eObject);
    }

    protected String getDuplicateMessage( final int numDuplicates,
                                          final String signature ) {
        final Object[] params = new Object[] {new Integer(numDuplicates - 1), signature};
        final String msg = RelationalPlugin.Util.getString("SourceProcedureUniquenessRule.Same_signature_as_{0}_sibling(s)__{1}", params); //$NON-NLS-1$
        return msg;
    }

    /**
     * Obtain those siblings of the supplied object that are to be considered the domain of objects for the uniqueness check.
     * 
     * @param eObject the object whose name is to be checked for uniqueness amongst its siblings
     * @return the siblings that should be used to check for uniqueness in the name; never null
     */
    protected List getSiblingsForUniquenessCheck( final EObject eObject ) {
        final EObject parent = eObject.eContainer();
        if (parent != null) {
            // eObject is not a parent ...
            return parent.eContents();
        }
        // eObject is one of the roots ...
        final Resource resource = eObject.eResource();
        if (resource != null) {
            return resource.getContents();
        }
        return new ArrayList(1);
    }
    
}
