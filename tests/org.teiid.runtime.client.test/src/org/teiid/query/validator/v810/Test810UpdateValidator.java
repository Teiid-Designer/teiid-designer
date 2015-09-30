/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v810;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v89.Test89UpdateValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test810UpdateValidator extends Test89UpdateValidator {

    protected Test810UpdateValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test810UpdateValidator() {
        this(Version.TEIID_8_10);
    }

}
