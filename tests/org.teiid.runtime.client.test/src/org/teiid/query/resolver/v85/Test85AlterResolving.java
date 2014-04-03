/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v85;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v8.Test8AlterResolving;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test85AlterResolving extends Test8AlterResolving {

    public Test85AlterResolving() {
        super(Version.TEIID_8_5.get());
    }

}
