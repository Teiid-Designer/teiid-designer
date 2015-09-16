/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v87;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v86.Test86Cloning;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test87Cloning extends Test86Cloning {

    protected Test87Cloning(Version teiidVersion) {
        super(teiidVersion);
    }
 
    public Test87Cloning() {
        this(Version.TEIID_8_7);
    }
}
