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
 * Test class for the {@link VdbAdminObject} class.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class VdbAdminObjectTest {

    private static class VdbAdminObjectImpl extends VdbAdminObject {
    }

    private final Listener listener = new Listener();
    private VdbAdminObject vdbAdminObject;

    @Before
    public void beforeEach() {
        this.vdbAdminObject = new VdbAdminObjectImpl();
        this.listener.clear();
    }

    @Test
    public void changingPropertyShouldNotAddNewProperty() {
        this.vdbAdminObject.setProperty("prop", "value");
        this.vdbAdminObject.setProperty("prop", "newValue");
        assertThat(this.vdbAdminObject.getProperties().size(), is(1));
    }

    @Test
    public void descriptionShouldBeNullAfterConstruction() {
        assertThat(this.vdbAdminObject.getDescription(), is(nullValue()));
    }

    @Test
    public void instancesWithDifferentDescriptionsAndSameIdShouldNotBeEqual() {
        final VdbAdminObject thatVdbAdminObject = new VdbAdminObjectImpl();

        thatVdbAdminObject.setDescription(StringUtil.EMPTY_STRING);
        assertThat(this.vdbAdminObject.equals(thatVdbAdminObject), is(false));

        this.vdbAdminObject.setDescription("newDescription");
        assertThat(this.vdbAdminObject.equals(thatVdbAdminObject), is(false));

        thatVdbAdminObject.setDescription("differentDescription");
        assertThat(this.vdbAdminObject.equals(thatVdbAdminObject), is(false));
    }

    @Test
    public void instancesWithDifferentDescriptionsAndSameIdShouldNotHaveSameHashCode() {
        final VdbAdminObject thatVdbAdminObject = new VdbAdminObjectImpl();

        this.vdbAdminObject.setDescription("newDescription");
        assertThat(this.vdbAdminObject.hashCode(), is(not(thatVdbAdminObject.hashCode())));

        thatVdbAdminObject.setDescription("differentDescription");
        assertThat(this.vdbAdminObject.hashCode(), is(not(thatVdbAdminObject.hashCode())));
    }

    @Test
    public void instancesWithDifferentPropertiesShouldHaveDifferentHashCodes() {
        final VdbAdminObject thatVdbAdminObject = new VdbAdminObjectImpl();

        this.vdbAdminObject.setProperty("prop", "value");
        assertThat(this.vdbAdminObject.hashCode(), is(not(thatVdbAdminObject.hashCode())));

        thatVdbAdminObject.setProperty("differentProp", "differentValue");
        assertThat(this.vdbAdminObject.hashCode(), is(not(thatVdbAdminObject.hashCode())));
    }

    @Test
    public void instancesWithDifferentPropertiesShouldNotBeEqual() {
        final VdbAdminObject thatVdbAdminObject = new VdbAdminObjectImpl();

        this.vdbAdminObject.setProperty("prop", "value");
        assertThat(this.vdbAdminObject.equals(thatVdbAdminObject), is(false));

        thatVdbAdminObject.setProperty("differentProp", "differentValue");
        assertThat(this.vdbAdminObject.equals(thatVdbAdminObject), is(false));
    }

    @Test
    public void instancesWithSameDescriptionAndIdShouldBeEqual() {
        final VdbAdminObject thatVdbAdminObject = new VdbAdminObjectImpl();

        {
            final String description = StringUtil.EMPTY_STRING;
            thatVdbAdminObject.setDescription(description);
            this.vdbAdminObject.setDescription(description);
            assertThat(this.vdbAdminObject.equals(thatVdbAdminObject), is(true));
        }

        {
            final String newDescription = "newDescription";
            thatVdbAdminObject.setDescription(newDescription);
            this.vdbAdminObject.setDescription(newDescription);
            assertThat(this.vdbAdminObject.equals(thatVdbAdminObject), is(true));
        }
    }

    @Test
    public void instancesWithSameDescriptionAndIdShouldHaveSameHashCode() {
        final VdbAdminObject thatVdbAdminObject = new VdbAdminObjectImpl();

        {
            final String description = StringUtil.EMPTY_STRING;
            thatVdbAdminObject.setDescription(description);
            this.vdbAdminObject.setDescription(description);
            assertThat(this.vdbAdminObject.hashCode(), is(thatVdbAdminObject.hashCode()));
        }

        {
            final String newDescription = "newDescription";
            thatVdbAdminObject.setDescription(newDescription);
            this.vdbAdminObject.setDescription(newDescription);
            assertThat(this.vdbAdminObject.hashCode(), is(thatVdbAdminObject.hashCode()));
        }
    }

    @Test
    public void instancesWithSamePropertiesShouldBeEqual() {
        final VdbAdminObject thatVdbAdminObject = new VdbAdminObjectImpl();
        final String prop = "prop";
        final String value = "value";

        this.vdbAdminObject.setProperty(prop, value);
        thatVdbAdminObject.setProperty(prop, value);

        assertThat(this.vdbAdminObject.equals(thatVdbAdminObject), is(true));
    }

    @Test
    public void instancesWithSamePropertiesShouldHaveSameHashCode() {
        final VdbAdminObject thatVdbAdminObject = new VdbAdminObjectImpl();
        final String prop = "prop";
        final String value = "value";

        this.vdbAdminObject.setProperty(prop, value);
        thatVdbAdminObject.setProperty(prop, value);

        assertThat(this.vdbAdminObject.hashCode(), is(thatVdbAdminObject.hashCode()));
    }

    @Test
    public void shouldAddProperty() {
        this.vdbAdminObject.setProperty("prop", "value");
        assertThat(this.vdbAdminObject.getProperties().size(), is(1));
    }

    @Test
    public void shouldAllowEmptyPropertyValue() {
        final String prop = "prop";
        final String value = StringUtil.EMPTY_STRING;
        this.vdbAdminObject.setProperty(prop, value);
        assertThat(this.vdbAdminObject.getProperty(prop), is(value));
    }

    @Test
    public void shouldAllowNullPropertyValue() {
        final String prop = "prop";
        final String value = null;
        this.vdbAdminObject.setProperty(prop, value);
        assertThat(this.vdbAdminObject.getProperty(prop), is(value));
    }

    @Test
    public void shouldHaveEmptyPropertiesAfterConstruction() {
        assertThat(this.vdbAdminObject.getProperties(), is(notNullValue()));
        assertThat(this.vdbAdminObject.getProperties().size(), is(0));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowModifyingAnEmptyProperty() {
        this.vdbAdminObject.setProperty(StringUtil.EMPTY_STRING, "value");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemovingAnEmptyProperty() {
        this.vdbAdminObject.removeProperty(StringUtil.EMPTY_STRING);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemovingANullProperty() {
        this.vdbAdminObject.removeProperty(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRequestingAnEmptyProperty() {
        this.vdbAdminObject.getProperty(StringUtil.EMPTY_STRING);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRequestingANullProperty() {
        this.vdbAdminObject.getProperty(null);
    }

    @Test
    public void shouldReceiveCorrectEventInformationWhenDescriptionIsChanged() {
        this.vdbAdminObject.addListener(this.listener);

        final String description = "newDescription";
        this.vdbAdminObject.setDescription(description);

        assertThat(this.listener.getEvent(), is(notNullValue()));
        assertThat((String)this.listener.getNewValue(), is(description));
        assertThat(this.listener.getOldValue(), is(nullValue()));
        assertThat(this.listener.getPropertyName(), is(VdbAdminObject.PropertyName.DESCRIPTION));
    }

    @Test
    public void shouldReceiveCorrectEventInformationWhenPropertyIsChanged() {
        this.vdbAdminObject.addListener(this.listener);

        final String prop = "prop";
        final String value = "value";
        this.vdbAdminObject.setProperty(prop, value);

        assertThat(this.listener.getEvent(), is(notNullValue()));
        assertThat((String)this.listener.getNewValue(), is(value));
        assertThat(this.listener.getOldValue(), is(nullValue()));
        assertThat(this.listener.getPropertyName(), is(prop));
    }

    @Test
    public void shouldReceiveCorrectEventInformationWhenPropertyIsRemoved() {
        final String prop = "prop";
        final String value = "value";
        this.vdbAdminObject.setProperty(prop, value);
        this.vdbAdminObject.addListener(this.listener);
        this.vdbAdminObject.removeProperty(prop);

        assertThat(this.listener.getEvent(), is(notNullValue()));
        assertThat(this.listener.getNewValue(), is(nullValue()));
        assertThat((String)this.listener.getOldValue(), is(value));
        assertThat(this.listener.getPropertyName(), is(prop));
    }

    @Test
    public void shouldReceiveEventAfterChangingDescription() {
        this.vdbAdminObject.addListener(this.listener);

        final String description = "newDescription";
        this.vdbAdminObject.setDescription(description);

        assertThat(this.listener.getCount(), is(1));
    }

    @Test
    public void shouldReceiveEventAfterChangingProperty() {
        this.vdbAdminObject.addListener(this.listener);

        this.vdbAdminObject.setProperty("prop", "value");
        assertThat(this.listener.getCount(), is(1));
    }

    @Test
    public void shouldReceiveEventAfterRemovingProperty() {
        final String prop = "prop";
        this.vdbAdminObject.setProperty(prop, "value");
        this.vdbAdminObject.addListener(this.listener);
        this.vdbAdminObject.removeProperty(prop);
        assertThat(this.listener.getCount(), is(1));
    }

    @Test
    public void shouldReceiveNullValueIfPropertyDoesNotExist() {
        assertThat(this.vdbAdminObject.getProperty("unknownProperty"), is(nullValue()));
    }

    @Test
    public void shouldRemoveProperty() {
        final String prop = "prop";
        this.vdbAdminObject.setProperty(prop, "value");
        this.vdbAdminObject.removeProperty(prop);
        assertThat(this.vdbAdminObject.getProperties().size(), is(0));
    }

    @Test
    public void shouldSetDescription() {
        {
            final String description = StringUtil.EMPTY_STRING;
            this.vdbAdminObject.setDescription(description);
            assertThat(this.vdbAdminObject.getDescription(), is(description));
        }

        {
            final String newDescription = "newDescription";
            this.vdbAdminObject.setDescription(newDescription);
            assertThat(this.vdbAdminObject.getDescription(), is(newDescription));
        }

        {
            this.vdbAdminObject.setDescription(null);
            assertThat(this.vdbAdminObject.getDescription(), is(nullValue()));
        }
    }

    @Test
    public void shouldSetPropertyValue() {
        final String prop = "prop";

        {
            final String value = "value";
            this.vdbAdminObject.setProperty(prop, value);
            assertThat(this.vdbAdminObject.getProperty(prop), is(value));
        }

        {
            final String newValue = "newValue";
            this.vdbAdminObject.setProperty(prop, newValue);
            assertThat(this.vdbAdminObject.getProperty(prop), is(newValue));
        }
    }

}
