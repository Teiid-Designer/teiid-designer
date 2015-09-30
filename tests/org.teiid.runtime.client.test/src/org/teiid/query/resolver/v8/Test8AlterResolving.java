/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v8;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.AbstractTestAlterResolving;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.v8.Test8Factory;

/**
 *
 */
@SuppressWarnings( {"nls" , "javadoc"})
public class Test8AlterResolving extends AbstractTestAlterResolving {

    private Test8Factory factory;

    protected Test8AlterResolving(Version teiidVersion) {
        super(teiidVersion);
    }
   
    public Test8AlterResolving() {
        super(Version.TEIID_8_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test8Factory(getQueryParser());

        return factory;
    }

    @Test
    public void testAlterProcedure() {
        AlterProcedure alterProc = (AlterProcedure)helpResolve("alter procedure MMSP5 as begin select param1; end",
                                                               getMetadataFactory().exampleBQTCached());
        assertNotNull(alterProc.getTarget().getMetadataID());
        assertTrue(alterProc.getDefinition() instanceof CreateProcedureCommand);

        CreateProcedureCommand command = (CreateProcedureCommand) alterProc.getDefinition();
        Query q = (Query)((CommandStatement) command.getBlock().getStatements().get(0)).getCommand();
        assertTrue(((ElementSymbol)q.getSelect().getSymbol(0)).isExternalReference());
    }
}
