/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v810;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
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
    public void testTrimExpression() throws Exception {
        String sql = "select trim(substring(Description, pos1+1))";
        Query actualCommand = (Query)parser.parseCommand(sql, new ParseInfo());
        assertEquals("SELECT trim(' ' FROM substring(Description, (pos1 + 1)))", actualCommand.toString());
    }

    @Test
    public void testTrimExpressionWithComments() throws Exception {
        String sql = "select trim(substring(Description, pos1+1)) /* Trailing Comment */";
        String expectedSql = "SELECT trim(' ' FROM substring(Description, (pos1 + 1))) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testLineCommentSimple1() {
        String sql = "select 1 -- some comment";
        String expectedSql = "SELECT 1 -- some comment";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testLineCommentsSimple2() {
        String sql = "/*+ cache(ttl:300000) */" + NEW_LINE + "-- Comment 1" + NEW_LINE + "SELECT /*+sh KEEP ALIASES */ *"
                     + NEW_LINE + "-- Comment 2" + NEW_LINE + "-- Comment 2.5" + NEW_LINE + "FROM" + NEW_LINE + "/* Comment 3 */"
                     + NEW_LINE + "g1 INNER JOIN" + NEW_LINE + "/*+ MAKEDEP */" + NEW_LINE + "g2 ON g1.a1 = g2.a2" + NEW_LINE
                     + "/* Comment 4 */";
        String expectedSql = "/*+ cache(ttl:300000) */ -- Comment 1\nSELECT /*+sh KEEP ALIASES */ * "
                             + "-- Comment 2\n-- Comment 2.5\nFROM /* Comment 3 */ g1 INNER JOIN "
                             + "/*+ MAKEDEP */ g2 ON g1.a1 = g2.a2 /* Comment 4 */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testInnerJoinWithComments() {
        String sql = "SELECT *" + NEW_LINE + "-- Comment1" + NEW_LINE + "FROM" + NEW_LINE + "-- Comment2" + NEW_LINE
                     + "g1 inner join g2" + NEW_LINE + "-- Comment3" + NEW_LINE + "on g1.a1=g2.a2" + NEW_LINE + "-- Comment4";
        String expectedSql = "SELECT * -- Comment1" + NEW_LINE + "FROM -- Comment2" + NEW_LINE + "g1 INNER JOIN g2 -- Comment3"
                             + NEW_LINE + "ON g1.a1 = g2.a2 -- Comment4";
        helpTest(sql, expectedSql, null);
    }
}
