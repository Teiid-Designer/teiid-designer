/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teiid.core.util.TestUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.IJoinType;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.lang.ISubqueryCompareCriteria;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.proc.IAssignmentStatement;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.proc.IDeclareStatement;
import org.teiid.designer.query.sql.proc.IRaiseStatement;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@SuppressWarnings("javadoc")
public class TestDisplayNodeFactory extends TestCase {

    private static final Version VERSION_7_7_0 = Version.TEIID_7_7;
    
    private static final Version VERSIONS_8_3[] = {
                                                        Version.TEIID_8_0,
                                                        Version.TEIID_8_1,
                                                        Version.TEIID_8_2,
                                                        Version.TEIID_8_3
                                                     };

    private static final Version VERSIONS_8_4[] = {
                                                        Version.TEIID_8_4,
                                                        Version.TEIID_8_5,
                                                        Version.TEIID_8_6,
                                                        Version.TEIID_8_7,
                                                        Version.TEIID_8_8,
                                                        Version.TEIID_8_9,
                                                        Version.TEIID_8_10,
                                                        Version.TEIID_8_11,
                                                        Version.TEIID_8_12_4
//                                                        ,
//                                                        Version.TEIID_8_13_5,
//                                                        Version.TEIID_9_0
                                                        };

    private IQueryFactory factory;

    private IQueryParser parser;

    private Collection<ITeiidServerVersion> serverVersions;
    
    private Map<String, Map<ITeiidServerVersion, String>> expectedResults;

    // ################################## FRAMEWORK ################################

    public TestDisplayNodeFactory(String name) throws Exception {
        super(name);
        serverVersions = TeiidRuntimeRegistry.getInstance().getSupportedVersions();
        
        expectedResults = new HashMap<String, Map<ITeiidServerVersion,String>>();
        initExpectedResults();
    }
    
    private void addExpectedResult(String testName, Version version, String expectedResult) {
        Map<ITeiidServerVersion, String> map = expectedResults.get(testName);
        if (map == null) {
            map = new HashMap<ITeiidServerVersion, String>();
            expectedResults.put(testName, map);
        }
        
        map.put(version.get(), expectedResult);
    }
    
    private void initExpectedResults() {
        addExpectedResult("testAggregateSymbol1", VERSION_7_7_0, "COUNT('abc')");  //$NON-NLS-1$//$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testAggregateSymbol1", version83, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testAggregateSymbol1", version84, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        addExpectedResult("testAggregateSymbol2", VERSION_7_7_0, "COUNT(DISTINCT 'abc')"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testAggregateSymbol2", version83, "abc(DISTINCT 'abc')"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testAggregateSymbol2", version84, "abc(DISTINCT 'abc')"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        addExpectedResult("testAggregateSymbol3", VERSION_7_7_0, "COUNT(*)"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testAggregateSymbol3", version83, "abc(*)"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testAggregateSymbol3", version84, "abc(*)"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        addExpectedResult("testAggregateSymbol4", VERSION_7_7_0, "AVG('abc')");  //$NON-NLS-1$//$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testAggregateSymbol4", version83, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testAggregateSymbol4", version84, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        addExpectedResult("testAggregateSymbol5", VERSION_7_7_0, "SUM('abc')");  //$NON-NLS-1$//$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testAggregateSymbol5", version83, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testAggregateSymbol5", version84, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        addExpectedResult("testAggregateSymbol6", VERSION_7_7_0, "MIN('abc')");  //$NON-NLS-1$//$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testAggregateSymbol6", version83, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testAggregateSymbol6", version84, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        addExpectedResult("testAggregateSymbol7", VERSION_7_7_0, "MAX('abc')");  //$NON-NLS-1$//$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testAggregateSymbol7", version83, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testAggregateSymbol7", version84, "abc('abc')");  //$NON-NLS-1$//$NON-NLS-2$
        }

