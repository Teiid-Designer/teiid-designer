/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v811;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v810.TestQuery810Parser;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class TestQuery811Parser extends TestQuery810Parser {

    protected TestQuery811Parser(Version teiidVersion) {
        super(teiidVersion);
    }

    public TestQuery811Parser() {
        this(Version.TEIID_8_11);
    }
}
