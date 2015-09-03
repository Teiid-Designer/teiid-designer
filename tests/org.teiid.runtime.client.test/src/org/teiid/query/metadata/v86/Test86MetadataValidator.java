/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata.v86;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.v85.Test85MetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test86MetadataValidator extends Test85MetadataValidator {

    protected Test86MetadataValidator(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test86MetadataValidator() {
        this(Version.TEIID_8_6.get());
    }
}
