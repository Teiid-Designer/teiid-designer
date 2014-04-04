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

package org.teiid772.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import org.junit.Test;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.TeiidComponentException;
import org.teiid.core.TeiidException;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.navigator.DeepPreOrderNavigator;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid772.unittest.RealMetadataFactory;

@SuppressWarnings( "nls" )
public class TestAlterResolving {

    private Command helpParse(String sql) {
        try {
            return QueryParser.getQueryParser().parseCommand(sql);
        } catch (TeiidException e) {
            throw new RuntimeException(e);
        }
    }

    private Command helpResolve(String sql, QueryMetadataInterface queryMetadata) {
        return helpResolve(helpParse(sql), queryMetadata);
    }

    private Command helpResolve(Command command, QueryMetadataInterface queryMetadataInterface) {
        // resolve
        try {
            QueryResolver.resolveCommand(command, queryMetadataInterface);
        } catch (TeiidException e) {
            throw new RuntimeException(e);
        }

        CheckSymbolsAreResolvedVisitor vis = new CheckSymbolsAreResolvedVisitor();
        DeepPreOrderNavigator.doVisit(command, vis);
        Collection unresolvedSymbols = vis.getUnresolvedSymbols();
        assertTrue("Found unresolved symbols: " + unresolvedSymbols, unresolvedSymbols.isEmpty()); //$NON-NLS-1$
        return command;
    }

    private void helpResolveException(String sql, QueryMetadataInterface queryMetadata) {
        helpResolveException(sql, queryMetadata, null);
    }

    private void helpResolveException(String sql, QueryMetadataInterface queryMetadata, String expectedExceptionMessage) {

        // parse
        Command command = helpParse(sql);

        // resolve
        try {
            QueryResolver.resolveCommand(command, queryMetadata);
            fail("Expected exception for resolving " + sql); //$NON-NLS-1$
        } catch (QueryResolverException e) {
            if (expectedExceptionMessage != null) {
                assertEquals(expectedExceptionMessage, e.getMessage());
            }
        } catch (TeiidComponentException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAlterView() {
        AlterView alterView = (AlterView)helpResolve("alter view SmallA_2589 as select 2", RealMetadataFactory.exampleBQTCached());
        assertNotNull(alterView.getTarget().getMetadataID());
    }

    @Test
    public void testAlterProcedure() {
        AlterProcedure alterProc = (AlterProcedure)helpResolve("alter procedure MMSP5 as begin select param1; end",
                                                               RealMetadataFactory.exampleBQTCached());
        assertNotNull(alterProc.getTarget().getMetadataID());
        Query q = (Query)alterProc.getDefinition().getResultsCommand();
        assertTrue(((ElementSymbol)q.getSelect().getSymbol(0)).isExternalReference());
    }

    @Test
    public void testAlterTriggerInsert() {
        AlterTrigger alterTrigger = (AlterTrigger)helpResolve("alter trigger on SmallA_2589 instead of insert as for each row begin atomic select new.intkey; end",
                                                              RealMetadataFactory.exampleBQTCached());
        assertNotNull(alterTrigger.getTarget().getMetadataID());
    }

    @Test
    public void testAlterTriggerInsert_Invalid() {
        helpResolveException("alter trigger on SmallA_2589 instead of insert as for each row begin atomic select old.intkey; end",
                             RealMetadataFactory.exampleBQTCached());
    }

    @Test
    public void testAlterView_Invalid() {
        helpResolveException("alter view bqt1.SmallA as select 2", RealMetadataFactory.exampleBQTCached());
    }

}
