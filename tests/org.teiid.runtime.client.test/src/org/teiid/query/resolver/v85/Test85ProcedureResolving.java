/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v85;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.resolver.v8.Test8ProcedureResolving;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.symbol.Array;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Symbol;

@SuppressWarnings( {"javadoc", "nls"} )
public class Test85ProcedureResolving extends Test8ProcedureResolving {

    protected Test85ProcedureResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test85ProcedureResolving() {
        this(Version.TEIID_8_5);
    }

    @Test
    public void testVarArgs2() throws Exception {
        String ddl = "create foreign procedure proc (VARIADIC z object) returns (x string);\n";
        TransformationMetadata tm = createMetadata(ddl);

        String sql = "call proc ()"; //$NON-NLS-1$
        StoredProcedure sp = (StoredProcedure)helpResolve(sql, tm);
        assertEquals("EXEC proc()", sp.toString());
        Array expected = getFactory().newArray(DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass(),
                                           new ArrayList<Expression>(0));
        expected.setImplicit(true);
        assertEquals(expected,
                     sp.getParameter(1).getExpression());

        sql = "call proc (1, (2, 3))"; //$NON-NLS-1$
        sp = (StoredProcedure)helpResolve(sql, tm);
        assertEquals("EXEC proc(1, (2, 3))", sp.toString());
        ArrayList<Expression> expressions = new ArrayList<Expression>();
        expressions.add(getFactory().newConstant(1));
        expressions.add(getFactory().newArray(DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(),
                                              Arrays.asList((Expression)getFactory().newConstant(2), getFactory().newConstant(3))));
        Array expected2 = getFactory().newArray(DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass(), expressions);
        expected2.setImplicit(true);
        assertEquals(expected2,
                     sp.getParameter(1).getExpression());
    }

    @Test
    public void testAnonBlock() throws Exception {
        String sql = "begin select 1 as something; end"; //$NON-NLS-1$
        CreateProcedureCommand sp = (CreateProcedureCommand)helpResolve(sql, getMetadataFactory().example1Cached());
        assertEquals(1, sp.getResultSetColumns().size());
        Expression expr = sp.getResultSetColumns().get(0);
        assertTrue(expr instanceof Symbol);
        assertEquals("something", ((Symbol)expr).getName());
        assertEquals(1, sp.getProjectedSymbols().size());
        assertTrue(sp.returnsResultSet());
    }

    @Test
    public void testAnonBlockNoResult() throws Exception {
        String sql = "begin select 1 as something without return; end"; //$NON-NLS-1$
        CreateProcedureCommand sp = (CreateProcedureCommand)helpResolve(sql, getMetadataFactory().example1Cached());
        assertEquals(0, sp.getProjectedSymbols().size());
        assertFalse(sp.returnsResultSet());
    }
}
