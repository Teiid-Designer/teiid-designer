/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata.v85;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.v8.Test8MetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test85MetadataValidator extends Test8MetadataValidator {

    protected Test85MetadataValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test85MetadataValidator() {
        this(Version.TEIID_8_5);
    }
}
