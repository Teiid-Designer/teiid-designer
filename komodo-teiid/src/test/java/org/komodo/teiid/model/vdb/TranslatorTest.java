/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.komodo.common.Listener;
import org.komodo.common.util.StringUtil;

/**
 * Test class for the {@link Translator} class.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class TranslatorTest {

    private Translator translator;

    @Before
    public void beforeEach() {
        this.translator = new Translator();
    }

    @Test
    public void instancesWithDifferentTypesShouldNotBeEqual() {
        final Translator thatTranslator = new Translator();

        thatTranslator.setType(StringUtil.EMPTY_STRING);
        assertThat(this.translator.equals(thatTranslator), is(false));

        this.translator.setType("newType");
        assertThat(this.translator.equals(thatTranslator), is(false));

        thatTranslator.setType("differentType");
        assertThat(this.translator.equals(thatTranslator), is(false));
    }

    @Test
    public void instancesWithDifferentTypesShouldNotHaveSameHashCode() {
        final Translator thatTranslator = new Translator();

        this.translator.setType("newType");
        assertThat(this.translator.hashCode(), is(not(thatTranslator.hashCode())));

        thatTranslator.setType("differentType");
        assertThat(this.translator.hashCode(), is(not(thatTranslator.hashCode())));
    }

    @Test
    public void instancesWithSameTypeShouldBeEqual() {
        final Translator thatTranslator = new Translator();
        assertThat(this.translator.equals(thatTranslator), is(true));

        {
            final String type = StringUtil.EMPTY_STRING;
            thatTranslator.setType(type);
            this.translator.setType(type);
            assertThat(this.translator.equals(thatTranslator), is(true));
        }

        {
            final String newType = "newType";
            thatTranslator.setType(newType);
            this.translator.setType(newType);
            assertThat(this.translator.equals(thatTranslator), is(true));
        }
    }

    @Test
    public void instancesWithSameTypeShouldHaveSameHashCode() {
        final Translator thatTranslator = new Translator();
        assertThat(this.translator.hashCode(), is(thatTranslator.hashCode()));

        {
            final String type = StringUtil.EMPTY_STRING;
            thatTranslator.setType(type);
            this.translator.setType(type);
            assertThat(this.translator.hashCode(), is(thatTranslator.hashCode()));
        }

        {
            final String newType = "newType";
            thatTranslator.setType(newType);
            this.translator.setType(newType);
            assertThat(this.translator.hashCode(), is(thatTranslator.hashCode()));
        }
    }

    @Test
    public void shouldReceiveCorrectEventInformationWhenTypeIsChanged() {
        final Listener listener = new Listener();
        this.translator.addListener(listener);

        final String type = "newType";
        this.translator.setType(type);

        assertThat(listener.getEvent(), is(notNullValue()));
        assertThat((String)listener.getNewValue(), is(type));
        assertThat(listener.getOldValue(), is(nullValue()));
        assertThat(listener.getPropertyName(), is(Translator.PropertyName.TYPE));
    }

    @Test
    public void shouldReceiveEventAfterChangingType() {
        final Listener listener = new Listener();
        this.translator.addListener(listener);

        final String type = "newType";
        this.translator.setType(type);

        assertThat(listener.getCount(), is(1));
    }

    @Test
    public void shouldSetType() {
        {
            final String type = StringUtil.EMPTY_STRING;
            this.translator.setType(type);
            assertThat(this.translator.getTranslatorType(), is(type));
        }

        {
            final String newType = "newType";
            this.translator.setType(newType);
            assertThat(this.translator.getTranslatorType(), is(newType));
        }

        {
            this.translator.setType(null);
            assertThat(this.translator.getTranslatorType(), is(nullValue()));
        }
    }

    @Test
    public void typeShouldBeNullAfterConstruction() {
        assertThat(this.translator.getTranslatorType(), is(nullValue()));
    }

}
