/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v87;

import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v86.Test86Validator;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class Test87Validator extends Test86Validator {

    protected Test87Validator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test87Validator() {
        this(Version.TEIID_8_7);
    }

    @Test
    public void testTextTableFixedSelector() {
        helpValidate("SELECT * from texttable(null SELECTOR 'a' columns x string width 1) as x",
                     new String[] {},
                     getMetadataFactory().exampleBQTCached());
    }
}
