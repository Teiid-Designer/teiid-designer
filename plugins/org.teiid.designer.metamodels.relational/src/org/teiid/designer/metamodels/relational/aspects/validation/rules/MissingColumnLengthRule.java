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
 * MissingColumnLengthRule
 *
 * @since 8.0
 */
public class MissingColumnLengthRule implements ObjectValidationRule {
        
    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(Column.class, eObject);

        Column column = (Column) eObject;

        if(column.getLength() > 0) {
            return;
        }        
        
        int status = getPreferenceStatus(context);
        
        //MyDefect : 17817 added validate_ignore for ignore prefrence check.        
        if(status == IStatus.OK) {
            return ;
        }
        
        EObject type = column.getType();
        final DatatypeManager dtMgr = context.getDatatypeManager();
        final String typeName = dtMgr.getName(type);
        if( type != null && typeName != null ) {
            if( typeName.equals(DatatypeConstants.BuiltInNames.STRING) || 
                            typeName.equals(DatatypeConstants.BuiltInNames.CHAR) ) {
                ValidationResult result = new ValidationResultImpl(eObject);
                // create validation problem and add it to the result
                final Object[] params = new Object[]{};
                final String msg = RelationalPlugin.Util.getString("MissingColumnLengthRule.failure_message",params); //$NON-NLS-1$
                ValidationProblem problem  = new ValidationProblemImpl(0, status ,msg);
                problem.setHasPreference(context.hasPreferences());
                result.addProblem(problem);
                context.addResult(result);
            }
        }
    }
    
    protected int getPreferenceStatus(ValidationContext context) {
        return context.getPreferenceStatus(ValidationPreferences.RELATIONAL_MISSING_COLUMN_LENGTH, IStatus.WARNING);
    }

}
