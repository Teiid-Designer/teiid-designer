/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ColumnSet;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Table;


/**
 * AccessPatternColumnsRule
 *
 * @since 8.0
 */
public class AccessPatternColumnsRule implements ObjectValidationRule {

    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate( final EObject eObject,
                          final ValidationContext context ) {
        CoreArgCheck.isInstanceOf(AccessPattern.class, eObject);

        final AccessPattern accessPattern = (AccessPattern)eObject;

        // get the columns for the accessPattern
        final EList apColumns = accessPattern.getColumns();

        // get the table for the accessPattern
        final Table apTable = accessPattern.getTable();
        CoreArgCheck.isNotNull(apTable);

        final Iterator colIter = apColumns.iterator();
        while (colIter.hasNext()) {
            final Column column = (Column)colIter.next();
            final ColumnSet colTable = (ColumnSet)column.eContainer();
            CoreArgCheck.isNotNull(colTable);

            if (!apTable.equals(colTable)) {
                final ValidationResult result = new ValidationResultImpl(eObject);
                // create validation problem and add it to the result
                final String msg = RelationalPlugin.Util.getString("AccessPatternColumnsRule.AccessPattern_{0}_may_not_reference_column_{1}_from_a_different_table_1", accessPattern.getName(), column.getName()); //$NON-NLS-1$
                result.addProblem(new ValidationProblemImpl(0, IStatus.ERROR, msg));
                context.addResult(result);
                return;
            }
        }
    }

}
