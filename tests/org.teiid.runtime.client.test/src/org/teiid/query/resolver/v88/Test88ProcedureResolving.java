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
import org.teiid.query.resolver.v87.Test87ProcedureResolving;

@SuppressWarnings( {"javadoc"} )
public class Test88ProcedureResolving extends Test87ProcedureResolving {

    protected Test88ProcedureResolving(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test88ProcedureResolving() {
        this(Version.TEIID_8_8.get());
    }
}
