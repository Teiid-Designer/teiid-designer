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
import org.komodo.teiid.model.vdb.Permission;

/**
 * A validator for a Teiid {@link Permission data permission}.
 */
class PermissionValidator implements Validator {

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        Precondition.instanceOf(modelObject, "modelObject", Permission.class); //$NON-NLS-1$

        final Permission permission = (Permission)modelObject;
        final List<Status> errors = new ArrayList<Status>(3);

        // make sure resource name is not empty
        if (StringUtil.isEmpty(permission.getId())) {
            final Status error = Error.EMPTY_PERMISSION_RESOURCE_NAME.createStatus();
            error.addContext(permission);
            errors.add(error);
        } else {
            // make sure resource name is valid
            // TODO implement source name validation
        }

        return errors;
    }

}
