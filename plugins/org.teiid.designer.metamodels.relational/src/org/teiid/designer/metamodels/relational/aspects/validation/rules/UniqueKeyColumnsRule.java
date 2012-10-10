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
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.UniqueKey;


/**
 * UniqueKeyColumnsRule. 1) Warning if a primary key references a nullable coulmn. 2) Error if unique key references columns from
 * differrent tables.
 *
 * @since 8.0
 */
public class UniqueKeyColumnsRule implements ObjectValidationRule {

    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate( final EObject eObject,
                          final ValidationContext context ) {
        CoreArgCheck.isInstanceOf(UniqueKey.class, eObject);

        final UniqueKey uniqueKey = (UniqueKey)eObject;

        // unique key reference not set, nothing to validate
        // there is already a validation error
        // get the columns for the unique key
        final EList ukColumns = uniqueKey.getColumns();

        // get the table for the unique key
        final BaseTable ukTable = uniqueKey.getTable();
        CoreArgCheck.isNotNull(ukTable);

        final ValidationResult result = new ValidationResultImpl(eObject);

        final Iterator colIter = ukColumns.iterator();
        while (colIter.hasNext()) {
            final Column column = (Column)colIter.next();
            // 1) Warning if a proimary key references a nullable coulmn.
            if (uniqueKey instanceof PrimaryKey) {
                final NullableType nullableType = column.getNullable();
                if (nullableType.getValue() == NullableType.NULLABLE) {
                    final ValidationProblem problem = new ValidationProblemImpl(
                                                                                0,
                                                                                IStatus.WARNING,
                                                                                RelationalPlugin.Util.getString("UniqueKeyColumnsRule.Primary_key__1") + uniqueKey.getName() + RelationalPlugin.Util.getString("UniqueKeyColumnsRule._references_a_nullable_column._2")); //$NON-NLS-1$ //$NON-NLS-2$
                    result.addProblem(problem);
                    break;
                }
            }
            final BaseTable colTable = (BaseTable)column.eContainer();
            CoreArgCheck.isNotNull(colTable);
            // 2) Error if unique key references columns from differrent tables.
            if (!ukTable.equals(colTable)) {
                // create validation problem and add it to the result
                final String msg = RelationalPlugin.Util.getString("UniqueKeyColumnsRule.Unique_key_{0}_may_not_reference_column_{1}_from_a_different_table_1", uniqueKey.getName(), column.getName()); //$NON-NLS-1$
                final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                result.addProblem(problem);
                break;
            }
        }

        // add the result to the context
        context.addResult(result);
    }

}
