/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v87;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v86.Test86AlterResolving;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test87AlterResolving extends Test86AlterResolving {

    protected Test87AlterResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test87AlterResolving() {
        this(Version.TEIID_8_7);
    }

}
