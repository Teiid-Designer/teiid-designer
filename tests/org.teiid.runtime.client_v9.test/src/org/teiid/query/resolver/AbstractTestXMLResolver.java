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

package org.teiid.query.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import org.junit.Test;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.navigator.DeepPreOrderNavigator;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;

@SuppressWarnings( {"nls", "javadoc"} )
public abstract class AbstractTestXMLResolver extends AbstractTest {

    /**
     * @param teiidVersion
     */
    public AbstractTestXMLResolver(Version teiidVersion) {
        super(teiidVersion);
    }

    protected Command helpParse(String sql) {
        try {
            return getQueryParser().parseCommand(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Command helpResolve(Command command, IQueryMetadataInterface queryMetadataInterface) {
        // resolve
        try {
            QueryResolver queryResolver = new QueryResolver(getQueryParser());
            queryResolver.resolveCommand(command, queryMetadataInterface);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CheckSymbolsAreResolvedVisitor vis = new CheckSymbolsAreResolvedVisitor(getTeiidVersion());
        DeepPreOrderNavigator.doVisit(command, vis);
        Collection<LanguageObject> unresolvedSymbols = vis.getUnresolvedSymbols();
        assertTrue("Found unresolved symbols: " + unresolvedSymbols, unresolvedSymbols.isEmpty()); //$NON-NLS-1$
        return command;
    }

    protected Command helpResolve(String sql, IQueryMetadataInterface queryMetadata) {
        return helpResolve(helpParse(sql), queryMetadata);
    }

    protected Command helpResolve(String sql) {
        Command cmd = helpResolve(sql, getMetadataFactory().example1Cached());
        ResolverUtil.fullyQualifyElements(cmd);
        return cmd;
    }

    protected void helpResolveException(String sql, IQueryMetadataInterface queryMetadata, String expectedExceptionMessage) {

        // parse
        Command command = helpParse(sql);

        // resolve
        try {
            QueryResolver queryResolver = new QueryResolver(getQueryParser());
            queryResolver.resolveCommand(command, queryMetadata);
            fail("Expected exception for resolving " + sql); //$NON-NLS-1$
        } catch (QueryResolverException e) {
            if (expectedExceptionMessage != null) {
                assertEquals(expectedExceptionMessage, e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void helpResolveException(String sql, IQueryMetadataInterface queryMetadata) {
        helpResolveException(sql, queryMetadata, null);
    }

    protected void helpResolveException(String sql) {
        helpResolveException(sql, getMetadataFactory().example1Cached());
    }

    protected void helpResolveException(String sql, String expectedMessage) {
        helpResolveException(sql, getMetadataFactory().example1Cached(), expectedMessage);
    }

    @Test
    public void testXMLCriteriaShortElement() {
        GroupSymbol gs = getFactory().newGroupSymbol("xmltest.doc1");
        ElementSymbol es = getFactory().newElementSymbol("root.node1", gs);
        CompareCriteria expected = getFactory().newCompareCriteria(es, Operator.EQ, getFactory().newConstant("yyz"));

        Query query = (Query)helpResolve("select * from xmltest.doc1 where node1 = 'yyz'");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
    }

    @Test
    public void testXMLCriteriaLongElement1() {
        GroupSymbol gs = getFactory().newGroupSymbol("xmltest.doc1");
        ElementSymbol es = getFactory().newElementSymbol("root.node1", gs);
        CompareCriteria expected = getFactory().newCompareCriteria(es, Operator.EQ, getFactory().newConstant("yyz"));

        Query query = (Query)helpResolve("select * from xmltest.doc1 where root.node1 = 'yyz'");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
    }

    @Test
    public void testXMLCriteriaLongElement2() {
        GroupSymbol gs = getFactory().newGroupSymbol("xmltest.doc4");
        ElementSymbol es1 = getFactory().newElementSymbol("root.node1", gs);
        CompareCriteria expected1 = getFactory().newCompareCriteria(es1, Operator.EQ, getFactory().newConstant("xyz"));
        
        Query query = (Query)helpResolve("select * from xmltest.doc4 where root.node1 = 'xyz'");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected1, actual);
    }

    @Test
    public void testXMLCriteriaLongElement3() {
        GroupSymbol gs = getFactory().newGroupSymbol("xmltest.doc4");
        ElementSymbol es2 = getFactory().newElementSymbol("root.node1.@node2", gs);
        CompareCriteria expected2 = getFactory().newCompareCriteria(es2, Operator.EQ, getFactory().newConstant("xyz"));

        Query query = (Query)helpResolve("select * from xmltest.doc4 where root.node1.@node2 = 'xyz'");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected2, actual);
    }

    @Test
    public void testXMLCriteriaLongElement4() {
        GroupSymbol gs = getFactory().newGroupSymbol("xmltest.doc4");
        ElementSymbol es3 = getFactory().newElementSymbol("root.node3", gs);
        CompareCriteria expected3 = getFactory().newCompareCriteria(es3, Operator.EQ, getFactory().newConstant("xyz"));
        
        Query query = (Query)helpResolve("select * from xmltest.doc4 where root.node3 = 'xyz'");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected3, actual);
    }

    @Test
    public void testXMLCriteriaLongElement5() {
        helpResolve("select * from xmltest.doc4 where root.node1 = 'yyz'");
    }

    @Test
    public void testXMLCriteriaLongElement6() {
        helpResolve("select * from xmltest.doc4 where root.node1.@node2 = 'yyz'");
    }

    @Test
    public void testXMLCriteriaLongElement7() {
        helpResolve("select * from xmltest.doc4 where root.node3 = 'yyz'");
    }

    @Test
    public void testXMLCriteriaLongElement8() {
        helpResolve("select * from xmltest.doc4 where node3 = 'yyz'");
    }

    @Test
    public void testXMLCriteriaLongElementFail1() {
        helpResolveException("select * from xmltest.doc4 where node3.node1.node2 = 'xyz'");
    }

    @Test
    public void testXMLCriteriaLongElementFail2() {
        helpResolveException("select * from xmltest.doc4 where root.node1.node2.node3 = 'xyz'");
    }

    @Test
    public void testXMLCriteriaLongElementFail3() {
        helpResolveException("select * from xmltest.doc4 where root.node1.node3 = 'xyz'");
    }

    @Test
    public void testXMLCriteriaLongElementFail4() {
        helpResolveException("select * from xmltest.doc4 where node2.node1.node2 = 'xyz'");
    }

    @Test
    public void testXMLCriteriaTempElement1() {
        helpResolve("select * from xmltest.doc4 where tm1.g1.e1 = 'x'");
    }

    @Test
    public void testXMLCriteriaTempElement2() {
        helpResolve("select * from xmltest.doc4 where root.node1.@node2 = 'yyz' and tm1.g1.e2 = 'y'");
    }

    @Test
    public void testXMLCriteriaTempElement3() {
        helpResolve("select * from xmltest.doc4 where tm1.g1.e1 = 'x' and tm1.g1.e2 = 'y'");
    }

    @Test
    public void testXMLCriteriaTempElementFail1() {
        helpResolveException("select * from xmltest.doc4 where tm1.g2.e1 = 'xyz'");
    }

    @Test
    public void testXMLCriteriaTempElementFail2() {
        helpResolveException("select * from xmltest.doc4 where root.node1.node2.node3 = 'xyz' and e1 = 'x'");
    }

    @Test
    public void testXMLCriteriaTempElementFail3() {
        helpResolveException("select * from xmltest.doc4 where e3 = 'xyz' and tm1.g2.e4='m'");
    }

    //tests ambiguously-named elements in both root temp group and document
    @Test
    public void testXMLAmbiguousName1() {
        helpResolve("select * from xmltest.doc4 where root.node1 is null");
    }

    @Test
    public void testXMLAmbiguousName2() {
        helpResolve("select * from xmltest.doc4 where tm1.g1.node1 = 'yyz'");
    }

    @Test
    public void testXMLAmbiguousName3() {
        helpResolveException("select * from xmltest.doc4 where node1 = 'yyz'");
    }

    @Test
    public void testXMLCriteriaLongElementInAnonymous() {
        GroupSymbol gs = getFactory().newGroupSymbol("xmltest.doc2");
        ElementSymbol es = getFactory().newElementSymbol("root.node1.node3", gs);
        CompareCriteria expected = getFactory().newCompareCriteria(es, Operator.EQ, getFactory().newConstant("yyz"));
        
        Query query = (Query)helpResolve("select * from xmltest.doc2 where root.node1.node3 = 'yyz'");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
    }

    @Test
    public void testXMLAmbiguousShortName() {
        helpResolveException("select * from xmltest.doc3 where node2 = 'yyz'");
    }

    /**
     * defect 9745
     */
    @Test
    public void testXMLAttributeInCriteria() {
        helpResolve("select * from xmltest.doc4 where root.node1.@node2 = 'x'");
    }

    /**
     * defect 9745
     */
    @Test
    public void testXMLAttributeInCriteria2() {
        helpResolve("select * from xmltest.doc4 where root.node1.node2 = 'x'");
    }

    /**
     * defect 9745
     */
    @Test
    public void testXMLAttributeInCriteria3() {
        helpResolve("select * from xmltest.doc4 where node2 = 'x'");
    }

    @Test
    public void testXMLAttributeElementAmbiguity1() {
        helpResolve("select * from xmltest.doc4 where root.node3.node4 = 'x'");
    }

    @Test
    public void testXMLAttributeElementAmbiguity2() {
        helpResolve("select * from xmltest.doc4 where root.node3.@node4 = 'x'");
    }

    @Test
    public void testXMLAttributeElementAmbiguity3() {
        helpResolve("select * from xmltest.doc4 where root.node3.node4 = 'x' and root.node3.@node4='y'");
    }

    /*
     * This should resolve to the XML element root.node3.root.node6
     */
    @Test
    public void testXMLAttributeElementAmbiguity4() {
        helpResolve("select * from xmltest.doc4 where root.node6 = 'x'");
    }

    /*
     * This should resolve to the XML attribute root.@node6
     */
    @Test
    public void testXMLAttributeElementAmbiguity5() {
        helpResolve("select * from xmltest.doc4 where root.@node6 = 'x'");
    }

    @Test
    public void testXMLAttributeFullPath() {
        helpResolve("select * from xmltest.doc4 where xmltest.doc4.root.@node6 = 'x'");
    }

    @Test
    public void testXMLCriteriaLongElementWithGroup1() {
        helpResolve("select * from xmltest.doc4 where xmltest.doc4.root.node1 = 'yyz'");
    }

    @Test
    public void testXMLCriteriaLongElementWithGroup2() {
        helpResolve("select * from xmltest.doc4 where xmltest.doc4.root.node1.@node2 = 'yyz'");
    }

    @Test
    public void testXMLCriteriaLongElementWithGroup3() {
        helpResolve("select * from xmltest.doc4 where xmltest.doc4.root.node3 = 'yyz'");
    }

    /*@Test public void testXMLElementPotentialAmbiguous() {    
        helpResolve("select * from xmltest.doc6 where node = 'yyz'");
    }*/

    @Test
    public void testXMLSelect() {
        helpResolve("select root.node3.@node4 from xmltest.doc4");
    }

    @Test
    public void testXMLSelect2() {
        helpResolve("select root.node3.node4 from xmltest.doc4");
    }

    @Test
    public void testXMLSelect3() {
        helpResolve("select root.@node6 from xmltest.doc4");
    }

    @Test
    public void testXMLSelect4() {
        helpResolve("select root.node6 from xmltest.doc4");
    }

    @Test
    public void testXMLSelect5() {
        helpResolve("select node2 from xmltest.doc4");
    }

    @Test
    public void testDEFECT_19771() {
        helpResolveException("select node2 AS NODE2 from xmltest.doc4");
    }

    @Test
    public void testContext() {
        GroupSymbol gs1 = getFactory().newGroupSymbol("xmltest.doc1");
        ElementSymbol es1 = getFactory().newElementSymbol("root.node1.node2.node3", gs1);
        ElementSymbol es2 = getFactory().newElementSymbol("root.node1", gs1);
        Expression[] exprs = new Expression[] {es1, es2};

        Function context = getFactory().newFunction("context", exprs);
        CompareCriteria expected = getFactory().newCompareCriteria(context, Operator.EQ, getFactory().newConstant("yyz"));

        Query query = (Query)helpResolve("select * from xmltest.doc1 where context(node3, node1) = 'yyz'");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
    }

    @Test
    public void testRowLimit() {
        GroupSymbol gs1 = getFactory().newGroupSymbol("xmltest.doc1");
        ElementSymbol es1 = getFactory().newElementSymbol("root.node1.node2.node3", gs1);
        Expression[] exprs = new Expression[] {es1};

        Function context = getFactory().newFunction("rowlimit", exprs);
        CompareCriteria expected = getFactory().newCompareCriteria(context, Operator.EQ, getFactory().newConstant(new Integer(2)));

        Query query = (Query)helpResolve("select * from xmltest.doc1 where rowlimit(node3) = 2");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
    }

    @Test
    public void testRowLimitException() {
        GroupSymbol gs1 = getFactory().newGroupSymbol("xmltest.doc1");
        ElementSymbol es1 = getFactory().newElementSymbol("root.node1.node2.node3", gs1);
        Expression[] exprs = new Expression[] {es1};

        Function context = getFactory().newFunction("rowlimitexception", exprs);
        CompareCriteria expected = getFactory().newCompareCriteria(context, Operator.EQ, getFactory().newConstant(new Integer(2)));

        Query query = (Query)helpResolve("select * from xmltest.doc1 where rowlimitexception(node3) = 2");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
    }

    @Test
    public void testXMLQueryFail1() {
        helpResolveException("SELECT DISTINCT * FROM vm1.doc1");
    }

    @Test
    public void testXMLQueryFail2() {
        helpResolveException("SELECT a2 FROM vm1.doc1");
    }

    @Test
    public void testXMLQueryFail3() {
        helpResolveException("SELECT * FROM vm1.doc1, vm1.doc2");
    }

    @Test
    public void testXMLWithOrderBy1() {
        helpResolveException("select * from xmltest.doc4 order by node1");
    }

    @Test
    public void testConversionInXML() {
        // Expected left expression
        GroupSymbol gs1 = getFactory().newGroupSymbol("xmltest.doc1");
        ElementSymbol es1 = getFactory().newElementSymbol("root.node1", gs1);
        // Expected right expression
        Function convert = getFactory().newFunction("convert",
                                                    new Expression[] {
                                                        getFactory().newConstant(new Integer(5)),
                                                        getFactory().newConstant("string")}); //$NON-NLS-2$

        // Expected criteria
        CompareCriteria expected = getFactory().newCompareCriteria(es1, Operator.EQ, convert);

        // Resolve the query and check against expected objects
        Query query = (Query)helpResolve("select * from xmltest.doc1 where node1 = convert(5, string)");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
        Function actualRightExpr = (Function)((CompareCriteria)actual).getRightExpression();
        assertNotNull("Failed to resolve function", actualRightExpr.getFunctionDescriptor());
    }

    @Test
    public void testXMLWithSelect1() throws Exception {
        GroupSymbol gs = getFactory().newGroupSymbol("xmltest.doc1");
        ElementSymbol es = getFactory().newElementSymbol("root.node1", gs);
        CompareCriteria expected = getFactory().newCompareCriteria(es, Operator.EQ, getFactory().newConstant("yyz"));

        Query query = (Query)helpResolve(getQueryParser().parseCommand("select \"xml\" from xmltest.doc1 where node1 = 'yyz'"),
                                                              getMetadataFactory().example1Cached());
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
    }

    @Test
    public void testXMLWithSelect1a() {
        helpResolveException("select 'a' from xmltest.doc1 where node1 = 'yyz'", "TEIID30134 Expressions cannot be selected by XML Queries"); //$NON-NLS-2$
    }

    @Test
    public void testXMLWithSelect2() {
        GroupSymbol gs = getFactory().newGroupSymbol("xmltest.doc1");
        ElementSymbol es = getFactory().newElementSymbol("root.node1", gs);
        CompareCriteria expected = getFactory().newCompareCriteria(es, Operator.EQ, getFactory().newConstant("yyz"));

        Query query = (Query)helpResolve("select xmltest.doc1.xml from xmltest.doc1 where node1 = 'yyz'");
        Criteria actual = query.getCriteria();
        assertEquals("Did not match expected criteria", expected, actual);
    }

}
