/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.teiid.designer.validator.IValidator.IValidatorFailure;
import org.teiid.designer.validator.IValidator.IValidatorReport;
import org.teiid.query.validator.ValidatorFailure;
import org.teiid.query.validator.ValidatorReport;

/**
 *
 */
public class ValidatorReportImpl implements IValidatorReport {

    private ValidatorReport validateReport;

    /**
     * @param validateReport
     */
    public ValidatorReportImpl(ValidatorReport validateReport) {
        this.validateReport = validateReport;
    }

    @Override
    public boolean hasItems() {
        return validateReport.hasItems();
    }

    @Override
    public Collection<IValidatorFailure> getItems() {
        Collection<ValidatorFailure> items = validateReport.getItems();
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<IValidatorFailure> failures = new ArrayList<IValidatorFailure>();
        for (ValidatorFailure failure : items) {
            failures.add(new ValidatorFailureImpl(failure));
        }
        
        return failures;
    }
    
}
