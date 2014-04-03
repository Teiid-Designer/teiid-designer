/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v85;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v8.Test8AccessPattern;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test85AccessPattern extends Test8AccessPattern {

    public Test85AccessPattern() {
        super(Version.TEIID_8_5.get());
    }

}
