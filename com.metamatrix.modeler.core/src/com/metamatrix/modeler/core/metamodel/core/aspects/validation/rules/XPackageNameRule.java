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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * @since 4.2
 */

public class XPackageNameRule extends StringNameRule {

    /**
     * @since 4.2
     */
    public XPackageNameRule() {
        super(ExtensionPackage.XPACKAGE__NAME);
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
    public void validate( EStructuralFeature eStructuralFeature,
                          EObject eObject,
                          Object value,
                          ValidationContext context ) {

        ArgCheck.isInstanceOf(XPackage.class, eObject);
        if (eStructuralFeature.getFeatureID() != this.getFeatureID()) {
            return;
        }

        super.validate(eStructuralFeature, eObject, value, context);
        final XPackage xenum = (XPackage)eObject;

        // Make sure the name is set ...
        final String name = xenum.getName();
        if (name == null || name.trim().length() == 0) {
            final ValidationResult result = new ValidationResultImpl(xenum);
            final String msg = ModelerCore.Util.getString("XEnumNameRule.MissingName"); //$NON-NLS-1$
            final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
            context.addResult(result);
            return;
        }

    }

    /**
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#getSiblingsForUniquenessCheck(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    protected List getSiblingsForUniquenessCheck( EObject eObject ) {

        final List siblings = new LinkedList(super.getSiblingsForUniquenessCheck(eObject));
        // Remove those not to be considered ...
        final Iterator iter = siblings.iterator();
        while (iter.hasNext()) {
            final EObject sibling = (EObject)iter.next();
            if (!(sibling instanceof XPackage)) {
                iter.remove();
            }
        }

        return siblings;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#getInvalidCharactersSeverityCode()
     * @since 4.2
     */
    @Override
    protected int getInvalidCharactersSeverityCode() {
        return IStatus.WARNING;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#validateCharacters()
     * @since 4.3
     */
    @Override
    protected boolean validateCharacters() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#validateUniqueness()
     * @since 4.3
     */
    @Override
    protected boolean validateUniqueness() {
        return true;
    }

}
