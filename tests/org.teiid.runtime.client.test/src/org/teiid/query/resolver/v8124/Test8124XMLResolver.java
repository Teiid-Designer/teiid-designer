/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v8124;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v811.Test811XMLResolver;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test8124XMLResolver extends Test811XMLResolver {

    protected Test8124XMLResolver(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8124XMLResolver() {
        this(Version.TEIID_8_12_4);
    }

}
