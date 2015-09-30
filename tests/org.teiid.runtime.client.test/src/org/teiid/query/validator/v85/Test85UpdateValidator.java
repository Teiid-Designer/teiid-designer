/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v85;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v8.Test8UpdateValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test85UpdateValidator extends Test8UpdateValidator {

    protected Test85UpdateValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test85UpdateValidator() {
        this(Version.TEIID_8_5);
    }

}
