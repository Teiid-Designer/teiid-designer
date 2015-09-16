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
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.navigator.DeepPreOrderNavigator;

@SuppressWarnings( {"nls", "javadoc"} )
public abstract class AbstractTestAlterResolving extends AbstractTest {

    /**
     * @param teiidVersion
     */
    public AbstractTestAlterResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    protected Command helpParse(String sql) {
        try {
            return getQueryParser().parseCommand(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Command helpResolve(String sql, IQueryMetadataInterface queryMetadata) {
        return helpResolve(helpParse(sql), queryMetadata);
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
        Collection unresolvedSymbols = vis.getUnresolvedSymbols();
        assertTrue("Found unresolved symbols: " + unresolvedSymbols, unresolvedSymbols.isEmpty()); //$NON-NLS-1$
        return command;
    }

    protected void helpResolveException(String sql, IQueryMetadataInterface queryMetadata) {
        helpResolveException(sql, queryMetadata, null);
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

    @Test
    public void testAlterView() {
        AlterView alterView = (AlterView)helpResolve("alter view SmallA_2589 as select 2", getMetadataFactory().exampleBQTCached());
        assertNotNull(alterView.getTarget().getMetadataID());
    }

    @Test
    public void testAlterTriggerInsert() {
        AlterTrigger alterTrigger = (AlterTrigger)helpResolve("alter trigger on SmallA_2589 instead of insert as for each row begin atomic select new.intkey; end",
                                                              getMetadataFactory().exampleBQTCached());
        assertNotNull(alterTrigger.getTarget().getMetadataID());
    }

    @Test
    public void testAlterTriggerInsert_Invalid() {
        helpResolveException("alter trigger on SmallA_2589 instead of insert as for each row begin atomic select old.intkey; end",
                             getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testAlterView_Invalid() {
        helpResolveException("alter view bqt1.SmallA as select 2", getMetadataFactory().exampleBQTCached());
    }

}
