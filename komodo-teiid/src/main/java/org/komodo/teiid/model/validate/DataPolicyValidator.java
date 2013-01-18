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
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.Permission;

/**
 * A validator for a Teiid {@link DataPolicy data policy}.
 */
class DataPolicyValidator implements Validator {

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        Precondition.instanceOf(modelObject, "modelObject", DataPolicy.class); //$NON-NLS-1$

        final DataPolicy dataPolicy = (DataPolicy)modelObject;
        final List<Status> errors = new ArrayList<Status>(3);

        // make sure name is not empty
        if (StringUtil.isEmpty(dataPolicy.getId())) {
            final Status error = Error.EMPTY_DATA_POLICY_NAME.createStatus();
            error.addContext(dataPolicy);
            errors.add(error);
        } else {
            // make sure name is valid
            // TODO implement schema name validation
        }

        // make sure there at least one permission
        if (dataPolicy.getPermissions().isEmpty()) {
            final Status error = Error.NO_DATA_POLICY_PERMISSIONS.createStatus();
            error.addContext(dataPolicy);
            errors.add(error);
        } else {
            // validate each permission
            for (final Permission permission : dataPolicy.getPermissions()) {
                for (final Status permissionError : Validators.SHARED.validate(permission)) {
                    permissionError.addContext(dataPolicy);
                    errors.add(permissionError);
                }
            }
        }

        // make sure role names are valid
        // TODO validate data policy role name
        //        for (final String roleName : dataPolicy.getRoleNames()) {
        //        }

        return errors;
    }

}
