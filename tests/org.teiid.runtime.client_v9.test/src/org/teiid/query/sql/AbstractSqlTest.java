/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.ParseInfo;
import org.teiid.query.sql.lang.CacheHint;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public abstract class AbstractSqlTest extends AbstractTest<Command> {

    /**
     * @param teiidVersion
     */
    public AbstractSqlTest(Version teiidVersion) {
        super(teiidVersion);
    }

    protected void helpTest(String sql, String expectedString, Command expectedCommand, boolean designerCommand) {
        helpTest(sql, expectedString, expectedCommand, new ParseInfo(), true);
    }

    protected void helpTest(String sql, String expectedString, Command expectedCommand) {
        helpTest(sql, expectedString, expectedCommand, new ParseInfo(), false);
    }

    protected void helpTest(String sql, String expectedString, Command expectedCommand, ParseInfo info, boolean designerCommand) {
        Command actualCommand = null;
        String actualString = null;
    
        try {
            if (designerCommand)
                actualCommand = parser.parseDesignerCommand(sql);
            else
                actualCommand = parser.parseCommand(sql, info);
    
            actualString = actualCommand.toString();
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    
        if (expectedCommand != null)
            assertEquals("Command objects do not match: ", expectedCommand, actualCommand);
    
        if (expectedString != null)
            assertEquals("SQL strings do not match: ", expectedString, actualString);
    }

    protected void helpTestLiteral(Boolean expected, Class<?> expectedType, String sql, String expectedSql) {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(expected, expectedType)));
    
        Query query = getFactory().newQuery();
        query.setSelect(select);
    
        helpTest(sql, expectedSql, query);
    }

    protected void helpCriteriaTest(String crit, String expectedString, Criteria expectedCrit) {
        Criteria actualCriteria;
        String actualString;
    
        try {
            actualCriteria = parser.parseCriteria(crit);
            actualString = actualCriteria.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    
        assertEquals("Criteria does not match: ", expectedCrit, actualCriteria);
        assertEquals("SQL strings do not match: ", expectedString, actualString);
    }

    protected void helpException(String sql) {
        helpException(sql, null);
    }

    protected CacheHint helpGetCacheHint(String sql) {
    	return parser.getTeiidParser(sql).getQueryCacheOption(sql);
    }

    protected void helpException(String sql, String expected) {
        try {
            parser.parseCommand(sql);
            fail("Expected exception for parsing " + sql);
        } catch (Exception e) {
            if (expected != null) {
                assertEquals(expected, e.getMessage());
            }
        } catch (AssertionError e) {
            throw e;
        } catch (Error e) {
            if (expected != null) {
                assertEquals(expected, e.getMessage());
            }
        }
    }

    protected void helpTestExpression(String sql, String expectedString, Expression expected) throws Exception {
        Expression actual = parser.parseExpression(sql);
        String actualString = actual.toString();
        if (expected != null)
            assertEquals("Command objects do not match: ", expected, actual);
    
        assertEquals("SQL strings do not match: ", expectedString, actualString);
    }

    protected void helpStmtTest(String stmt, String expectedString, Statement expectedStmt) throws Exception {
        Statement actualStmt = parser.getTeiidParser(stmt).statement(new ParseInfo());
        String actualString = actualStmt.toString();
    
        if (expectedStmt != null)
            assertEquals("Language objects do not match: ", expectedStmt, actualStmt);
    
        assertEquals("SQL strings do not match: ", expectedString, actualString);
    }

}
