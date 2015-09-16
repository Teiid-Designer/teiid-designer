/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v86;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v85.Test85Resolver;
import org.teiid.query.sql.lang.Command;

@SuppressWarnings( {"javadoc"} )
public class Test86Resolver extends Test85Resolver {

    protected Test86Resolver(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test86Resolver() {
        this(Version.TEIID_8_6);
    }

    @Override
    @Test
    public void testImplicitTempInsertWithNoColumns() {
        StringBuffer proc = new StringBuffer("CREATE VIRTUAL PROCEDURE") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  create local temporary table #matt (x integer);") //$NON-NLS-1$
        .append("\n  insert into #matt values (1);") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        Command cmd = helpResolve(proc.toString());

        String sExpected = "BEGIN\nCREATE LOCAL TEMPORARY TABLE #matt (x integer);\nINSERT INTO #matt (x) VALUES (1);\nEND\n\tCREATE LOCAL TEMPORARY TABLE #matt (x integer)\n\tINSERT INTO #matt (x) VALUES (1)\n"; //$NON-NLS-1$
        String sActual = cmd.printCommandTree();
        assertEquals(sExpected, sActual);
    }
}
