/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.teiid.adminapi.PropertyDefinition;

/**
 * 
 */
public class ConnectorTypeTest {

    @Mock
    private ExecutionAdmin admin;

    private Collection propDefs;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        propDefs = new ArrayList<PropertyDefinition>();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullName() {
        new ConnectorType(null, null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullPropertyDefinitions() {
        new ConnectorType("", null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullAdmin() {
        new ConnectorType("", propDefs, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCompareToWithNullType() {
        new ConnectorType("name", propDefs, admin).compareTo(null);
    }

    @Test
    public void shouldAllowCompareTo() {
        ConnectorType type = mock(ConnectorType.class);
        when(type.getName()).thenReturn("name_2");
        new ConnectorType("name", propDefs, admin).compareTo(type);
    }

    @Test
    public void shouldAllowGetAdmin() {
        new ConnectorType("name", propDefs, admin).getAdmin();
    }

    @Test
    public void shouldAllowGetName() {
        new ConnectorType("name", propDefs, admin).getName();
    }

    @Test
    public void shouldAllowGetPropertyDefinitions() {
        new ConnectorType("name", propDefs, admin).getPropertyDefinitions();
    }

    @Test
    public void shouldAllowGetPropertyDefinition() {
        new ConnectorType("name", propDefs, admin).getPropertyDefinition("propName");
    }
}
