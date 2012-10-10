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
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalPlugin;


/**
 * The <code>EmptyColumnNativeTypeRule</code> ensures that a column's native type is not
 * empty or only contains spaces. A <code>null</code> value is valid and indicates the default
 * native type should be used.
 * @since 8.0 
 */
public class ColumnNativeTypeRule implements ObjectValidationRule {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
//    protected static final String VALID_SPECIAL_CHARS = "()"; //$NON-NLS-1$
    
    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject theObject,
                         ValidationContext theContext) {
        CoreArgCheck.isInstanceOf(Column.class, theObject);

        // validate the native type
        String validationMsg = validate((Column)theObject);
        
        if (validationMsg != null) {
            ValidationResult result = new ValidationResultImpl(theObject);
            result.addProblem(new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, validationMsg));
            theContext.addResult(result);
        }
    }
    
    /**
     * Validates the specified column making sure the native type is not empty or blank.
     * @param theColumn the column being validated
     * @return a validation failure message or <code>null</code> if column validation was successful
     * @since 5.0.2
     */
    protected String validate(Column theColumn) {
        String result = null;
        String nativeType = theColumn.getNativeType();

        // null value is valid and indicates that the default native type should be used.
        if (nativeType != null) {
            if (nativeType.trim().length() == 0) {
                result = RelationalPlugin.Util.getString("ColumnNativeTypeRule.emptyValueMsg"); //$NON-NLS-1$
//            } else {
//                // make sure all chars are alphanumeric
//                for (int size = nativeType.length(), i = 0; i < size; ++i) {
//                    if (!isValidChar(nativeType.charAt(i))) {
//                        result = RelationalPlugin.Util.getString("ColumnNativeTypeRule.invalidCharactersMsg"); //$NON-NLS-1$
//                        break;
//                    }
//                }
            }
        }
        
        return result;
    }
    
    /**
     * Indicates if the specified character is valid for a native type. 
     * @param theChar the character being checked
     * @return <code>true</code> if valid; <code>false</code> otherwise.
     * @since 5.0.2
     */
//    private boolean isValidChar(char theChar) {
//        return StringUtil.isLetterOrDigit(theChar) || (VALID_SPECIAL_CHARS.indexOf(theChar) != -1);
//    }
//
}
