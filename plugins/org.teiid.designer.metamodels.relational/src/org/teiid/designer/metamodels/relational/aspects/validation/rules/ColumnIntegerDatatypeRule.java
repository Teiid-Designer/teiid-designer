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
import org.teiid.designer.core.types.DatatypeConstants;
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
 * ColumnIntegerDatatypeRule
 *
 * @since 8.0
 */
public class ColumnIntegerDatatypeRule implements ObjectValidationRule {
    
    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(Column.class, eObject);

        Column column = (Column) eObject;

        EObject dataType = column.getType();
        if(dataType == null) {
            return;    
        }
        final DatatypeManager dtMgr = context.getDatatypeManager();
        boolean isBuiltInType = dtMgr.isBuiltInDatatype(dataType);
        String typeName = dtMgr.getName(dataType);
        if(!isBuiltInType ||typeName == null || !typeName.equals(DatatypeConstants.BuiltInNames.INTEGER)) {
            return;
        }
        
        int severity = IStatus.WARNING;

        if (context.hasPreferences()) {
            severity = context.getPreferenceStatus(ValidationPreferences.RELATIONAL_COLUMN_INTEGER_TYPE, severity);

            if (severity == IStatus.OK) {
                return;
            }
        }

        // problem exists
        ValidationResult result = new ValidationResultImpl(eObject);
        // create validation problem and add it to the result
        final String msg = RelationalPlugin.Util.getString("ColumnIntegerDatatypeRule.Integer_datatype_would_result_in_a_bigInteger_runtimetype._1"); //$NON-NLS-1$
        ValidationProblem problem  = new ValidationProblemImpl(0, severity ,msg);
        result.addProblem(problem);
        context.addResult(result);
    }

}
