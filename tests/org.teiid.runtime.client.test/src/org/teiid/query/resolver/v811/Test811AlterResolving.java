/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v811;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v810.Test810AlterResolving;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test811AlterResolving extends Test810AlterResolving {

    protected Test811AlterResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test811AlterResolving() {
        this(Version.TEIID_8_11);
    }

}
