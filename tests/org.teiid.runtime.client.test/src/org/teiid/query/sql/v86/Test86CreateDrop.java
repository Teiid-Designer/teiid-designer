/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v86;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v85.Test85CreateDrop;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class Test86CreateDrop extends Test85CreateDrop {

    protected Test86CreateDrop(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test86CreateDrop() {
        this(Version.TEIID_8_6);
    }

}
