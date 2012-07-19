/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ValidationPreferences;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalPlugin;


/**
 * MissingColumnPrecisionRule
 *
 * @since 8.0
 */
public class MissingColumnPrecisionRule implements
                                       ObjectValidationRule {

    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject eObject,
                         ValidationContext context) {
        CoreArgCheck.isInstanceOf(Column.class, eObject);

        final Column column = (Column)eObject;

        if (column.getPrecision() > 0) {
            return;
        }

        final int status = getPreferenceStatus(context);

        if (status == IStatus.OK) {
            return;
        }

        final EObject type = column.getType();
        final DatatypeManager dtMgr = context.getDatatypeManager();
        if (type != null) {
            if (dtMgr.isNumeric(type)) { 
                final ValidationResult result = new ValidationResultImpl(eObject);
                // create validation problem and add it to the result
                final Object[] params = new Object[] {};
                final String msg = RelationalPlugin.Util.getString("MissingColumnPrecisionRule.failure_message", params); //$NON-NLS-1$
                final ValidationProblem problem = new ValidationProblemImpl(0, status, msg);
                problem.setHasPreference(context.hasPreferences());
                result.addProblem(problem);
                context.addResult(result);
            }
        }
    }

    protected int getPreferenceStatus(ValidationContext context) {
        return context.getPreferenceStatus(ValidationPreferences.RELATIONAL_MISSING_COLUMN_PRECISION, IStatus.WARNING);
    }

}
