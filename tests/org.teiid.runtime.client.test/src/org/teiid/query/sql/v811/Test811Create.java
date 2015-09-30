/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v811;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v810.Test810Create;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test811Create extends Test810Create {

    protected Test811Create(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test811Create() {
        this(Version.TEIID_8_11);
    }
}
