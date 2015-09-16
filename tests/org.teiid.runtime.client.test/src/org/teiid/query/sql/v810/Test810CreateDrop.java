/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v810;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.lang.Create.CommitAction;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.v89.Test89CreateDrop;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class Test810CreateDrop extends Test89CreateDrop {

    protected Test810CreateDrop(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test810CreateDrop() {
        this(Version.TEIID_8_10);
    }

    @Override
    @Test
    public void testCreateTempTable1() {
        Create create = getFactory().newCreate();
        create.setTable(getFactory().newGroupSymbol("tempTable")); //$NON-NLS-1$
        List<ElementSymbol> columns = new ArrayList<ElementSymbol>();
        ElementSymbol column = getFactory().newElementSymbol("c1");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
        columns.add(column);
        column = getFactory().newElementSymbol("c2");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BYTE.getTypeClass());
        columns.add(column);
        create.setElementSymbolsAsColumns(columns);
        create.setCommitAction(CommitAction.PRESERVE_ROWS);
        helpTest("Create local TEMPORARY table tempTable (c1 boolean, c2 byte) on commit preserve rows", "CREATE LOCAL TEMPORARY TABLE tempTable (c1 boolean, c2 byte) ON COMMIT PRESERVE ROWS", create); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
