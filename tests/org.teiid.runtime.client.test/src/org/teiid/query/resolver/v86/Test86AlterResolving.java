/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v86;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v85.Test85AlterResolving;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test86AlterResolving extends Test85AlterResolving {

    protected Test86AlterResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test86AlterResolving() {
        this(Version.TEIID_8_6);
    }

}
