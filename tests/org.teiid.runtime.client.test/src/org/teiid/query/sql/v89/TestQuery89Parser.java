/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v89;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v88.TestQuery88Parser;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class TestQuery89Parser extends TestQuery88Parser {

    protected TestQuery89Parser(Version teiidVersion) {
        super(teiidVersion);
    }

    public TestQuery89Parser() {
        this(Version.TEIID_8_9);
    }
}
