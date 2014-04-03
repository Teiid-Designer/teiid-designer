/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v87;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v8.Test8AlterValidation;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test87AlterValidation extends Test8AlterValidation {

    protected Test87AlterValidation(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test87AlterValidation() {
        this(Version.TEIID_8_7.get());
    }

}
