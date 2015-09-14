/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v810;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v89.Test89XMLResolver;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test810XMLResolver extends Test89XMLResolver {

    protected Test810XMLResolver(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test810XMLResolver() {
        this(Version.TEIID_8_10.get());
    }

}
