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
import org.teiid.query.resolver.v89.Test89ProcedureResolving;

@SuppressWarnings( {"javadoc"} )
public class Test810ProcedureResolving extends Test89ProcedureResolving {

    protected Test810ProcedureResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test810ProcedureResolving() {
        this(Version.TEIID_8_10);
    }
}
