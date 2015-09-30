/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v811;

import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.validator.v810.Test810AlterValidation;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test811AlterValidation extends Test810AlterValidation {

    protected Test811AlterValidation(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test811AlterValidation() {
        this(Version.TEIID_8_11);
    }

}
