/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v811;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v810.Test810ProcedureResolving;

@SuppressWarnings( {"javadoc"} )
public class Test811ProcedureResolving extends Test810ProcedureResolving {

    protected Test811ProcedureResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test811ProcedureResolving() {
        this(Version.TEIID_8_11);
    }
}
