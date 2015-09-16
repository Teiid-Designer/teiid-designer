/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v89;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v88.Test88CreateDrop;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class Test89CreateDrop extends Test88CreateDrop {

    protected Test89CreateDrop(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test89CreateDrop() {
        this(Version.TEIID_8_9);
    }

}
