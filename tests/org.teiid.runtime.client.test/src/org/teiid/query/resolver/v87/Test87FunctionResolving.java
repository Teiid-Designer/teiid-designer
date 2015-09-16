/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v87;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.resolver.v86.Test86FunctionResolving;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.unittest.RealMetadataFactory.DDLHolder;

@SuppressWarnings( {"nls", "javadoc"} )
public class Test87FunctionResolving extends Test86FunctionResolving {

    protected Test87FunctionResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test87FunctionResolving() {
        this(Version.TEIID_8_7);
    }

    /*
     * UDF function for testVarArgsFunction
     */
    public static String vararg(Object... vals) {
        return String.valueOf(vals.length);
    }

    @Test
    public void testAmbiguousUDF() throws Exception {
        TransformationMetadata tm = getMetadataFactory().fromDDL("x",
                                                                new DDLHolder("y", "create foreign function f () returns string"),
                                                                new DDLHolder("z", "create foreign function f () returns string"));

        String sql = "f()";
        Function func = (Function)getQueryParser().parseExpression(sql);

        ResolverVisitor resolver = new ResolverVisitor(getTeiidVersion());
        try {
            resolver.resolveLanguageObject(func, tm);
            fail();
        } catch (QueryResolverException e) {

        }

        sql = "z.f()";
        func = (Function)getQueryParser().parseExpression(sql);
        resolver = new ResolverVisitor(getTeiidVersion());
        resolver.resolveLanguageObject(func, tm);
    }

    @Test
    public void testUDFResolveOrder() throws Exception {

        IQueryMetadataInterface tm = getMetadataFactory().fromDDL("create foreign function func(x object) returns object; "
                                                                + " create foreign function func(x string) returns string;"
                                                                + " create foreign function func1(x object) returns double;"
                                                                + " create foreign function func1(x string[]) returns bigdecimal;",
                                                                "x",
                                                                "y");

        String sql = "func('a')";

        Function func = (Function) getQueryParser().parseExpression(sql);
        ResolverVisitor resolver = new ResolverVisitor(getTeiidVersion());
        resolver.resolveLanguageObject(func, tm);
        assertEquals(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass(), func.getArgs()[0].getType());
        assertEquals(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass(), func.getType());

        sql = "func1(('1',))";

        func = (Function) getQueryParser().parseExpression(sql);
        resolver = new ResolverVisitor(getTeiidVersion());
        resolver.resolveLanguageObject(func, tm);
        System.out.println(func.getType());
    }
}
