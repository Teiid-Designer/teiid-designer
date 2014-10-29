/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v88;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v87.Test87Validator;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class Test88Validator extends Test87Validator {

    protected Test88Validator(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test88Validator() {
        this(Version.TEIID_8_8.get());
    }
}