        addExpectedResult("testRaiseStatement", VERSION_7_7_0, "ERROR 'My Error';");  //$NON-NLS-1$//$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testRaiseStatement", version83, "RAISE 'My Error';");  //$NON-NLS-1$//$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
        	if( version84 == Version.TEIID_8_12_4) {
        		addExpectedResult("testRaiseStatement", version84, "RAISE 'My Error';");  //$NON-NLS-1$//$NON-NLS-2$
        	} else {
        		addExpectedResult("testRaiseStatement", version84, "RAISE 'My Error';");  //$NON-NLS-1$//$NON-NLS-2$
        	}
        }

        addExpectedResult("testRaiseStatementWithExpression", VERSION_7_7_0, "ERROR a;");  //$NON-NLS-1$//$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testRaiseStatementWithExpression", version83, "RAISE a;");  //$NON-NLS-1$//$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testRaiseStatementWithExpression", version84, "RAISE a;");  //$NON-NLS-1$//$NON-NLS-2$
        }

        addExpectedResult("testBlock1", VERSION_7_7_0, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tERROR 'My Error';\nEND");  //$NON-NLS-1$//$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testBlock1", version83, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
        	if( version84 == Version.TEIID_8_12_4) {
        		addExpectedResult("testBlock1", version84, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        	} else {
        		addExpectedResult("testBlock1", version84, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        	}
        }

        addExpectedResult("testCreateUpdateProcedure1", VERSION_7_7_0, "CREATE PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tERROR 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testCreateUpdateProcedure1", version83, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
        	if( version84 == Version.TEIID_8_12_4) {
        		addExpectedResult("testCreateUpdateProcedure1", version84, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        	} else {
        		addExpectedResult("testCreateUpdateProcedure1", version84, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        	}
        }

        addExpectedResult("testCreateUpdateProcedure2", VERSION_7_7_0, "CREATE PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tERROR 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testCreateUpdateProcedure2", version83, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
        	if( version84 == Version.TEIID_8_12_4) {
        		addExpectedResult("testCreateUpdateProcedure2", version84, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        	} else {
        		addExpectedResult("testCreateUpdateProcedure2", version84, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        	}
        }

        addExpectedResult("testCreateUpdateProcedure3", VERSION_7_7_0, "CREATE PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tERROR 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testCreateUpdateProcedure3", version83, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
        	if( version84 == Version.TEIID_8_12_4) {
        		addExpectedResult("testCreateUpdateProcedure3", version84, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        	} else {
        		addExpectedResult("testCreateUpdateProcedure3", version84, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$ //$NON-NLS-2$
        	}
        }

        addExpectedResult("testTrimAliasSymbol", VERSION_7_7_0, "SELECT\n\t\ttrim(' ' FROM X) AS ID\n\tFROM\n\t\tY"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testTrimAliasSymbol", version83, "SELECT\n\t\ttrim(' ' FROM X) AS ID\n\tFROM\n\t\tY"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testTrimAliasSymbol", version84, "SELECT\n\t\ttrim(' ' FROM X) AS ID\n\tFROM\n\t\tY"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        addExpectedResult("testConstantAliasSymbol", VERSION_7_7_0, "SELECT\n\t\t'123' AS ID\n\tFROM\n\t\tX"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testConstantAliasSymbol", version83, "SELECT\n\t\t'123' AS ID\n\tFROM\n\t\tX"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testConstantAliasSymbol", version84, "SELECT\n\t\t'123' AS ID\n\tFROM\n\t\tX"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        addExpectedResult("testConcatWithNull", VERSION_7_7_0, "SELECT\n\t\tconcat('abcd', null) AS ProductName\n\tFROM\n\t\tPRODUCTDATA"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Version version83 : VERSIONS_8_3) {
            addExpectedResult("testConcatWithNull", version83, "SELECT\n\t\tconcat('abcd', null) AS ProductName\n\tFROM\n\t\tPRODUCTDATA"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (Version version84 : VERSIONS_8_4) {
            addExpectedResult("testConcatWithNull", version84, "SELECT\n\t\tconcat('abcd', null) AS ProductName\n\tFROM\n\t\tPRODUCTDATA"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private String getExpectedResult(String testName, ITeiidServerVersion version) {
        Map<ITeiidServerVersion, String> versionResultMap = expectedResults.get(testName);
        assertNotNull(versionResultMap);
        
        for (Map.Entry<ITeiidServerVersion, String> entry : versionResultMap.entrySet()) {
            if (version.compareTo(entry.getKey()))
                return entry.getValue();
        }
        
        fail("Cannot get expected value for version " + version); //$NON-NLS-1$
        return null;
    }
    
    @Override
    protected void setUp() throws Exception {
        assertNotNull(serverVersions);
        assertFalse(serverVersions.isEmpty());
    }

    @Override
    protected void tearDown() throws Exception {
        factory = null;
        parser = null;
        TestUtilities.unregisterTeiidServerManager();
    }

    /**
     * @param version
     */
    private void setDefaultServerVersion(ITeiidServerVersion version) {
        TestUtilities.setDefaultServerVersion(version);
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        factory = queryService.createQueryFactory();
        parser = queryService.getQueryParser();
    }
    
    /**
     * Convert array to list
     * 
     * @param criteria
     * @return
     */
    private List<? extends ICriteria> createList(ICriteria ...criteria) {
        return Arrays.asList(criteria);
    }
    
    private List<? extends IExpression> createList(IExpression ... expressions) {
        return Arrays.asList(expressions);
    }

    // ################################## TEST HELPERS ################################

    private void helpTest(ILanguageObject obj,
                          String expectedStr) {
        DisplayNode displayNode = DisplayNodeFactory.createDisplayNode(null, obj);

        String actualStr = displayNode.toString();
        assertEquals("Expected and actual strings don't match", expectedStr, actualStr); //$NON-NLS-1$
    }

    // ################################## ACTUAL TESTS ################################

    public void testBetweenCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            IBetweenCriteria bc = factory.createBetweenCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                factory.createConstant(new Integer(1000)),
                                                                factory.createConstant(new Integer(2000)));
            helpTest(bc, "m.g.c1 BETWEEN 1000 AND 2000"); //$NON-NLS-1$
        }
    }

    public void testBetweenCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            IBetweenCriteria bc = factory.createBetweenCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                factory.createConstant(new Integer(1000)),
                                                                factory.createConstant(new Integer(2000)));
            bc.setNegated(true);
            helpTest(bc, "m.g.c1 NOT BETWEEN 1000 AND 2000"); //$NON-NLS-1$
        }
    }

    public void testCompareCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                ICompareCriteria.EQ,
                                                                factory.createConstant("abc")); //$NON-NLS-1$

            helpTest(cc, "m.g.c1 = 'abc'"); //$NON-NLS-1$
        }
    }

    public void testCompareCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                ICompareCriteria.NE,
                                                                factory.createConstant("abc")); //$NON-NLS-1$

            helpTest(cc, "m.g.c1 <> 'abc'"); //$NON-NLS-1$
        }
    }

    public void testCompareCriteria3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                ICompareCriteria.GT,
                                                                factory.createConstant("abc")); //$NON-NLS-1$

            helpTest(cc, "m.g.c1 > 'abc'"); //$NON-NLS-1$
        }
    }

    public void testCompareCriteria4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                ICompareCriteria.GE,
                                                                factory.createConstant("abc")); //$NON-NLS-1$

            helpTest(cc, "m.g.c1 >= 'abc'"); //$NON-NLS-1$
        }
    }

    public void testCompareCriteria5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                ICompareCriteria.LT,
                                                                factory.createConstant("abc")); //$NON-NLS-1$

            helpTest(cc, "m.g.c1 < 'abc'"); //$NON-NLS-1$
        }
    }

    public void testCompareCriteria6() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                ICompareCriteria.LE,
                                                                factory.createConstant("abc")); //$NON-NLS-1$

            helpTest(cc, "m.g.c1 <= 'abc'"); //$NON-NLS-1$
        }
    }

    public void testCompareCriteria7() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc = factory.createCompareCriteria(null, ICompareCriteria.EQ, null);

            helpTest(cc, "<undefined> = <undefined>"); //$NON-NLS-1$
        }
    }

    public void testCompoundCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                ICompareCriteria.EQ,
                                                                factory.createConstant("abc")); //$NON-NLS-1$
            
            ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.AND, createList(cc));

            helpTest(comp, "m.g.c1 = 'abc'"); //$NON-NLS-1$
        }
    }

    public void testCompoundCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                 ICompareCriteria.EQ,
                                                                 factory.createConstant("abc")); //$NON-NLS-1$
            ICompareCriteria cc2 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c2"), //$NON-NLS-1$
                                                                 ICompareCriteria.EQ,
                                                                 factory.createConstant("abc")); //$NON-NLS-1$
            ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.AND, createList(cc1, cc2));

            helpTest(comp, "(m.g.c1 = 'abc') AND (m.g.c2 = 'abc')"); //$NON-NLS-1$
        }
    }

    public void testCompoundCriteria3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                 ICompareCriteria.EQ,
                                                                 factory.createConstant("abc")); //$NON-NLS-1$
            ICompareCriteria cc2 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c2"), //$NON-NLS-1$
                                                                 ICompareCriteria.EQ,
                                                                 factory.createConstant("abc")); //$NON-NLS-1$
            ICompareCriteria cc3 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c3"), //$NON-NLS-1$
                                                                 ICompareCriteria.EQ,
                                                                 factory.createConstant("abc")); //$NON-NLS-1$
            ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.OR, createList(cc1, cc2, cc3));

            helpTest(comp, "(m.g.c1 = 'abc') OR (m.g.c2 = 'abc') OR (m.g.c3 = 'abc')"); //$NON-NLS-1$
        }
    }

    public void testCompoundCriteria4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                 ICompareCriteria.EQ,
                                                                 factory.createConstant("abc")); //$NON-NLS-1$
            ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.OR, createList(cc1, null));

            helpTest(comp, "(m.g.c1 = 'abc') OR (<undefined>)"); //$NON-NLS-1$
        }
    }

    public void testCompoundCriteria5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                 ICompareCriteria.EQ,
                                                                 factory.createConstant("abc")); //$NON-NLS-1$
            ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.OR, createList(null, cc1));

            helpTest(comp, "(<undefined>) OR (m.g.c1 = 'abc')"); //$NON-NLS-1$
        }
    }

    public void testCompoundCriteria6() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                                 ICompareCriteria.EQ,
                                                                 factory.createConstant("abc")); //$NON-NLS-1$
            ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.OR, createList(cc1, null));

            helpTest(comp, "(m.g.c1 = 'abc') OR (<undefined>)"); //$NON-NLS-1$
        }
    }

    public void testDelete1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IDelete delete = factory.createDelete();
            delete.setGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$

            helpTest(delete, "DELETE FROM m.g"); //$NON-NLS-1$
        }
    }

    public void testDelete2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IDelete delete = factory.createDelete();
            delete.setGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            delete.setCriteria(factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                             ICompareCriteria.EQ,
                                                             factory.createConstant("abc"))); //$NON-NLS-1$

            helpTest(delete, "DELETE FROM m.g\nWHERE\n\tm.g.c1 = 'abc'"); //$NON-NLS-1$
        }
    }

    public void testFrom1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            from.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$

            helpTest(from, "FROM\n\tm.g1, m.g2"); //$NON-NLS-1$
        }
    }

    public void testFrom2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFrom from = factory.createFrom();
            from.addClause(factory.createUnaryFromClause(factory.createGroupSymbol("m.g1"))); //$NON-NLS-1$
            from.addClause(factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                                       factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                                       factory.getJoinType(IJoinType.Types.JOIN_CROSS)));

            helpTest(from, "FROM\n\tm.g1, m.g2 CROSS JOIN m.g3"); //$NON-NLS-1$
        }
    }

    public void testGroupBy1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IGroupBy gb = factory.createGroupBy();
            gb.addSymbol(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$

            helpTest(gb, "GROUP BY m.g.e1"); //$NON-NLS-1$
        }
    }

    public void testGroupBy2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IGroupBy gb = factory.createGroupBy();
            gb.addSymbol(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
            gb.addSymbol(factory.createElementSymbol("m.g.e2")); //$NON-NLS-1$
            gb.addSymbol(factory.createElementSymbol("m.g.e3")); //$NON-NLS-1$

            helpTest(gb, "GROUP BY m.g.e1, m.g.e2, m.g.e3"); //$NON-NLS-1$
        }
    }

    public void testInsert1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IInsert insert = factory.createInsert();
            insert.setGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$

            List vars = new ArrayList();
            vars.add(factory.createElementSymbol("e1")); //$NON-NLS-1$
            vars.add(factory.createElementSymbol("e2")); //$NON-NLS-1$
            insert.setVariables(vars);
            List values = new ArrayList();
            values.add(factory.createConstant(new Integer(5)));
            values.add(factory.createConstant("abc")); //$NON-NLS-1$
            insert.setValues(values);

            helpTest(insert, "INSERT INTO m.g1\n\t\t(e1, e2)\n\tVALUES\n\t\t(5, 'abc')"); //$NON-NLS-1$
        }
    }

    public void testIsNullCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IIsNullCriteria inc = factory.createIsNullCriteria();
            inc.setExpression(factory.createConstant("abc")); //$NON-NLS-1$

            helpTest(inc, "'abc' IS NULL"); //$NON-NLS-1$
        }
    }

    public void testIsNullCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IIsNullCriteria inc = factory.createIsNullCriteria();
            inc.setExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$

            helpTest(inc, "m.g.e1 IS NULL"); //$NON-NLS-1$
        }
    }

    public void testIsNullCriteria3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IIsNullCriteria inc = factory.createIsNullCriteria();
            helpTest(inc, "<undefined> IS NULL"); //$NON-NLS-1$
        }
    }

    public void testIsNullCriteria4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IIsNullCriteria inc = factory.createIsNullCriteria();
            inc.setExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
            inc.setNegated(true);
            helpTest(inc, "m.g.e1 IS NOT NULL"); //$NON-NLS-1$
        }
    }

    public void testJoinPredicate1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                                            factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                                            factory.getJoinType(IJoinType.Types.JOIN_CROSS));

            helpTest(jp, "m.g2 CROSS JOIN m.g3"); //$NON-NLS-1$
        }
    }

    public void testOptionalJoinPredicate1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                                            factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                                            factory.getJoinType(IJoinType.Types.JOIN_CROSS));
            jp.setOptional(true);
            helpTest(jp, "/*+ optional */ (m.g2 CROSS JOIN m.g3)"); //$NON-NLS-1$
        }
    }

    public void testJoinPredicate2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ArrayList<ICriteria> crits = new ArrayList<ICriteria>();
            crits.add(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
            IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                                            factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                                            factory.getJoinType(IJoinType.Types.JOIN_INNER),
                                                            crits);

            helpTest(jp, "m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1"); //$NON-NLS-1$
        }
    }

    public void testJoinPredicate3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ArrayList<ICriteria> crits = new ArrayList<ICriteria>();
            crits.add(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
            crits.add(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e2"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e2"))); //$NON-NLS-1$ //$NON-NLS-2$
            IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                                            factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                                            factory.getJoinType(IJoinType.Types.JOIN_INNER),
                                                            crits);

            helpTest(jp, "m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1 AND m.g2.e2 = m.g3.e2"); //$NON-NLS-1$
        }
    }

    public void testJoinPredicate4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ArrayList<ICriteria> crits = new ArrayList<ICriteria>();
            crits.add(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
            IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                                            factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                                            factory.getJoinType(IJoinType.Types.JOIN_INNER),
                                                            crits);

            IJoinPredicate jp2 = factory.createJoinPredicate(jp,
                                                             factory.createUnaryFromClause(factory.createGroupSymbol("m.g1")), //$NON-NLS-1$
                                                             factory.getJoinType(IJoinType.Types.JOIN_CROSS));

            helpTest(jp2, "(m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1) CROSS JOIN m.g1"); //$NON-NLS-1$
        }
    }

    public void testJoinPredicate5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ArrayList<ICriteria> crits = new ArrayList<ICriteria>();
            crits.add(factory.createNotCriteria(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1")))); //$NON-NLS-1$ //$NON-NLS-2$
            IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                                            factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                                            factory.getJoinType(IJoinType.Types.JOIN_INNER),
                                                            crits);

            helpTest(jp, "m.g2 INNER JOIN m.g3 ON NOT (m.g2.e1 = m.g3.e1)"); //$NON-NLS-1$
        }
    }

    public void testJoinPredicate6() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ICompareCriteria comprCrit1 = factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1")); //$NON-NLS-1$ //$NON-NLS-2$
            ICompareCriteria comprCrit2 = factory.createCompareCriteria(factory.createElementSymbol("m.g2.e2"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e2")); //$NON-NLS-1$ //$NON-NLS-2$
            IIsNullCriteria inc = factory.createIsNullCriteria();
            inc.setExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$

            ICompoundCriteria compCrit = factory.createCompoundCriteria(ICompoundCriteria.OR, createList(inc, comprCrit2));

            ArrayList crits2 = new ArrayList();
            crits2.add(comprCrit1);
            crits2.add(compCrit);

            IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                                            factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                                            factory.getJoinType(IJoinType.Types.JOIN_LEFT_OUTER),
                                                            crits2);

            helpTest(jp, "m.g2 LEFT OUTER JOIN m.g3 ON m.g2.e1 = m.g3.e1 AND ((m.g.e1 IS NULL) OR (m.g2.e2 = m.g3.e2))"); //$NON-NLS-1$
        }
    }

    public void testMatchCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IMatchCriteria mc = factory.createMatchCriteria();
            mc.setLeftExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
            mc.setRightExpression(factory.createConstant("abc")); //$NON-NLS-1$

            helpTest(mc, "m.g.e1 LIKE 'abc'"); //$NON-NLS-1$
        }
    }

    public void testMatchCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IMatchCriteria mc = factory.createMatchCriteria();
            mc.setLeftExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
            mc.setRightExpression(factory.createConstant("%")); //$NON-NLS-1$
            mc.setEscapeChar('#');

            helpTest(mc, "m.g.e1 LIKE '%' ESCAPE '#'"); //$NON-NLS-1$
        }
    }

    public void testMatchCriteria3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IMatchCriteria mc = factory.createMatchCriteria();
            mc.setLeftExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
            mc.setRightExpression(factory.createConstant("abc")); //$NON-NLS-1$
            mc.setNegated(true);
            helpTest(mc, "m.g.e1 NOT LIKE 'abc'"); //$NON-NLS-1$
        }
    }

    public void testINotCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            INotCriteria not = factory.createNotCriteria(factory.createIsNullCriteria(factory.createElementSymbol("m.g.e1"))); //$NON-NLS-1$
            helpTest(not, "NOT (m.g.e1 IS NULL)"); //$NON-NLS-1$
        }
    }

    public void testINotCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            INotCriteria not = factory.createNotCriteria();
            helpTest(not, "NOT (<undefined>)"); //$NON-NLS-1$
        }
    }

    public void testOption1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IOption option = factory.createOption();
            helpTest(option, "OPTION"); //$NON-NLS-1$
        }
    }

    public void testOrderBy1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IOrderBy ob = factory.createOrderBy();
            ob.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

            helpTest(ob, "ORDER BY e1"); //$NON-NLS-1$
        }
    }

    public void testOrderBy2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IOrderBy ob = factory.createOrderBy();
            ob.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$
            ob.addVariable(factory.createAliasSymbol("x", factory.createElementSymbol("e2"))); //$NON-NLS-1$ //$NON-NLS-2$

            helpTest(ob, "ORDER BY e1, x"); //$NON-NLS-1$
        }
    }

    public void testOrderBy3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IOrderBy ob = factory.createOrderBy();
            ob.addVariable(factory.createElementSymbol("e1"), IOrderBy.DESC); //$NON-NLS-1$
            ob.addVariable(factory.createElementSymbol("x"), IOrderBy.DESC); //$NON-NLS-1$

            helpTest(ob, "ORDER BY e1 DESC, x DESC"); //$NON-NLS-1$
        }
    }

    public void testQuery1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createMultipleElementSymbol());
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            IQuery query = factory.createQuery();
            query.setSelect(select);
            query.setFrom(from);

            helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g"); //$NON-NLS-1$
        }
    }

    public void testQuery2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createMultipleElementSymbol());
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
            IGroupBy groupBy = factory.createGroupBy();
            groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            ICompareCriteria having = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$
            IOrderBy orderBy = factory.createOrderBy();
            orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

            IQuery query = factory.createQuery();
            query.setSelect(select);
            query.setFrom(from);
            query.setCriteria(cc);
            query.setGroupBy(groupBy);
            query.setHaving(having);
            query.setOrderBy(orderBy);

            helpTest(query,
                     "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
        }
    }

    public void testQuery3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createMultipleElementSymbol());
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            IGroupBy groupBy = factory.createGroupBy();
            groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            ICompareCriteria having = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$
            IOrderBy orderBy = factory.createOrderBy();
            orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

            IQuery query = factory.createQuery();
            query.setSelect(select);
            query.setFrom(from);
            query.setGroupBy(groupBy);
            query.setHaving(having);
            query.setOrderBy(orderBy);

            helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
        }
    }

    public void testQuery4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createMultipleElementSymbol());
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
            ICompareCriteria having = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$
            IOrderBy orderBy = factory.createOrderBy();
            orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

            IQuery query = factory.createQuery();
            query.setSelect(select);
            query.setFrom(from);
            query.setCriteria(cc);
            query.setHaving(having);
            query.setOrderBy(orderBy);

            helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
        }
    }

    public void testQuery5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createMultipleElementSymbol());
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
            IGroupBy groupBy = factory.createGroupBy();
            groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IOrderBy orderBy = factory.createOrderBy();
            orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

            IQuery query = factory.createQuery();
            query.setSelect(select);
            query.setFrom(from);
            query.setCriteria(cc);
            query.setGroupBy(groupBy);
            query.setOrderBy(orderBy);

            helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tORDER BY e1"); //$NON-NLS-1$
        }
    }

    public void testQuery6() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createMultipleElementSymbol());
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
            IGroupBy groupBy = factory.createGroupBy();
            groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            ICompareCriteria having = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$

            IQuery query = factory.createQuery();
            query.setSelect(select);
            query.setFrom(from);
            query.setCriteria(cc);
            query.setGroupBy(groupBy);
            query.setHaving(having);

            helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0"); //$NON-NLS-1$
        }
    }

    public void testQuery7() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createMultipleElementSymbol());
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
            IGroupBy groupBy = factory.createGroupBy();
            groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            ICompareCriteria having = factory.createCompareCriteria(factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$
            IOrderBy orderBy = factory.createOrderBy();
            orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

            IQuery query = factory.createQuery();
            query.setSelect(select);
            query.setFrom(from);
            query.setCriteria(cc);
            query.setGroupBy(groupBy);
            query.setHaving(having);
            query.setOrderBy(orderBy);

            helpTest(query,
                     "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
        }
    }

    public void testSetCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISetCriteria sc = factory.createSetCriteria();
            sc.setExpression(factory.createElementSymbol("e1")); //$NON-NLS-1$
            sc.setValues(new ArrayList());

            helpTest(sc, "e1 IN ()"); //$NON-NLS-1$
        }
    }

    public void testSetCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISetCriteria sc = factory.createSetCriteria();
            sc.setExpression(factory.createElementSymbol("e1")); //$NON-NLS-1$
            ArrayList values = new ArrayList();
            values.add(factory.createElementSymbol("e2")); //$NON-NLS-1$
            values.add(factory.createConstant("abc")); //$NON-NLS-1$
            sc.setValues(values);

            helpTest(sc, "e1 IN (e2, 'abc')"); //$NON-NLS-1$
        }
    }

    public void testSetCriteria3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISetCriteria sc = factory.createSetCriteria();
            sc.setExpression(factory.createElementSymbol("e1")); //$NON-NLS-1$
            ArrayList values = new ArrayList();
            values.add(null);
            values.add(factory.createConstant("b")); //$NON-NLS-1$
            sc.setValues(values);

            helpTest(sc, "e1 IN (<undefined>, 'b')"); //$NON-NLS-1$
        }
    }

    public void testSetCriteria4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISetCriteria sc = factory.createSetCriteria();
            sc.setExpression(factory.createElementSymbol("e1")); //$NON-NLS-1$
            ArrayList values = new ArrayList();
            values.add(factory.createElementSymbol("e2")); //$NON-NLS-1$
            values.add(factory.createConstant("abc")); //$NON-NLS-1$
            sc.setValues(values);
            sc.setNegated(true);
            helpTest(sc, "e1 NOT IN (e2, 'abc')"); //$NON-NLS-1$
        }
    }

    public void testSetQuery1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            ISelect s2 = factory.createSelect();
            s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f2 = factory.createFrom();
            f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
            IQuery q2 = factory.createQuery();
            q2.setSelect(s2);
            q2.setFrom(f2);

            ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION);
            sq.setAll(false);
            sq.setLeftQuery(q1);
            sq.setRightQuery(q2);

            helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
        }
    }

    public void testSetQuery2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            ISelect s2 = factory.createSelect();
            s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f2 = factory.createFrom();
            f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
            IQuery q2 = factory.createQuery();
            q2.setSelect(s2);
            q2.setFrom(f2);

            ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION);
            sq.setLeftQuery(q1);
            sq.setRightQuery(q2);

            helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION ALL\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
        }
    }

    public void testSetQuery3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            ISelect s2 = factory.createSelect();
            s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f2 = factory.createFrom();
            f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
            IQuery q2 = factory.createQuery();
            q2.setSelect(s2);
            q2.setFrom(f2);

            IOrderBy orderBy = factory.createOrderBy();
            orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

            ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);
            sq.setOrderBy(orderBy);

            helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2\nORDER BY e1"); //$NON-NLS-1$
        }
    }

    public void testSetQuery4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            ISelect s2 = factory.createSelect();
            s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f2 = factory.createFrom();
            f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
            IQuery q2 = factory.createQuery();
            q2.setSelect(s2);
            q2.setFrom(f2);

            ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);

            helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
        }
    }

    public void testSetQuery5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            ISelect s2 = factory.createSelect();
            s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f2 = factory.createFrom();
            f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
            IQuery q2 = factory.createQuery();
            q2.setSelect(s2);
            q2.setFrom(f2);

            ISelect s3 = factory.createSelect();
            s3.addSymbol(factory.createElementSymbol("e3")); //$NON-NLS-1$
            IFrom f3 = factory.createFrom();
            f3.addGroup(factory.createGroupSymbol("m.g3")); //$NON-NLS-1$
            IQuery q3 = factory.createQuery();
            q3.setSelect(s3);
            q3.setFrom(f3);

            ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);

            ISetQuery sq2 = factory.createSetQuery(ISetQuery.Operation.UNION, true, q3, sq);

            helpTest(sq2,
                     "SELECT\n\t\te3\n\tFROM\n\t\tm.g3\nUNION ALL\n(SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2)"); //$NON-NLS-1$
        }
    }

    public void testSubqueryFromClause1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            ISubqueryFromClause sfc = factory.createSubqueryFromClause("temp", q1); //$NON-NLS-1$
            helpTest(sfc, "(SELECT e1 FROM m.g1) AS temp"); //$NON-NLS-1$
        }
    }

    public void testOptionalSubqueryFromClause1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            ISubqueryFromClause sfc = factory.createSubqueryFromClause("temp", q1); //$NON-NLS-1$
            sfc.setOptional(true);
            helpTest(sfc, "/*+ optional */ (SELECT e1 FROM m.g1) AS temp"); //$NON-NLS-1$
        }
    }

    public void testSubquerySetCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            IElementSymbol expr = factory.createElementSymbol("e2"); //$NON-NLS-1$

            ISubquerySetCriteria ssc = factory.createSubquerySetCriteria(expr, q1);
            helpTest(ssc, "e2 IN (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
        }
    }

    public void testSubquerySetCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            IElementSymbol expr = factory.createElementSymbol("e2"); //$NON-NLS-1$

            ISubquerySetCriteria ssc = factory.createSubquerySetCriteria(expr, q1);
            ssc.setNegated(true);
            helpTest(ssc, "e2 NOT IN (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
        }
    }

    public void testUnaryFromClause() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createUnaryFromClause(factory.createGroupSymbol("m.g1")), "m.g1"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testOptionalUnaryFromClause() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IUnaryFromClause unaryFromClause = factory.createUnaryFromClause(factory.createGroupSymbol("m.g1"));//$NON-NLS-1$
            unaryFromClause.setOptional(true);
            helpTest(unaryFromClause, "/*+ optional */ m.g1"); //$NON-NLS-1$ 
        }
    }

    public void testAggregateSymbol1() {
        for (ITeiidServerVersion version : serverVersions) {    
            setDefaultServerVersion(version);
            IAggregateSymbol agg = factory.createAggregateSymbol("abc", IAggregateSymbol.Type.COUNT, false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(agg, getExpectedResult("testAggregateSymbol1", version)); //$NON-NLS-1$
        }
    }

    public void testAggregateSymbol2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAggregateSymbol agg = factory.createAggregateSymbol("abc", IAggregateSymbol.Type.COUNT, true, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(agg, getExpectedResult("testAggregateSymbol2", version)); //$NON-NLS-1$
        }
    }

    public void testAggregateSymbol3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAggregateSymbol agg = factory.createAggregateSymbol("abc", IAggregateSymbol.Type.COUNT, false, null); //$NON-NLS-1$
            helpTest(agg, getExpectedResult("testAggregateSymbol3", version)); //$NON-NLS-1$
        }
    }

    public void testAggregateSymbol4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAggregateSymbol agg = factory.createAggregateSymbol("abc", IAggregateSymbol.Type.AVG, false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(agg, getExpectedResult("testAggregateSymbol4", version)); //$NON-NLS-1$
        }
    }

    public void testAggregateSymbol5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAggregateSymbol agg = factory.createAggregateSymbol("abc", IAggregateSymbol.Type.SUM, false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(agg, getExpectedResult("testAggregateSymbol5", version)); //$NON-NLS-1$
        }
    }

    public void testAggregateSymbol6() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAggregateSymbol agg = factory.createAggregateSymbol("abc", IAggregateSymbol.Type.MIN, false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(agg, getExpectedResult("testAggregateSymbol6", version)); //$NON-NLS-1$
        }
    }

    public void testAggregateSymbol7() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAggregateSymbol agg = factory.createAggregateSymbol("abc", IAggregateSymbol.Type.MAX, false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(agg, getExpectedResult("testAggregateSymbol7", version)); //$NON-NLS-1$
        }
    }

    public void testAliasSymbol1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAliasSymbol as = factory.createAliasSymbol("x", factory.createElementSymbol("y")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(as, "y AS x"); //$NON-NLS-1$
        }
    }

    // Test alias symbol with reserved word
    public void testAliasSymbol2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAliasSymbol as = factory.createAliasSymbol("select", factory.createElementSymbol("y")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(as, "y AS \"select\""); //$NON-NLS-1$
        }
    }

    public void testMultipleElementSymbol() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createMultipleElementSymbol(), "*"); //$NON-NLS-1$
        }
    }

    public void testConstantNull() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant(null), "null"); //$NON-NLS-1$
        }
    }

    public void testConstantString() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant("abc"), "'abc'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testConstantInteger() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant(new Integer(5)), "5"); //$NON-NLS-1$
        }
    }

    public void testConstantBigDecimal() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant(new BigDecimal("5.4")), "5.4"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testConstantStringWithTick() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant("O'Leary"), "'O''Leary'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testConstantStringWithTicks() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant("'abc'"), "'''abc'''"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testConstantStringWithMoreTicks() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant("a'b'c"), "'a''b''c'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testConstantStringWithDoubleTick() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant("group=\"x\""), "'group=\"x\"'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testConstantBooleanTrue() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant(Boolean.TRUE), "TRUE"); //$NON-NLS-1$
        }
    }

    public void testConstantBooleanFalse() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant(Boolean.FALSE), "FALSE"); //$NON-NLS-1$
        }
    }

    public void testConstantDate() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant(java.sql.Date.valueOf("2002-10-02")), "{d'2002-10-02'}"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testConstantTime() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant(java.sql.Time.valueOf("5:00:00")), "{t'05:00:00'}"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testConstantTimestamp() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            helpTest(factory.createConstant(java.sql.Timestamp.valueOf("2002-10-02 17:10:35.0234")), "{ts'2002-10-02 17:10:35.0234'}"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void testElementSymbol1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IElementSymbol es = factory.createElementSymbol("elem"); //$NON-NLS-1$
            helpTest(es, "elem"); //$NON-NLS-1$
        }
    }

    public void testElementSymbol2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IElementSymbol es = factory.createElementSymbol("elem", false); //$NON-NLS-1$
            es.setGroupSymbol(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            helpTest(es, "elem"); //$NON-NLS-1$
        }
    }

    public void testElementSymbol3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IElementSymbol es = factory.createElementSymbol("m.g.elem", true); //$NON-NLS-1$
            es.setGroupSymbol(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            helpTest(es, "m.g.elem"); //$NON-NLS-1$
        }
    }

    public void testElementSymbol4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IElementSymbol es = factory.createElementSymbol("elem", true); //$NON-NLS-1$
            es.setGroupSymbol(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            helpTest(es, "m.g.elem"); //$NON-NLS-1$
        }
    }

    public void testElementSymbol5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IElementSymbol es = factory.createElementSymbol("m.g.select", false); //$NON-NLS-1$
            es.setGroupSymbol(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
            helpTest(es, "\"select\""); //$NON-NLS-1$
        }
    }

    public void testExpressionSymbol1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IExpressionSymbol expr = factory.createExpressionSymbol("abc", factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(expr, "'abc'"); //$NON-NLS-1$
        }
    }

    public void testFunction1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            List<? extends IExpression> expressions = Arrays.asList(factory.createConstant("a"), null); //$NON-NLS-1$
            IFunction func = factory.createFunction("concat", expressions); //$NON-NLS-1$
            helpTest(func, "concat('a', <undefined>)"); //$NON-NLS-1$
        }
    }

    public void testFunction2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("now", new ArrayList<IExpression>()); //$NON-NLS-1$
            helpTest(func, "now()"); //$NON-NLS-1$
        }
    }

    public void testFunction3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("concat", createList(null, null)); //$NON-NLS-1$
            helpTest(func, "concat(<undefined>, <undefined>)"); //$NON-NLS-1$
        }
    }

    public void testFunction4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func1 = factory.createFunction("power", createList( //$NON-NLS-1$
                                                     factory.createConstant(new Integer(5)),
                                                         factory.createConstant(new Integer(3))));
            IFunction func2 = factory.createFunction("power", createList( //$NON-NLS-1$
                                                     func1, factory.createConstant(new Integer(3))));
            IFunction func3 = factory.createFunction("+", createList( //$NON-NLS-1$
                                                     factory.createConstant(new Integer(1000)), func2));
            helpTest(func3, "(1000 + power(power(5, 3), 3))"); //$NON-NLS-1$
        }
    }

    public void testFunction5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func1 = factory.createFunction("concat", createList( //$NON-NLS-1$
                                                     factory.createElementSymbol("elem2"), //$NON-NLS-1$
                                                         null));
            IFunction func2 = factory.createFunction("concat", createList( //$NON-NLS-1$
                                                     factory.createElementSymbol("elem1"), //$NON-NLS-1$
                                                         func1));
            helpTest(func2, "concat(elem1, concat(elem2, <undefined>))"); //$NON-NLS-1$
        }
    }

    public void testConvertFunction1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("convert", createList( //$NON-NLS-1$
                                                    factory.createConstant("5"), //$NON-NLS-1$
                                                        factory.createConstant("integer") //$NON-NLS-1$
                                                    ));
            helpTest(func, "convert('5', integer)"); //$NON-NLS-1$
        }
    }

    public void testConvertFunction2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("convert", createList( //$NON-NLS-1$
                                                    null, factory.createConstant("integer") //$NON-NLS-1$
                                                    ));
            helpTest(func, "convert(<undefined>, integer)"); //$NON-NLS-1$
        }
    }

    public void testConvertFunction3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("convert", createList( //$NON-NLS-1$
                                                    factory.createConstant(null), factory.createConstant("integer") //$NON-NLS-1$
                                                    ));
            helpTest(func, "convert(null, integer)"); //$NON-NLS-1$
        }
    }

    public void testConvertFunction5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("convert", null); //$NON-NLS-1$
            helpTest(func, "convert()"); //$NON-NLS-1$
        }
    }

    public void testConvertFunction6() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("convert", new ArrayList<IExpression>()); //$NON-NLS-1$
            helpTest(func, "convert()"); //$NON-NLS-1$
        }
    }

    public void testCastFunction1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("cast", createList( //$NON-NLS-1$
                                                    factory.createConstant("5"), //$NON-NLS-1$
                                                        factory.createConstant("integer") //$NON-NLS-1$
                                                    ));
            helpTest(func, "cast('5' AS integer)"); //$NON-NLS-1$
        }
    }

    public void testCastFunction2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("cast", createList( //$NON-NLS-1$
                                                    null, factory.createConstant("integer") //$NON-NLS-1$
                                                    ));
            helpTest(func, "cast(<undefined> AS integer)"); //$NON-NLS-1$
        }
    }

    public void testCastFunction3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("cast", createList( //$NON-NLS-1$
                                                    factory.createConstant(null), factory.createConstant("integer") //$NON-NLS-1$
                                                    ));
            helpTest(func, "cast(null AS integer)"); //$NON-NLS-1$
        }
    }

    public void testArithemeticFunction1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IFunction func = factory.createFunction("-", createList( //$NON-NLS-1$
                                                    factory.createConstant(new Integer(-2)),
                                                        factory.createConstant(new Integer(-1))));
            helpTest(func, "(-2 - -1)"); //$NON-NLS-1$
        }
    }

    public void testGroupSymbol1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IGroupSymbol gs = factory.createGroupSymbol("g"); //$NON-NLS-1$
            helpTest(gs, "g"); //$NON-NLS-1$
        }
    }

    public void testGroupSymbol2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IGroupSymbol gs = factory.createGroupSymbol("x", "g"); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(gs, "g AS x"); //$NON-NLS-1$
        }
    }

    public void testGroupSymbol3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IGroupSymbol gs = factory.createGroupSymbol("vdb.g"); //$NON-NLS-1$
            helpTest(gs, "vdb.g"); //$NON-NLS-1$
        }
    }

    public void testGroupSymbol4() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IGroupSymbol gs = factory.createGroupSymbol("x", "vdb.g"); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(gs, "vdb.g AS x"); //$NON-NLS-1$
        }
    }

    public void testGroupSymbol5() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IGroupSymbol gs = factory.createGroupSymbol("from", "m.g"); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(gs, "m.g AS \"from\""); //$NON-NLS-1$
        }
    }

    public void testGroupSymbol6() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IGroupSymbol gs = factory.createGroupSymbol("x", "on.select"); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(gs, "\"on\".\"select\" AS x"); //$NON-NLS-1$
        }
    }

    public void testExecNoParams() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IStoredProcedure proc = factory.createStoredProcedure();
            proc.setProcedureName("myproc"); //$NON-NLS-1$
            helpTest(proc, "EXEC myproc()"); //$NON-NLS-1$
        }
    }

    public void testExecInputParam() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IStoredProcedure proc = factory.createStoredProcedure();
            proc.setProcedureName("myproc"); //$NON-NLS-1$
            ISPParameter param = factory.createSPParameter(1, factory.createReference(0));
            proc.setParameter(param);
            helpTest(proc, "EXEC myproc(?)"); //$NON-NLS-1$
        }
    }

    public void testExecInputOutputParam() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IStoredProcedure proc = factory.createStoredProcedure();
            proc.setProcedureName("myproc"); //$NON-NLS-1$
            ISPParameter param1 = factory.createSPParameter(1, factory.createConstant(new Integer(5)));
            param1.setParameterType(ISPParameter.ParameterInfo.IN);
            proc.setParameter(param1);

            ISPParameter param2 = factory.createSPParameter(2, ISPParameter.ParameterInfo.OUT, "x"); //$NON-NLS-1$
            proc.setParameter(param2);

            helpTest(proc, "EXEC myproc(5)"); //$NON-NLS-1$
        }
    }

    public void testExecOutputInputParam() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IStoredProcedure proc = factory.createStoredProcedure();
            proc.setProcedureName("myproc"); //$NON-NLS-1$

            ISPParameter param2 = factory.createSPParameter(2, ISPParameter.ParameterInfo.OUT, "x"); //$NON-NLS-1$
            proc.setParameter(param2);

            ISPParameter param1 = factory.createSPParameter(1, factory.createConstant(new Integer(5)));
            param1.setParameterType(ISPParameter.ParameterInfo.IN);
            proc.setParameter(param1);

            helpTest(proc, "EXEC myproc(5)"); //$NON-NLS-1$
        }
    }

    public void testExecReturnParam() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IStoredProcedure proc = factory.createStoredProcedure();
            proc.setProcedureName("myproc"); //$NON-NLS-1$

            ISPParameter param = factory.createSPParameter(1, ISPParameter.ParameterInfo.RETURN_VALUE, "ret"); //$NON-NLS-1$
            proc.setParameter(param);
            helpTest(proc, "EXEC myproc()"); //$NON-NLS-1$
        }
    }

    public void testExecNamedParam() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IStoredProcedure proc = factory.createStoredProcedure();
            proc.setDisplayNamedParameters(true);
            proc.setProcedureName("myproc"); //$NON-NLS-1$
            ISPParameter param = factory.createSPParameter(1, factory.createReference(0));
            param.setName("p1");//$NON-NLS-1$
            proc.setParameter(param);
            helpTest(proc, "EXEC myproc(p1 => ?)"); //$NON-NLS-1$
        }
    }

    public void testExecNamedParams() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IStoredProcedure proc = factory.createStoredProcedure();
            proc.setDisplayNamedParameters(true);
            proc.setProcedureName("myproc"); //$NON-NLS-1$
            ISPParameter param = factory.createSPParameter(1, factory.createReference(0));
            param.setName("p1");//$NON-NLS-1$
            proc.setParameter(param);
            ISPParameter param2 = factory.createSPParameter(2, factory.createReference(0));
            param2.setName("p2");//$NON-NLS-1$
            proc.setParameter(param2);
            helpTest(proc, "EXEC myproc(p1 => ?, p2 => ?)"); //$NON-NLS-1$
        }
    }

    /**
     * Test when a parameter's name is a reserved word. (Note: parameters should always have short names, not multiple
     * period-delimited name components.)
     * 
     * @since 4.3
     */
    public void testExecNamedParamsReservedWord() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IStoredProcedure proc = factory.createStoredProcedure();
            proc.setDisplayNamedParameters(true);
            proc.setProcedureName("myproc"); //$NON-NLS-1$
            ISPParameter param = factory.createSPParameter(1, factory.createReference(0));
            param.setName("in");//$NON-NLS-1$
            proc.setParameter(param);
            ISPParameter param2 = factory.createSPParameter(2, factory.createReference(0));
            param2.setName("in2");//$NON-NLS-1$
            proc.setParameter(param2);
            helpTest(proc, "EXEC myproc(\"in\" => ?, in2 => ?)"); //$NON-NLS-1$
        }
    }

    // Test methods for Update Procedure Language Objects

    public void testDeclareStatement() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IDeclareStatement dclStmt = factory.createDeclareStatement(factory.createElementSymbol("a"), "String"); //$NON-NLS-1$ //$NON-NLS-2$
            helpTest(dclStmt, "DECLARE String a;"); //$NON-NLS-1$
        }
    }

    public void testRaiseStatement() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
            helpTest(errStmt, getExpectedResult("testRaiseStatement", version)); //$NON-NLS-1$
        }
    }

    public void testRaiseStatementWithExpression() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IRaiseStatement errStmt = factory.createRaiseStatement(factory.createElementSymbol("a")); //$NON-NLS-1$
            helpTest(errStmt, getExpectedResult("testRaiseStatementWithExpression", version)); //$NON-NLS-1$
        }
    }

    public void testAssignmentStatement1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
            helpTest(assigStmt, "a = 1;"); //$NON-NLS-1$
        }
    }

    public void FAILINGtestAssignmentStatement2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            // TODO fix this test
            IQuery q1 = factory.createQuery();
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createElementSymbol("x")); //$NON-NLS-1$
            q1.setSelect(select);
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
            q1.setFrom(from);

            IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), q1); //$NON-NLS-1$
            helpTest(assigStmt, "a = SELECT x FROM g;"); //$NON-NLS-1$
        }
    }

    public void testCommandStatement1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IQuery q1 = factory.createQuery();
            ISelect select = factory.createSelect();
            select.addSymbol(factory.createElementSymbol("x")); //$NON-NLS-1$
            q1.setSelect(select);
            IFrom from = factory.createFrom();
            from.addGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
            q1.setFrom(from);

            ICommandStatement cmdStmt = factory.createCommandStatement(q1);
            helpTest(cmdStmt, "SELECT x FROM g;"); //$NON-NLS-1$
        }
    }

    public void testCommandStatement2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IDelete d1 = factory.createDelete();
            d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
            ICommandStatement cmdStmt = factory.createCommandStatement(d1);
            helpTest(cmdStmt, "DELETE FROM g;"); //$NON-NLS-1$
        }
    }

    public void testBlock1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IDelete d1 = factory.createDelete();
            d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
            ICommandStatement cmdStmt = factory.createCommandStatement(d1);
            IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
            IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
            IBlock b = factory.createBlock();
            b.addStatement(cmdStmt);
            b.addStatement(assigStmt);
            b.addStatement(errStmt);
            helpTest(b, getExpectedResult("testBlock1", version)); //$NON-NLS-1$
        }
    }

    public void testCreateUpdateProcedure1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IDelete d1 = factory.createDelete();
            d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
            ICommandStatement cmdStmt = factory.createCommandStatement(d1);
            IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
            IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
            IBlock b = factory.createBlock();
            b.addStatement(cmdStmt);
            b.addStatement(assigStmt);
            b.addStatement(errStmt);
            ICreateProcedureCommand cup = factory.createCreateProcedureCommand(b);
            helpTest(cup, getExpectedResult("testCreateUpdateProcedure1", version)); //$NON-NLS-1$
        }
    }

    public void testCreateUpdateProcedure2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IDelete d1 = factory.createDelete();
            d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
            ICommandStatement cmdStmt = factory.createCommandStatement(d1);
            IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
            IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
            IBlock b = factory.createBlock();
            b.addStatement(cmdStmt);
            b.addStatement(assigStmt);
            b.addStatement(errStmt);
            ICreateProcedureCommand cup = factory.createCreateProcedureCommand(b);
            helpTest(cup, getExpectedResult("testCreateUpdateProcedure2", version)); //$NON-NLS-1$
        }
    }

    public void testCreateUpdateProcedure3() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IDelete d1 = factory.createDelete();
            d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
            ICommandStatement cmdStmt = factory.createCommandStatement(d1);
            IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
            IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
            IBlock b = factory.createBlock();
            b.addStatement(cmdStmt);
            b.addStatement(assigStmt);
            b.addStatement(errStmt);
            ICreateProcedureCommand cup = factory.createCreateProcedureCommand(b);
            helpTest(cup, getExpectedResult("testCreateUpdateProcedure3", version)); //$NON-NLS-1$
        }
    }

    public void testSubqueryCompareCriteria1() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            IElementSymbol expr = factory.createElementSymbol("e2"); //$NON-NLS-1$

            ISubqueryCompareCriteria scc = factory.createSubqueryCompareCriteria(expr,
                                                                                 q1,
                                                                                 ICompareCriteria.EQ,
                                                                                 ISubqueryCompareCriteria.ANY);

            helpTest(scc, "e2 = ANY (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
        }
    }

    public void testSubqueryCompareCriteria2() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            IElementSymbol expr = factory.createElementSymbol("e2"); //$NON-NLS-1$

            ISubqueryCompareCriteria scc = factory.createSubqueryCompareCriteria(expr,
                                                                                 q1,
                                                                                 ICompareCriteria.LE,
                                                                                 ISubqueryCompareCriteria.SOME);

            helpTest(scc, "e2 <= SOME (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
        }
    }

    public void testScalarSubquery() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            IScalarSubquery obj = factory.createScalarSubquery(q1);

            helpTest(obj, "(SELECT e1 FROM m.g1)"); //$NON-NLS-1$
        }
    }

    public void testNewSubqueryObjects() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            IFrom f1 = factory.createFrom();
            f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);
            q1.setFrom(f1);

            ISelect s2 = factory.createSelect();
            s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
            s2.addSymbol(factory.createExpressionSymbol("blargh", factory.createScalarSubquery(q1))); //$NON-NLS-1$
            IFrom f2 = factory.createFrom();
            f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
            ICriteria left = factory.createSubqueryCompareCriteria(factory.createElementSymbol("e3"), q1, ICompareCriteria.GE, ISubqueryCompareCriteria.ANY); //$NON-NLS-1$
            ICriteria right = factory.createExistsCriteria(q1);
            ICriteria outer = factory.createCompoundCriteria(ICompoundCriteria.AND, createList(left, right));
            IQuery q2 = factory.createQuery();
            q2.setSelect(s2);
            q2.setFrom(f2);
            q2.setCriteria(outer);

            helpTest(q2,
                     "SELECT\n\t\te1, (SELECT e1 FROM m.g1)\n\tFROM\n\t\tm.g2\n\tWHERE\n\t\t(e3 >= ANY (SELECT e1 FROM m.g1)) AND (EXISTS (SELECT e1 FROM m.g1))"); //$NON-NLS-1$
        }
    }

    /**
     * For some reason this test was outputting SELECT 'A' AS FOO UNION SELECT 'A' AS FOO
     */
    public void testSetQueryUnionOfLiteralsCase3102() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            String expected = "SELECT\n\t\t'A' AS FOO\nUNION\nSELECT\n\t\t'B' AS FOO"; //$NON-NLS-1$

            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createAliasSymbol("FOO", factory.createExpressionSymbol("xxx", factory.createConstant("A")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);

            ISelect s2 = factory.createSelect();
            s2.addSymbol(factory.createAliasSymbol("FOO", factory.createExpressionSymbol("xxx", factory.createConstant("B")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            IQuery q2 = factory.createQuery();
            q2.setSelect(s2);

            ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);

            helpTest(sq, expected);
        }
    }

    /**
     * For some reason this test was outputting SELECT 'A' AS FOO UNION SELECT 'A' AS FOO Same as above except that
     * ExpressionSymbols' internal names (which aren't visible in the query) are different
     */
    public void testSetQueryUnionOfLiteralsCase3102a() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);

            String expected = "SELECT\n\t\t'A' AS FOO\nUNION\nSELECT\n\t\t'B' AS FOO"; //$NON-NLS-1$

            ISelect s1 = factory.createSelect();
            s1.addSymbol(factory.createAliasSymbol("FOO", factory.createExpressionSymbol("xxx", factory.createConstant("A")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            IQuery q1 = factory.createQuery();
            q1.setSelect(s1);

            ISelect s2 = factory.createSelect();
            s2.addSymbol(factory.createAliasSymbol("FOO", factory.createExpressionSymbol("yyy", factory.createConstant("B")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            IQuery q2 = factory.createQuery();
            q2.setSelect(s2);

            ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);

            helpTest(sq, expected);
        }
    }

    public void FAILINGtestNullExpressionInNamedParameter() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            // TODO fix this test
            String expected = "EXEC sp1(PARAM => sp1.PARAM)"; //$NON-NLS-1$

            IStoredProcedure sp = factory.createStoredProcedure();
            sp.setDisplayNamedParameters(true);
            sp.setProcedureName("sp1"); //$NON-NLS-1$

            ISPParameter param = factory.createSPParameter(0, ISPParameter.ParameterInfo.IN, "sp1.PARAM"); //$NON-NLS-1$
            sp.setParameter(param);

            helpTest(sp, expected);
        }
    }

    public void testQueryWithMakeDep() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IQuery query = factory.createQuery();
            ISelect select = factory.createSelect(Arrays.asList(factory.createMultipleElementSymbol()));
            IFromClause fromClause = factory.createUnaryFromClause(factory.createGroupSymbol("a")); //$NON-NLS-1$
            fromClause.setMakeDep(true);
            IFrom from = factory.createFrom(Arrays.asList(fromClause));
            query.setSelect(select);
            query.setFrom(from);
            String expectedSQL = "SELECT\n\t\t*\n\tFROM\n\t\t/*+ MAKEDEP */ a"; //$NON-NLS-1$ 
            helpTest(query, expectedSQL);
        }
    }

    public void testQueryWithJoinPredicateMakeDep() {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IQuery query = factory.createQuery();
            ISelect select = factory.createSelect(Arrays.asList(factory.createMultipleElementSymbol()));
            IFromClause fromClause = factory.createUnaryFromClause(factory.createGroupSymbol("a")); //$NON-NLS-1$
            fromClause.setMakeNotDep(true);
            IFromClause fromClause1 = factory.createUnaryFromClause(factory.createGroupSymbol("b")); //$NON-NLS-1$
            fromClause1.setMakeDep(true);
            IFrom from = factory.createFrom(Arrays.asList(factory.createJoinPredicate(fromClause,
                                                                                      fromClause1,
                                                                                      factory.getJoinType(IJoinType.Types.JOIN_CROSS))));
            query.setSelect(select);
            query.setFrom(from);
            helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\t/*+ MAKENOTDEP */ a CROSS JOIN /*+ MAKEDEP */ b"); //$NON-NLS-1$ 
        }
    }

    public void testQueryWithNestedJoinPredicateMakeDep() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IQuery query = (IQuery)parser.parseCommand("Select a From (db.g1 JOIN db.g2 ON a = b) makedep LEFT OUTER JOIN db.g3 ON a = c"); //$NON-NLS-1$
            helpTest(query,
                     "SELECT\n\t\ta\n\tFROM\n\t\t/*+ MAKEDEP */ (db.g1 INNER JOIN db.g2 ON a = b) LEFT OUTER JOIN db.g3 ON a = c"); //$NON-NLS-1$
        }
    }

    public void testCast() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IExpression ex = parser.parseExpression("cast(x as integer)"); //$NON-NLS-1$
            helpTest(ex, "cast(x AS integer)"); //$NON-NLS-1$
        }
    }

    public void testXMLPi() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IExpression ex = parser.parseExpression("xmlpi(name foo, 'bar')"); //$NON-NLS-1$
            helpTest(ex, "xmlpi(NAME foo, 'bar')"); //$NON-NLS-1$
        }
    }

    public void testXMLPi1() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IExpression ex = parser.parseExpression("xmlpi(name \"table\", 'bar')"); //$NON-NLS-1$
            helpTest(ex, "xmlpi(NAME \"table\", 'bar')"); //$NON-NLS-1$
        }
    }

    public void testTimestampAdd() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            IExpression ex = parser.parseExpression("timestampadd(SQL_TSI_DAY, x, y)"); //$NON-NLS-1$
            helpTest(ex, "timestampadd(SQL_TSI_DAY, x, y)"); //$NON-NLS-1$
        }
    }

    public void testXMLAgg() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ILanguageObject ex = parser.parseCommand("select xmlagg(x order by y)"); //$NON-NLS-1$
            helpTest(ex, "SELECT\n\t\tXMLAGG(x ORDER BY y)"); //$NON-NLS-1$
        }
    }

    public void testXMLElement() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ILanguageObject ex = parser.parseExpression("xmlelement(name y, xmlattributes('x' as foo), q)"); //$NON-NLS-1$
            helpTest(ex, "XMLELEMENT(NAME y, XMLATTRIBUTES('x' AS foo), q)"); //$NON-NLS-1$
        }
    }

    /* Cache hints have been removed from the teiid runtime client */
