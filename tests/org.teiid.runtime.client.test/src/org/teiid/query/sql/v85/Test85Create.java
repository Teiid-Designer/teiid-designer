/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v85;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v8.Test8Create;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class Test85Create extends Test8Create {

    protected Test85Create(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test85Create() {
        this(Version.TEIID_8_5);
    }
}
