/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v85;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v8.Test8AccessPattern;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test85AccessPattern extends Test8AccessPattern {

    protected Test85AccessPattern(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test85AccessPattern() {
        this(Version.TEIID_8_5);
    }

}
