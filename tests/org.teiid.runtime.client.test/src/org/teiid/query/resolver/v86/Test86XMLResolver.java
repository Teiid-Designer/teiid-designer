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
import org.teiid.query.resolver.v85.Test85XMLResolver;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test86XMLResolver extends Test85XMLResolver {

    protected Test86XMLResolver(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test86XMLResolver() {
        this(Version.TEIID_8_6);
    }

}
