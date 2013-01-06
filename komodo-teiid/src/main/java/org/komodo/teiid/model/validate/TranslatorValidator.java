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
import org.komodo.common.validate.Status.Severity;
import org.komodo.teiid.model.vdb.Translator;

/**
 * A validator for a Teiid translator.
 */
public class TranslatorValidator {

    /**
     * The translator validation errors.
     */
    public interface ValidationStatus {

        /**
         * Indicates the name is <code>null</code> or empty.
         */
        Status EMPTY_NAME = new Status(Severity.ERROR, 1000);

        /**
         * Indicates the type is <code>null</code> or empty.
         */
        Status EMPTY_TYPE = new Status(Severity.ERROR, 1010);

        /**
         * Indicates the translator has no properties.
         */
        Status NO_PROPERTIES = new Status(Severity.ERROR, 1020);
    }

    /**
     * @param translator the translator whose state is being validated (cannot be <code>null</code>)
     * @return a collection of validation statuses (never <code>null</code> but can be empty)
     */
    public static List<Status> validate(final Translator translator) {
        Precondition.notNull(translator, "translator"); //$NON-NLS-1$

        final List<Status> errors = new ArrayList<Status>(3);

        // make sure name is not empty
        if (StringUtil.isEmpty(translator.getId())) {
            errors.add(ValidationStatus.EMPTY_NAME);
        }

        // make sure type is not empty
        if (StringUtil.isEmpty(translator.getType())) {
            errors.add(ValidationStatus.EMPTY_TYPE);
        }

        // make sure properties is not empty
        if (translator.getProperties().isEmpty()) {
            errors.add(ValidationStatus.NO_PROPERTIES);
        }

        return errors;
    }

    /**
     * Don't allow public construction.
     */
    private TranslatorValidator() {
        // nothing to do
    }

}
