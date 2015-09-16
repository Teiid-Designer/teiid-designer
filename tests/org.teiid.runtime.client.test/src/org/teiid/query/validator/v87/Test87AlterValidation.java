/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v87;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v86.Test86AlterValidation;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test87AlterValidation extends Test86AlterValidation {

    protected Test87AlterValidation(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test87AlterValidation() {
        this(Version.TEIID_8_7);
    }

}
