/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.validate;

import java.util.List;
import org.komodo.common.validate.Status;
import org.komodo.teiid.model.ModelObject;

/**
 * A Teiid model object validator.
 */
public interface Validator extends Errors {

    /**
     * @param modelObject the model object whose state is being validated (cannot be <code>null</code>)
     * @return a collection of validation statuses (never <code>null</code> but can be empty)
     * @throws IllegalArgumentException if the model object is of the wrong type for the validator
     */
    List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException;

}
