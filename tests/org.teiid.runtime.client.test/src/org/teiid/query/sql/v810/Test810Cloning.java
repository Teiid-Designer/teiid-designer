/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v810;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v89.Test89Cloning;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test810Cloning extends Test89Cloning {

    protected Test810Cloning(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }
 
    public Test810Cloning() {
        this(Version.TEIID_8_10.get());
    }
}
