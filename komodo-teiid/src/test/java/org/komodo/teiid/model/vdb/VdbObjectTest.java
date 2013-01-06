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
 * Test class for the {@link VdbObject} class.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class VdbObjectTest {

    private static class VdbObjectImpl extends VdbObject {
    }

    private final Listener listener = new Listener();
    private VdbObject vdbObject;

    @Before
    public void beforeEach() {
        this.vdbObject = new VdbObjectImpl();
        this.listener.clear();
    }

    @Test
    public void idShouldBeNullAfterConstruction() {
        assertThat(this.vdbObject.getId(), is(nullValue()));
    }

    @Test
    public void instancesWithDifferentIdsShouldNotBeEqual() {
        final VdbObject thatVdbObject = new VdbObjectImpl();

        thatVdbObject.setId(StringUtil.EMPTY_STRING);
        assertThat(this.vdbObject.equals(thatVdbObject), is(false));

        this.vdbObject.setId("newId");
        assertThat(this.vdbObject.equals(thatVdbObject), is(false));

        thatVdbObject.setId("differentId");
        assertThat(this.vdbObject.equals(thatVdbObject), is(false));
    }

    @Test
    public void instancesWithDifferentIdsShouldNotHaveSameHashCode() {
        final VdbObject thatVdbObject = new VdbObjectImpl();

        this.vdbObject.setId("newId");
        assertThat(this.vdbObject.hashCode(), is(not(thatVdbObject.hashCode())));

        thatVdbObject.setId("differentId");
        assertThat(this.vdbObject.hashCode(), is(not(thatVdbObject.hashCode())));
    }

    @Test
    public void instancesWithSameIdShouldBeEqual() {
        final VdbObject thatVdbObject = new VdbObjectImpl();
        assertThat(this.vdbObject.equals(thatVdbObject), is(true));

        {
            final String id = StringUtil.EMPTY_STRING;
            thatVdbObject.setId(id);
            this.vdbObject.setId(id);
            assertThat(this.vdbObject.equals(thatVdbObject), is(true));
        }

        {
            final String newId = "newId";
            thatVdbObject.setId(newId);
            this.vdbObject.setId(newId);
            assertThat(this.vdbObject.equals(thatVdbObject), is(true));
        }
    }

    @Test
    public void instancesWithSameIdShouldHaveSameHashCode() {
        final VdbObject thatVdbObject = new VdbObjectImpl();
        assertThat(this.vdbObject.hashCode(), is(thatVdbObject.hashCode()));

        {
            final String id = StringUtil.EMPTY_STRING;
            thatVdbObject.setId(id);
            this.vdbObject.setId(id);
            assertThat(this.vdbObject.hashCode(), is(thatVdbObject.hashCode()));
        }

        {
            final String newId = "newId";
            thatVdbObject.setId(newId);
            this.vdbObject.setId(newId);
            assertThat(this.vdbObject.hashCode(), is(thatVdbObject.hashCode()));
        }
    }

    @Test
    public void shouldAddListener() {
        assertThat(this.vdbObject.addListener(this.listener), is(true));
    }

    @Test
    public void shouldNotAddListenerMultipleTimes() {
        this.vdbObject.addListener(this.listener);
        assertThat(this.vdbObject.addListener(this.listener), is(false));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowAddingNullListener() {
        this.vdbObject.addListener(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemovingNullListener() {
        this.vdbObject.removeListener(null);
    }

    @Test
    public void shouldNotReceiveEventAfterUnregistering() {
        this.vdbObject.addListener(this.listener);
        this.vdbObject.removeListener(this.listener);

        this.vdbObject.setId("newId");
        assertThat(this.listener.getCount(), is(0));
    }

    @Test
    public void shouldNotRemoveUnregisteredListener() {
        assertThat(this.vdbObject.removeListener(this.listener), is(false));
    }

    @Test
    public void shouldReceiveCorrectEventInformationWhenIdIsChanged() {
        this.vdbObject.addListener(this.listener);

        final String id = "newId";
        this.vdbObject.setId(id);

        assertThat(this.listener.getEvent(), is(notNullValue()));
        assertThat((String)this.listener.getNewValue(), is(id));
        assertThat(this.listener.getOldValue(), is(nullValue()));
        assertThat(this.listener.getPropertyName(), is(VdbObject.PropertyName.ID));
    }

    @Test
    public void shouldReceiveEventAfterChangingId() {
        this.vdbObject.addListener(this.listener);

        final String id = "newId";
        this.vdbObject.setId(id);

        assertThat(this.listener.getCount(), is(1));
    }

    @Test
    public void shouldRemoveListener() {
        this.vdbObject.addListener(this.listener);
        assertThat(this.vdbObject.removeListener(this.listener), is(true));
    }

    @Test
    public void shouldSetId() {
        {
            final String id = StringUtil.EMPTY_STRING;
            this.vdbObject.setId(id);
            assertThat(this.vdbObject.getId(), is(id));
        }

        {
            final String newId = "newId";
            this.vdbObject.setId(newId);
            assertThat(this.vdbObject.getId(), is(newId));
        }

        {
            this.vdbObject.setId(null);
            assertThat(this.vdbObject.getId(), is(nullValue()));
        }
    }

}
