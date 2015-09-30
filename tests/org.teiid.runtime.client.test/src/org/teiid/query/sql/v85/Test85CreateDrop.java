/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v85;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v8.Test8CreateDrop;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class Test85CreateDrop extends Test8CreateDrop {

    protected Test85CreateDrop(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test85CreateDrop() {
        this(Version.TEIID_8_5);
    }

    @Override
    @Test
    public void testCreateTempTable3() {
        String sql = "Create TEMPORARY table tempTable (c1 boolean, c2 byte)";
        try {
            assertEquals("CREATE LOCAL TEMPORARY TABLE tempTable (c1 boolean, c2 byte)",
                     parser.parseCommand(sql).toString()); //$NON-NLS-1$
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }
}
