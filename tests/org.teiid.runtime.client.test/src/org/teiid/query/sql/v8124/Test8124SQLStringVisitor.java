/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v8124;

import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v811.Test811SQLStringVisitor;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class Test8124SQLStringVisitor extends Test811SQLStringVisitor {

    protected Test8124SQLStringVisitor(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8124SQLStringVisitor() {
        this(Version.TEIID_8_12_4);
    }

    @Test
    public void testEscaping() throws Exception {
        
        String sql = "select 'a\\u0000\u0001b''c''d\u0002e\u0003f''' from TEXTTABLE(x COLUMNS y string ESCAPE '\u0000' HEADER) AS A";

        helpTest(parser.parseCommand(sql),
                 "SELECT 'a\\u0000\\u0001b''c''d\\u0002e\\u0003f''' FROM TEXTTABLE(x COLUMNS y string ESCAPE '\\u0000' HEADER) AS A"); //$NON-NLS-1$
    }
}
