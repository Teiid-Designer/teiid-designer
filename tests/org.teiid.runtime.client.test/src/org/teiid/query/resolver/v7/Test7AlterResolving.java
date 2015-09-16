/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v7;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.AbstractTestAlterResolving;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.v7.Test7Factory;

/**
 *
 */
@SuppressWarnings( {"nls" , "javadoc"})
public class Test7AlterResolving extends AbstractTestAlterResolving {

    private Test7Factory factory;

    /**
     *
     */
    public Test7AlterResolving() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test7Factory(getQueryParser());

        return factory;
    }

    @Test
    public void testAlterProcedure() {
        AlterProcedure alterProc = (AlterProcedure)helpResolve("alter procedure MMSP5 as begin select param1; end",
                                                               getMetadataFactory().exampleBQTCached());
        assertNotNull(alterProc.getTarget().getMetadataID());
        assertTrue(alterProc.getDefinition() instanceof CreateUpdateProcedureCommand);

        CreateUpdateProcedureCommand command = (CreateUpdateProcedureCommand) alterProc.getDefinition();
        Query q = (Query) command.getResultsCommand();
        assertTrue(((ElementSymbol)q.getSelect().getSymbol(0)).isExternalReference());
    }
}
