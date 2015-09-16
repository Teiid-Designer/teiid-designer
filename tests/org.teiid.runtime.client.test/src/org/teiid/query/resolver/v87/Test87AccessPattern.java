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
import org.teiid.query.resolver.v86.Test86AccessPattern;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test87AccessPattern extends Test86AccessPattern {

    protected Test87AccessPattern(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test87AccessPattern() {
        this(Version.TEIID_8_7);
    }

}
