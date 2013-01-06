/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.komodo.common.util.StringUtil;
import org.komodo.common.validate.Status;
import org.komodo.teiid.model.validate.TranslatorValidator;

/**
 * Test class for the {@link TranslatorValidator} class.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class TranslatorValidatorTest {

    private static final String PROP = "prop";

    private Translator translator;

    @Before
    public void beforeEach() {
        this.translator = new Translator();
        this.translator.setId("newId");
        this.translator.setType("newType");
        this.translator.setProperty(PROP, "value");
    }

    @Test
    public void nullIdShouldBeAnError() {
        this.translator.setId(null);
        final List<Status> errors = TranslatorValidator.validate(this.translator);
        assertThat(errors, hasItem(TranslatorValidator.ValidationStatus.EMPTY_NAME));
    }

    @Test
    public void emptyIdShouldBeAnError() {
        this.translator.setId(StringUtil.EMPTY_STRING);
        final List<Status> errors = TranslatorValidator.validate(this.translator);
        assertThat(errors, hasItem(TranslatorValidator.ValidationStatus.EMPTY_NAME));
    }

    @Test
    public void nullTypeShouldBeAnError() {
        this.translator.setType(null);
        final List<Status> errors = TranslatorValidator.validate(this.translator);
        assertThat(errors, hasItem(TranslatorValidator.ValidationStatus.EMPTY_TYPE));
    }

    @Test
    public void emptyTypeShouldBeAnError() {
        this.translator.setType(StringUtil.EMPTY_STRING);
        final List<Status> errors = TranslatorValidator.validate(this.translator);
        assertThat(errors, hasItem(TranslatorValidator.ValidationStatus.EMPTY_TYPE));
    }

    @Test
    public void noPropertiesShouldBeAnError() {
        this.translator.removeProperty(PROP);
        final List<Status> errors = TranslatorValidator.validate(this.translator);
        assertThat(errors, hasItem(TranslatorValidator.ValidationStatus.NO_PROPERTIES));
    }

    @Test
    public void validTranslatorShouldNotHaveErrors() {
        final List<Status> errors = TranslatorValidator.validate(this.translator);
        assertThat(errors.size(), is(0));
    }

    @Test
    public void shouldHaveMultipleErrorsAfterConstruction() {
        final Translator newTranslator = new Translator();
        final List<Status> errors = TranslatorValidator.validate(newTranslator);
        assertThat(errors.size(), is(3));
        assertThat(errors, hasItems(new Status[] {TranslatorValidator.ValidationStatus.EMPTY_NAME,
            TranslatorValidator.ValidationStatus.EMPTY_TYPE, TranslatorValidator.ValidationStatus.NO_PROPERTIES}));
    }

}
