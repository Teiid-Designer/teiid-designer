/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v89;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v87.Test87UpdateValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test89UpdateValidator extends Test87UpdateValidator {

    protected Test89UpdateValidator(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test89UpdateValidator() {
        this(Version.TEIID_8_9.get());
    }

}
