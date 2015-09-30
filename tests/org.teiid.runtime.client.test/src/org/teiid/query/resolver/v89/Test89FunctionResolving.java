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
import org.teiid.query.resolver.v88.Test88FunctionResolving;

@SuppressWarnings( {"javadoc"} )
public class Test89FunctionResolving extends Test88FunctionResolving {

    protected Test89FunctionResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test89FunctionResolving() {
        this(Version.TEIID_8_9);
    }
}
