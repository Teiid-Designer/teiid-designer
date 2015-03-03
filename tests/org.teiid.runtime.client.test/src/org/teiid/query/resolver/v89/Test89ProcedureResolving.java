/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v89;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.v88.Test88ProcedureResolving;

@SuppressWarnings( {"javadoc"} )
public class Test89ProcedureResolving extends Test88ProcedureResolving {

    protected Test89ProcedureResolving(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test89ProcedureResolving() {
        this(Version.TEIID_8_9.get());
    }
}
