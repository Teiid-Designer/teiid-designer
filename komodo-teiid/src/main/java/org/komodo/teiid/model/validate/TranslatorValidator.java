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
import org.komodo.teiid.model.vdb.Translator;

/**
 * A validator for a Teiid {@link Translator translator}.
 */
class TranslatorValidator implements Validator {

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        Precondition.instanceOf(modelObject, "modelObject", Translator.class); //$NON-NLS-1$

        final Translator translator = (Translator)modelObject;
        final List<Status> errors = new ArrayList<Status>(3);

        // make sure name is not empty
        if (StringUtil.isEmpty(translator.getId())) {
            final Status error = Error.EMPTY_TRANSLATOR_NAME.createStatus();
            error.addContext(translator);
            errors.add(error);
        } else {
            // make sure name is valid
            // TODO implement translator name validation
        }

        // make sure type is not empty
        if (StringUtil.isEmpty(translator.getType())) {
            final Status error = Error.EMPTY_TRANSLATOR_TYPE.createStatus();
            error.addContext(translator);
            errors.add(error);
        } else {
            // make sure translator type is valid
            // TODO implement translator type validation
        }

        // make sure properties is not empty
        if (translator.getProperties().isEmpty()) {
            final Status error = Error.NO_TRANSLATOR_PROPERTIES.createStatus();
            error.addContext(translator);
            errors.add(error);
        } else {
            // validate properties
            for (final Map.Entry<String, String> prop : translator.getProperties().entrySet()) {
                if (StringUtil.isEmpty(prop.getKey())) {
                    final Status error = Error.EMPTY_TRANSLATOR_PROPERTY_NAME.createStatus();
                    error.addContext(translator);
                    errors.add(error);
                } else {
                    // make sure translator property name is valid
                    // TODO implement translator property name validation
                }

                if (StringUtil.isEmpty(prop.getKey())) {
                    final Status error = Error.EMPTY_TRANSLATOR_PROPERTY_VALUE.createStatus();
                    error.addContext(translator);
                    errors.add(error);
                } else {
                    // make sure translator property value is valid
                    // TODO implement translator property value validation
                }
            }
        }

        return errors;
    }

}
