/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.runtime.client.lang.ParseInfo;
import org.teiid.runtime.client.lang.TeiidNodeFactory.ASTNodes;
import org.teiid.runtime.client.lang.ast.Command;
import org.teiid.runtime.client.lang.ast.CompareCriteria;
import org.teiid.runtime.client.lang.ast.Criteria;
import org.teiid.runtime.client.lang.ast.CriteriaOperator.Operator;
import org.teiid.runtime.client.lang.ast.ElementSymbol;
import org.teiid.runtime.client.lang.ast.From;
import org.teiid.runtime.client.lang.ast.GroupSymbol;
import org.teiid.runtime.client.lang.ast.JoinPredicate;
import org.teiid.runtime.client.lang.ast.JoinType;
import org.teiid.runtime.client.lang.ast.MultipleElementSymbol;
import org.teiid.runtime.client.lang.ast.Query;
import org.teiid.runtime.client.lang.ast.Select;
import org.teiid.runtime.client.lang.ast.UnaryFromClause;

/**
 * Unit testing for the Query Parser taking into account teiid versions
 */
public class TestQueryParser extends TestCase {

    private static List<ITeiidServerVersion> versions = new ArrayList<ITeiidServerVersion>();

    private QueryParser parser;
        
    @Override
    protected void setUp() throws Exception {
//        versions.add(new TeiidServerVersion("7.7.0")); //$NON-NLS-1$
        versions.add(new TeiidServerVersion("8.0.0")); //$NON-NLS-1$
    }

    @Override
    protected void tearDown() throws Exception {
        parser = null;
    }

    private <T> T newNode(ASTNodes nodeType) {
        return parser.getTeiidParser().createASTNode(nodeType);
    }

    private void helpTest(String sql, String expectedString, Command expectedCommand) {
        helpTest(sql, expectedString, expectedCommand, new ParseInfo());
    }

    private void helpTest(String sql, String expectedString, Command expectedCommand, ParseInfo info) {
        Command actualCommand = null;

        try {
            actualCommand = parser.parseCommand(sql);
        } catch(Throwable e) { 
            throw new RuntimeException(e);
        }

        assertEquals("Command objects do not match: ", expectedCommand, actualCommand);              //$NON-NLS-1$
    }

    // ======================== Joins ===============================================

    /** SELECT * FROM g1 inner join g2 on g1.a1=g2.a2 */
    @Test 
    public void testInnerJoin() {
        for (ITeiidServerVersion version : versions) {
            parser = new QueryParser(version);

            GroupSymbol gs1 = newNode(ASTNodes.GROUP_SYMBOL);
            gs1.setName("g1"); //$NON-NLS-1$
            UnaryFromClause g1 = newNode(ASTNodes.UNARY_FROM_CLAUSE);
            g1.setGroupSymbol(gs1);

            GroupSymbol gs2 = newNode(ASTNodes.GROUP_SYMBOL);
            gs2.setName("g2"); //$NON-NLS-1$
            UnaryFromClause g2 = newNode(ASTNodes.UNARY_FROM_CLAUSE);
            g2.setGroupSymbol(gs2);

            ElementSymbol es1 = newNode(ASTNodes.ELEMENT_SYMBOL);
            es1.setName("g1.a1"); //$NON-NLS-1$
            ElementSymbol es2 = newNode(ASTNodes.ELEMENT_SYMBOL);
            es2.setName("g2.a2"); //$NON-NLS-1$

            CompareCriteria jcrit = newNode(ASTNodes.COMPARE_CRITERIA);
            jcrit.setLeftExpression(es1);
            jcrit.setOperator(Operator.EQ);
            jcrit.setRightExpression(es2);
            ArrayList<Criteria> crits = new ArrayList<Criteria>();
            crits.add(jcrit);

            JoinType joinType = newNode(ASTNodes.JOIN_TYPE);
            joinType.setKind(JoinType.Kind.JOIN_INNER);
            JoinPredicate jp = newNode(ASTNodes.JOIN_PREDICATE);
            jp.setLeftClause(g1);
            jp.setRightClause(g2);
            jp.setJoinType(joinType);
            jp.setJoinCriteria(crits);

            From from = newNode(ASTNodes.FROM);
            from.setClauses(Collections.singletonList(jp));

            MultipleElementSymbol all = newNode(ASTNodes.MULTIPLE_ELEMENT_SYMBOL);
            Select select = newNode(ASTNodes.SELECT);
            select.addSymbol(all);

            Query query = newNode(ASTNodes.QUERY);
            query.setSelect(select);
            query.setFrom(from);
            
        helpTest("SELECT * FROM g1 inner join g2 on g1.a1=g2.a2",  //$NON-NLS-1$
                       "SELECT * FROM g1 INNER JOIN g2 ON g1.a1 = g2.a2",  //$NON-NLS-1$
                       query);
        }
    }
}
