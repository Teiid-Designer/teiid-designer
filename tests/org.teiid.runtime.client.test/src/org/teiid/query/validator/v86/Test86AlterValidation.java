/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v86;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v85.Test85AlterValidation;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test86AlterValidation extends Test85AlterValidation {

    protected Test86AlterValidation(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test86AlterValidation() {
        this(Version.TEIID_8_6);
    }

}
