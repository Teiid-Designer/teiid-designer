/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.validate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.komodo.common.util.Precondition;
import org.komodo.common.validate.Status;
import org.komodo.teiid.model.ModelObject;
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.Entry;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Permission;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Source;
import org.komodo.teiid.model.vdb.Translator;
import org.komodo.teiid.model.vdb.Vdb;

/**
 * A cached set of validators for model objects. Also can validate model objects itself.
 */
public class Validators implements Validator {

    private final ConcurrentMap<String, Validator> validators = new ConcurrentHashMap<String, Validator>();

    /**
     * @param modelObject the model object whose validator is being requested (cannot be <code>null</code>)
     * @return the validator (never <code>null</code>)
     */
    public Validator get(final ModelObject modelObject) {
        Precondition.notNull(modelObject, "modelObject"); //$NON-NLS-1$
        Validator validator = this.validators.get(modelObject.getClass().getName());

        if (validator == null) {
            if (DataPolicy.class.isInstance(modelObject)) {
                validator = new DataPolicyValidator();
            } else if (Entry.class.isInstance(modelObject)) {
                validator = new EntryValidator();
            } else if (ImportVdb.class.isInstance(modelObject)) {
                validator = new ImportVdbValidator();
            } else if (Permission.class.isInstance(modelObject)) {
                validator = new PermissionValidator();
            } else if (Schema.class.isInstance(modelObject)) {
                validator = new SchemaValidator();
            } else if (Source.class.isInstance(modelObject)) {
                validator = new SourceValidator();
            } else if (Translator.class.isInstance(modelObject)) {
                validator = new TranslatorValidator();
            } else if (Vdb.class.isInstance(modelObject)) {
                validator = new VdbValidator();
            }

            assert (validator != null) : "validator is null"; //$NON-NLS-1$
            this.validators.putIfAbsent(modelObject.getClass().getName(), validator);
        }

        return validator;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.validate.Validator#validate(org.komodo.teiid.model.ModelObject)
     */
    @Override
    public List<Status> validate(final ModelObject modelObject) throws IllegalArgumentException {
        final Validator validator = get(modelObject);
        return validator.validate(modelObject);
    }

}
