/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v8124;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v811.Test811Cloning;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test8124Cloning extends Test811Cloning {

    protected Test8124Cloning(Version teiidVersion) {
        super(teiidVersion);
    }
 
    public Test8124Cloning() {
        this(Version.TEIID_8_12_4);
    }
}
