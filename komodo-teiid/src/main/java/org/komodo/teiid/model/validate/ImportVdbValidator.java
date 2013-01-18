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
import org.komodo.teiid.model.vdb.ImportVdb;

/**
 * A validator for a Teiid {@link ImportVdb VDB import}.
 */
class ImportVdbValidator implements Validator {

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        Precondition.instanceOf(modelObject, "modelObject", ImportVdb.class); //$NON-NLS-1$

        final ImportVdb importVdb = (ImportVdb)modelObject;
        final List<Status> errors = new ArrayList<Status>(3);

        // make sure name is not empty
        if (StringUtil.isEmpty(importVdb.getId())) {
            final Status error = Error.EMPTY_IMPORT_VDB_NAME.createStatus();
            error.addContext(importVdb);
            errors.add(error);
        } else {
            // make sure name is valid
            // TODO implement import VDB name validation
        }

        // make sure version is valid
        final int version = importVdb.getVersion();

        if (version < 0) {
            final Status error = Error.INVALID_IMPORT_VDB_VERSION.createStatus();
            error.addContext(importVdb);
            errors.add(error);
        }

        return errors;
    }

}
