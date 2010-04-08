/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation.rules;

import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * ForeignKeyColumnsRule
 */
public class ForeignKeyColumnsRule implements ObjectValidationRule {

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(ForeignKey.class, eObject);

        ForeignKey foreignKey = (ForeignKey) eObject;

        // get the unique key reference
        UniqueKey uniqueKey = foreignKey.getUniqueKey();
        // unique key reference not set, nothing to validate
        // there is already a validatio error
        if(uniqueKey == null) {
            return;    
        }        

        // get the columns from the forign key and the unique key it references
        // compare the number of columns on each
        EList fkColumns = foreignKey.getColumns();
        EList ukColumns = uniqueKey.getColumns();
        
        int numFkColumns = fkColumns.size();
        int numUkColumns = ukColumns.size();
        // column references not set on fk or uk, nothing
        // to validate there are validation errors already
        if(numFkColumns == 0 || numUkColumns == 0) {
            return;
        } else if(numFkColumns != numUkColumns) {
            ValidationResult result = new ValidationResultImpl(eObject);
            // create validation problem and add it to the result
            final String msg = RelationalPlugin.Util.getString("ForeignKeyColumnsRule.The_number_of_columns_on_the_foreignkey__1")+foreignKey.getName() //$NON-NLS-1$
                                +RelationalPlugin.Util.getString("ForeignKeyColumnsRule._do_not_match_the_number_of_columns_on_the_referenced_uniquekey__2")+uniqueKey.getName(); //$NON-NLS-1$
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
            result.addProblem(problem);
            context.addResult(result);
            return;
        }
        
        
        // if the column count is the same on fks and uks,
        // compare the datatypes on the columns
        Iterator fkColIter = fkColumns.iterator();
        Iterator ukColIter = ukColumns.iterator();
        
        while(fkColIter.hasNext() && ukColIter.hasNext()) {
             Column fkColumn = (Column) fkColIter.next();
             Column ukColumn = (Column) ukColIter.next();
             
             EObject fkType = fkColumn.getType();
             EObject ukType = ukColumn.getType();
             // one of the columns does not have a datatype set
             // so there is already a problem, no need to validate furthur
             if(fkType == null || ukType == null) {
                return;    
             } else if(!fkType.equals(ukType)) {
                 ValidationResult result = new ValidationResultImpl(eObject);
                 // create validation problem and add it to the result
                 final String msg = RelationalPlugin.Util.getString("ForeignKeyColumnsRule.The_datatype_of_the_column__3")+fkColumn.getName()+RelationalPlugin.Util.getString("ForeignKeyColumnsRule._on_the_foreignKey__4")+foreignKey.getName() //$NON-NLS-1$ //$NON-NLS-2$
                                     +RelationalPlugin.Util.getString("ForeignKeyColumnsRule._doesn__t_match_the_that_of_the__5")+ukColumn.getName()+RelationalPlugin.Util.getString("ForeignKeyColumnsRule._on_the_referenced_uniquekey__6")+uniqueKey.getName(); //$NON-NLS-1$ //$NON-NLS-2$
                 ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.WARNING, msg);
                 result.addProblem(problem);
                 context.addResult(result);
                 return;
             }
        }
    }

}
