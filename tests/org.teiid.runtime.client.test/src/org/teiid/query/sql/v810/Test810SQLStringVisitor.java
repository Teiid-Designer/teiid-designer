/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v810;

import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v89.Test89SQLStringVisitor;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class Test810SQLStringVisitor extends Test89SQLStringVisitor {

    protected Test810SQLStringVisitor(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test810SQLStringVisitor() {
        this(Version.TEIID_8_10);
    }

    @Override
    @Test
    public void testTextTable() throws Exception {
        String sql = "SELECT * from texttable(file columns y for ordinality, x string WIDTH 1 NO TRIM NO ROW DELIMITER) as x";
        helpTest(parser.parseCommand(sql),
                 "SELECT * FROM TEXTTABLE(file COLUMNS y FOR ORDINALITY, x string WIDTH 1 NO TRIM NO ROW DELIMITER) AS x");
    }
}
