/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.sql.v7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.AbstractTest;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Create;

@SuppressWarnings( {"javadoc"} )
public class Test7Create extends AbstractTest<Command> {

    // ################################## FRAMEWORK ################################

    private Test7Factory factory;

    /**
     *
     */
    public Test7Create() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected Test7Factory getFactory() {
        if (factory == null)
            factory = new Test7Factory(parser);

        return factory;
    }

    // ################################## TEST HELPERS ################################	

    public Create sample1() {
        Create create = getFactory().newCreate();
        create.setTable(getFactory().newGroupSymbol("temp_table"));//$NON-NLS-1$

        List elements = new ArrayList();
        elements.add(getFactory().newElementSymbol("a")); //$NON-NLS-1$
        elements.add(getFactory().newElementSymbol("b")); //$NON-NLS-1$

        create.setElementSymbolsAsColumns(elements);
        return create;
    }

    public Create sample2() {
        Create create = getFactory().newCreate();
        create.setTable(getFactory().newGroupSymbol("temp_table2"));//$NON-NLS-1$

        List elements = new ArrayList();
        elements.add(getFactory().newElementSymbol("a")); //$NON-NLS-1$
        elements.add(getFactory().newElementSymbol("b")); //$NON-NLS-1$

        create.setElementSymbolsAsColumns(elements);
        return create;
    }

    // ################################## ACTUAL TESTS ################################

    @Test
    public void testSelfEquivalence() {
        Create c1 = sample1();
        assertEquals(c1, c1);
    }

    @Test
    public void testEquivalence() {
        Create c1 = sample1();
        Create c2 = sample1();
        assertEquals(c1, c2);
    }

    @Test
    public void testNonEquivalence() {
        Create c1 = sample1();
        Create c2 = sample2();
        assertNotEquals(c1, c2);
    }

}
