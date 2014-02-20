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
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalPlugin;

/**
 *
 */
public class CharDatatypeLengthRule implements ObjectValidationRule {
    
    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject theObject,
                         ValidationContext theContext) {
        CoreArgCheck.isTrue( theObject instanceof Column || theObject instanceof ProcedureParameter,
        		RelationalPlugin.Util.getString("CharDatatypeLengthRule.illegalObjectError"));  //$NON-NLS-1$

        EObject dataType = null;
        int length = 0;
        
        if( theObject instanceof Column ) {
        	dataType = ((Column)theObject).getType();
	        length = ((Column)theObject).getLength();
        } else {
	        dataType = ((ProcedureParameter)theObject).getType();
	        length = ((ProcedureParameter)theObject).getLength();
        }
        
        if(dataType == null) {
            return;    
        }
        
        final DatatypeManager dtMgr = theContext.getDatatypeManager();
        boolean isBuiltInType = dtMgr.isBuiltInDatatype(dataType);
        String typeName = dtMgr.getName(dataType);
        if(!isBuiltInType || typeName == null || !typeName.equals(DatatypeConstants.BuiltInNames.CHAR)) {
            return;
        }
        
        if( length > 1 ) {
        	String message = RelationalPlugin.Util.getString("CharDatatypeLengthRule.lengthGreaterThanOneMessage"); //$NON-NLS-1$
            ValidationResult result = new ValidationResultImpl(theObject);
            result.addProblem(new ValidationProblemImpl(IStatus.OK, IStatus.WARNING, message));
            theContext.addResult(result);
        }
    }
}
