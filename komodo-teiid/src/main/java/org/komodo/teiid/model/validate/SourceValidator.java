/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.validate;

import java.util.ArrayList;
import java.util.List;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.common.validate.Status;
import org.komodo.teiid.model.ModelObject;
import org.komodo.teiid.model.vdb.Source;

/**
 * A validator for a Teiid {@link Source source}.
 */
class SourceValidator implements Validator {

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        Precondition.instanceOf(modelObject, "modelObject", Source.class); //$NON-NLS-1$

        final Source source = (Source)modelObject;
        final List<Status> errors = new ArrayList<Status>(3);

        // make sure name is not empty
        if (StringUtil.isEmpty(source.getId())) {
            final Status error = Error.EMPTY_SOURCE_NAME.createStatus();
            error.addContext(source);
            errors.add(error);
        } else {
            // make sure name is valid
            // TODO implement source name validation
        }

        // make sure translator name is not empty
        if (StringUtil.isEmpty(source.getTranslatorName())) {
            final Status error = Error.EMPTY_SOURCE_TRANSLATOR_NAME.createStatus();
            error.addContext(source);
            errors.add(error);
        } else {
            // make sure translator name is valid
            // TODO implement source translator name validation
        }

        return errors;
    }
}
