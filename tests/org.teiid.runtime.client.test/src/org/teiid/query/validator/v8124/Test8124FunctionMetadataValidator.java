/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v8124;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v810.Test810FunctionMetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test8124FunctionMetadataValidator extends Test810FunctionMetadataValidator {

    protected Test8124FunctionMetadataValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8124FunctionMetadataValidator() {
        this(Version.TEIID_8_12_4);
    }

}
