/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata.v8;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.AbstractTestMetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test8MetadataValidator extends AbstractTestMetadataValidator {

    protected Test8MetadataValidator(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }
   
    public Test8MetadataValidator() {
        super(Version.TEIID_8_0.get());
    }

}