//    public void testCacheHint() throws Exception {
//        for (ITeiidServerVersion version : serverVersions) {
//            setDefaultServerVersion(version);
//            ILanguageObject ex = parser.parseCommand("/*+ cache(pref_mem) */ select * from db.g2"); //$NON-NLS-1$
//            helpTest(ex, "/*+ cache(pref_mem) */\nSELECT\n\t\t*\n\tFROM\n\t\tdb.g2"); //$NON-NLS-1$
//        }
//    }

    public void testConstantAliasSymbol() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ILanguageObject agg = parser.parseCommand("SELECT '123' AS ID FROM X"); //$NON-NLS-1$
            helpTest(agg, getExpectedResult("testConstantAliasSymbol", version)); //$NON-NLS-1$
        }
    }
    
    public void testTrimAliasSymbol() throws Exception {
    	// "testTrimAliasSymbol" trim(' ' FROM X) AS ID"); //$NON-NLS-1$ //$NON-NLS-2$
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ILanguageObject agg = parser.parseCommand("SELECT trim(' ' FROM X) AS ID FROM Y"); //$NON-NLS-1$
            helpTest(agg, getExpectedResult("testTrimAliasSymbol", version)); //$NON-NLS-1$
        }
    }
    
    public void testConcatWithNull() throws Exception {
        for (ITeiidServerVersion version : serverVersions) {
            setDefaultServerVersion(version);
            ILanguageObject agg = parser.parseCommand("SELECT concat('abcd', null) AS ProductName FROM	PRODUCTDATA"); //$NON-NLS-1$
            helpTest(agg, getExpectedResult("testConcatWithNull", version)); //$NON-NLS-1$
        }
    }

    // ################################## TEST SUITE ################################

    /**
     * This suite of all tests could be defined in another class but it seems easier to maintain it here.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestSQLStringVisitor"); //$NON-NLS-1$
        suite.addTestSuite(TestDisplayNodeFactory.class);
        // suite.addTest(new TestSQLStringVisitor("testSetQueryUnionOfLiteralsCase3102"));
        return suite;
    }

}
