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

package org.teiid.query.resolver.v7;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.AbstractTestFunctionResolving;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.v7.Test7Factory;

@SuppressWarnings( {"nls" , "javadoc"})
public class Test7FunctionResolving extends AbstractTestFunctionResolving {

    private Test7Factory factory;

    /**
     *
     */
    public Test7FunctionResolving() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test7Factory(getQueryParser());

        return factory;
    }

    @Test
    public void testResolveBadConvert() throws Exception {
        Function function = getFactory().newFunction(
                                         "convert", new Expression[] {
                                             getFactory().newConstant(new Character('a')), 
                                             getFactory().newConstant(DataTypeManagerService.DefaultDataTypes.DATE.getId())}); //$NON-NLS-1$

        try {
            ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
            visitor.resolveLanguageObject(function, getMetadataFactory().example1Cached());
            fail("excpetion expected"); //$NON-NLS-1$
        } catch (QueryResolverException err) {
            assertTrue(err.getMessage().contains("The conversion from char to date is not allowed."));
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
            assertTrue(err.getMessage().contains("The function 'LCASE(?)' has more than one possible signature."));
        }
    }
}
