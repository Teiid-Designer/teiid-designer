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

package org.teiid.query.resolver.v8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.AbstractTestFunctionResolving;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.v8.Test8Factory;
import org.teiid.query.unittest.RealMetadataFactory.DDLHolder;

@SuppressWarnings( {"nls", "javadoc"} )
public class Test8FunctionResolving extends AbstractTestFunctionResolving {

    private Test8Factory factory;

    protected Test8FunctionResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8FunctionResolving() {
        this(Version.TEIID_8_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test8Factory(getQueryParser());

        return factory;
    }

    @Test
    public void testResolveBadConvert() throws Exception {
        Function function = getFactory().newFunction("convert",
                                                     new Expression[] {
                                                         getFactory().newConstant(new Character('a')),
                                                         getFactory().newConstant(DataTypeManagerService.DefaultDataTypes.DATE.getId())}); //$NON-NLS-1$

        try {
            ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
            visitor.resolveLanguageObject(function, getMetadataFactory().example1Cached());
            fail("excpetion expected"); //$NON-NLS-1$
        } catch (QueryResolverException err) {
            assertEquals("TEIID30071 The conversion from char to date is not allowed.", err.getMessage()); //$NON-NLS-1$
        }
    }

    @Test
    public void testResolveAmbiguousFunction() throws Exception {
        Function function = getFactory().newFunction("LCASE",
                                                     new Expression[] {getFactory().newReference(0)}); //$NON-NLS-1$

        try {
            ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
            visitor.resolveLanguageObject(function, getMetadataFactory().example1Cached());
            fail("excpetion expected"); //$NON-NLS-1$
        } catch (QueryResolverException err) {
            assertEquals("TEIID30069 The function 'LCASE(?)' has more than one possible signature.", err.getMessage()); //$NON-NLS-1$
        }
    }

    @Test( expected = QueryResolverException.class )
    public void testStringAggWrongTypes() throws Exception {
        String sql = "string_agg(pm1.g1.e1, pm1.g1.e2)"; //$NON-NLS-1$
        getExpression(sql);
    }

    @Test( expected = QueryResolverException.class )
    public void testStringAggWrongArgs() throws Exception {
        String sql = "string_agg(pm1.g1.e1)"; //$NON-NLS-1$
        getExpression(sql);
    }

    @Test
    public void testImportedPushdown() throws Exception {
        getMetadataFactory().example1Cached();
        IQueryMetadataInterface tm = getMetadataFactory().fromDDL("x", new DDLHolder("y", "create foreign function func(x object) returns object;"), new DDLHolder("z", "create foreign function func(x object) returns object;"));

        String sql = "func('a')";

        Function func = (Function) getQueryParser().parseExpression(sql);
        try {
            ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
            visitor.resolveLanguageObject(func, tm);
            fail("should be ambiguous");
        } catch (QueryResolverException e) {

        }

        tm = getMetadataFactory().fromDDL("x", new DDLHolder("y", "create foreign function func(x object) returns object options (\"teiid_rel:system-name\" 'f');"), new DDLHolder("z", "create foreign function func(x object) returns object options (\"teiid_rel:system-name\" 'f');"));

        func = (Function) getQueryParser().parseExpression(sql);
        ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
        visitor.resolveLanguageObject(func, tm);

        tm = getMetadataFactory().fromDDL("x", new DDLHolder("y", "create foreign function func() returns object options (\"teiid_rel:system-name\" 'f');"), new DDLHolder("z", "create foreign function func() returns object options (\"teiid_rel:system-name\" 'f');"));

        func = (Function) getQueryParser().parseExpression("func()");
        visitor = new ResolverVisitor(getTeiidVersion());
        visitor.resolveLanguageObject(func, tm);
    }

    /**
     * e1 is of type string, so 1 should be converted to string
     * @throws Exception
     */
    @Test
    public void testNumericConversion() throws Exception {
        String sql = "1.0/2"; //$NON-NLS-1$
        Function f = (Function)getExpression(sql);
        assertEquals(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL.getTypeClass(), f.getType());
    }
}
