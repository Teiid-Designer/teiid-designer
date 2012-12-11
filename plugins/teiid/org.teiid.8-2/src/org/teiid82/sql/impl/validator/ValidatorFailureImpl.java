/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.validator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.validator.IValidator.IValidatorFailure;
import org.teiid.query.validator.ValidatorFailure;

/**
 *
 */
public class ValidatorFailureImpl implements IValidatorFailure {

    private ValidatorFailure failure;

    /**
     * @param failure
     */
    public ValidatorFailureImpl(ValidatorFailure failure) {
        this.failure = failure;
    }

    @Override
    public IStatus getStatus() {
        return new Status(
                          failure.getStatus() == ValidatorFailure.Status.ERROR ? IStatus.ERROR : IStatus.WARNING, 
                          "Validation Plugin", 0, failure.toString(), null); //$NON-NLS-1$
    }
    
}
