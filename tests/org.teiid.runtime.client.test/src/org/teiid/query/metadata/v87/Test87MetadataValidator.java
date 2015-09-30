/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata.v87;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.v86.Test86MetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test87MetadataValidator extends Test86MetadataValidator {

    protected Test87MetadataValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test87MetadataValidator() {
        this(Version.TEIID_8_7);
    }
}
