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
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Source;

/**
 * A validator for a Teiid {@link Schema schema/model}.
 */
class SchemaValidator implements Validator {

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        Precondition.instanceOf(modelObject, "modelObject", Schema.class); //$NON-NLS-1$

        final Schema schema = (Schema)modelObject;
        final List<Status> errors = new ArrayList<Status>(3);

        // make sure name is not empty
        if (StringUtil.isEmpty(schema.getId())) {
            final Status error = Error.EMPTY_SCHEMA_NAME.createStatus();
            error.addContext(schema);
            errors.add(error);
        } else {
            // make sure name is valid
            // TODO implement schema name validation
        }

        // make sure type is not empty
        if (StringUtil.isEmpty(schema.getType())) {
            final Status error = Error.EMPTY_SCHEMA_TYPE.createStatus();
            error.addContext(schema);
            errors.add(error);
        } else {
            // make sure type is valid
            final String type = schema.getType();
            boolean valid = false;

            for (final Schema.Type validType : Schema.Type.values()) {
                if (validType.name().equals(type)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                final Status error = Error.INVALID_SCHEMA_TYPE.createStatus();
                error.addContext(schema);
                errors.add(error);
            }
        }

        // make sure metadata type is set and valid
        if (!StringUtil.isEmpty(schema.getMetadata()) || !StringUtil.isEmpty(schema.getMetadataType())) {
            // make sure metadata type is not empty
            if (StringUtil.isEmpty(schema.getMetadataType())) {
                final Status error = Error.EMPTY_SCHEMA_METADATA_TYPE.createStatus();
                error.addContext(schema);
                errors.add(error);
            } else {
                // make sure metadata type is valid
                final String metadataType = schema.getMetadataType();
                boolean valid = false;

                for (final Schema.MetadataType validMetadataType : Schema.MetadataType.values()) {
                    if (validMetadataType.name().equals(metadataType)) {
                        valid = true;
                        break;
                    }
                }

                if (!valid) {
                    final Status error = Error.INVALID_SCHEMA_METADATA_TYPE.createStatus();
                    error.addContext(schema);
                    errors.add(error);
                }
            }
        }

        // sources
        for (final Source source : schema.getSources()) {
            for (final Status sourceError : Validators.SHARED.validate(source)) {
                sourceError.addContext(schema);
                errors.add(sourceError);
            }
        }

        // validate properties
        for (final Map.Entry<String, String> prop : schema.getProperties().entrySet()) {
            if (StringUtil.isEmpty(prop.getKey())) {
                final Status error = Error.EMPTY_SCHEMA_PROPERTY_NAME.createStatus();
                error.addContext(schema);
                errors.add(error);
            } else {
                // make sure schema property name is valid
                // TODO implement schema property name validation
            }

            if (StringUtil.isEmpty(prop.getKey())) {
                final Status error = Error.EMPTY_SCHEMA_PROPERTY_VALUE.createStatus();
                error.addContext(schema);
                errors.add(error);
            } else {
                // make sure schema property value is valid
                // TODO implement schema property value validation
            }
        }

        return errors;
    }

}
