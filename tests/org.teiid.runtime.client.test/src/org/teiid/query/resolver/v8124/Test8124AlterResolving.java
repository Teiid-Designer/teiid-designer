/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v8124;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v811.Test811AlterResolving;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test8124AlterResolving extends Test811AlterResolving {

    protected Test8124AlterResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8124AlterResolving() {
        this(Version.TEIID_8_12_4);
    }

}
