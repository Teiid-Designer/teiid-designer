/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XAttribute;
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
public class XAttributeNameRule extends StringNameRule {

    /**
     * @since 4.2
     */
    public XAttributeNameRule() {
        super(ExtensionPackage.XATTRIBUTE__NAME);
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
        ArgCheck.isInstanceOf(XAttribute.class, eObject);
        if (eStructuralFeature.getFeatureID() != this.getFeatureID()) {
            return;
        }
        super.validate(eStructuralFeature, eObject, value, context);

        final XAttribute xattribute = (XAttribute)eObject;

        // Make sure the name is set ...
        final String name = xattribute.getName();
        if (name == null || name.trim().length() == 0) {
            final ValidationResult result = new ValidationResultImpl(xattribute);
            final String msg = ModelerCore.Util.getString("XAttributeNameRule.MissingName"); //$NON-NLS-1$
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
            if (!(sibling instanceof XAttribute)) {
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
