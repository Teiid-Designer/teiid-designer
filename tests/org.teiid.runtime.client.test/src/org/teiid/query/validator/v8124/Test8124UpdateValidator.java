/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v8124;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v810.Test810UpdateValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test8124UpdateValidator extends Test810UpdateValidator {

    protected Test8124UpdateValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8124UpdateValidator() {
        this(Version.TEIID_8_12_4);
    }

}
