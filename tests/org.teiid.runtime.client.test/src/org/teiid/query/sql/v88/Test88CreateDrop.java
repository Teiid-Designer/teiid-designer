/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v88;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v87.Test87CreateDrop;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class Test88CreateDrop extends Test87CreateDrop {

    protected Test88CreateDrop(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test88CreateDrop() {
        this(Version.TEIID_8_8.get());
    }

}
