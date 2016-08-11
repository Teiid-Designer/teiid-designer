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
import org.junit.Test;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.XMLSerialize;

@SuppressWarnings( {"javadoc"})
public abstract class AbstractTestFunctionResolving extends AbstractTest {

    /**
     * @param teiidVersion
     */
    public AbstractTestFunctionResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    @Test
    public void testResolvesClosestType() throws Exception {
        ElementSymbol e1 = getFactory().newElementSymbol("pm1.g1.e1"); //$NON-NLS-1$
        //dummy resolve to a byte
        e1.setType(DataTypeManagerService.DefaultDataTypes.BYTE.getTypeClass());
        e1.setMetadataID(new Object());
        Function function = getFactory().newFunction("abs", new Expression[] {e1}); //$NON-NLS-1$

        ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
        visitor.resolveLanguageObject(function, getMetadataFactory().example1Cached());

        assertEquals(DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(), function.getType());
    }

    @Test
    public void testResolveConvertReference() throws Exception {
        Function function = getFactory().newFunction(
                                         "convert", new Expression[] { //$NON-NLS-1$
                                             getFactory().newReference(0), getFactory().newConstant(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId())});

        ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
        visitor.resolveLanguageObject(function, getMetadataFactory().example1Cached());

        assertEquals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass(), function.getType());
        assertEquals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass(), function.getArgs()[0].getType());
    }

    @Test
    public void testResolveCoalesce() throws Exception {
        String sql = "coalesce('', '')"; //$NON-NLS-1$
        helpResolveFunction(sql);
    }

    @Test
    public void testResolveCoalesce1() throws Exception {
        String sql = "coalesce('', '', '')"; //$NON-NLS-1$
        helpResolveFunction(sql);
    }

    /**
     * Should resolve using varags logic
     */
    @Test
    public void testResolveCoalesce1a() throws Exception {
        String sql = "coalesce('', '', '', '')"; //$NON-NLS-1$
        helpResolveFunction(sql);
    }

    /**
     * Should resolve as 1 is implicitly convertable to string
     */
    @Test
    public void testResolveCoalesce2() throws Exception {
        String sql = "coalesce('', 1, '', '')"; //$NON-NLS-1$
        helpResolveFunction(sql);
    }

    @Test
    public void testResolveCoalesce3() throws Exception {
        String sql = "coalesce('', 1, null, '')"; //$NON-NLS-1$
        helpResolveFunction(sql);
    }

    @Test
    public void testResolveCoalesce4() throws Exception {
        String sql = "coalesce({d'2009-03-11'}, 1)"; //$NON-NLS-1$
        helpResolveFunction(sql);
    }

    private Function helpResolveFunction(String sql) throws Exception {
        Function func = (Function)getExpression(sql);
        assertEquals(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass(), func.getType());
        return func;
    }

    public Expression getExpression(String sql)
        throws Exception {
        Expression func = getQueryParser().parseExpression(sql);
        ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
        TransformationMetadata tm = getMetadataFactory().example1Cached();
        visitor.resolveLanguageObject(func, tm);
        return func;
    }

    /**
     * e1 is of type string, so 1 should be converted to string
     * @throws Exception
     */
    @Test
    public void testLookupTypeConversion() throws Exception {
        String sql = "lookup('pm1.g1', 'e2', 'e1', 1)"; //$NON-NLS-1$
        Function f = (Function)getExpression(sql);
        assertEquals(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass(), f.getArg(3).getType());
    }

    @Test
    public void testXMLSerialize() throws Exception {
        String sql = "xmlserialize(DOCUMENT '<a/>' as clob)"; //$NON-NLS-1$
        XMLSerialize xs = (XMLSerialize)getExpression(sql);
        assertEquals(DataTypeManagerService.DefaultDataTypes.CLOB.getTypeClass(), xs.getType());
    }

    @Test( expected = QueryResolverException.class )
    public void testXMLSerialize_1() throws Exception {
        String sql = "xmlserialize(DOCUMENT 1 as clob)"; //$NON-NLS-1$
        XMLSerialize xs = (XMLSerialize)getExpression(sql);
        assertEquals(DataTypeManagerService.DefaultDataTypes.CLOB.getTypeClass(), xs.getType());
    }
}
