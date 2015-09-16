/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v85;

import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.v84.TestQuery84Parser;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class TestQuery85Parser extends TestQuery84Parser {

    protected TestQuery85Parser(Version teiidVersion) {
        super(teiidVersion);
    }

    public TestQuery85Parser() {
        this(Version.TEIID_8_5);
    }

    @Test
    public void testGroupByRollup() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g"); //$NON-NLS-1$
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a")); //$NON-NLS-1$

        GroupBy groupBy = getFactory().newGroupBy();
        groupBy.setRollup(true);
        groupBy.addSymbol(getFactory().newElementSymbol("b")); //$NON-NLS-1$
        groupBy.addSymbol(getFactory().newElementSymbol("c")); //$NON-NLS-1$

        Query query = getFactory().newQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setGroupBy(groupBy);
        helpTest("SELECT a FROM m.g GROUP BY rollup(b, c)", //$NON-NLS-1$
                 "SELECT a FROM m.g GROUP BY ROLLUP(b, c)", //$NON-NLS-1$
                 query);
    }
}
