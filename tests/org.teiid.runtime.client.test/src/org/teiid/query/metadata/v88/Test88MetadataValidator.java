/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata.v88;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.v87.Test87MetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test88MetadataValidator extends Test87MetadataValidator {

    protected Test88MetadataValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test88MetadataValidator() {
        this(Version.TEIID_8_8);
    }
}
