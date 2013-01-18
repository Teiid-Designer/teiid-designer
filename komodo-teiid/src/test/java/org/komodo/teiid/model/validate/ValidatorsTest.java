/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.validate;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.Entry;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Permission;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Source;
import org.komodo.teiid.model.vdb.Translator;
import org.komodo.teiid.model.vdb.Vdb;

/**
 * A test class for the Teiid model object {@link Validators validators}.
 */
@SuppressWarnings( {"javadoc"} )
public class ValidatorsTest {

    private Validators validators;

    @Before
    public void constructValidators() {
        this.validators = new Validators();
    }

    @Test
    public void shouldGetDataPolicyValidator() {
        assertThat(this.validators.get(new DataPolicy()), is(instanceOf(DataPolicyValidator.class)));
    }

    @Test
    public void shouldGetEntryValidator() {
        assertThat(this.validators.get(new Entry()), is(instanceOf(EntryValidator.class)));
    }

    @Test
    public void shouldGetImportVdbValidator() {
        assertThat(this.validators.get(new ImportVdb()), is(instanceOf(ImportVdbValidator.class)));
    }

    @Test
    public void shouldGetPermissionValidator() {
        assertThat(this.validators.get(new Permission()), is(instanceOf(PermissionValidator.class)));
    }

    @Test
    public void shouldGetSameValidatorInstance() {
        final Validator thisValidator = this.validators.get(new Vdb());
        final Validator thatValidator = this.validators.get(new Vdb());
        assertThat(thisValidator, is(sameInstance(thatValidator)));
    }

    @Test
    public void shouldGetSchemaValidator() {
        assertThat(this.validators.get(new Schema()), is(instanceOf(SchemaValidator.class)));
    }

    @Test
    public void shouldGetSourceValidator() {
        assertThat(this.validators.get(new Source()), is(instanceOf(SourceValidator.class)));
    }

    @Test
    public void shouldGetTranslatorValidator() {
        assertThat(this.validators.get(new Translator()), is(instanceOf(TranslatorValidator.class)));
    }

    @Test
    public void shouldGetVdbValidator() {
        assertThat(this.validators.get(new Vdb()), is(instanceOf(VdbValidator.class)));
    }

}
