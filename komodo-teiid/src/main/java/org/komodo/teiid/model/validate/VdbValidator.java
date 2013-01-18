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
import org.komodo.teiid.model.vdb.Entry;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Translator;
import org.komodo.teiid.model.vdb.Vdb;

/**
 * A validator for a Teiid {@link Vdb virtual database}.
 */
class VdbValidator implements Validator {

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        Precondition.instanceOf(modelObject, "modelObject", Vdb.class); //$NON-NLS-1$

        final Vdb vdb = (Vdb)modelObject;
        final List<Status> errors = new ArrayList<Status>(3);

        // make sure name is not empty
        if (StringUtil.isEmpty(vdb.getId())) {
            final Status error = Error.EMPTY_VDB_NAME.createStatus();
            error.addContext(vdb);
            errors.add(error);
        } else {
            // make sure name is valid
            // TODO implement import VDB name validation
        }

        // make sure version is valid
        final int version = vdb.getVersion();

        if (version < 0) {
            final Status error = Error.INVALID_VDB_VERSION.createStatus();
            error.addContext(vdb);
            errors.add(error);
        }

        // validate data policies
        for (final DataPolicy dataPolicy : vdb.getDataPolicies()) {
            for (final Status dataPolicyError : Validators.SHARED.validate(dataPolicy)) {
                dataPolicyError.addContext(vdb);
                errors.add(dataPolicyError);
            }
        }

        // validate entries
        for (final Entry entry : vdb.getEntries()) {
            for (final Status entryError : Validators.SHARED.validate(entry)) {
                entryError.addContext(vdb);
                errors.add(entryError);
            }
        }

        // validate import vdbs
        for (final ImportVdb importVdb : vdb.getImportVdbs()) {
            for (final Status importVdbError : Validators.SHARED.validate(importVdb)) {
                importVdbError.addContext(vdb);
                errors.add(importVdbError);
            }
        }

        // validate schemas
        for (final Schema schema : vdb.getSchemas()) {
            for (final Status schemaError : Validators.SHARED.validate(schema)) {
                schemaError.addContext(vdb);
                errors.add(schemaError);
            }
        }

        // validate translators
        for (final Translator translator : vdb.getTranslators()) {
            for (final Status translatorError : Validators.SHARED.validate(translator)) {
                translatorError.addContext(vdb);
                errors.add(translatorError);
            }
        }

        return errors;
    }

}
