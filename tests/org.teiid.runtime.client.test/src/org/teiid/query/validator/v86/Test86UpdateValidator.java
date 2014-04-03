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
import org.teiid.query.validator.v8.Test8UpdateValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test86UpdateValidator extends Test8UpdateValidator {

    protected Test86UpdateValidator(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test86UpdateValidator() {
        this(Version.TEIID_8_6.get());
    }

}
