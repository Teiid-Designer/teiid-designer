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
import org.teiid.query.validator.v89.Test89FunctionMetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test810FunctionMetadataValidator extends Test89FunctionMetadataValidator {

    protected Test810FunctionMetadataValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test810FunctionMetadataValidator() {
        this(Version.TEIID_8_10);
    }

}
