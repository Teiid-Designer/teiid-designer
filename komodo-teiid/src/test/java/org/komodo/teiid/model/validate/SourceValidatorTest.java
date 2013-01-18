/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.validate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komodo.common.util.StringUtil;
import org.komodo.common.validate.Status;
import org.komodo.teiid.model.vdb.Source;

/**
 * Test class for the {@link SourceValidator} class.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class SourceValidatorTest {

    private static Validator _validator;
    private static final String NAME = "sourceName";

    private static final String TRANSLATOR_NAME = "translatorName";

    @BeforeClass
    public static void constructValidator() {
        _validator = new SourceValidator();
    }

    private Source source;

    @Before
    public void beforeEach() {
        this.source = new Source();
        this.source.setId(NAME);
        this.source.setTranslatorName(TRANSLATOR_NAME);
    }

    @Test
    public void emptyNameAndTranslatorNameShouldHaveTwoErrors() {
        this.source.setId(StringUtil.EMPTY_STRING);
        this.source.setTranslatorName(StringUtil.EMPTY_STRING);
        final List<Status> errors = _validator.validate(this.source);
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_NAME));
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_TRANSLATOR_NAME));
    }

    @Test
    public void emptyNameShouldBeAnError() {
        this.source.setId(StringUtil.EMPTY_STRING);
        final List<Status> errors = _validator.validate(this.source);
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_NAME));
    }

    @Test
    public void emptyTranslatorNameShouldBeAnError() {
        this.source.setTranslatorName(StringUtil.EMPTY_STRING);
        final List<Status> errors = _validator.validate(this.source);
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_TRANSLATOR_NAME));
    }

    @Test
    public void nullNameAndTranslatorNameShouldHaveTwoErrors() {
        this.source.setId(null);
        this.source.setTranslatorName(null);
        final List<Status> errors = _validator.validate(this.source);
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_NAME));
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_TRANSLATOR_NAME));
    }

    @Test
    public void nullNameShouldBeAnError() {
        this.source.setId(null);
        final List<Status> errors = _validator.validate(this.source);
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_NAME));
    }

    @Test
    public void nullTranslatorNameShouldBeAnError() {
        this.source.setTranslatorName(null);
        final List<Status> errors = _validator.validate(this.source);
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_TRANSLATOR_NAME));
    }

    @Test
    public void shouldHaveErrorsAfterConstruction() {
        final Source source = new Source();
        final List<Status> errors = _validator.validate(source);
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_NAME));
        assertThat(errors, hasItem(Errors.EMPTY_SOURCE_TRANSLATOR_NAME));
    }

    public void validTranslatorShouldNotHaveErrors() {
        final List<Status> errors = _validator.validate(this.source);
        assertThat(errors.size(), is(0));
    }

}
