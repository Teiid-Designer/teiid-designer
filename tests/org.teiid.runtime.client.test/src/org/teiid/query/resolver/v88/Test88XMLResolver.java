/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v88;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v87.Test87XMLResolver;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test88XMLResolver extends Test87XMLResolver {

    protected Test88XMLResolver(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test88XMLResolver() {
        this(Version.TEIID_8_8);
    }

}
