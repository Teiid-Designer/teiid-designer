/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata.v811;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.v810.Test810MetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test811MetadataValidator extends Test810MetadataValidator {

    protected Test811MetadataValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test811MetadataValidator() {
        this(Version.TEIID_8_11);
    }
}
