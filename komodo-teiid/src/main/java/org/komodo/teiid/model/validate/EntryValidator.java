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
import java.util.Map;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.common.validate.Status;
import org.komodo.teiid.model.ModelObject;
import org.komodo.teiid.model.vdb.Entry;

/**
 * A validator for a Teiid {@link Entry entry}.
 */
class EntryValidator implements Validator {

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        Precondition.instanceOf(modelObject, "modelObject", Entry.class); //$NON-NLS-1$

        final Entry entry = (Entry)modelObject;
        final List<Status> errors = new ArrayList<Status>(3);

        // make sure path is not empty
        if (StringUtil.isEmpty(entry.getPath())) {
            final Status error = Error.EMPTY_ENTRY_PATH.createStatus();
            error.addContext(entry);
            errors.add(error);
        }

        // validate properties
        for (final Map.Entry<String, String> prop : entry.getProperties().entrySet()) {
            if (StringUtil.isEmpty(prop.getKey())) {
                final Status error = Error.EMPTY_ENTRY_PROPERTY_NAME.createStatus();
                error.addContext(entry);
                errors.add(error);
            } else {
                // make sure entry property name is valid
                // TODO implement entry property name validation
            }

            if (StringUtil.isEmpty(prop.getKey())) {
                final Status error = Error.EMPTY_ENTRY_PROPERTY_VALUE.createStatus();
                error.addContext(entry);
                errors.add(error);
            } else {
                // make sure entry property value is valid
                // TODO implement entry property value validation
            }
        }

        return errors;
    }

}
