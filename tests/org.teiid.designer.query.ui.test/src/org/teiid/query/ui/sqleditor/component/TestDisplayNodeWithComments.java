/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.ui.sqleditor.component;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.util.TestUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestDisplayNodeWithComments implements StringConstants {

    private void helpTest(String sql, String expectedSql) throws Exception {
        IQueryParser parser = ModelerCore.getTeiidQueryService().getQueryParser();
        ICommand command = parser.parseDesignerCommand(sql);

        DisplayNode displayNode = DisplayNodeFactory.createDisplayNode(null, command);
        String actualStr = displayNode.toString();
        assertEquals(expectedSql, actualStr);
    }

    @Test
    public void testSimple1() throws Exception {
        String sql = "/*+ cache(ttl:300000) */" + NEW_LINE +
                            "SELECT" + NEW_LINE +
                            "/* Comment 1 */ " + "*" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 2 */ " + "Products_SQL_Server.products.dbo.ProductData" + NEW_LINE +
                            "/* Comment 3 */";
        String expectedSql = "/*+ cache(ttl:300000) */" + NEW_LINE +
                                            "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "*" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE + 
                                            TAB + TAB + "Products_SQL_Server.products.dbo.ProductData" + NEW_LINE +
                                            "/* Comment 3 */";
        helpTest(sql, expectedSql);
    }

    @Test
    public void testSimple2() throws Exception {
        String sql = "/*+ cache(ttl:300000) */" + NEW_LINE +
                            "SELECT" + NEW_LINE +
                            "/* Comment 1 */ " + "*" + NEW_LINE +
                            "FROM /* Comment 2 */" + NEW_LINE +
                            "Products_SQL_Server.products.dbo.ProductData" + NEW_LINE +
                            "/* Comment 3 */";
        String expectedSql = "/*+ cache(ttl:300000) */" + NEW_LINE +
                                            "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "*" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE + 
                                            TAB + TAB + "Products_SQL_Server.products.dbo.ProductData" + NEW_LINE +
                                            "/* Comment 3 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void testLineComments() throws Exception {
        String sql = "/*+ cache(ttl:300000) */" + NEW_LINE +
                            "SELECT" + NEW_LINE +
                            "-- Comment 1" + NEW_LINE +
                            "*" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "-- Comment 2" + NEW_LINE +
                            "Products_SQL_Server.products.dbo.ProductData" + NEW_LINE +
                            "-- Comment 3";
        String expectedSql = "/*+ cache(ttl:300000) */" + NEW_LINE +
                                            "SELECT" + NEW_LINE +
                                            TAB + TAB + "-- Comment 1" + NEW_LINE +
                                            TAB + TAB + "*" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "-- Comment 2" + NEW_LINE + 
                                            TAB + TAB + "Products_SQL_Server.products.dbo.ProductData" + NEW_LINE +
                                            "-- Comment 3" + NEW_LINE;

        // Only compatible with Teiid 8.10+
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_10.get());
        helpTest(sql, expectedSql);
    }

    @Test
    public void test1() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_7_7.get());
        String sql = "CREATE PROCEDURE" + NEW_LINE +
                            "BEGIN" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "DELETE FROM g;" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "a = 1;" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "ERROR 'My Error';" + NEW_LINE +
                            "END" + NEW_LINE +
                            "/* Comment 4 */";
        String expectedSql = "CREATE PROCEDURE" + NEW_LINE +
                                            "BEGIN" + NEW_LINE +
                                            TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + "DELETE FROM g;" + NEW_LINE +
                                            TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "a = 1;" + NEW_LINE +
                                            TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + "ERROR 'My Error';" + NEW_LINE +
                                            "END" + NEW_LINE +
                                            "/* Comment 4 */";
        helpTest(sql, expectedSql);
    }

    @Test
    public void test2() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_3.get());
        String sql = "CREATE PROCEDURE" + NEW_LINE +
                            "BEGIN" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "DELETE FROM g;" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "a = 1;" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "ERROR 'My Error';" + NEW_LINE +
                            "END" + NEW_LINE +
                            "/* Comment 4 */";
        String expectedSql = "CREATE VIRTUAL PROCEDURE" + NEW_LINE +
                                            "BEGIN" + NEW_LINE +
                                            TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + "DELETE FROM g;" + NEW_LINE +
                                            TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "a = 1;" + NEW_LINE +
                                            TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + "RAISE SQLEXCEPTION 'My Error';" + NEW_LINE +
                                            "END" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test3() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_4.get());
        String sql = "BEGIN" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "DELETE FROM g;" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "a = 1;" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "ERROR 'My Error';" + NEW_LINE +
                            "END" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "BEGIN" + NEW_LINE +
                                            TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + "DELETE FROM g;" + NEW_LINE +
                                            TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "a = 1;" + NEW_LINE +
                                            TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + "RAISE SQLEXCEPTION 'My Error';" + NEW_LINE +
                                            "END" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test4() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_7_7.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "trim(' ' FROM X) AS ID" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "Y" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "trim(' ' FROM X) AS ID" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + TAB + "Y" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test5() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_3.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "trim(' ' FROM X) AS ID" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "Y" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "trim(' ' FROM X) AS ID" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + TAB + "Y" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test6() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_4.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "trim(' ' FROM X) AS ID" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "Y" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "trim(' ' FROM X) AS ID" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "Y" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test7() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_7_7.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "'123' AS ID" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "X" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "'123' AS ID" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + TAB + "X" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test8() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_3.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "'123' AS ID" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "X" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "'123' AS ID" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + TAB + "X" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test9() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_4.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "'123' AS ID" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "X" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "'123' AS ID" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + TAB + "X" + NEW_LINE +
                                            "/* Comment 4 */";


        helpTest(sql, expectedSql);
    }

    @Test
    public void test10() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_7_7.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "concat('abcd', null) AS ProductName" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "PRODUCTDATA" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "concat('abcd', null) AS ProductName" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + TAB + "PRODUCTDATA" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test11() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_3.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "concat('abcd', null) AS ProductName" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "PRODUCTDATA" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "concat('abcd', null) AS ProductName" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + TAB + "PRODUCTDATA" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }

    @Test
    public void test12() throws Exception {
        TestUtilities.setDefaultServerVersion(Version.TEIID_8_4.get());
        String sql = "SELECT" + NEW_LINE +
                            "/* Comment 1 */" + NEW_LINE +
                            "concat('abcd', null) AS ProductName" + NEW_LINE +
                            "/* Comment 2 */" + NEW_LINE +
                            "FROM" + NEW_LINE +
                            "/* Comment 3 */" + NEW_LINE +
                            "PRODUCTDATA" + NEW_LINE +
                            "/* Comment 4 */";

        String expectedSql = "SELECT" + NEW_LINE +
                                            TAB + TAB + "/* Comment 1 */" + NEW_LINE +
                                            TAB + TAB + "concat('abcd', null) AS ProductName" + NEW_LINE +
                                            TAB + TAB + "/* Comment 2 */" + NEW_LINE +
                                            TAB + "FROM" + NEW_LINE +
                                            TAB + TAB + "/* Comment 3 */" + NEW_LINE +
                                            TAB + TAB + "PRODUCTDATA" + NEW_LINE +
                                            "/* Comment 4 */";

        helpTest(sql, expectedSql);
    }
}
