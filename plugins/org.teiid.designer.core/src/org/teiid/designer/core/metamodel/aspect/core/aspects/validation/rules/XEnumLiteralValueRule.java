/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.validation.rules.StringNameRule;
import org.teiid.designer.metamodels.core.extension.ExtensionPackage;
import org.teiid.designer.metamodels.core.extension.XEnumLiteral;


/**
 * @since 8.0
 */
public class XEnumLiteralValueRule extends StringNameRule {

    /**
     * @since 4.2
     */
    public XEnumLiteralValueRule() {
        super(ExtensionPackage.XENUM_LITERAL__VALUE);
    }

    /**
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
    public void validate( EStructuralFeature eStructuralFeature,
                          EObject eObject,
                          Object value,
                          ValidationContext context ) {

        CoreArgCheck.isInstanceOf(XEnumLiteral.class, eObject);
        if (eStructuralFeature.getFeatureID() != this.getFeatureID()) {
            return;
        }

        final XEnumLiteral xenum = (XEnumLiteral)eObject;

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

        List siblings = getSiblingsForUniquenessCheck(eObject);
        final Iterator iter = siblings.iterator();
        while (iter.hasNext()) {
            final XEnumLiteral sibling = (XEnumLiteral)iter.next();
            if (!sibling.getName().equalsIgnoreCase(name) && sibling.getValue() == xenum.getValue()) {
                final String msg = ModelerCore.Util.getString("XEnumLiteralValueRule.DuplicateValue"); //$NON-NLS-1$                
                final ValidationResult result = new ValidationResultImpl(xenum);
                final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                result.addProblem(problem);
                context.addResult(result);
                return;
            }
        }

    }

    /**
     * @see org.teiid.designer.core.validation.rules.StringNameRule#getSiblingsForUniquenessCheck(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
    protected List getSiblingsForUniquenessCheck( EObject eObject ) {

        final List siblings = new LinkedList(super.getSiblingsForUniquenessCheck(eObject));
        // Remove those not to be considered ...
        final Iterator iter = siblings.iterator();
        while (iter.hasNext()) {
            final EObject sibling = (EObject)iter.next();
            if (!(sibling instanceof XEnumLiteral)) {
                iter.remove();
            }
        }
        return siblings;
    }

}
