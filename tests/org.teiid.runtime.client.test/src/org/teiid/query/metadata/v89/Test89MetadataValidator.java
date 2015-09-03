/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata.v89;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.v88.Test88MetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test89MetadataValidator extends Test88MetadataValidator {

    protected Test89MetadataValidator(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test89MetadataValidator() {
        this(Version.TEIID_8_9.get());
    }
}
