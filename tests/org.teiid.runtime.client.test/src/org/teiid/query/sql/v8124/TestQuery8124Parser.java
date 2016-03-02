/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v8124;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.ParseInfo;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.v811.TestQuery811Parser;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestQuery8124Parser extends TestQuery811Parser {

    protected TestQuery8124Parser(Version teiidVersion) {
        super(teiidVersion);
    }

    public TestQuery8124Parser() {
        this(Version.TEIID_8_12_4);
    }

    @Test
    public void testDateTimeKeywordLiterals() throws Exception {
        String sql = "select DATE '1970-01-02', TIME '00:01:02', TIMESTAMP '2001-01-01 02:03:04.1'";
        Query actualCommand = (Query)parser.parseCommand(sql, new ParseInfo());
        assertEquals("SELECT {d'1970-01-02'}, {t'00:01:02'}, {ts'2001-01-01 02:03:04.1'}", actualCommand.toString());
    }

    @Test
    @Override
    public void testIsDistinctCriteria() throws Exception {
        String stmt = "IF(c IS DISTINCT FROM b) BEGIN DECLARE short a; END ELSE BEGIN DECLARE short b; END";
        Statement actualStmt = parser.getTeiidParser(stmt).statement(new ParseInfo());        
        String expected =   "IF(c IS DISTINCT FROM b)" + NEW_LINE +
                                        "BEGIN" + NEW_LINE +
                                        "DECLARE short a;" + NEW_LINE +
                                        "END" + NEW_LINE +
                                        "ELSE" + NEW_LINE +
                                        "BEGIN" + NEW_LINE +
                                        "DECLARE short b;" + NEW_LINE +
                                        "END";
        helpStmtTest(stmt, expected, actualStmt);
    }
}
