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
import org.teiid.query.validator.v88.Test88FunctionMetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test89FunctionMetadataValidator extends Test88FunctionMetadataValidator {

    protected Test89FunctionMetadataValidator(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test89FunctionMetadataValidator() {
        this(Version.TEIID_8_9.get());
    }

}
