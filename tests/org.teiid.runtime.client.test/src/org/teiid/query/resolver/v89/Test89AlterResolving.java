/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v89;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v88.Test88AlterResolving;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test89AlterResolving extends Test88AlterResolving {

    protected Test89AlterResolving(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test89AlterResolving() {
        this(Version.TEIID_8_9.get());
    }

}
