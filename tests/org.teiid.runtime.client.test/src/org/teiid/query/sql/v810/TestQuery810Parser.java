/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v810;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.ParseInfo;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.v89.TestQuery89Parser;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class TestQuery810Parser extends TestQuery89Parser {

    protected TestQuery810Parser(Version teiidVersion) {
        super(teiidVersion);
    }

    public TestQuery810Parser() {
        this(Version.TEIID_8_10);
    }

    @Test
    public void testLineComment() {
        String sql = "select 1 -- some comment";
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(getFactory().newConstant(1))));
        helpTest(sql, "SELECT 1", query);
    }

    @Test
    public void testTrimExpression() throws Exception {
        String sql = "select trim(substring(Description, pos1+1))";
        Query actualCommand = (Query) parser.parseCommand(sql, new ParseInfo());
        assertEquals("SELECT trim(' ' FROM substring(Description, (pos1 + 1)))", actualCommand.toString());
    }
}
